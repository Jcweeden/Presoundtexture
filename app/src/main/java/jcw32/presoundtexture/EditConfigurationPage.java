package jcw32.presoundtexture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;


public class EditConfigurationPage extends ActionBarActivity {

    public Spinner redInstruments;
    public Spinner redNotes;
    public Spinner orangeInstruments;
    public Spinner orangeNotes;
    public Spinner yellowInstruments;
    public Spinner yellowNotes;
    public Spinner greenInstruments;
    public Spinner greenNotes;
    public Spinner blueInstruments;
    public Spinner blueNotes;
    public Spinner indigoInstruments;
    public Spinner indigoNotes;
    public Spinner violetInstruments;
    public Spinner violetNotes;
    public EditText newConfigurationName;
    public Button editConfig;
    public CheckBox checkBox;


    private static final String PREFS = null;             //PREFS as a filename to keep your SharedPreferences in a single location
    SharedPreferences mSharedPreferences;                 //for storing a reference to the shared preferences class

    private static final String numOfConfigs_key = "configNum";       //key for number of configurations

    private static final String currentConfig_key = "currentConfig";    //key to collect current config array index

    private static final String configArray_key = "configurationArray"; //collect serialised configurations array

    public ArrayList<SoundConfig> configArray = new ArrayList<SoundConfig>();

