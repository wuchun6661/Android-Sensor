package com.example.sensor2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Environment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import  java.util.List;

public class ImuActivity extends AppCompatActivity implements SensorEventListener,View.OnClickListener {

    private SensorManager mSensorMgr;
    private  TextView tvx;
    private  TextView tvy;
    private  TextView tvz;
    private  List<String> LS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imu);

        LS=new ArrayList<String>();

        Button bt=findViewById(R.id.bt_dsp);
        bt.setOnClickListener(this);

        Button bt_stop=findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(this);

        tvx=findViewById(R.id.tvx);
        tvy=findViewById(R.id.tvy);
        tvz=findViewById(R.id.tvz);
        //
        mSensorMgr=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
    }

    protected void onPause()
    {
        super.onPause();
        mSensorMgr.unregisterListener(this);
    }

    protected void onResume()
    {
        super.onResume();
    }
    protected void onStop()
    {
        super.onStop();
        mSensorMgr.unregisterListener(this);

    }
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            float[] values=event.values;

            tvx.setText("ACC_X: "+Float.toString(values[0]));
            tvy.setText("ACC_Y: "+Float.toString(values[1]));
            tvz.setText("ACC_Z: "+Float.toString(values[2]));

            Date date=new Date();
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time=formatter.format(date);
            String s="";
            s=time+" "+Float.toString(values[0])+" "+Float.toString(values[1])+" "+Float.toString(values[2])+"\n";
            LS.add(s);

        }
    }

    public void onAccuracyChanged(Sensor sensor,int accuracy)
    {//不用处理，空着就行
        return;
    }





    private static final String TAG = "ACCCollection:";

    public static void writeLS(List<String> LS) {
        try {
            String path=Environment.getExternalStorageDirectory()+"/DCIM/CameraV2/";
            File folde=new File(path);
            //tvx.setText(path);
            Log.i(TAG, "write: -------1");
            if (!folde.exists() || !folde.isDirectory())
            {
                Log.i(TAG, "write: --------2");
                folde.mkdirs();
            }
            File file=new File(path,"aa.csv");
            if(!file.exists())
            {
                file.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            int i;
            for(i=0;i<LS.size();i++)
            {
                bw.write(LS.get(i));;
                bw.newLine();// 行换行
            }

            bw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public void onClick(View v)
    {
        if(v.getId()==R.id.bt_dsp)
        {
            mSensorMgr.unregisterListener(this,mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            mSensorMgr.registerListener(this,
                    mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
            LS.clear();
            return;
        }
        if(v.getId()==R.id.bt_stop)
        {
            mSensorMgr.unregisterListener(this);

            writeLS(LS);
            String path1=Environment.getExternalStorageDirectory()+"/DCIM/CameraV2/";
            Toast.makeText(ImuActivity.this, "Image Saved in:" + path1, Toast.LENGTH_SHORT).show();
            return;
        }
    }
}