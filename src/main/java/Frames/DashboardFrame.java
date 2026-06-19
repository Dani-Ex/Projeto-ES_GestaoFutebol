package Frames;

import Design.MenuLateral;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Models.Equipa;
import Models.EquipaService;
import Models.Jogo;
import Models.JogoService;
import Models.Receita;
import Models.ReceitaService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DashboardFrame extends JFrame {

    private final List<JTable> tabelasDashboard = new ArrayList<>();
    private final List<ReceitaJogo> receitas = new ArrayList<>();
    private final ReceitaService receitaService = ReceitaService.getInstance();
    private final JogoService jogoService = JogoService.getInstance();
    private final EquipaService equipaService = EquipaService.getInstance();

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    public DashboardFrame() {
        setTitle("Dashboard do Campeonato");
        setSize(1920, 1080);
        setMinimumSize(new Dimension(1280, 760));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        carregarReceitas();

        JPanel main = new JPanel(new BorderLayout());
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
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        limparSelecaoAoClicar(content);

        content.add(criarTopo(main));
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO));
        content.add(criarCardsResumo());
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO));
        content.add(criarSecaoMeio());
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO));
        content.add(criarSecaoInferior());

        main.add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarTopo(JPanel main) {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JPanel tituloBox = new JPanel();
        tituloBox.setOpaque(false);
        tituloBox.setLayout(new BoxLayout(tituloBox, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Dashboard do Campeonato");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel(
                "Resumo geral com indicadores, ranking das equipas, proximos jogos e ultimos jogos."
        );
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        tituloBox.add(titulo);
        tituloBox.add(Box.createVerticalStrut(4));
        tituloBox.add(subtitulo);

        JButton btnMenu = new JButton("=");
        btnMenu.setFont(Tema.FONTE_BOTAO_MENU);
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setBackground(Tema.COR_FUNDO);
        btnMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        btnMenu.setPreferredSize(new Dimension(55, 45));
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
        JPanel painel = new JPanel(new GridLayout(1, 4, 18, 0));
        painel.setOpaque(false);
        painel.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                Tema.ALTURA_CARD_RESUMO
        ));

        ResumoFinanceiro resumo = calcularResumoFinanceiro();

        painel.add(criarCardResumo(
                "Patrocinios Totais",
                formatarEuros(resumo.totalPatrocinios),
                Tema.CARD_AZUL,
                Tema.CARD_TEXTO_AZUL
        ));

        painel.add(criarCardResumo(
                "Bilhetes Vendidos",
                formatarInteiro(resumo.totalBilhetes),
                Tema.CARD_VERDE,
                Tema.CARD_TEXTO_VERDE
        ));

        painel.add(criarCardResumo(
                "Lucro Medio por Jogo",
                formatarEuros(resumo.mediaLucroPorJogo),
                Tema.CARD_ROXO,
                Tema.CARD_TEXTO_ROXO
        ));

        painel.add(criarCardResumo(
                "Lucro Total",
                formatarEuros(resumo.totalLucro),
                Tema.CARD_AMARELO,
                Tema.CARD_TEXTO_LARANJA
        ));

        return painel;
    }

    private JPanel criarCardResumo(String titulo, String valor, Color corFundo, Color corTitulo) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, corFundo);
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

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(Tema.FONTE_CARD_VALOR);
        lblValor.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarSecaoMeio() {
        JPanel meio = new JPanel(new GridLayout(1, 2, 20, 0));
        meio.setOpaque(false);
        meio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        meio.add(criarCardGrafico());
        meio.add(criarCardGrupo());

        return meio;
    }

    private JPanel criarCardGrafico() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));
        limparSelecaoAoClicar(card);

        JLabel titulo = new JLabel("Lucro por Jogo");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {"Jogo", "Lucro"};
        DefaultTableModel model = criarModeloNaoEditavel(colunas);

        for (ReceitaJogo receita : receitas) {
            model.addRow(new Object[]{receita.jogo, formatarEuros(receita.lucro())});
        }

        JTable tabela = new JTable(model);
        configurarTabela(tabela, 0);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(260);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(100);

        tabelasDashboard.add(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, Tema.ESPACAMENTO_PEQUENO, 0));
        limparSelecaoAoClicar(topo);
        topo.add(titulo, BorderLayout.WEST);

        card.add(topo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardGrupo() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));
        limparSelecaoAoClicar(card);

        JLabel titulo = new JLabel("Classificacao");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {"#", "Equipa", "Grupo", "Golos", "Jog.", "Pts"};
        DefaultTableModel model = criarModeloNaoEditavel(colunas);

        List<Equipa> equipas = new ArrayList<>(equipaService.listarEquipas());
        equipas.sort(Comparator
                .comparingInt(Equipa::getPontos).reversed()
                .thenComparing(Comparator.comparingInt(Equipa::getGolos).reversed())
                .thenComparing(Equipa::getNome));

        int limite = Math.min(4, equipas.size());
        for (int i = 0; i < limite; i++) {
            Equipa equipa = equipas.get(i);
            model.addRow(new Object[]{
                    String.valueOf(i + 1),
                    equipa.getNome(),
                    valorOuTraco(equipa.getGrupo()),
                    String.valueOf(equipa.getGolos()),
                    String.valueOf(equipa.getTotalJogadores()),
                    String.valueOf(equipa.getPontos())
            });
        }

        JTable tabela = new JTable(model);
        configurarTabelaGrupo(tabela);
        tabelasDashboard.add(tabela);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(95);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(55);

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarSecaoInferior() {
        JPanel baixo = new JPanel(new GridLayout(1, 2, 20, 0));
        baixo.setOpaque(false);
        baixo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        baixo.add(criarTabelaProximosJogos());
        baixo.add(criarTabelaUltimosJogos());

        return baixo;
    }

    private JPanel criarTabelaProximosJogos() {
        RoundedPanel card = criarCardTabela("Proximos Jogos");

        String[] colunas = {"Data", "Jogo", "Estadio"};
        DefaultTableModel model = criarModeloNaoEditavel(colunas);

        for (Jogo jogo : listarJogosPorEstado("Agendado")) {
            model.addRow(new Object[]{
                    formatarData(jogo.getData()),
                    jogo.getNomeJogo(),
                    jogo.getEstadio()
            });
        }

        JTable tabela = new JTable(model);
        configurarTabela(tabela, 1);
        tabelasDashboard.add(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarTabelaUltimosJogos() {
        RoundedPanel card = criarCardTabela("Ultimos Jogos");

        String[] colunas = {"Data", "Jogo", "Estadio", "Resultado"};
        DefaultTableModel model = criarModeloNaoEditavel(colunas);

        for (Jogo jogo : listarJogosPorEstado("Realizado")) {
            model.addRow(new Object[]{
                    formatarData(jogo.getData()),
                    jogo.getNomeJogo(),
                    jogo.getEstadio(),
                    jogo.getResultado()
            });
        }

        JTable tabela = new JTable(model);
        configurarTabela(tabela, 1);
        tabelasDashboard.add(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private RoundedPanel criarCardTabela(String tituloTexto) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));
        limparSelecaoAoClicar(card);

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, Tema.ESPACAMENTO_PEQUENO, 0));

        card.add(titulo, BorderLayout.NORTH);

        return card;
    }

    private void configurarTabela(JTable tabela, int colunaEsquerda) {
        TableStyle.aplicarTabelaLimpa(tabela, colunaEsquerda);
        tabela.setRowHeight(34);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    private void configurarTabelaGrupo(JTable tabela) {
        TableStyle.aplicarTabelaLimpa(tabela, 1);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
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

                setFont(Tema.FONTE_TEXTO_PEQUENO);
                setVerticalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);

                if (isSelected) {
                    c.setBackground(Tema.COR_SELECAO_NEUTRA);
                } else if (row == 0 || row == 1) {
                    c.setBackground(Tema.COR_VERDE_SUAVE);
                } else {
                    c.setBackground(Tema.COR_CARD);
                }

                c.setForeground(Tema.COR_TEXTO_PRINCIPAL);

                return c;
            }
        };

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    private DefaultTableModel criarModeloNaoEditavel(String[] colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void carregarReceitas() {
        receitas.clear();

        for (Receita receita : receitaService.listarReceitas()) {
            Jogo jogo = jogoService.procurarPorId(receita.getIdJogo());
            String nomeJogo = jogo == null ? receita.getIdJogo() : jogo.getNomeJogo();

            receitas.add(new ReceitaJogo(
                    nomeJogo,
                    receita.getBilhetes(),
                    receita.getBilheteira(),
                    receita.getPatrocinio(),
                    receita.getDireitosTv()
            ));
        }
    }

    private ResumoFinanceiro calcularResumoFinanceiro() {
        ResumoFinanceiro resumo = new ResumoFinanceiro();

        for (ReceitaJogo receita : receitas) {
            resumo.totalBilhetes += receita.bilhetes;
            resumo.totalPatrocinios += receita.patrocinio;
            resumo.totalLucro += receita.lucro();
        }

        resumo.mediaLucroPorJogo = receitas.isEmpty() ? 0 : resumo.totalLucro / receitas.size();

        return resumo;
    }

    private List<Jogo> listarJogosPorEstado(String estado) {
        List<Jogo> resultado = new ArrayList<>();

        for (Jogo jogo : jogoService.listarJogos()) {
            if (jogo.getEstado().equalsIgnoreCase(estado)) {
                resultado.add(jogo);
            }
        }

        resultado.sort(Comparator.comparing(Jogo::getData));

        if ("Realizado".equalsIgnoreCase(estado)) {
            resultado.sort(Comparator.comparing(Jogo::getData).reversed());
        }

        if (resultado.size() > 4) {
            return new ArrayList<>(resultado.subList(0, 4));
        }

        return resultado;
    }

    private String formatarData(String data) {
        try {
            LocalDate localDate = LocalDate.parse(data);
            return localDate.format(DateTimeFormatter.ofPattern("dd/MM"));
        } catch (DateTimeParseException e) {
            return data;
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

    private String valorOuTraco(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    private void limparSelecaoTabelas() {
        for (JTable tabela : tabelasDashboard) {
            tabela.clearSelection();
        }
    }

    private void limparSelecaoAoClicar(JComponent componente) {
        componente.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                limparSelecaoTabelas();
            }
        });
    }

    private static class ReceitaJogo {
        private final String jogo;
        private final int bilhetes;
        private final double bilheteira;
        private final double patrocinio;
        private final double direitosTv;

        private ReceitaJogo(
                String jogo,
                int bilhetes,
                double bilheteira,
                double patrocinio,
                double direitosTv
        ) {
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

    private static class ResumoFinanceiro {
        private int totalBilhetes;
        private double totalPatrocinios;
        private double totalLucro;
        private double mediaLucroPorJogo;
    }
}
