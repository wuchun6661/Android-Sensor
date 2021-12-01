package com.example.sensor2;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SuccessActivity extends AppCompatActivity implements SensorEventListener {  // 实现Sensor Event Listener接口
    private EditText textAcceX,textAcceY,textAcceZ;   //编辑框组件
    private EditText textGyroX,textGyroY,textGyroZ;
    private SensorManager sensorManager;  //传感器管理器组件
    private Button btn_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        textAcceX = findViewById(R.id.et1);
        textAcceY = findViewById(R.id.et2);
        textAcceZ = findViewById(R.id.et3);

        textGyroX = findViewById(R.id.et4);
        textGyroY = findViewById(R.id.et5);
        textGyroZ = findViewById(R.id.et6);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);    // 获取传感器管理器

        btn_1 = findViewById(R.id.btn_1);
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置传感器类型及采样率
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);  // 暂停采集
    }

    @Override
    public void onSensorChanged(SensorEvent event) {   // 重写SensorEventListener接口的方法
        float [] values = event.values;
        int sensorType = event.sensor.getType();
        StringBuilder stringBuilderX = null, stringBuilderY = null, stringBuilderZ = null;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {   //  判断是否所需传感器
            stringBuilderX = new StringBuilder();
            stringBuilderY = new StringBuilder();
            stringBuilderZ = new StringBuilder();
            stringBuilderX.append(String.format("%.3f", values[0]));
            stringBuilderY.append(String.format("%.3f", values[1]));
            stringBuilderZ.append(String.format("%.3f", values[2]));


            textAcceX.setText(stringBuilderX.toString());   // 编辑框内显示
            textAcceY.setText(stringBuilderY.toString());
            textAcceZ.setText(stringBuilderZ.toString());
        }

        if (sensorType == Sensor.TYPE_GYROSCOPE) {
            stringBuilderX = new StringBuilder();
            stringBuilderY = new StringBuilder();
            stringBuilderZ = new StringBuilder();
            stringBuilderX.append(String.format("%.3f", values[0]));
            stringBuilderY.append(String.format("%.3f", values[1]));
            stringBuilderZ.append(String.format("%.3f", values[2]));


            textGyroX.setText(stringBuilderX.toString());   // 编辑框内显示
            textGyroY.setText(stringBuilderY.toString());
            textGyroZ.setText(stringBuilderZ.toString());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {   //  重写SensorEventListener接口的方法

    }

}