    public int currentConfigIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_configuration_page);

        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);         //init SharedPreferences
        currentConfigIndex = Integer.parseInt(mSharedPreferences.getString(currentConfig_key, ""));

        editConfig = (Button) findViewById(R.id.confirmEditConfigButton);
        editConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editConfiguration();    //edits the old config in configArray using info from spinner boxes. Then saves it
                //return to home screen
            }
        });
        newConfigurationName = (EditText) findViewById(R.id.configNameText);
        checkBox = (CheckBox) findViewById(R.id.invertCheckBox);

        //load configurations from preferences
        try {
            // configArray = (ArrayList<SoundConfig>) ObjectSerializer.deserialize(mSharedPreferences.getString(configArray_key, ObjectSerializer.serialize(new ArrayList<SoundConfig>())));
            configArray = (ArrayList<SoundConfig>) ObjectSerializer.deserialize(mSharedPreferences.getString(configArray_key, ""));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
      initSpinners();

        //currentConfigIndex = Integer.parseInt(mSharedPreferences.getString(currentConfig_key,""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_configuration_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void editConfiguration() {

        //collect information from spinners

        String[] instruments = new String[8]; //create array to store instruments //decapitalise and remove whitespace

        instruments[1] = decapitaliseString(redInstruments.getSelectedItem().toString());
        instruments[1] = instruments[1].replaceAll("\\s", "");
        instruments[2] = decapitaliseString(orangeInstruments.getSelectedItem().toString());
        instruments[2] = instruments[2].replaceAll("\\s", "");
        instruments[3] = decapitaliseString(yellowInstruments.getSelectedItem().toString());
        instruments[3] = instruments[3].replaceAll("\\s", "");
        instruments[4] = decapitaliseString(greenInstruments.getSelectedItem().toString());
        instruments[4] = instruments[4].replaceAll("\\s", "");
        instruments[5] = decapitaliseString(blueInstruments.getSelectedItem().toString());
        instruments[5] = instruments[5].replaceAll("\\s", "");
        instruments[6] = decapitaliseString(indigoInstruments.getSelectedItem().toString());
        instruments[6] = instruments[6].replaceAll("\\s", "");
        instruments[7] = decapitaliseString(violetInstruments.getSelectedItem().toString());
        instruments[7] = instruments[7].replaceAll("\\s", "");


        String[] notes = new String[8]; //create array to store notes

        notes[1] = decapitaliseString(redNotes.getSelectedItem().toString());
        notes[2] = decapitaliseString(orangeNotes.getSelectedItem().toString());
        notes[3] = decapitaliseString(yellowNotes.getSelectedItem().toString());
        notes[4] = decapitaliseString(greenNotes.getSelectedItem().toString());
        notes[5] = decapitaliseString(blueNotes.getSelectedItem().toString());
        notes[6] = decapitaliseString(indigoNotes.getSelectedItem().toString());
        notes[7] = decapitaliseString(violetNotes.getSelectedItem().toString());


        //edit configArray with new information

        if (checkBox.isChecked() == true) {                 //if checked set true
            configArray.get(Integer.parseInt(mSharedPreferences.getString("currentConfig", ""))).setInvertBrightness(true);
        }
        else {configArray.get(Integer.parseInt(mSharedPreferences.getString("currentConfig", ""))).setInvertBrightness(false);}

        configArray.get(Integer.parseInt(mSharedPreferences.getString("currentConfig", ""))).setInstruments(instruments);
        configArray.get(Integer.parseInt(mSharedPreferences.getString("currentConfig", ""))).setNotes(notes);
        configArray.get(Integer.parseInt(mSharedPreferences.getString("currentConfig", ""))).setName(newConfigurationName.getText().toString());


        //serialise configuration

        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        try {
            editor.putString(configArray_key, ObjectSerializer.serialize(configArray));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();

        //return to main Activity
        Intent returnToMain = new Intent(EditConfigurationPage.this, MainActivity.class);
        startActivity(returnToMain);
    }

    public String decapitaliseString(String inputString) {
        char inputChar[] = inputString.toCharArray();
        inputChar[0] = Character.toLowerCase(inputChar[0]);
        return new String(inputChar);
    }

    public void initSpinners() {

        newConfigurationName = (EditText) findViewById(R.id.configNameText);

        newConfigurationName.setText(configArray.get(currentConfigIndex).getName());

        String[] instruments = new String[] //{ "Piano", "Clarinet", "Trumpet", "Saxophone" };
                {"Acoustic Guitar", "Bass Guitar", "Bassoon", "Cello", "Clarinet", "Double Bass", "Electric Guitar", "Flute", "Harpsichord", "Marimba", "Oboe", "Organ",
                        "Piano", "Saxophone", "Sitar", "Synth", "Theremin", "Trumpet", "Tuba", "Violin", "Xylophone"};
        String[] notes = new String[]{"C", "D", "E", "F", "G", "A", "B"};

        redNotes = (Spinner) findViewById(R.id.spinnerRedNote);
        ArrayAdapter<String> rNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        redNotes.setAdapter(rNoteAdapter);
        redNotes.setSelection(configArray.get(currentConfigIndex).getNoteIndex(1)-1);


            //get note from array
        //pass into get noteIndex

        orangeNotes = (Spinner) findViewById(R.id.spinnerOrangeNote);
        ArrayAdapter<String> oNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        orangeNotes.setAdapter(oNoteAdapter);
        orangeNotes.setSelection(configArray.get(currentConfigIndex).getNoteIndex(2)-1);

        yellowNotes = (Spinner) findViewById(R.id.spinnerYellowNote);
        ArrayAdapter<String> yNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        yellowNotes.setAdapter(yNoteAdapter);
        yellowNotes.setSelection(configArray.get(currentConfigIndex).getNoteIndex(3)-1);

        greenNotes = (Spinner) findViewById(R.id.spinnerGreenNote);
        ArrayAdapter<String> gNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        greenNotes.setAdapter(gNoteAdapter);
        greenNotes.setSelection(configArray.get(currentConfigIndex).getNoteIndex(4)-1);

        blueNotes = (Spinner) findViewById(R.id.spinnerBlueNote);
        ArrayAdapter<String> bNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        blueNotes.setAdapter(bNoteAdapter);
        blueNotes.setSelection(configArray.get(currentConfigIndex).getNoteIndex(5)-1);

        indigoNotes = (Spinner) findViewById(R.id.spinnerIndigoNote);
        ArrayAdapter<String> iNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        indigoNotes.setAdapter(iNoteAdapter);
        indigoNotes.setSelection(configArray.get(currentConfigIndex).getNoteIndex(6)-1);

        violetNotes = (Spinner) findViewById(R.id.spinnerVioletNote);
        ArrayAdapter<String> vNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        violetNotes.setAdapter(vNoteAdapter);
        violetNotes.setSelection(configArray.get(currentConfigIndex).getNoteIndex(7)-1);


//set the default according to value

        //instruments
        redInstruments = (Spinner) findViewById(R.id.spinnerRedInstrument);
        ArrayAdapter<String> rInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        redInstruments.setAdapter(rInstruAdapter);
        redInstruments.setSelection(configArray.get(currentConfigIndex).getInstrumentIndex(1)-1);

        orangeInstruments = (Spinner) findViewById(R.id.spinnerOrangeInstrument);
        ArrayAdapter<String> oInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        orangeInstruments.setAdapter(oInstruAdapter);
        orangeInstruments.setSelection(configArray.get(currentConfigIndex).getInstrumentIndex(2)-1);

        yellowInstruments = (Spinner) findViewById(R.id.spinnerYellowInstrument);
        ArrayAdapter<String> yInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        yellowInstruments.setAdapter(yInstruAdapter);
        yellowInstruments.setSelection(configArray.get(currentConfigIndex).getInstrumentIndex(3)-1);

        greenInstruments = (Spinner) findViewById(R.id.spinnerGreenInstrument);
        ArrayAdapter<String> gInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        greenInstruments.setAdapter(gInstruAdapter);
        greenInstruments.setSelection(configArray.get(currentConfigIndex).getInstrumentIndex(4)-1);

        blueInstruments = (Spinner) findViewById(R.id.spinnerBlueInstrument);
        ArrayAdapter<String> bInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        blueInstruments.setAdapter(bInstruAdapter);
        blueInstruments.setSelection(configArray.get(currentConfigIndex).getInstrumentIndex(5)-1);

        indigoInstruments = (Spinner) findViewById(R.id.spinnerIndigoInstrument);
        ArrayAdapter<String> iInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        indigoInstruments.setAdapter(iInstruAdapter);
        indigoInstruments.setSelection(configArray.get(currentConfigIndex).getInstrumentIndex(6)-1);

        violetInstruments = (Spinner) findViewById(R.id.spinnerVioletInstrument);
        ArrayAdapter<String> vInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        violetInstruments.setAdapter(vInstruAdapter);
        violetInstruments.setSelection(configArray.get(currentConfigIndex).getInstrumentIndex(7)-1);

        //set checkbox to correct value
        configArray.get(currentConfigIndex).getInvertBrightnessValue();

        checkBox.setChecked(configArray.get(currentConfigIndex).getInvertBrightnessValue());

    }

    /*
    Set the spinners to the already designated information as contained in configArray.
     */
    public void setSpinners(){

    }
}
