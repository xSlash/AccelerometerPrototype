package slash.development.accelerometerprototype;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView xTextView, yTextView, zTextView;
    private Button startButton, stopButton;
    private Sensor mySensor;
    private SensorManager SM;
    private int i = 0;
    private int sensorCounts = 0;
    private float[] xValues, yValues, zValues;
    private boolean startTracking = false;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                Toast.makeText(context, sensorCounts + " records stored", Toast.LENGTH_LONG).show();

                sensorCounts = 0;
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




    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //zzz
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (startTracking == true)
        {

            //Sensor delayed, to only send data every 0,04 s, which is 25 times a second.
            if (i >= 1)
            {
                xTextView.setText("X: " + event.values[0]);
                yTextView.setText("Y: " + event.values[1]);
                zTextView.setText("Z: " + event.values[2]);

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
