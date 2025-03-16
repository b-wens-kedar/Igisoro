
// Import the necessary classes
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * A class representing a pit that can contain seeds and sow them in
 * horizontally adjacent pits.
 *
 * @author Wens Kedar Barambona
 * @date 3/10/2024
 */
public class Pit extends JButton {
    private int numSeeds;
    public static ArrayList<ImageIcon> icons = new ArrayList<>();

    /**
     * Initializes the ImageIcons by loading images from the specified file paths,
     * resizing them to a standard size of 50x50 pixels, and adding them to the
     * static 'icons' ArrayList. This should be called before any pit is created.
     * 
     * All images got from wikimedia
     * (https://commons.wikimedia.org/wiki/Mancala_diagram)
     */
    public static void initializeImages() {
        for (int i = 0; i < 20; i++) {
            // All the images have a standard path and are named such that their index match
            // the number of seeds their represent.
            ImageIcon temp = new ImageIcon("images/Mancala_hole_(" + i + ").png");
            ImageIcon resizedTemp = resizeImageIcon(temp, 50, 50);
            icons.add(resizedTemp);
        }
    }

    /**
     * A static method that resizes an ImageIcon to the specified width and height.
     * Citattion:
     * https://stackoverflow.com/questions/14548808/scale-the-imageicon-automatically-to-label-size/14550112#14550112
     * Got idea for custom method but didn't want to use complicated rendering and
     * draw methods so used getScaledInstance though not recommended.
     *
     * @param originalIcon The original ImageIcon to be resized.
     * @param width        The desired width of the resized ImageIcon.
     * @param height       The desired height of the resized ImageIcon.
     *
     * @return A new ImageIcon that is resized to the specified width and height.
     */
    public static ImageIcon resizeImageIcon(ImageIcon originalIcon, int width, int height) {
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    /**
     * Constructor method that creates a pit containing the specified number of
     * seeds
     * 
     * @param numSeeds The number of seeds that this pit is to originally have.
     */
    public Pit(int numSeeds) {
        super(("" + numSeeds), icons.get(numSeeds));
        this.numSeeds = numSeeds;
    }

    /**
     * This method removes all seeds from the pit and updates the pit's icon and
     * text. Used to spread seeds when the pit is clicked.
     */
    public void sowSeeds() {
        numSeeds = 0;
        this.setIcon(icons.get(numSeeds));
        this.setText(Integer.toString(numSeeds));
    }

    /**
     * Increments the number of seeds in the pit by one and updates the pit's icon
     * and text. i.e this pit received a seed from an adjacent pit.
     */
    public void addseed() {
        numSeeds++;
        this.setIcon(icons.get(numSeeds));
        this.setText(Integer.toString(numSeeds));
    }

    /**
     * Sets the number of seeds in the pit to the specified value.
     *
     * @param numSeeds The new number of seeds to be set in the pit.
     */
    public void setNumSeeds(int numSeeds) {
        if (numSeeds >= 0) {
            this.numSeeds = numSeeds;
        }
    }

    /**
     * Retrieves the current number of seeds in the pit.
     *
     * @return The number of seeds currently in the pit.
     */
    public int getNumSeeds() {
        return numSeeds;
    }

    /**
     * Checks if the pit currently has any seeds.
     *
     * @return true if the pit contains at least one seed, false otherwise.
     */
    public boolean hasSeeds() {
        return (numSeeds == 0) ? false : true;
    }

}
