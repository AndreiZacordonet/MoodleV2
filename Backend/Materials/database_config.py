from pymongo import MongoClient
from private_keys.keys import MONGO_CONNECTION_STRING


mongo_client = MongoClient(MONGO_CONNECTION_STRING)

database = mongo_client.materials   # set database name

course_data_collection = database["course_data_collection"]

