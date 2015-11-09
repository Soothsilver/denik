package hudecek.denik;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
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

    public static void store(Activity context, String key, String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
         SharedPreferences.Editor editor = settings.edit();
         editor.putString(key, value);
        editor.commit();
    }
    public static String retrieve(Activity context, String key) {
        SharedPreferences settings =PreferenceManager.getDefaultSharedPreferences(context);
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
            Utils.Toast(context, "Nebyl nalezen žádný správce souborù.");
        }
    }
    public static String readAllText(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        byte[] b  = new byte[(int) file.length()];
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


        return new String( b );
    }

    public static void setStorageDirectory(Activity context, Uri uri) {
        String path = uri.getPath();
        String lpath = uri.getLastPathSegment();
        path = path.substring(0, path.length() - lpath.length() - 1);
        Session.diaryDirectory = new File(path);
        store(context, "diaryDirectory", path);
        Toast(context, context.getString(R.string.denikySeNyniBudouUkladatDo) + path);
        Toast(context, context.getString(R.string.soucasneDenikyNebylyPrekopirovany));
    }

    public static void importFile(Activity context, Uri uri) {
        try {
            String data = readAllText(new File(uri.getPath().toString()));
            Session.deserialize(data);
            Toast(context, context.getString(R.string.importovano));

        } catch (IOException e) {
            Toast(context, context.getString(R.string.couldNotOpenFile));
        }

    }

    public static void exportFile(Activity context, Uri uri) {
        String data = Session.serialize();
        if ( Utils.saveAllText(context, new File(uri.getPath().toString()), data) ) {
            Toast.makeText(context, context.getString(R.string.exportovano), Toast.LENGTH_SHORT).show();
        }

    }
    public static boolean saveAllText(Activity context, File file, String data) {

        try {
            file.createNewFile();
        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.errorSaving), Toast.LENGTH_LONG).show();
            return false;
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Chyba pøi tvorbì proudu.", Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            String sss = data;
            if (sss == null) {
                return false ;
            }
            stream.write(sss.getBytes());
        } catch (IOException e) {
            Toast.makeText(context, "Chyba pøi zapisování.", Toast.LENGTH_LONG).show();
            return false;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                Toast.makeText(context, "Chyba pøi uzavírání.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}
