package ru.taxtelecom.docs.ui;

import ru.taxtelecom.docs.model.BusinessDocument;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.util.Set;

public final class DocumentListRenderer extends JCheckBox implements ListCellRenderer<BusinessDocument> {
    private final Set<BusinessDocument> checkedDocuments;

    public DocumentListRenderer(Set<BusinessDocument> checkedDocuments) {
        this.checkedDocuments = checkedDocuments;
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends BusinessDocument> list, BusinessDocument value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getListTitle());
        setSelected(checkedDocuments.contains(value));
        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
        setFont(list.getFont());
        setEnabled(list.isEnabled());
        return this;
    }
}

