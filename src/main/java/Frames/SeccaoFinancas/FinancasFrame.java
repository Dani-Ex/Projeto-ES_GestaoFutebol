package Frames.SeccaoFinancas;

import Design.MenuLateral;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Models.Jogo;
import Models.JogoService;
import Models.Receita;
import Models.ReceitaService;

import javax.swing.*;
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
    private static final int LARGURA_CARD_RESUMO = 180;
    private static final int ESPACAMENTO_CARDS = 18;
    private static final int LARGURA_AREA_FINANCAS = (LARGURA_CARD_RESUMO * 6) + (ESPACAMENTO_CARDS * 5);

    private final List<ReceitaJogo> receitas = new ArrayList<>();
    private final JogoService jogoService = JogoService.getInstance();
    private final ReceitaService receitaService = ReceitaService.getInstance();

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

        carregarReceitas();

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
        topo.setPreferredSize(new Dimension(LARGURA_AREA_FINANCAS, 70));
        topo.setMaximumSize(new Dimension(LARGURA_AREA_FINANCAS, 70));
        topo.setAlignmentX(Component.CENTER_ALIGNMENT);
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

        JButton btnNovaReceita = new RoundedButton("+ Nova Receita", Tema.COR_INFO, Tema.COR_TEXTO_CLARO, 14);
        btnNovaReceita.setPreferredSize(new Dimension(150, 40));
        btnNovaReceita.setMinimumSize(new Dimension(150, 40));
        btnNovaReceita.setMaximumSize(new Dimension(150, 40));
        btnNovaReceita.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        btnNovaReceita.addActionListener(e -> new NovaReceitaFrame(this::recarregarReceitas));

        topo.add(tituloBox, BorderLayout.WEST);
        topo.add(btnNovaReceita, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarCardsResumo() {
        JPanel painel = new JPanel();
        painel.setFocusable(true);
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.X_AXIS));
        painel.setPreferredSize(new Dimension(LARGURA_AREA_FINANCAS, 90));
        painel.setMaximumSize(new Dimension(LARGURA_AREA_FINANCAS, 90));
        painel.setAlignmentX(Component.CENTER_ALIGNMENT);
        limparSelecaoAoClicar(painel);

        valorLucroTotal = new JLabel();
        valorBilheteira = new JLabel();
        valorPatrocinios = new JLabel();
        valorDireitosTv = new JLabel();
        valorBilhetesVendidos = new JLabel();
        valorMediaJogo = new JLabel();

        painel.add(criarCardResumo("Lucro Total", valorLucroTotal, Tema.CARD_AZUL, Tema.CARD_TEXTO_AZUL));
        painel.add(Box.createHorizontalStrut(ESPACAMENTO_CARDS));
        painel.add(criarCardResumo("Bilheteira", valorBilheteira, CARD_BILHETEIRA, Tema.CARD_TEXTO_VERDE));
        painel.add(Box.createHorizontalStrut(ESPACAMENTO_CARDS));
        painel.add(criarCardResumo("Patroc\u00EDnios", valorPatrocinios, Tema.CARD_ROXO, Tema.CARD_TEXTO_ROXO));
        painel.add(Box.createHorizontalStrut(ESPACAMENTO_CARDS));
        painel.add(criarCardResumo("Direitos TV", valorDireitosTv, CARD_DIREITOS_TV, Tema.CARD_TEXTO_LARANJA));
        painel.add(Box.createHorizontalStrut(ESPACAMENTO_CARDS));
        painel.add(criarCardResumo("Bilhetes Vendidos", valorBilhetesVendidos, CARD_BILHETES, Tema.COR_TEXTO_PRINCIPAL));
        painel.add(Box.createHorizontalStrut(ESPACAMENTO_CARDS));
        painel.add(criarCardResumo("M\u00E9dio/Jogo", valorMediaJogo, CARD_MEDIA, Tema.COR_ERRO));

        atualizarResumo();

        return painel;
    }

    private JPanel criarCardResumo(String titulo, JLabel valor, Color corFundo, Color corTitulo) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, corFundo);
        card.setFocusable(true);
        card.setPreferredSize(new Dimension(LARGURA_CARD_RESUMO, Tema.ALTURA_CARD_RESUMO));
        card.setMinimumSize(new Dimension(LARGURA_CARD_RESUMO, Tema.ALTURA_CARD_RESUMO));
        card.setMaximumSize(new Dimension(LARGURA_CARD_RESUMO, Tema.ALTURA_CARD_RESUMO));
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
        card.setPreferredSize(new Dimension(LARGURA_AREA_FINANCAS, 520));
        card.setMaximumSize(new Dimension(LARGURA_AREA_FINANCAS, Integer.MAX_VALUE));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
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
                "Bilheteira",
                "Patroc\u00EDnio",
                "Direitos TV",
                "Lucro"
        };

        modeloReceitas = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaReceitas = new JTable(modeloReceitas);
        configurarTabelaReceitas(tabelaReceitas);
        tabelaReceitas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirReceitaSelecionada();
                }
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
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(260);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(170);

        for (int coluna = 1; coluna < tabela.getColumnCount(); coluna++) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(TableStyle.rendererCentro());
        }
    }

    private void atualizarTabela() {
        modeloReceitas.setRowCount(0);

        for (ReceitaJogo receita : receitas) {
            modeloReceitas.addRow(new Object[]{
                    receita.jogo,
                    formatarInteiro(receita.bilhetes),
                    formatarEuros(receita.bilheteira),
                    formatarEuros(receita.patrocinio),
                    formatarEuros(receita.direitosTv),
                    formatarEuros(receita.lucro())
            });
        }

        atualizarResumo();
    }

    private void recarregarReceitas() {
        carregarReceitas();
        atualizarTabela();
    }

    private void abrirReceitaSelecionada() {
        int linha = tabelaReceitas.getSelectedRow();

        if (linha < 0) {
            return;
        }

        int linhaModelo = tabelaReceitas.convertRowIndexToModel(linha);

        if (linhaModelo >= 0 && linhaModelo < receitas.size()) {
            new EditarReceitaFrame(receitas.get(linhaModelo).idJogo, this::recarregarReceitas);
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

    private void carregarReceitas() {
        receitas.clear();

        for (Receita receita : receitaService.listarReceitas()) {
            Jogo jogo = jogoService.procurarPorId(receita.getIdJogo());
            String nomeJogo = jogo == null ? receita.getIdJogo() : jogo.getNomeJogo();

            receitas.add(new ReceitaJogo(
                    receita.getIdJogo(),
                    nomeJogo,
                    receita.getBilhetes(),
                    receita.getBilheteira(),
                    receita.getPatrocinio(),
                    receita.getDireitosTv()
            ));
        }
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
        private final String idJogo;
        private final String jogo;
        private final int bilhetes;
        private final double bilheteira;
        private final double patrocinio;
        private final double direitosTv;

        private ReceitaJogo(
                String idJogo,
                String jogo,
                int bilhetes,
                double bilheteira,
                double patrocinio,
                double direitosTv
        ) {
            this.idJogo = idJogo;
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
