from database_config import course_data_collection
from models import EvaluationTypes

course_data = [
    {
        "code": "MATH69",
        "evaluation": [
            {"type": EvaluationTypes.FINAL_EXAM.name, "weight": 50},
            {"type": EvaluationTypes.LAB_ACTIVITY.name, "weight": 50},
        ],
        "course": [
            {"number": 1, "file": "ciorba_de_vacuta_aerodinamica.pdf"},
            {"number": 2, "file": "ciorba_de_vacuta_aerodinamica2.pdf"},
            {"number": 3, "file": "ecuatii_diferentiale_lichide.pdf"},
        ],
        "lab": [
            {"number": 1, "file": "namaletfai.txt"},
        ]
    },

    {
        "code": "NM",
        "evaluation": [
            {"type": EvaluationTypes.FINAL_EXAM.name, "weight": 60},
            {"type": EvaluationTypes.LAB_ACTIVITY.name, "weight": 10},
            {"type": EvaluationTypes.PROJECT.name, "weight": 30},
        ],
        "course": [
            {"number": 1, "file": "nambani.pdf"},
            {"number": 2, "file": "iarnambani.pdf"},
            {"number": 3, "file": "namcefacecuviatamea.pdf"},
        ],
        "lab": [
            {"number": 1, "file": "numiaplacutdeloclalaboratorlamn.txt"},
        ]
    },
]

course_data_collection.create_index("code", unique=True)

course_data_collection.delete_many({})

course_data_collection.insert_many(course_data)
