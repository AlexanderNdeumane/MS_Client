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
    //Variable declarations
    private VlcVideoLibrary vlcVideoLibrary;
    private String firstPartIp = "rtsp://192.168.";
    private String lastPartIp = ":554";
    SurfaceView surfaceView;
    private String[] options = new String[]{":fullscreen"};
	/*
	Method starts up the mobile app
	*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        Stream(getPreferencesIp());
        notificationService();
    }
    /*
    Overrides onStop() method of VLC library
    turns off the stream
     */
	@Override
    protected void onStop() {
        super.onStop();
        vlcVideoLibrary = null;
    }
    /*
    Method refreshes Connect number
    and restarts the notification service
     */
    protected void onRestart() {
        super.onRestart();
        super.onStop();
        Stream(getPreferencesIp());
        notificationService();
    }
    /*
    Stops streaming video when the
    main activity is paused
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pauseStreaming() {
        vlcVideoLibrary = null;
    }
    /*
    Starts stream and notification service when main
    activity restarts
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resumeStreaming() {
        Stream(getPreferencesIp());
        notificationService();
    }
    /*
    Populates the menu with items
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }
    /*
    Decides which activity is opened
    depending on which menu item is selected
     */
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
    /*
    Overrides onComplete() method of VLC library
    shows text when it connects to the stream
     */
    @Override
    public void onComplete() {
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
    }
    /*
    Overrides onError() method of VLC library
    gives user a warning message when it fails to connect
     */
    @Override
    public void onError() {
        Toast.makeText(this, "Please connect to wifi network or enter the correct connect number in settings", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary = null;
    }
    /*
    Overrides onBuffering() method of VLC library
     */
    @Override
    public void onBuffering(MediaPlayer.Event event) {

    }
	/*
	Method connects to the streaming server that allows the mobile
	app to receive receive and view the live stream
	*/
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
    /*
    Method gets the latest connect number
    the user has put it in
     */
    public String getPreferencesIp(){
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String serverIp = sharedPref.getString
                (SettingsActivity.KEY_SERVER_IP,"8.100");
        return serverIp;
    }
    /*
    Method starts the notification service
    if the user enables notifications
     */
    public void notificationService() {
        Intent intent = new Intent(this, MotionAlert.class);
        try{
            startService(intent);
        }
        catch (Exception e){

        }
    }
}
