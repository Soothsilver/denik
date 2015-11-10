package hudecek.denik;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SelectDirectoryActivity extends Activity {
    private File selectedFolder;
    private ArrayList<String> files = new ArrayList<>();
    private Activity context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectdir);
        context = this;

        selectedFolder = Diaries.getDirectory(this);
        final ListView lv  = (ListView)findViewById(R.id.lbDirectories);
        loadFromFolder();

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, files);
        lv.setAdapter(adapter);
        Button bSelect = (Button)findViewById(R.id.bChooseThisFolder);
        Button bUp = (Button)findViewById(R.id.bNahoru);
        bUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File parent = selectedFolder.getParentFile();
                if (parent != null) {
                    selectedFolder = parent;
                    loadFromFolder();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) lv.getItemAtPosition(position);
                File file = new File(selectedFolder, name);
                if (file.isDirectory()) {
                    if (file.listFiles() != null) {
                        selectedFolder = file;
                        loadFromFolder();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        bSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFolder.isDirectory()) {
                    try {
                        File randomFile = new File(selectedFolder, "diarystorage.placeholder");
                        randomFile.createNewFile();
                        randomFile.delete();
                        Diaries.setDirectory(context, selectedFolder);
                        UserInterface.switchTo(context, WelcomeActivity.class);
                    } catch (IOException ex) {

                    }
                }
            }
        });
    }

    private void loadFromFolder() {
        files.clear();
        for (File file : selectedFolder.listFiles()) {
            if (file.isDirectory()) {
                files.add(file.getName());
            }
        }
        for (File file : selectedFolder.listFiles()) {
            if (!file.isDirectory()) {
                files.add("Soubor: " + file.getName());
            }
        }
        getActionBar().setTitle(selectedFolder.getName());
        Button bUp = (Button)findViewById(R.id.bNahoru);
        bUp.setEnabled(selectedFolder.getParentFile() != null);
        Button bSelect = (Button)findViewById(R.id.bChooseThisFolder);
        try {
            File randomFile = new File(selectedFolder, "diarystorage.placeholder");
            randomFile.createNewFile();
            randomFile.delete();
            bSelect.setEnabled(true);
        } catch (IOException ex) {
            bSelect.setEnabled(false);
        }
    }
}