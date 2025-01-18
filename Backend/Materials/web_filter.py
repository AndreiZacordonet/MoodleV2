from fastapi import HTTPException, Request
from grpc_client import validate_token

import re


# ALLOWED_ROLES = {
#     ["/api/materials/", "GET"]: ["STUDENT", "PROFESSOR"],   # get all
#     ["/api/materials/{code}", "GET"]: ["STUDENT", "PROFESSOR"],     # get by code
#     ["/api/materials/", "PUT"]: ["PROFESSOR"],      # create new
#     ["/api/materials/{code}", "DELETE"]: ["ADMIN", "PROFESSOR"],    # delete by code
#     ["/api/materials/{code}/course", "POST"]: ["PROFESSOR"],    # add course materials (not file upload)
#     ["/api/materials/{code}/{course_number}/upload-course", "POST"]: ["PROFESSOR"],  # add (upload) course file
#     ["/api/materials/{code}/lab", "POST"]: ["PROFESSOR"],    # add lab materials (not file upload)
#     ["/api/materials/{code}/course", "DELETE"]: ["PROFESSOR"],   # delete course material
#     ["/api/materials/{code}/lab", "DELETE"]: ["PROFESSOR"],   # delete lab material
#     ["/api/materials/{code}/evaluation", "GET"]: ["STUDENT", "PROFESSOR"],   # get evaluation
#     ["/api/materials/{code}/lab", "GET"]: ["STUDENT", "PROFESSOR"],     # get labs
#     ["/api/materials/{code}/course", "GET"]: ["STUDENT", "PROFESSOR"],     # get courses
#     ["/api/materials/{code}/evaluation", "POST"]: ["PROFESSOR"],    # update evaluation
#     ["/api/materials/{code}/courses/{course_number}", "GET"]: ["STUDENT", "PROFESSOR"],    # get course file
# }

ALLOWED_ROLES = {
    (re.compile(r"^/api/materials/$"), "GET"): ["STUDENT", "PROFESSOR"],  # get all
    (re.compile(r"^/api/materials/[^/]+$"), "GET"): ["STUDENT", "PROFESSOR"],  # get by code
    (re.compile(r"^/api/materials/$"), "PUT"): ["PROFESSOR"],  # create new
    (re.compile(r"^/api/materials/[^/]+$"), "DELETE"): ["ADMIN", "PROFESSOR"],  # delete by code
    (re.compile(r"^/api/materials/[^/]+/course$"), "POST"): ["PROFESSOR"],  # add course materials
    (re.compile(r"^/api/materials/[^/]+/\d+/upload-course$"), "POST"): ["PROFESSOR"],  # add (upload) course file
    (re.compile(r"^/api/materials/[^/]+/lab$"), "POST"): ["PROFESSOR"],  # add lab materials
    (re.compile(r"^/api/materials/[^/]+/course$"), "DELETE"): ["PROFESSOR"],  # delete course material
    (re.compile(r"^/api/materials/[^/]+/lab$"), "DELETE"): ["PROFESSOR"],  # delete lab material
    (re.compile(r"^/api/materials/[^/]+/evaluation$"), "GET"): ["STUDENT", "PROFESSOR"],  # get evaluation
    (re.compile(r"^/api/materials/[^/]+/lab$"), "GET"): ["STUDENT", "PROFESSOR"],  # get labs
    (re.compile(r"^/api/materials/[^/]+/course$"), "GET"): ["STUDENT", "PROFESSOR"],  # get courses
    (re.compile(r"^/api/materials/[^/]+/evaluation$"), "POST"): ["PROFESSOR"],  # update evaluation
    (re.compile(r"^/api/materials/[^/]+/courses/\d+$"), "GET"): ["STUDENT", "PROFESSOR"],  # get course file
}


async def role_based_filter(request: Request):
    """
    Dependency to filter requests based on roles defined in ALLOWED_ROLES.
    """
    auth_header = request.headers.get("Authorization")
    if not auth_header or not auth_header.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Authorization header missing or malformed")

    token = auth_header.split(" ")[1]
    try:
        user_data = validate_token(token)
    except ValueError:
        raise HTTPException(status_code=401, detail="Invalid or expired token")
    except RuntimeError as e:
        raise HTTPException(status_code=500, detail=str(e))

    # Extract HTTP method and path
    method = request.method
    path = request.url.path

    print(f'\npath: {path}\nmethod: {method}\nuser role: {user_data["role"]}')

    # Match the path and method in the ALLOWED_ROLES dictionary
    for (path_pattern, allowed_method), allowed_roles in ALLOWED_ROLES.items():
        if path_pattern.match(path) and method == allowed_method:
            # Check if the user's role is allowed
            if user_data["role"] not in allowed_roles:
                raise HTTPException(status_code=403, detail="You do not have the required role to access this route")
            return user_data

    # If no matching route is found, deny access
    raise HTTPException(status_code=403, detail="Access to this route is restricted")

# async def role_based_filter(request: Request):
#     """
#     Dependency to filter requests based on roles defined in ALLOWED_ROLES.
#     """
#     auth_header = request.headers.get("Authorization")
#     if not auth_header or not auth_header.startswith("Bearer "):
#         raise HTTPException(status_code=401, detail="Authorization header missing or malformed")
#
#     token = auth_header.split(" ")[1]
#     try:
#         user_data = validate_token(token)
#     except ValueError:
#         raise HTTPException(status_code=401, detail="Invalid or expired token")
#     except RuntimeError as e:
#         raise HTTPException(status_code=500, detail=str(e))
#
#     # Get the allowed roles for the current route
#     route_path = request.url.path
#     allowed_roles = ALLOWED_ROLES.get(route_path)
#
#     if not allowed_roles:
#         raise HTTPException(status_code=403, detail="Access to this route is restricted")
#
#     # Check if the user's role is in the allowed roles
#     if user_data["role"] not in allowed_roles:
#         raise HTTPException(status_code=403, detail="You do not have the required role to access this route")
#
#     return user_data
