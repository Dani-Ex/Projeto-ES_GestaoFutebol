package Design;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoundedButton extends JButton {

    private final int radius;

    public RoundedButton(String text, Color background, Color foreground) {
        this(text, background, foreground, 12);
    }

    public RoundedButton(String text, Color background, Color foreground, int radius) {
        super(text);
        this.radius = radius;

        setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 12));
        setBackground(background);
        setForeground(foreground);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new EmptyBorder(7, 14, 7, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();

        super.paintComponent(g);
    }
}
