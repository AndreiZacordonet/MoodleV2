import time

from repository import *
from exceptions.exceptions import *
from configurations.database_config import redis_client, database


def authenticate(email: str, password: str) -> str:
    """
    Checks email and password integrity.
    """
    for _ in range(3):
        try:
            # Validate email format
            if not validate_email(email):
                raise ValueError("Invalid email format.")

            # Validate password strength
            if not validate_password(password):
                raise ValueError(
                    "Password must be at least 8 characters long, contain at least one uppercase letter, "
                    "one lowercase letter, one digit, and one special character."
                )

            print("veificam db")
            if database.is_closed():
                print("deschidem db")
                database.connect()

            user = get_user_by_email(email)
            print(f"User: {user}")

            if user is None:
                raise UserNotFoundException(f"No user with email {email} was found.")

            is_valid = check_credentials(password, user)
            if not is_valid:
                raise InvalidCredentialsException("Invalid credentials.")

            return generate_token(user)
        finally:
            if not database.is_closed():
                database.close()
            time.sleep(1)


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
