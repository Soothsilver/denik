package hudecek.denik;

import android.app.Activity;
import android.widget.Toast;

import org.jasypt.encryption.*;
import org.jasypt.encryption.pbe.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class Encryption {
    public static String roundkey(String key) {
        if (key.length() > 16)
            return key.substring(0, 16);

        int diff=  16 - key.length();
        for (int i =0; i < diff;i++)
            key += "A";
        return key;
    }
    static byte[] iv = {-89, -19, 17, -83, 86, 106, -31, 30, -5, -111, 61, -75, -84, 95, 120, -53};

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
        /*
        se.simbio.encryption.Encryption encryption = se.simbio.encryption.Encryption.getDefault(key, "SALT", iv);
        String result = encryption.encryptOrNull(text);
        if (result == null) {
            Toast.makeText(context, "Selhalo zašifrování.", Toast.LENGTH_LONG).show();
            return null;
        }
        return result;*/
    }
    public static String decrypt(Activity context, String text, String key) {
        try {
            StandardPBEStringEncryptor decryptor = new StandardPBEStringEncryptor();
            decryptor.setPassword(key);
            String result = decryptor.decrypt(text);
            return result;
        } catch (Exception ex) {
            Toast.makeText(context, context.getString(R.string.passIncorrect), Toast.LENGTH_LONG).show();
            return null;
        }
        /*
        se.simbio.encryption.Encryption encryption = se.simbio.encryption.Encryption.getDefault(key, "SALT", iv);
        String result = encryption.decryptOrNull(text);
        if (result == null) {
            Toast.makeText(context, context.getString(R.string.passIncorrect), Toast.LENGTH_LONG).show();
            return null;
        }
        return result;*/
    }
}
