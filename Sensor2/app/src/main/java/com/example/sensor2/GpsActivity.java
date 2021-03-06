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
            //???????????????GPS
            mLocateType = locationManager.GPS_PROVIDER;
            //?????????????????????GPS????????????
            isOPenGps = false;
            //???????????????????????????30??????,?????????????????????30??????
            mMinTime = 30*60*1000;
            //mMinTime = 10000;
            //???????????????????????????????????????0m
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
         * ??????????????????????????????????????????????????????
         *
         * @param minTime ???????????????????????????ms???
         */
        public void setScanSpan(long minTime) {
            this.mMinTime = minTime;
        }

        /**
         * ????????????????????????????????????????????????
         *
         * @param minDistance ?????????????????????m???
         */
        public void setMinDistance(float minDistance) {
            this.mMinDistance = minDistance;
        }

        /**
         * ???????????????????????????????????????????????????????????????????????????GPS???????????????
         *
         * @param gpsLocationListener
         */
        public void start(GPSLocationListener gpsLocationListener) {

            this.start(gpsLocationListener, isOPenGps);
        }

        /**
         * ???????????????????????????
         *
         * @param gpsLocationListener
         * @param isOpenGps           ?????????GPS????????????????????????????????????GPS
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
            //???????????????2???3???????????????3??????0???????????????3???????????????3???0?????????????????????????????????????????????0??????????????????
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
            }, 10,  30 * 1000 * 60);//?????????30??????????????????

        }


        /**
         * ??????????????????????????????????????????????????????GPS
         */
        public void openGPS() {
            Toast.makeText(mContext.get(), "?????????GPS??????", Toast.LENGTH_SHORT).show();
     /*  if (Build.VERSION.SDK_INT > 15) {
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.get().startActivityForResult(intent, 2);
        }*/
        }

        /**
         * ?????????????????????GPS??????,??????????????????onPause()?????????
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

        //??????????????????GPS
        public static final int GPS_ENABLED = 0;
        //??????????????????GPS
        public static final int GPS_DISABLED = 1;
        //???????????????????????????????????????????????????
        public static final int GPS_OUT_OF_SERVICE = 2;
        //???????????????????????????????????????????????????
        public static final int GPS_TEMPORARILY_UNAVAILABLE = 3;
        //??????????????????
        public static final int GPS_AVAILABLE = 4;
    }

    public interface GPSLocationListener {
        /**
         * ???????????????????????????????????????????????????
         *
         * @param location ????????????????????????Location??????
         */
        void UpdateLocation(Location location);

        /**
         * ???????????????provider?????????????????????????????????
         *
         * @param provider provider?????????
         * @param status   provider??????
         * @param extras   provider??????????????????????????????????????????????????????
         */
        void UpdateStatus(String provider, int status, Bundle extras);

        /**
         * ???????????????GPS?????????????????????????????????GPS??????????????????????????????GPS??????????????????GPS??????????????????GPS??????)
         *
         * @param gpsStatus ??????{@link GPSProviderStatus}
         */
        void UpdateGPSProviderStatus(int gpsStatus);

    }

    class MyListener implements GPSLocationListener {

        @Override
        public void UpdateLocation(Location location) {

            et_gps_1.setText( location.getLongitude() + "");
            et_gps_2.setText( location.getLatitude()  + "");
            et_gps_3.setText( location.getAltitude()  + "");
//            Log.e("gps==", "?????????" + location.getLongitude() + "\n?????????" + location.getLatitude());
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
//            if ("gps" == provider) {
//                Log.e("UpdateStatus--gps", "???????????????" + provider);
//            }
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {
            switch (gpsStatus) {

                case GPSProviderStatus.GPS_ENABLED:
                    //   Toast.makeText(MainActivity.this, "GPS??????", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_DISABLED:
                    //    Toast.makeText(MainActivity.this, "GPS??????", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                    //  Toast.makeText(MainActivity.this, "GPS?????????", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                    //  Toast.makeText(MainActivity.this, "??????GPS???????????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_AVAILABLE:
                    //     Toast.makeText(MainActivity.this, "GPS?????????", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}


