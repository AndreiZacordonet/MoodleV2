import json
import mimetypes
from typing import List, Optional, Set

from bson.json_util import dumps

from fastapi import APIRouter, HTTPException, Body, Query, UploadFile, Depends, Request
from fastapi.responses import FileResponse

from pymongo.errors import DuplicateKeyError

from database_config import course_data_collection
from exceptions import *
from models import CourseCreateRequest, Material, Evaluation, Course
from service import check_formula, enhanced_body_build, build_links

from pathlib import Path
import os

from web_filter import role_based_filter

course_router = APIRouter()


@course_router.get("/",
                   summary="Retrieve all courses information.",
                   description="Returns a list of all available courses with their details",
                   responses={
                        201: {"description": "Course created successfully"},
                        401: {"description": "Unauthorized access"},
                        403: {"description": "Forbidden"},
                   })
async def get_all(request: Request, user: dict = Depends(role_based_filter)):

    documents: [Course] = course_data_collection.find({}, {"_id": 0})

    result = json.loads(dumps(documents))

    return enhanced_body_build(
        status=200,
        message="Courses retrieved successfully",
        request=request,
        additional_links={
            "self": build_links("/", "self", "Retrieve all courses", "GET"),
            "create_course": build_links("/", "create_course", "Create a new course", "PUT"),
        },
        returned_value=result,
    )


@course_router.get("/{code}")
async def get_by_code(code: str, request: Request, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document: Course = course_data_collection.find_one({"code": code}, {"_id": 0})
    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' was not found")

    # result = json.loads(dumps(document))

    return enhanced_body_build(
        status=200,
        message="Course retrieved successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}", "self", "Retrieve course details", "GET"),
            "parent": build_links("/", "parent", "Retrieve all courses", "GET"),
            "add_course_materials": build_links(f"/{code}/course", "course_materials", "Add course materials",
                                                "POST"),
            "add_lab_materials": build_links(f"/{code}/lab", "lab_materials", "Add lab materials", "POST"),
        },
        returned_value=document,
    )


@course_router.put("/")
async def create(course: CourseCreateRequest, request: Request, user: dict = Depends(role_based_filter)):
    """
    Create a new course.
    - `code`: Unique course code.
    - `evaluation`: List of evaluation items, each with `type` and `weight`.
    - `materials`: Optional materials for course and lab files.
    """

    if sum(item.weight for item in course.evaluation) != 100:
        raise FormulaLogicException("Sum of evaluation weights is incorrect.")

    try:
        result = course_data_collection.insert_one(course.serialize_course())

        return enhanced_body_build(
            status=201,
            message="Course created successfully",
            request=request,
            additional_links={
                "self": build_links(f"/{course.code}", "self", "View created course", "GET"),
                "parent": build_links("/", "all_courses", "Retrieve all courses", "GET"),
                "add_course_materials": build_links(f"/{course.code}/course", "course_materials", "Add course materials", "POST"),
                "add_lab_materials": build_links(f"/{course.code}/lab", "lab_materials", "Add lab materials", "POST"),
            },
            response_code="created",
            returned_value=course.serialize_course(),
        )

    except DuplicateKeyError:
        raise ResourceAlreadyExistsException(f"Course with code '{course.code}' already exists.")


@course_router.post("/{code}/course")
async def add_course_materials(code: str, materials: List[Material], request: Request, user: dict = Depends(role_based_filter)):
    # TODO: check course numbers min 1 max 20 + add new exception for this

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise CourseNotFoundException("Course with code '{code}' not found.")

    existing_materials = document.get("course", [])

    new_materials = [material.model_dump() for material in materials]

    combined_materials = {tuple(material.items()): material for material in existing_materials + new_materials}.values()

    result = course_data_collection.update_one({"code": code}, {"$set": {"course": list(combined_materials)}})

    return enhanced_body_build(
        status=200,
        message="Materials updated successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}", "self", "Updated course", "GET"),
            "parent": build_links("/", "all_courses", "Retrieve all courses", "GET"),
            "add_lab_materials": build_links(f"/{code}/lab", "lab_materials", "Add lab materials", "POST"),
        },
        returned_value={"modified_count": result.modified_count},
    )


