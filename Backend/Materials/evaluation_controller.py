import json
from typing import List, Optional, Set

from bson.json_util import dumps

from fastapi import APIRouter, HTTPException, Body
from pymongo.errors import DuplicateKeyError

from database_config import course_data_collection
from models import CourseCreateRequest, Material, Evaluation
from service import check_formula

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
async def add_course_materials(code: str, materials: List[Material]):
    # TODO: input validation

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise HTTPException(status_code=404, detail=f"Course with code '{code}' not found.")

    existing_materials = document.get("course")

    new_materials = [material.model_dump() for material in materials]

    combined_materials = {tuple(material.items()): material for material in existing_materials + new_materials}.values()

    result = course_data_collection.update_one({"code": code}, {"$set": {"course": list(combined_materials)}})

    return {"message": "Field added successfully.", "modified_count": result.modified_count}


@course_router.post("/{code}/lab")
async def add_course_materials(code: str, materials: List[Material]):
    # TODO: input validation

    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise HTTPException(status_code=404, detail=f"Course with code '{code}' not found.")

    existing_materials = document.get("lab")

    new_materials = [material.model_dump() for material in materials]

    combined_materials = {tuple(material.items()): material for material in existing_materials + new_materials}.values()

    result = course_data_collection.update_one({"code": code}, {"$set": {"lab": list(combined_materials)}})

    return {"message": "Field added successfully.", "modified_count": result.modified_count}


@course_router.delete("/{code}/course")
async def remove_material(code: str, material: Material):
    ### BIG TODO: every file to have in name "_{code}" where code is course unique code
    ### or maybe stored in a specific folder ###
    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise HTTPException(status_code=404, detail=f"Course with code '{code}' not found.")

    existing_materials = document.get("course")

    if existing_materials is None:
        # TODO: create exception for this case
        raise Exception("The specified course does not have any materials.")

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
async def remove_material(code: str, material: Material):
    ### BIG TODO: every file to have in name "_{code}" where code is course unique code
    ### or maybe stored in a specific folder ###
    document = course_data_collection.find_one({"code": code}, {"_id": 0})

    if not document:
        raise HTTPException(status_code=404, detail=f"Course with code '{code}' not found.")

    existing_materials = document.get("lab")

    if existing_materials is None:
        # TODO: create exception for this case
        raise Exception("The specified course does not have any materials.")

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


@course_router.get("/{code}/lab")
async def get_labs(code: str):

    document = course_data_collection.find_one(
        {"code": code},
        {"_id": 0, "lab": 1}
    )

    if not document:
        raise HTTPException(status_code=404, detail="Course not found")

    return document


@course_router.get("/{code}/course")
async def get_courses(code: str):

    document = course_data_collection.find_one(
        {"code": code},
        {"_id": 0, "course": 1}
    )

    if not document:
        raise HTTPException(status_code=404, detail="Course not found")

    return document


@course_router.post("/{code/evaluation")
async def update_evaluation(code: str, evaluation: List[Evaluation]):
    # TODO: check the code
    document = course_data_collection.find_one({"code": code}, {"_id": 0})
    if not document:
        raise HTTPException(status_code=404, detail=f"Course with code '{code}' not found.")

    if not check_formula(evaluation):
        # TODO: create custom exception
        raise HTTPException(status_code=422, detail="Formula doesnt add up to 100.")

    result = course_data_collection.update_one({"code": code},
                                               {"$set": {"evaluation": [eva.model_dump() for eva in evaluation]}})

    return {"message": "Evaluation method updated successfully.", "modified_count": result.modified_count}

