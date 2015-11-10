package hudecek.denik;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

/**
 * Static functions that wrap around Android user interface libraries.
 */
public final class UserInterface {
    /**
     * Starts a new Android activity screen.
     * @param from The current activity.
     * @param to The activity we want to switch to.
     */
    public static void switchTo(Activity from, Class to) {
        from.startActivity(new Intent(from, to));
    }


    /**
     * Pushes a short toast notification to the user.
     * @param context The current activity.
     * @param text The text to show to the user.
     */
    public static void toast(Activity context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
