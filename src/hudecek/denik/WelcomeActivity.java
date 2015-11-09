package hudecek.denik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class WelcomeActivity extends Activity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case Utils.FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Utils.setStorageDirectory(this, data.getData());
                }
                break;
            case Utils.FILE_IMPORT:
                if (resultCode == RESULT_OK) {
                    Utils.importFile(this, data.getData());
                }
                break;
            case Utils.FILE_EXPORT:
                if (resultCode == RESULT_OK) {
                    Utils.exportFile(this, data.getData());
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setStorageDirectory) {
            Utils.launchFileDialog(this, Utils.FILE_SELECT_CODE);
        } else {
            // Session.password = "";
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.welcomemenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        EditText tbName = (EditText)findViewById(R.id.tbName);
        EditText tbPass = (EditText)findViewById(R.id.tbPassword);

        EditText tbNewDiary = (EditText)findViewById(R.id.tbNewDiary);
        EditText tbNewPass1 = (EditText)findViewById(R.id.tbNewPass1);
        EditText tbNewPass2 = (EditText)findViewById(R.id.tbNewPass2);

        if (Session.diaryName != null) tbName.setText(Session.diaryName);
        if (Session.password != null) tbPass.setText(Session.password);

        Button bLogin = (Button)findViewById(R.id.bLogin);
        Button bRegister = (Button)findViewById(R.id.bNewDiary);

        Activity activ = this;
        getActionBar().setTitle(getString(R.string.loginTitle));

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tbNewDiary.getText().toString();
                String pass1 = tbNewPass1.getText().toString();
                String pass2 = tbNewPass2.getText().toString();
                if (!pass1.equals(pass2)) {
                    Toast.makeText(activ, getString(R.string.heslaNejsouShodna), Toast.LENGTH_LONG).show();
                    return;
                }
                Session.diaryName = name;
                Session.password = pass1;
                Session.stories = new ArrayList<Story>();
                if (Session.save(activ)) {
                    Toast.makeText(activ, getString(R.string.denikZalozen), Toast.LENGTH_LONG).show();
                }
            }
        });
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String diaryName = tbName.getText().toString();
                String diaryPass = tbPass.getText().toString();
                if (Session.load(activ, diaryName, diaryPass)) {
                    Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    //Toast.makeText(activ, getString(R.string.thisDiaryDoesNotExist), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Attempt login
        if (Session.diaryName != null && Session.password != null && !Objects.equals(Session.diaryName, "") && !Objects.equals(Session.password, "")) {
            if (Session.load(activ, Session.diaryName, Session.password)) {
                Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(i);
            }
        }

    }
}