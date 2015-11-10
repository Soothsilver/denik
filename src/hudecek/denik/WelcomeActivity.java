package hudecek.denik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setStorageDirectory) {
            Utils.launchFileDialog(this, Utils.FILE_SELECT_CODE);
        } else if (item.getItemId() == R.id.createNewDiary) {
            UserInterface.switchTo(this, NewDiaryActivity.class);
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

        final Spinner spName = (Spinner) findViewById(R.id.spnName);
        final EditText tbPass = (EditText)findViewById(R.id.tbPassword);
        final CheckBox cbRemember = (CheckBox)findViewById(R.id.cbRememberPassword);
        populateSpinner(spName);
        String ancientName = Utils.retrieveSetting(this, "login-name");
        String ancientPassword = Utils.retrieveSetting(this, "login-password");
        if (ancientPassword != null) {
            tbPass.setText(ancientPassword);
        }
        if ("true".equals(Utils.retrieveSetting(this, "login-remember"))) {
            cbRemember.setChecked(true);
        }
        if (ancientName != null) {
            for (int i = 0; i <spName.getCount();i++) {
                if (spName.getItemAtPosition(i).toString().equals(ancientName)) {
                    spName.setSelection(i);
                    break;
                }
            }
        }


        Button bLogin = (Button)findViewById(R.id.bLogin);

        final Activity context = this;
        getActionBar().setTitle(getString(R.string.loginTitle));


        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diaryName = spName.getSelectedItem().toString();
                String diaryPass = tbPass.getText().toString();
                if (Session.login(context, diaryName, diaryPass)) {

                    if (!cbRemember.isChecked()) {
                        tbPass.setText("");
                        Utils.storeSetting(context, "login-password", "");
                    }
                    Utils.storeSetting(context, "login-remember", cbRemember.isChecked() ? "true" : "false");
                    UserInterface.switchTo(context, MainActivity.class);
                }
                /*
                if (Session.load(context, diaryName, diaryPass)) {
                    Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(i);
                }*/
            }
        });
    }

    @Override
    protected void onStop() {
        final Spinner spName = (Spinner) findViewById(R.id.spnName);
        final EditText tbPass = (EditText)findViewById(R.id.tbPassword);
        Object o = spName.getSelectedItem();
        Utils.storeSetting(this, "login-name", o == null ? "" : o.toString());
        Utils.storeSetting(this, "login-password", tbPass.getText().toString());
        super.onStop();
    }

    private void populateSpinner(Spinner spinner) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item);
        for(String s : Diaries.enumerateDiaries(this)) {
            spinnerAdapter.add(s);
        }
        spinner.setAdapter(spinnerAdapter);
        if (spinnerAdapter.isEmpty()) {
            UserInterface.toast(this, getString(R.string.zadnyDenikNebylNalezen));
        }
    }
}