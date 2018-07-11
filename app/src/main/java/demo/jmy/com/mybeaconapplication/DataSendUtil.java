package demo.jmy.com.mybeaconapplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSendUtil {
    private static DatagramPacket packet;
    private static DatagramSocket socket;
    private static ExecutorService executor;
    private static InetAddress address;

    static {
        executor = Executors.newSingleThreadExecutor();
        try {
            socket = new DatagramSocket(7088);
            address = InetAddress.getByName("192.168.154.178");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void sendData(final byte[] data) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    packet = new DatagramPacket(data, data.length);
                    packet.setPort(7088);
                    packet.setAddress(address);
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}