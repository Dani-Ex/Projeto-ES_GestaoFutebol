package GrupoEeleminatoria;

import Design.MenuLateral;
import Models.Campeonato;

import javax.swing.*;
import java.awt.*;

public class EliminatoriasFrame extends JFrame {

    public EliminatoriasFrame(Campeonato campeonato) {

        if (!campeonato.isFaseGruposTerminada()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Ainda não podes abrir as eliminatórias.\nPrimeiro tens de terminar a fase de grupos.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            new GruposFrame(campeonato);
            return;
        }

        if (campeonato.getEquipasEliminatorias() == null || campeonato.getEquipasEliminatorias().isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Ainda não existem equipas classificadas para as eliminatórias.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

            new GruposFrame(campeonato);
            return;
        }

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