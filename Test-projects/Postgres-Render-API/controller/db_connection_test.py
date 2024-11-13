from model.models import *
from config.database import db


def check_if_user_exists(searched_username: str, checked_user: User) -> None:
    if checked_user:
        print(f"Searching by username '{searched_username}' resulted in: User: {checked_user.username}")
    else:
        print(f"No user found with username '{searched_username}'")

# def get_all_users_and_games: