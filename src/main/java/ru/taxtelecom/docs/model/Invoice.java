package ru.taxtelecom.docs.model;

import java.time.LocalDate;

public final class Invoice extends BusinessDocument {
    private final String currency;
    private final double currencyRate;
    private final String product;
    private final double quantity;

    public Invoice(String number, LocalDate date, String user, double amount, String currency,
                   double currencyRate, String product, double quantity) {
        super(number, date, user, amount);
        this.currency = requireText(currency, "Валюта");
        this.currencyRate = currencyRate;
        this.product = requireText(product, "Товар");
        this.quantity = quantity;
    }

    @Override
    public DocumentType getType() {
        return DocumentType.INVOICE;
    }

    public String getCurrency() {
        return currency;
    }

    public double getCurrencyRate() {
        return currencyRate;
    }

    public String getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }

    @Override
    protected void appendSpecificMemo(StringBuilder memo) {
        appendLine(memo, "Валюта", currency);
        appendLine(memo, "Курс валюты", formatDouble(currencyRate));
        appendLine(memo, "Товар", product);
        appendLine(memo, "Количество", formatDouble(quantity));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " обязателен");
        }
        return value.trim();
    }
}

