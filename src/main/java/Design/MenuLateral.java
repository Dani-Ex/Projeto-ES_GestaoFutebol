package Design;

import Frames.*;

import javax.swing.*;
import java.awt.*;

public class MenuLateral extends JPanel {

    public MenuLateral(JFrame frame) {
        setPreferredSize(new Dimension(Tema.LARGURA_MENU, 0));
        setBackground(Tema.COR_MENU);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_MENU.top,
                Tema.PADDING_MENU.left,
                Tema.PADDING_MENU.bottom,
                Tema.PADDING_MENU.right
        ));

        JLabel titulo = new JLabel("Campeonato");
        titulo.setForeground(Tema.COR_TEXTO_CLARO);
        titulo.setFont(Tema.FONTE_MENU_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnDashboard = criarBotaoMenu("Dashboard");
        JButton btnEquipas = criarBotaoMenu("Equipas");
        JButton btnJogadores = criarBotaoMenu("Jogadores");
        JButton btnEstadios = criarBotaoMenu("Estádios");
        JButton btnBilhetes = criarBotaoMenu("Bilhetes");
        JButton btnEstatisticas = criarBotaoMenu("Estatísticas");
        JButton btnRegras = criarBotaoMenu("Regras");

        btnDashboard.addActionListener(e -> {
            frame.dispose();
            new DashboardFrame();
        });

        btnEquipas.addActionListener(e -> {
            frame.dispose();
            new EquipasFrame();
        });

        btnJogadores.addActionListener(e -> {
            frame.dispose();
            new JogadoresFrame();
        });

        btnEstatisticas.addActionListener(e -> {
            frame.dispose();
            new EstadisticasFrame();
        });

        btnRegras.addActionListener(e -> {
            frame.dispose();
            new RegrasFrame();
        });

        add(titulo);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_GRANDE));
        add(btnDashboard);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnEquipas);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnJogadores);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnEstadios);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnBilhetes);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnEstatisticas);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnRegras);
    }

    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);

        btn.setMaximumSize(new Dimension(200, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);

        btn.setBackground(Tema.COR_MENU_BOTAO);
        btn.setForeground(Tema.COR_TEXTO_CLARO);
        btn.setFont(Tema.FONTE_MENU);

        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        return btn;
    }
}