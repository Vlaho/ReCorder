package hr.tvz.zavrsni.vlahovic.recorder.activities.main.activities;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import hr.tvz.zavrsni.vlahovic.recorder.R;
import hr.tvz.zavrsni.vlahovic.recorder.activities.main.MyDevice;

public class MainActivity extends AppCompatActivity {

    private Button disconnectButton;
    private ImageButton recordButton;
    private DatagramPacket packet;
    private DatagramSocket streamingSocket;
    private int port=4445;
    private AudioRecord recorder;
    private InetAddress destination;
    private int sampleRate = 8000 ;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;
    private MyDevice connectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();
        String[] parts = bundle.getString("ConnectedDevice").split("/");
        connectedDevice = new MyDevice(parts[0],parts[1]);

        try {
            destination = InetAddress.getByName(connectedDevice.getAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        recordButton = (ImageButton) findViewById(R.id.recordButton);
        disconnectButton=(Button) findViewById(R.id.disconnectButton);


        Thread streamStartedThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress(InetAddress.getByName(connectedDevice.getAddress()), 4444), 100);
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeObject("STREAMING");
                    output.flush();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        streamStartedThread.start();

        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        status = true;
                        startStreaming();
                        break;

                    case MotionEvent.ACTION_UP:
                        status = false;
                        break;
                }

                return true;
            }
        });


        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = false;
                startActivity(new Intent(MainActivity.this, DeviceConnectionActivity.class));
            }
        });
    }
    public void startStreaming(){
        Thread streamThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    streamingSocket = new DatagramSocket();
                    byte[] buffer = new byte[minBufSize];

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize);
                    recorder.getState();
                    recorder.startRecording();

                    while(status) {
                        //reading data from MIC into buffer
                        recorder.read(buffer, 0, buffer.length);
                        //putting buffer in the packet
                        packet = new DatagramPacket (buffer,buffer.length,destination,port);

                        streamingSocket.send(packet);

                        System.out.println("MinBufferSize: " +minBufSize);
                    }
                    recorder.release();
                } catch(UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        streamThread.start();
    }
}
