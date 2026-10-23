
package com.spendsphere;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.database.ExpenseDAO;
import com.expense.Expense;

public class SpendSphereApp {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color NAVY = new Color(24, 35, 56);
    private static final Color TEXT = new Color(40, 50, 65);
    private static final Color MUTED = new Color(115, 125, 140);
    private static final Color BORDER = new Color(225, 230, 237);

    private static final ExpenseDAO expenseDAO = new ExpenseDAO();

    private static JLabel totalSpendingValue;
    private static JLabel monthlySpendingValue;
    private static JLabel transactionCountValue;
    private static JLabel totalDescription;

    private static DefaultTableModel tableModel;
    private static JTable expenseTable;
    private static JFrame frame;

    private static JTextField searchField;
    private static JComboBox<String> categoryFilter;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpendSphereApp::createUI);
    }

    private static void createUI() {
        frame = new JFrame("SpendSphere | Where Did My Money Go?");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 720);
        frame.setMinimumSize(new Dimension(850, 600));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        );

        JLabel brand = new JLabel("SpendSphere");
        brand.setFont(new Font("SansSerif", Font.BOLD, 26));
        brand.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("WHERE DID MY MONEY GO?");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tagline.setForeground(new Color(190, 200, 215));

        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.add(brand);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(tagline);

        JLabel status = new JLabel("PERSONAL FINANCE");
        status.setForeground(new Color(210, 220, 235));
        status.setFont(new Font("SansSerif", Font.BOLD, 12));

        JButton addExpenseButton = new JButton("+ Add Expense");
        addExpenseButton.setFocusPainted(false);
        addExpenseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addExpenseButton.addActionListener(e -> showAddExpenseForm());

        JPanel headerRight = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 15, 0)
        );
        headerRight.setOpaque(false);
        headerRight.add(status);
        headerRight.add(addExpenseButton);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        // Main content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(
                BorderFactory.createEmptyBorder(32, 35, 30, 35)
        );

        JLabel welcome = new JLabel("Your money, in perspective.");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcome.setForeground(TEXT);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Track your spending, understand your habits, "
                + "and plan with confidence."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 18, 0));
        summaryPanel.setBackground(BACKGROUND);
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 135)
        );
        summaryPanel.setPreferredSize(new Dimension(1000, 135));

        JPanel totalCard = createSummaryCard(
                "TOTAL SPENDING", "₹0.00", "All recorded expenses"
        );
        JPanel monthlyCard = createSummaryCard(
                "THIS MONTH", "₹0.00", "Current month spending"
        );
        JPanel transactionsCard = createSummaryCard(
                "TRANSACTIONS", "0", "Expenses recorded"
        );

        totalSpendingValue =
                (JLabel) totalCard.getClientProperty("value");
        monthlySpendingValue =
                (JLabel) monthlyCard.getClientProperty("value");
        transactionCountValue =
                (JLabel) transactionsCard.getClientProperty("value");
        totalDescription =
                (JLabel) totalCard.getClientProperty("description");

        summaryPanel.add(totalCard);
        summaryPanel.add(monthlyCard);
        summaryPanel.add(transactionsCard);

        // Search and category filter panel
        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 8)
        );
        filterPanel.setBackground(BACKGROUND);
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchField = new JTextField(18);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setToolTipText("Search expenses by title");

        String[] filterCategories = {
                "All Categories", "Food", "Travel", "Shopping",
                "Bills", "Education", "Entertainment", "Other"
        };

        categoryFilter = new JComboBox<>(filterCategories);
        categoryFilter.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton searchButton = new JButton("Search");
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> filterExpenses());

        JButton clearButton = new JButton("Clear");
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            refreshDashboard();
        });

        // Pressing Enter in the search box runs the search.
        searchField.addActionListener(e -> filterExpenses());

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(categoryFilter);
        filterPanel.add(searchButton);
        filterPanel.add(clearButton);

        // Recent activity panel
        JPanel activityPanel = new JPanel(new BorderLayout(0, 15));
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(22, 22, 22, 22)
                )
        );
        activityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel activityTitle = new JLabel("Recent activity");
        activityTitle.setFont(new Font("SansSerif", Font.BOLD, 19));
        activityTitle.setForeground(TEXT);

        String[] columns = {
                "ID", "Title", "Amount (₹)", "Category", "Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        expenseTable.setRowHeight(30);
        expenseTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        expenseTable.setForeground(TEXT);
        expenseTable.setBackground(Color.WHITE);
        expenseTable.setGridColor(BORDER);
        expenseTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        expenseTable.getTableHeader().setBackground(
                new Color(242, 245, 249)
        );
        expenseTable.setFillsViewportHeight(true);

        // Keep the database ID for deletion but hide its column.
        expenseTable.getColumnModel().getColumn(0).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(0).setMaxWidth(0);
        expenseTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane tableScrollPane = new JScrollPane(expenseTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.getViewport().setBackground(Color.WHITE);

        JButton deleteButton = new JButton("Delete Selected Expense");
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.addActionListener(e -> deleteSelectedExpense());

        JPanel activityBottom = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0)
        );
        activityBottom.setBackground(Color.WHITE);
        activityBottom.add(deleteButton);

        activityPanel.add(activityTitle, BorderLayout.NORTH);
        activityPanel.add(tableScrollPane, BorderLayout.CENTER);
        activityPanel.add(activityBottom, BorderLayout.SOUTH);
        activityPanel.setPreferredSize(new Dimension(1000, 300));
        activityPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 340)
        );

        content.add(welcome);
        content.add(Box.createVerticalStrut(8));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(28));
        content.add(summaryPanel);
        content.add(Box.createVerticalStrut(20));
        content.add(filterPanel);
        content.add(Box.createVerticalStrut(10));
        content.add(activityPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(header, BorderLayout.NORTH);
        root.add(scrollPane, BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setVisible(true);

        refreshDashboard();
    }

    private static JPanel createSummaryCard(
            String title, String value, String description
    ) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        BorderFactory.createEmptyBorder(18, 18, 18, 18)
                )
        );

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLabel.setForeground(MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 27));
        valueLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descriptionLabel.setForeground(MUTED);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(7));
        card.add(descriptionLabel);

        card.putClientProperty("value", valueLabel);
        card.putClientProperty("description", descriptionLabel);

        return card;
    }

    private static void refreshDashboard() {
        try {
            List<Expense> expenses = expenseDAO.getAllExpenses();

            double total = 0;
            double monthlyTotal = 0;
            YearMonth currentMonth = YearMonth.now();

            tableModel.setRowCount(0);

            for (Expense expense : expenses) {
                total += expense.getAmount();

                if (YearMonth.from(expense.getDate()).equals(currentMonth)) {
                    monthlyTotal += expense.getAmount();
                }

                tableModel.addRow(new Object[]{
                        expense.getId(),
                        expense.getTitle(),
                        String.format("%.2f", expense.getAmount()),
                        expense.getCategory(),
                        expense.getDate()
                });
            }

            totalSpendingValue.setText(String.format("₹%.2f", total));
            monthlySpendingValue.setText(
                    String.format("₹%.2f", monthlyTotal)
            );
            transactionCountValue.setText(
                    String.valueOf(expenses.size())
            );
            totalDescription.setText(
                    expenses.size() == 1
                            ? "1 expense recorded"
                            : expenses.size() + " expenses recorded"
            );

        } catch (SQLException e) {
            showError("Could not load expenses: " + e.getMessage());
        }
    }

    private static void filterExpenses() {
        String searchText = searchField.getText()
                .trim()
                .toLowerCase();

        String selectedCategory =
                (String) categoryFilter.getSelectedItem();

        try {
            List<Expense> expenses = expenseDAO.getAllExpenses();
            tableModel.setRowCount(0);

            for (Expense expense : expenses) {
                boolean matchesTitle = expense.getTitle()
                        .toLowerCase()
                        .contains(searchText);

                boolean matchesCategory =
                        "All Categories".equals(selectedCategory)
                        || expense.getCategory().equals(selectedCategory);

                if (matchesTitle && matchesCategory) {
                    tableModel.addRow(new Object[]{
                            expense.getId(),
                            expense.getTitle(),
                            String.format("%.2f", expense.getAmount()),
                            expense.getCategory(),
                            expense.getDate()
                    });
                }
            }
        } catch (SQLException e) {
            showError("Could not filter expenses: " + e.getMessage());
        }
    }

    private static void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please select an expense from the table first.",
                    "No Expense Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int modelRow = expenseTable.convertRowIndexToModel(selectedRow);
        int expenseId = (int) tableModel.getValueAt(modelRow, 0);
        String title = (String) tableModel.getValueAt(modelRow, 1);

        int confirmation = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to delete \"" + title + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            expenseDAO.deleteExpense(expenseId);
            refreshDashboard();

            JOptionPane.showMessageDialog(
                    frame,
                    "Expense deleted successfully.",
                    "SpendSphere",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {
            showError("Could not delete expense: " + e.getMessage());
        }
    }

    private static void showAddExpenseForm() {
        JTextField titleField = new JTextField(20);
        JTextField amountField = new JTextField(20);
        JTextField dateField = new JTextField(
                LocalDate.now().toString(), 20
        );

        String[] categories = {
                "Food", "Travel", "Shopping", "Bills",
                "Education", "Entertainment", "Other"
        };

        JComboBox<String> categoryBox = new JComboBox<>(categories);
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));

        form.add(new JLabel("Expense title:"));
        form.add(titleField);
        form.add(new JLabel("Amount (₹):"));
        form.add(amountField);
        form.add(new JLabel("Category:"));
        form.add(categoryBox);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);

        int result = JOptionPane.showConfirmDialog(
                frame,
                form,
                "Add New Expense",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String title = titleField.getText().trim();
        String amountText = amountField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        String dateText = dateField.getText().trim();

        if (title.isEmpty()) {
            showError("Please enter an expense title.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (!Double.isFinite(amount) || amount <= 0) {
                showError("Amount must be a positive number.");
                return;
            }

            LocalDate date = LocalDate.parse(dateText);
            Expense expense = new Expense(title, amount, category, date);

            expenseDAO.createTable();
            expenseDAO.addExpense(expense);
            refreshDashboard();

            JOptionPane.showMessageDialog(
                    frame,
                    "Expense saved successfully!",
                    "SpendSphere",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (NumberFormatException e) {
            showError("Please enter a valid numeric amount.");
        } catch (DateTimeParseException e) {
            showError(
                    "Enter the date as YYYY-MM-DD, "
                    + "for example 2026-10-23."
            );
        } catch (SQLException e) {
            showError("Could not save expense: " + e.getMessage());
        }
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(
                frame,
                message,
                "SpendSphere",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
