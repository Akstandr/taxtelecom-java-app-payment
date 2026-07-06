package ru.taxtelecom.docs;

import ru.taxtelecom.docs.io.DocumentStorage;
import ru.taxtelecom.docs.model.BusinessDocument;
import ru.taxtelecom.docs.model.DocumentType;
import ru.taxtelecom.docs.ui.DocumentDialog;
import ru.taxtelecom.docs.ui.DocumentListRenderer;
import ru.taxtelecom.docs.ui.ViewDocumentDialog;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public final class MainFrame extends JFrame {
    private final DefaultListModel<BusinessDocument> documents = new DefaultListModel<BusinessDocument>();
    private final Set<BusinessDocument> checkedDocuments =
            Collections.newSetFromMap(new IdentityHashMap<BusinessDocument, Boolean>());
    private final JList<BusinessDocument> documentList = new JList<BusinessDocument>(documents);
    private final JFileChooser fileChooser = new JFileChooser();

    public MainFrame() {
        super("Тест");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 360));
        setSize(650, 430);
        setLocationRelativeTo(null);

        documentList.setCellRenderer(new DocumentListRenderer(checkedDocuments));
        documentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        documentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = documentList.locationToIndex(e.getPoint());
                if (index >= 0 && documentList.getCellBounds(index, index).contains(e.getPoint())) {
                    toggleChecked(index);
                }
            }
        });
        documentList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && documentList.getSelectedIndex() >= 0) {
                    toggleChecked(documentList.getSelectedIndex());
                    e.consume();
                }
            }
        });

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(new JScrollPane(documentList), BorderLayout.CENTER);
        root.add(createButtonPanel(), BorderLayout.EAST);
        setContentPane(root);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 8));
        panel.setPreferredSize(new Dimension(150, 0));
        addButton(panel, "Накладная", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createDocument(DocumentType.INVOICE);
            }
        });
        addButton(panel, "Платёжка", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createDocument(DocumentType.PAYMENT_ORDER);
            }
        });
        addButton(panel, "Заявка на оплату", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createDocument(DocumentType.PAYMENT_REQUEST);
            }
        });
        addButton(panel, "Сохранить", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSelected();
            }
        });
        addButton(panel, "Загрузить", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDocument();
            }
        });
        addButton(panel, "Просмотр", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewSelected();
            }
        });
        addButton(panel, "Удалить выбранные", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteChecked();
            }
        });
        addButton(panel, "Выход", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        return panel;
    }

    private static void addButton(JPanel panel, String title, ActionListener listener) {
        JButton button = new JButton(title);
        button.addActionListener(listener);
        panel.add(button);
    }

    private void createDocument(DocumentType type) {
        DocumentDialog dialog = new DocumentDialog(this, type);
        dialog.setVisible(true);
        BusinessDocument document = dialog.getDocument();
        if (document != null) {
            documents.addElement(document);
            documentList.setSelectedIndex(documents.size() - 1);
        }
    }

    private void saveSelected() {
        BusinessDocument document = documentList.getSelectedValue();
        if (document == null) {
            showInfo("Выберите документ для сохранения");
            return;
        }
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                DocumentStorage.save(document, fileChooser.getSelectedFile().toPath());
            } catch (IOException ex) {
                showError("Не удалось сохранить документ", ex);
            }
        }
    }

    private void loadDocument() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BusinessDocument document = DocumentStorage.load(selectedFile.toPath());
                documents.addElement(document);
                documentList.setSelectedIndex(documents.size() - 1);
            } catch (IOException | IllegalArgumentException ex) {
                showError("Не удалось загрузить документ", ex);
            }
        }
    }

    private void viewSelected() {
        BusinessDocument document = documentList.getSelectedValue();
        if (document == null) {
            showInfo("Выберите документ для просмотра");
            return;
        }
        new ViewDocumentDialog(this, document).setVisible(true);
    }

    private void deleteChecked() {
        if (checkedDocuments.isEmpty()) {
            showInfo("Отметьте документы для удаления");
            return;
        }
        for (int i = documents.size() - 1; i >= 0; i--) {
            BusinessDocument document = documents.get(i);
            if (checkedDocuments.remove(document)) {
                documents.remove(i);
            }
        }
        documentList.repaint();
    }

    private void toggleChecked(int index) {
        BusinessDocument document = documents.get(index);
        if (!checkedDocuments.remove(document)) {
            checkedDocuments.add(document);
        }
        documentList.setSelectedIndex(index);
        documentList.repaint(documentList.getCellBounds(index, index));
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Сообщение", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
