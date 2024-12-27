from typing import List

from models import Evaluation


def check_formula(evaluation: List[Evaluation]) -> bool:

    summ = 100

    for eva in evaluation:
        summ -= eva.weight

    return True if summ == 0 else False

