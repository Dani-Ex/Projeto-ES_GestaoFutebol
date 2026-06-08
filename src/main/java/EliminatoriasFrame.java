import javax.swing.*;
import java.awt.*;

public class EliminatoriasFrame extends JFrame {

    public EliminatoriasFrame(Campeonato campeonato) {
        setTitle("Eliminatórias - " + campeonato.getNome());
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        PaginaEliminatorias paginaEliminatorias = new PaginaEliminatorias(
                campeonato,
                () -> {
                    menuLateral.setVisible(!menuLateral.isVisible());
                    revalidate();
                    repaint();
                },
                pagina -> {
                    if (pagina.equals("grupos")) {
                        dispose();
                        new GruposFrame(campeonato);
                    }
                }
        );

        add(menuLateral, BorderLayout.WEST);
        add(paginaEliminatorias, BorderLayout.CENTER);

        setVisible(true);
    }
}