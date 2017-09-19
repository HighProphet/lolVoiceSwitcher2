package pw.highprophet.switcher.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class InitDialog extends JDialog {
    JLabel lblInformation;

    public InitDialog() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     *
     * @wbp.parser.entryPoint
     */
    private void initialize() {

        this.setSize(500, 200);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setSize(500, 200);
        panel.setBorder(new LineBorder(new Color(102, 204, 255), 2, true));
        panel.setLayout(null);
        this.getContentPane().add(panel, BorderLayout.CENTER);

        lblInformation = new JLabel("正在载入...");
        lblInformation.setBounds(10, 171, 127, 19);
        panel.add(lblInformation);
        lblInformation.setFont(new Font("华文中宋", Font.PLAIN, 16));

        JLabel lblTitle = new JLabel("lol英雄语音切换程序");
        lblTitle.setBounds(319, 138, 138, 20);
        lblTitle.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        lblTitle.setHorizontalAlignment(SwingConstants.TRAILING);
        panel.add(lblTitle);

        JLabel lblLogo = new JLabel("Voice Switcher");
        lblLogo.setBounds(45, 30, 412, 98);
        lblLogo.setVerticalAlignment(SwingConstants.TOP);
        lblLogo.setForeground(new Color(255, 153, 0));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setFont(new Font("MV Boli", Font.PLAIN, 60));
        panel.add(lblLogo);
    }

    public void initStart() {
        EventQueue.invokeLater(() -> this.setVisible(true));
    }

    public void initComplete() {
        EventQueue.invokeLater(() -> this.dispose());
    }

    public void changeInfo(String info) {
        EventQueue.invokeLater(() -> lblInformation.setText(info));
    }
}
