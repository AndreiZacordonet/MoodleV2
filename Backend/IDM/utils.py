import datetime
import hashlib
import uuid
import jwt

from private_keys.keys import JWT_SECRET

from configurations.database_config import redis_client


def hash_password(password: str) -> str:
    hashed_password = hashlib.md5(password.encode('utf-8')).hexdigest()
    return hashed_password + "1A4"


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
