# SpendSphere 💸
### Where Did My Money Go?

SpendSphere is a Java-based desktop personal finance application that helps users record expenses, monitor spending patterns, and manage monthly budgets through an interactive graphical dashboard.

Built using Java Swing, SQLite, JDBC, and Maven, the application combines expense management, persistent storage, visual analytics, and budget tracking in one place.

## ✨ Features

- **Expense Management:** Add and delete expense records.
- **Search and Filtering:** Search transactions and filter them by category and date range.
- **Persistent Storage:** Store expense records locally using SQLite.
- **Interactive Dashboard:** View spending summaries and recent transactions.
- **Visual Analytics:** Explore category-wise and monthly spending through charts.
- **Overall Monthly Budget:** Set a monthly spending limit and track progress.
- **Category Budgets:** Set separate monthly budgets for individual expense categories.
- **Date Picker:** Select transaction dates using a date-picker component.
- **Quick Launch:** Start the application on Windows using `run.bat`.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core programming language |
| Java Swing | Desktop graphical user interface |
| Apache Maven | Build automation and dependency management |
| SQLite | Local database for expense records |
| SQLite JDBC | Database connectivity between Java and SQLite |
| LGoodDatePicker | Date selection component |
| Java Preferences API | Local budget settings |
| Git and GitHub | Version control and project hosting |
| Visual Studio Code | Development environment |

## 🏗️ Architecture

SpendSphere separates the user interface, database operations, and data storage.

- **Presentation Layer:** Java Swing components display the dashboard, transactions, analytics, and budget interfaces.
- **Database Access Layer:** `ExpenseDAO` organizes expense-related database operations.
- **Database Layer:** SQLite stores expense records in a local database file.
- **Budget Settings:** Java Preferences stores overall and category budget settings separately from the expense database.

JDBC and the SQLite JDBC driver allow the Java application to execute SQL operations against SQLite.

## 📋 Prerequisites

- Java JDK 21 or later
- Apache Maven 3.10.0 or later for running Maven commands directly
- Git, if cloning the repository

**Windows launcher note:** `run.bat` uses a configured Maven installation path. If Maven is installed in a different location, update the path in the batch file before running it.

## 🚀 Getting Started

### 1. Clone the repository

Open a terminal and run:

```bash
git clone https://github.com/tejwanirishabh1/SpendSphere.git
cd SpendSphere
```

### 2. Run the application

**Option A: Windows launcher**

Double-click `run.bat` in the project directory.

**Option B: Maven command**

Open a terminal in the project directory and run:

```bash
mvn compile exec:java "-Dexec.mainClass=com.spendsphere.SpendSphereApp"
```

This command requires Maven to be available in your system's `PATH`.

### 3. Database and budget storage

- **Expense records:** Stored locally in `spendsphere.db`.
- **Budget settings:** Stored locally using Java Preferences.
- **Version control:** The SQLite database file is excluded from Git to avoid publishing personal expense records.

The application initializes its database tables through its database setup code.

## 📁 Project Structure

```text
SpendSphere/
├── src/
│   └── main/
│       └── java/
│           ├── com/
│           │   ├── spendsphere/
│           │   │   ├── SpendSphereApp.java
│           │   │   └── CategoryBudgetManager.java
│           │   ├── database/
│           │   │   ├── DatabaseManager.java
│           │   │   ├── ExpenseDAO.java
│           │   │   ├── DatabaseTest.java
│           │   │   └── ExpenseDAOTest.java
│           │   └── expense/
│           │       └── Expense.java
├── run.bat
├── pom.xml
├── .gitignore
└── README.md
```

## 📌 Development Status

The core expense-tracking, dashboard, analytics, and budget-management features are implemented and working.

SpendSphere is a learning and portfolio project, and further improvements may be added in future versions.

## 🔮 Future Improvements

- Edit existing transactions
- Export expense reports
- Expand analytics and spending insights
- Improve usability and accessibility
- Explore an AI-assisted spending insights feature

## 👨‍💻 Author

**Rishabh Tejwani**

- GitHub Profile: [tejwanirishabh1](https://github.com/tejwanirishabh1)
- Project Repository: [SpendSphere](https://github.com/tejwanirishabh1/SpendSphere)

---

*Spend smarter. Understand better. Take control of your expenses.*