package hudecek.denik;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents one diary entry.
 */
public class Story implements Comparable<Story>, Serializable {
    public String name;
    public Date date = new Date();
    public String text;

    public  Story(String name) {
        this.name = name;
        this.text = "";
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Story another) {
        return -date.compareTo(another.date);
    }

    public boolean matches(String pattern) {
        if (pattern == null || pattern.equals("")) return true;
        String[] words = pattern.toLowerCase().split(" ");
        for(String word : words) {
            if (name.toLowerCase().contains(word)) continue;
            if (text.toLowerCase().contains(word)) continue;

            Calendar c = Calendar.getInstance(); c.setTime(date);
            String dateString = c.get(Calendar.DATE) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);
            if (dateString.contains(word)) continue;
            return false;
        }
        return true;
    }
}
