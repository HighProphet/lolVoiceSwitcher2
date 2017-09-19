package pw.highprophet.switcher.gui;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import pw.highprophet.autopersistedjson.AutoPersistedJSONFactory;
import pw.highprophet.sharedutils.Utils;
import pw.highprophet.sharedutils.Zipper;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static pw.highprophet.switcher.gui.Switcher.Keys.AUTO_UPDATE;
import static pw.highprophet.switcher.gui.Switcher.Keys.LOL_ROOT;

public class Switcher {

    private static Logger l = LogManager.getLogger(Switcher.class);

    private static final File APP_ROOT = new File(System.getProperty("user.dir"));

    private static final String VOICE_PAK_ARCHIVE_DIR = APP_ROOT + File.separator + "data/voice_pack_archive";

    private static final String VOICE_PAK_EXTRACTED_DIR = APP_ROOT + File.separator + "data/voice_pack_extracted";

    private static final String LOCAL_VOICE_DIR = "Game/DATA/Sounds/Wwise/VO/zh_CN";

    private static final String VOICE_PAK_MANIFEST_FILENAME = "manifest.json";

    private static final String APP_CONFIG_FILE_PATH = APP_ROOT + File.separator + "config.json";

    private static final String BACKUP_ARCHIVE_PATH = APP_ROOT + File.separator + "data/cn_voice_bak.zip";

    private static Zipper zipper = Zipper.getZipper();

    private static JSONObject config;

    private static MainFrame frame;

    private static ProgressDialog progressDialog;

