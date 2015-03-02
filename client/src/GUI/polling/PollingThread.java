package GUI.polling;

import GUI.SaftBlenderGUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Thread that keeps polling the server for it's status.
 * Created by Gustav "Tylhadras" Lundström on 3/2/15.
 */
public class PollingThread implements Runnable {

    private HttpURLConnection connection;
    private SaftBlenderGUI controllerGUI;
    private URL url;

    public PollingThread(SaftBlenderGUI controllerGUI, URL url) {
        this.controllerGUI = controllerGUI;
        this.url = url;
    }

    @Override
    public void run() {
        while(true) {
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.getResponseCode();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

                if (connection != null) {
                    connection.disconnect();
                }

                controllerGUI.setStatusFieldDisplay(response.toString());

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                handleIt(e);
            } catch (IOException e) {
                handleIt(e);
            }
        }
    }

    private void handleIt(Exception e) {
        if (connection != null) {
            connection.disconnect();
        }
        controllerGUI.setStatusFieldDisplay("err");
        e.printStackTrace();
    }
}
