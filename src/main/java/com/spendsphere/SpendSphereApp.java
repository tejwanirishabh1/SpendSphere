
package com.spendsphere;

import com.database.ExpenseDAO;
import com.expense.Expense;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

public class SpendSphereApp {

    private static final Color NAVY = new Color(19, 30, 50);
    private static final Color NAVY_LIGHT = new Color(31, 45, 69);
    private static final Color ACCENT = new Color(20, 184, 166);
    private static final Color BG = new Color(246, 248, 251);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(35, 45, 62);
    private static final Color MUTED = new Color(119, 131, 150);
    private static final Color BORDER = new Color(228, 233, 240);
    private static final Color RED = new Color(220, 75, 85);
    private static final Color AMBER = new Color(217, 145, 20);
    private static final Color GREEN = new Color(16, 145, 100);

    private static final Font FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font BOLD = new Font("SansSerif", Font.BOLD, 13);

    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private static final String[] CATEGORIES = {
            "Food", "Travel", "Shopping", "Bills",
            "Education", "Entertainment", "Other"
    };

    private static final ExpenseDAO expenseDAO = new ExpenseDAO();
    private static final Preferences preferences =
            Preferences.userNodeForPackage(SpendSphereApp.class);

    private static JFrame frame;
    private static JPanel pageContainer;
    private static CardLayout pageLayout;

    private static final Map<String, JButton> navigationButtons =
            new LinkedHashMap<>();

    private static DefaultTableModel tableModel;
    private static JTable expenseTable;

    private static JLabel totalValue;
    private static JLabel monthlyValue;
    private static JLabel countValue;
    private static JLabel totalDescription;
    private static JLabel overviewMonthLabel;
    private static JLabel activityTitle;

    private static JLabel analyticsTotalValue;
    private static JLabel analyticsTransactionValue;
    private static JLabel analyticsCategoryValue;

    private static JLabel workspaceBudgetValue;
    private static JLabel workspaceSpentValue;
    private static JLabel workspaceRemainingValue;
    private static JProgressBar budgetProgress;

    // Smart category-budget components
    private static DefaultTableModel categoryBudgetTableModel;
    private static JTable categoryBudgetTable;
    private static JLabel categoryBudgetStatus;
    private static JPanel categoryBudgetPanel;

    private static JTextField searchField;
    private static JComboBox<String> categoryFilter;
    private static DatePicker fromDatePicker;
    private static DatePicker toDatePicker;

    private static JComboBox<String> monthSelector;
    private static JComboBox<Integer> yearSelector;

    private static CategoryChart overviewCategoryChart;
    private static MonthlyChart overviewMonthlyChart;
    private static CategoryChart analyticsCategoryChart;
    private static MonthlyChart analyticsMonthlyChart;

    private static YearMonth selectedMonth = YearMonth.now();
    private static boolean updatingSelectors;

    private static final String[] PAGES = {
            "Overview", "Transactions", "Analytics", "Workspace"
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
            }

