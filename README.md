# Land Management System

A desktop application for managing land records in Senegal: parcel registration, ownership transfers, disputes and audit trails, with separate workspaces for citizens, land agents and administrators.

## Features

**Citizens**
- View their parcels and transactions
- Open and follow land disputes
- Receive notifications

**Land agents**
- Register new parcels in their region
- Process ownership transfers
- Handle disputes and add notes to parcels

**Administrators**
- Dashboard with key statistics
- Manage users, parcels, transactions and disputes
- Audit log of sensitive actions

**Security**
- Passwords hashed with bcrypt
- Role-based access and session management
- Input validation and automatic parcel number generation

## Tech stack

Java 17 · Swing · SQLite (JDBC) · Maven · jBCrypt · JUnit 5

## Project structure

```
src/
├── models/     # Parcel, Citizen, LandAgent, Transaction, Dispute, ...
├── dao/        # Data access objects (one per model)
├── database/   # SQLite connection and schema initialization
├── views/      # Swing UI: login, register, and one dashboard per role
├── utils/      # Password hashing, sessions, validation, dates
└── main/       # Test entry points
```

## Getting started

Requirements: Java 17+ and Maven.

```bash
mvn clean package
```

Then run the application from your IDE (IntelliJ IDEA project files are included). The SQLite database `land_management.db` is created and initialized automatically.

## Author

Fatoumata Diouf — [@timah1402](https://github.com/timah1402)
