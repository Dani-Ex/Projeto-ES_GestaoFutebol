package GrupoEeleminatoria;

import javax.swing.*;
import java.awt.*;
import Design.MenuLateral;
import Models.Campeonato;

public class GruposFrame extends JFrame {

    public GruposFrame(Campeonato campeonato) {
        setTitle("Models.Campeonato - " + campeonato.getNome());
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        PaginaGrupos paginaGrupos = new PaginaGrupos(
                campeonato,
                () -> {
                    menuLateral.setVisible(!menuLateral.isVisible());
                    revalidate();
                    repaint();
                },
                pagina -> {
                    if (pagina.equals("eliminatorias")) {
                        dispose();
                        new EliminatoriasFrame(campeonato);
                    }
                }
        );

        add(menuLateral, BorderLayout.WEST);
        add(paginaGrupos, BorderLayout.CENTER);

        setVisible(true);
    }
}