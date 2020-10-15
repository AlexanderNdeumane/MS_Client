package com.e.ms_client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pedro.vlc.VlcListener;
import com.pedro.vlc.VlcVideoLibrary;

import org.videolan.libvlc.MediaPlayer;

import java.util.Arrays;

public class LiveStream extends Fragment implements VlcListener{
    private VlcVideoLibrary vlcVideoLibrary;
    private Button bStartStop;
    private EditText etEndpoint;
    private String firstPartIp = "rtsp://192.168.";
    private String lastPartIp = ":554";
    SurfaceView surfaceView;
    private String[] options = new String[]{":fullscreen"};

    public LiveStream() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_stream, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SurfaceView surfaceView = view.findViewById(R.id.surfaceView);
        bStartStop = view.findViewById(R.id.b_start_stop);

        etEndpoint = view.findViewById(R.id.et_endpoint);
        vlcVideoLibrary = new VlcVideoLibrary(view.getContext(), this, surfaceView);
        vlcVideoLibrary.setOptions(Arrays.asList(options));

        bStartStop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try{
                    if (!vlcVideoLibrary.isPlaying()) {
                        vlcVideoLibrary.play(firstPartIp+""+etEndpoint.getText().toString()+""+lastPartIp);
                        bStartStop.setText(getString(R.string.stop_player));
                    } else if(vlcVideoLibrary.isPlaying()) {
                        vlcVideoLibrary.stop();
                        bStartStop.setText(getString(R.string.start_player));
                    }
                }
                catch (Exception e){

                }

            }

        });
    }

    @Override
    public void onComplete() {
        Toast.makeText(this.getActivity(), "Playing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(this.getActivity(), "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show();
        vlcVideoLibrary.stop();
        bStartStop.setText(getString(R.string.start_player));
    }

    @Override
    public void onBuffering(MediaPlayer.Event event) {

    }
}