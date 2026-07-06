package ru.taxtelecom.docs.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class BusinessDocument {
    public static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final String number;
    private final LocalDate date;
    private final String user;
    private final double amount;

    protected BusinessDocument(String number, LocalDate date, String user, double amount) {
        this.number = requireText(number, "Номер");
        if (date == null) {
            throw new IllegalArgumentException("Дата обязательна");
        }
        this.date = date;
        this.user = requireText(user, "Пользователь");
        this.amount = amount;
    }

    public abstract DocumentType getType();

    public String getNumber() {
        return number;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }

    public String getListTitle() {
        return getType().getTitle() + " от " + date.format(DISPLAY_DATE_FORMAT) + " номер " + number;
    }

    public String toMemoText() {
        StringBuilder memo = new StringBuilder();
        appendLine(memo, "Номер", number);
        appendLine(memo, "Дата", date.format(DISPLAY_DATE_FORMAT));
        appendLine(memo, "Пользователь", user);
        appendLine(memo, "Сумма", formatDouble(amount));
        appendSpecificMemo(memo);
        return memo.toString();
    }

    protected abstract void appendSpecificMemo(StringBuilder memo);

    protected static void appendLine(StringBuilder memo, String name, String value) {
        memo.append(name).append(": ").append(value).append(System.lineSeparator());
    }

    protected static String formatDouble(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return Double.toString(value);
        }
        if (value == Math.rint(value)) {
            return String.format(java.util.Locale.US, "%.0f", value);
        }
        return Double.toString(value);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " обязателен");
        }
        return value.trim();
    }
}

