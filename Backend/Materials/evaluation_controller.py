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

MAX_FILE_SIZE_MB = 25
ALLOWED_EXTENSIONS = {".pdf", ".docx", ".txt", ".xlsx", ".pptx"}

course_router = APIRouter()


@course_router.get("/",
                   summary="Retrieve all courses information.",
                   description="Returns a list of all available courses with their details",
                   response_model=[Course],
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


@course_router.get("/{code}",
                   summary="Retrieve a specific course by its code",
                   description="Fetch the details of a course using its unique code.",
                   response_model=Course,
                   responses={
                       200: {"description": "Course details retrieved successfully"},
                       401: {"description": "Unauthorized access"},
                       403: {"description": "Forbidden"},
                       404: {"description": "Course not found"},
                       416: {"description": "Invalid course code"},
                   })
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


@course_router.put("/",
                   summary="Create a new course",
                   description="Creates a course with a unique code, evaluation method, and optional materials.",
                   responses={
                       201: {
                           "description": "Course created successfully",
                       },
                       400: {"description": "Invalid evaluation formula (does not sum to 100)"},
                       409: {"description": "Course with the given code already exists"},
                       401: {"description": "Unauthorized access"},
                       403: {"description": "Forbidden"},
                   }, )
async def create(course: CourseCreateRequest, request: Request, user: dict = Depends(role_based_filter)):
    """
    Create a new course.
    - `code`: Unique course code.
    - `evaluation`: List of evaluation items, each with `type` and `weight`.
    - `materials`: Optional materials for course and lab files.
    """

    if not course.code or len(course.code) > 20 or course.code.isspace():
        raise InvalidCourseCodeException(f"Code '{course.code}' is invalid or out of bounds")

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
                "add_course_materials": build_links(f"/{course.code}/course", "course_materials",
                                                    "Add course materials", "POST"),
                "add_lab_materials": build_links(f"/{course.code}/lab", "lab_materials", "Add lab materials", "POST"),
            },
            response_code="created",
            returned_value=course.serialize_course(),
        )

    except DuplicateKeyError:
        raise ResourceAlreadyExistsException(f"Course with code '{course.code}' already exists.")


@course_router.post("/{code}/course",
                    summary="Add materials to a course",
                    description="Adds a list of materials to the specified course.",
                    responses={
                        200: {
                            "description": "Materials added successfully",
                        },
                        404: {"description": "Course not found"},
                        416: {"description": "Invalid course code"},
                        401: {"description": "Unauthorized access"},
                        403: {"description": "Forbidden"},
                    },
                    )
async def add_course_materials(code: str, materials: List[Material], request: Request,
                               user: dict = Depends(role_based_filter)):
    for material in materials:
        if 20 < material.number < 1:
            raise InvalidCourseNumberException(f"Course number '{material.number}' is not valid")

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


@course_router.post("/{code}/{course_number}/upload-course",
                    summary="Upload course document file.",
                    description="Add a file to a specified course.",
                    responses={
                        200: {"description": "File uploaded successfully"},
                        404: {"description": "Course not found"},
                        413: {"description": "File dimension is too big"},
                        416: {"description": "Parameter length not exceeded the limits"},
                        422: {"description": "File extension is not valid"}
                    })
async def upload_course_file(code: str, course_number: int, course_file: UploadFile, request: Request,
                             user: dict = Depends(role_based_filter)):
    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    if 1 > course_number > 20:
        raise InvalidCourseNumberException(f"Course number '{course_number}' out of bounds")

    file_extension = Path(course_file.filename).suffix.lower()
    if file_extension not in ALLOWED_EXTENSIONS:
        raise FileNotValidException(f"File extension '{file_extension}' not allowed")

    content = await course_file.read()  # Read file content to calculate size
    file_size_mb = len(content) / (1024 * 1024)  # Convert to MB
    if file_size_mb > MAX_FILE_SIZE_MB:
        raise FileTooBigException(f"File dimension '{file_size_mb}' is bigger than {MAX_FILE_SIZE_MB}")

    # file path naming: path-to-course/{code}/courses/course_file

    document = course_data_collection.find_one({"code": code}, {"_id": 0, "course": 1})

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' was not found")

    # this operation changes the original course name with the name of the uploaded file
    # TODO: update course_name in the database
    course_name, extention = os.path.splitext(course_file.filename)  # dont include file extention

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


