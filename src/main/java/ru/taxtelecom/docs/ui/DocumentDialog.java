package ru.taxtelecom.docs.ui;

import ru.taxtelecom.docs.model.BusinessDocument;
import ru.taxtelecom.docs.model.DocumentType;
import ru.taxtelecom.docs.model.Invoice;
import ru.taxtelecom.docs.model.PaymentOrder;
import ru.taxtelecom.docs.model.PaymentRequest;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DocumentDialog extends JDialog {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final DocumentType type;
    private final Map<String, JTextField> fields = new LinkedHashMap<String, JTextField>();
    private BusinessDocument document;

    public DocumentDialog(JFrame owner, DocumentType type) {
        super(owner, type.getTitle(), true);
        this.type = type;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(createFieldsPanel(), BorderLayout.CENTER);
        add(createButtonsPanel(), BorderLayout.SOUTH);
        setSize(420, 320);
        setLocationRelativeTo(owner);
    }

    public BusinessDocument getDocument() {
        return document;
    }

    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 4, 10));
        addField(panel, "number", "Номер", 0);
        addField(panel, "date", "Дата", 1);
        addField(panel, "user", "Пользователь", 2);

        int row = 3;
        if (type == DocumentType.PAYMENT_REQUEST) {
            addField(panel, "counterparty", "Контрагент", row++);
        }
        addField(panel, "amount", "Сумма", row++);
        if (type == DocumentType.INVOICE || type == DocumentType.PAYMENT_REQUEST) {
            addField(panel, "currency", "Валюта", row++);
            addField(panel, "currencyRate", "Курс валюты", row++);
        }
        if (type == DocumentType.INVOICE) {
            addField(panel, "product", "Товар", row++);
            addField(panel, "quantity", "Количество", row++);
        } else if (type == DocumentType.PAYMENT_ORDER) {
            addField(panel, "employee", "Сотрудник", row++);
        } else if (type == DocumentType.PAYMENT_REQUEST) {
            addField(panel, "commission", "Комиссия", row++);
        }

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = row;
        filler.weighty = 1.0;
        panel.add(new JLabel(), filler);
        fields.get("date").setText(LocalDate.now().format(INPUT_DATE_FORMAT));
        return panel;
    }

    private void addField(JPanel panel, String key, String label, int row) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(3, 0, 3, 8);
        panel.add(new JLabel(label + ":"), labelConstraints);

        JTextField textField = new JTextField();
        fields.put(key, textField);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(3, 0, 3, 0);
        panel.add(textField, fieldConstraints);
    }

    private JPanel createButtonsPanel() {
        JPanel panel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отмена");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accept();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }

    private void accept() {
        try {
            String number = text("number");
            LocalDate date = parseDate(text("date"));
            String user = text("user");
            double amount = parseDouble(text("amount"), "Сумма");

            if (type == DocumentType.INVOICE) {
                document = new Invoice(number, date, user, amount, text("currency"),
                        parseDouble(text("currencyRate"), "Курс валюты"), text("product"),
                        parseDouble(text("quantity"), "Количество"));
            } else if (type == DocumentType.PAYMENT_ORDER) {
                document = new PaymentOrder(number, date, user, amount, text("employee"));
            } else {
                document = new PaymentRequest(number, date, user, text("counterparty"), amount, text("currency"),
                        parseDouble(text("currencyRate"), "Курс валюты"),
                        parseDouble(text("commission"), "Комиссия"));
            }
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String text(String key) {
        return fields.get(key).getText().trim();
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Дата должна быть в формате ДД.ММ.ГГГГ");
        }
    }

    private static double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " должен быть числом");
        }
    }
}

