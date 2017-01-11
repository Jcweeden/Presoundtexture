package jcw32.presoundtexture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;


public class ConfigurationPage extends ActionBarActivity {

    private static final String configArray_key = "configurationArray"; //collect serialised configurations array
    private static final String PREFS = null;             //PREFS as a filename to keep your SharedPreferences in a single location
    public ArrayList<SoundConfig> configArray = new ArrayList<SoundConfig>();
    //public ListView configListView;
    public TextView currentConfigText;
    Button newConfigButton;
    Button editConfigButton;
    SharedPreferences mSharedPreferences;                 //for storing a reference to the shared preferences class
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_page);
        newConfigButton = (Button) findViewById(R.id.newConfigBut);
        newConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change page
                Intent changeToNewConfig = new Intent(ConfigurationPage.this, NewConfigurationPage.class);
                startActivity(changeToNewConfig);
            }
        });

        editConfigButton = (Button) findViewById(R.id.editConfigBut);
        editConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change page
                Intent changeToEditConfig = new Intent(ConfigurationPage.this, EditConfigurationPage.class);
                startActivity(changeToEditConfig);
            }
        });

        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);         //init SharedPreferences
        try {
            configArray = (ArrayList<SoundConfig>) ObjectSerializer.deserialize(mSharedPreferences.getString(configArray_key, ""));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set config names in ListView
        ListView configListView = (ListView) findViewById(R.id.configList);
        ArrayList<String> configNames = new ArrayList<String>();

        for (int i = 0; i < configArray.size(); i++) {
            configNames.add(configArray.get(i).getName());
        }
        adapter = new ArrayAdapter<String>(getApplicationContext(), /*android.R.layout.simple_spinner_item*/ R.layout.custom_layout, configNames);




        configListView.setAdapter(adapter);




        configListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View arg1, int index, long arg3) {
                // TODO Auto-generated method stub
     //           String selectedConfigName = adapter.getItemAtPosition(index).toString();

                //update current config
                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putString("currentConfig", Integer.toString(index));  //store newly created config - store array number
                e.commit();

                Intent changeToMainActivity = new Intent(ConfigurationPage.this, MainActivity.class);
                startActivity(changeToMainActivity);
            }
        });

        currentConfigText = (TextView) findViewById(R.id.currentConfigText);
        String configName = configArray.get(Integer.parseInt(mSharedPreferences.getString("currentConfig", ""))).getName();
        currentConfigText.setText(Html.fromHtml("<u><b>Current Config:</b></u>  " + configName));

    }
//load configArray names into listView
    //return index of selected item
    //save to current config
    //return to mainActivity

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration_page, menu);
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
}
