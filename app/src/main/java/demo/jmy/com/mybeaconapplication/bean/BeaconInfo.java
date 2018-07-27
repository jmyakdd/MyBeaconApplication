package demo.jmy.com.mybeaconapplication.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeaconInfo {
    private iBeaconClass.iBeacon iBeacon;
    private List<Integer> rssiList;
    private Integer rssi;
    private long currentTime;

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public BeaconInfo() {
        rssiList = new ArrayList<>();
    }

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        /*rssiList.add(rssi);
        this.rssi = getHalfValue();*/
        this.rssi = rssi;
    }

    public iBeaconClass.iBeacon getiBeacon() {
        return iBeacon;
    }

    public void setiBeacon(iBeaconClass.iBeacon iBeacon) {
        this.iBeacon = iBeacon;
    }

    public Integer getHalfValue() {
        Integer d = 0;
        Collections.sort(rssiList);
        d = rssiList.get(rssiList.size()-1);
        /*switch (rssiList.size()) {
            case 0:
                d = 0;
                break;
            case 1:
                d = rssiList.get(0);
                break;
            case 2:
                d = (rssiList.get(0) + rssiList.get(1)) / 2;
                break;
            default:
                if (rssiList.size() % 2 == 1) {
                    d = rssiList.get(rssiList.size() / 2);
                } else {
                    d = (rssiList.get(rssiList.size() / 2) + rssiList.get(rssiList.size() / 2 - 1)) / 2;
                }
                break;
        }*/
        return d;
    }

    @Override
    public String toString() {
        return "BeaconInfo{" +iBeacon.minor+
                ", rssiList=" + rssiList +
                '}';
    }
}
