package ui.paginas;

import ui.components.PrimaryButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class PaginaEliminatorias extends JPanel {

    private final Runnable alternarMenu;
    private final Consumer<String> mostrarPagina;

    public PaginaEliminatorias(
            Runnable alternarMenu,
            Consumer<String> mostrarPagina
    ) {
        this.alternarMenu = alternarMenu;
        this.mostrarPagina = mostrarPagina;

        setLayout(new BorderLayout());
        setBackground(new Color(32, 157, 171));
        setBorder(new EmptyBorder(22, 24, 22, 24));

        construirInterface();
    }

    private void construirInterface() {
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);

        JButton botaoMenu = new JButton("☰");
        botaoMenu.setFocusPainted(false);
        botaoMenu.setBorderPainted(false);
        botaoMenu.setContentAreaFilled(false);
        botaoMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        botaoMenu.setForeground(Color.WHITE);
        botaoMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoMenu.addActionListener(e -> alternarMenu.run());

        JLabel titulo = new JLabel("Fase Eliminatória");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);

        PrimaryButton botaoVoltar = new PrimaryButton("Ver Grupos");
        botaoVoltar.addActionListener(e -> mostrarPagina.accept("grupos"));

        barraSuperior.add(botaoMenu, BorderLayout.WEST);
        barraSuperior.add(titulo, BorderLayout.CENTER);
        barraSuperior.add(botaoVoltar, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        ChaveamentoEliminatorio chaveamento = new ChaveamentoEliminatorio();
        add(chaveamento, BorderLayout.CENTER);
    }
}