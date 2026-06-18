package Frames.SeccaoFinancas;

import Design.MenuLateral;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FinancasFrame extends JFrame {

    private static final Color CARD_BILHETEIRA = new Color(220, 252, 231);
    private static final Color CARD_DIREITOS_TV = new Color(255, 237, 213);
    private static final Color CARD_BILHETES = new Color(229, 231, 235);
    private static final Color CARD_MEDIA = new Color(254, 226, 226);

    private final List<ReceitaJogo> receitas = new ArrayList<>();

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    private DefaultTableModel modeloReceitas;
    private JTable tabelaReceitas;

    private JLabel valorLucroTotal;
    private JLabel valorBilheteira;
    private JLabel valorPatrocinios;
    private JLabel valorDireitosTv;
    private JLabel valorBilhetesVendidos;
    private JLabel valorMediaJogo;

    public FinancasFrame() {
        setTitle("Finan\u00E7as");
        setSize(1920, 1080);
        setMinimumSize(new Dimension(1280, 760));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        carregarDadosExemplo();

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
        main.add(menuLateral, BorderLayout.WEST);

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

        content.add(criarCabecalho());
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO));
        content.add(criarCardsResumo());
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO + 5));
        content.add(criarCardReceitas());

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(criarLinhaMenu(main), BorderLayout.NORTH);
        centro.add(content, BorderLayout.CENTER);

        main.add(centro, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarLinhaMenu(JPanel main) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setFocusable(true);
        linha.setOpaque(false);
        linha.setBorder(BorderFactory.createEmptyBorder(0, Tema.ESPACAMENTO_GRANDE + 5, 0, 0));
        limparSelecaoAoClicar(linha);
        linha.add(criarBotaoMenu(main));

        return linha;
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

    private JPanel criarCabecalho() {
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

        JLabel titulo = new JLabel("Finan\u00E7as");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Bilheteira, patroc\u00EDnios, direitos televisivos e lucro.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        tituloBox.add(titulo);
        tituloBox.add(Box.createVerticalStrut(4));
        tituloBox.add(subtitulo);

        topo.add(tituloBox, BorderLayout.WEST);

        return topo;
    }

    private JPanel criarCardsResumo() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        painel.setFocusable(true);
        painel.setOpaque(false);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        limparSelecaoAoClicar(painel);

        valorLucroTotal = new JLabel();
        valorBilheteira = new JLabel();
        valorPatrocinios = new JLabel();
        valorDireitosTv = new JLabel();
        valorBilhetesVendidos = new JLabel();
        valorMediaJogo = new JLabel();

        painel.add(criarCardResumo("Lucro Total", valorLucroTotal, Tema.CARD_AZUL, Tema.CARD_TEXTO_AZUL));
        painel.add(criarCardResumo("Bilheteira", valorBilheteira, CARD_BILHETEIRA, Tema.CARD_TEXTO_VERDE));
        painel.add(criarCardResumo("Patroc\u00EDnios", valorPatrocinios, Tema.CARD_ROXO, Tema.CARD_TEXTO_ROXO));
        painel.add(criarCardResumo("Direitos TV", valorDireitosTv, CARD_DIREITOS_TV, Tema.CARD_TEXTO_LARANJA));
        painel.add(criarCardResumo("Bilhetes Vendidos", valorBilhetesVendidos, CARD_BILHETES, Tema.COR_TEXTO_PRINCIPAL));
        painel.add(criarCardResumo("M\u00E9dio/Jogo", valorMediaJogo, CARD_MEDIA, Tema.COR_ERRO));

        atualizarResumo();

        return painel;
    }

    private JPanel criarCardResumo(String titulo, JLabel valor, Color corFundo, Color corTitulo) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, corFundo);
        card.setFocusable(true);
        card.setPreferredSize(new Dimension(180, Tema.ALTURA_CARD_RESUMO));
        card.setMinimumSize(new Dimension(180, Tema.ALTURA_CARD_RESUMO));
        card.setMaximumSize(new Dimension(180, Tema.ALTURA_CARD_RESUMO));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_CARD_RESUMO.top,
                Tema.PADDING_CARD_RESUMO.left,
                Tema.PADDING_CARD_RESUMO.bottom,
                Tema.PADDING_CARD_RESUMO.right
        ));
        limparSelecaoAoClicar(card);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Tema.FONTE_CARD_TITULO);
        lblTitulo.setForeground(corTitulo);

        valor.setFont(Tema.FONTE_CARD_VALOR);
        valor.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardReceitas() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setFocusable(true);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(760, 520));
        card.setMaximumSize(new Dimension(760, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));
        limparSelecaoAoClicar(card);

        JLabel titulo = new JLabel("Receita por Jogo");
        titulo.setFont(Tema.FONTE_TABELA_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {
                "Jogo",
                "Bilhetes",
                "",
                "Bilheteira",
                "",
                "Patroc\u00EDnio",
                "",
                "Direitos TV",
                "",
                "Lucro"
        };

        modeloReceitas = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 4 || column == 6 || column == 8;
            }
        };

        tabelaReceitas = new JTable(modeloReceitas);
        configurarTabelaReceitas(tabelaReceitas);
        tabelaReceitas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                editarCelulaFinanceira();
            }
        });
        atualizarTabela();

        JScrollPane scroll = new JScrollPane(tabelaReceitas);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setFocusable(true);
        topo.setOpaque(false);
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, Tema.ESPACAMENTO_PEQUENO + 5, 0));
        limparSelecaoAoClicar(topo);
        topo.add(titulo, BorderLayout.WEST);

        card.add(topo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void configurarTabelaReceitas(JTable tabela) {
        TableStyle.aplicarTabelaLimpa(tabela, 0);
        tabela.setRowHeight(34);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(82);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(95);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(95);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(95);
        tabela.getColumnModel().getColumn(8).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(9).setPreferredWidth(95);

        DefaultTableCellRenderer editorRenderer = new DefaultTableCellRenderer();
        editorRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        editorRenderer.setVerticalAlignment(SwingConstants.CENTER);
        editorRenderer.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        editorRenderer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        for (int coluna : new int[]{2, 4, 6, 8}) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(editorRenderer);
        }

        tabela.getColumnModel().getColumn(9).setCellRenderer(TableStyle.rendererCentro());
    }

    private void atualizarTabela() {
        modeloReceitas.setRowCount(0);

        for (ReceitaJogo receita : receitas) {
            modeloReceitas.addRow(new Object[]{
                    receita.jogo,
                    formatarInteiro(receita.bilhetes),
                    "\u270E",
                    formatarEuros(receita.bilheteira),
                    "\u270E",
                    formatarEuros(receita.patrocinio),
                    "\u270E",
                    formatarEuros(receita.direitosTv),
                    "\u270E",
                    formatarEuros(receita.lucro())
            });
        }

        atualizarResumo();
    }

    private void editarCelulaFinanceira() {
        int linha = tabelaReceitas.getSelectedRow();
        int coluna = tabelaReceitas.getSelectedColumn();

        if (linha < 0 || !(coluna == 2 || coluna == 4 || coluna == 6 || coluna == 8)) {
            return;
        }

        int linhaModelo = tabelaReceitas.convertRowIndexToModel(linha);
        ReceitaJogo receita = receitas.get(linhaModelo);

        String campo = campoDaColuna(coluna);
        String valorAtual = valorAtualDaColuna(receita, coluna);
        String novoValor = JOptionPane.showInputDialog(
                this,
                "Novo valor para " + campo + ":",
                valorAtual
        );

        if (novoValor == null || novoValor.trim().isEmpty()) {
            limparSelecao();
            return;
        }

        try {
            aplicarNovoValor(receita, coluna, novoValor.trim());
            atualizarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "O valor deve ser numerico e positivo.",
                    "Valor invalido",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        limparSelecao();
    }

    private String campoDaColuna(int coluna) {
        return switch (coluna) {
            case 2 -> "bilhetes vendidos";
            case 4 -> "bilheteira";
            case 6 -> "patroc\u00EDnio";
            case 8 -> "direitos TV";
            default -> "valor";
        };
    }

    private String valorAtualDaColuna(ReceitaJogo receita, int coluna) {
        return switch (coluna) {
            case 2 -> String.valueOf(receita.bilhetes);
            case 4 -> String.valueOf(receita.bilheteira);
            case 6 -> String.valueOf(receita.patrocinio);
            case 8 -> String.valueOf(receita.direitosTv);
            default -> "";
        };
    }

    private void aplicarNovoValor(ReceitaJogo receita, int coluna, String valor) {
        double numero = Double.parseDouble(valor.replace(".", "").replace(",", "."));

        if (numero < 0) {
            throw new NumberFormatException();
        }

        switch (coluna) {
            case 2 -> receita.bilhetes = (int) Math.round(numero);
            case 4 -> receita.bilheteira = numero;
            case 6 -> receita.patrocinio = numero;
            case 8 -> receita.direitosTv = numero;
            default -> {
            }
        }
    }

    private void atualizarResumo() {
        if (valorLucroTotal == null) {
            return;
        }

        int totalBilhetes = 0;
        double totalBilheteira = 0;
        double totalPatrocinios = 0;
        double totalDireitosTv = 0;
        double totalLucro = 0;

        for (ReceitaJogo receita : receitas) {
            totalBilhetes += receita.bilhetes;
            totalBilheteira += receita.bilheteira;
            totalPatrocinios += receita.patrocinio;
            totalDireitosTv += receita.direitosTv;
            totalLucro += receita.lucro();
        }

        double media = receitas.isEmpty() ? 0 : totalLucro / receitas.size();

        valorLucroTotal.setText(formatarEuros(totalLucro));
        valorBilheteira.setText(formatarEuros(totalBilheteira));
        valorPatrocinios.setText(formatarEuros(totalPatrocinios));
        valorDireitosTv.setText(formatarEuros(totalDireitosTv));
        valorBilhetesVendidos.setText(formatarInteiro(totalBilhetes));
        valorMediaJogo.setText(formatarEuros(media));
    }

    private void carregarDadosExemplo() {
        receitas.add(new ReceitaJogo("Porto vs Benfica", 41532, 120000, 50000, 80000));
        receitas.add(new ReceitaJogo("Sporting vs Braga", 28765, 85000, 35000, 70000));
        receitas.add(new ReceitaJogo("Benfica vs Vit\u00F3ria", 52118, 150000, 60000, 90000));
        receitas.add(new ReceitaJogo("Moreirense vs Gil", 12345, 22000, 10000, 20000));
        receitas.add(new ReceitaJogo("Rio Ave vs Estoril", 9860, 18000, 8000, 18000));
    }

    private String formatarEuros(double valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        formato.setMaximumFractionDigits(0);
        formato.setMinimumFractionDigits(0);
        return formato.format(valor).replace(" ", "");
    }

    private String formatarInteiro(int valor) {
        return NumberFormat.getIntegerInstance(Locale.GERMANY).format(valor);
    }

    private void limparSelecao() {
        if (tabelaReceitas != null) {
            tabelaReceitas.clearSelection();
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

    private static class ReceitaJogo {
        private final String jogo;
        private int bilhetes;
        private double bilheteira;
        private double patrocinio;
        private double direitosTv;

        private ReceitaJogo(String jogo, int bilhetes, double bilheteira, double patrocinio, double direitosTv) {
            this.jogo = jogo;
            this.bilhetes = bilhetes;
            this.bilheteira = bilheteira;
            this.patrocinio = patrocinio;
            this.direitosTv = direitosTv;
        }

        private double lucro() {
            return bilheteira + patrocinio + direitosTv;
        }
    }
}
