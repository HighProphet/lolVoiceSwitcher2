package pw.highprophet.switcher.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ProgressDialog extends JDialog {
    private JTextArea textArea;

    public ProgressDialog(Frame frame, boolean modal) {
        super(frame, modal);
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     *
     * @wbp.parser.entryPoint
     */
    private void initialize() {

        this.setSize(336, 114);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        this.setLocationRelativeTo(this.getOwner());
        this.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setSize(336, 114);
        panel.setBorder(new LineBorder(new Color(102, 204, 255), 2, true));
        panel.setLayout(null);
        this.getContentPane().add(panel, BorderLayout.CENTER);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setBounds(10, 10, 316, 94);
        panel.add(textArea);
    }

    public void progressBegin() {
        EventQueue.invokeLater(() -> {
            clearMessage();
            this.setVisible(true);
        });
    }

    public void progressBegin(String initMessage) {
        EventQueue.invokeLater(() -> {
            textArea.setText(initMessage + "\n");
            this.setVisible(true);
        });
    }

    public void progressEnd() {
        EventQueue.invokeLater(() -> this.setVisible(false));
    }

    public void progressEnd(long hideAfter) {
        EventQueue.invokeLater(() -> {
            try {
                Thread.sleep(hideAfter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.setVisible(false);
        });
    }

    public void appendMessage(String message) {
        EventQueue.invokeLater(() -> {
            textArea.append(message + "\n");
            textArea.setCaretPosition(textArea.getText().length() - 1);
        });
    }

    public void replaceMessage(String message) {
        EventQueue.invokeLater(() -> {
            textArea.setText(message);
            textArea.setCaretPosition(textArea.getText().length() - 1);
        });
    }

    public void clearMessage() {
        EventQueue.invokeLater(() -> textArea.setText(""));
    }
}
