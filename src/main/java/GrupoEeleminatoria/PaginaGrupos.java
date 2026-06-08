package GrupoEeleminatoria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class PaginaGrupos extends JPanel {

    private final String nomeCampeonato;
    private final Runnable alternarMenu;
    private final Consumer<String> mostrarPagina;



    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(120, 130, 150);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color ORANGE = new Color(249, 115, 22);
    private final Color PURPLE = new Color(124, 58, 237);

    private final Color BLUE_BG = new Color(231, 240, 253);
    private final Color GREEN_BG = new Color(232, 248, 238);
    private final Color ORANGE_BG = new Color(255, 243, 224);
    private final Color PURPLE_BG = new Color(242, 235, 255);

    private JLabel tituloCampeonato;

    public PaginaGrupos(
            String nomeCampeonato,
            Runnable alternarMenu,
            Consumer<String> mostrarPagina
    ) {
        this.nomeCampeonato = nomeCampeonato;
        this.alternarMenu = alternarMenu;
        this.mostrarPagina = mostrarPagina;

        setLayout(new BorderLayout());
        setBackground(BG);
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
        botaoMenu.setForeground(TEXT);
        botaoMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoMenu.addActionListener(e -> alternarMenu.run());

        barraSuperior.add(botaoMenu, BorderLayout.WEST);
        add(barraSuperior, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(10, 150, 20, 150));

        tituloCampeonato = new JLabel("Mundial FIFA 2026");
        tituloCampeonato.setFont(new Font("Segoe UI", Font.BOLD, 28));
        tituloCampeonato.setForeground(TEXT);
        tituloCampeonato.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Gestão de grupos, partidas e estatísticas do campeonato.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(MUTED);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(tituloCampeonato);
        centro.add(Box.createVerticalStrut(4));
        centro.add(subtitulo);
        centro.add(Box.createVerticalStrut(26));

        JPanel estatisticas = new JPanel(new GridLayout(1, 4, 18, 0));
        estatisticas.setOpaque(false);
        estatisticas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        estatisticas.setAlignmentX(Component.LEFT_ALIGNMENT);

        estatisticas.add(criarCartaoEstatistica("Partidas Jogadas", "6", BLUE, BLUE_BG));
        estatisticas.add(criarCartaoEstatistica("Total de Golos", "3", GREEN, GREEN_BG));
        estatisticas.add(criarCartaoEstatistica("Equipas Classificadas", "2", ORANGE, ORANGE_BG));
        estatisticas.add(criarCartaoEstatistica("Equipas Participantes", "24", PURPLE, PURPLE_BG));

        centro.add(estatisticas);
        centro.add(Box.createVerticalStrut(28));

        JPanel barraAcoes = new JPanel(new BorderLayout());
        barraAcoes.setOpaque(false);
        barraAcoes.setAlignmentX(Component.LEFT_ALIGNMENT);
        barraAcoes.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JPanel separadores = new PainelArredondado(14, Color.WHITE);
        separadores.setLayout(new GridLayout(1, 2, 6, 0));
        separadores.setBorder(new EmptyBorder(5, 5, 5, 5));
        separadores.setPreferredSize(new Dimension(220, 42));

        JButton botaoGrupos = criarBotaoSeparador("Grupos", true);

        JButton botaoEliminatorias = criarBotaoSeparador("Eliminatórias", false);
        botaoEliminatorias.addActionListener(e -> mostrarPagina.accept("eliminatorias"));

        separadores.add(botaoGrupos);
        separadores.add(botaoEliminatorias);

        JPanel botoesDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoesDireita.setOpaque(false);

        JButton botaoEditar = criarBotaoAzul("Editar");
        botaoEditar.addActionListener(e -> editarNomeCampeonato());

        JButton botaoNovo = criarBotaoAzul("Novo");
        botaoNovo.addActionListener(e -> criarNovoCampeonato());

        JButton botaoGerarGrupos = criarBotaoAzul("Gerar Grupos");
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

        painelGrupos.add(new cartaogrupo("Grupo A", new String[]{
                "México",
                "África do Sul",
                "Coreia do Sul CP",
                "República Checa"
        }));

        painelGrupos.add(new cartaogrupo("Grupo B", new String[]{
                "Canadá",
                "Bósnia",
                "Catar",
                "Suíça"
        }));

        painelGrupos.add(new cartaogrupo("Grupo C", new String[]{
                "Brasil",
                "Marrocos",
                "Haiti",
                "Escócia"
        }));

        painelGrupos.add(new cartaogrupo("Grupo D", new String[]{
                "Estados Unidos",
                "Paraguai",
                "Austrália",
                "Turquia"
        }));

        centro.add(painelGrupos);

        add(centro, BorderLayout.CENTER);
    }

    private JPanel criarCartaoEstatistica(String titulo, String valor, Color corTexto, Color corFundo) {
        JPanel cartao = new PainelArredondado(18, corFundo);
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBorder(new EmptyBorder(18, 20, 14, 20));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelTitulo.setForeground(corTexto);

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        labelValor.setForeground(TEXT);

        cartao.add(labelTitulo);
        cartao.add(Box.createVerticalStrut(8));
        cartao.add(labelValor);

        return cartao;
    }

    private JButton criarBotaoSeparador(String texto, boolean selecionado) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 12));
        botao.setForeground(selecionado ? Color.WHITE : new Color(100, 116, 139));
        botao.setBackground(selecionado ? BLUE : Color.WHITE);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return botao;
    }

    private JButton criarBotaoAzul(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(BLUE);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));

        return botao;
    }

    private void editarNomeCampeonato() {
        String novoNome = JOptionPane.showInputDialog(
                this,
                "Novo nome do campeonato:",
                tituloCampeonato.getText()
        );

        if (novoNome != null && !novoNome.trim().isEmpty()) {
            tituloCampeonato.setText(novoNome.trim());
        }
    }

    private void criarNovoCampeonato() {
        JTextField nomeField = new JTextField("Novo Campeonato");

        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.add(new JLabel("Nome do campeonato:"), BorderLayout.NORTH);
        painel.add(nomeField, BorderLayout.CENTER);

        int resultado = JOptionPane.showConfirmDialog(
                this,
                painel,
                "Novo Campeonato",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            String nome = nomeField.getText().trim();

            if (!nome.isEmpty()) {
                tituloCampeonato.setText(nome);
            }
        }
    }

    static class PainelArredondado extends JPanel {

        private final int raio;
        private final Color corFundo;

        public PainelArredondado(int raio, Color corFundo) {
            this.raio = raio;
            this.corFundo = corFundo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D desenho = (Graphics2D) g.create();

            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            desenho.setColor(new Color(0, 0, 0, 18));
            desenho.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.dispose();

            super.paintComponent(g);
        }
    }
}