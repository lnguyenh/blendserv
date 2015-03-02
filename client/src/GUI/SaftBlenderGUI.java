package GUI;

import GUI.polling.PollingThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

/**
 * Gui for sending on and off signals to the saftblandare.
 * Created by Gustav "Tylhadras" Lundström on 2/27/15.
 */
public class SaftBlenderGUI extends JFrame {

    private HttpURLConnection connection;
    private URL url;

    private String userName;
    private String passWord;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPassword() {
        return this.passWord;
    }

    private JButton onButton;
    private JButton offButton;

    private JLabel statusField;
    private JLabel statusDesc;

    /***
     * Default constructor, with default values
     */
    public SaftBlenderGUI() {
        setUserName("foop");
        setPassWord("froopberry");
        try {
            url = new URL("http://scramble.se:8192");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(getUserName(), getPassword().toCharArray());
            }
        });
    }

    public void buildAndShow() {
        this.setSize(480, 60);
        this.setLayout(new FlowLayout());
        onButton = new JButton("ON!");
        onButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Send the on signal.
                sendPostRequest(1);
            }
        });
        this.add(onButton);
        offButton = new JButton("OFF!");
        offButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Send the off signal.
                sendPostRequest(0);
            }
        });
        this.add(offButton);
        statusDesc = new JLabel("Status:");
        this.add(statusDesc);
        statusField = new JLabel();
        this.add(statusField);
        this.setTitle("Saft Blender Control Panel by Gustav The Awesome");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        //Dispatch polling thread
        new Thread(new PollingThread(this, url)).start();
    }

    private void sendPostRequest(int status) {
        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String urlParams = "blender=" + Integer.toString(status);
            System.out.println("URL: " + url);
            System.out.println("URLParams: " + urlParams);

            //connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParams.getBytes().length));

            //connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
            wr.writeBytes(urlParams);
            wr.flush ();
            wr.close ();

//            InputStream is = connection.getInputStream();
//            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
//            String line;
//            StringBuffer response = new StringBuffer();
//            while((line = rd.readLine()) != null) {
//                response.append(line);
//                response.append('\r');
//            }
//            rd.close();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void setStatusFieldDisplay(String status) {
        this.statusField.setText(status);
    }
}
