import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GameFrame extends JFrame {
    private JPanel FirstPanel;
    private JLabel instructions;
    private String fileContent;
    private JButton startButton;
    File file = new File("README.md");
    Scanner fscnr = new Scanner(file);

    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel p1Panel;
    private JPanel statusPanel;
    private JPanel p2Panel;
    private JPanel bottomPanel;
    private JLabel p1Status;
    private JLabel gameStatus;
    private JLabel p2Status;
    private ArrayList<Pit> myPits = new ArrayList<>();
    private ArrayList<Pit> otherPits = new ArrayList<>();
    private final int numPits = 15;
    private int p1NumSeeds = 32;
    private int p2NumSeeds = 32;
    private boolean whosTurn = true;
    private Color red = new Color(100, 0, 5);
    private Color green = new Color(28, 64, 5);
    private Color brown = new Color(64, 41, 5);

    public GameFrame(String title) throws FileNotFoundException {
        super(title);
        this.setSize(1000, 700);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UIManager.put("Button.background", brown);
        UIManager.put("Button.foreground", Color.gray);
        UIManager.put("Button.select", brown);
        UIManager.put("Button.border", brown);

        FirstPanel = new JPanel();
        FirstPanel.setLayout(new BoxLayout(FirstPanel, BoxLayout.Y_AXIS));
        FirstPanel.setBorder(new EmptyBorder(-10, 30, 0, 30));
        FirstPanel.setBackground(Color.black);

        instructions = new JLabel();
        instructions.setFont(new Font("", Font.BOLD, 14));
        instructions.setForeground(Color.white);

        fileContent = "";
        while (fscnr.hasNextLine()) {
            fileContent += (fscnr.nextLine() + "<br>");
        }
        fscnr.close();

        instructions.setText(fileContent);
        FirstPanel.add(instructions);

        startButton = new JButton("START GAME");
        startButton.setForeground(Color.white);
        startButton.setBackground(green);
        startButton.setFocusable(false);
        startButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        startButton.addActionListener(e -> {
            this.setSize(900, 385);
            this.setContentPane(mainPanel);
        });
        FirstPanel.add(startButton);

        this.setContentPane(FirstPanel);
        this.setVisible(true);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        topPanel = new JPanel();
        topPanel.setBackground(Color.gray);
        p2Status = new JLabel("Player 2 : " + p2NumSeeds + " seeds");
        p2Status.setForeground(Color.white);
        topPanel.add(p2Status);
        mainPanel.add(topPanel);

        p2Panel = new JPanel();
        p2Panel.setLayout(new GridLayout(2, 8));
        p2Panel.setPreferredSize(new Dimension(900, 140));
        mainPanel.add(p2Panel);

        statusPanel = new JPanel();
        gameStatus = new JLabel("Player 1's turn");
        statusPanel.add(gameStatus);
        mainPanel.add(statusPanel);

        p1Panel = new JPanel();
        p1Panel.setLayout(new GridLayout(2, 8));
        p1Panel.setPreferredSize(new Dimension(900, 140));
        mainPanel.add(p1Panel);

        bottomPanel = new JPanel();
        bottomPanel.setBackground(green);
        p1Status = new JLabel("Player 1 : " + p1NumSeeds + " seeds");
        p1Status.setForeground(Color.white);
        bottomPanel.add(p1Status);
        mainPanel.add(bottomPanel);

        ////////////////////////////
        Pit.initializeImages();

        class playPit implements ActionListener {
            Pit thisPit;
            Pit innerOtherPit;
            Pit outerOtherPit;
            ArrayList<Pit> thesePits;
            ArrayList<Pit> theOtherPits;
            int numSeeds;
            int index;

            @Override
            public void actionPerformed(ActionEvent e) {
                thisPit = (Pit) e.getSource();
                numSeeds = thisPit.getNumSeeds();

                if (myPits.contains(thisPit)) {
                    thesePits = myPits;
                    theOtherPits = otherPits;
                } else {
                    thesePits = otherPits;
                    theOtherPits = myPits;
                }
                index = thesePits.indexOf(thisPit);

                if ((numSeeds > 1) && (whosTurn == myPits.contains(thisPit))) {
                    thisPit.sowSeeds();

                    for (int i = 0; i < numSeeds; i++) {
                        if (index >= 0 && index <= 15) {
                            index = (index == 15) ? -1 : index;
                            index++;
                            thesePits.get(index).addseed();
                        }
                    }

                    if (thesePits.get(index).getNumSeeds() == 1) {
                        if (whosTurn) {
                            gameStatus.setText("Player 2's turn");
                            topPanel.setBackground(red);
                            bottomPanel.setBackground(Color.gray);
                            whosTurn = false;
                        } else {
                            gameStatus.setText("Player 1's turn");
                            topPanel.setBackground(Color.gray);
                            bottomPanel.setBackground(green);
                            whosTurn = true;
                        }

                        if (outOfMoves(myPits)) {
                            gameStatus.setText("Player 2 won");
                            gameStatus.setForeground(Color.white);
                            statusPanel.setBackground(red);
                            topPanel.setBackground(red);
                        } else if (outOfMoves(otherPits)) {
                            gameStatus.setText("Player 1 won");
                            gameStatus.setForeground(Color.white);
                            statusPanel.setBackground(green);
                            bottomPanel.setBackground(green);
                        }
                    }

                    if (thesePits.get(index).getNumSeeds() > 1 && isInnerPit(index)) {
                        stealAcross(index);
                    }

                    thesePits.get(index).doClick();
                }
            }

            public boolean isInnerPit(int index) {
                int i = thesePits.indexOf(thesePits.get(index));

                if (myPits.contains(thisPit) && i >= 8 && i <= 15) {
                    return true;
                }
                if (!myPits.contains(thisPit) && i >= 0 && i <= 7) {
                    return true;
                }

                return false;
            }

            public void stealAcross(int index) {
                innerOtherPit = theOtherPits.get(index);
                outerOtherPit = theOtherPits.get(numPits - index);

                if (innerOtherPit.hasSeeds() && outerOtherPit.hasSeeds()) {
                    numSeeds = innerOtherPit.getNumSeeds() + outerOtherPit.getNumSeeds();
                    innerOtherPit.sowSeeds();
                    outerOtherPit.sowSeeds();

                    if (myPits.contains(thisPit)) {
                        p1NumSeeds += numSeeds;
                        p2NumSeeds -= numSeeds;
                        p1Status.setText("Player 1 : " + p1NumSeeds + " seeds");
                        p2Status.setText("Player 2 : " + p2NumSeeds + " seeds");
                    } else {
                        p1NumSeeds -= numSeeds;
                        p2NumSeeds += numSeeds;
                        p1Status.setText("Player 1 : " + p1NumSeeds + " seeds");
                        p2Status.setText("Player 2 : " + p2NumSeeds + " seeds");
                    }

                    thisPit.setNumSeeds(numSeeds);
                    thisPit.doClick();
                }
            }

            public static boolean outOfMoves(ArrayList<Pit> pitList) {
                boolean outOfMoves = true;

                for (Pit pit : pitList) {
                    if (pit.getNumSeeds() > 1) {
                        outOfMoves = false;
                    }
                }

                return outOfMoves;
            }
        }

        for (int i = 0; i <= numPits; i++) {

            if ((i >= 8 && i < 16)) {
                myPits.add(new Pit(4));
                otherPits.add(new Pit(0));
            } else {
                myPits.add(new Pit(0));
                otherPits.add(new Pit(4));
            }

            myPits.get(i).setFocusable(false);
            otherPits.get(i).setFocusable(false);
            myPits.get(i).addActionListener(new playPit());
            otherPits.get(i).addActionListener(new playPit());
        }

        arrangePanels();
    }

    public void arrangePanels() {
        for (int i = 0; i < (numPits * 2); i++) {
            if (i < 8) {
                p1Panel.add(myPits.get(15 - i));
                p2Panel.add(otherPits.get(15 - i));
            }
            if (i >= 8 && i < 16) {
                p1Panel.add(myPits.get(i - 8));
                p2Panel.add(otherPits.get(i - 8));
            }
        }
    }
}