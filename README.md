# Bakery Inventory & Management System

A console-based CRUD application built with **Pure Java + JDBC**
for the Intermediate Programming subject.

## Group Members & Roles

| Name     | Role                            | Branch         |
|----------|---------------------------------|----------------|
| Ken      | Backend Logic Developer         | ken-crud        |
| Giane    | Input & Validation Engineer     | giane-input     |
| Jonathan | System Integrator/Menu Developer| jonathan-menu   |

## Tech Stack
- Language: Java (Procedural — no OOP, no frameworks)
- Database: MySQL
- DB Access: JDBC with PreparedStatement

## Project Structure
src/
├── Main.java            # Menu system (Jonathan)
├── DBConnection.java    # JDBC connection (Jonathan)
├── CRUDOperations.java  # All SQL/CRUD logic (Ken)
└── InputHelper.java     # Input validation (Giane)

sql/
└── bakery_schema.sql    # Database schema (Giane)

##  How to Run
1. Import bakery_schema.sql into MySQL
2. Update DB credentials in DBConnection.java
3. Compile: javac src/*.java
4. Run: java -cp src Main