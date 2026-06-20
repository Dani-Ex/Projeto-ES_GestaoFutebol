package Frames.SeccaoFinancas;

import Design.MenuLateral;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Models.Campeonatos.CampeonatoRepositorio;
import Models.Jogos.Jogo;
import Models.Jogos.JogoService;
import Models.Bilheteria.Bilhete;
import Models.Bilheteria.BilheteriaService;
import Models.Financas.Receita;
import Models.Financas.ReceitaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FinancasFrame extends JFrame {

    private static final String FINANCAS_TOTAIS = "Todas as finanças";
    private static final Color CARD_BILHETEIRA = new Color(220, 252, 231);
    private static final Color CARD_DIREITOS_TV = new Color(255, 237, 213);
    private static final Color CARD_BILHETES = new Color(229, 231, 235);
    private static final Color CARD_MEDIA = new Color(254, 226, 226);
    private static final int LARGURA_CARD_RESUMO = 180;
    private static final int ESPACAMENTO_CARDS = 18;
    private static final int LARGURA_AREA_FINANCAS = (LARGURA_CARD_RESUMO * 6) + (ESPACAMENTO_CARDS * 5);

    private final List<ReceitaJogo> receitas = new ArrayList<>();
    private final List<ReceitaJogo> receitasVisiveis = new ArrayList<>();
    private final JogoService jogoService = JogoService.getInstance();
    private final ReceitaService receitaService = ReceitaService.getInstance();
    private final BilheteriaService bilheteriaService = new BilheteriaService();

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
    private JComboBox<String> comboCampeonatoFinancas;

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

        comboCampeonatoFinancas = new JComboBox<>(listarOpcoesCampeonatosFinancas().toArray(new String[0]));
        comboCampeonatoFinancas.setFont(Tema.FONTE_CARD_TITULO);
        comboCampeonatoFinancas.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        comboCampeonatoFinancas.setBackground(Tema.COR_BOTAO_SECUNDARIO);
        comboCampeonatoFinancas.setFocusable(false);
        comboCampeonatoFinancas.setPreferredSize(new Dimension(230, 40));
        comboCampeonatoFinancas.setMaximumSize(new Dimension(230, 40));
        comboCampeonatoFinancas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboCampeonatoFinancas.setRenderer(criarRendererComboFinancas());
        comboCampeonatoFinancas.addActionListener(e -> atualizarTabela());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        acoes.setOpaque(false);
        acoes.add(comboCampeonatoFinancas);
        acoes.add(btnNovaReceita);

        topo.add(tituloBox, BorderLayout.WEST);
        topo.add(acoes, BorderLayout.EAST);

        return topo;
    }

    private List<String> listarOpcoesCampeonatosFinancas() {
        List<String> opcoes = new ArrayList<>();
        opcoes.add(FINANCAS_TOTAIS);

        for (String campeonato : CampeonatoRepositorio.listarNomesCampeonatosGuardados()) {
            if (campeonato != null && !campeonato.trim().isEmpty()) {
                opcoes.add(campeonato);
            }
        }

        return opcoes;
    }

    private ListCellRenderer<? super String> criarRendererComboFinancas() {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();

        return (list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = (JLabel) renderer.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus
            );

            if (FINANCAS_TOTAIS.equals(value)) {
                label.setFont(Tema.FONTE_CARD_TITULO.deriveFont(Font.BOLD));
                if (!isSelected) {
                    label.setForeground(Tema.CARD_TEXTO_AZUL);
                }
            }

            return label;
        };
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
        receitasVisiveis.clear();

        for (ReceitaJogo receita : receitasFiltradasPorCampeonato()) {
            receitasVisiveis.add(receita);
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
        atualizarOpcoesCampeonatosFinancas();
        atualizarTabela();
    }

    private void abrirReceitaSelecionada() {
        int linha = tabelaReceitas.getSelectedRow();

        if (linha < 0) {
            return;
        }

        int linhaModelo = tabelaReceitas.convertRowIndexToModel(linha);

        if (linhaModelo >= 0 && linhaModelo < receitasVisiveis.size()) {
            new EditarReceitaFrame(receitasVisiveis.get(linhaModelo).idJogo, this::recarregarReceitas);
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

        for (ReceitaJogo receita : receitasVisiveis) {
            totalBilhetes += receita.bilhetes;
            totalBilheteira += receita.bilheteira;
            totalPatrocinios += receita.patrocinio;
            totalDireitosTv += receita.direitosTv;
            totalLucro += receita.lucro();
        }

        double media = receitasVisiveis.isEmpty() ? 0 : totalLucro / receitasVisiveis.size();

        valorLucroTotal.setText(formatarEuros(totalLucro));
        valorBilheteira.setText(formatarEuros(totalBilheteira));
        valorPatrocinios.setText(formatarEuros(totalPatrocinios));
        valorDireitosTv.setText(formatarEuros(totalDireitosTv));
        valorBilhetesVendidos.setText(formatarInteiro(totalBilhetes));
        valorMediaJogo.setText(formatarEuros(media));
    }

    private void atualizarOpcoesCampeonatosFinancas() {
        if (comboCampeonatoFinancas == null) {
            return;
        }

        String selecionado = getCampeonatoFinancasSelecionado();
        comboCampeonatoFinancas.removeAllItems();

        for (String opcao : listarOpcoesCampeonatosFinancas()) {
            comboCampeonatoFinancas.addItem(opcao);
        }

        comboCampeonatoFinancas.setSelectedItem(selecionado);
        if (comboCampeonatoFinancas.getSelectedItem() == null) {
            comboCampeonatoFinancas.setSelectedItem(FINANCAS_TOTAIS);
        }
    }

    private List<ReceitaJogo> receitasFiltradasPorCampeonato() {
        String campeonato = getCampeonatoFinancasSelecionado();

        if (FINANCAS_TOTAIS.equals(campeonato)) {
            return new ArrayList<>(receitas);
        }

        List<ReceitaJogo> filtradas = new ArrayList<>();
        for (ReceitaJogo receita : receitas) {
            if (textoIgual(receita.campeonato, campeonato)) {
                filtradas.add(receita);
            }
        }

        return filtradas;
    }

    private String getCampeonatoFinancasSelecionado() {
        if (comboCampeonatoFinancas == null || comboCampeonatoFinancas.getSelectedItem() == null) {
            return FINANCAS_TOTAIS;
        }

        return comboCampeonatoFinancas.getSelectedItem().toString();
    }

    private boolean textoIgual(String valor, String esperado) {
        return valorOuTraco(valor).equalsIgnoreCase(valorOuTraco(esperado));
    }

    private String valorOuTraco(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    private void carregarReceitas() {
        receitas.clear();
        Map<String, ResumoBilheteira> bilheteiraPorJogo = carregarBilheteiraPorJogo();

        for (Receita receita : receitaService.listarReceitas()) {
            String idJogo = receita.getIdJogo();

            Jogo jogo = procurarJogoDaReceita(idJogo);
            String nomeJogo = jogo == null ? receita.getIdJogo() : jogo.getNomeJogo();
            ResumoBilheteira bilheteira = bilheteiraPorJogo.get(idJogo);

            receitas.add(new ReceitaJogo(
                    idJogo,
                    nomeJogo,
                    obterCampeonato(jogo),
                    bilheteira == null ? receita.getBilhetes() : bilheteira.bilhetes,
                    bilheteira == null ? receita.getBilheteira() : bilheteira.total,
                    receita.getPatrocinio(),
                    receita.getDireitosTv()
            ));
        }

    }

    private Map<String, ResumoBilheteira> carregarBilheteiraPorJogo() {
        Map<String, ResumoBilheteira> resumoPorJogo = new LinkedHashMap<>();

        for (Bilhete bilhete : bilheteriaService.listarBilhetes()) {
            ResumoBilheteira resumo = resumoPorJogo.computeIfAbsent(
                    bilhete.getIdJogo(),
                    idJogo -> new ResumoBilheteira()
            );

            resumo.bilhetes += bilhete.getQuantidade();
            resumo.total += bilhete.getTotal();
        }

        return resumoPorJogo;
    }

    private Jogo procurarJogoDaReceita(String idJogo) {
        if (idJogo == null) {
            return null;
        }

        String[] partes = idJogo.split("::", 2);
        if (partes.length == 2) {
            for (Jogo jogo : jogoService.listarJogos()) {
                if (jogo.getId().equalsIgnoreCase(partes[0])
                        && jogo.getCampeonato() != null
                        && jogo.getCampeonato().equalsIgnoreCase(partes[1])) {
                    return jogo;
                }
            }
        }

        return jogoService.procurarPorId(idJogo);
    }

    private String obterCampeonato(Jogo jogo) {
        if (jogo == null || jogo.getCampeonato() == null || jogo.getCampeonato().trim().isEmpty()) {
            return "Sem campeonato";
        }

        return jogo.getCampeonato();
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
        private final String campeonato;
        private final int bilhetes;
        private final double bilheteira;
        private final double patrocinio;
        private final double direitosTv;

        private ReceitaJogo(
                String idJogo,
                String jogo,
                String campeonato,
                int bilhetes,
                double bilheteira,
                double patrocinio,
                double direitosTv
        ) {
            this.idJogo = idJogo;
            this.jogo = jogo;
            this.campeonato = campeonato;
            this.bilhetes = bilhetes;
            this.bilheteira = bilheteira;
            this.patrocinio = patrocinio;
            this.direitosTv = direitosTv;
        }

        private double lucro() {
            return bilheteira + patrocinio + direitosTv;
        }
    }

    private static class ResumoBilheteira {
        private int bilhetes;
        private double total;
    }
}
