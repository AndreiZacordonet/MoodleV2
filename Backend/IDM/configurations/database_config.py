from peewee import PostgresqlDatabase
from private_keys.keys import DATABASE_CONNECTION_STRING, REDIS_DB_PASSWORD_STRING

import redis

database = PostgresqlDatabase(None)
database.init(DATABASE_CONNECTION_STRING)

# database = PostgresqlDatabase(None)
# database.init(
#     DATABASE_CONNECTION_STRING,
#     **{
#         "keepalives": 1,  # Enable TCP keepalives
#         "keepalives_idle": 60,  # Time before sending the first keepalive packet
#         "keepalives_interval": 30,  # Interval between keepalive packets
#         "keepalives_count": 2,  # Number of keepalives before timeout
#         # "timeout": 30,  # Connection timeout in seconds
#     }
# )

redis_client = redis.Redis(
    host='redis-13740.c135.eu-central-1-1.ec2.redns.redis-cloud.com',
    port=13740,
    decode_responses=True,
    username="default",
    password=REDIS_DB_PASSWORD_STRING,
)
