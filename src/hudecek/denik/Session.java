package hudecek.denik;

import android.app.Activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Represents everything that needs to be saved when the application is paused. The entire session is serialized into the diary file.
 */
public class Session implements Serializable {
    private static Session currentSession;
    private ArrayList<Story> entries;
    private String name;
    private String pass;

    public Session(String name, String pass) {
        this.name = name;
        this.pass = pass;
        this.entries = new ArrayList<>();
    }

    public static Session getSession() {
        return Session.currentSession;
    }

    public ArrayList<Story> getStories() {
        return entries;
    }

    public String getName() {
        return name;
    }

    /**
     * Serializes all entries into a string.
     * @return The serialized list of all entries.
     */
    private String serialize() {
        String sum = "";
        for(Story story : entries) {
            Calendar c = Calendar.getInstance(); c.setTime(story.date);
            String line = story.name + ";" + c.get(Calendar.DATE) + ";" + c.get(Calendar.MONTH) + ";" + c.get(Calendar.YEAR) + ";" + story.text.replace(';', ':').replace("\r", "").replace("\n", "__NEWLINE__");
            sum += line + "\r\n";
        }
        // TODO optimize this
        return sum;
    }
    private boolean deserialize(String from) {
        entries = new ArrayList<>();
        try {
            for (String line : from.split("\r\n")) {
                if (line.trim().length() == 0) continue;
                String[] split = line.split(";");
                Story story = new Story(split[0]);
                story.date = new GregorianCalendar(
                        Integer.parseInt(split[3]),
                        Integer.parseInt(split[2]),
                        Integer.parseInt(split[1])).getTime();
                if (split.length == 4) {
                    story.text = "";
                } else {
                    story.text = split[4].replace("__NEWLINE__", "\n");
                }
                entries.add(story);
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
    public boolean saveToFile(Activity context) {
        String clearText = serialize();
        String encryptedText = Encryption.encrypt(context, clearText, pass);
        File file = Diaries.getFileFromDiaryName(context, name);
        boolean saved = Utils.saveAllText(context, file, encryptedText);
        if (saved) {
            // Backup
            File backups = Diaries.getBackupDirectory(context);
            Date d = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            File backupFile = new File(backups, name + "." + c.get(Calendar.DATE) + "." + c.get(Calendar.MONTH) + "." + c.get(Calendar.YEAR) + ".hudecek");
            Utils.saveAllText(context, backupFile, encryptedText);
            return true;
        } else {
            return false;
        }
    }
    @SuppressWarnings("RedundantIfStatement")
    private boolean loadFromFile(Activity context) {
        File file = Diaries.getFileFromDiaryName(context, name);
        String encryptedText = Utils.readAllText(file);
        String clearText = Encryption.decrypt(context, encryptedText, pass);
        if (clearText == null) return false;
        if (!deserialize(clearText)) return false;
        return true;
    }

    public static File diaryDirectory;
    public static String searchText = null;
    public static Story editingStory = null;


    public static boolean login(Activity context, String name, String password) {
        if (!Diaries.exists(context, name)) {
            UserInterface.toast(context, context.getString(R.string.denikSTimtoJmenemNeexistuje));
            return false;
        }
        Session session = new Session(name, password);
        if (session.loadFromFile(context)) {
            Session.currentSession = session;
            return true;
        } else {
            return false;
        }
    }

    public void addStory(Story story) {
        entries.add(story);
    }

    public void removeStory(Story story) {
        this.entries.remove(story);
    }

    public void saveAndToast(Activity activity) {
        if (saveToFile(activity))
            UserInterface.toast(activity, activity.getString(R.string.saveSuccessfulIs));
        else
            UserInterface.toast(activity, activity.getString(R.string.saveFailed));
    }

    public void setPassword(String password) {
        this.pass = password;
    }
}
