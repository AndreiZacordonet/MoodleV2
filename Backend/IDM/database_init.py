from repository import save_auth
from models.models import *

with database:
    database.drop_tables([Auth])
    database.create_tables([Auth])

    save_auth("andrei74c0@gmail.com", "Pass.1234", Roles.ADMIN)
    save_auth("a@gmail.com", "Pass.1234", Roles.STUDENT)
    save_auth("asd@asd.asd", "Pass.1234", Roles.PROFESSOR)
