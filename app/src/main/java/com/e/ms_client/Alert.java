package com.e.ms_client;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pedro.vlc.VlcListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.videolan.libvlc.MediaPlayer;

public class Alert extends Fragment implements MqttCallback {
    private Button b_mqtt;
    private String topic = "look";
    private EditText etServerIp;
    private String firstPartIp = "tcp://192.168.";
    private String lastPartIp = ":1883";
    private static final String TAG = "";
    private static final String CHANNEL_ID = "dab";
    private MqttClient client;
    public Alert() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        etServerIp = view.findViewById(R.id.etServerIp);
        b_mqtt = view.findViewById(R.id.b_mqtt);
        b_mqtt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                try{
                    createClient(firstPartIp+""+etServerIp.getText().toString()+""+lastPartIp);
                    if (!client.isConnected()) {
                        connectSubscribe();
                        b_mqtt.setText(getString(R.string.mqtt_disconnect));
                    }
                    else if(client.isConnected()){
                        disconnectUnsubscribe();
                        b_mqtt.setText(getString(R.string.mqtt_connect));
                    }
                }
                catch (Exception e){

                }
            }

        });
        //

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

    public void createClient(String ip){
        try {
            client = new MqttClient(ip, "Subscriber", new MemoryPersistence());
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
            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alert")
                .setContentText("Motion has been detected by your camera")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.getContext());
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());

    }
}