package jcw32.presoundtexture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class NewConfigurationPage extends ActionBarActivity {

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
    public Button createConfig;
    public CheckBox checkBox;

    //SharedPreferences

    //need to store arrayList of SoundConfig - user defined sound configurations
    //store integer of current config being used

    private static final String PREFS = null;             //PREFS as a filename to keep your SharedPreferences in a single location
    SharedPreferences mSharedPreferences;                 //for storing a reference to the shared preferences class

    private static final String numOfConfigs_key = "configNum";       //key for number of configurations
    public int configCounter;    //integer counter of number of configs created - collected from SharedPrefs

    private static final String currentConfig_key = "currentConfig";    //key to collect current config array index

    private static final String configArray_key = "configurationArray"; //collect serialised configurations array

    public ArrayList<SoundConfig> configArray = new ArrayList<SoundConfig>();

    public NewConfigurationPage() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_configuration_page);

        initSpinners();
        checkBox = (CheckBox) findViewById(R.id.invertCheckBox);

        createConfig = (Button) findViewById(R.id.confirmNewConfigButton);
        createConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundConfig newConfiguration = createNewConfig();    //creates new config from spinner boxes
                saveNewConfig(newConfiguration);                     //return to MainActivity with new config applied
            }
        });

        //SharedPreferences
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
//
        //collect number of configurations - needed to add new config to end of array
        String stringProfileNum = mSharedPreferences.getString(numOfConfigs_key, "");  //collect number of configs

        String currentProfileNum = mSharedPreferences.getString(currentConfig_key, "");

        if (stringProfileNum != "0" && stringProfileNum != "") {    //should never be used
            configCounter = Integer.parseInt(stringProfileNum);  //create variable of this - can only parse when number
        } else {
            configCounter = 0;  //for testing only
        }


        //load configurations from preferences
        try {
           // configArray = (ArrayList<SoundConfig>) ObjectSerializer.deserialize(mSharedPreferences.getString(configArray_key, ObjectSerializer.serialize(new ArrayList<SoundConfig>())));
            configArray = (ArrayList<SoundConfig>) ObjectSerializer.deserialize(mSharedPreferences.getString(configArray_key, ""));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_configuration_page, menu);
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


    public void initSpinners() {

        newConfigurationName = (EditText) findViewById(R.id.configNameText);

        String[] instruments = new String[] //{ "Piano", "Clarinet", "Trumpet", "Saxophone" };
                {"Acoustic Guitar", "Bass Guitar", "Bassoon", "Cello", "Clarinet", "Double Bass", "Electric Guitar", "Flute", "Harpsichord", "Marimba", "Oboe", "Organ",
                        "Piano", "Saxophone", "Sitar", "Synth", "Theremin", "Trumpet", "Tuba", "Violin", "Xylophone"};
        String[] notes = new String[]{"C", "D", "E", "F", "G", "A", "B"};

        redNotes = (Spinner) findViewById(R.id.spinnerRedNote);
        ArrayAdapter<String> rNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        redNotes.setAdapter(rNoteAdapter);

        orangeNotes = (Spinner) findViewById(R.id.spinnerOrangeNote);
        ArrayAdapter<String> oNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        orangeNotes.setAdapter(oNoteAdapter);
        orangeNotes.setSelection(1);


        yellowNotes = (Spinner) findViewById(R.id.spinnerYellowNote);
        ArrayAdapter<String> yNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        yellowNotes.setAdapter(yNoteAdapter);
        yellowNotes.setSelection(2);


        greenNotes = (Spinner) findViewById(R.id.spinnerGreenNote);
        ArrayAdapter<String> gNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        greenNotes.setAdapter(gNoteAdapter);
        greenNotes.setSelection(3);

        blueNotes = (Spinner) findViewById(R.id.spinnerBlueNote);
        ArrayAdapter<String> bNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        blueNotes.setAdapter(bNoteAdapter);
        blueNotes.setSelection(4);


        indigoNotes = (Spinner) findViewById(R.id.spinnerIndigoNote);
        ArrayAdapter<String> iNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        indigoNotes.setAdapter(iNoteAdapter);
        indigoNotes.setSelection(5);


        violetNotes = (Spinner) findViewById(R.id.spinnerVioletNote);
        ArrayAdapter<String> vNoteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, notes);
        violetNotes.setAdapter(vNoteAdapter);
        violetNotes.setSelection(6);


