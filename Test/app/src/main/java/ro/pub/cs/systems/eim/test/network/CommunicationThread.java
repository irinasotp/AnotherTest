package ro.pub.cs.systems.eim.test.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import ro.pub.cs.systems.eim.test.Utilities;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("abc", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e("abc", "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i("abc", "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            String informationType = bufferedReader.readLine();  // citeste informatia de la client
            if (informationType == null || informationType.isEmpty()) {
                Log.e("abc", "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }
            //HashMap<String, WeatherForecastInformation> data = serverThread.getData();
            //WeatherForecastInformation weatherForecastInformation = null;
            String result;
            if(false) {//(data.containsKey(city)) {
                //Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                //weatherForecastInformation = data.get(city);
            } else {
                Log.i("abc", "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpPost = new HttpGet("https://api.coindesk.com/v1/bpi/currentprice.json");

                //List<NameValuePair> params = new ArrayList<>();
                //params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, city));
                //UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                //httpPost.setEntity(urlEncodedFormEntity);

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null) {
                    Log.e("abc", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

                Log.i("abc", pageSourceCode);
                printWriter.println(pageSourceCode);
                printWriter.flush();

            }

        } catch (IOException ioException) {
            Log.e("abc", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (true) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("abc", "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (true) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}