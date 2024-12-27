from fastapi import FastAPI
from evaluation_controller import course_router

app = FastAPI()

app.include_router(course_router, prefix="/api/materials")
