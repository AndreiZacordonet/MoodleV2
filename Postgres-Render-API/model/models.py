from peewee import Model, IntegerField, CharField, ForeignKeyField, CompositeKey, PrimaryKeyField, AutoField
from config.database import db


class BaseModel(Model):
    class Meta:
        database = db


class Game(BaseModel):
    id = IntegerField(primary_key=True)
    username = CharField()

    class Meta:
        db_table = "users"


class Achievement(BaseModel):
    id = IntegerField(primary_key=True)
    name = CharField()
    game_id = ForeignKeyField(Game, backref='achievements')

    class Meta:
        db_table = "achievements"


class User(BaseModel):
    id = AutoField(primary_key=True)    # used for auto-generated PK
    username = CharField()

    class Meta:
        db_table = "users"


class GamesUsers(BaseModel):
    user_id = ForeignKeyField(User)
    game_id = ForeignKeyField(Game)

    class Meta:
        db_table = "game_users"
        primary_key = CompositeKey('user_id', 'game_id')

