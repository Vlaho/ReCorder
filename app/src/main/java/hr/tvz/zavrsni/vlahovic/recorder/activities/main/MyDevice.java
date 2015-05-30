package hr.tvz.zavrsni.vlahovic.recorder.activities.main;

/**
 * Created by Vlaho on 24.5.2015..
 */
public class MyDevice {

    String hostName;
    String address;

    public MyDevice(String hostName, String address) {
        this.hostName = hostName;
        this.address = address;
    }

    public String getHostName() {
        return hostName;
    }

    public String getAddress() {
        return address;
    }
}
