package ru.taxtelecom.docs;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class DocumentApp {
    private DocumentApp() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {

                }
                new MainFrame().setVisible(true);
            }
        });
    }
}

