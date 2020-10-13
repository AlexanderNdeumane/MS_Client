package com.e.ms_client;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements VlcListener, MqttCallback {

    private static final String TAG = "";
    private static final String CHANNEL_ID = "dab";
    private VlcVideoLibrary vlcVideoLibrary;
    private Button bStartStop;
    private Button b_mqtt;
    private MqttClient client;
    private EditText etEndpoint;
    private String topic = "look";

    private String[] options = new String[]{":fullscreen"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        //
        bStartStop = (Button) findViewById(R.id.b_start_stop);
        bStartStop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (!vlcVideoLibrary.isPlaying()) {
                    vlcVideoLibrary.play(etEndpoint.getText().toString());
                    bStartStop.setText(getString(R.string.stop_player));
                } else {
                    vlcVideoLibrary.stop();
                    bStartStop.setText(getString(R.string.start_player));
                }
            }

        });
        //
        b_mqtt = (Button) findViewById(R.id.b_mqtt);
        //
        createClient();
        //
        b_mqtt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {

                if (!client.isConnected()) {
                    connectSubscribe();
                    b_mqtt.setText(getString(R.string.mqtt_disconnect));
                }
                else{
                    disconnectUnsubscribe();
                    b_mqtt.setText(getString(R.string.mqtt_connect));
                }
            }

        });
        //

        etEndpoint = (EditText) findViewById(R.id.et_endpoint);
        vlcVideoLibrary = new VlcVideoLibrary(this, this, surfaceView);
        vlcVideoLibrary.setOptions(Arrays.asList(options));


    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();
        bStartStop.setText(getString(R.string.start_player));
    }

    @Override
    public void onBuffering(MediaPlayer.Event event) {

    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "connectionLost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload());
        Log.d(TAG, payload);
        //textView.setText(payload.toString());
        if(payload.toString().equals("motion")){
            ActivateNotification();
        }
        Log.d(TAG, payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG, "deliveryComplete");
    }

    public void createClient(){
        try {
            client = new MqttClient("tcp://192.168.8.101:1883", "Subscriber", new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void connectSubscribe(){
        try {
            client.connect();
            client.setCallback(this);
            client.subscribe(topic,0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnectUnsubscribe(){
        try {
            client.unsubscribe(topic);
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void ActivateNotification(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Motion";
            String description = "Notification manager for on motion detected";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alert")
                .setContentText("Motion has been detected by your camera")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());

    }
}
