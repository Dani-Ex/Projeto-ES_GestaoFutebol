import GrupoEeleminatoria.PaginaGrupos;

import javax.swing.*;
import java.awt.*;

public class GruposFrame extends JFrame {

    public GruposFrame(String nomeCampeonato) {
        setTitle("Campeonato - " + nomeCampeonato);
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        PaginaGrupos paginaGrupos = new PaginaGrupos(
                nomeCampeonato,
                () -> {
                    menuLateral.setVisible(!menuLateral.isVisible());
                    revalidate();
                    repaint();
                },
                pagina -> {
                    if (pagina.equals("eliminatorias")) {
                        dispose();
                        new EliminatoriasFrame(nomeCampeonato);
                    }
                }
        );

        add(menuLateral, BorderLayout.WEST);
        add(paginaGrupos, BorderLayout.CENTER);

        setVisible(true);
    }
}