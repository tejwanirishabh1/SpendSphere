
package com.spendsphere;

import java.util.prefs.Preferences;

/**
 * Stores category budgets separately from the expense database.
 * Each category has an independent budget for each month.
 */
public final class CategoryBudgetManager {

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(CategoryBudgetManager.class);

    private CategoryBudgetManager() {
    }

    private static String key(String month, String category) {
        return "categoryBudget_" + month + "_" + category;
    }

    public static double getBudget(String month, String category) {
        return PREFS.getDouble(key(month, category), 0.0);
    }

    public static void setBudget(
            String month, String category, double amount) {

        if (month == null || category == null
                || !Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException(
                    "Invalid month, category, or budget amount.");
        }

        PREFS.putDouble(key(month, category), amount);
    }

    public static void removeBudget(String month, String category) {
        PREFS.remove(key(month, category));
    }
}
