package Frames;

import Design.MenuLateral;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class EstadisticasFrame extends JFrame {

    private MenuLateral menuLateral;
    private boolean menuAberto = false;
    private final List<JTable> tabelas = new ArrayList<>();

    public EstadisticasFrame() {
        setTitle("Estatísticas");
        setSize(1920, 1080);
        setMinimumSize(new Dimension(1180, 700));
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
        limparSelecaoAoClicar(main);

        menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);
        main.add(menuLateral, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(0, 25, 0, 25));
        limparSelecaoAoClicar(content);

        content.add(criarTopo(main));
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO));
        content.add(criarCardsResumo());
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_GRANDE));
        content.add(criarTabelas());

        main.add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarTopo(JPanel main) {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JButton btnMenu = new JButton("=");
        btnMenu.setFont(Tema.FONTE_BOTAO_MENU);
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
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Tema.ALTURA_CARD_RESUMO));
        limparSelecaoAoClicar(painel);

        painel.add(criarCardResumo("Total de Golos", "312", Tema.CARD_AZUL, Tema.CARD_TEXTO_AZUL));
        painel.add(criarCardResumo("Assistências", "198", Tema.CARD_VERDE, Tema.CARD_TEXTO_VERDE));
        painel.add(criarCardResumo("Faltas", "842", Tema.COR_LARANJA_SUAVE, Tema.CARD_TEXTO_LARANJA));
        painel.add(criarCardResumo("Amarelos", "146", Tema.CARD_ROXO, Tema.CARD_TEXTO_ROXO));
        painel.add(criarCardResumo("Vermelhos", "18", Tema.COR_ERRO_SUAVE, Tema.COR_ERRO));
        painel.add(criarCardResumo("Remates", "1245", Tema.COR_BOTAO_SECUNDARIO, Tema.COR_TEXTO_PRINCIPAL));

        return painel;
    }

    private JPanel criarCardResumo(String titulo, String valor, Color fundo, Color corTitulo) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, fundo);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(
                Tema.PADDING_CARD_RESUMO.top,
                Tema.PADDING_CARD_RESUMO.left,
                Tema.PADDING_CARD_RESUMO.bottom,
                Tema.PADDING_CARD_RESUMO.right
        ));
        limparSelecaoAoClicar(card);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Tema.FONTE_CARD_TITULO);
        lblTitulo.setForeground(corTitulo);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(Tema.FONTE_CARD_VALOR_GRANDE);
        lblValor.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarTabelas() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 25, 0));
        painel.setOpaque(false);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 470));
        limparSelecaoAoClicar(painel);

        painel.add(criarCardTabelaJogador());
        painel.add(criarCardTabelaJogo());

        return painel;
    }

    private JPanel criarCardTabelaJogador() {
        RoundedPanel card = criarCardTabela("Estatísticas por Jogador");

        String[] colunas = {"Jogador", "Equipa", "Golos", "Assist.", "Faltas", "Cartões"};

        Object[][] dados = {
                {"Rafa Silva", "SL Benfica", 8, 4, 9, 1},
                {"Paulinho", "Sporting", 6, 2, 7, 2},
                {"Otávio", "FC Porto", 4, 7, 10, 1},
                {"Alan", "Moreirense", 5, 1, 4, 0},
                {"João Moutinho", "Braga", 2, 5, 8, 0},
                {"Tiago Silva", "Vitória", 0, 2, 11, 3}
        };

        JTable tabela = criarTabela(colunas, dados, 0);
        tabela.getColumnModel().getColumn(1).setCellRenderer(TableStyle.rendererEsquerda());

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);
        scroll.setBorder(new EmptyBorder(16, 0, 0, 0));

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardTabelaJogo() {
        RoundedPanel card = criarCardTabela("Estatísticas por Jogo");

        String[] colunas = {"Jogo", "Resultado", "Golos", "Faltas", "Cartões", "Posse", "Remates"};

        Object[][] dados = {
                {"Porto vs Braga", "2-1", 3, 25, 5, "58/42", "16/9"},
                {"Benfica vs Vitória", "3-0", 3, 19, 4, "61/39", "18/7"},
                {"Sporting vs Boavista", "1-1", 2, 23, 5, "52/48", "12/10"},
                {"Moreirense vs Gil", "2-0", 2, 21, 2, "49/51", "11/8"},
                {"Rio Ave vs Estoril", "-", "-", "-", "-", "-", "-"}
        };

        JTable tabela = criarTabela(colunas, dados, 0);

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);
        scroll.setBorder(new EmptyBorder(16, 0, 0, 0));

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private RoundedPanel criarCardTabela(String tituloTexto) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));
        limparSelecaoAoClicar(card);

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        card.add(titulo, BorderLayout.NORTH);

        return card;
    }

    private JTable criarTabela(String[] colunas, Object[][] dados, int colunaEsquerda) {
        DefaultTableModel modelo = new DefaultTableModel(dados, colunas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(modelo);
        TableStyle.aplicarTabelaLimpa(tabela, colunaEsquerda);
        tabela.setRowHeight(38);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabela.setDefaultRenderer(Object.class, criarRendererPadrao(colunaEsquerda));
        tabelas.add(tabela);

        return tabela;
    }

    private DefaultTableCellRenderer criarRendererPadrao(int colunaEsquerda) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                label.setFont(column == colunaEsquerda ? Tema.FONTE_CARD_TITULO : Tema.FONTE_TEXTO_PEQUENO);
                label.setHorizontalAlignment(column == colunaEsquerda ? SwingConstants.LEFT : SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setBorder(new EmptyBorder(0, 8, 0, 8));
                label.setOpaque(true);

                if (isSelected) {
                    label.setBackground(Tema.COR_SELECAO_NEUTRA);
                    label.setForeground(Tema.COR_TEXTO_PRINCIPAL);
                } else {
                    label.setBackground(Tema.COR_CARD);
                    label.setForeground(Tema.COR_TEXTO_PRINCIPAL);
                }

                return label;
            }
        };
    }

    private void limparSelecaoTabelas() {
        for (JTable tabela : tabelas) {
            tabela.clearSelection();
        }
    }

    private void limparSelecaoAoClicar(JComponent componente) {
        componente.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                limparSelecaoTabelas();
            }
        });
    }
}
