package demo.jmy.com.mybeaconapplication.util;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataSendUtil {
    private static DatagramPacket packet;
    private static DatagramSocket socket;
    private static ExecutorService executor;

    static {
        executor = Executors.newSingleThreadExecutor();
        try {
            socket = new DatagramSocket(7088);
        } catch (SocketException e) {
            e.printStackTrace();
            Log.e("test",e.toString());
        }
    }

    public static void sendData(final byte[] data) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    packet = new DatagramPacket(data, data.length);
                    packet.setPort(7983);
                    packet.setAddress(InetAddress.getByName("192.168.2.133"));
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test",e.toString());
                }
            }
        });
    }
}
