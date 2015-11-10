package hudecek.denik;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewDiaryActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newdiary);
        getActionBar().setTitle("Založit nový deník");


        final EditText tbNewDiary = (EditText) findViewById(R.id.tbNewName);
        final EditText tbNewPass1 = (EditText) findViewById(R.id.tbPass);
        final EditText tbNewPass2 = (EditText) findViewById(R.id.tbPass2);

        Button bRegister = (Button) findViewById(R.id.bRegister);

        final Activity context = this;

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tbNewDiary.getText().toString();
                String pass1 = tbNewPass1.getText().toString();
                String pass2 = tbNewPass2.getText().toString();
                if (name.length() == 0) {
                    UserInterface.toast(context, getString(R.string.jmenoUnempty));
                    return;
                }
                if (pass1.length() == 0) {
                    UserInterface.toast(context, getString(R.string.hesloUnempty));
                    return;
                }
                if (!pass1.equals(pass2)) {
                    UserInterface.toast(context, getString(R.string.heslaNejsouShodna));
                    return;
                }
                if (Diaries.exists(context, name)) {
                    UserInterface.toast(context, getString(R.string.denikSTimtoJmenemJizExistuje));
                    return;
                }
                if (Diaries.spawn(context, name, pass1)) {
                     if (Session.login(context, name, pass1)) {
                         UserInterface.switchTo(context, MainActivity.class);
                     }
                }
            }
        });
    }
}