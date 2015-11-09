package hudecek.denik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Session.stories == null) {
            startActivity(new Intent(this, WelcomeActivity.class));
            return;
        }
        setContentView(R.layout.main);

        if (Session.searchText == null) {
            Session.searchText = "";
        }

        reloadRecords();

        filter(Session.searchText);
        Session.save(this);

        getActionBar().setTitle(Session.diaryName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        EditText tbSearch = (EditText)findViewById(R.id.tbSearch);
        tbSearch.setText(Session.searchText);
        tbSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    filter(tbSearch.getText().toString());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void filter(String search) {

            if (search == null) {
                search = "";
            }

            Session.searchText = search;

            searchResults.clear();
            for (Story story : Session.stories) {
                if (search.equals("")) {
                    searchResults.add(story);
                } else {
                    if (story.name.contains(search) || story.text.contains(search)) {
                        searchResults.add(story);
                    }
                }
            }
            if (searchResults.size() == 0) {
                searchResults.add(null);
            }
            Collections.sort(searchResults);

    }

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
                    filter( null );
                    adapter.notifyDataSetChanged();
                }
                break;
            case Utils.FILE_EXPORT:
                if (resultCode == RESULT_OK) {
                    Utils.exportFile(this, data.getData());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.newStory) {
            Story story = new Story("");
            story.date = new Date();
            story.text = "";
            Session.stories.add(story);
            EditText tbSearch = (EditText) findViewById(R.id.tbSearch);
            filter(tbSearch.getText().toString());
            adapter.notifyDataSetChanged();
        } else if (item.getItemId() == R.id.setStorageDirectory) {
            Utils.launchFileDialog(this, Utils.FILE_SELECT_CODE);
        } else if (item.getItemId() == R.id.exportAsPlaintext) {
            Utils.launchFileDialog(this, Utils.FILE_EXPORT);
        } else if (item.getItemId() == R.id.importFromPlaintext) {
            Utils.launchFileDialog(this, Utils.FILE_IMPORT);
        } else {
            // Without this, the login screen will automatically move back in here
            Session.password = "";
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    ListView lv;
    StoryAdapter adapter;
    ArrayList<Story> searchResults = new ArrayList<>();

    public void reloadRecords() {
        lv = (ListView) findViewById(R.id.lbStories);

        adapter = new StoryAdapter(this, searchResults);


        lv.setAdapter(adapter);

    }

    public void openStory(Story story) {
        if (story.text != null) {
            Session.editingStory = story;
            Intent i = new Intent(this, EditStory.class);
            startActivity(i);
        }
    }
}
    class StoryAdapter extends BaseAdapter {

        ArrayList<Story> stories = new ArrayList<>();
        MainActivity context;
        private static LayoutInflater inflater = null;

        public StoryAdapter(MainActivity context, ArrayList<Story> stories) {
            this.stories = stories;
            this.context = context;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return stories.size();
        }

        @Override
        public Object getItem(int position) {
            return stories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView;
            rowView = inflater.inflate(R.layout.row, null);
            TextView lblCaption = (TextView) rowView.findViewById(R.id.lblStoryCaption);
            TextView lblDesc = (TextView) rowView.findViewById(R.id.lblStoryDesc);
            if (stories.get(position) == null) {
                lblCaption.setText(context.getString(R.string.noMatchFound));
                lblDesc.setText("");
            }else {
                String thaname = stories.get(position).name;
                if (thaname.equals("")) thaname = context.getString(R.string.noname);

                lblCaption.setText(stories.get(position).date.getDate() + ". " + (stories.get(position).date.getMonth() + 1) + ". " + thaname);
                lblDesc.setText(stories.get(position).text);
                if (stories.get(position).text.length() == 0) {
                    lblDesc.setVisibility(View.GONE);
                } else {
                }

                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.openStory(stories.get(position));
                    }
                });
            }
            return rowView;
        }
    }
