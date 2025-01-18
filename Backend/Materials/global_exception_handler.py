from fastapi import Request
from fastapi.responses import JSONResponse
from datetime import datetime

from exceptions import *


async def global_exception_handler(request: Request, e: Exception):
    """
        Global exception handler for unexpected errors.
        """
    return body_build(500, "Internal Server Error", "Carrington Event like error", request)


def body_build(status: int, error: str, message: str, request: Request) -> JSONResponse:
    return JSONResponse(
        status_code=status,
        content={
            "timestamp": datetime.utcnow().isoformat(),
            "status": status,
            "error": error,
            "message": message,
            "path": str(request.url),
            "_links": {
                "api-docs": {
                    "href": "/openapi.json",
                    "rel": "openapi.json",
                    "title": "API Documentation",
                },
                "materials": {"href": "/api/materials", "rel": "materials"},
            },
        },
    )


async def course_not_found_exception_handler(
    request: Request, e: CourseNotFoundException
):
    return body_build(404, "Not Found", e.message, request)


async def invalid_course_code_exception_handler(
    request: Request, e: InvalidCourseCodeException
):
    return body_build(416, "Range Not Satisfiable", e.message, request)


async def invalid_course_number_exception_handler(
    request: Request, e: InvalidCourseNumberException
):
    return body_build(416, "Range Not Satisfiable", e.message, request)


async def materials_not_found_exception_handler(
    request: Request, e: MaterialsNotFoundException
):
    return body_build(404, "Not Found", e.message, request)


async def formula_logic_exception_handler(
    request: Request, e: FormulaLogicException
):
    return body_build(422, "Unprocessable Entity", e.message, request)


async def file_not_found_exception_handler(
    request: Request, e: FileNotFoundError
):
    return body_build(422, "Unprocessable Entity", e.strerror, request)


async def resource_already_exists_exception_handler(
    request: Request, e: ResourceAlreadyExistsException
):
    return body_build(409, "Conflict", e.message, request)


async def file_not_valid_exception_handler(
    request: Request, e: FileNotValidException
):
    return body_build(422, "Unprocessable Entity", e.message, request)
