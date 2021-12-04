package com.example.sensor2;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btn_launch;
    private Button btn_to_imu,btn_to_camera,btn_to_gps;
    private EditText et_user,et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_launch = findViewById(R.id.bt_launch);
        btn_to_imu = findViewById(R.id.btn_to_imu);
        btn_to_camera = findViewById(R.id.btn_to_camera);
        btn_to_gps = findViewById(R.id.btn_to_gps);

        et_user = findViewById(R.id.et_1);
        et_password = findViewById(R.id.et_2);

        //EditText监听时间（变化前、变化中、变化后）
        et_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
        setListeners();

    }
    private void setListeners(){
        OnClick onclick = new OnClick();
        btn_launch.setOnClickListener(onclick);
        btn_to_imu.setOnClickListener(onclick);
        btn_to_camera.setOnClickListener(onclick);
        btn_to_gps.setOnClickListener(onclick);
    }

    private class OnClick implements View.OnClickListener{
        @Override
        public void onClick(View v){
            Intent intent = null;
            switch (v.getId()){
                /******************************************************************/
                case R.id.bt_launch:
                    String user = et_user.getText().toString().trim();
                    String password = et_password.getText().toString().trim();

                    String user_true = "15518153551";
                    String password_true = "帅";

                    if(password.equals(password_true)){
                        Toast.makeText(MainActivity.this,"登陆成功", Toast.LENGTH_SHORT).show();
                        intent = new Intent(MainActivity.this,SuccessActivity.class);
                        startActivity(intent);
                    }
                    else {Toast.makeText(MainActivity.this,"密码错误！\n请再次输入~", Toast.LENGTH_SHORT).show();}
                    break;
                /******************************************************************/
                case R.id.btn_to_imu:
                    intent = new Intent(MainActivity.this,ImuActivity.class);
                    startActivity(intent);
                    break;
                /******************************************************************/
                case R.id.btn_to_camera:
                    intent = new Intent(MainActivity.this,CameraActivity.class);
                    startActivity(intent);
                    break;
                /******************************************************************/
                case R.id.btn_to_gps:
                    intent = new Intent(MainActivity.this,GpsActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

}