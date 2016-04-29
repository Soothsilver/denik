package hudecek.denik;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows manipulation of the list of diaries.
 */
public class Diaries {
    private static File folder;

    public static List<String> enumerateDiaries(Activity context) {
        loadDiaryDirectory(context);
        ArrayList<String> names = new ArrayList<>();
        for (File f : folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".hudecek");
            }
        })) {
            names.add(f.getName().substring(0, f.getName().length() - ".hudecek".length()));
        }
        return names;
    }

    private static void loadDiaryDirectory(Activity context) {
        String retrievedString = Utils.retrieveSetting(context, "diaryDirectory");
        if (retrievedString != null) {
            folder = new File(retrievedString);

            if (!folder.isDirectory()) {
                retrievedString = null;
                UserInterface.toast(context, context.getString(R.string.mistoByloSmazano));
            }
        }
        if (retrievedString == null) {
            File documents = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File denikDir = new File(documents, "denik");
            //noinspection ResultOfMethodCallIgnored
            denikDir.mkdirs();
            folder = denikDir;
            Utils.storeSetting(context, "diaryDirectory", folder.getAbsolutePath());
        }
    }

    public static boolean exists(Activity context, String diaryName) {
        loadDiaryDirectory(context);
        File thisFile = new File(folder, diaryName + ".hudecek");
        return (thisFile.isFile());
    }

    public static boolean spawn(Activity context, String name, String password) {
        loadDiaryDirectory(context);
        Session session = new Session(name, password);
        return session.saveToFile(context);
    }

    public static File getFileFromDiaryName(Activity context, String name) {
        loadDiaryDirectory(context);
        return new File(folder, name + ".hudecek");
    }

    public static File getBackupDirectory(Activity context) {
        loadDiaryDirectory(context);
        File backup = new File(folder, "backups");
        //noinspection ResultOfMethodCallIgnored
        backup.mkdirs();
        return backup;
    }

    public static File getDirectory(Activity context) {
        loadDiaryDirectory(context);
        return folder;
    }

    public static void setDirectory(Activity context, File selectedFolder) {
        Utils.storeSetting(context, "diaryDirectory", selectedFolder.getAbsolutePath());
        loadDiaryDirectory(context);
    }
}
