package ru.taxtelecom.docs.model;

public enum DocumentType {
    INVOICE("Накладная"),
    PAYMENT_ORDER("Платёжка"),
    PAYMENT_REQUEST("Заявка на оплату");

    private final String title;

    DocumentType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
