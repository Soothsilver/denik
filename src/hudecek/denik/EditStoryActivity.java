package hudecek.denik;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class EditStoryActivity extends Activity {
    Story story;
    EditText tbDate;
    EditText tbNadpis;
    EditText tbContents;
    Button bChangeDate;

    public void refreshDate() {
        final Calendar c = Calendar.getInstance();
        c.setTime(story.date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        tbDate.setText(day +". " + (1+month) + ". " + year);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.singlemenu, menu);
        return super.onCreateOptionsMenu(menu);    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.deleteThis:
                Session.getSession().removeStory(Session.editingStory);
                Session.editingStory = null;
                Utils.Toast(this, getString(R.string.zaznamSmazan));
                Session.getSession().saveAndToast(this);
                UserInterface.switchTo(this, MainActivity.class);
                return true;
            default:
                // Save
                story.name = tbNadpis.getText().toString();
                story.text = tbContents.getText().toString();
                Session.getSession().saveAndToast(this);
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private EditStoryActivity es;
        public DatePickerFragment() {
            this.es = null;
        }
        @SuppressLint("ValidFragment")
        public  DatePickerFragment(EditStoryActivity es) {
          this.es = es;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            if (es != null) {
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                c.setTime(es.story.date);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                return new DatePickerDialog(getActivity(), this, year, month, day);
            }
            else return  new DatePickerDialog(getActivity(), this, 2015, 1,1);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            if (es != null) {
                es.story.date = c.getTime();
                es.refreshDate();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editstory);


        getActionBar().setTitle(getString(R.string.editacenanmu));
        getActionBar().setDisplayHomeAsUpEnabled(true);

         tbDate = (EditText) findViewById(R.id.tbDate);
         tbNadpis = (EditText) findViewById(R.id.tbNadpis);
         tbContents = (EditText) findViewById(R.id.tbContents);
         bChangeDate = (Button) findViewById(R.id.bChangeDate);
         Button bSaveNow = (Button) findViewById(R.id.bQuickSave);
         final EditStoryActivity es = this;
         bChangeDate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 DialogFragment newFragment = new DatePickerFragment(es);
                 newFragment.show(getFragmentManager(), "datePicker");
             }
         });

         story = Session.editingStory;

         refreshDate();
         tbNadpis.setText(story.name);
         tbContents.setText(story.text);

         final Activity context = this;
         bSaveNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                story.name = tbNadpis.getText().toString();
                story.text = tbContents.getText().toString();
                Session.getSession().saveAndToast(context);
            }
         });


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tbNadpis.setText(savedInstanceState.getString("nadpis"));
        tbContents.setText(savedInstanceState.getString("text"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("nadpis", tbNadpis.getText().toString());
        outState.putString("text", tbContents.getText().toString());
        super.onSaveInstanceState(outState);
    }
}