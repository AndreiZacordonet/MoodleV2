from repository import *
from exceptions.exceptions import *
from configurations.database_config import redis_client


def authenticate(email: str, password: str) -> str:
    """
    Checks email and password integrity.
    """
    # TODO: validate email, validate password

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
