package ru.taxtelecom.docs.ui;

import ru.taxtelecom.docs.model.BusinessDocument;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ViewDocumentDialog extends JDialog {
    public ViewDocumentDialog(JFrame owner, BusinessDocument document) {
        super(owner, document.getType().getTitle(), true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(360, 260));
        setSize(420, 340);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(8, 8));

        JTextArea memo = new JTextArea(document.toMemoText());
        memo.setEditable(false);
        memo.setLineWrap(true);
        memo.setWrapStyleWord(true);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(new JScrollPane(memo), BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        JPanel buttons = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        buttons.add(okButton);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);
    }
}

