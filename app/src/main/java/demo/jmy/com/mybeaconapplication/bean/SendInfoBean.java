package demo.jmy.com.mybeaconapplication.bean;

import java.util.List;

public class SendInfoBean {
    private String id;
    private List<BeaconInfo>data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BeaconInfo> getData() {
        return data;
    }

    public void setData(List<BeaconInfo> data) {
        this.data = data;
    }

    public static class BeaconInfo{
        private int major;
        private int minor;
        private int txPower;
        private int rssi;

        public BeaconInfo(int major, int minor, int rssi, int txPower) {
            this.major = major;
            this.minor = minor;
            this.txPower = txPower;
            this.rssi = rssi;
        }

        public int getMajor() {
            return major;
        }

        public void setMajor(int major) {
            this.major = major;
        }

        public int getMinor() {
            return minor;
        }

        public void setMinor(int minor) {
            this.minor = minor;
        }

        public int getTxPower() {
            return txPower;
        }

        public void setTxPower(int txPower) {
            this.txPower = txPower;
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }
    }
}
