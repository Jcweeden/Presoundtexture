package jcw32.presoundtexture;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements TextureView.SurfaceTextureListener {

    //camera
    private TextureView texturePreview;
    private Camera mCamera;
    public Bitmap textureSingleFrame;   //single frame of the texturePreview

    //GUI
    public TextView averageColour;  //contains the primary colour name that the texturePreview sound averages to
    public TextView colourDescrip;
    public TextView framesPerSoundText; //textView that displays the amount of frames per sound
    public TextView rValue;
    public TextView gValue;
    public TextView bValue;
    public TextView volumeTextField;
    public SeekBar framesPerSoundSeekBar;  //allows user to set how many frames before a sound plays
    public Button muteButton;
    public Button changeConfigBut;
    public Button newConfigBut;
    public Button editConfigButton;


    //SoundPool stuff
    private SoundPool soundPool;

    boolean loaded = false;
    float actVolume, maxVolume, volume;
    AudioManager audioManager;

    int framesRun = 0;    //counter of frames passed
    float volumeLevel;  //calculated from the perceived brightness of the colour
    HashMap<String, Integer> soundPoolResourceID = new HashMap<>(); //key to access instrument resourceIDs

    //Colour stuff
    int numOfColours = 7;
    int bestMatchIndex;
    ArrayList<ColourName> colourList = new ArrayList<>();   //arrayList of 150 colours with rgb values


    //Configuration Settings
    String[] colourInstruments = new String[8];     //contains representative instrument for each primary colour - 0 not used
    String[] colourNote = new String[8];            //contains representative note for each primary colour - 0 not used
    int frameLimiter = 30;                          //set number of frames before sound plays
    boolean muted = false;
    public ArrayList<SoundConfig> configArray = new ArrayList<SoundConfig>(); //loaded from SharedPreferences


    //SharedPreferences

    private static final String PREFS = null;             //PREFS as a filename to keep your SharedPreferences in a single location
    SharedPreferences mSharedPreferences;                 //for storing a reference to the shared preferences class

    public int currentConfigIndex;    //integer index of current configs

    private static final String currentConfig_key = "currentConfig";    //key to collect current config array index
    private static final String configArray_key = "configurationArray"; //collect serialised configurations array
    private static final String seeker_key = "seekerprogress";          //collect saved seekBar value


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        texturePreview = new TextureView(this);
        texturePreview = (TextureView) findViewById(R.id.texturePreview);
        texturePreview.setSurfaceTextureListener(this);
        averageColour = (TextView) findViewById(R.id.averageColourDisplay); //displays block of colour and its name
        colourDescrip = (TextView) findViewById(R.id.colourDescription);    //displays the average primary colour
        framesPerSoundSeekBar = (SeekBar) findViewById(R.id.seekBarFps);    //seekbar to adjust how many camera frames are required to play a sound
        framesPerSoundText = (TextView) findViewById(R.id.refreshText); //text displaying how many frames per sound
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        String savedProgress = mSharedPreferences.getString(seeker_key, "");

        if (!savedProgress.equals("") && !savedProgress.equals(0)) {
            framesPerSoundSeekBar.setProgress(Integer.parseInt(mSharedPreferences.getString(seeker_key,"")));                 //change seeker to saved value
            frameLimiter = Integer.parseInt(mSharedPreferences.getString(seeker_key,""));
            int savedProgressInt = Integer.parseInt(savedProgress); savedProgressInt++;
           // mSharedPreferences.getString(seeker_key,"");
            if (frameLimiter <= 9) {
                framesPerSoundText.setText("Camera Frames per Sound:   " + savedProgressInt);
            } else {
                framesPerSoundText.setText("Camera Frames per Sound: " + savedProgressInt);
            }
        }
        muteButton = (Button) findViewById(R.id.muteButton);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muteUnmuteButton();
            }
        });


        changeConfigBut = (Button) findViewById(R.id.changeConfigButton);
        changeConfigBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change page
                Intent changeToConfig = new Intent(MainActivity.this, ConfigurationPage.class);
                startActivity(changeToConfig);
            }
        });

        newConfigBut = (Button) findViewById(R.id.newConfigButton);
        newConfigBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change page
                Intent changeToNewConfig = new Intent(MainActivity.this, NewConfigurationPage.class);
                startActivity(changeToNewConfig);
            }
        });


        editConfigButton = (Button) findViewById(R.id.editConfigButton);
        editConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change page
                Intent changeToEditConfig = new Intent(MainActivity.this, EditConfigurationPage.class);
                startActivity(changeToEditConfig);
            }
        });

        framesPerSoundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frameLimiter = progress + 1;
                framesRun = 0;

                SharedPreferences.Editor e = mSharedPreferences.edit();                 //save to mSharedPreferences
                e.putString(seeker_key, Integer.toString(progress));
                e.commit();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (frameLimiter <= 9) {
                    framesPerSoundText.setText("Camera Frames per Sound:   " + frameLimiter);
                } else {
                    framesPerSoundText.setText("Camera Frames per Sound: " + frameLimiter);
                }
    //mSharedPreferences.//frameLimiterSaved
            }
        });


        //collect values from SharedPreferences
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        String currentProfileNum = mSharedPreferences.getString(currentConfig_key, "");

        if (currentProfileNum != "") {
            currentConfigIndex = Integer.parseInt(mSharedPreferences.getString(currentConfig_key, ""));
        }
        //load configs from preferences
        try {
            configArray = (ArrayList<SoundConfig>) ObjectSerializer.deserialize(mSharedPreferences.getString(configArray_key, ""/*ObjectSerializer.serialize(new ArrayList<SoundConfig>()))*/));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (configArray == null || configArray.size() == 0) { //create default config if no other profiles exist
            SoundConfig newSoundConfig = new SoundConfig("Default Configuration");  //make new SoundConfig class, instruments inside initialised to default
            configArray = new ArrayList<SoundConfig>();//init array
            configArray.add(newSoundConfig);    //add to configArray

            SharedPreferences.Editor editor = mSharedPreferences.edit();

            try {
                editor.putString(configArray_key, ObjectSerializer.serialize(configArray));            //save to SharedPreferences
            } catch (IOException e) {
                e.printStackTrace();
            }
            editor.commit();

            int configCounter = 0; //update config counter

            //update NumberOfConfigs
            editor.putString("configNum", Integer.toString(configCounter));
            editor.commit();
            //update current config
            editor.putString(currentConfig_key, Integer.toString(configCounter));  //store newly created config - store array number
            editor.commit();
        }
        initConfigFromSaved();  //next load the initilaisation from SharedPreferences, whether new or old

        Toast.makeText(MainActivity.this, "Config loaded: " + configArray.get(currentConfigIndex).getName(),
                Toast.LENGTH_LONG).show();


        rValue = (TextView) findViewById(R.id.rValue);
        gValue = (TextView) findViewById(R.id.gValue);
        bValue = (TextView) findViewById(R.id.bValue);
        volumeTextField = (TextView) findViewById(R.id.volumeLevel);


        //SoundPool stuff//////////////////////////////////
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Load the sounds
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        initSoundsFromConfig();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
                                          int arg2) {
        mCamera = Camera.open();
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        texturePreview.setLayoutParams(new FrameLayout.LayoutParams(
                previewSize.width, previewSize.height, Gravity.CENTER));
        try {
            mCamera.setPreviewTexture(arg0);
        } catch (IOException t) {
        }
        mCamera.startPreview();
        texturePreview.setAlpha(1.0f);
        texturePreview.setRotation(90.0f);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
                                            int arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
        // TODO Auto-generated method stub


        textureSingleFrame = texturePreview.getBitmap();

        int[] rArray = new int[49]; //work out central 49x49 area
        int[] gArray = new int[49];
        int[] bArray = new int[49];

        int pixel;              //individual pixel to collect rgb
        int rowSize = 49;
        int colSize = 49;


        for (int col = 0; col < colSize; col++) {
            for (int row = 0; row < rowSize; row++) {
                pixel = textureSingleFrame.getPixel(270 + col, 220 + row);
                rArray[row] = Color.red(pixel);
                bArray[row] = Color.blue(pixel);
                gArray[row] = Color.green(pixel);
                ;
            }           //cut down to just taking pixel val and adding it to total/49 to get average
        }

        int rTotal = 0;
        int gTotal = 0;
        int bTotal = 0;

        for (int k = 0; k < rowSize; k++) {
            rTotal = rTotal + rArray[k];
            gTotal = gTotal + gArray[k];
            bTotal = bTotal + bArray[k];
        }

        int rAverage = rTotal / rowSize;
        int gAverage = gTotal / rowSize;
        int bAverage = bTotal / rowSize;


        if (framesRun == frameLimiter) {  //variable to change how often sound plays - for different activities - looking at painting
            String frameColourName = getColourNameFromRgb(rAverage, gAverage, bAverage);        //method to collect colour name and classification

            volumeLevel = calculateVolumeFromBrightness(rAverage, gAverage, bAverage);          //use rgb to work out perceived brightness and therefore volume

            displayAverageColour(rAverage, gAverage, bAverage);                                 //show average colour in textView on screen
            displayColourAndRgb(frameColourName, rAverage, gAverage, bAverage, volumeLevel);    //display colour name & rgb text values, plus volume level

            int colourDescription = getColourLabel(bestMatchIndex);                             //calculates which primary shade colour belongs to e.g. 1 (red)

            displayColourDescription(colourDescription);                                        //displays average colour name

            getInstrumentAndNote(colourDescription, volumeLevel);                               //plays instrument & notes associated with colour, at calculated volume level

            framesRun = 0;  //reset the counter
        } else {
            framesRun++;
        }
    }

    //GUI functions ////////////////////////

    void displayAverageColour(int red, int green, int blue) {
        averageColour.setBackgroundColor(Color.rgb(red, green, blue));
    }

    void displayColourAndRgb(String colourName, int red, int green, int blue, float volumeLevel) {
        averageColour.setText(colourName);  //places name in textbox - e.g. 'slateGray'
        rValue.setText("r:  " + red);         //set R G B boxes
        gValue.setText("g:  " + green);
        bValue.setText("b:  " + blue);

        String volumeTwoDP = String.format("%.2f", volumeLevel); //set volume to 2dp

        volumeTextField.setText("vol: " + volumeTwoDP);        //show volume level
    }

    void displayColourDescription(int colourLabel) {
        String colourLabelString = "";

        if (colourLabel == 1) {
            colourLabelString = "Average Colour: Violet";
        } else if (colourLabel == 2) {
            colourLabelString = "Average Colour: Indigo";
        } else if (colourLabel == 3) {
            colourLabelString = "Average Colour: Blue";
        } else if (colourLabel == 4) {
            colourLabelString = "Average Colour: Green";
        } else if (colourLabel == 5) {
            colourLabelString = "Average Colour: Yellow";
        } else if (colourLabel == 6) {
            colourLabelString = "Average Colour: Orange";
        } else if (colourLabel == 7) {
            colourLabelString = "Average Colour:" + "\n" + " Red";
        } else {
            colourLabelString = "Average Colour:" + "\n" + " Black";
        }

        //if no colour is found, will be set to "Black"
        colourDescrip.setText(colourLabelString);
    }

    /*
    Applied to onClickListener for muteButton.
     */
    public void muteUnmuteButton() {
        if (muted) {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            muted = false;
            muteButton.setText("Mute");
            Toast.makeText(MainActivity.this, "Application Unmuted",
                    Toast.LENGTH_SHORT).show();
        } else {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            muted = true;
            muteButton.setText("Unmute");
            Toast.makeText(MainActivity.this, "Application Muted",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Audio Methods ////////////////////////////////

    /*
    Initialises the sound based on those selected in the configuration.
     */
    public void initSoundsFromConfig() {
        for (int i = 0; i < 7; i++) {
            String selectedInstrumentNote = colourInstruments[i + 1] + colourNote[i + 1];
            //String raw = "R.raw." + selectedInstrumentNote;
            selectedInstrumentNote = selectedInstrumentNote.toLowerCase();
            int resourceID = this.getResources().getIdentifier(selectedInstrumentNote, "raw", this.getPackageName());
            soundPoolResourceID.put(selectedInstrumentNote, soundPool.load(this, resourceID, 1));
        }
    }

    /*
    Using RGB values, calculates the perceived brightness, and then translates it into a value between 0 and 1.0, that can be applied to playSound as a volume level
     */
    public float calculateVolumeFromBrightness(int red, int green, int blue) {
        return ((0.299f * red + 0.587f * green + 0.114f * blue) / 255);    //calculates perceived brightness of RGB colour
    }

    /*
    The instrument and note corresponding to the colour is retrieved, then concatentated to collect the SoundPoolID for the corresponding instrument noise.
    This is passed into playcolourSound(), with the volume level, which plays the sound which represents the colour.
     */
    public void getInstrumentAndNote(int colour, float volumeLevel) {
        if (colour <= 7 && colour >= 1) {   //if actually a colour

            String instrument = colourInstruments[colour];  //retrieve which instrument represents the colour
            String note = colourNote[colour];               //retrieve which note represents the colour

            String soundPoolInstrument = (instrument + note).toLowerCase();

            int instrumentID = soundPoolResourceID.get(soundPoolInstrument);

            playColourSound(instrumentID, volumeLevel);
        }
    }

    /*
    Plays the instrument for the frame colour, at the correct note (e.g. C for red). The brightness (volume) is also included in this
     */
    private void playColourSound(int instrumentID, float volumeLevel) {
        if (!configArray.get(currentConfigIndex).getInvertBrightnessValue()) {  //if set to false
            soundPool.play(instrumentID, volumeLevel, volumeLevel, 1, 0, 1f);
        }
        else {
            soundPool.play(instrumentID, 1-volumeLevel, 1-volumeLevel, 1, 0, 1f);
        }
    }


    //Colour Classification methods ////////////////////////////////////

    /*
    Places all colour shades with their RGB values in an array. The frame average RGB is compared to this in getColourNameFromRgb() to find the closest shade.
     */
    public ArrayList<ColourName> setupColourArray() {
        //violet
        colourList.add(new ColourName("LightPink", 0xFF, 0xB6, 0xC1));
        colourList.add(new ColourName("Pink", 0xFF, 0xC0, 0xCB));
        colourList.add(new ColourName("Crimson", 0xDC, 0x14, 0x3C));
        colourList.add(new ColourName("LavenderBlush", 0xFF, 0xF0, 0xF5));
        colourList.add(new ColourName("PaleVioletRed", 0xDB, 0x70, 0x93));
        colourList.add(new ColourName("HotPink", 0xFF, 0x69, 0xB4));
        colourList.add(new ColourName("DeepPink", 0xFF, 0x14, 0x93));
        colourList.add(new ColourName("MediumVioletRed", 0xC7, 0x15, 0x85));
        colourList.add(new ColourName("Orchid", 0xDA, 0x70, 0xD6));
        colourList.add(new ColourName("Thistle", 0xD8, 0xBF, 0xD8));
        colourList.add(new ColourName("Plum", 0xDD, 0xA0, 0xDD));
        colourList.add(new ColourName("Violet", 0xEE, 0x82, 0xEE));


        //indigo
        colourList.add(new ColourName("Magenta", 0xFF, 0x00, 0xFF));
        colourList.add(new ColourName("DarkMagenta", 0x8B, 0x00, 0x8B));
        colourList.add(new ColourName("Purple", 0x80, 0x00, 0x80));
        colourList.add(new ColourName("MediumOrchid", 0xBA, 0x55, 0xD3));
        colourList.add(new ColourName("DarkViolet", 0x94, 0x00, 0xD3));
        colourList.add(new ColourName("DarkOrchid", 0x99, 0x32, 0xCC));
        colourList.add(new ColourName("Indigo", 0x4B, 0x00, 0x82));
        colourList.add(new ColourName("BlueViolet", 0x8A, 0x2B, 0xE2));
        colourList.add(new ColourName("MediumPurple", 0x93, 0x70, 0xDB));

        //blue
        colourList.add(new ColourName("MediumSlateBlue", 0x7B, 0x68, 0xEE));
        colourList.add(new ColourName("SlateBlue", 0x6A, 0x5A, 0xCD));
        colourList.add(new ColourName("DarkSlateBlue", 0x48, 0x3D, 0x8B));
        colourList.add(new ColourName("GhostWhite", 0xF8, 0xF8, 0xFF));
        colourList.add(new ColourName("Lavender", 0xE6, 0xE6, 0xFA));
        colourList.add(new ColourName("Blue", 0x00, 0x00, 0xFF));
        colourList.add(new ColourName("MediumBlue", 0x00, 0x00, 0xCD));
        colourList.add(new ColourName("DarkBlue", 0x00, 0x00, 0x8B));
        colourList.add(new ColourName("Navy", 0x00, 0x00, 0x80));
        colourList.add(new ColourName("MidnightBlue", 0x19, 0x19, 0x70));
        colourList.add(new ColourName("RoyalBlue", 0x41, 0x69, 0xE1));
        colourList.add(new ColourName("CornflowerBlue", 0x64, 0x95, 0xED));
        colourList.add(new ColourName("LightSteelBlue", 0xB0, 0xC4, 0xDE));
        colourList.add(new ColourName("SlateGray", 0x70, 0x80, 0x90));
        colourList.add(new ColourName("DodgerBlue", 0x1E, 0x90, 0xFF));
        colourList.add(new ColourName("AliceBlue", 0xF0, 0xF8, 0xFF));
        colourList.add(new ColourName("SteelBlue", 0x46, 0x82, 0xB4));
        colourList.add(new ColourName("LightSkyBlue", 0x87, 0xCE, 0xFA));
        colourList.add(new ColourName("SkyBlue", 0x87, 0xCE, 0xEB));
        colourList.add(new ColourName("DeepSkyBlue", 0x00, 0xBF, 0xFF));
        colourList.add(new ColourName("LightBlue", 0xAD, 0xD8, 0xE6));
        colourList.add(new ColourName("PowderBlue", 0xB0, 0xE0, 0xE6));
        colourList.add(new ColourName("CadetBlue", 0x5F, 0x9E, 0xA0));
        colourList.add(new ColourName("DarkTurquoise", 0x00, 0xCE, 0xD1));
        colourList.add(new ColourName("Azure", 0xF0, 0xFF, 0xFF));
        colourList.add(new ColourName("LightCyan", 0xE0, 0xFF, 0xFF));
        colourList.add(new ColourName("PaleTurquoise", 0xAF, 0xEE, 0xEE));
        colourList.add(new ColourName("Aqua", 0x00, 0xFF, 0xFF));
        colourList.add(new ColourName("Teal", 0x00, 0x80, 0x80));   //moved


        //green
        colourList.add(new ColourName("DarkCyan", 0x00, 0x8B, 0x8B));   //blue
        colourList.add(new ColourName("MediumTurquoise", 0x48, 0xD1, 0xCC));
        colourList.add(new ColourName("LightSeaGreen", 0x20, 0xB2, 0xAA));
        colourList.add(new ColourName("Turquoise", 0x40, 0xE0, 0xD0));
        colourList.add(new ColourName("Aquamarine", 0x7F, 0xFF, 0xD4));
        colourList.add(new ColourName("MediumAquaMarine", 0x66, 0xCD, 0xAA));
        colourList.add(new ColourName("MediumSpringGreen", 0x00, 0xFA, 0x9A));
        colourList.add(new ColourName("MintCream", 0xF5, 0xFF, 0xFA));
        colourList.add(new ColourName("SpringGreen", 0x00, 0xFF, 0x7F));
        colourList.add(new ColourName("MediumSeaGreen", 0x3C, 0xB3, 0x71));
        colourList.add(new ColourName("SeaGreen", 0x2E, 0x8B, 0x57));
        colourList.add(new ColourName("HoneyDew", 0xF0, 0xFF, 0xF0));
        colourList.add(new ColourName("DarkSeaGreen", 0x8F, 0xBC, 0x8F));
        colourList.add(new ColourName("PaleGreen", 0x98, 0xFB, 0x98));
        colourList.add(new ColourName("LightGreen", 0x90, 0xEE, 0x90));
        colourList.add(new ColourName("LimeGreen", 0x32, 0xCD, 0x32));
        colourList.add(new ColourName("Lime", 0x00, 0xFF, 0x00));
        colourList.add(new ColourName("ForestGreen", 0x22, 0x8B, 0x22));
        colourList.add(new ColourName("Green", 0x00, 0x80, 0x00));
        colourList.add(new ColourName("DarkGreen", 0x00, 0x64, 0x00));
        colourList.add(new ColourName("LawnGreen", 0x7C, 0xFC, 0x00));
        colourList.add(new ColourName("Chartreuse", 0x7F, 0xFF, 0x00));
        colourList.add(new ColourName("GreenYellow", 0xAD, 0xFF, 0x2F));
        colourList.add(new ColourName("DarkOliveGreen", 0x55, 0x6B, 0x2F));
        colourList.add(new ColourName("YellowGreen", 0x9A, 0xCD, 0x32));
        colourList.add(new ColourName("OliveDrab", 0x6B, 0x8E, 0x23));

        //yellow
        colourList.add(new ColourName("Ivory", 0xFF, 0xFF, 0xF0));
        colourList.add(new ColourName("Beige", 0xF5, 0xF5, 0xDC));
        colourList.add(new ColourName("LightYellow", 0xFF, 0xFF, 0xE0));
        colourList.add(new ColourName("LightGoldenRodYellow", 0xFA, 0xFA, 0xD2));
        colourList.add(new ColourName("Yellow", 0xFF, 0xFF, 0x00));
        colourList.add(new ColourName("Yellow2", 0xEE, 0xEE, 0x00)); //new
        colourList.add(new ColourName("Yellow3", 0xCD, 0xCD, 0x00)); //new
        colourList.add(new ColourName("Yellow4", 0x8B, 0x8B, 0x00)); //new
        colourList.add(new ColourName("Olive", 0x80, 0x80, 0x00));
        colourList.add(new ColourName("DarkKhaki", 0xBD, 0xB7, 0x6B));
        colourList.add(new ColourName("PaleGoldenRod", 0xEE, 0xE8, 0xAA));
        colourList.add(new ColourName("LemonChiffon", 0xFF, 0xFA, 0xCD));
        colourList.add(new ColourName("LemonChiffon2", 0xEE, 0xE9, 0xBF)); //new
        colourList.add(new ColourName("LemonChiffon3", 0xCD, 0xC9, 0xA5)); //new
        colourList.add(new ColourName("LemonChiffon4", 0x8B, 0x89, 0x70)); //new
        colourList.add(new ColourName("Khaki", 0xF0, 0xE6, 0x8C));

        //orange
        colourList.add(new ColourName("Gold", 0xFF, 0xD7, 0x00));
        colourList.add(new ColourName("Gold2", 0xEE, 0xC9, 0x00));
        colourList.add(new ColourName("Gold3", 0xCD, 0xAD, 0x00));
        colourList.add(new ColourName("Gold4", 0x8B, 0x75, 0x00));
        colourList.add(new ColourName("Cornsilk", 0xFF, 0xF8, 0xDC));
        colourList.add(new ColourName("GoldenRod", 0xDA, 0xA5, 0x20));
        colourList.add(new ColourName("GoldenRod1", 0xFF, 0xC1, 0x25)); //new
        colourList.add(new ColourName("GoldenRod2", 0xEE, 0xB4, 0x22)); //new
        colourList.add(new ColourName("GoldenRod3", 0xCD, 0x9B, 0x1D)); //new
        colourList.add(new ColourName("GoldenRod4", 0x8B, 0x69, 0x14)); //new
        colourList.add(new ColourName("DarkGoldenRod", 0xB8, 0x86, 0x0B));
        colourList.add(new ColourName("FloralWhite", 0xFF, 0xFA, 0xF0));
        colourList.add(new ColourName("OldLace", 0xFD, 0xF5, 0xE6));
        colourList.add(new ColourName("Wheat", 0xF5, 0xDE, 0xB3));
        colourList.add(new ColourName("Orange", 0xFF, 0xA5, 0x00));
        colourList.add(new ColourName("Moccasin", 0xFF, 0xE4, 0xB5));
        colourList.add(new ColourName("PapayaWhip", 0xFF, 0xEF, 0xD5));
        colourList.add(new ColourName("BlanchedAlmond", 0xFF, 0xEB, 0xCD));
        colourList.add(new ColourName("NavajoWhite", 0xFF, 0xDE, 0xAD));
        colourList.add(new ColourName("AntiqueWhite", 0xFA, 0xEB, 0xD7));
        colourList.add(new ColourName("Tan", 0xD2, 0xB4, 0x8C));
        colourList.add(new ColourName("BurlyWood", 0xDE, 0xB8, 0x87));
        colourList.add(new ColourName("DarkOrange", 0xFF, 0x8C, 0x00));
        colourList.add(new ColourName("Bisque", 0xFF, 0xE4, 0xC4));
        colourList.add(new ColourName("Linen", 0xFA, 0xF0, 0xE6));
        colourList.add(new ColourName("Peru", 0xCD, 0x85, 0x3F));
        colourList.add(new ColourName("PeachPuff", 0xFF, 0xDA, 0xB9));
        colourList.add(new ColourName("SandyBrown", 0xF4, 0xA4, 0x60));
        colourList.add(new ColourName("Chocolate", 0xD2, 0x69, 0x1E));
        colourList.add(new ColourName("SaddleBrown", 0x8B, 0x45, 0x13));
        colourList.add(new ColourName("SeaShell", 0xFF, 0xF5, 0xEE));
        colourList.add(new ColourName("Sienna", 0xA0, 0x52, 0x2D));
        colourList.add(new ColourName("LightSalmon", 0xFF, 0xA0, 0x7A));
        colourList.add(new ColourName("Coral", 0xFF, 0x7F, 0x50));

        //red
        colourList.add(new ColourName("OrangeRed", 0xFF, 0x45, 0x00));
        colourList.add(new ColourName("DarkSalmon", 0xE9, 0x96, 0x7A));
        colourList.add(new ColourName("Tomato", 0xFF, 0x63, 0x47));
        colourList.add(new ColourName("Salmon", 0xFA, 0x80, 0x72));
        colourList.add(new ColourName("MistyRose", 0xFF, 0xE4, 0xE1));
        colourList.add(new ColourName("LightCoral", 0xF0, 0x80, 0x80));
        colourList.add(new ColourName("Snow", 0xFF, 0xFA, 0xFA));
        colourList.add(new ColourName("RosyBrown", 0xBC, 0x8F, 0x8F));  //move this
        colourList.add(new ColourName("IndianRed", 0xCD, 0x5C, 0x5C));
        colourList.add(new ColourName("Red", 0xFF, 0x00, 0x00));
        colourList.add(new ColourName("Brown", 0xA5, 0x2A, 0x2A));
        colourList.add(new ColourName("FireBrick", 0xB2, 0x22, 0x22));
        colourList.add(new ColourName("DarkRed", 0x8B, 0x00, 0x00));
        colourList.add(new ColourName("Maroon", 0x80, 0x00, 0x00));

        //non-colour
        colourList.add(new ColourName("White", 0xFF, 0xFF, 0xFF));
        colourList.add(new ColourName("WhiteSmoke", 0xF5, 0xF5, 0xF5));
        colourList.add(new ColourName("Gainsboro", 0xDC, 0xDC, 0xDC));
        colourList.add(new ColourName("DarkSlateGray", 0x2F, 0x4F, 0x4F));      //moved
        colourList.add(new ColourName("LightSlateGray", 0x77, 0x88, 0x99));     //moved
        colourList.add(new ColourName("LightGray", 0xD3, 0xD3, 0xD3));
        colourList.add(new ColourName("Silver", 0xC0, 0xC0, 0xC0));
        colourList.add(new ColourName("DarkGray", 0xA9, 0xA9, 0xA9));
        colourList.add(new ColourName("Gray", 0x80, 0x80, 0x80));
        colourList.add(new ColourName("DimGray", 0x69, 0x69, 0x69));
        colourList.add(new ColourName("Black", 0x00, 0x00, 0x00));

        return colourList;
    }

    public String getColourNameFromRgb(int r, int g, int b) {
        ArrayList<ColourName> colorList = setupColourArray();
        ColourName closestMatch = null;
        int minMSE = Integer.MAX_VALUE;
        int mse;
        for (ColourName c : colorList) {
            mse = c.computeMSE(r, g, b);
            if (mse < minMSE) { //if a closer match
                minMSE = mse;
                closestMatch = c;
                bestMatchIndex = colorList.indexOf(c); //should return int array index
            }
        }

        if (closestMatch != null) {
            return closestMatch.getName();
        } else {
            return "No matched color name.";
        }
    }

    /*
    Returns the integer representing the solid colour the frame contains
    */
    public int getColourLabel(int colourArrayIndex) {


        if (numOfColours == 7) {    //if settings set to play 7 colours
            if (colourArrayIndex >= 0 && colourArrayIndex <= 11) {
                return 1; /*red*/
            } else if (colourArrayIndex >= 12 && colourArrayIndex <= 20) {
                return 2; /*orange*/
            } else if (colourArrayIndex >= 21 && colourArrayIndex <= 48) {
                return 3; /*yellow*/
            } else if (colourArrayIndex >= 49 && colourArrayIndex <= 74) {
                return 4; /*green*/
            } else if (colourArrayIndex >= 75 && colourArrayIndex <= 91) {
                return 5; /*blue*/
            } else if (colourArrayIndex >= 92 && colourArrayIndex <= 125) {
                return 6; /*indigo*/
            } else if (colourArrayIndex >= 126 && colourArrayIndex <= 139) {
                return 7; /*violet*/
            } else {
                return 8;
            } //142 to 150 - not a colour

        }

        return 8;   //else return 8 - sound will not be played
    }

    // config methods /////////////////////

    /*
    Places user configured instruments into array at index number of their respective colour
     */
    public void initConfig() {
        colourInstruments[1] = "piano";
        colourInstruments[2] = "piano";
        colourInstruments[3] = "piano";
        colourInstruments[4] = "piano";
        colourInstruments[5] = "piano";
        colourInstruments[6] = "piano";
        colourInstruments[7] = "piano";

        colourNote[1] = "c";
        colourNote[2] = "d";
        colourNote[3] = "e";
        colourNote[4] = "f";
        colourNote[5] = "g";
        colourNote[6] = "a";
        colourNote[7] = "b";
    }

    /*
    Loads configuration from the current user configuration saved in SharedPreferences. The instruments and notes are collected from SoundConfig classes.
     */
    public void initConfigFromSaved() {
        for (int i = 0; i < 7; i++) {
            colourInstruments[i + 1] = (configArray.get(currentConfigIndex).getInstrument(i + 1));
        }

        for (int i = 0; i < 7; i++) {
            colourNote[i + 1] = (configArray.get(currentConfigIndex).getNote(i + 1));
        }
    }
}

class ColourName {
    public int r, g, b;
    public String name;

    public ColourName(String name, int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.name = name;
    }

    public int computeMSE(int pixR, int pixG, int pixB) {
        return (int) (((pixR - r) * (pixR - r) + (pixG - g) * (pixG - g) + (pixB - b)
                * (pixB - b)) / 3);
    }

    public String getName() {
        return name;
    }
}