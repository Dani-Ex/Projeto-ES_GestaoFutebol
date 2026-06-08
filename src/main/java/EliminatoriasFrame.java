import GrupoEeleminatoria.PaginaEliminatorias;

import javax.swing.*;
import java.awt.*;

public class EliminatoriasFrame extends JFrame {

    public EliminatoriasFrame(String nomeCampeonato) {
        setTitle("Eliminatórias - " + nomeCampeonato);
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        PaginaEliminatorias paginaEliminatorias = new PaginaEliminatorias(
                nomeCampeonato,
                () -> {
                    menuLateral.setVisible(!menuLateral.isVisible());
                    revalidate();
                    repaint();
                },
                pagina -> {
                    if (pagina.equals("grupos")) {
                        dispose();
                        new GruposFrame(nomeCampeonato);
                    }
                }
        );

        add(menuLateral, BorderLayout.WEST);
        add(paginaEliminatorias, BorderLayout.CENTER);

        setVisible(true);
    }
}