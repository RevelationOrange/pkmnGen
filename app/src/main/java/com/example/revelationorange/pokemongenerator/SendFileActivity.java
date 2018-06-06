package com.example.revelationorange.pokemongenerator;

import android.content.Intent;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendFileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    String sendfname = "";
    //    File sDir = MainActivity.saveDir;
    File sDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        Intent i = getIntent();

        String sDirName = i.getStringExtra("fileLoc");
        sDir = new File(sDirName);

        Spinner fileListDropdown = findViewById(R.id.spinnerFileList);
        List<String> csvList = new ArrayList<>(Arrays.asList(sDir.list()));
        // sort here, maybe
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, csvList);
        fileListDropdown.setAdapter(adapter);
        fileListDropdown.setOnItemSelectedListener(this);
    }

    public void sendPokemon(View v) {
        System.out.println(sendfname);
        File f = new File(sDir + File.separator + sendfname);
        Intent sendIntent = new Intent();
        sendIntent.setType("application/csv");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(SendFileActivity.this, "com.example.revelationorange.pokemongenerator.fileprovider", f));
        startActivity(sendIntent);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
        sendfname = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