//set the default according to value

        //instruments
        redInstruments = (Spinner) findViewById(R.id.spinnerRedInstrument);
        ArrayAdapter<String> rInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        redInstruments.setAdapter(rInstruAdapter);

        orangeInstruments = (Spinner) findViewById(R.id.spinnerOrangeInstrument);
        ArrayAdapter<String> oInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        orangeInstruments.setAdapter(oInstruAdapter);

        yellowInstruments = (Spinner) findViewById(R.id.spinnerYellowInstrument);
        ArrayAdapter<String> yInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        yellowInstruments.setAdapter(yInstruAdapter);

        greenInstruments = (Spinner) findViewById(R.id.spinnerGreenInstrument);
        ArrayAdapter<String> gInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        greenInstruments.setAdapter(gInstruAdapter);

        blueInstruments = (Spinner) findViewById(R.id.spinnerBlueInstrument);
        ArrayAdapter<String> bInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        blueInstruments.setAdapter(bInstruAdapter);

        indigoInstruments = (Spinner) findViewById(R.id.spinnerIndigoInstrument);
        ArrayAdapter<String> iInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        indigoInstruments.setAdapter(iInstruAdapter);

        violetInstruments = (Spinner) findViewById(R.id.spinnerVioletInstrument);
        ArrayAdapter<String> vInstruAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, instruments);
        violetInstruments.setAdapter(vInstruAdapter);
    }

    /*
    collects info from spinners and create a new config class
     */
    public SoundConfig createNewConfig() {

        SoundConfig newSoundConfig = new SoundConfig(newConfigurationName.getText().toString());  //create config and set name


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

        newSoundConfig.setInstruments(instruments);
        newSoundConfig.setNotes(notes);

        if (checkBox.isChecked()) {                 //if checked set true
            newSoundConfig.setInvertBrightness(true);
        }

        //now config has been created, return it
        return newSoundConfig;
    }

    public String decapitaliseString(String inputString) {
        char inputChar[] = inputString.toCharArray();
        inputChar[0] = Character.toLowerCase(inputChar[0]);
        return new String(inputChar);
    }

    /*
    Adds new config to configs array.
     */
    public void saveNewConfig(SoundConfig newSoundConfig) {

        //add one to num of profiles - update configCounter
        configCounter++; //update config counter

        //add name to SharedPreferences, add to memory & commit
        SharedPreferences.Editor e = mSharedPreferences.edit();

        //update NumberOfConfigs
        e.putString(numOfConfigs_key, Integer.toString(configCounter));
        e.commit();


        //update current config
        e.putString(currentConfig_key, Integer.toString(configCounter));  //store newly created config - store array number
        e.commit();

        //add new config to config array         //seralise config array and update SharedPreferences
        addConfig(newSoundConfig);

        //return to MainActivity
        Intent changeToConfig = new Intent(NewConfigurationPage.this, MainActivity.class);
        startActivity(changeToConfig);
    }

    public void addConfig(SoundConfig c) {

        if (null == configArray) {
            configArray = new ArrayList<SoundConfig>();
        }
        configArray.add(c);

        //save the task list to preference
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        try {
            editor.putString(configArray_key, ObjectSerializer.serialize(configArray));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }
}

 class ObjectSerializer {


    public static String serialize(Serializable obj) throws IOException {
        if (obj == null) return "";
        try {
            ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
            objStream.writeObject(obj);
            objStream.close();
            return encodeBytes(serialObj.toByteArray());
        } catch (Exception e) {
            //throw WrappedIOException.wrap("Serialization error: " + e.getMessage(), e);
        }
        return "";
    }

    public static Object deserialize(String str) throws IOException {
        if (str == null || str.length() == 0) return null;
        try {
            ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
            ObjectInputStream objStream = new ObjectInputStream(serialObj);
            return objStream.readObject();
        } catch (Exception e) {
            //throw WrappedIOException.wrap("Deserialization error: " + e.getMessage(), e);
        }
        return new Object();
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    public static byte[] decodeBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i+=2) {
            char c = str.charAt(i);
            bytes[i/2] = (byte) ((c - 'a') << 4);
            c = str.charAt(i+1);
            bytes[i/2] += (c - 'a');
        }
        return bytes;
    }

}