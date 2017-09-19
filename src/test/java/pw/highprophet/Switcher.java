package pw.highprophet;

import java.io.File;

/**
 * Created by HighProphet945 on 2017/9/18.
 */
public class Switcher {
    public static void main(String[] args) throws Exception {
        System.out.println(new File(Switcher.class.getClassLoader().getResource("").toURI()).getParentFile().getAbsolutePath());
    }
}
