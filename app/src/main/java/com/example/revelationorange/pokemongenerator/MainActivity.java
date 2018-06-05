package com.example.revelationorange.pokemongenerator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
//import android.os.Build;
//import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
//    public static final String EXTRA_MESSAGE = "com.example.mythirdfirstapp.MESSAGE";
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 7;
    public Pkmn usrPkmn;
    public File saveDir;
    private Spinner lvlsListDropdown;
    private Integer enteredLvl = 50;
    public String lastSavedNick = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvlsListDropdown = findViewById(R.id.spinnerLvlsList);

        List<Integer> lvlsList = new ArrayList<>();
        for (int i = 0; i < 100; i++) { lvlsList.add(i+1); }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, lvlsList);
        lvlsListDropdown.setAdapter(adapter);
        lvlsListDropdown.setOnItemSelectedListener(this);

        // setup the touch listener to close keyboard when touched off
        setupUI(findViewById(R.id.parent));

        // build the directory name and make the File of it
//        dirStrings.add(getPublicStorageDir("PKMN").toString());
//        dirStrings.add("PKMN");
        saveDir = new File(getPublicStorageDir("PKMN").toString());

        try {
            Pkmn.setup(getResources().openRawResource(R.raw.basestats), getResources().openRawResource(R.raw.natures), getResources().openRawResource(R.raw.moves));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public File getPublicStorageDir(String dirname) {
        // Get the directory for the user's public documents directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), dirname);
        if (!file.mkdirs()) {
            System.out.println("Directory not created");
        }
        return file;
    }


//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void checkSpecies(View view) {
        EditText spcInput = findViewById(R.id.editTextSpcInput);
        String spc = spcInput.getText().toString().toLowerCase();
        TextView errorBox = findViewById(R.id.textViewErrors);
        if (Pkmn.chkSpc(spc)) {
            hideSoftKeyboard(this, true);
            errorBox.setText("");
            generate(spc);
            EditText nick = findViewById(R.id.editTextNickname);
            nick.setVisibility(View.VISIBLE);
            Button save = (Button) findViewById(R.id.buttonSave);
            save.setVisibility(View.VISIBLE);
        }
        else {
            errorBox.setText(R.string.errSpcNotFound);
            hideSoftKeyboard(this, false);
            EditText nick = findViewById(R.id.editTextNickname);
            nick.setVisibility(View.INVISIBLE);
            Button save = (Button) findViewById(R.id.buttonSave);
            save.setVisibility(View.INVISIBLE);
        }
//        tmp();
    }

    protected void generate(String spc) {
        usrPkmn = new Pkmn(spc, enteredLvl);
        List<Integer> stats = usrPkmn.getStats();
        List<Integer> ivs = usrPkmn.getIVs();

        TextView statBox = findViewById(R.id.textViewHP);
        statBox.setText(String.format(stats.get(0).toString()));
        statBox = findViewById(R.id.textViewATK);
        statBox.setText(String.format(stats.get(1).toString()));
        statBox = findViewById(R.id.textViewDEF);
        statBox.setText(String.format(stats.get(2).toString()));
        statBox = findViewById(R.id.textViewSPATK);
        statBox.setText(String.format(stats.get(3).toString()));
        statBox = findViewById(R.id.textViewSPDEF);
        statBox.setText(String.format(stats.get(4).toString()));
        statBox = findViewById(R.id.textViewSPD);
        statBox.setText(String.format(stats.get(5).toString()));

        statBox = findViewById(R.id.textViewHPIV);
        statBox.setText(String.format(ivs.get(0).toString()));
        statBox = findViewById(R.id.textViewATKIV);
        statBox.setText(String.format(ivs.get(1).toString()));
        statBox = findViewById(R.id.textViewDEFIV);
        statBox.setText(String.format(ivs.get(2).toString()));
        statBox = findViewById(R.id.textViewSPATKIV);
        statBox.setText(String.format(ivs.get(3).toString()));
        statBox = findViewById(R.id.textViewSPDEFIV);
        statBox.setText(String.format(ivs.get(4).toString()));
        statBox = findViewById(R.id.textViewSPDIV);
        statBox.setText(String.format(ivs.get(5).toString()));

        statBox = findViewById(R.id.textViewNature);
        statBox.setText(usrPkmn.getNature());

        List<String> moves = usrPkmn.getMoves();
        List<TextView> moveBoxes = new ArrayList<>();
        moveBoxes.add((TextView) findViewById(R.id.textViewMove0));
        moveBoxes.add((TextView) findViewById(R.id.textViewMove1));
        moveBoxes.add((TextView) findViewById(R.id.textViewMove2));
        moveBoxes.add((TextView) findViewById(R.id.textViewMove3));
        for (int i = 0; i < moveBoxes.size(); i++) {
            moveBoxes.get(i).setText("");
        }
        for (int i = 0; i < moves.size(); i++) {
            moveBoxes.get(i).setText(moves.get(i));
        }
    }

    public void savePkmnStats(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {

            //
            EditText nick = findViewById(R.id.editTextNickname);
            String nickname = nick.getText().toString();
            if (nickname.length() <= 0) {
                nickname = usrPkmn.getSpecies();
            }
            List<String> x = new ArrayList<>();
            x.add(nickname);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            if (!usrPkmn.isSaved() || !nickname.equals(lastSavedNick)) {
                String fname;
                int id = 0;
                while (new File(saveDir + File.separator + nickname + id + ".csv").exists()) {
                    id++;
                }
                fname = nickname + id + ".csv";
                File outf0 = new File(saveDir.toString() + File.separator + fname);
                CSVBuilder csvString = new CSVBuilder();
                try {
                    FileWriter fw = new FileWriter(outf0);

                    List<Integer> stats = usrPkmn.getStats();
                    List<Integer> IVs = usrPkmn.getIVs();
                    List<Integer> EVs = usrPkmn.getEVs();
                    List<String> moves = usrPkmn.getMoves();
                    String[] statNames = Pkmn.si;

                    csvString.addLine(usrPkmn.getSpecies(), usrPkmn.getNature(), "nature", "lv " + usrPkmn.getLv());
                    csvString.addLine("", "stat", "IV", "EV");
                    for (int i = 0; i < 6; i++) {
                        csvString.addLine(statNames[i], stats.get(i).toString(),
                                IVs.get(i).toString(), EVs.get(i).toString());
                    }
                    csvString.addLine("moves", TextUtils.join(",", moves));

                    fw.write(csvString.getStr());
                    usrPkmn.setSaved();
                    lastSavedNick = nickname;
                    fw.flush();
                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // print 'this pkmn has already been saved (as [].csv)'
                Toast.makeText(MainActivity.this, "this pkmn has already been saved", Toast.LENGTH_SHORT).show();
            }
            /*
             */
        }
    }

    public void startSendActivity(View v) {
        Intent i = new Intent(this, SendFileActivity.class);
        i.putExtra("fileLoc", saveDir.toString());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else { startActivity(i); }
    }

    // straight from stackoverflow
    //
    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this, true);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
    //

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    public static void hideSoftKeyboard (Activity act, boolean hide) {
        InputMethodManager inputManager = (InputMethodManager)
                act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hide) {
            inputManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        else {
            inputManager.showSoftInputFromInputMethod(act.getCurrentFocus().getWindowToken(),
                    0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        enteredLvl = Integer.parseInt(parent.getItemAtPosition(pos).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
