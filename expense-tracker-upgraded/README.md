# 💰 ExpenseTracker v2.0 — Production Upgrade

## 🚀 Quick Start

### 1. Setup MySQL
```sql
CREATE DATABASE expensetracker;
```

### 2. Configure application.properties
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expensetracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 3. Run
```bash
mvn spring-boot:run
```
Visit: http://localhost:8080

---

## 🔑 Default Credentials
| Role  | Username | Password |
|-------|----------|----------|
| Admin | admin    | admin123 |
| User  | user     | user123  |

---

## ✅ Features
- ✅ MySQL database (H2 fallback available)
- ✅ Role-based login: USER → /dashboard, ADMIN → /admin/dashboard
- ✅ BCrypt password encoding
- ✅ Admin: view all users, delete/disable, view all expenses, top spenders
- ✅ Salary + monthly budget tracking
- ✅ Remaining balance = Salary - Expenses
- ✅ Budget warning at 80% threshold
- ✅ Expense categories, description, date
- ✅ Filter by category, date range, keyword
- ✅ Chart.js: Pie (category), Bar (monthly), Line (income vs expense)
- ✅ Budget progress bar with color alerts
- ✅ High-spending category alert
- ✅ Monthly archive / reset feature
- ✅ Export to CSV
- ✅ Export to PDF (iText)
- ✅ Session timeout (30 min)
- ✅ Mobile-responsive modern UI
- ✅ Profile page to set salary & budget

---

## 📁 Project Structure
```
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
