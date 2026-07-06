package ru.taxtelecom.docs.model;

import java.time.LocalDate;

public final class PaymentRequest extends BusinessDocument {
    private final String counterparty;
    private final String currency;
    private final double currencyRate;
    private final double commission;

    public PaymentRequest(String number, LocalDate date, String user, String counterparty, double amount,
                          String currency, double currencyRate, double commission) {
        super(number, date, user, amount);
        this.counterparty = requireText(counterparty, "Контрагент");
        this.currency = requireText(currency, "Валюта");
        this.currencyRate = currencyRate;
        this.commission = commission;
    }

    @Override
    public DocumentType getType() {
        return DocumentType.PAYMENT_REQUEST;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public String getCurrency() {
        return currency;
    }

    public double getCurrencyRate() {
        return currencyRate;
    }

    public double getCommission() {
        return commission;
    }

    @Override
    protected void appendSpecificMemo(StringBuilder memo) {
        appendLine(memo, "Контрагент", counterparty);
        appendLine(memo, "Валюта", currency);
        appendLine(memo, "Курс валюты", formatDouble(currencyRate));
        appendLine(memo, "Комиссия", formatDouble(commission));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " обязателен");
        }
        return value.trim();
    }
}

