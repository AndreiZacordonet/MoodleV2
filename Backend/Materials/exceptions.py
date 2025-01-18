class CourseNotFoundException(Exception):
    def __init__(self, message: str):
        self.message = message


class InvalidCourseCodeException(Exception):
    def __init__(self, message: str):
        self.message = message


class InvalidCourseNumberException(Exception):
    def __init__(self, message: str):
        self.message = message


class MaterialsNotFoundException(Exception):
    def __init__(self, message: str):
        self.message = message


class FormulaLogicException(Exception):
    def __init__(self, message: str):
        self.message = message


class ResourceAlreadyExistsException(Exception):
    def __init__(self, message: str):
        self.message = message


class FileNotValidException(Exception):
    def __init__(self, message: str):
        self.message = message
