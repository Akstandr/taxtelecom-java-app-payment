package ru.taxtelecom.docs;

import ru.taxtelecom.docs.io.DocumentStorage;
import ru.taxtelecom.docs.model.BusinessDocument;
import ru.taxtelecom.docs.model.DocumentType;
import ru.taxtelecom.docs.model.Invoice;
import ru.taxtelecom.docs.model.PaymentOrder;
import ru.taxtelecom.docs.model.PaymentRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public final class DocumentSerializerTest {
    private DocumentSerializerTest() {
    }

    public static void main(String[] args) throws Exception {
        roundTrip(new Invoice("H156", LocalDate.of(2008, 10, 12), "Иванов", 1200.50,
                "USD", 30.45, "Монитор", 2.0));
        roundTrip(new PaymentOrder("K45", LocalDate.of(2008, 7, 13), "Самоделкин",
                10364.09, "Умельцев"));
        roundTrip(new PaymentRequest("П5", LocalDate.of(2008, 9, 1), "Петров",
                "ООО Ромашка", 5000.0, "EUR", 44.3, 100.0));
    }

    private static void roundTrip(BusinessDocument source) throws Exception {
        Path file = Files.createTempFile("document-", ".txt");
        DocumentStorage.save(source, file);
        BusinessDocument loaded = DocumentStorage.load(file);

        assert loaded.getType() == source.getType();
        assert loaded.getNumber().equals(source.getNumber());
        assert loaded.getDate().equals(source.getDate());
        assert loaded.getUser().equals(source.getUser());
        assert loaded.getAmount() == source.getAmount();
        assert loaded.getListTitle().equals(source.getListTitle());

        if (source.getType() == DocumentType.INVOICE) {
            assert loaded instanceof Invoice;
        } else if (source.getType() == DocumentType.PAYMENT_ORDER) {
            assert loaded instanceof PaymentOrder;
        } else if (source.getType() == DocumentType.PAYMENT_REQUEST) {
            assert loaded instanceof PaymentRequest;
        }
        Files.deleteIfExists(file);
    }
}

