package Frames;

import Design.MenuLateral;
import Design.PlaceholderTextField;
import Design.RoundedPanel;
import Design.Tema;
import Models.Equipa;
import Models.EquipaService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.AbstractBorder;
import java.awt.*;

public class EquipasFrame extends JFrame {

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    private JTable tabelaEquipas;
    private DefaultTableModel modeloEquipas;
    private PlaceholderTextField campoPesquisa;
    private final EquipaService equipaService = new EquipaService();

    public EquipasFrame() {
        setTitle("Equipas");
        setSize(1280, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setFocusable(true);
        main.setBackground(Tema.COR_FUNDO);
        main.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_JANELA.top,
                Tema.PADDING_JANELA.left,
                Tema.PADDING_JANELA.bottom,
                Tema.PADDING_JANELA.right
        ));
        add(main);
        limparSelecaoAoClicar(main);

        menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        JPanel navegacao = new JPanel(new BorderLayout());
        navegacao.setFocusable(true);
        navegacao.setOpaque(false);
        limparSelecaoAoClicar(navegacao);

        JPanel menuTopo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        menuTopo.setOpaque(false);
        menuTopo.setBorder(BorderFactory.createEmptyBorder(0, 0, Tema.ESPACAMENTO_PEQUENO, 0));
        menuTopo.add(criarBotaoMenu(main));

        navegacao.add(menuTopo, BorderLayout.NORTH);
        navegacao.add(menuLateral, BorderLayout.CENTER);
        main.add(navegacao, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setFocusable(true);
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(
                Tema.ESPACAMENTO_GRANDE + 5,
                Tema.ESPACAMENTO_GRANDE + 5,
                Tema.ESPACAMENTO_GRANDE + Tema.ESPACAMENTO_MEDIO,
                Tema.ESPACAMENTO_GRANDE + 5
        ));
        limparSelecaoAoClicar(content);

