package slash.development.accelerometerprototype;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView xTextView, yTextView, zTextView;
    private EditText filenameEditText;
    private Button startButton, stopButton;
    private Sensor mySensor;
    private SensorManager SM;
    private int i = 0;
    private int sensorCounts = 0;
    private ArrayList<String> xValues, yValues, zValues;
    private boolean startTracking = false;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
        zValues = new ArrayList<>();

        //Create sensor
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Acc Sensor
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Sensor listener. SENSOR_DELAY_GAME = every 0,02 seconds, which is 50 times a second
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_GAME);

        //Assign TextView
        xTextView = (TextView)findViewById(R.id.x_textView);
        yTextView = (TextView)findViewById(R.id.y_textView);
        zTextView = (TextView)findViewById(R.id.z_textView);

        filenameEditText = (EditText)findViewById(R.id.filename_editText);

        startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking = true;
            }
        });

        stopButton = (Button)findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking = false;
                Toast.makeText(context, sensorCounts + " records stored. X,Y,Z: " + xValues.size(), Toast.LENGTH_LONG).show();

                sensorCounts = 0;

                createTextFile();
                //readFile();
                sendFile();
            }
        });







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void sendFile()
    {
        String filename = filenameEditText.getText().toString() + ".txt";
        File filelocation = new File(Environment.getRootDirectory(), filename);
        Uri path = Uri.fromFile(filelocation);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {"slash.development@gmail.com"};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        // the attachment
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        // the mail subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "BIOMETRIC - DATA");
        startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    public void readFile()
    {
        String ret = "";

        try {
            InputStream inputStream = openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }


    }

    public void createTextFile()
    {
        /*try
        {
            File root = new File(Environment.getRootDirectory(), "CollectedData");
            String filename = filenameEditText.getText().toString();
            File filepath = new File(root, filename + ".txt");
            FileWriter writer = new FileWriter(filepath);
            for (int j = 0; j < xValues.size(); j++)
            {
                writer.append(xValues.get(j) + " " + yValues.get(j) + " " + zValues.get(j) + "\n\r");
            }
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
        try
        {
            String filename = filenameEditText.getText().toString() + ".txt";
            OutputStreamWriter osw = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            for (int j = 0; j < xValues.size(); j++)
            {
                osw.append(xValues.get(j) + " " + yValues.get(j) + " " + zValues.get(j) + "\n\r");
            }
            osw.flush();
            osw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        //zzz
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {

        if (startTracking == true)
        {

            //Sensor delayed, to only send data every 0,04 s, which is 25 times a second.
            if (i >= 1)
            {
                xTextView.setText("X: " + event.values[0]);
                yTextView.setText("Y: " + event.values[1]);
                zTextView.setText("Z: " + event.values[2]);

                xValues.add(Float.toString(event.values[0]));
                yValues.add(Float.toString(event.values[1]));
                zValues.add(Float.toString(event.values[2]));

                sensorCounts++;
                i = 0;
            }
            else {
                i++;
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
