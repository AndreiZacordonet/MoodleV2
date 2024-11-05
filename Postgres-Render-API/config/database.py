from peewee import PostgresqlDatabase
from private_keys.keys import POSTGRES_CONNECTION_STRING

db = PostgresqlDatabase(None)
db.init(POSTGRES_CONNECTION_STRING)
