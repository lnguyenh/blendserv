package GUI;

import GUI.polling.PollingThread;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;

/**
 * Gui for sending on and off signals to the saftblandare.
 * Created by Gustav "Tylhadras" Lundström on 2/27/15.
 */
public class SaftBlenderGUI extends JFrame {

    private HttpURLConnection connection;
    private URL url;

    private int previousStatus;

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
    private JLabel imageLabel;
    private ImageIcon onImage, offImage;

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

        onImage = new ImageIcon("resources/strobe.gif");
        offImage = new ImageIcon("resources/strobe_off.gif");
        imageLabel = new JLabel();
        this.add(imageLabel);

        this.setTitle("Saft Blender Control Panel by Gustav The Awesome");
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        this.previousStatus = 999;

        //Dispatch polling thread
        Thread pollingThread = new Thread(new PollingThread(this, url));
        pollingThread.start();
    }

    private void sendPostRequest(int status) {
        try {
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String urlParams = "blender=" + Integer.toString(status);

            connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParams.getBytes().length));

            connection.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
            wr.writeBytes(urlParams);
            wr.flush ();
            wr.close ();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void setStatusFieldDisplay(String status) {
        int numberStatus = Integer.parseInt(status.substring(0,1));

        if(numberStatus == previousStatus) {
            return;
        } else {
            previousStatus = numberStatus;
        }

        if(numberStatus == 1) {
            imageLabel.setIcon(onImage);
        } else if(numberStatus == 0) {
            imageLabel.setIcon(offImage);
        } else {
            imageLabel.setText("Error");
        }
    }
}
