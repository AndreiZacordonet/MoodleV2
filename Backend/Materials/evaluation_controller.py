import json
import mimetypes
from typing import List, Optional, Set

from bson.json_util import dumps

from fastapi import APIRouter, HTTPException, Body, Query, UploadFile, Depends
from fastapi.responses import FileResponse

from pymongo.errors import DuplicateKeyError

from database_config import course_data_collection
from exceptions import *
from models import CourseCreateRequest, Material, Evaluation
from service import check_formula

from pathlib import Path
import os

from web_filter import role_based_filter

course_router = APIRouter()


@course_router.get("/")
async def get_all(user: dict = Depends(role_based_filter)):
    # TODO: add pagination
    documents = course_data_collection.find({}, {"_id": 0})

    result = json.loads(dumps(documents))

    return result


@course_router.get("/{code}")
async def get_by_code(code: str, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})
    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' was not found")

    result = json.loads(dumps(document))

    return result


@course_router.put("/")
async def create(course: CourseCreateRequest, user: dict = Depends(role_based_filter)):
    """
    Create a new course.
    - `code`: Unique course code.
    - `evaluation`: List of evaluation items, each with `type` and `weight`.
    - `materials`: Optional materials for course and lab files.
    """

    if sum(item.weight for item in course.evaluation) != 100:
        # TODO: proper error handling
        raise HTTPException(status_code=400, detail="Sum of evaluation weights is incorrect.")

    try:
        result = course_data_collection.insert_one(course.serialize_course())

        return {"message": "Course created successfully.", "_id": str(result.inserted_id)}
    except DuplicateKeyError:
        # TODO: proper error handling
        raise HTTPException(status_code=400, detail=f"Course with code '{course.code}' already exists.")


@course_router.post("/{code}/course")
async def add_course_materials(code: str, materials: List[Material], user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise CourseNotFoundException("Course with code '{code}' not found.")

    existing_materials = document.get("course")

    new_materials = [material.model_dump() for material in materials]

    combined_materials = {tuple(material.items()): material for material in existing_materials + new_materials}.values()

    result = course_data_collection.update_one({"code": code}, {"$set": {"course": list(combined_materials)}})

    return {"message": "Field added successfully.", "modified_count": result.modified_count}


@course_router.post("/{code}/{course_number}/upload-course")
async def upload_course_file(code: str, course_number: int, course_file: UploadFile, user: dict = Depends(role_based_filter)):

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

    return {"message": "File added successfully.", "modified_count": result.modified_count}


@course_router.post("/{code}/lab")
async def add_course_materials(code: str, materials: List[Material], user: dict = Depends(role_based_filter)):
    # TODO: input validation

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    existing_materials = document.get("lab")

    new_materials = [material.model_dump() for material in materials]

    combined_materials = {tuple(material.items()): material for material in existing_materials + new_materials}.values()

    result = course_data_collection.update_one({"code": code}, {"$set": {"lab": list(combined_materials)}})

    return {"message": "Field added successfully.", "modified_count": result.modified_count}

# TODO: upload lab file route


@course_router.delete("/{code}/course")
async def remove_material(code: str, material: Material, user: dict = Depends(role_based_filter)):
    # BIG TODO: every file to have in name "_{code}" where code is course unique code
    # or maybe stored in a specific folder ###

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    existing_materials = document.get("course")

    if existing_materials is None:
        raise MaterialsNotFoundException(f"The course '{code}' does not have any materials.")

    try:
        print(f'\n\nexisting materilas: {existing_materials}\ndocument: {document}\n\n')

        existing_materials.index(material.model_dump())     # this raises ValueError

        existing_materials.remove(material.model_dump())

        result = course_data_collection.update_one({"code": code}, {"$set": {"course": list(existing_materials)}})

        return {"message": "Field deleted successfully.", "modified_count": result.modified_count}

    except ValueError as e:
        # TODO: raise a custom exception
        raise HTTPException(status_code=404, detail=f"Course with code '{code}' not found.")


@course_router.delete("/{code}/lab")
async def remove_material(code: str, material: Material, user: dict = Depends(role_based_filter)):
    # BIG TODO: every file to have in name "_{code}" where code is course unique code
    # or maybe stored in a specific folder ###

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    existing_materials = document.get("lab")

    if existing_materials is None:
        raise MaterialsNotFoundException(f"The course '{code}' does not have any materials.")

    try:
        print(f'\n\nexisting materilas: {existing_materials}\ndocument: {document}\n\n')

        existing_materials.index(material.model_dump())     # this raises ValueError

        existing_materials.remove(material.model_dump())

        result = course_data_collection.update_one({"code": code}, {"$set": {"lab": list(existing_materials)}})

        return {"message": "Field deleted successfully.", "modified_count": result.modified_count}

    except ValueError:
        # TODO: raise a custom exception
        raise HTTPException(status_code=404, detail=f"Lab with code '{code}' not found.")


@course_router.delete("/{code}")
async def remove_by_code(code: str, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    result = course_data_collection.delete_one({"code": code})

    if result.deleted_count == 0:
        raise CourseNotFoundException(f"Course with code '{code}' not found")

    return {"message": f"Course with code '{code}' has been successfully deleted"}


@course_router.get("/{code}/evaluation")
async def get_evaluation_formula(code: str, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one(
        {"code": code},
        {"_id": 0, "evaluation": 1}
    )

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    return document


@course_router.get("/{code}/lab")
async def get_labs(code: str, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one(
        {"code": code},
        {"_id": 0, "lab": 1}
    )

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    return document


@course_router.get("/{code}/course")
async def get_courses(code: str, user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one(
        {"code": code},
        {"_id": 0, "course": 1}
    )

    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    return document


@course_router.post("/{code/evaluation")
async def update_evaluation(code: str, evaluation: List[Evaluation], user: dict = Depends(role_based_filter)):

    if not code or len(code) > 20 or code.isspace():
        raise InvalidCourseCodeException(f"Code '{code}' is invalid or out of bounds")

    document = course_data_collection.find_one({"code": code}, {"_id": 0})
    if not document:
        raise CourseNotFoundException(f"Course with code '{code}' not found.")

    if not check_formula(evaluation):
        raise FormulaLogicException("Formula doesn't add up to 100.")

    result = course_data_collection.update_one({"code": code},
                                               {"$set": {"evaluation": [eva.model_dump() for eva in evaluation]}})

    return {"message": "Evaluation method updated successfully.", "modified_count": result.modified_count}


@course_router.get("/{code}/courses/{course_number}")
async def get_course_file(code: str, course_number: str, user: dict = Depends(role_based_filter)):

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

