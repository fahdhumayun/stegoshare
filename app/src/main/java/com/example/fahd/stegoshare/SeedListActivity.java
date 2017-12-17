// By Fahd Humayun

package com.example.fahd.stegoshare;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

public class SeedListActivity extends AppCompatActivity {

    ArrayList<String> seedArrayList;
    int user_selected_shares_n;

    private ListView wordsListView;
    private CustomAdapter ca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle("  Stegoshare");
        getSupportActionBar().setSubtitle("    Hide Seed List - Step 1: Input words list");

        seedArrayList = new ArrayList<String>(getIntent().getStringArrayListExtra("seedArrayList"));
        user_selected_shares_n = getIntent().getIntExtra("user_selected_shares_n", -1);

        wordsListView = (ListView) findViewById(R.id.id_wordsListView);

        ca = new CustomAdapter(this, seedArrayList);

        wordsListView.setAdapter(ca);

        wordsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l){

                showAlertDialog(i);

                return true;
            }
        });

    }

    private void showAlertDialog(final int i){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText wordEditText = new EditText(this);
        alert.setMessage("Enter new word.");
        alert.setTitle("Change word");

        alert.setView(wordEditText);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String word = wordEditText.getText().toString();
                seedArrayList.set(i, word);
                ca = new CustomAdapter(SeedListActivity.this, seedArrayList);
                wordsListView.setAdapter(ca);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }


}
