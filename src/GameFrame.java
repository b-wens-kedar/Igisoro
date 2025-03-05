import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GameFrame extends JFrame {
    JPanel mainPanel;
    JPanel centerPanel;
    JPanel p1Panel;
    JPanel p2Panel;
    ArrayList<JButton> buttons = new ArrayList<>();
    ArrayList<ImageIcon> icons = new ArrayList<>();

    public GameFrame(String title) {
        super(title);
        this.setSize(900, 300);
        this.setLocation(100, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        this.setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout());

        centerPanel = new JPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        p1Panel = new JPanel();
        centerPanel.add(p1Panel);
        p1Panel.setLayout(new GridLayout(2, 8));

        class middleLine extends JComponent {
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(500));
                g.drawLine(0, 0, 10000, 0);
            }
        }
        centerPanel.add(new middleLine());

        p2Panel = new JPanel();
        centerPanel.add(p2Panel);
        p2Panel.setLayout(new GridLayout(2, 8));

        for (int i = 0; i < 20; i++) {
            ImageIcon temp = new ImageIcon("images/128px-Mancala_hole_(" + i + ").png");
            ImageIcon resizedTemp = resizeImageIcon(temp, 50, 50);
            icons.add(resizedTemp);
        }

        class clicked implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton temp = (JButton) e.getSource();
                temp.setIcon(icons.get(0));
            }

        }

        for (int i = 0; i < 16; i++) {
            if (i >= 8 && i <= 15) {
                buttons.add(new JButton(("" + (1 + i)), icons.get(4)));
            } else {
                buttons.add(new JButton(("" + (1 + i)), icons.get(0)));
            }

            buttons.get(i).setFocusable(false);
            buttons.get(i).addActionListener(new clicked());
            p1Panel.add(buttons.get(i));
        }

        for (int i = 0; i < 16; i++) {
            if (i >= 0 && i <= 7) {
                buttons.add(new JButton(("" + (1 + i)), icons.get(4)));

            } else {
                buttons.add(new JButton(("" + (1 + i)), icons.get(0)));
            }

            buttons.get(i + 16).setFocusable(false);
            p2Panel.add(buttons.get(i + 16));
        }

        this.setVisible(true);
    }

    public static ImageIcon resizeImageIcon(ImageIcon originalIcon, int width, int height) {
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

}