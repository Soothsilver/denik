package hudecek.denik;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        reloadRecords();
        getActionBar().setTitle(Session.getSession().getName());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final EditText tbSearch = (EditText)findViewById(R.id.tbSearch);
        final LinearLayout layoutSearch = (LinearLayout)findViewById(R.id.layoutSearch);
        final Button bCloseSearch = (Button)findViewById(R.id.bHideSearch);
        if (Session.searchText == null) Session.searchText = "";
        layoutSearch.setVisibility(Session.searchText.isEmpty() ? View.GONE : View.VISIBLE);
        tbSearch.setText(Session.searchText);
        filter(Session.searchText);

        bCloseSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Session.searchText = "";
                tbSearch.setText("");
                filter(Session.searchText);
                layoutSearch.setVisibility(View.GONE);
            }
        });

        tbSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    filter(tbSearch.getText().toString());
                    Session.searchText = tbSearch.getText().toString();
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
            for (Story story : Session.getSession().getStories()) {
                if (search.equals("")) {
                    searchResults.add(story);
                } else {
                    if (story.matches(search)) {
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
            /*
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
                break;*/
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.newStory) {
            Story story = new Story("");
            Session.getSession().addStory(story);
            filter(Session.searchText);
            adapter.notifyDataSetChanged();
            openStory(story);
        } else if (item.getItemId() == R.id.setStorageDirectory) {
            Utils.launchFileDialog(this, Utils.FILE_SELECT_CODE);
        } else if (item.getItemId() == R.id.openSearch) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutSearch);
            layout.setVisibility(View.VISIBLE);
        } else if (item.getItemId() == R.id.changePassword) {
            UserInterface.switchTo(this, ChangePasswordActivity.class);
            /*
        } else if (item.getItemId() == R.id.exportAsPlaintext) {
            Utils.launchFileDialog(this, Utils.FILE_EXPORT);
        } else if (item.getItemId() == R.id.importFromPlaintext) {
            Utils.launchFileDialog(this, Utils.FILE_IMPORT);
            */
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
        Session.editingStory = story;
        UserInterface.switchTo(this, EditStoryActivity.class);
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

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = inflater.inflate(R.layout.row, null);
            TextView lblCaption = (TextView) rowView.findViewById(R.id.lblStoryCaption);
            TextView lblDesc = (TextView) rowView.findViewById(R.id.lblStoryDesc);
            if (stories.get(position) == null) {
                lblCaption.setText(context.getString(R.string.noMatchFound));
                lblDesc.setText("");
            }else {
                String storyName = stories.get(position).name;
                if (storyName.equals("")) storyName = context.getString(R.string.noname);
                Calendar c = Calendar.getInstance(); c.setTime(stories.get(position).date);
                lblCaption.setText(c.get(Calendar.DATE) + ". " + (c.get(Calendar.MONTH) + 1) + ". " + (c.get(Calendar.YEAR)) +" "+ storyName);
                lblDesc.setText(stories.get(position).text);
                if (stories.get(position).text.length() == 0) {
                    lblDesc.setVisibility(View.GONE);
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
