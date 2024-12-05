
# Moodle Simulation Platform - MoodleV2

This project implements a Spring Boot RESTful API to simulate a Moodle-like platform for managing university data. The application supports operations for students, professors, and courses, with a PostgreSQL database hosted on [Supabase](https://supabase.com).

---

## Features

### Core Functionalities
- **Student and Professor Management**:
  - CRUD operations for students and professors.
  - Filtering and pagination support for efficient data querying.
- **Course Management**:
  - CRUD operations for courses, including associated materials and assessments.
  - Relationship management between professors, students, and courses. *(TO BE DONE)*
- **Identity Management Module (IDM)**:
*(TO BE DONE)*
  - User roles: Administrator, Professor, and Student.
  - Role-based access control for data operations.

### API Design
- **RESTful Endpoints**:
  - Endpoints adhere to RESTful principles with HATEOAS compliance.
  - Rich navigation through resource states using hypermedia links.
- **DTO Pattern**:
  - Efficient data transfer with minimal API calls using Data Transfer Objects.
- **Query Parameters**:
  - Flexible filtering and search functionality.
  - Path and query parameters for precise resource access.

### Database
- **PostgreSQL**:
  - Normalized schema with tables for students, professors, courses, and their relationships.
  - Hosted on Supabase for reliable cloud-based database management.

---

## Project Structure

The project follows a modular design with separation of concerns:

- **Model**: Defines entities and data access objects (DAOs).
- **Controller**: Handles routing and business logic.
- **Service**: Encapsulates application logic and interacts with the database.

---

## Disclaimer

This project is a work in progress and is actively under development. Features, functionality, and documentation are subject to change as the project evolves. Contributions and feedback are welcome, but please note that the current implementation may not yet fully reflect the final product.

