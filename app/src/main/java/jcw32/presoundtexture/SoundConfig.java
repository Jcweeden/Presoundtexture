package jcw32.presoundtexture;

import java.io.Serializable;

/**
 * Created by Joshua on 16/04/15.
 */
public class SoundConfig implements Serializable {

    String name;
    Boolean invertBrightness = false;
    String[] colourInstruments = new String[8];     //contains representative instrument for each primary colour - 0 not used
    String[] colourNote = new String[8];            //contains representative note for each primary colour - 0 not used

    SoundConfig(String inputName) {
        setDefaultSettings();
        name = inputName;
    }

    public void setDefaultSettings() {

        setDefaultNotes();
        setDefaultInstruments();
    }

    public void setDefaultNotes() {
        colourNote[1] = "c";
        colourNote[2] = "d";
        colourNote[3] = "e";
        colourNote[4] = "f";
        colourNote[5] = "g";
        colourNote[6] = "a";
        colourNote[7] = "b";
    }

    public void setDefaultInstruments() {
        colourInstruments[1] = "piano";
        colourInstruments[2] = "piano";
        colourInstruments[3] = "piano";
        colourInstruments[4] = "piano";
        colourInstruments[5] = "piano";
        colourInstruments[6] = "piano";
        colourInstruments[7] = "piano";
    }

    /*
    allows for a single instrument to be changed to a different
     */
    public void setIndividualInstrument(int colour, String instrument) {

    }

    /*
 allows for a single instrument to be changed to a different
     */
    public void setIndividualNote(int colour, String note) {

    }

    /*
    sets all instruments in a single method
     */
    public void setInstruments(String[] instruments) {
        for (int i = 1; i<=7;i++) {
            colourInstruments[i] = instruments[i].toLowerCase(); //new just in case
        }
    }

    /*
    sets all notes in a single method
     */
    public void setNotes(String[] notes) {
        for (int i = 1; i<=7;i++) {
            colourNote[i] = notes[i];
        }
    }

    public void setName(String inputName) {
        name = inputName;
    }

    public void setInvertBrightness(boolean checkBox) {
            invertBrightness = checkBox;
    }

    public boolean getInvertBrightnessValue() {
        return invertBrightness;
    }

    public String getInstrument(int index) {
        return colourInstruments[index];
    }

    public String getNote(int index) {
        return colourNote[index];
    }

    public int getNoteIndex(int index) {
        if (getNote(index).equals("c")) {
            return 1;
        }
        else if (getNote(index).equals("d")) {
            return 2;
        }
        else if (getNote(index).equals("e")) {
            return 3;
        }
        else if (getNote(index).equals("f")) {
            return 4;
        }
        else if (getNote(index).equals("g")) {
            return 5;
        }
        else if (getNote(index).equals("a")) {
            return 6;
        }
        else if (getNote(index).equals("b")) {
            return 7;
        }
        else return 1;
    }

//    {"Acoustic Guitar", "Bass Guitar", "Bassoon", "Cello", "Clarinet", "Double Bass", "Electric Guitar", "Flute", "Harpsichord", "Marimba", "Oboe", "Organ",
//            "Piano", "Saxophone", "Sitar", "Synth", "Theremin", "Trumpet", "Tuba", "Violin", "Xylophone"};

    public int getInstrumentIndex(int index) {
        if (getInstrument(index).equals("acousticguitar")) {
            return 1;
        }
        else if (getInstrument(index).equals("bassguitar")) {
            return 2;
        }
        else if (getInstrument(index).equals("bassoon")) {
            return 3;
        }
        else if (getInstrument(index).equals("cello")) {
            return 4;
        }
        else if (getInstrument(index).equals("clarinet")) {
            return 5;
        }
        else if (getInstrument(index).equals("doublebass")) {
            return 6;
        }
        else if (getInstrument(index).equals("electricguitar")) {
            return 7;
        }
        else if (getInstrument(index).equals("flute")) {
            return 8;
        }
        else if (getInstrument(index).equals("harpsicord")) {
            return 9;
        }
        else if (getInstrument(index).equals("marimba")) {
            return 10;
        }
        else if (getInstrument(index).equals("oboe")) {
            return 11;
        }
        else if (getInstrument(index).equals("organ")) {
            return 12;
        }
        else if (getInstrument(index).equals("piano")) {
            return 13;
        }
        else if (getInstrument(index).equals("saxophone")) {
            return 14;
        }
        else if (getInstrument(index).equals("sitar")) {
            return 15;
        }
        else if (getInstrument(index).equals("synth")) {
            return 16;
        }
        else if (getInstrument(index).equals("theremin")) {
            return 17;
        }
        else if (getInstrument(index).equals("trumpet")) {
            return 18;
        }
        else if (getInstrument(index).equals("tuba")) {
            return 19;
        }
        else if (getInstrument(index).equals("violin")) {
            return 20;
        }
        else if (getInstrument(index).equals("xylophone")) {
            return 21;
        }
        else return 1;
    }

    public String getName() {
        return name;
    }
}

