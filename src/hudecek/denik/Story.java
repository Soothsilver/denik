package hudecek.denik;

import java.util.Date;

public class Story implements Comparable<Story> {
    public String name;
    public Date date = new Date();
    public String text;

    public  Story(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Story another) {
        return -date.compareTo(another.date);
    }
}
