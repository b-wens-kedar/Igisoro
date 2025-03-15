import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Pit extends JButton {
    private int numSeeds;
    private int index;
    public static ArrayList<ImageIcon> icons = new ArrayList<>();

    public static void initializeImages() {
        for (int i = 0; i < 20; i++) {
            ImageIcon temp = new ImageIcon("images/Mancala_hole_(" + i + ").png");
            ImageIcon resizedTemp = resizeImageIcon(temp, 50, 50);
            icons.add(resizedTemp);
        }
    }

    public static ImageIcon resizeImageIcon(ImageIcon originalIcon, int width, int height) {
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    public Pit(int value, int i) {
        super(("" + value), icons.get(value));
        numSeeds = value;
        this.index = i;
    }

    public void playPit() {
        numSeeds = 0;
        this.setIcon(icons.get(numSeeds));
        this.setText(Integer.toString(numSeeds));
    }

    public void addseed() {
        numSeeds++;
        this.setIcon(icons.get(numSeeds));
        this.setText(Integer.toString(numSeeds));
    }

    public void setNumSeeds(int numSeeds) {
        this.numSeeds = numSeeds;
    }

    public int getNumSeeds() {
        return numSeeds;
    }

    public int getIndex() {
        return index;
    }

    public boolean hasSeeds() {
        return (numSeeds == 0) ? false : true;
    }
}
