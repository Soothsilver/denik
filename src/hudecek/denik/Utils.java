package hudecek.denik;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.*;

public class Utils {

    public static void Toast(Activity context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates or updates an application setting.
     * @param context Current activity.
     * @param key Key of the setting to create or update.
     * @param value The setting's new value.
     */
    public static void storeSetting(Activity context, String key, String value) {
         SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
         SharedPreferences.Editor editor = settings.edit();
         editor.putString(key, value);
         editor.apply();
    }

    /**
     * Retrieves a string value from the application settings.
     * @param context Current activity.
     * @param key Key of the setting to retrieve.
     * @return Value of the setting, or null if the setting does not yet exist.
     */
    public static String retrieveSetting(Activity context, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, null);
    }

    public static final int FILE_SELECT_CODE = 0;
    public static final int FILE_EXPORT = 1;
    public static final int FILE_IMPORT = 2;

    public static void launchFileDialog(Activity context, int code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            context.startActivityForResult(
                    Intent.createChooser(intent, "Vybrat"),
                    code);
        } catch (ActivityNotFoundException ex) {
            Utils.Toast(context, "Nebyl nalezen ��dn� spr�vce soubor�.");
        }
    }

    /**
     * Reads all text from the given file and returns it. Returns null if the file does not exist or cannot be read. Has undefined behaviour if the file is not a text file.
     * @param file The file to read.
     * @return The text in the file.
     */
    public static String readAllText(File file)  {
        try {
            InputStream in = new FileInputStream(file);
            byte[] b = new byte[(int) file.length()];
            int len = b.length;
            int total = 0;
            while (total < len) {
                int result = in.read(b, total, len - total);
                if (result == -1) {
                    break;
                }
                total += result;
            }
            in.close();
            return new String(b);
        } catch (IOException ex) {
            return null;
        }
    }

    public static void setStorageDirectory(Activity context, Uri uri) {
        String path = uri.getPath();
        String lpath = uri.getLastPathSegment();
        path = path.substring(0, path.length() - lpath.length() - 1);
        Session.diaryDirectory = new File(path);
        storeSetting(context, "diaryDirectory", path);
        Toast(context, context.getString(R.string.denikySeNyniBudouUkladatDo) + path);
        Toast(context, context.getString(R.string.soucasneDenikyNebylyPrekopirovany));
    }
/*
    public static void importFile(Activity context, Uri uri) {
        String data = readAllText(new File(uri.getPath()));
        Session.deserialize(data);
        Toast(context, context.getString(R.string.importovano));
    }

    public static void exportFile(Activity context, Uri uri) {
        String data = Session.serialize();
        if ( Utils.saveAllText(context, new File(uri.getPath()), data) ) {
            Toast.makeText(context, context.getString(R.string.exportovano), Toast.LENGTH_SHORT).show();
        }

    }
    */

    /**
     * Writes the specified string into a file.
     * @param context Current activity used to push notifications.
     * @param file The file to overwrite or create.
     * @param data The string to write into the file.
     * @return True if the text was saved successfully.
     */
    public static boolean saveAllText(Activity context, File file, String data) {

        try {
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.errorSaving), Toast.LENGTH_LONG).show();
            return false;
        }
        FileOutputStream stream;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, R.string.chybaPriTvorbeProudu, Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            if (data == null) {
                return false ;
            }
            stream.write(data.getBytes());
        } catch (IOException e) {
            Toast.makeText(context, R.string.chybaPriZapisovani, Toast.LENGTH_LONG).show();
            return false;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                Toast.makeText(context, R.string.chybaPriUzavirani, Toast.LENGTH_LONG).show();
                //noinspection ReturnInsideFinallyBlock
                return false;
            }
        }
        return true;
    }
}
