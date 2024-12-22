from enum import Enum
from pydantic import BaseModel
from typing import List, Optional


class EvaluationTypes(Enum):

    FINAL_EXAM = 1
    LAB_ACTIVITY = 2
    PROJECT = 3


class Evaluation(BaseModel):
    type: str
    weight: int


class Material(BaseModel):
    number: int
    file: str


class Materials(BaseModel):
    course: Optional[List[Material]] = []
    lab: Optional[List[Material]] = []


class CourseCreateRequest(BaseModel):
    code: str
    evaluation: List[Evaluation]
    materials: Materials = Materials()

