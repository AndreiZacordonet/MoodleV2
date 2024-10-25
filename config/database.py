from pymongo import MongoClient
from private_keys.keys import MONGO_CLIENT

client = MongoClient(MONGO_CLIENT)

db = client.todo_db

collection_name = db["todo_collection"]