package hudecek.denik;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePasswordActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepassword);

        final EditText tbNew1 = (EditText)findViewById(R.id.tbChPass1);
        final EditText tbNew2 = (EditText)findViewById(R.id.tbChPass2);
        Button b = (Button)findViewById(R.id.bChangePassword);
        TextView lblName = (TextView)findViewById(R.id.lblChangePassDiaryName);
        lblName.setText(getString(R.string.denikcolon) + Session.getSession().getName());
        getActionBar().setTitle(R.string.zmenitHeslo);
        final Activity context = this;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass1 = tbNew1.getText().toString();
                String pass2 = tbNew2.getText().toString();
                if (pass1.length() == 0) {
                    UserInterface.toast(context, getString(R.string.hesloUnempty));
                    return;
                }
                if (!pass1.equals(pass2)) {
                    UserInterface.toast(context, getString(R.string.heslaNejsouShodna));
                    return;
                }
                Session.getSession().setPassword(pass1);
                if (Session.getSession().saveToFile(context)) {
                    UserInterface.toast(context, getString(R.string.hesloChanged));
                    UserInterface.switchTo(context, MainActivity.class);
                }
            }
        });
    }

}