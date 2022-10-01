package dev.Volatile.Chopper;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class Gui {

    /**
     * Runs the GUI and adds all relevant settings.
     * @param main Main method
     */
    public void run(Main main) {

        JFrame jFrame = new JFrame("Volatile Chopper");
        jFrame.setSize(300, 500);
        jFrame.setResizable(false);

        JPanel settingsPanel = new JPanel();
        TitledBorder leftBorder = BorderFactory.createTitledBorder("Settings");
        leftBorder.setTitleJustification(TitledBorder.LEFT);
        settingsPanel.setBorder(leftBorder);
        settingsPanel.setLayout(null);
        settingsPanel.setBounds(5, 200, 280, 180);
        jFrame.add(settingsPanel);

        JPanel startPanel = new JPanel();
        startPanel.setLayout(null);
        startPanel.setBounds(5, 350, 70, 20);
        jFrame.add(startPanel);

        JLabel treeSelection = new JLabel("Select a Tree:");
        treeSelection.setBounds(10, 40, 95, 20);
        settingsPanel.add(treeSelection);
        JComboBox<String> treeList = new JComboBox<>(new String[]{"None", "Tree", "Oak", "Willow", "Maple tree", "Yew", "Magic tree"});
        treeList.addActionListener(e -> main.tree = (String) treeList.getSelectedItem());
        treeList.setBounds(160, 40, 110, 20);
        settingsPanel.add(treeList);

        JLabel treeLocation = new JLabel("Select a Location:");
        treeLocation.setBounds(10, 90, 95, 20);
        settingsPanel.add(treeLocation);
        JComboBox<String> locationList = new JComboBox<>(new String[]{"Varrock", "Draynor", "Camelot", "Grand Exchange"});
        locationList.addActionListener(e -> main.location = (String) locationList.getSelectedItem());
        locationList.setBounds(160, 90, 110, 20);
        settingsPanel.add(locationList);

        JLabel axeSelection = new JLabel("Select an Axe:");
        axeSelection.setBounds(10, 140, 95, 20);
        settingsPanel.add(axeSelection);
        JComboBox<String> axeList = new JComboBox<>(new String[]{"Bronze axe", "Iron axe", "Steel axe", "Black axe", "Mithril axe", "Adamant axe", "Rune axe", "Dragon axe"});
        axeList.addActionListener(e -> main.axe = (String) axeList.getSelectedItem());
        axeList.setBounds(160, 140, 110, 20);
        settingsPanel.add(axeList);

        JButton startButton = new JButton("Start Chopping!");
        startButton.addActionListener(e -> {
            synchronized (main.lock) {
                main.lock.notify();
            }
            jFrame.setVisible(false);
            main.canStart = true;
        });
        startButton.setBounds(5, 390, 120, 20);
        startPanel.add(startButton);

        jFrame.setVisible(true);
    }
}
