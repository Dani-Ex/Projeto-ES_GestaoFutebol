package Design;

import Frames.*;
import Frames.SeccaoEquipas.EquipasFrame;
import Frames.SeccaoFinancas.FinancasFrame;
import Frames.SeccaoJogadores.JogadoresFrame;

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
        JButton btnCampeonatos = criarBotaoMenu("Campeonatos");
        JButton btnEquipas = criarBotaoMenu("Equipas");
        JButton btnJogadores = criarBotaoMenu("Jogadores");
        JButton btnJogos = criarBotaoMenu("Jogos");
        JButton btnEstatisticas = criarBotaoMenu("Estatisticas");
        JButton btnFinancas = criarBotaoMenu("Finan\u00E7as");
        JButton btnEstadios = criarBotaoMenu("Estadios");
        JButton btnBilheteria = criarBotaoMenu("Bilheteria");
        JButton btnRegras = criarBotaoMenu("Regras");

        btnDashboard.addActionListener(e -> {
            frame.dispose();
            new DashboardFrame();
        });

        btnCampeonatos.addActionListener(e -> abrirPlaceholder(frame, "Campeonatos"));

        btnEquipas.addActionListener(e -> {
            frame.dispose();
            new EquipasFrame();
        });

        btnJogadores.addActionListener(e -> {
            frame.dispose();
            new JogadoresFrame();
        });

        btnJogos.addActionListener(e -> abrirPlaceholder(frame, "Jogos"));

        btnEstatisticas.addActionListener(e -> {
            frame.dispose();
            new EstadisticasFrame();
        });

        btnFinancas.addActionListener(e -> {
            frame.dispose();
            new FinancasFrame();
        });
        btnEstadios.addActionListener(e -> abrirPlaceholder(frame, "Estadios"));
        btnBilheteria.addActionListener(e -> abrirPlaceholder(frame, "Bilheteria"));

        btnRegras.addActionListener(e -> {
            frame.dispose();
            new RegrasFrame();
        });

        add(titulo);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_GRANDE));
        add(btnDashboard);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnCampeonatos);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnEquipas);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnJogadores);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnJogos);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_GRANDE));
        add(btnEstatisticas);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnFinancas);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnEstadios);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnBilheteria);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));
        add(btnRegras);
    }

    private void abrirPlaceholder(JFrame frame, String titulo) {
        frame.dispose();
        new SecaoPlaceholderFrame(titulo);
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
