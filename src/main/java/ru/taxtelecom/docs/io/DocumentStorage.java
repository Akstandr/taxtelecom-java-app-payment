package ru.taxtelecom.docs.io;

import ru.taxtelecom.docs.model.BusinessDocument;
import ru.taxtelecom.docs.model.DocumentType;
import ru.taxtelecom.docs.model.Invoice;
import ru.taxtelecom.docs.model.PaymentOrder;
import ru.taxtelecom.docs.model.PaymentRequest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DocumentStorage {
    private DocumentStorage() {
    }

    public static void save(BusinessDocument document, Path path) throws IOException {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put("type", document.getType().name());
        values.put("number", document.getNumber());
        values.put("date", document.getDate().toString());
        values.put("user", document.getUser());
        values.put("amount", Double.toString(document.getAmount()));

        if (document instanceof Invoice) {
            Invoice invoice = (Invoice) document;
            values.put("currency", invoice.getCurrency());
            values.put("currencyRate", Double.toString(invoice.getCurrencyRate()));
            values.put("product", invoice.getProduct());
            values.put("quantity", Double.toString(invoice.getQuantity()));
        } else if (document instanceof PaymentOrder) {
            PaymentOrder paymentOrder = (PaymentOrder) document;
            values.put("employee", paymentOrder.getEmployee());
        } else if (document instanceof PaymentRequest) {
            PaymentRequest paymentRequest = (PaymentRequest) document;
            values.put("counterparty", paymentRequest.getCounterparty());
            values.put("currency", paymentRequest.getCurrency());
            values.put("currencyRate", Double.toString(paymentRequest.getCurrencyRate()));
            values.put("commission", Double.toString(paymentRequest.getCommission()));
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, String> entry : values.entrySet()) {
                writer.write(escape(entry.getKey()));
                writer.write('=');
                writer.write(escape(entry.getValue()));
                writer.newLine();
            }
        }
    }

    public static BusinessDocument load(Path path) throws IOException {
        Map<String, String> values = new LinkedHashMap<String, String>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }
                int separator = findSeparator(line);
                if (separator < 0) {
                    throw new IOException("Некорректная строка: " + line);
                }
                values.put(unescape(line.substring(0, separator)), unescape(line.substring(separator + 1)));
            }
        }

        DocumentType type = DocumentType.valueOf(required(values, "type"));
        String number = required(values, "number");
        LocalDate date = LocalDate.parse(required(values, "date"));
        String user = required(values, "user");
        double amount = parseDouble(values, "amount");

        switch (type) {
            case INVOICE:
                return new Invoice(number, date, user, amount, required(values, "currency"),
                        parseDouble(values, "currencyRate"), required(values, "product"),
                        parseDouble(values, "quantity"));
            case PAYMENT_ORDER:
                return new PaymentOrder(number, date, user, amount, required(values, "employee"));
            case PAYMENT_REQUEST:
                return new PaymentRequest(number, date, user, required(values, "counterparty"), amount,
                        required(values, "currency"), parseDouble(values, "currencyRate"),
                        parseDouble(values, "commission"));
            default:
                throw new IOException("Неизвестный тип документа: " + type);
        }
    }

    private static String required(Map<String, String> values, String key) throws IOException {
        String value = values.get(key);
        if (value == null) {
            throw new IOException("В файле отсутствует поле: " + key);
        }
        return value;
    }

    private static double parseDouble(Map<String, String> values, String key) throws IOException {
        try {
            return Double.parseDouble(required(values, key).replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IOException("Некорректное число в поле: " + key, e);
        }
    }

    private static int findSeparator(String line) {
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (escaped) {
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else if (current == '=') {
                return i;
            }
        }
        return -1;
    }

    private static String escape(String value) {
        StringBuilder result = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\\' || current == '=') {
                result.append('\\');
            } else if (current == '\n') {
                result.append("\\n");
                continue;
            } else if (current == '\r') {
                result.append("\\r");
                continue;
            }
            result.append(current);
        }
        return result.toString();
    }

    private static String unescape(String value) {
        StringBuilder result = new StringBuilder(value.length());
        boolean escaped = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (!escaped && current == '\\') {
                escaped = true;
                continue;
            }
            if (escaped) {
                if (current == 'n') {
                    result.append('\n');
                } else if (current == 'r') {
                    result.append('\r');
                } else {
                    result.append(current);
                }
                escaped = false;
            } else {
                result.append(current);
            }
        }
        if (escaped) {
            result.append('\\');
        }
        return result.toString();
    }
}

