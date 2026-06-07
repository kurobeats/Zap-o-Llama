package org.zaproxy.zap.extension.ollama;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class OllamaResultDialog extends JDialog {

    public OllamaResultDialog(Frame parent, String title, String content, boolean modal) {
        super(parent, title, modal);
        JTextArea textArea = new JTextArea(20, 70);
        textArea.setText(content);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton copyBtn = new JButton("Copy to clipboard");
        copyBtn.addActionListener(e -> {
            String t = textArea.getText();
            if (t != null && !t.isBlank())
                Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new StringSelection(t), null);
        });
        JPanel bp = new JPanel(); bp.add(copyBtn);
        add(bp, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(800, 500));
        pack();
        setLocationRelativeTo(parent);
    }

    public static void show(Frame parent, String title, String content) {
        SwingUtilities.invokeLater(() ->
            new OllamaResultDialog(parent, title, content, false).setVisible(true));
    }
}
