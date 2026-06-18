package Design;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollBarUI extends BasicScrollBarUI {

    private static final int THUMB_RADIUS = 10;

    public static void aplicar(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return criarBotaoVazio();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return criarBotaoVazio();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Tema.COR_FUNDO);
        g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(new Color(148, 163, 184));
        g2.fillRoundRect(
                thumbBounds.x + 2,
                thumbBounds.y + 2,
                thumbBounds.width - 4,
                thumbBounds.height - 4,
                THUMB_RADIUS,
                THUMB_RADIUS
        );

        g2.dispose();
    }

    private JButton criarBotaoVazio() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        button.setBorder(null);
        button.setFocusable(false);
        button.setContentAreaFilled(false);

        return button;
    }
}
