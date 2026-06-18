package Design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class FocusUtils {

    private FocusUtils() {
    }

    public static void limparFoco() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
    }

    public static void limparFocoAoClicar(JComponent componente) {
        componente.setFocusable(true);
        componente.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                limparFoco();
            }
        });
    }
}
