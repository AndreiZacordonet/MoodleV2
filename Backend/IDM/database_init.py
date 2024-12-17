from repository import save_auth
from models.models import *

with database:
    database.drop_tables([Auth])
    database.create_tables([Auth])

    save_auth("andrei74c0@gmail.com", "pass1234", Roles.ADMIN)
    save_auth("shaqoniq@bingxilling.com", "pass1234", Roles.STUDENT)
