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
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import  java.util.List;

public class ImuActivity extends AppCompatActivity implements SensorEventListener,View.OnClickListener {

    private SensorManager mSensorMgr;
    private EditText tvx,tvy,tvz;
    private EditText gyx,gyy,gyz;
    private  List<String> LS1;
    private List<String> LS2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imu);

        LS1=new ArrayList<String>();
        LS2=new ArrayList<String>();

        Button bt=findViewById(R.id.btn_start);
        bt.setOnClickListener(this);

        Button bt_stop=findViewById(R.id.btn_stop);
        bt_stop.setOnClickListener(this);

        Button btn_imu_main=findViewById(R.id.btn_imu_main);
        btn_imu_main.setOnClickListener(this);

        tvx=findViewById(R.id.et1);
        tvy=findViewById(R.id.et2);
        tvz=findViewById(R.id.et3);

        gyx=findViewById(R.id.et4);
        gyy=findViewById(R.id.et5);
        gyz=findViewById(R.id.et6);
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

            tvx.setText(String.format("%.6f", values[0]));
            tvy.setText(String.format("%.6f", values[1]));
            tvz.setText(String.format("%.6f", values[2]));

            Date date=new Date();
            SimpleDateFormat formatter=new SimpleDateFormat("sss:SSS");
            String time=formatter.format(date);
            String s="";
            s=time+"    "+String.format("%.6f", values[0])+"    "+String.format("%.6f", values[1])+"    "+String.format("%.6f", values[2])+"\n";
            LS1.add(s);
        }

        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE)
        {
            float[] values=event.values;

            gyx.setText(String.format("%.6f", values[0]));
            gyy.setText(String.format("%.6f", values[1]));
            gyz.setText(String.format("%.6f", values[2]));

            Date date=new Date();
            SimpleDateFormat formatter=new SimpleDateFormat("sss:SSS");
            String time=formatter.format(date);
            String s="";
            s=time+"    "+String.format("%.6f", values[0])+"    "+String.format("%.6f", values[1])+"    "+String.format("%.6f", values[2])+"\n";
            LS2.add(s);
        }
    }

    public void onAccuracyChanged(Sensor sensor,int accuracy)
    {//不用处理，空着就行
        return;
    }





    private static final String TAG = "ACCCollection:";

    public  void writeLS(List<String> LS,String sensor_name) {
        Date date_now=new Date();
        SimpleDateFormat formatter_now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_now = formatter_now.format(date_now);
        String filename_now = sensor_name + " " + time_now + ".csv";
        try {
            File dir = new File(getExternalFilesDir(null),"传感器数据");
            Log.i(TAG, "write: -------1");
            if (!dir.exists() || !dir.isDirectory())
            {
                Log.i(TAG, "write: --------2");
                dir.mkdirs();
            }
            File file=new File(dir,filename_now);
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
            Toast.makeText(ImuActivity.this,"保存成功: " + file.getAbsolutePath().toString(), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }




    public void onClick(View v)
    {
        if(v.getId()==R.id.btn_start)
        {
            mSensorMgr.unregisterListener(this,mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            mSensorMgr.unregisterListener(this,mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE));//关掉后重启管理器

            mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
            mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
            LS1.clear();
            LS2.clear();
            return;
        }
        if(v.getId()==R.id.btn_stop)
        {
            mSensorMgr.unregisterListener(this);
            writeLS(LS1,"Acc");
            writeLS(LS2,"Gyr");
            return;
        }
        if(v.getId()==R.id.btn_imu_main)
        {
            mSensorMgr.unregisterListener(this);
            finish();
        }
    }
}