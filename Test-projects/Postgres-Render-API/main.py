from fastapi import FastAPI, HTTPException
from config.database import db
from model.models import User, Game, Achievement, GamesUsers
from peewee import OperationalError, IntegrityError
app = FastAPI()

with db:
    db.drop_tables([Achievement])
    db.drop_tables([GamesUsers])
    db.drop_tables([Game])
    db.drop_tables([User])
    # db.drop_tables([Achievement, User, Game, GamesUsers], safe=False)
    db.create_tables([User, Game, Achievement, GamesUsers])


@app.get("/")
async def root():

    return {"message": "Hello World"}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}


@app.post("/users/")
async def create_user(username: str):
    db.connect()
    try:
        user, created = User.get_or_create(username=username)
        if created:
            return {"id": user.id, "username": user.username}
    except OperationalError as e:
        print("OperationalError: ", e)
    except IntegrityError as e:
        print("IntegrityError:", e)
    finally:
        db.close()
    db.close()


@app.get("/users/")
async def read_users():
    users = User.select()
    return [{"id": user.id, "username": user.username} for user in users]


@app.post("/games/")
async def create_game(name: str):
    game, created = Game.get_or_create(name=name)
    if created:
        return {"id": game.id, "name": game.name}
    raise HTTPException(status_code=400, detail="Game already exists")


@app.get("/games/")
async def read_games():
    games = Game.select()
    return [{"id": game.id, "name": game.name} for game in games]


@app.post("/achievements/")
async def create_achievement(name: str, game_id: int):
    try:
        game = Game.get(Game.id == game_id)
        achievement = Achievement.create(name=name, game_id=game)
        return {"id": achievement.id, "name": achievement.name, "game_id": game_id}
    except Game.DoesNotExist:
        raise HTTPException(status_code=404, detail="Game not found")


@app.get("/achievements/")
async def read_achievements():
    achievements = Achievement.select()
    return [{"id": ach.id, "name": ach.name, "game_id": ach.game_id.id} for ach in achievements]


@app.post("/games_users/")
async def add_user_to_game(user_id: int, game_id: int):
    try:
        user = User.get(User.id == user_id)
        game = Game.get(Game.id == game_id)
        GamesUsers.create(user_id=user, game_id=game)
        return {"user_id": user_id, "game_id": game_id}
    except User.DoesNotExist:
        raise HTTPException(status_code=404, detail="User not found")
    except Game.DoesNotExist:
        raise HTTPException(status_code=404, detail="Game not found")


@app.delete("/users/{user_id}")
async def delete_user(user_id: int):
    query = User.delete().where(User.id == user_id)
    deleted = query.execute()
    if deleted:
        return {"detail": "User deleted successfully"}
    raise HTTPException(status_code=404, detail="User not found")

