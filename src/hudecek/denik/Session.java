package hudecek.denik;

import java.io.*;
import java.util.ArrayList;

import android.app.Activity;
import android.os.*;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class Session {
    private static String PREFERENCES_FILE = "denikpreferences";
    public static String diaryName = "";
    public static String password = "";
    public static boolean initialized = false;
    public static File diaryDirectory;
    public static String searchText = null;

    public static Story editingStory = null;
    public static ArrayList<Story> stories = null;

    public static String serialize() {
        String sum = "";
        for(Story story : stories) {
            String line = story.name + ";" + story.date.getDate() + ";" + story.date.getMonth() + ";" + story.date.getYear() + ";" + story.text.replace(';', ':').replace("\r", "").replace("\n", "__NEWLINE__");
            sum += line + "\r\n";
        }
        return sum;
    }
    public static void deserialize(String from) {
        stories = new ArrayList<>();
        try {
            for (String line : from.split("\r\n")) {
                if (line.trim().length() == 0) continue;
                String[] split = line.split(";");
                Story story = new Story(split[0]);
                Date d = new Date(Integer.parseInt(split[3]),
                        Integer.parseInt(split[2]),
                        Integer.parseInt(split[1]));
                story.date = d;
                if (split.length == 4) {
                    story.text = "";
                } else {
                    story.text = split[4].replace("__NEWLINE__", "\n");
                }
                stories.add(story);
            }
        } catch (Exception ex) {
        }
    }

    public static void loadDiaryDirectory(Activity context) {
        diaryDirectory = new File(Utils.retrieve(context, "diaryDirectory"));
        if (diaryDirectory == null) {
            File documents = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File denikDir = new File(documents, "denik");
            denikDir.mkdirs();
            diaryDirectory = denikDir;
        }
    }

    public static String oldcontents;

    public static boolean load(Activity context, String name, String pass) {
        Session.diaryName = name;
        Session.password = pass;

        if (diaryDirectory == null) loadDiaryDirectory(context);
        File denikFile = new File(diaryDirectory, diaryName + ".hudecek");

        try {

            String contents = Utils.readAllText(denikFile);
            oldcontents = contents;

            contents = Encryption.decrypt(context, contents, Session.password);

            if (contents == null) {
                return false;
            }
            Session.deserialize(contents);


        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean save(Activity context) {
        if (diaryDirectory == null) loadDiaryDirectory(context);
        File denikFile = new File(diaryDirectory, diaryName + ".hudecek");
        String sss = serialize();
        sss = Encryption.encrypt(context, sss, Session.password);
        String ancient = "";
        try {
             ancient = Utils.readAllText(denikFile);
        } catch (IOException e) {
            ancient = sss;
            Session.oldcontents = sss;
        }
        if (!Session.oldcontents.equals(ancient)) {
            Utils.Toast(context, context.getString(R.string.souborBylZmenen));
            File ancientFile = new File(diaryDirectory, diaryName + ".theirs.hudecek");
            if (Utils.saveAllText(context, ancientFile, ancient)) {
                Utils.Toast(context, context.getString(R.string.dfgdkjfbdsjgnbsjdfbdsmbndsgnbdsfds));
            } else {
                Utils.Toast(context, context.getString(R.string.fdgsdfsgsdfg));
            }
        }

        try {
            denikFile.createNewFile();
        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.errorSaving), Toast.LENGTH_LONG).show();
            return false;
        }
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(denikFile);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Chyba p�i tvorb� proudu.", Toast.LENGTH_LONG).show();
            return false;
        }
        try {
            if (sss == null) {
                return false;
            }
            stream.write(sss.getBytes());
        } catch (IOException e) {
            Toast.makeText(context, "Chyba p�i zapisov�n�.", Toast.LENGTH_LONG).show();
            return false;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                Toast.makeText(context, "Chyba p�i uzav�r�n�.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        Session.oldcontents = sss;
        backup(context, diaryDirectory, diaryName, sss);
        Toast.makeText(context, context.getString(R.string.saveSuccessful), Toast.LENGTH_SHORT).show();
        return true;
    }

    private static void backup(Activity context, File diaryDirectory, String diaryName, String sss) {
        File backups = new File(diaryDirectory, "backups");
        backups.mkdirs();
        Date d = new Date();

        File backupfile = new File(backups, diaryName + "." + d.getDate() + "." + d.getMonth() + "." + d.getYear() + ".hudecek" );
        Utils.saveAllText(context, backupfile, sss);
    }
}
