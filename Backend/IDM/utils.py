import datetime
import hashlib
import uuid
import jwt
import re

from private_keys.keys import JWT_SECRET, NONCE

from configurations.database_config import redis_client


def hash_password(password: str) -> str:
    hashed_password = hashlib.md5(password.encode('utf-8')).hexdigest()
    return hashed_password + NONCE


def generate_token(user) -> str:
    """
        Generate a JWT token with the required claims.
    """
    claims = {
        # TODO: put the right route
        "iss": "http://localhost:8080/api/academia",
        "sub": str(user.id),
        "exp": datetime.datetime.utcnow() + datetime.timedelta(hours=1),
        "jti": str(uuid.uuid4()),
        "role": user.role
    }

    token = jwt.encode(claims, JWT_SECRET, algorithm="HS256")

    return token


def check_credentials(password: str, user) -> bool or None:
    if user:
        return True if hash_password(password) == user.password else False
    else:
        return None


def decode_token(token: str) -> (dict, str):

    if redis_client.exists(token):
        return None, "Token is blacklisted."

    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=["HS256"])

        return payload, None

    except jwt.ExpiredSignatureError:
        redis_client.set(token, "expired", ex=3600 * 72)  # store token for 3 days
        return None, "Expired token."

    except jwt.InvalidTokenError:
        redis_client.set(token, "invalid", ex=3600 * 24)  # store token for 1 day
        return None, "Invalid token."


def validate_email(email: str) -> bool:
    """
    Validates the email format using a regular expression.
    """
    email_regex = r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)"
    return re.match(email_regex, email) is not None


def validate_password(password: str) -> bool:
    """
    Validates the password based on strength criteria:
    - Minimum 8 characters
    - At least one uppercase letter
    - At least one lowercase letter
    - At least one digit
    - At least one special character
    """
    if len(password) < 8:
        return False

    has_upper = any(char.isupper() for char in password)
    has_lower = any(char.islower() for char in password)
    has_digit = any(char.isdigit() for char in password)
    has_special = any(char in "!@#$%^&*()-_=+[{]}|;:'\",<.>/?`~" for char in password)

    return has_upper and has_lower and has_digit and has_special
