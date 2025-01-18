from fastapi import FastAPI
from evaluation_controller import course_router
from fastapi.middleware.cors import CORSMiddleware

from global_exception_handler import *
from exceptions import *

app = FastAPI()

# Configure CORS for hangular
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],  # Allow all methods
    allow_headers=["*"],  # Allow all headers
)
app.include_router(course_router, prefix="/api/materials")

app.add_exception_handler(Exception, global_exception_handler)  # totally unexpected exceptions (Carrington Event like)

# normal exception handlers:
app.add_exception_handler(CourseNotFoundException, course_not_found_exception_handler)
app.add_exception_handler(InvalidCourseCodeException, invalid_course_code_exception_handler)
app.add_exception_handler(InvalidCourseNumberException, invalid_course_number_exception_handler)
app.add_exception_handler(MaterialsNotFoundException, materials_not_found_exception_handler)
app.add_exception_handler(FileNotFoundError, file_not_found_exception_handler)
app.add_exception_handler(ResourceAlreadyExistsException, resource_already_exists_exception_handler)
app.add_exception_handler(FormulaLogicException, formula_logic_exception_handler)
app.add_exception_handler(FileNotValidException, file_not_valid_exception_handler)
app.add_exception_handler(FileTooBigException, file_too_big_exception_handler)
