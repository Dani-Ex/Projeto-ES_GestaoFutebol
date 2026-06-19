package Frames;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Design.MenuLateral;
import Models.Campeonato;
import GrupoEeleminatoria.GruposFrame;
import GrupoEeleminatoria.NovoCampeonatoFrame;
import GrupoEeleminatoria.CampeonatoRepositorio;

public class CampeonatosFrame extends JFrame {

    private JTable tabelaCampeonatos;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(120, 130, 150);
    private final Color BLUE = new Color(37, 99, 235);

    public CampeonatosFrame() {
        setTitle("Campeonatos");
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        JPanel pagina = criarPagina(menuLateral);

        add(menuLateral, BorderLayout.WEST);
        add(pagina, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarPagina(JPanel menuLateral) {
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(BG);
        pagina.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);

        JButton botaoMenu = new JButton("=");
        botaoMenu.setFocusPainted(false);
        botaoMenu.setBorderPainted(false);
        botaoMenu.setContentAreaFilled(false);
        botaoMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        botaoMenu.setForeground(TEXT);
        botaoMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoMenu.addActionListener(e -> {
            menuLateral.setVisible(!menuLateral.isVisible());
            revalidate();
            repaint();
        });

        barraSuperior.add(botaoMenu, BorderLayout.WEST);
        pagina.add(barraSuperior, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(10, 90, 20, 90));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Campeonatos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXT);

        JLabel subtitulo = new JLabel("Gestão de torneios, fases, grupos e estado do campeonato.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton btnNovo = criarBotaoAzul("+ Novo Models.Campeonato");
        btnNovo.addActionListener(e -> {
            dispose();
            new NovoCampeonatoFrame();
        });

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(btnNovo, BorderLayout.EAST);

        centro.add(cabecalho);
        centro.add(Box.createVerticalStrut(24));

        JPanel estatisticas = new JPanel(new GridLayout(1, 4, 18, 0));
        estatisticas.setOpaque(false);
        estatisticas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        estatisticas.setAlignmentX(Component.LEFT_ALIGNMENT);

        estatisticas.add(criarCartaoEstatistica("Campeonatos Totais", "6", BLUE, new Color(231, 240, 253)));
        estatisticas.add(criarCartaoEstatistica("A Decorrer", "3", new Color(22, 163, 74), new Color(232, 248, 238)));
        estatisticas.add(criarCartaoEstatistica("Finalizados", "2", new Color(249, 115, 22), new Color(255, 243, 224)));
        estatisticas.add(criarCartaoEstatistica("Equipas Participantes", "24", new Color(124, 58, 237), new Color(242, 235, 255)));

        centro.add(estatisticas);
        centro.add(Box.createVerticalStrut(28));

        JPanel conteudo = new JPanel(new BorderLayout(35, 0));
        conteudo.setOpaque(false);
        conteudo.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(criarCardListaCampeonatos(), BorderLayout.CENTER);
        conteudo.add(criarCardRegras(), BorderLayout.EAST);

        centro.add(conteudo);

        pagina.add(centro, BorderLayout.CENTER);

        return pagina;
    }

    private JPanel criarCardListaCampeonatos() {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(720, 430));

        JLabel titulo = new JLabel("Lista de Campeonatos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(TEXT);

        String[] colunas = {
                "Models.Campeonato",
                "Início",
                "Fim",
                "Equipas",
                "Grupos",
                "Fase",
                "Estado"
        };

        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Campeonato campeonato : CampeonatoRepositorio.listar()) {
            modelo.addRow(new Object[]{
                    campeonato.getNome(),
                    "01 Jul",
                    "22 Jul",
                    "24",
                    "4",
                    campeonato.isGruposGerados() ? "Grupos gerados" : "Por gerar",
                    campeonato.isFaseGruposTerminada() ? "Fase grupos terminada" : "Planeado"
            });
        }

        tabelaCampeonatos = new JTable(modelo);
        tabelaCampeonatos.setRowHeight(34);
        tabelaCampeonatos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaCampeonatos.setForeground(new Color(51, 65, 85));
        tabelaCampeonatos.setGridColor(new Color(226, 232, 240));
        tabelaCampeonatos.setShowVerticalLines(false);
        tabelaCampeonatos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelaCampeonatos.getTableHeader().setForeground(new Color(100, 116, 139));
        tabelaCampeonatos.getTableHeader().setBackground(Color.WHITE);

        tabelaCampeonatos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabelaCampeonatos.getSelectedRow();

                if (linha >= 0) {
                    String nomeCampeonato = tabelaCampeonatos.getValueAt(linha, 0).toString();
                    Campeonato campeonato = CampeonatoRepositorio.procurarPorNome(nomeCampeonato);

                    if (campeonato != null) {
                        dispose();
                        new GruposFrame(campeonato);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaCampeonatos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardRegras() {
        JPanel card = new PainelArredondado(18, new Color(231, 240, 253));
        card.setPreferredSize(new Dimension(220, 430));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 20, 20, 20));

        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setOpaque(false);
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        texto.setForeground(TEXT);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);

        texto.setText("""
                Regras do Models.Campeonato

                • Equipas divididas por grupos

                • 1.º e 2.º lugar passam à fase mata-mata

                • Grupos devem ter número par de equipas

                • Cada equipa deve ter 23 jogadores
                """);

        texto.setFont(new Font("Segoe UI", Font.BOLD, 16));

        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCartaoEstatistica(String titulo, String valor, Color corTexto, Color corFundo) {
        JPanel card = new PainelArredondado(18, corFundo);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 20, 14, 20));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelTitulo.setForeground(corTexto);

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        labelValor.setForeground(TEXT);

        card.add(labelTitulo);
        card.add(Box.createVerticalStrut(8));
        card.add(labelValor);

        return card;
    }

    private JButton criarBotaoAzul(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(BLUE);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(11, 20, 11, 20));

        return botao;
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