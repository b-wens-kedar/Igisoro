
//import necessary classes
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A frame class representing the board of the Igisoro/Urubugu game.
 * Offers a interactive GUI interface version of the game and contains most of
 * the games implementation.
 * See README.md for game description.
 * 
 * @author Wens Kedar Barambona
 * @date 3/10/2025
 */
public class GameFrame extends JFrame {
    // Declare the elements of the first/instruction panel
    private JPanel FirstPanel;
    private JLabel instructions;
    private String fileContent;
    private JButton startButton;
    File file = new File("README.md");
    Scanner fscnr = new Scanner(file);

    // Declare all other game elements
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

    /**
     * No arg constructor that creates the game frame with specified title
     * 
     * @param title the title given to the frame
     * @throws FileNotFoundException throws FNFE to recognize the checked exception.
     */
    public GameFrame(String title) throws FileNotFoundException {
        // Create the Gameframe with given title and assign location, size, and
        // defaultCloseOperation properties
        super(title);
        this.setSize(1000, 700);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Modify the UIManager to change properties of button components at ounce.
        // Citation:
        // https://stackoverflow.com/questions/69040286/is-there-a-method-to-disable-the-blue-box-when-i-click-a-jbutton
        // https://gist.github.com/ezhov-da/357f80b3182cce3ece19d47935d377b8
        UIManager.put("Button.background", brown);
        UIManager.put("Button.foreground", Color.gray);
        UIManager.put("Button.select", brown);
        UIManager.put("Button.border", brown);

        // Initialize the firstPanel, assign Boxlayout manager, border and Background
        FirstPanel = new JPanel();
        FirstPanel.setLayout(new BoxLayout(FirstPanel, BoxLayout.Y_AXIS));
        FirstPanel.setBorder(new EmptyBorder(-10, 30, 0, 30));
        FirstPanel.setBackground(Color.black);

        // Initialize the instructions JLabel, and set it's font and foreground
        instructions = new JLabel();
        instructions.setFont(new Font("", Font.BOLD, 14));
        instructions.setForeground(Color.white);

        // Read the contents of the README.md file into the String fileContent
        // Appending line breaks after each line, then close the fscnr
        fileContent = "";
        while (fscnr.hasNextLine()) {
            fileContent += (fscnr.nextLine() + "<br>");
        }
        fscnr.close();

        // Set fileContent as instructions' text and add the JLabel to FirstPanel
        instructions.setText(fileContent);
        FirstPanel.add(instructions);

        // Initialize the startButton, set its font, background,focusable, and border
        // properties
        startButton = new JButton("START GAME");
        startButton.setForeground(Color.white);
        startButton.setBackground(green);
        startButton.setFocusable(false);
        startButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        // Add an ActionaListener to startButton, which adjusts the frames size and
        // switches this' ContentPane to mainPanel, which is fully loaded by then.
        startButton.addActionListener(e -> {
            this.setSize(900, 385);
            this.setContentPane(mainPanel);
        });
        // Add the startButton to FirstPanel
        FirstPanel.add(startButton);

        // Add the FirstPanel to the GameFrame
        this.setContentPane(FirstPanel);
        this.setVisible(true);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Initialize the topPanel which contains Player 2's status bar (p2Status Label)
        // showing their remaining seeds
        topPanel = new JPanel();
        topPanel.setBackground(Color.gray);
        p2Status = new JLabel("Player 2 : " + p2NumSeeds + " seeds");
        p2Status.setForeground(Color.white);
        topPanel.add(p2Status);
        mainPanel.add(topPanel);

        // Initialize the p2Panel which contains Player 2's pits, with a Gridlayout
        // manager
        p2Panel = new JPanel();
        p2Panel.setLayout(new GridLayout(2, 8));
        p2Panel.setPreferredSize(new Dimension(900, 140));
        mainPanel.add(p2Panel);

        // Initialize game status panel which contains the status label indicating whos
        // turn it is
        statusPanel = new JPanel();
        gameStatus = new JLabel("Player 1's turn");
        statusPanel.add(gameStatus);
        mainPanel.add(statusPanel);

        // Initialize the p1Panel which contains Player 2's pits, with a Gridlayout
        // manager
        p1Panel = new JPanel();
        p1Panel.setLayout(new GridLayout(2, 8));
        p1Panel.setPreferredSize(new Dimension(900, 140));
        mainPanel.add(p1Panel);

        // Initialize the bottomPanel which contains Player 1's status bar (p1Status
        // JLabel)
        // showing their remaining seeds
        bottomPanel = new JPanel();
        bottomPanel.setBackground(green);
        p1Status = new JLabel("Player 1 : " + p1NumSeeds + " seeds");
        p1Status.setForeground(Color.white);
        bottomPanel.add(p1Status);
        mainPanel.add(bottomPanel);

        // Call static initializeImages to make resized ImageIcons of all pit images
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

                // make other arraylist to know who is who
                if (myPits.contains(thisPit)) {
                    thesePits = myPits;
                    theOtherPits = otherPits;
                } else {
                    thesePits = otherPits;
                    theOtherPits = myPits;
                }
                index = thesePits.indexOf(thisPit);

                // Only do anything when this pit has more than one seed and its this players
                // turn
                if ((numSeeds > 1) && (whosTurn == myPits.contains(thisPit))) {
                    thisPit.sowSeeds();

                    // sows the seeds of this pit to adjacent pits
                    for (int i = 0; i < numSeeds; i++) {
                        if (index >= 0 && index <= 15) {
                            index = (index == 15) ? -1 : index;
                            index++;
                            thesePits.get(index).addseed();
                        }
                    }

                    // Only runs on the last pit to recieve a seed
                    if (thesePits.get(index).getNumSeeds() == 1) {
                        // Updates whos turn and status bars colors
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

                        // check if either player is out of moves to update status bars by calling the
                        // outOfMoves method
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

                    // checks if the last pit to recieve a seed has more than one seed and is inner
                    // pit to call stealAcross method
                    if (thesePits.get(index).getNumSeeds() > 1 && isInnerPit(index)) {
                        stealAcross(index);
                    }

                    // do to sow again from the last pit to recieve a seed if the pit has more than
                    // one seed
                    thesePits.get(index).doClick();
                }
            }

            // Checks if pit is an inner pit using logical index placement
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

            /**
             * Checks if requirements to capture opponent's seeds are met and if captures
             * the seeds and sows
             */
            public void stealAcross(int index) {
                // Get opponent's pits
                innerOtherPit = theOtherPits.get(index);
                outerOtherPit = theOtherPits.get(numPits - index);

                // if both pits have seeds, calculate totalseeds and assign the pits value to
                // zero
                if (innerOtherPit.hasSeeds() && outerOtherPit.hasSeeds()) {
                    numSeeds = innerOtherPit.getNumSeeds() + outerOtherPit.getNumSeeds();
                    innerOtherPit.sowSeeds();
                    outerOtherPit.sowSeeds();

                    // update status bar depending on who made the steal
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

                    // set this pits seed value as numSeeds captured and doclick to sow seeds from
                    // original sowing position
                    thisPit.setNumSeeds(numSeeds);
                    thisPit.doClick();
                }
            }

            /**
             * Checks if a player is out of moves by checking if any of their pits has
             * atleast two seeds in them,
             * 
             * @return true if out of moves
             */
            public boolean outOfMoves(ArrayList<Pit> pitList) {
                boolean outOfMoves = true;

                for (Pit pit : pitList) {
                    if (pit.getNumSeeds() > 1) {
                        outOfMoves = false;
                    }
                }

                return outOfMoves;
            }
        }

        // Iterate 16 to creates the Arraylists of Pits with proper initial number of
        // seeds and adding half to myPits and the other half to theOtherPits
        for (int i = 0; i <= numPits; i++) {
            // Assign 4 as initial seed value to inner pits (closest to center)
            if ((i >= 8 && i < 16)) {
                myPits.add(new Pit(4));
                otherPits.add(new Pit(0));
            } else {
                myPits.add(new Pit(0));
                otherPits.add(new Pit(4));
            }

            // Set focusable to false and add action listener to all pits
            myPits.get(i).setFocusable(false);
            otherPits.get(i).setFocusable(false);
            myPits.get(i).addActionListener(new playPit());
            otherPits.get(i).addActionListener(new playPit());
        }

        // Call arrange method to add the pits to their respective panels in the proper
        // order
        arrangePanels();
    }

    /**
     * Arranges the pits in the ArrayList so that pits with 4 seeds are inner pits
     * and the other outer pits
     * i.e manipulating the gridlayout of panels to make the rest of game
     * implementation easier
     */
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