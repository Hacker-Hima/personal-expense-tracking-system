###💰 Personal Expense Tracking System

A full-stack expense tracking and budget management application built using Spring Boot, Thymeleaf, MySQL, and Spring Security.

## Features

### User Features

* User Registration & Login
* Secure Authentication using Spring Security + BCrypt
* Add / Delete Expenses
* Expense Categorization
* Monthly Budget Tracking
* Salary Management
* Remaining Balance Calculation
* Expense History
* Expense Filtering
* CSV & PDF Export
* Budget Warning Alerts
* Responsive User Dashboard

### Admin Features

* Separate Admin Dashboard
* View All Users
* Delete Users
* Monitor System Transactions
* View Top Spenders
* Expense Analytics Overview

### Analytics

* Category-wise Expense Analysis
* Monthly Expense Tracking
* Income vs Expense Visualization
* Interactive Dashboard Charts

## Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate

### Frontend

* Thymeleaf
* HTML5
* CSS3
* JavaScript
* Chart.js

### Database

* MySQL

### Build Tool

* Maven

## 📁 Project Structure

```plaintext
src/main/java/com/expensetracker/
├── config/         SecurityConfig, UserDetailsServiceImpl
├── controller/     AuthController, ExpenseController, AdminController
├── model/          User, Expense, ArchivedExpense
├── repository/     UserRepository, ExpenseRepository, ArchivedExpenseRepository
└── service/        ExpenseService, ExportService

src/main/resources/
├── templates/      login, register, user-dashboard, admin-dashboard,
│                   admin-users, profile, history
└── application.properties
```

## Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/Hacker-Hima/personal-expense-tracking-system.git
```

### 2. Open Project

Open the project in VS Code or IntelliJ IDEA.

### 3. Create MySQL Database

```sql
CREATE DATABASE expensetracker;
```

### 4. Configure Database

Edit:

```plaintext
src/main/resources/application.properties
```

Set your MySQL username and password:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 5. Run Application

```bash
mvn spring-boot:run
```

### 6. Open Application

```plaintext
http://localhost:8080
```

## Default Login Credentials

### Admin

```plaintext
Username: admin
Password: *****
```

### User

```plaintext
Username: user
Password: *****
```

## Security Features

* BCrypt Password Encryption
* Role-Based Access Control
* Secure Authentication
* Protected Routes

## Future Improvements

* Dark Mode
* AI-based Spending Insights
* Mobile App Integration
* Cloud Deployment
* Email Notifications

## Author

Himachalam C

## License

This project is for educational and learning purposes.