    private static InitDialog initDialog;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame = new MainFrame();
        progressDialog = new ProgressDialog(frame, true);
        initDialog = new InitDialog();
        initialize();
        frame.startGUI();
        registerActionListeners();
    }

    private static void registerActionListeners() {
        // 备份
        frame.btnBackup.addActionListener((e) -> {
            if (backupExists()) {
                int option = JOptionPane.showConfirmDialog(frame, "语音包备份文件已存在,是否覆盖", "提示",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                switch (option) {
                    case JOptionPane.NO_OPTION:
                    case JOptionPane.CANCEL_OPTION:
                        return;
                    default:
                        break;
                }
            }
            asyncBackup();
        });

        // 更改游戏目录
        frame.btnChangeGameDir.addActionListener((e) -> {
            String gameDir = JOptionPane.showInputDialog(frame, "请输入LOL国服游戏目录");
            while (!isCnLOLGameDirectory(gameDir)) {
                gameDir = JOptionPane.showInputDialog(frame, "输入有误,请重新输入");
            }
            config.replace(LOL_ROOT, gameDir);
            frame.changeGameDirText(gameDir);
        });

        frame.chckbxUpdateOnStartUp.addActionListener((e) -> {
            boolean selected = frame.chckbxUpdateOnStartUp.isSelected();
            if (selected && config.getBoolean(AUTO_UPDATE)) {
                return;
            }
            config.replace(AUTO_UPDATE, selected);
        });

        // 检查更新
        frame.btnCheckUpdate.addActionListener((e) -> {

        });

        // 恢复备份
        frame.btnRecover.addActionListener((e) -> {
            if (!backupExists()) {
                JOptionPane.showConfirmDialog(frame, "备份文件不存在,无法还原!", "ERROR", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            asyncRecover();
        });

        // 替换
        frame.banSubstitute.addActionListener((e) -> {
            if (!backupExists()) {
                int option = JOptionPane.showConfirmDialog(frame, "没有发现国服语音包备份文件,推荐进行备份", "提示",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                switch (option) {
                    case JOptionPane.YES_OPTION:
                        asyncBackup();
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.CANCEL_OPTION:
                        return;
                }
            }
            asyncSubstitute();
        });
    }

    /**
     * 1.检查语音包存在性,若语音包文件不存在则从服务端拉取,若无法连接服务端则弹出错误提示然后退出 2.语音包存在,进行解包操作,校验完整性
     */
    private static void initialize() {
        initDialog.initStart();
        initDialog.changeInfo("正在初始化...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 检查语音包archive文件夹是否存在,若存不存在则创建
        File dir = new File(VOICE_PAK_ARCHIVE_DIR);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        // 检查语音包archive文件是否存在,若不存在则向服务端拉取
        File extracted = new File(VOICE_PAK_EXTRACTED_DIR);
        File[] files = extracted.listFiles();
        if (files == null || files.length == 0) {
            File[] archives = dir.listFiles();
            if (archives == null) {
                fetchVoicePakArchive();
                archives = dir.listFiles();
                if (archives == null) {
                    initDialog.changeInfo("Impossible");
                    System.exit(0);
                }
            }
            // 解包语音包Archive
            initDialog.changeInfo("程序正在解压语音包,请稍后");
            zipper.turnOnInfoOutput(false);
            for (File archive : archives) {
                zipper.unzip(archive, VOICE_PAK_EXTRACTED_DIR);
            }
        }

        // 校验完整性
        File mf = new File(VOICE_PAK_EXTRACTED_DIR, VOICE_PAK_MANIFEST_FILENAME);
        if (!mf.exists()) {
            JOptionPane.showConfirmDialog(frame, "没有找到语音包清单文件,程序可能已经被破坏","错误",JOptionPane.DEFAULT_OPTION);
            System.exit(0);
        }
        JSONObject manifest = null;
        try {
            manifest = Utils.getJSONParser(new FileInputStream(mf)).parseObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONArray voiceFiles = manifest.getJSONArray("voiceFiles");
        boolean valid = true;
        initDialog.changeInfo("正在校验语音包文件");
        try {
            for (Object o : voiceFiles) {
                JSONArray ary = (JSONArray) o;
                File file = new File(VOICE_PAK_EXTRACTED_DIR, ary.getString(0));
                if (!ary.getString(1).equals(Utils.md5Digest(new FileInputStream(file)))) {
                    initDialog.changeInfo("文件[" + file.getName() + "]已被破坏");
                    valid = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!valid) {
            initDialog.changeInfo("程序完整性已无法保证,是否继续?");
            // todo 是否继续
        }

        // 读取配置文件
        File configFile = new File(APP_CONFIG_FILE_PATH);
        initDialog.changeInfo("正在读取配置");
        try {
            config = AutoPersistedJSONFactory.getPersistedJSON(configFile, JSONObject.class);
        } catch (IOException e) {
            l.error(e);
            System.exit(0);
        }
        if (config.getString(LOL_ROOT) == null) {
            String path = JOptionPane.showInputDialog(frame, "没有找到LOL国服目录,请输入:");
            do {
                if (isCnLOLGameDirectory(path)) {
                    config.put(LOL_ROOT, path);
                    break;
                }
                path = JOptionPane.showInputDialog(frame, "路径有误,请重新输入:");
            } while (true);
        }
        frame.chckbxUpdateOnStartUp.setSelected(config.getBooleanValue(AUTO_UPDATE));
        frame.changeGameDirText(config.getString(LOL_ROOT));
        initDialog.initComplete();
    }

    private static boolean isCnLOLGameDirectory(String path) {
        File tagFile = new File(path, "Game\\League of Legends.exe");
        return tagFile.exists();
    }

    private static void fetchVoicePakArchive() {

    }

    private static void asyncRecover() {
        new Thread(() -> {
            progressDialog.progressBegin("正在恢复备份");
            zipper.setMessageAppender((m) -> {
                progressDialog.replaceMessage(m);
            });
            File backupFiles = new File(zipper.unzip(BACKUP_ARCHIVE_PATH));
            doSubstitute(new File(LOCAL_VOICE_DIR), backupFiles.listFiles());
            progressDialog.replaceMessage("已恢复备份");
            progressDialog.progressEnd(1000);
        }).start();
    }

    private static void asyncSubstitute() {
        new Thread(() -> {
            progressDialog.progressBegin("正在替换语音包");
            doSubstitute(new File(config.getString(LOL_ROOT), LOCAL_VOICE_DIR),
                    new File(VOICE_PAK_EXTRACTED_DIR).listFiles());
            progressDialog.replaceMessage("替换完成");
            JOptionPane.showConfirmDialog(frame, "请在更新游戏之前还原语音包", "提示", JOptionPane.OK_CANCEL_OPTION);
            progressDialog.progressEnd();
        }).start();
    }

    private static void doSubstitute(File root, File[] files) {
        for (File f : root.listFiles()) {
            if (f.isDirectory()) {
                doSubstitute(f, files);
            } else {
                for (File f2 : files) {
                    if (f2.getName().equals(f.getName())) {
                        progressDialog.replaceMessage("正在替换: " + f2.getName());
                        Utils.recursivelyCopy(f2, f);
                    }
                }
            }
        }
    }

    private static void asyncBackup() {
        new Thread(() -> {
            progressDialog.progressBegin("正在备份中文语音包");
            zipper.setMessageAppender((m) -> {
                progressDialog.replaceMessage(m);
            });
            zipper.zip(BACKUP_ARCHIVE_PATH, new File(config.getString(LOL_ROOT), LOCAL_VOICE_DIR).listFiles());
            progressDialog.replaceMessage("备份已完成");
            progressDialog.progressEnd(1000);
        }).start();
    }

    /**
     * 检查备份文件的存在是否与期望的一致
     */
    private static boolean backupExists() {
        File backup = new File(BACKUP_ARCHIVE_PATH);
        return backup.exists();
    }


    static final class Keys {
        static final String LOL_ROOT = "lolRoot";
        static final String AUTO_UPDATE = "autoUpdate";
    }

}
