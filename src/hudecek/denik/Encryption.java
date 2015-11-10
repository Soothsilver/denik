package hudecek.denik;

import android.app.Activity;
import android.widget.Toast;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class Encryption {

    /**
     * Encrypts the given plaintext with the specified key.
     * @param context Context for push notifications.
     * @param text The plaintext.
     * @param key The encryption key.
     * @return The encrypted text.
     */
    public static String encrypt(Activity context, String text, String key)
    {
        if (key.length() == 0) {
            Toast.makeText(context,  context.getString(R.string.passwordMustNotBeEmpty), Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
            encryptor.setPassword(key);
            return encryptor.encrypt(text);
        }
        catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();

            Toast.makeText(context, context.getString(R.string.encryptionFailure), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * Attempts to decrypt the encrypted text with the given key.
     * @param context Context for push notification.
     * @param text Encrypted text.
     * @param key Encryption key.
     * @return The plaintext, or null if decryption fails.
     */
    public static String decrypt(Activity context, String text, String key) {
        try {
            StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
            decryptor.setPassword(key);
            return decryptor.decrypt(text);
        } catch (Exception ex) {
            Toast.makeText(context, context.getString(R.string.passIncorrect), Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