@course_router.post("/{code}/{course_number}/upload-course")
async def upload_course_file(code: str, course_number: int, course_file: UploadFile, request: Request, user: dict = Depends(role_based_filter)):
    # todo: check for file dimension (max 25 mb) also check extention

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    if course_number > 20:
        raise InvalidCourseNumberException(f"Course number '{course_number}' out of bounds")

    # file path naming: path-to-course/{code}/courses/course_file

    document = course_data_collection.find_one({"code": code}, {"_id": 0, "course": 1})

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' was not found")

    # this operation changes the original course name with the name of the uploaded file
    # TODO: update course_name in the database
    course_name, extention = os.path.splitext(course_file.filename)     # dont include file extention

    courses = document.get("course", [])

    for course in courses:
        if course.get("number") == course_number:
            course["file"] = course_name
            break

    result = course_data_collection.update_one({"code": code}, {"$set": {"course": courses}})

    # if result.modified_count == 0:
    #     raise Exception("something is wrong, I can feel it")

    # TODO: delete the older file if exists
    file_path = Path("files") / code / "courses" / str(course_number) / (course_name + extention)
    file_path.parent.mkdir(parents=True, exist_ok=True)

    with open(file_path, "wb") as f:
        content = await course_file.read()
        f.write(content)

    return enhanced_body_build(
        status=200,
        message="Course file uploaded successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}", "self", "Updated course", "GET"),
            "parent": build_links("/", "all_courses", "Retrieve all courses", "GET"),
            "add_course_materials": build_links(f"/{code}/course", "course_materials", "Add course materials", "POST"),
            "add_lab_materials": build_links(f"/{code}/lab", "lab_materials", "Add lab materials", "POST"),
        },
        returned_value={"modified_count": result.modified_count},
    )


@course_router.post("/{code}/lab")
async def add_lab_materials(code: str, materials: List[Material], request: Request, user: dict = Depends(role_based_filter)):
    # TODO: check course numbers min 1 max 20

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    existing_materials = document.get("lab", [])

    new_materials = [material.model_dump() for material in materials]

    combined_materials = {tuple(material.items()): material for material in existing_materials + new_materials}.values()

    result = course_data_collection.update_one({"code": code}, {"$set": {"lab": list(combined_materials)}})

    return enhanced_body_build(
        status=200,
        message="Lab uploaded successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}/lab", "self", "Updated lab in all labs", "GET"),
            "parent": build_links("/", "all_courses", "Retrieve all courses", "GET"),
            "add_course_materials": build_links(f"/{code}/course", "course_materials", "Add course materials", "POST"),
        },
        returned_value={"modified_count": result.modified_count},
    )

# TODO: upload lab file route


@course_router.delete("/{code}/{course_number}/course")
async def remove_course_material(code: str, course_number: int, request: Request, user: dict = Depends(role_based_filter)):
    # todo: check course number

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    existing_materials = document.get("course")

    if existing_materials is None:
        raise MaterialsNotFoundException(f"The course '{code}' does not have any materials.")

    material_to_remove = next((material for material in existing_materials if material["number"] == course_number),
                              None)

    if not material_to_remove:
        raise MaterialsNotFoundException(f"Course number {course_number} does not have materials.")

    existing_materials.remove(material_to_remove)
    result = course_data_collection.update_one({"code": code}, {"$set": {"course": list(existing_materials)}})

    base_path = Path("files") / code / "courses" / str(course_number)
    if base_path.exists() and base_path.is_dir():
        import shutil
        shutil.rmtree(base_path)

    return enhanced_body_build(
        status=200,
        message="Course materials deleted successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}", "self", "Updated course", "GET"),
            "parent": build_links("/", "all_courses", "Retrieve all courses", "GET"),
            "add_course_materials": build_links(f"/{code}/course", "course_materials", "Add course materials",
                                                "POST"),
        },
        returned_value={"modified_count": result.modified_count},
    )


@course_router.delete("/{code}/{lab_number}/lab")
async def remove_lab_material(code: str, lab_number: int, request: Request, user: dict = Depends(role_based_filter)):
    # todo: check lab number max 1 max 20

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    existing_materials = document.get("lab")

    if existing_materials is None:
        raise MaterialsNotFoundException(f"The course '{code}' does not have any lab materials.")

    material_to_remove = next((material for material in existing_materials if material["number"] == lab_number),
                              None)

    if not material_to_remove:
        raise MaterialsNotFoundException(f"Lab number {lab_number} does not have materials.")

    existing_materials.remove(material_to_remove)
    result = course_data_collection.update_one({"code": code}, {"$set": {"lab": list(existing_materials)}})

    base_path = Path("files") / code / "labs" / str(lab_number)
    if base_path.exists() and base_path.is_dir():
        import shutil
        shutil.rmtree(base_path)

    return enhanced_body_build(
        status=200,
        message="Lab materials deleted successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}", "self", "Updated course", "GET"),
            "parent": build_links("/", "all_courses", "Retrieve all courses", "GET"),
            "add_course_materials": build_links(f"/{code}/course", "course_materials", "Add course materials",
                                                "POST"),
            "add_lab_materials": build_links(f"/{code}/lab", "lab_materials", "Add lab materials", "POST"),
        },
        returned_value={"modified_count": result.modified_count},
    )


