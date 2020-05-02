package com.example.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
Float azimuth_angle;
private SensorManager compassSensorManager;
Sensor accelerometr;
Sensor magnetometer;
ImageView iv;
TextView tv;
private float current_degree=0f;

float[] accel_read;
float[] magnetic_read;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometr=compassSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer=compassSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        compassSensorManager.registerListener(this,accelerometr,SensorManager.SENSOR_DELAY_UI);
        compassSensorManager.registerListener(this,magnetometer,SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        compassSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        tv=(TextView)findViewById(R.id.text2);
        iv=(ImageView)findViewById(R.id.img);
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            accel_read=event.values;
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            magnetic_read=event.values;
        }
        if(accel_read!=null && magnetic_read!=null){
            float [] R=new float[9];
            float [] I=new float[9];
            boolean successful_read=SensorManager.getRotationMatrix(R,I,accel_read,magnetic_read);
            if(successful_read){
                float[] orientation=new float[3];
                SensorManager.getOrientation(R,orientation);
                azimuth_angle=orientation[0];
                float degrees=((azimuth_angle*180f)/3.14f);
                int degreesInt=Math.round(degrees);
                tv.setText(Integer.toString(degreesInt)+(char)0x00B0+" to absolute North");
                RotateAnimation rotate=new RotateAnimation(current_degree,-degreesInt, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                rotate.setDuration(100);
                rotate.setFillAfter(true);
                iv.startAnimation(rotate);
                current_degree=-degreesInt;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
