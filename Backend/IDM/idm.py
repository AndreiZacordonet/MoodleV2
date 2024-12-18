from repository import *
from exceptions.exceptions import *
from configurations.database_config import redis_client


def authenticate(email: str, password: str) -> str:
    """
    Checks email and password integrity.
    """
    # Validate email format
    if not validate_email(email):
        raise ValueError("Invalid email format.")

    # Validate password strength
    if not validate_password(password):
        raise ValueError(
            "Password must be at least 8 characters long, contain at least one uppercase letter, "
            "one lowercase letter, one digit, and one special character."
        )

    user = get_user_by_email(email)

    if user is None:
        raise UserNotFoundException(f"No user with email {email} was found.")

    is_valid = check_credentials(password, user)

    if not is_valid:
        raise InvalidCredentialsException("Invalid credentials.")

    token = generate_token(user)

    return token


def validate(token: str) -> (str, str):

    payload, error_message = decode_token(token)

    if error_message is not None:
        raise InvalidOrExpiredTokenException(error_message)

    sub, role = payload["sub"], payload["role"]

    return sub, role


def destroy(token: str) -> bool:

    if redis_client.exists(token):
        return False

    redis_client.set(token, "destroyed", ex=3600 * 72)

    return True