            createUI();
        });
    }

    private static void createUI() {
        frame = new JFrame("SpendSphere | Personal Finance");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1380, 900);
        frame.setMinimumSize(new Dimension(1050, 720));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(createSidebar(), BorderLayout.WEST);

        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.setBackground(BG);
        rightSide.add(createTopBar(), BorderLayout.NORTH);

        pageLayout = new CardLayout();
        pageContainer = new JPanel(pageLayout);
        pageContainer.setBackground(BG);

        pageContainer.add(createOverviewPage(), "Overview");
        pageContainer.add(createTransactionsPage(), "Transactions");
        pageContainer.add(createAnalyticsPage(), "Analytics");
        pageContainer.add(createWorkspacePage(), "Workspace");

        JScrollPane pageScroll = new JScrollPane(pageContainer);
        pageScroll.setBorder(BorderFactory.createEmptyBorder());
        pageScroll.getViewport().setBackground(BG);
        pageScroll.getVerticalScrollBar().setUnitIncrement(18);

        rightSide.add(pageScroll, BorderLayout.CENTER);
        root.add(rightSide, BorderLayout.CENTER);
        frame.setContentPane(root);

        syncMonthSelectors();
        refreshDashboard();
        showPage("Overview");
        frame.setVisible(true);
    }

    // SIDEBAR AND NAVIGATION

    private static JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(NAVY);
        sidebar.setPreferredSize(new Dimension(225, 0));
        sidebar.setBorder(new EmptyBorder(28, 16, 22, 16));

        JLabel logo = new JLabel("SpendSphere");
        logo.setFont(new Font("SansSerif", Font.BOLD, 23));
        logo.setForeground(Color.WHITE);

        JLabel sub = new JLabel("PERSONAL FINANCE");
        sub.setFont(new Font("SansSerif", Font.BOLD, 10));
        sub.setForeground(new Color(155, 173, 197));

        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(sub);
        sidebar.add(Box.createVerticalStrut(38));
        sidebar.add(sidebarLabel("MAIN MENU"));
        sidebar.add(Box.createVerticalStrut(14));

        addNavigationButton(sidebar, "Overview", "▦   Overview");
        addNavigationButton(sidebar, "Transactions", "▤   Transactions");
        addNavigationButton(sidebar, "Analytics", "◷   Analytics");
        addNavigationButton(sidebar, "Workspace", "▣   Workspace");

        sidebar.add(Box.createVerticalGlue());

        JPanel tip = new JPanel();
        tip.setLayout(new BoxLayout(tip, BoxLayout.Y_AXIS));
        tip.setBackground(NAVY_LIGHT);
        tip.setBorder(new EmptyBorder(14, 12, 14, 12));
        tip.setAlignmentX(Component.LEFT_ALIGNMENT);
        tip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        tip.add(sidebarLabel("A LITTLE REMINDER"));
        tip.add(Box.createVerticalStrut(9));

        JLabel tipText = new JLabel(
                "<html>Understand your habits.<br>" +
                        "Make every rupee count.</html>"
        );
        tipText.setForeground(new Color(200, 210, 225));
        tipText.setFont(FONT);
        tip.add(tipText);

        sidebar.add(tip);
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(sidebarLabel("SpendSphere • Desktop"));
        return sidebar;
    }

    private static void addNavigationButton(
            JPanel sidebar, String page, String text) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setForeground(new Color(184, 197, 216));
        button.setBackground(NAVY);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(13, 12, 13, 8));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> showPage(page));

        navigationButtons.put(page, button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(6));
    }

    private static void showPage(String page) {
        pageLayout.show(pageContainer, page);

        for (Map.Entry<String, JButton> entry :
                navigationButtons.entrySet()) {
            boolean active = entry.getKey().equals(page);
            entry.getValue().setBackground(active ? NAVY_LIGHT : NAVY);
            entry.getValue().setForeground(
                    active ? Color.WHITE : new Color(184, 197, 216));
        }

        if (page.equals("Transactions")) {
            filterExpenses();
        }

        frame.setTitle("SpendSphere | " + page);
    }

    private static JLabel sidebarLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(160, 175, 195));
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        return label;
    }

    // TOP BAR

    private static JPanel createTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(CARD);
        top.setBorder(new EmptyBorder(17, 26, 17, 26));

        JLabel title = new JLabel("Personal Finance");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(TEXT);

        JButton addButton = button("+ Add Expense", true);
        addButton.addActionListener(e -> showAddExpenseForm());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel tag = new JLabel("YOUR MONEY, YOUR CONTROL");
        tag.setFont(new Font("SansSerif", Font.BOLD, 10));
        tag.setForeground(MUTED);

        right.add(tag);
        right.add(addButton);
        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    // PAGE HELPERS

    private static JPanel createPage(String heading, String subtitle) {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(26, 26, 30, 26));
        page.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel(heading);
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(subtitle);
        description.setFont(FONT);
        description.setForeground(MUTED);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(title);
        page.add(Box.createVerticalStrut(7));
        page.add(description);
        page.add(Box.createVerticalStrut(23));
        return page;
    }

    private static void addPageSection(
            JPanel page, String heading, String subtitle) {
        JLabel title = new JLabel(heading);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(subtitle);
        description.setFont(FONT);
        description.setForeground(MUTED);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(title);
        page.add(Box.createVerticalStrut(5));
        page.add(description);
        page.add(Box.createVerticalStrut(14));
    }

    private static JPanel createSummary() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 14, 0));
        panel.setBackground(BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));

        JPanel total = summaryCard(
                "TOTAL SPENDING", "₹0.00",
                "Across all recorded expenses");
        JPanel monthly = summaryCard(
                "MONTHLY SPENDING", "₹0.00",
                "For the selected month");
        JPanel count = summaryCard(
                "MONTHLY TRANSACTIONS", "0",
                "Expenses in the selected month");

        totalValue = (JLabel) total.getClientProperty("value");
        monthlyValue = (JLabel) monthly.getClientProperty("value");
        countValue = (JLabel) count.getClientProperty("value");
        totalDescription =
                (JLabel) total.getClientProperty("description");

        panel.add(total);
        panel.add(monthly);
        panel.add(count);
        return panel;
    }

    private static JPanel summaryCard(
            String heading, String value, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(16, 18, 16, 18)));

        JLabel h = new JLabel(heading);
        h.setFont(new Font("SansSerif", Font.BOLD, 10));
        h.setForeground(MUTED);

        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 25));
        v.setForeground(TEXT);

        JLabel d = new JLabel(description);
        d.setFont(new Font("SansSerif", Font.PLAIN, 11));
        d.setForeground(MUTED);

        card.add(h);
        card.add(Box.createVerticalStrut(10));
        card.add(v);
        card.add(Box.createVerticalStrut(5));
        card.add(d);

        card.putClientProperty("value", v);
        card.putClientProperty("description", d);
        return card;
    }

    // OVERVIEW PAGE

    private static JPanel createOverviewPage() {
        JPanel page = createPage(
                "Welcome to your finances.",
                "Your financial snapshot, all in one place.");

        overviewMonthLabel = new JLabel();
        overviewMonthLabel.setFont(BOLD);
        overviewMonthLabel.setForeground(MUTED);
        overviewMonthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(overviewMonthLabel);
        page.add(Box.createVerticalStrut(12));
        page.add(createSummary());
        page.add(Box.createVerticalStrut(26));

        addPageSection(page, "Spending overview",
                "Your category breakdown and recent monthly spending.");

        JPanel charts = new JPanel(new GridLayout(1, 2, 16, 0));
        charts.setBackground(BG);
        charts.setAlignmentX(Component.LEFT_ALIGNMENT);
        charts.setPreferredSize(new Dimension(900, 290));
        charts.setMaximumSize(new Dimension(Integer.MAX_VALUE, 290));

        JPanel categoryPanel = chartCard(
                "Spending by category", "All recorded expenses");
        JPanel monthlyPanel = chartCard(
                "Monthly spending trend",
                "Six months ending in the selected month");

        overviewCategoryChart = new CategoryChart();
        overviewMonthlyChart = new MonthlyChart();

        categoryPanel.add(overviewCategoryChart, BorderLayout.CENTER);
        monthlyPanel.add(overviewMonthlyChart, BorderLayout.CENTER);
        charts.add(categoryPanel);
        charts.add(monthlyPanel);

        page.add(charts);
        page.add(Box.createVerticalStrut(22));

        JButton transactionsButton = button(
                "View all transactions →", false);
        transactionsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        transactionsButton.addActionListener(
                e -> showPage("Transactions"));
        page.add(transactionsButton);

        return page;
    }

    // TRANSACTIONS PAGE

    private static JPanel createTransactionsPage() {
        JPanel page = createPage(
                "Transactions",
                "Add, search, filter and manage your recorded expenses.");

        page.add(createMonthPanel());
        page.add(Box.createVerticalStrut(16));

        addPageSection(page, "Find a transaction",
                "Use the filters below. Date filters can span multiple months.");

        page.add(createFilterPanel());
        page.add(Box.createVerticalStrut(18));

        addPageSection(page, "Your expense records",
                "Select a row and use Delete selected to remove an expense.");

        page.add(createActivityPanel());
        return page;
    }

    // ANALYTICS PAGE

    private static JPanel createAnalyticsPage() {
        JPanel page = createPage(
                "Spending Analytics",
                "Explore your spending patterns and compare monthly activity.");

        JPanel metrics = new JPanel(new GridLayout(1, 3, 14, 0));
        metrics.setBackground(BG);
        metrics.setAlignmentX(Component.LEFT_ALIGNMENT);
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel total = summaryCard(
                "ALL-TIME SPENDING", "₹0.00",
                "Total recorded expenses");
        JPanel transactions = summaryCard(
                "TRANSACTIONS", "0", "All recorded expenses");
        JPanel categories = summaryCard(
                "CATEGORIES USED", "0",
                "Distinct expense categories");

        analyticsTotalValue =
                (JLabel) total.getClientProperty("value");
        analyticsTransactionValue =
                (JLabel) transactions.getClientProperty("value");
        analyticsCategoryValue =
                (JLabel) categories.getClientProperty("value");

        metrics.add(total);
        metrics.add(transactions);
        metrics.add(categories);
        page.add(metrics);
        page.add(Box.createVerticalStrut(28));

        addPageSection(page, "Category-wise spending",
                "See how your expenses are distributed across categories.");

        JPanel categoryPanel = chartCard(
                "Spending distribution",
                "Based on all recorded expenses");
        analyticsCategoryChart = new CategoryChart();
        categoryPanel.add(analyticsCategoryChart, BorderLayout.CENTER);
        categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryPanel.setPreferredSize(new Dimension(900, 330));
        categoryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));

        page.add(categoryPanel);
        page.add(Box.createVerticalStrut(25));

        addPageSection(page, "Monthly spending trends",
                "Compare the six months ending in your selected dashboard month.");

        JPanel monthlyPanel = chartCard(
                "Monthly comparison",
                "Amounts are calculated from your saved transactions");
        analyticsMonthlyChart = new MonthlyChart();
        monthlyPanel.add(analyticsMonthlyChart, BorderLayout.CENTER);
        monthlyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        monthlyPanel.setPreferredSize(new Dimension(900, 330));
        monthlyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));

        page.add(monthlyPanel);
        page.add(Box.createVerticalStrut(20));

        JButton viewTransactions = button(
                "Explore transactions →", false);
        viewTransactions.setAlignmentX(Component.LEFT_ALIGNMENT);
        viewTransactions.addActionListener(
                e -> showPage("Transactions"));
        page.add(viewTransactions);

        return page;
    }

    // WORKSPACE PAGE AND OVERALL MONTHLY BUDGET

    private static JPanel createWorkspacePage() {
        JPanel page = createPage(
                "Workspace",
                "Plan your spending with monthly and category budgets.");

        JPanel budgetCard = new JPanel();
        budgetCard.setLayout(new BoxLayout(budgetCard, BoxLayout.Y_AXIS));
        budgetCard.setBackground(CARD);
        budgetCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(22, 22, 22, 22)));
        budgetCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        budgetCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));

        JLabel budgetHeading = new JLabel("Monthly budget planner");
        budgetHeading.setFont(new Font("SansSerif", Font.BOLD, 20));
        budgetHeading.setForeground(TEXT);

        JLabel budgetSubtitle = new JLabel(
                "Your overall budget for the selected month.");
        budgetSubtitle.setFont(FONT);
        budgetSubtitle.setForeground(MUTED);

        JPanel values = new JPanel(new GridLayout(1, 3, 12, 0));
        values.setOpaque(false);

        workspaceBudgetValue = new JLabel("₹0.00");
        workspaceSpentValue = new JLabel("₹0.00");
        workspaceRemainingValue = new JLabel("₹0.00");

        values.add(smallMetric("MONTHLY BUDGET", workspaceBudgetValue));
        values.add(smallMetric("SPENT THIS MONTH", workspaceSpentValue));
        values.add(smallMetric("BUDGET REMAINING", workspaceRemainingValue));

        budgetProgress = new JProgressBar(0, 100);
        budgetProgress.setStringPainted(true);
        budgetProgress.setForeground(ACCENT);
        budgetProgress.setBackground(new Color(232, 238, 243));
        budgetProgress.setPreferredSize(new Dimension(400, 22));
        budgetProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JButton setBudgetButton = button("Set monthly budget", true);
        setBudgetButton.addActionListener(e -> setMonthlyBudget());

        budgetCard.add(budgetHeading);
        budgetCard.add(Box.createVerticalStrut(7));
        budgetCard.add(budgetSubtitle);
        budgetCard.add(Box.createVerticalStrut(24));
        budgetCard.add(values);
        budgetCard.add(Box.createVerticalStrut(20));
        budgetCard.add(budgetProgress);
        budgetCard.add(Box.createVerticalStrut(17));
        budgetCard.add(setBudgetButton);

        page.add(budgetCard);
        page.add(Box.createVerticalStrut(25));

        // CATEGORY SMART BUDGETING
        addPageSection(page, "Smart category budgets",
                "Set a separate limit for each category. Spending updates automatically.");

        categoryBudgetPanel = new JPanel(new BorderLayout(0, 12));
        categoryBudgetPanel.setBackground(CARD);
        categoryBudgetPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(17, 17, 17, 17)));
        categoryBudgetPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        categoryBudgetPanel.setPreferredSize(new Dimension(900, 360));
        categoryBudgetPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 390));

        JLabel categoryHeading = new JLabel("Category budget tracker");
        categoryHeading.setFont(new Font("SansSerif", Font.BOLD, 17));
        categoryHeading.setForeground(TEXT);

        String[] columns = {
                "Category", "Budget (₹)", "Spent (₹)",
                "Remaining (₹)", "Used", "Status"
        };

        categoryBudgetTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryBudgetTable = new JTable(categoryBudgetTableModel);
        categoryBudgetTable.setRowHeight(39);
        categoryBudgetTable.setFont(FONT);
        categoryBudgetTable.setForeground(TEXT);
        categoryBudgetTable.setGridColor(BORDER);
        categoryBudgetTable.setShowVerticalLines(false);
        categoryBudgetTable.setFillsViewportHeight(true);
        categoryBudgetTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        categoryBudgetTable.getTableHeader().setFont(BOLD);
        categoryBudgetTable.getTableHeader().setBackground(
                new Color(243, 246, 250));
        categoryBudgetTable.getTableHeader().setForeground(TEXT);
        categoryBudgetTable.getTableHeader().setPreferredSize(
                new Dimension(0, 36));

        DefaultTableCellRenderer rightRenderer =
                new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        for (int col : new int[]{1, 2, 3, 4}) {
            categoryBudgetTable.getColumnModel().getColumn(col)
                    .setCellRenderer(rightRenderer);
        }

        categoryBudgetTable.getColumnModel().getColumn(5)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean selected,
                            boolean focus, int row, int column) {

                        super.getTableCellRendererComponent(
                                table, value, selected, focus, row, column);

                        setHorizontalAlignment(SwingConstants.CENTER);

                        if (!selected) {
                            String status = value == null
                                    ? "" : value.toString();

                            if (status.equals("OVER BUDGET")) {
                                setForeground(RED);
                                setFont(new Font(
                                        "SansSerif", Font.BOLD, 11));
                            } else if (status.equals("Near limit")) {
                                setForeground(AMBER);
                                setFont(new Font(
                                        "SansSerif", Font.BOLD, 11));
                            } else if (status.equals("On track")) {
                                setForeground(GREEN);
                                setFont(new Font(
                                        "SansSerif", Font.BOLD, 11));
                            } else {
                                setForeground(MUTED);
                                setFont(FONT);
                            }
                        }

                        return this;
                    }
                });

        JScrollPane categoryScroll = new JScrollPane(categoryBudgetTable);
        categoryScroll.setBorder(new LineBorder(BORDER));

        categoryBudgetStatus = new JLabel("Set category budgets to get started.");
        categoryBudgetStatus.setFont(BOLD);
        categoryBudgetStatus.setForeground(MUTED);

        JButton setCategoryButton = button("Set / edit category budget", true);
        setCategoryButton.addActionListener(e -> setCategoryBudget());

        JButton removeCategoryButton = button("Remove selected budget", false);
        removeCategoryButton.addActionListener(e -> removeCategoryBudget());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(setCategoryButton);
        buttons.add(removeCategoryButton);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);
        bottom.add(categoryBudgetStatus);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(buttons);

        categoryBudgetPanel.add(categoryHeading, BorderLayout.NORTH);
        categoryBudgetPanel.add(categoryScroll, BorderLayout.CENTER);
        categoryBudgetPanel.add(bottom, BorderLayout.SOUTH);

        page.add(categoryBudgetPanel);
        page.add(Box.createVerticalStrut(25));

        addPageSection(page, "Quick actions",
                "Jump straight to the tools you use most.");

        JPanel actions = new JPanel(new GridLayout(1, 3, 14, 0));
        actions.setBackground(BG);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

        actions.add(actionCard("Add an expense",
                "Record a new purchase or payment.", "＋",
                () -> showAddExpenseForm()));

        actions.add(actionCard("Manage transactions",
                "Search, filter and delete expenses.", "≡",
                () -> showPage("Transactions")));

        actions.add(actionCard("Explore analytics",
                "Review your category and monthly trends.", "↗",
                () -> showPage("Analytics")));

        page.add(actions);
        page.add(Box.createVerticalStrut(24));

        JLabel note = new JLabel(
                "<html><b>Budget tip:</b> Review category spending regularly. " +
                "A warning appears when a category reaches 80% of its limit.</html>");
        note.setFont(FONT);
        note.setForeground(MUTED);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        page.add(note);

        return page;
    }

    private static JPanel smallMetric(String heading, JLabel value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel label = new JLabel(heading);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(MUTED);

        value.setFont(new Font("SansSerif", Font.BOLD, 20));
        value.setForeground(TEXT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(value);
        return panel;
    }

    private static JPanel actionCard(
            String heading, String description, String symbol,
            Runnable action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(16, 16, 16, 16)));

        JLabel icon = new JLabel(symbol);
        icon.setFont(new Font("SansSerif", Font.BOLD, 24));
        icon.setForeground(ACCENT);

        JLabel title = new JLabel(heading);
        title.setFont(BOLD);
        title.setForeground(TEXT);

        JLabel text = new JLabel("<html>" + description + "</html>");
        text.setFont(new Font("SansSerif", Font.PLAIN, 11));
        text.setForeground(MUTED);

        card.add(icon);
        card.add(Box.createVerticalStrut(7));
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(text);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setToolTipText("Click to open");
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });
        return card;
    }

    private static void setMonthlyBudget() {
        String existing = preferences.get(budgetPreferenceKey(), "");

        String answer = JOptionPane.showInputDialog(
                frame,
                "Enter your overall budget for " +
                        selectedMonth.format(
                                DateTimeFormatter.ofPattern("MMMM yyyy"))
                        + " (₹):",
                existing);

        if (answer == null) {
            return;
        }

        try {
            double amount = Double.parseDouble(answer.trim());

            if (!Double.isFinite(amount) || amount <= 0) {
                showError("Budget must be a positive amount.");
                return;
            }

            preferences.put(budgetPreferenceKey(), Double.toString(amount));
            refreshDashboard();

            JOptionPane.showMessageDialog(
                    frame, "Monthly budget saved.",
                    "SpendSphere", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            showError("Enter a valid numeric budget.");
        }
    }

    private static String budgetPreferenceKey() {
        return "monthlyBudget_" + selectedMonth;
    }

    // SMART CATEGORY BUDGETING

    private static void setCategoryBudget() {
        JComboBox<String> categoryBox = new JComboBox<>(CATEGORIES);
        JTextField amountField = new JTextField(15);
        amountField.setFont(FONT);

        JLabel existingLabel = new JLabel();
        existingLabel.setFont(FONT);
        existingLabel.setForeground(MUTED);

        Runnable loadSelectedBudget = () -> {
            String category = (String) categoryBox.getSelectedItem();
            if (category == null) {
                return;
            }

            double existing = CategoryBudgetManager.getBudget(
                    selectedMonth.toString(), category);

            amountField.setText(existing > 0
                    ? String.format("%.2f", existing) : "");

            existingLabel.setText(existing > 0
                    ? "Current limit: " + formatCurrency(existing)
                    : "No budget has been set for this category.");
        };

        categoryBox.addActionListener(e -> loadSelectedBudget.run());
        loadSelectedBudget.run();

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(form, gbc, 0, "Category", categoryBox);
        addFormRow(form, gbc, 1, "Monthly limit (₹)", amountField);
        addFormRow(form, gbc, 2, "Current budget", existingLabel);

        int result = JOptionPane.showConfirmDialog(
                frame, form,
                "Set category budget — " +
                        selectedMonth.format(
                                DateTimeFormatter.ofPattern("MMMM yyyy")),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String category = (String) categoryBox.getSelectedItem();

        try {
            double amount = Double.parseDouble(amountField.getText().trim());

            if (!Double.isFinite(amount) || amount <= 0) {
                showError("Category budget must be a positive amount.");
                return;
            }

            CategoryBudgetManager.setBudget(
                    selectedMonth.toString(), category, amount);

            refreshDashboard();

            JOptionPane.showMessageDialog(
                    frame,
                    "Budget saved for " + category + ".",
                    "Smart Budgeting",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            showError("Enter a valid numeric budget.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private static void removeCategoryBudget() {
        int selected = categoryBudgetTable.getSelectedRow();

        if (selected < 0) {
            showError("Select a category in the table first.");
            return;
        }

        int modelRow = categoryBudgetTable.convertRowIndexToModel(selected);
        String category = (String) categoryBudgetTableModel.getValueAt(
                modelRow, 0);

        int answer = JOptionPane.showConfirmDialog(
                frame,
                "Remove the budget for " + category + " in " +
                        selectedMonth.format(
                                DateTimeFormatter.ofPattern("MMMM yyyy"))
                        + "?",
                "Remove category budget",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        CategoryBudgetManager.removeBudget(
                selectedMonth.toString(), category);

        refreshDashboard();
    }

    private static void refreshCategoryBudgets(List<Expense> expenses) {
        if (categoryBudgetTableModel == null) {
            return;
        }

        categoryBudgetTableModel.setRowCount(0);

        Map<String, Double> spentByCategory = new LinkedHashMap<>();
        for (String category : CATEGORIES) {
            spentByCategory.put(category, 0.0);
        }

        for (Expense expense : expenses) {
            if (YearMonth.from(expense.getDate()).equals(selectedMonth)) {
                spentByCategory.merge(
                        expense.getCategory(),
                        expense.getAmount(),
                        Double::sum);
            }
        }

        int budgetedCount = 0;
        int warningCount = 0;
        int overBudgetCount = 0;

        for (String category : CATEGORIES) {
            double budget = CategoryBudgetManager.getBudget(
                    selectedMonth.toString(), category);
            double spent = spentByCategory.getOrDefault(category, 0.0);
            double remaining = budget - spent;

            String usedText;
            String status;

            if (budget <= 0) {
                usedText = "—";
                status = "No limit set";
            } else {
                budgetedCount++;

                double percentage = spent / budget * 100;
                usedText = String.format("%.0f%%", percentage);

                if (spent >= budget) {
                    status = "OVER BUDGET";
                    overBudgetCount++;
                } else if (percentage >= 80) {
                    status = "Near limit";
                    warningCount++;
                } else {
                    status = "On track";
                }
            }

            categoryBudgetTableModel.addRow(new Object[]{
                    category,
                    budget > 0 ? formatCurrency(budget) : "—",
                    formatCurrency(spent),
                    budget > 0 ? formatCurrency(remaining) : "—",
                    usedText,
                    status
            });
        }

        String monthText = selectedMonth.format(
                DateTimeFormatter.ofPattern("MMMM yyyy"));

        if (budgetedCount == 0) {
            categoryBudgetStatus.setText(
                    monthText + ": No category budgets set yet.");
            categoryBudgetStatus.setForeground(MUTED);
        } else if (overBudgetCount > 0) {
            categoryBudgetStatus.setText(
                    monthText + ": " + overBudgetCount +
                            " category budget(s) exceeded.");
            categoryBudgetStatus.setForeground(RED);
        } else if (warningCount > 0) {
            categoryBudgetStatus.setText(
                    monthText + ": " + warningCount +
                            " category budget(s) at or above 80%.");
            categoryBudgetStatus.setForeground(AMBER);
        } else {
            categoryBudgetStatus.setText(
                    monthText + ": All budgeted categories are on track.");
            categoryBudgetStatus.setForeground(GREEN);
        }

        categoryBudgetTable.repaint();
    }

    private static void refreshWorkspace(
            double monthTotal, List<Expense> expenses) {
        if (workspaceBudgetValue == null) {
            return;
        }

        double budget = Double.parseDouble(
                preferences.get(budgetPreferenceKey(), "0"));

        workspaceBudgetValue.setText(formatCurrency(budget));
        workspaceSpentValue.setText(formatCurrency(monthTotal));
        workspaceRemainingValue.setText(
                formatCurrency(budget - monthTotal));

        if (budget <= 0) {
            budgetProgress.setValue(0);
            budgetProgress.setString("Set a budget to track progress");
            workspaceRemainingValue.setForeground(TEXT);
            budgetProgress.setForeground(ACCENT);
        } else {
            double percentage = monthTotal / budget * 100;
            int progress = (int) Math.min(
                    100, Math.round(percentage));

            budgetProgress.setValue(progress);
            budgetProgress.setString(
                    String.format("%.0f%% of budget used", percentage));

            if (percentage >= 100) {
                budgetProgress.setForeground(RED);
                workspaceRemainingValue.setForeground(RED);
            } else if (percentage >= 80) {
                budgetProgress.setForeground(AMBER);
                workspaceRemainingValue.setForeground(AMBER);
            } else {
                budgetProgress.setForeground(ACCENT);
                workspaceRemainingValue.setForeground(GREEN);
            }
        }

        refreshCategoryBudgets(expenses);
    }

    // CHART PANELS

    private static JPanel chartCard(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(15, 15, 15, 15)));

        JPanel headings = new JPanel();
        headings.setLayout(new BoxLayout(headings, BoxLayout.Y_AXIS));
        headings.setOpaque(false);

        JLabel h = new JLabel(title);
        h.setFont(new Font("SansSerif", Font.BOLD, 15));
        h.setForeground(TEXT);

        JLabel s = new JLabel(subtitle);
        s.setFont(new Font("SansSerif", Font.PLAIN, 10));
        s.setForeground(MUTED);

        headings.add(h);
        headings.add(Box.createVerticalStrut(5));
        headings.add(s);
        panel.add(headings, BorderLayout.NORTH);
        return panel;
    }

    private static class CategoryChart extends JPanel {
        private Map<String, Double> values = new LinkedHashMap<>();

        private final Color[] colors = {
                new Color(20, 184, 166),
                new Color(79, 112, 210),
                new Color(245, 158, 11),
                new Color(168, 85, 247),
                new Color(239, 91, 110),
                new Color(59, 130, 246),
                new Color(16, 145, 100),
                new Color(139, 151, 169)
        };

        CategoryChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 220));
        }

        void setValues(Map<String, Double> data) {
            values = new LinkedHashMap<>(data);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            double total = values.values().stream()
                    .mapToDouble(Double::doubleValue).sum();

            if (total <= 0) {
                g2.setColor(MUTED);
                g2.setFont(FONT);
                drawCentered(g2, "No spending data yet",
                        getWidth() / 2, getHeight() / 2);
                g2.dispose();
                return;
            }

            int diameter = Math.min(150, Math.max(95, getHeight() - 40));
            int x = 8;
            int y = Math.max(5, (getHeight() - diameter) / 2);
            int start = 90;
            int index = 0;

            for (double amount : values.values()) {
                int angle = (int) Math.round(amount / total * 360);
                g2.setColor(colors[index % colors.length]);
                g2.fillArc(x, y, diameter, diameter, start, -angle);
                start -= angle;
                index++;
            }

            int hole = diameter / 2;
            int holeX = x + (diameter - hole) / 2;
            int holeY = y + (diameter - hole) / 2;

            g2.setColor(CARD);
            g2.fillOval(holeX, holeY, hole, hole);
            g2.setColor(TEXT);
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            drawCentered(g2, "TOTAL",
                    holeX + hole / 2, holeY + hole / 2 - 5);
            drawCentered(g2, compactCurrency(total),
                    holeX + hole / 2, holeY + hole / 2 + 10);

            int legendX = diameter + 28;
            int legendY = Math.max(
                    8, (getHeight() - values.size() * 25) / 2);
            index = 0;

            for (Map.Entry<String, Double> entry : values.entrySet()) {
                if (legendY > getHeight() - 14) {
                    break;
                }

                g2.setColor(colors[index % colors.length]);
                g2.fillRoundRect(legendX, legendY, 10, 10, 4, 4);
                g2.setColor(TEXT);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 10));

                String name = entry.getKey();
                if (name.length() > 13) {
                    name = name.substring(0, 12) + "…";
                }

                g2.drawString(name, legendX + 16, legendY + 9);
                g2.setColor(MUTED);
                g2.drawString(compactCurrency(entry.getValue()),
                        legendX + 16, legendY + 20);

                legendY += 32;
                index++;
            }

            g2.dispose();
        }
    }

    private static class MonthlyChart extends JPanel {
        private List<YearMonth> months = new ArrayList<>();
        private Map<YearMonth, Double> values = new LinkedHashMap<>();

        MonthlyChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(360, 220));
        }

        void setValues(
                List<YearMonth> monthList,
                Map<YearMonth, Double> monthlyValues) {
            months = new ArrayList<>(monthList);
            values = new LinkedHashMap<>(monthlyValues);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (months.isEmpty()) {
                g2.setColor(MUTED);
                drawCentered(g2, "No monthly data",
                        getWidth() / 2, getHeight() / 2);
                g2.dispose();
                return;
            }

            int left = 42;
            int right = 10;
            int top = 20;
            int bottom = 36;
            int chartWidth = getWidth() - left - right;
            int chartHeight = getHeight() - top - bottom;

            if (chartWidth <= 0 || chartHeight <= 0) {
                g2.dispose();
                return;
            }

            double max = values.values().stream()
                    .mapToDouble(Double::doubleValue).max().orElse(0);

            if (max <= 0) {
                max = 1;
            }

            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));

            for (int i = 0; i <= 4; i++) {
                int yy = top + chartHeight - i * chartHeight / 4;
                g2.setColor(new Color(234, 238, 244));
                g2.drawLine(left, yy, left + chartWidth, yy);
                g2.setColor(MUTED);
                g2.drawString(compactCurrency(max * i / 4), 0, yy + 4);
            }

            int slot = chartWidth / months.size();
            int barWidth = Math.max(8, Math.min(35, slot - 12));

            for (int i = 0; i < months.size(); i++) {
                YearMonth month = months.get(i);
                double amount = values.getOrDefault(month, 0.0);

                int barHeight = (int) Math.round(
                        amount / max * (chartHeight - 3));
                int x = left + i * slot + (slot - barWidth) / 2;
                int y = top + chartHeight - barHeight;

                g2.setColor(ACCENT);
                g2.fillRoundRect(x, y, barWidth,
                        Math.max(0, barHeight), 8, 8);

                g2.setColor(MUTED);
                String label = month.format(
                        DateTimeFormatter.ofPattern("MMM"));
                FontMetrics fm = g2.getFontMetrics();

                g2.drawString(label,
                        x + (barWidth - fm.stringWidth(label)) / 2,
                        top + chartHeight + 17);
            }

            g2.dispose();
        }
    }

    private static void drawCentered(
            Graphics2D g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x - fm.stringWidth(text) / 2, y);
    }

    private static String compactCurrency(double amount) {
        if (amount >= 10000000) {
            return String.format("₹%.1fCr", amount / 10000000);
        }
        if (amount >= 100000) {
            return String.format("₹%.1fL", amount / 100000);
        }
        if (amount >= 1000) {
            return String.format("₹%.1fk", amount / 1000);
        }
        return String.format("₹%.0f", amount);
    }

    // MONTH SELECTOR

    private static JPanel createMonthPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(13, 15, 13, 15)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JPanel labels = new JPanel();
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labels.setOpaque(false);

        JLabel heading = new JLabel("Dashboard month");
        heading.setFont(BOLD);
        heading.setForeground(TEXT);

        JLabel currentMonth = new JLabel();
        currentMonth.setFont(FONT);
        currentMonth.setForeground(MUTED);
        currentMonth.setText(selectedMonth.toString());

        labels.add(heading);
        labels.add(Box.createVerticalStrut(4));
        labels.add(currentMonth);

        JPanel controls = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 6, 0));
        controls.setOpaque(false);

        JButton prev = button("‹", false);
        JButton next = button("›", false);
        prev.setToolTipText("Previous month");
        next.setToolTipText("Next month");

        monthSelector = new JComboBox<>(MONTHS);
        monthSelector.setFont(FONT);

        yearSelector = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();

        for (int y = currentYear - 20; y <= currentYear + 5; y++) {
            yearSelector.addItem(y);
        }

        prev.addActionListener(e -> {
            selectedMonth = selectedMonth.minusMonths(1);
            syncMonthSelectors();
            currentMonth.setText(selectedMonth.toString());
            refreshDashboard();
        });

        next.addActionListener(e -> {
            selectedMonth = selectedMonth.plusMonths(1);
            syncMonthSelectors();
            currentMonth.setText(selectedMonth.toString());
            refreshDashboard();
        });

        monthSelector.addActionListener(e -> {
            if (!updatingSelectors
                    && monthSelector.getSelectedIndex() >= 0
                    && yearSelector.getSelectedItem() != null) {
                selectedMonth = YearMonth.of(
                        (Integer) yearSelector.getSelectedItem(),
                        monthSelector.getSelectedIndex() + 1);
                currentMonth.setText(selectedMonth.toString());
                refreshDashboard();
            }
        });

        yearSelector.addActionListener(e -> {
            if (!updatingSelectors
                    && yearSelector.getSelectedItem() != null
                    && monthSelector.getSelectedIndex() >= 0) {
                selectedMonth = YearMonth.of(
                        (Integer) yearSelector.getSelectedItem(),
                        monthSelector.getSelectedIndex() + 1);
                currentMonth.setText(selectedMonth.toString());
                refreshDashboard();
            }
        });

        controls.add(prev);
        controls.add(monthSelector);
        controls.add(yearSelector);
        controls.add(next);

        panel.add(labels, BorderLayout.WEST);
        panel.add(controls, BorderLayout.EAST);
        return panel;
    }

    private static void syncMonthSelectors() {
        if (monthSelector == null || yearSelector == null) {
            return;
        }

        updatingSelectors = true;
        try {
            monthSelector.setSelectedIndex(
                    selectedMonth.getMonthValue() - 1);

            boolean found = false;
            for (int i = 0; i < yearSelector.getItemCount(); i++) {
                if (yearSelector.getItemAt(i).intValue()
                        == selectedMonth.getYear()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                yearSelector.addItem(selectedMonth.getYear());
            }

            yearSelector.setSelectedItem(selectedMonth.getYear());

            if (overviewMonthLabel != null) {
                overviewMonthLabel.setText(
                        "Currently viewing: " +
                                selectedMonth.format(
                                        DateTimeFormatter.ofPattern(
                                                "MMMM yyyy")));
            }
        } finally {
            updatingSelectors = false;
        }
    }

    // FILTERS

    private static JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(14, 14, 14, 14)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchField = new JTextField();
        styleInput(searchField);
        searchField.setToolTipText("Search by expense title");

        categoryFilter = new JComboBox<>(new String[]{
                "All Categories", "Food", "Travel", "Shopping",
                "Bills", "Education", "Entertainment", "Other"
        });

        fromDatePicker = createDatePicker("Choose starting date");
        toDatePicker = createDatePicker("Choose ending date");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        addFilterField(panel, gbc, 0, 0, "Search", searchField, 1);
        addFilterField(panel, gbc, 2, 0, "Category", categoryFilter, 1);
        addFilterField(panel, gbc, 0, 1, "From date", fromDatePicker, 1);
        addFilterField(panel, gbc, 2, 1, "To date", toDatePicker, 1);

        JButton apply = button("Apply filters", true);
        JButton clear = button("Clear filters", false);

        apply.addActionListener(e -> filterExpenses());
        searchField.addActionListener(e -> filterExpenses());

        clear.addActionListener(e -> {
            searchField.setText("");
            categoryFilter.setSelectedIndex(0);
            fromDatePicker.setDate(null);
            toDatePicker.setDate(null);
            filterExpenses();
        });

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(clear);
        actions.add(apply);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.weightx = 1;
        panel.add(actions, gbc);
        return panel;
    }

    private static void addFilterField(
            JPanel panel, GridBagConstraints gbc,
            int x, int y, String title, JComponent component,
            double weight) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(MUTED);

        gbc.gridy = y;
        gbc.gridx = x;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = x + 1;
        gbc.weightx = weight;
        panel.add(component, gbc);
    }

    private static DatePicker createDatePicker(String tooltip) {
        DatePicker picker = new DatePicker();
        picker.setDate(null);
        picker.getComponentDateTextField().setToolTipText(tooltip);
        picker.getComponentDateTextField().setColumns(9);
        picker.setPreferredSize(new Dimension(185, 34));
        return picker;
    }

    // TRANSACTION TABLE

    private static JPanel createActivityPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(17, 17, 15, 17)));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(900, 430));
        panel.setMinimumSize(new Dimension(600, 330));

        activityTitle = new JLabel("Transactions");
        activityTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
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
        expenseTable.setRowHeight(35);
        expenseTable.setFont(FONT);
        expenseTable.setForeground(TEXT);
        expenseTable.setShowVerticalLines(false);
        expenseTable.setGridColor(BORDER);
        expenseTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        expenseTable.setSelectionBackground(new Color(224, 247, 244));
        expenseTable.setSelectionForeground(TEXT);
        expenseTable.setFillsViewportHeight(true);
        expenseTable.setAutoCreateRowSorter(true);

        expenseTable.getTableHeader().setFont(BOLD);
        expenseTable.getTableHeader().setBackground(
                new Color(243, 246, 250));
        expenseTable.getTableHeader().setForeground(TEXT);
        expenseTable.getTableHeader().setPreferredSize(
                new Dimension(0, 36));

        expenseTable.getColumnModel().getColumn(0).setMinWidth(0);
        expenseTable.getColumnModel().getColumn(0).setMaxWidth(0);
        expenseTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        DefaultTableCellRenderer amountRenderer =
                new DefaultTableCellRenderer();
        amountRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        expenseTable.getColumnModel().getColumn(2)
                .setCellRenderer(amountRenderer);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy");

        expenseTable.getColumnModel().getColumn(4)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean selected,
                            boolean focus, int row, int column) {
                        super.getTableCellRendererComponent(
                                table, value, selected, focus, row, column);

                        if (value instanceof LocalDate) {
                            setText(((LocalDate) value).format(formatter));
                        }
                        return this;
                    }
                });

        JScrollPane scroll = new JScrollPane(expenseTable);
        scroll.setBorder(new LineBorder(BORDER));

        JButton delete = button("Delete selected", false);
        delete.setForeground(RED);
        delete.addActionListener(e -> deleteSelectedExpense());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        JLabel note = new JLabel("Select a transaction to delete it.");
        note.setForeground(MUTED);
        note.setFont(FONT);

        bottom.add(note, BorderLayout.WEST);
        bottom.add(delete, BorderLayout.EAST);

        panel.add(activityTitle, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // REFRESH DASHBOARD AND ANALYTICS

    private static void refreshDashboard() {
        try {
            List<Expense> expenses = expenseDAO.getAllExpenses();

            double total = 0;
            double monthTotal = 0;
            int monthCount = 0;

            Map<String, Double> categoryTotals = new LinkedHashMap<>();
            Map<YearMonth, Double> monthTotals = new LinkedHashMap<>();
            List<YearMonth> recentMonths = new ArrayList<>();

            YearMonth firstMonth = selectedMonth.minusMonths(5);

            for (int i = 0; i < 6; i++) {
                YearMonth month = firstMonth.plusMonths(i);
                recentMonths.add(month);
                monthTotals.put(month, 0.0);
            }

            for (Expense expense : expenses) {
                total += expense.getAmount();

                YearMonth expenseMonth = YearMonth.from(expense.getDate());

                if (expenseMonth.equals(selectedMonth)) {
                    monthTotal += expense.getAmount();
                    monthCount++;
                }

                categoryTotals.merge(
                        expense.getCategory(),
                        expense.getAmount(),
                        Double::sum);

                if (monthTotals.containsKey(expenseMonth)) {
                    monthTotals.merge(
                            expenseMonth,
                            expense.getAmount(),
                            Double::sum);
                }
            }

            totalValue.setText(formatCurrency(total));
            monthlyValue.setText(formatCurrency(monthTotal));
            countValue.setText(String.valueOf(monthCount));
            totalDescription.setText(
                    expenses.size() + " recorded transaction(s)");

            overviewCategoryChart.setValues(categoryTotals);
            overviewMonthlyChart.setValues(recentMonths, monthTotals);
            analyticsCategoryChart.setValues(categoryTotals);
            analyticsMonthlyChart.setValues(recentMonths, monthTotals);

            analyticsTotalValue.setText(formatCurrency(total));
            analyticsTransactionValue.setText(
                    String.valueOf(expenses.size()));
            analyticsCategoryValue.setText(
                    String.valueOf(categoryTotals.size()));

            syncMonthSelectors();
            refreshWorkspace(monthTotal, expenses);
            filterExpenses();

        } catch (SQLException e) {
            showError("Could not load expenses: " + e.getMessage());
        }
    }

    private static void filterExpenses() {
        if (searchField == null || fromDatePicker == null
                || tableModel == null) {
            return;
        }

        LocalDate from = fromDatePicker.getDate();
        LocalDate to = toDatePicker.getDate();

        if (from != null && to != null && from.isAfter(to)) {
            showError("From date cannot be after To date.");
            return;
        }

        String search = searchField.getText().trim().toLowerCase();
        String category = (String) categoryFilter.getSelectedItem();
        boolean dateRangeActive = from != null || to != null;

        try {
            List<Expense> expenses = expenseDAO.getAllExpenses();
            tableModel.setRowCount(0);

            for (Expense expense : expenses) {
                LocalDate date = expense.getDate();

                if (!dateRangeActive
                        && !YearMonth.from(date).equals(selectedMonth)) {
                    continue;
                }

                boolean matchesFrom = from == null || !date.isBefore(from);
                boolean matchesTo = to == null || !date.isAfter(to);
                boolean matchesSearch = expense.getTitle()
                        .toLowerCase().contains(search);
                boolean matchesCategory =
                        "All Categories".equals(category)
                                || expense.getCategory().equals(category);

                if (matchesFrom && matchesTo
                        && matchesSearch && matchesCategory) {
                    tableModel.addRow(new Object[]{
                            expense.getId(),
                            expense.getTitle(),
                            String.format("%.2f", expense.getAmount()),
                            expense.getCategory(),
                            date
                    });
                }
            }

            DateTimeFormatter fmt =
                    DateTimeFormatter.ofPattern("dd MMM yyyy");

            if (from != null && to != null) {
                activityTitle.setText("Transactions — " +
                        from.format(fmt) + " to " + to.format(fmt));
            } else if (from != null) {
                activityTitle.setText(
                        "Transactions — From " + from.format(fmt));
            } else if (to != null) {
                activityTitle.setText(
                        "Transactions — Until " + to.format(fmt));
            } else {
                activityTitle.setText("Transactions — " +
                        selectedMonth.format(
                                DateTimeFormatter.ofPattern("MMMM yyyy")));
            }

        } catch (SQLException e) {
            showError("Could not filter expenses: " + e.getMessage());
        }
    }

    // ADD EXPENSE

    private static void showAddExpenseForm() {
        JTextField titleField = new JTextField(18);
        JTextField amountField = new JTextField(18);

        styleInput(titleField);
        styleInput(amountField);

        JComboBox<String> categoryBox = new JComboBox<>(CATEGORIES);
        DatePicker datePicker = createDatePicker("Choose expense date");
        datePicker.setDate(LocalDate.now());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(form, gbc, 0, "Expense title", titleField);
        addFormRow(form, gbc, 1, "Amount (₹)", amountField);
        addFormRow(form, gbc, 2, "Category", categoryBox);
        addFormRow(form, gbc, 3, "Expense date", datePicker);

        int result = JOptionPane.showConfirmDialog(
                frame, form, "Add a new expense",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String title = titleField.getText().trim();
        String amountText = amountField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        LocalDate date = datePicker.getDate();

        if (title.isEmpty()) {
            showError("Please enter an expense title.");
            return;
        }

        if (date == null) {
            showError("Please select an expense date.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);

            if (!Double.isFinite(amount) || amount <= 0) {
                showError("Amount must be a positive number.");
                return;
            }

            expenseDAO.createTable();
            expenseDAO.addExpense(
                    new Expense(title, amount, category, date));

            refreshDashboard();

            JOptionPane.showMessageDialog(
                    frame, "Expense saved successfully!",
                    "SpendSphere", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            showError("Please enter a valid numeric amount.");
        } catch (SQLException e) {
            showError("Could not save expense: " + e.getMessage());
        }
    }

    private static void addFormRow(
            JPanel form, GridBagConstraints gbc, int row,
            String label, JComponent component) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        form.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(component, gbc);
    }

    // DELETE EXPENSE

    private static void deleteSelectedExpense() {
        int selected = expenseTable.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(
                    frame, "Please select an expense first.",
                    "No expense selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = expenseTable.convertRowIndexToModel(selected);
        int id = (int) tableModel.getValueAt(row, 0);
        String title = (String) tableModel.getValueAt(row, 1);

        int answer = JOptionPane.showConfirmDialog(
                frame, "Delete \"" + title + "\" permanently?",
                "Confirm deletion", JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            expenseDAO.deleteExpense(id);
            refreshDashboard();

            JOptionPane.showMessageDialog(
                    frame, "Expense deleted successfully.",
                    "SpendSphere", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            showError("Could not delete expense: " + e.getMessage());
        }
    }

    // UI HELPERS

    private static JButton button(String text, boolean primary) {
        JButton b = new JButton(text);
        b.setFont(BOLD);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);

        if (primary) {
            b.setBackground(ACCENT);
            b.setForeground(Color.WHITE);
            b.setBorder(new EmptyBorder(10, 15, 10, 15));
        } else {
            b.setBackground(new Color(245, 247, 250));
            b.setForeground(TEXT);
            b.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER),
                    new EmptyBorder(8, 12, 8, 12)));
        }

        return b;
    }

    private static void styleInput(JTextField field) {
        field.setFont(FONT);
        field.setPreferredSize(new Dimension(180, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER),
                new EmptyBorder(5, 8, 5, 8)));
    }

    private static String formatCurrency(double amount) {
        return String.format("₹%,.2f", amount);
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(
                frame, message, "SpendSphere",
                JOptionPane.WARNING_MESSAGE);
    }
}
