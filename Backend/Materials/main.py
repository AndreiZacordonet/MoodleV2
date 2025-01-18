from fastapi import FastAPI
from evaluation_controller import course_router
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

# Configure CORS for hangular
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],  # Allow all methods
    allow_headers=["*"],  # Allow all headers
)
app.include_router(course_router, prefix="/api/materials")
