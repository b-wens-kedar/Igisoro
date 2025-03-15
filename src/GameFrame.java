import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameFrame extends JFrame {
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
    private Color blue = new Color(5, 28, 64);
    private Color green = new Color(28, 64, 5);
    private Color brown = new Color(64, 41, 5);

    public GameFrame(String title) {
        super(title);
        this.setSize(900, 385);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UIManager.put("Button.background", brown);
        UIManager.put("Button.foreground", Color.gray);
        UIManager.put("Button.select", brown);
        UIManager.put("Button.border", brown);

        mainPanel = new JPanel();
        this.setContentPane(mainPanel);
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
                index = thisPit.getIndex();

                if (myPits.contains(thisPit)) {
                    thesePits = myPits;
                    theOtherPits = otherPits;
                } else {
                    thesePits = otherPits;
                    theOtherPits = myPits;
                }

                if ((numSeeds > 1) && (whosTurn == myPits.contains(thisPit))) {
                    thesePits.get(index).playPit();

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
                            topPanel.setBackground(blue);
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
                            statusPanel.setBackground(blue);
                            topPanel.setBackground(blue);
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
                int i = thesePits.get(index).getIndex();

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
                    innerOtherPit.playPit();
                    outerOtherPit.playPit();

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
                myPits.add(new Pit(4, i));
                otherPits.add(new Pit(0, i));
            } else {
                myPits.add(new Pit(0, i));
                otherPits.add(new Pit(1, i));
            }

            myPits.get(i).setFocusable(false);
            otherPits.get(i).setFocusable(false);
            myPits.get(i).addActionListener(new playPit());
            otherPits.get(i).addActionListener(new playPit());
        }

        arrangePanels();

        this.setVisible(true);
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