        content.add(criarTopo());
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO));
        content.add(criarPesquisa());
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO + 5));
        content.add(criarAreaPrincipal());

        main.add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setFocusable(true);
        topo.setOpaque(false);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        limparSelecaoAoClicar(topo);

        JPanel tituloBox = new JPanel();
        tituloBox.setFocusable(true);
        tituloBox.setOpaque(false);
        tituloBox.setLayout(new BoxLayout(tituloBox, BoxLayout.Y_AXIS));
        limparSelecaoAoClicar(tituloBox);

        JLabel titulo = new JLabel("Equipas");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Registo, consulta e validação das equipas participantes.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        tituloBox.add(titulo);
        tituloBox.add(Box.createVerticalStrut(4));
        tituloBox.add(subtitulo);

        JButton btnNova = criarBotaoArredondado("+ Nova Equipa", Tema.COR_INFO, Tema.COR_TEXTO_CLARO);
        btnNova.addActionListener(e -> new NovaEquipaFrame(this::atualizarTabela));

        topo.add(tituloBox, BorderLayout.WEST);
        topo.add(btnNova, BorderLayout.EAST);

        return topo;
    }

    private JButton criarBotaoMenu(JPanel main) {
        JButton btnMenu = new JButton("=");

        btnMenu.setFont(Tema.FONTE_BOTAO_MENU);
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnMenu.setPreferredSize(new Dimension(45, 45));
        btnMenu.setMinimumSize(new Dimension(45, 45));
        btnMenu.setMaximumSize(new Dimension(45, 45));
        btnMenu.setMargin(new Insets(0, 0, 0, 0));

        btnMenu.addActionListener(e -> {
            menuAberto = !menuAberto;
            menuLateral.setVisible(menuAberto);
            main.revalidate();
            main.repaint();
        });

        return btnMenu;
    }

    private JPanel criarPesquisa() {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setFocusable(true);
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        limparSelecaoAoClicar(linha);

        campoPesquisa = new PlaceholderTextField("Pesquisar equipa...");
        campoPesquisa.setPreferredSize(new Dimension(250, 40));
        campoPesquisa.setFont(Tema.FONTE_TEXTO_PEQUENO);
        campoPesquisa.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        campoPesquisa.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        campoPesquisa.setOpaque(false);
        campoPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarTabela();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarTabela();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarTabela();
            }
        });

        RoundedPanel campo = new RoundedPanel(18, Tema.COR_INPUT);
        campo.setLayout(new BorderLayout());
        campo.setPreferredSize(new Dimension(250, 40));
        campo.setBorder(new RoundedBorder(Tema.COR_LINHA, 18));
        campo.add(campoPesquisa, BorderLayout.CENTER);

        linha.add(campo);

        return linha;
    }

    private JPanel criarAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout(25, 0));
        area.setFocusable(true);
        area.setOpaque(false);
        area.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));
        limparSelecaoAoClicar(area);

        area.add(criarCardTabelaEquipas(), BorderLayout.CENTER);
        area.add(criarCardValidacao(), BorderLayout.EAST);

        return area;
    }

    private JPanel criarCardTabelaEquipas() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setFocusable(true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));
        limparSelecaoAoClicar(card);

        JLabel titulo = new JLabel("Equipas Registadas");
        titulo.setFont(Tema.FONTE_TABELA_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {
                "Nome da Equipa",
                "Campeonato",
                "Grupo",
                "Estado",
                "Jogadores",
                "Pontos"
        };

        modeloEquipas = criarModeloNaoEditavel(colunas);

        tabelaEquipas = new JTable(modeloEquipas);
        configurarTabelaEquipas(tabelaEquipas);
        atualizarTabela();

        tabelaEquipas.getColumnModel().getColumn(0).setPreferredWidth(170);
        tabelaEquipas.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabelaEquipas.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabelaEquipas.getColumnModel().getColumn(3).setPreferredWidth(85);
        tabelaEquipas.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabelaEquipas.getColumnModel().getColumn(5).setPreferredWidth(70);

        JScrollPane scroll = new JScrollPane(tabelaEquipas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Tema.COR_CARD);
        scroll.getViewport().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                limparSelecao();
            }
        });
        scroll.setBackground(Tema.COR_CARD);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setFocusable(true);
        topo.setOpaque(false);
        limparSelecaoAoClicar(topo);
        topo.add(titulo, BorderLayout.WEST);

        card.add(topo, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(10), BorderLayout.WEST);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardValidacao() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_VERDE_SUAVE);
        card.setFocusable(true);
        card.setPreferredSize(new Dimension(240, 0));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(
                Tema.ESPACAMENTO_MEDIO + 5,
                Tema.ESPACAMENTO_MEDIO + 5,
                Tema.ESPACAMENTO_MEDIO + 5,
                Tema.ESPACAMENTO_MEDIO + 5
        ));
        limparSelecaoAoClicar(card);

        JLabel titulo = new JLabel("<html>Validação<br>obrigatória</html>");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_VERDE_FORTE);

        JTextArea texto = new JTextArea(
                "Cada equipa deve ter exatamente 23 jogadores " +
                        "antes do início do campeonato.\n\n" +
                        "Equipas fora desta regra não podem participar nos jogos oficiais."
        );

        texto.setFont(Tema.FONTE_TEXTO);
        texto.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setOpaque(false);
        texto.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                limparSelecao();
            }
        });

        card.add(titulo, BorderLayout.NORTH);
        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private DefaultTableModel criarModeloNaoEditavel(String[] colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void atualizarTabela() {
        if (modeloEquipas == null) {
            return;
        }

        modeloEquipas.setRowCount(0);

        String termoPesquisa = campoPesquisa == null ? "" : campoPesquisa.getText();

        for (Equipa equipa : equipaService.pesquisarEquipas(termoPesquisa)) {
            modeloEquipas.addRow(new Object[]{
                    equipa.getNome(),
                    equipa.getCampeonato(),
                    equipa.getGrupo(),
                    equipa.getEstadoTexto(),
                    equipa.getTotalJogadores(),
                    equipa.getPontos()
            });
        }
    }

    private void configurarTabelaEquipas(JTable tabela) {
        tabela.setFont(Tema.FONTE_TEXTO_PEQUENO);
        tabela.setRowHeight(34);

        tabela.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setBackground(Tema.COR_CARD);

        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(false);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));

        tabela.setBorder(BorderFactory.createEmptyBorder());
        tabela.setFocusable(false);

        tabela.setSelectionBackground(Tema.COR_SELECAO_NEUTRA);
        tabela.setSelectionForeground(Tema.COR_TEXTO_PRINCIPAL);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(Tema.FONTE_CARD_TITULO);
        header.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        header.setBackground(Tema.COR_CARD);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
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
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );

                label.setFont(Tema.FONTE_CARD_TITULO);
                label.setForeground(Tema.COR_TEXTO_SECUNDARIO);
                label.setBackground(Tema.COR_CARD);
                label.setOpaque(true);
                label.setHorizontalAlignment(column == 0 || column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.COR_LINHA),
                        BorderFactory.createEmptyBorder(0, 8, 8, 8)
                ));

                return label;
            }
        });

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        centro.setVerticalAlignment(SwingConstants.CENTER);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        esquerda.setVerticalAlignment(SwingConstants.CENTER);
        esquerda.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centro);
        }

        tabela.getColumnModel().getColumn(0).setCellRenderer(esquerda);
        tabela.getColumnModel().getColumn(1).setCellRenderer(esquerda);
    }

    private void limparSelecao() {
        if (tabelaEquipas != null) {
            tabelaEquipas.clearSelection();
        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
    }

    private void limparSelecaoAoClicar(JComponent componente) {
        componente.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                limparSelecao();
            }
        });
    }

    private JButton criarBotaoArredondado(String texto, Color fundo, Color corTexto) {
        JButton botao = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        botao.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 14));
        botao.setBackground(fundo);
        botao.setForeground(corTexto);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setOpaque(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(150, 40));
        botao.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        return botao;
    }

    private static class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int radius;

        private RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 1;
            insets.left = 1;
            insets.bottom = 1;
            insets.right = 1;
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
