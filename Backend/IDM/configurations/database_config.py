from peewee import PostgresqlDatabase
from private_keys.keys import DATABASE_CONNECTION_STRING

database = PostgresqlDatabase(None)
database.init(DATABASE_CONNECTION_STRING)
