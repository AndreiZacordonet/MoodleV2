from peewee import Model, AutoField, CharField, Check
from configurations.database_config import database
from enum import Enum
import hashlib


class BaseModelPeewee(Model):
    class Meta:
        database = database


class Roles(Enum):
    STUDENT = 1
    PROFESSOR = 2
    ADMIN = 3


class Auth(BaseModelPeewee):
    id: int = AutoField(primary_key=True)

    email: str = CharField(unique=True,
                           constraints=[
                               Check("POSITION('@' IN email) > 0"),
                           ])

    password: str = CharField(null=False,
                              constraints=[
                                  Check("LENGTH(password) = 35"),
                              ])

    role: Roles = CharField(null=False, choices=[role.name for role in Roles])

    class Meta:
        db_table = 'authentication'




