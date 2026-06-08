package ui.paginas;

import service.ChampionshipService;
import ui.components.GroupCard;
import ui.components.PrimaryButton;
import ui.components.RoundedPanel;
import ui.components.StatCard;
import ui.dialogs.EditChampionshipDialog;
import ui.dialogs.NewChampionshipDialog;
import ui.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class PaginaGrupos extends JPanel {

    private final ChampionshipService servicoCampeonato;
    private final Runnable alternarMenu;
    private final Consumer<String> mostrarPagina;

    public PaginaGrupos(
            ChampionshipService servicoCampeonato,
            Runnable alternarMenu,
            Consumer<String> mostrarPagina
    ) {
        this.servicoCampeonato = servicoCampeonato;
        this.alternarMenu = alternarMenu;
        this.mostrarPagina = mostrarPagina;

        setLayout(new BorderLayout());
        setBackground(AppColors.BG);
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
        botaoMenu.setForeground(AppColors.TEXT);
        botaoMenu.addActionListener(e -> alternarMenu.run());

        barraSuperior.add(botaoMenu, BorderLayout.WEST);
        add(barraSuperior, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(10, 150, 20, 150));

        JLabel titulo = new JLabel(servicoCampeonato.getChampionship().getName());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(AppColors.TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Gestão de grupos, partidas e estatísticas do campeonato.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(AppColors.MUTED);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(titulo);
        centro.add(Box.createVerticalStrut(4));
        centro.add(subtitulo);
        centro.add(Box.createVerticalStrut(26));

        JPanel estatisticas = new JPanel(new GridLayout(1, 4, 18, 0));
        estatisticas.setOpaque(false);
        estatisticas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        estatisticas.setAlignmentX(Component.LEFT_ALIGNMENT);

        estatisticas.add(new StatCard("Partidas Jogadas", "6", AppColors.BLUE, AppColors.BLUE_BG));
        estatisticas.add(new StatCard("Total de Golos", "3", AppColors.GREEN, AppColors.GREEN_BG));
        estatisticas.add(new StatCard("Equipas Classificadas", "2", AppColors.ORANGE, AppColors.ORANGE_BG));
        estatisticas.add(new StatCard("Equipas Participantes", "24", AppColors.PURPLE, AppColors.PURPLE_BG));

        centro.add(estatisticas);
        centro.add(Box.createVerticalStrut(28));

        JPanel barraAcoes = new JPanel(new BorderLayout());
        barraAcoes.setOpaque(false);
        barraAcoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraAcoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel separadores = new RoundedPanel(14, Color.WHITE);
        separadores.setLayout(new GridLayout(1, 2, 6, 0));
        separadores.setBorder(new EmptyBorder(5, 5, 5, 5));
        separadores.setPreferredSize(new Dimension(210, 42));

        JButton botaoGrupos = criarBotaoSeparador("Grupos", true);

        JButton botaoEliminatorias = criarBotaoSeparador("Eliminatórias", false);
        botaoEliminatorias.addActionListener(e -> mostrarPagina.accept("eliminatorias"));

        separadores.add(botaoGrupos);
        separadores.add(botaoEliminatorias);

        JPanel botoesDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoesDireita.setOpaque(false);

        PrimaryButton botaoEditar = new PrimaryButton("Editar");
        botaoEditar.addActionListener(e -> {
            Window janela = SwingUtilities.getWindowAncestor(this);

            EditChampionshipDialog dialogo = new EditChampionshipDialog(
                    janela,
                    servicoCampeonato.getChampionship().getName()
            );

            dialogo.setVisible(true);

            String novoNome = dialogo.getNewName();

            if (novoNome != null && !novoNome.trim().isEmpty()) {
                servicoCampeonato.getChampionship().setName(novoNome.trim());
                titulo.setText(novoNome.trim());
            }
        });

        PrimaryButton botaoNovo = new PrimaryButton("Novo");
        botaoNovo.addActionListener(e -> {
            Window janela = SwingUtilities.getWindowAncestor(this);
            NewChampionshipDialog dialogo = new NewChampionshipDialog(janela);
            dialogo.setVisible(true);
        });

        PrimaryButton botaoGerarGrupos = new PrimaryButton("Gerar Grupos");
        botaoGerarGrupos.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Grupos gerados automaticamente.",
                "Gerar Grupos",
                JOptionPane.INFORMATION_MESSAGE
        ));

        botoesDireita.add(botaoEditar);
        botoesDireita.add(botaoNovo);
        botoesDireita.add(botaoGerarGrupos);

        barraAcoes.add(separadores, BorderLayout.WEST);
        barraAcoes.add(botoesDireita, BorderLayout.EAST);

        centro.add(barraAcoes);
        centro.add(Box.createVerticalStrut(20));

        JPanel painelGrupos = new JPanel(new GridLayout(2, 2, 20, 20));
        painelGrupos.setOpaque(false);
        painelGrupos.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelGrupos.add(new CartaoGrupo("Grupo A", new String[]{
                "México",
                "África do Sul",
                "Coreia do Sul CP",
                "República Checa"
        }));

        painelGrupos.add(new CartaoGrupo("Grupo B", new String[]{
                "Canadá",
                "Bósnia",
                "Catar",
                "Suíça"
        }));

        painelGrupos.add(new CartaoGrupo("Grupo C", new String[]{
                "Brasil",
                "Marrocos",
                "Haiti",
                "Escócia"
        }));

        painelGrupos.add(new CartaoGrupo("Grupo D", new String[]{
                "Estados Unidos",
                "Paraguai",
                "Austrália",
                "Turquia"
        }));

        centro.add(painelGrupos);

        add(centro, BorderLayout.CENTER);
    }

    private JButton criarBotaoSeparador(String texto, boolean selecionado) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setForeground(selecionado ? Color.WHITE : new Color(100, 116, 139));
        botao.setBackground(selecionado ? AppColors.BLUE : Color.WHITE);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return botao;
    }
}