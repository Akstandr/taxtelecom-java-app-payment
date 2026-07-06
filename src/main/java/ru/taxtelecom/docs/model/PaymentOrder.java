package ru.taxtelecom.docs.model;

import java.time.LocalDate;

public final class PaymentOrder extends BusinessDocument {
    private final String employee;

    public PaymentOrder(String number, LocalDate date, String user, double amount, String employee) {
        super(number, date, user, amount);
        this.employee = requireText(employee, "Сотрудник");
    }

    @Override
    public DocumentType getType() {
        return DocumentType.PAYMENT_ORDER;
    }

    public String getEmployee() {
        return employee;
    }

    @Override
    protected void appendSpecificMemo(StringBuilder memo) {
        appendLine(memo, "Сотрудник", employee);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " обязателен");
        }
        return value.trim();
    }
}

