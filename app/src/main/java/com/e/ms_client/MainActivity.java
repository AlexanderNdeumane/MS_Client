package com.e.ms_client;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.videolan.libvlc.MediaPlayer;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements VlcListener{
    private VlcVideoLibrary vlcVideoLibrary;
    private String firstPartIp = "rtsp://192.168.";
    private String lastPartIp = ":554";
    SurfaceView surfaceView;
    private String[] options = new String[]{":fullscreen"};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        Stream(getPreferencesIp());
        notificationService();
    }
    @Override
    protected void onStop() {
        super.onStop();
        vlcVideoLibrary = null;
    }
    protected void onRestart() {
        super.onRestart();
        super.onStop();
        Stream(getPreferencesIp());
        notificationService();
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pauseStreaming() {
        vlcVideoLibrary = null;
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resumeStreaming() {
        Stream(getPreferencesIp());
        notificationService();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onComplete() {
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onError() {
        Toast.makeText(this, "Please connect to wifi network or enter correct Server IP in settings", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary = null;
    }
    @Override
    public void onBuffering(MediaPlayer.Event event) {

    }
    public void Stream(String videoIp){
        try{
            surfaceView = findViewById(R.id.surfaceView);
            vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
            vlcVideoLibrary.setOptions(Arrays.asList(options));
            vlcVideoLibrary.play(firstPartIp+""+videoIp+""+lastPartIp);
        }
        catch (Exception e){

        }
    }
    public String getPreferencesIp(){
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String serverIp = sharedPref.getString
                (SettingsActivity.KEY_SERVER_IP,"8.100");
        return serverIp;
    }
    public void notificationService() {
        Intent intent = new Intent(this, MotionAlert.class);
        startService(intent);
    }
}
