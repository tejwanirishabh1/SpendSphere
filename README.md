
# SpendSphere 💸

### Where Did My Money Go?

SpendSphere is a desktop personal finance application built with Java Swing. It helps users record expenses, organize spending, review financial activity, and manage budgets through a visual dashboard.

## Features

- **Expense Management:** Add and delete expenses.
- **Search and Filtering:** Find transactions using search, category, and date filters.
- **Persistent Storage:** Store expense records using SQLite.
- **Dashboard:** View spending summaries and recent transactions.
- **Visual Analytics:** Explore category-wise and monthly spending charts.
- **Overall Monthly Budget:** Set a monthly spending limit and track progress.
- **Category Budgets:** Set separate monthly budgets for individual expense categories.
- **Date Selection:** Use a date picker when entering transactions.
- **Quick Launch:** Start the application using `run.bat` on Windows.

## Tech Stack

- **Language:** Java 21
- **GUI:** Java Swing
- **Build Tool:** Apache Maven
- **Database:** SQLite
- **JDBC Driver:** SQLite JDBC
- **Date Picker:** LGoodDatePicker
- **Version Control:** Git and GitHub
- **Development Environment:** Visual Studio Code

## Prerequisites

- Java JDK 21 or later
- Apache Maven 3.10.0 or later, or the included Windows launcher configured for your Maven installation
- Git, if cloning the repository

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/tejwanirishabh1/SpendSphere.git
cd SpendSphere
```

### 2. Run on Windows

Double-click `run.bat` from the project folder.

Alternatively, open a terminal in the project directory and run:

```bash
mvn compile exec:java "-Dexec.mainClass=com.spendsphere.SpendSphereApp"
```

The command-line method requires Maven to be available in your PATH.

### 3. Database and budgets

Expense records are stored locally in `spendsphere.db`. The database is intentionally excluded from version control so personal expense data is not published to GitHub.

Budget preferences are stored locally using Java Preferences.

## Project Structure

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

## Development Status

SpendSphere is an actively developed learning project. Features and interface details may evolve as development continues.

## Future Improvements

- Edit existing transactions
- Export reports
- Additional analytics and insights
- Further usability and accessibility improvements

## Author

**Rishabh Tejwani**

- GitHub: [@tejwanirishabh1](https://github.com/tejwanirishabh1)
- Repository: [SpendSphere](https://github.com/tejwanirishabh1/SpendSphere)

---

*Spend smarter. Understand better. Take control of your expenses.*
