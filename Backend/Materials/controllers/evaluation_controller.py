import json
from typing import List, Optional

from bson.json_util import dumps

from fastapi import APIRouter, HTTPException, Body
from pymongo.errors import DuplicateKeyError

from database_config import course_data_collection
from models import CourseCreateRequest, Material

course_router = APIRouter()


@course_router.get("/")
async def get_all():
    # TODO: add pagination
    documents = course_data_collection.find({}, {"_id": 0})

    result = json.loads(dumps(documents))

    return result


@course_router.get("/{code}")
async def get_by_code(code: str):

    document = course_data_collection.find_one({"code": code}, {"_id": 0})
    if not document:
        raise HTTPException(status_code=404, detail="Course not found")

    result = json.loads(dumps(document))

    return result


@course_router.put("/")
async def create(course: CourseCreateRequest):
    """
    Create a new course.
    - `code`: Unique course code.
    - `evaluation`: List of evaluation items, each with `type` and `weight`.
    - `materials`: Optional materials for course and lab files.
    """

    if sum(item.weight for item in course.evaluation) > 100:
        raise HTTPException(status_code=400, detail="Sum of evaluation weights exceeds 100.")

    try:
        result = course_data_collection.insert_one(course.serialize_course())

        return {"message": "Course created successfully.", "_id": str(result.inserted_id)}
    except DuplicateKeyError:
        raise HTTPException(status_code=400, detail=f"Course with code '{course.code}' already exists.")


@course_router.post("/{code}/course")
async def add_course_materials(code: str, course: Material):
    result = course_data_collection.update_one(
        {"code": code},
        {"$set": course}
    )

    if result.matched_count == 0:
        raise HTTPException(status_code=404, detail=f"Course with code '{code}' not found.")

    return {"message": "Field added successfully.", "modified_count": result.modified_count}


@course_router.delete("/{code}")
async def remove_by_code(code: str):
    result = course_data_collection.delete_one({"code": code})

    if result.deleted_count == 0:
        raise HTTPException(status_code=404, detail="Course not found")

    return {"message": f"Course with code '{code}' has been successfully deleted"}


@course_router.get("/{code}/evaluation")
async def get_evaluation_formula(code: str):

    document = course_data_collection.find_one(
        {"code": code},
        {"_id": 0, "evaluation": 1}
    )

    if not document:
        raise HTTPException(status_code=404, detail="Course not found")

    return document

