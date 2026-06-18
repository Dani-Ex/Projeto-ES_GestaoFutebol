package Frames;

import Design.MenuLateral;
import Design.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class EstadisticasFrame extends JFrame {

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    public EstadisticasFrame() {
        setTitle("Estatísticas");
        setSize(1280, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Tema.COR_FUNDO);
        main.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_JANELA.top,
                Tema.PADDING_JANELA.left,
                Tema.PADDING_JANELA.bottom,
                Tema.PADDING_JANELA.right
        ));

        setContentPane(main);

        menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);
        main.add(menuLateral, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(35, 15, 20, 25));

        content.add(criarTopo(main));
        content.add(Box.createVerticalStrut(22));
        content.add(criarCardsResumo());
        content.add(Box.createVerticalStrut(35));
        content.add(criarTabelas());

        main.add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarTopo(JPanel main) {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel tituloBox = new JPanel();
        tituloBox.setOpaque(false);
        tituloBox.setLayout(new BoxLayout(tituloBox, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Estatísticas");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Dados globais do campeonato, por jogo e por jogador.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        tituloBox.add(titulo);
        tituloBox.add(Box.createVerticalStrut(3));
        tituloBox.add(subtitulo);

        JButton btnMenu = new JButton("=");
        btnMenu.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 22));
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        btnMenu.setPreferredSize(new Dimension(50, 45));
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnMenu.addActionListener(e -> {
            menuAberto = !menuAberto;
            menuLateral.setVisible(menuAberto);
            main.revalidate();
            main.repaint();
        });

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        esquerda.setOpaque(false);
        esquerda.add(btnMenu);
        esquerda.add(tituloBox);

        topo.add(esquerda, BorderLayout.WEST);

        return topo;
    }

    private JPanel criarCardsResumo() {
        JPanel painel = new JPanel(new GridLayout(1, 6, 18, 0));
        painel.setOpaque(false);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        painel.add(criarCardResumo("Total de Golos", "312", Tema.CARD_AZUL, Tema.CARD_TEXTO_AZUL));
        painel.add(criarCardResumo("Assistências", "198", Tema.CARD_VERDE, Tema.CARD_TEXTO_VERDE));
        painel.add(criarCardResumo("Faltas", "842", new Color(255, 237, 213), Tema.CARD_TEXTO_LARANJA));
        painel.add(criarCardResumo("Amarelos", "146", Tema.CARD_ROXO, Tema.CARD_TEXTO_ROXO));
        painel.add(criarCardResumo("Vermelhos", "18", new Color(254, 226, 226), Tema.COR_ERRO));
        painel.add(criarCardResumo("Remates", "1245", new Color(241, 245, 249), Tema.COR_TEXTO_PRINCIPAL));

        return painel;
    }

    private JPanel criarCardResumo(String titulo, String valor, Color fundo, Color corTitulo) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, fundo);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 13));
        lblTitulo.setForeground(corTitulo);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 28));
        lblValor.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarTabelas() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 25, 0));
        painel.setOpaque(false);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 470));

        painel.add(criarCardTabelaJogador());
        painel.add(criarCardTabelaJogo());

        return painel;
    }

    private JPanel criarCardTabelaJogador() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Estatísticas por Jogador");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {"Jogador", "Equipa", "Golos", "Assist.", "Faltas", "Cartões"};

        Object[][] dados = {
                {"Rafa Silva", "SL Benfica", 8, 4, 9, 1},
                {"Paulinho", "Sporting", 6, 2, 7, 2},
                {"Otávio", "FC Porto", 4, 7, 10, 1},
                {"Alan", "Moreirense", 5, 1, 4, 0},
                {"João Moutinho", "Braga", 2, 5, 8, 0},
                {"Tiago Silva", "Vitória", 0, 2, 11, 3}
        };

        JTable tabela = criarTabela(colunas, dados);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Tema.COR_CARD);
        scroll.setBackground(Tema.COR_CARD);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(16, 0, 0, 0));
        centro.add(scroll, BorderLayout.CENTER);

        card.add(titulo, BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardTabelaJogo() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Estatísticas por Jogo");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {"Jogo", "Resultado", "Golos", "Faltas", "Cartões", "Posse", "Remates"};

        Object[][] dados = {
                {"Porto vs Braga", "2-1", 3, 25, 5, "58/42", "16/9"},
                {"Benfica vs Vitória", "3-0", 3, 19, 4, "61/39", "18/7"},
                {"Sporting vs Boavista", "1-1", 2, 23, 5, "52/48", "12/10"},
                {"Moreirense vs Gil", "2-0", 2, 21, 2, "49/51", "11/8"},
                {"Rio Ave vs Estoril", "-", "-", "-", "-", "-", "-"}
        };

        JTable tabela = criarTabela(colunas, dados);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Tema.COR_CARD);
        scroll.setBackground(Tema.COR_CARD);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(16, 0, 0, 0));
        centro.add(scroll, BorderLayout.CENTER);

        card.add(titulo, BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);

        return card;
    }

    private JTable criarTabela(String[] colunas, Object[][] dados) {
        DefaultTableModel modelo = new DefaultTableModel(dados, colunas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(modelo);

        tabela.setRowHeight(38);
        tabela.setFont(new Font(Tema.FONTE_PADRAO, Font.PLAIN, 12));
        tabela.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setBackground(Tema.COR_CARD);
        tabela.setGridColor(Tema.COR_LINHA);
        tabela.setShowVerticalLines(false);
        tabela.setShowHorizontalLines(true);
        tabela.setSelectionBackground(Tema.COR_SELECAO_NEUTRA);
        tabela.setSelectionForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setFocusable(false);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 12));
        header.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        header.setBackground(Tema.COR_CARD);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.COR_LINHA));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        DefaultTableCellRenderer renderNormal = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component c = super.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );

                setBorder(new EmptyBorder(0, 4, 0, 4));

                if (isSelected) {
                    c.setBackground(Tema.COR_SELECAO_NEUTRA);
                    c.setForeground(Tema.COR_TEXTO_PRINCIPAL);
                } else {
                    c.setBackground(Tema.COR_CARD);
                    c.setForeground(Tema.COR_TEXTO_PRINCIPAL);
                }

                c.setFont(new Font(Tema.FONTE_PADRAO, Font.PLAIN, 12));
                return c;
            }
        };

        DefaultTableCellRenderer renderNegrito = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component c = super.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );

                setBorder(new EmptyBorder(0, 4, 0, 4));

                if (isSelected) {
                    c.setBackground(Tema.COR_SELECAO_NEUTRA);
                    c.setForeground(Tema.COR_TEXTO_PRINCIPAL);
                } else {
                    c.setBackground(Tema.COR_CARD);
                    c.setForeground(Tema.COR_TEXTO_PRINCIPAL);
                }

                c.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 12));
                return c;
            }
        };

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(renderNormal);
        }

        tabela.getColumnModel().getColumn(0).setCellRenderer(renderNegrito);

        return tabela;
    }

    private static class RoundedPanel extends JPanel {

        private final int raio;
        private final Color corFundo;

        public RoundedPanel(int raio, Color corFundo) {
            this.raio = raio;
            this.corFundo = corFundo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(corFundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);

            g2.dispose();

            super.paintComponent(g);
        }
    }
}
