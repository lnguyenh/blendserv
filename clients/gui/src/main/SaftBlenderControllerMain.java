package main;

import GUI.SaftBlenderGUI;

import javax.swing.*;

/**
 * Entry point, creates the GUI.
 * Created by Gustav "Tylhadras" Lundström on 2/27/15.
 */
public class SaftBlenderControllerMain {

    public static void main(String [] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SaftBlenderGUI gui = new SaftBlenderGUI();
                gui.buildAndShow();
            }
        });
    }
}