@course_router.post("/{code}/lab",
                    summary="Add lab materials",
                    description="Add a list of lab materials (files not included)",
                    responses={
                        200: {"description": "Materials uploaded successfully"},
                        404: {"description": "Course not found"},
                        416: {"description": "Parameter length not exceeded the limits"},
                    })
async def add_lab_materials(code: str, materials: List[Material], request: Request,
                            user: dict = Depends(role_based_filter)):
    for material in materials:
        if 20 < material.number < 1:
            raise InvalidCourseNumberException(f"Course number '{material.number}' is not valid")

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


@course_router.delete("/{code}/{course_number}/course",
                      summary="Delete course materials",
                      description="Delete course materials from a specified course",
                      responses={
                        200: {"description": "Removed successfully"},
                        404: {"description": "Course not found"},
                        416: {"description": "Parameter length not exceeded the limits"},
                      })
async def remove_course_material(code: str, course_number: int, request: Request,
                                 user: dict = Depends(role_based_filter)):
    if 20 < course_number < 1:
        raise InvalidCourseNumberException(f"Course number '{course_number}' is not valid")

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


@course_router.delete("/{code}/{lab_number}/lab",
                      summary="Delete lab materials",
                      description="Delete lab materials from a specified lab",
                      responses={
                        200: {"description": "Removed successfully"},
                        404: {"description": "Course not found"},
                        416: {"description": "Parameter length not exceeded the limits"},
                      })
async def remove_lab_material(code: str, lab_number: int, request: Request, user: dict = Depends(role_based_filter)):
    if 20 < lab_number < 1:
        raise InvalidCourseNumberException(f"Course number '{lab_number}' is not valid")

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


@course_router.delete("/{code}",
                      summary="Delete a class",
                      description="Delete all data from a class included courses and labs",
                      responses={
                        200: {"description": "Course deleted successfully"},
                        404: {"description": "Course not found"},
                        416: {"description": "Parameter length not exceeded the limits"},
                      })
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


@course_router.get("/{code}/evaluation",
                   summary="Retrieve evaluation formula",
                   description="Retrieve evaluation formula of a specified class",
                   response_model=Evaluation,
                   responses={
                        200: {"description": "Evaluation retrieved successfully"},
                        404: {"description": "Course not found"},
                        416: {"description": "Parameter length not exceeded the limits"},
                   })
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


@course_router.get("/{code}/lab",
                   summary="Retrieve labs",
                   description="Get all labs from a specified class",
                   response_model=[Material],
                   responses={
                        200: {"description": "Labs retrieved successfully"},
                        404: {"description": "Course not found"},
                        416: {"description": "Parameter length not exceeded the limits"},
                   })
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


@course_router.get("/{code}/course",
                   summary="Retrieve courses",
                   description="Get all courses from a specified class",
                   response_model=[Material],
                   responses={
                        200: {"description": "Courses retrieved successfully"},
                        404: {"description": "Course not found"},
                        416: {"description": "Parameter length not exceeded the limits"},})
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


@course_router.post("/{code}/evaluation",
                    summary="Update evaluation",
                    description="Update evaluation method to a specified course",
                    responses={
                        200: {"description": "Evaluation updated successfully"},
                        404: {"description": "Course not found"},
                        416: {"description": "Parameter length not exceeded the limits"},
                        422: {"description": "Formula is unprocessable"},
                    })
async def update_evaluation(code: str, evaluation: List[Evaluation], request: Request,
                            user: dict = Depends(role_based_filter)):
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


@course_router.get("/{code}/courses/{course_number}",
                   summary="Retrieve course file",
                   description="Retrieve a file from a specified course",
                   response_model=FileResponse,
                   responses={
                        200: {"description": "File retrieved successfully"},
                        404: {"description": "Course or file not found"},
                        416: {"description": "Parameter length not exceeded the limits"},
                   })
async def get_course_file(code: str, course_number: int, request: Request, user: dict = Depends(role_based_filter)):
    if 20 < course_number < 1:
        raise InvalidCourseNumberException(f"Course number '{course_number}' is not valid")

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})
    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    path = Path("files") / code / "courses" / str(course_number)

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
