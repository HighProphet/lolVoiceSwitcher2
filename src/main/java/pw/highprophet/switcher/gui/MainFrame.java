package pw.highprophet.switcher.gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {


    private JLabel lblGameDir;
    JButton btnChangeGameDir;
    JButton banSubstitute;
    JButton btnBackup;
    JButton btnRecover;
    JButton btnCheckUpdate;
    JCheckBox chckbxUpdateOnStartUp;

    public MainFrame() {
        init();
    }

    private void init() {
        this.setTitle("LOL英雄语音转换程序");
        this.setSize(520, 148);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.getContentPane().setLayout(null);
        this.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "LOL\u6E38\u620F\u76EE\u5F55", TitledBorder.LEADING, TitledBorder.TOP,
                null, null));
        panel.setBounds(10, 10, 494, 60);
        this.getContentPane().add(panel);
        panel.setLayout(null);

        lblGameDir = new JLabel("G:\\Games\\英雄联盟");
        lblGameDir.setVerticalAlignment(SwingConstants.TOP);
        lblGameDir.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        lblGameDir.setBounds(10, 25, 404, 30);
        panel.add(lblGameDir);

        btnChangeGameDir = new JButton("更改");
        btnChangeGameDir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        btnChangeGameDir.setMargin(new Insets(2, 5, 2, 5));
        btnChangeGameDir.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        btnChangeGameDir.setBounds(424, 24, 60, 23);
        panel.add(btnChangeGameDir);

        banSubstitute = new JButton("转换");
        banSubstitute.setMargin(new Insets(2, 5, 2, 5));
        banSubstitute.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        banSubstitute.setBounds(10, 80, 60, 23);
        this.getContentPane().add(banSubstitute);

        btnBackup = new JButton("备份");
        btnBackup.setMargin(new Insets(2, 5, 2, 5));
        btnBackup.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        btnBackup.setBounds(80, 80, 60, 23);
        this.getContentPane().add(btnBackup);

        JButton btnExit = new JButton("退出");
        btnExit.setMargin(new Insets(2, 5, 2, 5));
        btnExit.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        btnExit.setBounds(434, 80, 60, 23);
        btnExit.addActionListener((e) -> System.exit(0));
        this.getContentPane().add(btnExit);

        btnRecover = new JButton("恢复备份");
        btnRecover.setMargin(new Insets(2, 5, 2, 5));
        btnRecover.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        btnRecover.setBounds(150, 80, 79, 23);
        this.getContentPane().add(btnRecover);

        chckbxUpdateOnStartUp = new JCheckBox("启动时更新");
        chckbxUpdateOnStartUp.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        chckbxUpdateOnStartUp.setBounds(323, 80, 105, 23);
        this.getContentPane().add(chckbxUpdateOnStartUp);

        btnCheckUpdate = new JButton("检查更新");
        btnCheckUpdate.setMargin(new Insets(2, 5, 2, 5));
        btnCheckUpdate.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        btnCheckUpdate.setBounds(239, 80, 79, 23);
        this.getContentPane().add(btnCheckUpdate);
    }

    public void startGUI() {
        EventQueue.invokeLater(() -> this.setVisible(true));
    }

    public void changeGameDirText(String gameDir) {
        EventQueue.invokeLater(() -> lblGameDir.setText(gameDir));
    }

    public JButton getBtnChangeGameDir() {
        return btnChangeGameDir;
    }

    public JButton getBanSubstitute() {
        return banSubstitute;
    }

    public JButton getBtnBackup() {
        return btnBackup;
    }

    public JButton getBtnRecover() {
        return btnRecover;
    }

    public JButton getBtnCheckUpdate() {
        return btnCheckUpdate;
    }

    public JCheckBox getChckbxUpdateOnStartUp() {
        return chckbxUpdateOnStartUp;
    }

}
