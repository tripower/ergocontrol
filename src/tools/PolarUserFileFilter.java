package tools;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: HaBe
 * Date: 23.02.2005
 * Time: 09:52:04
 * To change this template use File | Settings | File Templates.
 */
public class PolarUserFileFilter extends FileFilter {
    public boolean accept(File file)
    {
        return file.isDirectory() ||file.getName().endsWith(".ppd");
    }

    public String getDescription()
    {
        return "*.ppd";
    }
}
