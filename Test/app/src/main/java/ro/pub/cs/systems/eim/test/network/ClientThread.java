package ro.pub.cs.systems.eim.test.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.test.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private String informationType;
    private TextView bitcoinValueTextView;

    private Socket socket;

    public ClientThread(String address, int port, String informationType, TextView bitcoinValueTextView) {
        this.address = address;
        this.port = port;
        this.informationType = informationType;
        this.bitcoinValueTextView = bitcoinValueTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e("abc", "[CLIENT THREAD] Could not create socket!");
                return;
            }
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("abc", "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            // Send info la server
            printWriter.println(informationType);
            Log.i("abc", informationType);
            printWriter.flush();
            String valueInformation;
            while ((valueInformation = bufferedReader.readLine()) != null) {
                final String finalizedWeateherInformation = valueInformation;
                bitcoinValueTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        bitcoinValueTextView.setText(finalizedWeateherInformation);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e("abc", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (true) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("abc", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (true) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}