@course_router.delete("/{code}")
async def remove_by_code(code: str, request: Request, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    result = course_data_collection.delete_one({"code": code})

    if result.deleted_count == 0:
        raise CourseNotFoundException(f"Course with code '{code}' not found")

    base_path = Path("files") / code
    if base_path.exists() and base_path.is_dir():
        import shutil
        shutil.rmtree(base_path)

    return enhanced_body_build(
        status=200,
        message="Course deleted successfully",
        request=request,
        additional_links={
            "self": build_links("/", "all_courses", "Retrieve all courses", "GET"),
            "parent": build_links("/", "all_courses", "Retrieve all courses", "GET"),
            "create_course": build_links("/", "create_course", "Create a new course", "PUT"),
        },
        returned_value={"modified_count": result.deleted_count},
    )


@course_router.get("/{code}/evaluation")
async def get_evaluation_formula(code: str, request: Request, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one(
        {"code": code},
        {"_id": 0, "evaluation": 1}
    )

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    return enhanced_body_build(
        status=200,
        message="Evaluation retrieved successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}", "self", "Course", "GET"),
            "parent": build_links("/", "all_courses", "Retrieve all courses", "GET"),
            "create_course": build_links("/", "create_course", "Create a new course", "PUT"),
            "add_course_materials": build_links(f"/{code}/course", "course_materials", "Add course materials",
                                                "POST"),
            "add_lab_materials": build_links(f"/{code}/lab", "lab_materials", "Add lab materials", "POST"),
        },
        returned_value=document,
    )


@course_router.get("/{code}/lab")
async def get_labs(code: str, request: Request, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one(
        {"code": code},
        {"_id": 0, "lab": 1}
    )

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    return enhanced_body_build(
        status=200,
        message="Labs retrieved successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}/lab", "self", "Labs", "GET"),
            "parent": build_links(f"/{code}", "course", "Retrieve course", "GET"),
            "add_course_materials": build_links(f"/{code}/course", "course_materials", "Add course materials",
                                                "POST"),
            "add_lab_materials": build_links(f"/{code}/lab", "lab_materials", "Add lab materials", "POST"),
        },
        returned_value=document,
    )


@course_router.get("/{code}/course")
async def get_courses(code: str, request: Request, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one(
        {"code": code},
        {"_id": 0, "course": 1}
    )

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    return enhanced_body_build(
        status=200,
        message="Courses retrieved successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}/course", "self", "Courses", "GET"),
            "parent": build_links(f"/{code}", "course", "Retrieve course", "GET"),
            "add_course_materials": build_links(f"/{code}/course", "course_materials", "Add course materials",
                                                "POST"),
            "add_lab_materials": build_links(f"/{code}/lab", "lab_materials", "Add lab materials", "POST"),
        },
        returned_value=document,
    )


@course_router.post("/{code}/evaluation")
async def update_evaluation(code: str, evaluation: List[Evaluation], request: Request, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})
    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    if not check_formula(evaluation):
        raise FormulaLogicException("Formula doesn't add up to 100.")

    result = course_data_collection.update_one({"code": code},
                                               {"$set": {"evaluation": [eva.model_dump() for eva in evaluation]}})

    return enhanced_body_build(
        status=200,
        message="Evaluation updated successfully",
        request=request,
        additional_links={
            "self": build_links(f"/{code}/evaluation", "self", "Evaluation method", "GET"),
            "parent": build_links(f"/{code}", "course", "Retrieve course", "GET"),
            "create_course": build_links("/", "create_course", "Create a new course", "PUT"),
            "add_course_materials": build_links(f"/{code}/course", "course_materials", "Add course materials",
                                                "POST"),
            "add_lab_materials": build_links(f"/{code}/lab", "lab_materials", "Add lab materials", "POST"),
        },
        returned_value=result.modified_count,
    )


@course_router.get("/{code}/courses/{course_number}")
async def get_course_file(code: str, course_number: str, request: Request, user: dict = Depends(role_based_filter)):
    # todo: check course_number min 1 max 20

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})
    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    path = Path("files") / code / "courses" / course_number

    if not path.exists() or not path.is_dir():
        raise FileNotFoundError(f"Directory {path} does not exist.")

    file = list(path.iterdir())[0]

    if not file:
        raise FileNotFoundError(f"No files found in directory {path}.")

    mime_type, _ = mimetypes.guess_type(file.name)
    if mime_type is None:
        mime_type = "application/octet-stream"

    return FileResponse(file, media_type=mime_type, filename=file.name)

# TODO: get lab file

