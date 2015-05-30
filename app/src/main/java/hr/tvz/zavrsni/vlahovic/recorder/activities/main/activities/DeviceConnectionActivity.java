package hr.tvz.zavrsni.vlahovic.recorder.activities.main.activities;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import org.apache.commons.lang3.ArrayUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import hr.tvz.zavrsni.vlahovic.recorder.R;
import hr.tvz.zavrsni.vlahovic.recorder.activities.main.MyDevice;
import hr.tvz.zavrsni.vlahovic.recorder.activities.main.adapters.DeviceListAdapter;


public class DeviceConnectionActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private List<MyDevice> availableDevices;
    private String myIPAddress;
    private DeviceListAdapter deviceListAdapter;
    private ImageButton refreshButton;
    private ListView deviceList;
    private android.os.Handler handler;
    private Button connectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connection);

        connectButton = (Button) findViewById(R.id.connectButton);
        refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        deviceList = (ListView) findViewById(R.id.deviceList);
        handler = new android.os.Handler();
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wifiManager.isWifiEnabled()) {
                    Toast.makeText(DeviceConnectionActivity.this, "Searching for devices...please wait", Toast.LENGTH_LONG).show();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            availableDevices = getAvailableDevices();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DeviceConnectionActivity.this, "Searching for devices finished!", Toast.LENGTH_LONG).show();
                                    if(availableDevices.isEmpty()) {
                                        Toast.makeText(DeviceConnectionActivity.this,"No devices found!",Toast.LENGTH_LONG).show();
                                    }else {
                                        deviceListAdapter = new DeviceListAdapter(DeviceConnectionActivity.this, availableDevices);
                                        deviceList.setAdapter(deviceListAdapter);
                                    }
                                }
                            });
                        }
                    });
                    thread.start();
                } else {
                    Toast.makeText(DeviceConnectionActivity.this,"Enable Wifi first!",Toast.LENGTH_LONG).show();
                }
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DeviceConnectionActivity.this,MainActivity.class));
            }
        });
    }

    public List<MyDevice> getAvailableDevices(){

        wifiInfo = wifiManager.getConnectionInfo();
        byte[] myIP = BigInteger.valueOf(wifiInfo.getIpAddress()).toByteArray();
        ArrayUtils.reverse(myIP);

        try {
            InetAddress myInetIP = InetAddress.getByAddress(myIP);
            myIPAddress = myInetIP.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ArrayList<MyDevice> deviceList = new ArrayList<>();
        InetAddress currentPingAddress;
        String[] myIPArray = myIPAddress.split("\\.");
        Socket socket;
        ObjectInputStream input;
        ObjectOutputStream output;

        for(int i=0; i<256; i++) {
            try {
                currentPingAddress = InetAddress.getByName(myIPArray[0]+"."+myIPArray[1]+"."+myIPArray[2]+"."+Integer.toString(i));

                socket = new Socket();
                socket.connect(new InetSocketAddress(currentPingAddress, 4444), 100);

                input = new ObjectInputStream(socket.getInputStream());
                output = new ObjectOutputStream(socket.getOutputStream());
                String echo = (String) input.readObject();

                String[] parts = echo.split("/");
                System.out.println(parts[0]);
                System.out.println(parts[1]);
                MyDevice device = new MyDevice(parts[0],parts[1]);

                deviceList.add(device);
                output.writeObject("ENDPING");
                output.flush();
                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return deviceList;
    }
}
