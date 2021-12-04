package com.example.sensor2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class GpsActivity extends AppCompatActivity {

    private EditText et_gps_1,et_gps_2,et_gps_3;
    private Button btn_gps_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        et_gps_1 = findViewById(R.id.et_gps_1);
        et_gps_2 = findViewById(R.id.et_gps_2);
        et_gps_3 = findViewById(R.id.et_gps_3);
        btn_gps_main = findViewById(R.id.btn_gps_main);

        btn_gps_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(GpsActivity.this,MainActivity.class);
//                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GPSLocationManager gpsManager = GPSLocationManager.getInstances(GpsActivity.this);
        gpsManager.start(new MyListener(), true);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public static class GPSLocation implements LocationListener {
        private GPSLocationListener mGpsLocationListener;


        public GPSLocation(GPSLocationListener gpsLocationListener) {
            this.mGpsLocationListener = gpsLocationListener;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mGpsLocationListener.UpdateLocation(location);
            }
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            mGpsLocationListener.UpdateStatus(provider, status, extras);
            switch (status) {
                case LocationProvider.AVAILABLE:
                    mGpsLocationListener.UpdateGPSProviderStatus(GPSProviderStatus.GPS_AVAILABLE);
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    mGpsLocationListener.UpdateGPSProviderStatus(GPSProviderStatus.GPS_OUT_OF_SERVICE);
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    mGpsLocationListener.UpdateGPSProviderStatus(GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE);
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            mGpsLocationListener.UpdateGPSProviderStatus(GPSProviderStatus.GPS_ENABLED);
        }

        @Override
        public void onProviderDisabled(String provider) {
            mGpsLocationListener.UpdateGPSProviderStatus(GPSProviderStatus.GPS_DISABLED);
        }
    }

     public static class GPSLocationManager {

        private static final String GPS_LOCATION_NAME = android.location.LocationManager.GPS_PROVIDER;
        private static GPSLocationManager gpsLocationManager;
        private static Object objLock = new Object();
        private boolean isGpsEnabled;
        private String mLocateType;
        private WeakReference<Activity> mContext;
        private LocationManager locationManager;
        private GPSLocation mGPSLocation;
        private boolean isOPenGps;
        private long mMinTime;
        private float mMinDistance;
        private Handler handler;
        private Timer timer ;
        private TimerTask task;
        private Activity context;
        private GPSLocationManager(Activity context) {
            initData(context);
            this.context= context;
        }

        private void initData(Activity context) {
            this.mContext = new WeakReference<>(context);
            if (mContext.get() != null) {
                locationManager = (LocationManager) (mContext.get().getSystemService(Context.LOCATION_SERVICE));
            }
            //定位类型：GPS
            mLocateType = locationManager.GPS_PROVIDER;
            //默认不强制打开GPS设置面板
            isOPenGps = false;
            //默认定位时间间隔为30分钟,此处与下面都为30分钟
            mMinTime = 30*60*1000;
            //mMinTime = 10000;
            //默认位置可更新的最短距离为0m
            mMinDistance = 0;
        }

        public static GPSLocationManager getInstances(Activity context) {
            if (gpsLocationManager == null) {
                synchronized (objLock) {
                    if (gpsLocationManager == null) {
                        gpsLocationManager = new GPSLocationManager(context);
                    }
                }
            }
            return gpsLocationManager;
        }

        public void setHandler(final Handler handler) {
            this.handler = handler;
        }

        /**
         * 方法描述：设置发起定位请求的间隔时长
         *
         * @param minTime 定位间隔时长（单位ms）
         */
        public void setScanSpan(long minTime) {
            this.mMinTime = minTime;
        }

        /**
         * 方法描述：设置位置更新的最短距离
         *
         * @param minDistance 最短距离（单位m）
         */
        public void setMinDistance(float minDistance) {
            this.mMinDistance = minDistance;
        }

        /**
         * 方法描述：开启定位（默认情况下不会强制要求用户打开GPS设置面板）
         *
         * @param gpsLocationListener
         */
        public void start(GPSLocationListener gpsLocationListener) {

            this.start(gpsLocationListener, isOPenGps);
        }

        /**
         * 方法描述：开启定位
         *
         * @param gpsLocationListener
         * @param isOpenGps           当用户GPS未开启时是否强制用户开启GPS
         */
        public void start(GPSLocationListener gpsLocationListener, boolean isOpenGps) {
            this.isOPenGps = isOpenGps;
            if (mContext.get() == null) {
                return;
            }
            mGPSLocation = new GPSLocation(gpsLocationListener);
            isGpsEnabled = locationManager.isProviderEnabled(GPS_LOCATION_NAME);
            if (!isGpsEnabled && isOPenGps) {
                openGPS();
                return;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                        (mContext.get(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context,new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
                    return;
                }
            }
     /*   boolean xx= true;
        Location lastKnownLocation = null;
        while(xx){

             lastKnownLocation = locationManager.getLastKnownLocation(mLocateType);
            if(lastKnownLocation != null){
                xx=false;
            }
        }
            mGPSLocation.onLocationChanged(lastKnownLocation);
        */
            Location lastKnownLocation = null;
            lastKnownLocation = locationManager.getLastKnownLocation(mLocateType);
            if(lastKnownLocation == null){
                lastKnownLocation =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            mGPSLocation.onLocationChanged(lastKnownLocation);
            //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
            locationManager.requestLocationUpdates(mLocateType,0, 0, mGPSLocation);//mMinTime


        }

        public void start_TimerTask(){
            cancelTimer();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            }, 10,  30 * 1000 * 60);//定时每30分钟上传一次

        }


        /**
         * 方法描述：转到手机设置界面，用户设置GPS
         */
        public void openGPS() {
            Toast.makeText(mContext.get(), "请打开GPS设置", Toast.LENGTH_SHORT).show();
     /*  if (Build.VERSION.SDK_INT > 15) {
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.get().startActivityForResult(intent, 2);
        }*/
        }

        /**
         * 方法描述：终止GPS定位,该方法最好在onPause()中调用
         */
        public void stop() {
            if (mContext.get() != null) {
                if (ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext.get(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.removeUpdates(mGPSLocation);
            }
            cancelTimer();
        }
        private void cancelTimer(){
            if(timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }

    public class GPSProviderStatus {

        //用户手动开启GPS
        public static final int GPS_ENABLED = 0;
        //用户手动关闭GPS
        public static final int GPS_DISABLED = 1;
        //服务已停止，并且在短时间内不会改变
        public static final int GPS_OUT_OF_SERVICE = 2;
        //服务暂时停止，并且在短时间内会恢复
        public static final int GPS_TEMPORARILY_UNAVAILABLE = 3;
        //服务正常有效
        public static final int GPS_AVAILABLE = 4;
    }

    public interface GPSLocationListener {
        /**
         * 方法描述：位置信息发生改变时被调用
         *
         * @param location 更新位置后的新的Location对象
         */
        void UpdateLocation(Location location);

        /**
         * 方法描述：provider定位源类型变化时被调用
         *
         * @param provider provider的类型
         * @param status   provider状态
         * @param extras   provider的一些设置参数（如高精度、低功耗等）
         */
        void UpdateStatus(String provider, int status, Bundle extras);

        /**
         * 方法描述：GPS状态发生改变时被调用（GPS手动启动、手动关闭、GPS不在服务区、GPS占时不可用、GPS可用)
         *
         * @param gpsStatus 详见{@link GPSProviderStatus}
         */
        void UpdateGPSProviderStatus(int gpsStatus);

    }

    class MyListener implements GPSLocationListener {

        @Override
        public void UpdateLocation(Location location) {

            et_gps_1.setText( location.getLongitude() + "");
            et_gps_2.setText( location.getLatitude()  + "");
            et_gps_3.setText( location.getAltitude()  + "");
//            Log.e("gps==", "经度：" + location.getLongitude() + "\n纬度：" + location.getLatitude());
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
//            if ("gps" == provider) {
//                Log.e("UpdateStatus--gps", "定位类型：" + provider);
//            }
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {
            switch (gpsStatus) {

                case GPSProviderStatus.GPS_ENABLED:
                    //   Toast.makeText(MainActivity.this, "GPS开启", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_DISABLED:
                    //    Toast.makeText(MainActivity.this, "GPS关闭", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                    //  Toast.makeText(MainActivity.this, "GPS不可用", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                    //  Toast.makeText(MainActivity.this, "当前GPS状态为暂停服务状态", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_AVAILABLE:
                    //     Toast.makeText(MainActivity.this, "GPS可用啦", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}


