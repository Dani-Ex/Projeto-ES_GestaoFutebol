package Frames;

import Design.MenuLateral;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Frames.SeccaoEquipas.ConsultaEquipaFrame;
import Models.Campeonato;
import Models.CampeonatoRepositorio;
import Models.Bilhete;
import Models.BilheteriaService;
import Models.Equipa;
import Models.EquipaService;
import Models.DashboardLogic;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class DashboardFrame extends JFrame {

    private final List<JTable> tabelasDashboard = new ArrayList<>();
    private final List<ReceitaJogo> receitas = new ArrayList<>();
    private final List<Jogo> proximosJogosVisiveis = new ArrayList<>();
    private final List<Jogo> ultimosJogosVisiveis = new ArrayList<>();
    private final ReceitaService receitaService = ReceitaService.getInstance();
    private final BilheteriaService bilheteriaService = new BilheteriaService();
    private final JogoService jogoService = JogoService.getInstance();
    private final EquipaService equipaService = EquipaService.getInstance();

    private MenuLateral menuLateral;
    private boolean menuAberto = false;
    private JLabel tituloClassificacao;
    private JComboBox<String> comboFaseClassificacao;
    private JComboBox<String> comboCampeonatoClassificacao;
    private DefaultTableModel modeloClassificacao;
    private JTable tabelaClassificacao;
    private final List<Equipa> equipasClassificacaoVisiveis = new ArrayList<>();
    private List<String> gruposClassificacao = new ArrayList<>();
    private int indiceGrupoClassificacao = 0;

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

        JLabel titulo = new JLabel(TipoGraficoLucro.JOGOS_RECENTES.titulo);
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        GraficoLucro grafico = new GraficoLucro(criarDadosGrafico(TipoGraficoLucro.JOGOS_RECENTES));
        limparSelecaoAoClicar(grafico);

        JComboBox<TipoGraficoLucro> comboGrafico = new JComboBox<>(TipoGraficoLucro.values());
        comboGrafico.setSelectedItem(TipoGraficoLucro.JOGOS_RECENTES);
        comboGrafico.setFont(Tema.FONTE_CARD_TITULO);
        comboGrafico.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        comboGrafico.setBackground(Tema.COR_BOTAO_SECUNDARIO);
        comboGrafico.setFocusable(false);
        comboGrafico.setPreferredSize(new Dimension(190, 34));
        comboGrafico.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboGrafico.addActionListener(e -> {
            TipoGraficoLucro tipoSelecionado = (TipoGraficoLucro) comboGrafico.getSelectedItem();

            if (tipoSelecionado == null) {
                return;
            }

            titulo.setText(tipoSelecionado.titulo);
            grafico.atualizarDados(criarDadosGrafico(tipoSelecionado));
        });

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, Tema.ESPACAMENTO_PEQUENO, 0));
        limparSelecaoAoClicar(topo);
        topo.add(titulo, BorderLayout.WEST);
        topo.add(comboGrafico, BorderLayout.EAST);

        card.add(topo, BorderLayout.NORTH);
        card.add(grafico, BorderLayout.CENTER);

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

        tituloClassificacao = new JLabel("Classificacao");
        tituloClassificacao.setFont(Tema.FONTE_TITULO);
        tituloClassificacao.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        comboFaseClassificacao = new JComboBox<>(new String[]{"Fase de Grupos", "Fase Eliminatoria"});
        comboFaseClassificacao.setFont(Tema.FONTE_CARD_TITULO);
        comboFaseClassificacao.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        comboFaseClassificacao.setBackground(Tema.COR_BOTAO_SECUNDARIO);
        comboFaseClassificacao.setFocusable(false);
        comboFaseClassificacao.setPreferredSize(new Dimension(170, 34));
        comboFaseClassificacao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboFaseClassificacao.addActionListener(e -> selecionarFaseClassificacao());

        comboCampeonatoClassificacao = new JComboBox<>(
                CampeonatoRepositorio.listarNomesCampeonatosParaClassificacao().toArray(new String[0])
        );
        comboCampeonatoClassificacao.setFont(Tema.FONTE_CARD_TITULO);
        comboCampeonatoClassificacao.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        comboCampeonatoClassificacao.setBackground(Tema.COR_BOTAO_SECUNDARIO);
        comboCampeonatoClassificacao.setFocusable(false);
        comboCampeonatoClassificacao.setPreferredSize(new Dimension(190, 34));
        comboCampeonatoClassificacao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboCampeonatoClassificacao.addActionListener(e -> selecionarCampeonatoClassificacao());

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, Tema.ESPACAMENTO_PEQUENO, 0));
        limparSelecaoAoClicar(topo);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filtros.setOpaque(false);
        filtros.add(comboFaseClassificacao);
        filtros.add(comboCampeonatoClassificacao);

        topo.add(tituloClassificacao, BorderLayout.WEST);
        topo.add(filtros, BorderLayout.EAST);

        String[] colunas = {"#", "Equipa", "Golos", "Jog.", "Pts"};
        modeloClassificacao = criarModeloNaoEditavel(colunas);

        tabelaClassificacao = new JTable(modeloClassificacao);
        configurarTabelaGrupo(tabelaClassificacao);
        tabelaClassificacao.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirEquipaClassificacaoSelecionada();
                }
            }
        });
        tabelasDashboard.add(tabelaClassificacao);

        tabelaClassificacao.getColumnModel().getColumn(0).setPreferredWidth(35);
        tabelaClassificacao.getColumnModel().getColumn(1).setPreferredWidth(190);
        tabelaClassificacao.getColumnModel().getColumn(2).setPreferredWidth(60);
        tabelaClassificacao.getColumnModel().getColumn(3).setPreferredWidth(55);
        tabelaClassificacao.getColumnModel().getColumn(4).setPreferredWidth(55);

        JScrollPane scroll = new JScrollPane(tabelaClassificacao);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);

        JPanel navegacao = criarNavegacaoGrupos();

        atualizarGruposClassificacao();
        atualizarClassificacao();

        card.add(topo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(navegacao, BorderLayout.SOUTH);

        return card;
    }

    private JPanel criarNavegacaoGrupos() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createEmptyBorder(Tema.ESPACAMENTO_PEQUENO, 0, 0, 0));
        limparSelecaoAoClicar(painel);

        JButton btnAnterior = criarBotaoSetaGrupo("<");
        JButton btnSeguinte = criarBotaoSetaGrupo(">");

        btnAnterior.addActionListener(e -> navegarGrupoClassificacao(-1));
        btnSeguinte.addActionListener(e -> navegarGrupoClassificacao(1));

        painel.add(btnAnterior);
        painel.add(btnSeguinte);

        return painel;
    }

    private JButton criarBotaoSetaGrupo(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(Tema.FONTE_BOTAO_MENU);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(Tema.COR_BOTAO_SECUNDARIO);
        botao.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        botao.setPreferredSize(new Dimension(52, 30));
        botao.setMinimumSize(new Dimension(52, 30));
        botao.setMaximumSize(new Dimension(52, 30));
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
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
        proximosJogosVisiveis.clear();

        for (Jogo jogo : listarJogosPorEstado("Agendado")) {
            proximosJogosVisiveis.add(jogo);
            model.addRow(new Object[]{
                    formatarData(jogo.getData()),
                    jogo.getNomeJogo(),
                    jogo.getEstadio()
            });
        }

        JTable tabela = new JTable(model);
        configurarTabela(tabela, 1);
        adicionarDuploCliqueJogo(tabela, proximosJogosVisiveis);
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
        ultimosJogosVisiveis.clear();

        for (Jogo jogo : listarJogosPorEstado("Realizado")) {
            ultimosJogosVisiveis.add(jogo);
            model.addRow(new Object[]{
                    formatarData(jogo.getData()),
                    jogo.getNomeJogo(),
                    jogo.getEstadio(),
                    jogo.getResultado()
            });
        }

        JTable tabela = new JTable(model);
        configurarTabela(tabela, 1);
        adicionarDuploCliqueJogo(tabela, ultimosJogosVisiveis);
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

    private void adicionarDuploCliqueJogo(JTable tabela, List<Jogo> jogosVisiveis) {
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }

                int linha = tabela.getSelectedRow();
                if (linha < 0) {
                    return;
                }

                int linhaModelo = tabela.convertRowIndexToModel(linha);
                if (linhaModelo >= 0 && linhaModelo < jogosVisiveis.size()) {
                    new DetalheJogoFrame(jogosVisiveis.get(linhaModelo), () ->
                            SwingUtilities.invokeLater(() -> {
                                carregarReceitas();
                                atualizarClassificacao();
                            })
                    );
                }
            }
        });
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

    private void selecionarCampeonatoClassificacao() {
        indiceGrupoClassificacao = 0;
        atualizarGruposClassificacao();
        atualizarClassificacao();
    }

    private void selecionarFaseClassificacao() {
        indiceGrupoClassificacao = 0;
        atualizarGruposClassificacao();
        atualizarClassificacao();
    }

    private void navegarGrupoClassificacao(int direcao) {
        if (isFaseEliminatoriaSelecionada()) {
            return;
        }

        if (gruposClassificacao.isEmpty()) {
            return;
        }

        indiceGrupoClassificacao += direcao;

        if (indiceGrupoClassificacao < 0) {
            indiceGrupoClassificacao = gruposClassificacao.size() - 1;
        } else if (indiceGrupoClassificacao >= gruposClassificacao.size()) {
            indiceGrupoClassificacao = 0;
        }

        atualizarClassificacao();
    }

    private void atualizarGruposClassificacao() {
        if (isFaseEliminatoriaSelecionada()) {
            gruposClassificacao = new ArrayList<>();
            gruposClassificacao.add("Eliminatoria");
            indiceGrupoClassificacao = 0;
            return;
        }

        gruposClassificacao = listarGruposDoCampeonato(getCampeonatoClassificacaoSelecionado());

        if (gruposClassificacao.isEmpty()) {
            gruposClassificacao.add("Sem grupo");
        }

        if (indiceGrupoClassificacao >= gruposClassificacao.size()) {
            indiceGrupoClassificacao = 0;
        }
    }

    private void atualizarClassificacao() {
        if (modeloClassificacao == null) {
            return;
        }

        modeloClassificacao.setRowCount(0);
        equipasClassificacaoVisiveis.clear();

        String campeonato = getCampeonatoClassificacaoSelecionado();
        String grupo = getGrupoClassificacaoSelecionado();

        if (isFaseEliminatoriaSelecionada()) {
            tituloClassificacao.setText("Classificacao Eliminatoria");
        } else {
            tituloClassificacao.setText("Classificacao Grupo " + valorOuTraco(grupo));
        }

        List<Equipa> equipas = listarEquipasClassificacao(campeonato, grupo);
        equipasClassificacaoVisiveis.addAll(equipas);

        for (int i = 0; i < equipas.size(); i++) {
            Equipa equipa = equipas.get(i);
            modeloClassificacao.addRow(new Object[]{
                    String.valueOf(i + 1),
                    equipa.getNome(),
                    String.valueOf(equipa.getGolos()),
                    String.valueOf(equipa.getTotalJogadores()),
                    String.valueOf(equipa.getPontos())
            });
        }
    }

    private List<String> listarGruposDoCampeonato(String campeonato) {
        List<String> grupos = new ArrayList<>();

        for (Equipa equipa : equipaService.listarEquipas()) {
            if (!textoIgual(equipa.getCampeonato(), campeonato)) {
                continue;
            }

            String grupo = valorOuTraco(equipa.getGrupo());

            if (!grupos.contains(grupo)) {
                grupos.add(grupo);
            }
        }

        grupos.sort(String.CASE_INSENSITIVE_ORDER);
        return grupos;
    }

    private List<Equipa> listarEquipasClassificacao(String campeonato, String grupo) {
        if (isFaseEliminatoriaSelecionada()) {
            return listarEquipasEliminatoria(campeonato);
        }

        return DashboardLogic.ordenarClassificacao(equipaService.listarEquipas(), campeonato, grupo);
    }

    private List<Equipa> listarEquipasEliminatoria(String campeonato) {
        List<Equipa> equipas = new ArrayList<>();
        Campeonato campeonatoSelecionado = CampeonatoRepositorio.procurarPorNome(campeonato);

        if (campeonatoSelecionado == null
                || campeonatoSelecionado.getEquipasEliminatorias() == null
                || campeonatoSelecionado.getEquipasEliminatorias().isEmpty()) {
            return equipas;
        }

        for (String nomeEquipa : campeonatoSelecionado.getEquipasEliminatorias()) {
            Equipa equipa = procurarEquipaDoCampeonato(nomeEquipa, campeonato);
            if (equipa != null) {
                equipas.add(equipa);
            }
        }

        return equipas;
    }

    private Equipa procurarEquipaDoCampeonato(String nomeEquipa, String campeonato) {
        for (Equipa equipa : equipaService.listarEquipas()) {
            if (textoIgual(equipa.getNome(), nomeEquipa)
                    && textoIgual(equipa.getCampeonato(), campeonato)) {
                return equipa;
            }
        }

        return null;
    }

    private boolean isFaseEliminatoriaSelecionada() {
        return comboFaseClassificacao != null
                && comboFaseClassificacao.getSelectedItem() != null
                && comboFaseClassificacao.getSelectedItem().toString().toLowerCase(Locale.ROOT).contains("eliminatoria");
    }

    private String getCampeonatoClassificacaoSelecionado() {
        if (comboCampeonatoClassificacao == null
                || comboCampeonatoClassificacao.getSelectedItem() == null) {
            List<String> campeonatos = CampeonatoRepositorio.listarNomesCampeonatosParaClassificacao();
            return campeonatos.isEmpty() ? "" : campeonatos.get(0);
        }

        return comboCampeonatoClassificacao.getSelectedItem().toString();
    }

    private String getGrupoClassificacaoSelecionado() {
        if (gruposClassificacao.isEmpty()) {
            return "Sem grupo";
        }

        return gruposClassificacao.get(indiceGrupoClassificacao);
    }

    private boolean textoIgual(String valor, String esperado) {
        return valorOuTraco(valor).equalsIgnoreCase(valorOuTraco(esperado));
    }

    private void abrirEquipaClassificacaoSelecionada() {
        if (tabelaClassificacao == null) {
            return;
        }

        int linha = tabelaClassificacao.getSelectedRow();

        if (linha < 0) {
            return;
        }

        int linhaModelo = tabelaClassificacao.convertRowIndexToModel(linha);

        if (linhaModelo >= 0 && linhaModelo < equipasClassificacaoVisiveis.size()) {
            new ConsultaEquipaFrame(equipasClassificacaoVisiveis.get(linhaModelo), this::atualizarClassificacao);
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
            Jogo jogo = procurarJogoDaReceita(receita.getIdJogo());
            String nomeJogo = jogo == null ? receita.getIdJogo() : jogo.getNomeJogo();

            receitas.add(new ReceitaJogo(
                    nomeJogo,
                    obterData(jogo),
                    obterCampeonato(jogo),
                    receita.getBilhetes(),
                    receita.getBilheteira(),
                    receita.getPatrocinio(),
                    receita.getDireitosTv()
            ));
        }
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

    private List<PontoGrafico> criarDadosGrafico(TipoGraficoLucro tipo) {
        switch (tipo) {
            case CAMPEONATO:
                return criarLucrosPorCampeonato();
            case JOGOS_RECENTES:
                return criarLucrosJogosRecentes();
            case MES:
            default:
                return criarLucrosMensaisRecentes();
        }
    }

    private List<PontoGrafico> criarLucrosMensaisRecentes() {
        Map<YearMonth, Double> lucroPorMes = new TreeMap<>();

        for (ReceitaJogo receita : receitas) {
            if (receita.data == null) {
                continue;
            }

            lucroPorMes.merge(YearMonth.from(receita.data), receita.lucro(), Double::sum);
        }

        List<PontoGrafico> lucros = new ArrayList<>();

        for (Map.Entry<YearMonth, Double> entry : lucroPorMes.entrySet()) {
            String label = entry.getKey().format(DateTimeFormatter.ofPattern("MMM/yy", Locale.forLanguageTag("pt-PT")));
            lucros.add(new PontoGrafico(label, entry.getValue()));
        }

        int limiteMeses = 6;
        if (lucros.size() > limiteMeses) {
            return new ArrayList<>(lucros.subList(lucros.size() - limiteMeses, lucros.size()));
        }

        return lucros;
    }

    private List<PontoGrafico> criarLucrosPorCampeonato() {
        Map<String, Double> lucroPorCampeonato = new TreeMap<>();

        for (ReceitaJogo receita : receitas) {
            lucroPorCampeonato.merge(receita.campeonato, receita.lucro(), Double::sum);
        }

        List<PontoGrafico> lucros = new ArrayList<>();

        for (Map.Entry<String, Double> entry : lucroPorCampeonato.entrySet()) {
            lucros.add(new PontoGrafico(abreviarLabel(entry.getKey()), entry.getValue()));
        }

        lucros.sort(Comparator.comparingDouble(PontoGrafico::getValor).reversed());

        int limiteCampeonatos = 6;
        if (lucros.size() > limiteCampeonatos) {
            return new ArrayList<>(lucros.subList(0, limiteCampeonatos));
        }

        return lucros;
    }

    private List<PontoGrafico> criarLucrosJogosRecentes() {
        List<ReceitaJogo> receitasComData = new ArrayList<>();

        for (ReceitaJogo receita : receitas) {
            if (receita.data != null) {
                receitasComData.add(receita);
            }
        }

        receitasComData.sort(Comparator.comparing((ReceitaJogo receita) -> receita.data).reversed());

        List<PontoGrafico> lucros = new ArrayList<>();
        int limiteJogos = Math.min(6, receitasComData.size());

        for (int i = 0; i < limiteJogos; i++) {
            ReceitaJogo receita = receitasComData.get(i);
            lucros.add(new PontoGrafico(formatarData(receita.data.toString()), receita.lucro()));
        }

        return lucros;
    }

    private ResumoFinanceiro calcularResumoFinanceiro() {
        ResumoFinanceiro resumo = new ResumoFinanceiro();
        Map<String, ResumoBilheteira> bilheteiraPorJogo = carregarBilheteiraPorJogo();
        int totalJogosComReceita = 0;

        for (Receita receita : receitaService.listarReceitas()) {
            ResumoBilheteira bilheteira = bilheteiraPorJogo.get(receita.getIdJogo());
            int bilhetes = bilheteira == null ? receita.getBilhetes() : bilheteira.bilhetes;
            double totalBilheteira = bilheteira == null ? receita.getBilheteira() : bilheteira.total;

            resumo.totalBilhetes += bilhetes;
            resumo.totalPatrocinios += receita.getPatrocinio();
            resumo.totalLucro += totalBilheteira + receita.getPatrocinio() + receita.getDireitosTv();
            totalJogosComReceita++;
        }

        resumo.mediaLucroPorJogo = totalJogosComReceita == 0 ? 0 : resumo.totalLucro / totalJogosComReceita;

        return resumo;
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

    private List<Jogo> listarJogosPorEstado(String estado) {
        return DashboardLogic.listarJogosPorEstado(jogoService.listarJogos(), estado, 4);
    }

    private String formatarData(String data) {
        try {
            LocalDate localDate = LocalDate.parse(data);
            return localDate.format(DateTimeFormatter.ofPattern("dd/MM"));
        } catch (DateTimeParseException e) {
            return data;
        }
    }

    private LocalDate obterData(Jogo jogo) {
        if (jogo == null) {
            return null;
        }

        try {
            return LocalDate.parse(jogo.getData());
        } catch (DateTimeParseException e) {
            return null;
        }
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

    private String valorOuTraco(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    private String abreviarLabel(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return "-";
        }

        String limpo = valor.trim();
        return limpo.length() <= 14 ? limpo : limpo.substring(0, 12) + "..";
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
        private final LocalDate data;
        private final String campeonato;
        private final int bilhetes;
        private final double bilheteira;
        private final double patrocinio;
        private final double direitosTv;

        private ReceitaJogo(
                String jogo,
                LocalDate data,
                String campeonato,
                int bilhetes,
                double bilheteira,
                double patrocinio,
                double direitosTv
        ) {
            this.jogo = jogo;
            this.data = data;
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

    private static class PontoGrafico {
        private final String label;
        private final double valor;

        private PontoGrafico(String label, double valor) {
            this.label = label;
            this.valor = valor;
        }

        private double getValor() {
            return valor;
        }
    }

    private static class ResumoFinanceiro {
        private int totalBilhetes;
        private double totalPatrocinios;
        private double totalLucro;
        private double mediaLucroPorJogo;
    }

    private static class ResumoBilheteira {
        private int bilhetes;
        private double total;
    }

    private enum TipoGraficoLucro {
        MES("Lucro por Mes"),
        CAMPEONATO("Lucro por Campeonato"),
        JOGOS_RECENTES("Lucro Jogos Recentes");

        private final String titulo;

        TipoGraficoLucro(String titulo) {
            this.titulo = titulo;
        }

        @Override
        public String toString() {
            return titulo;
        }
    }

    private class GraficoLucro extends JPanel {
        private static final int PADDING_ESQUERDA = 58;
        private static final int PADDING_DIREITA = 18;
        private static final int PADDING_TOPO = 16;
        private static final int PADDING_BAIXO = 42;

        private List<PontoGrafico> pontos;

        private GraficoLucro(List<PontoGrafico> pontos) {
            this.pontos = pontos;
            setOpaque(true);
            setBackground(Tema.COR_CARD);
            setBorder(BorderFactory.createLineBorder(Tema.COR_LINHA));
        }

        private void atualizarDados(List<PontoGrafico> pontos) {
            this.pontos = pontos;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setFont(Tema.FONTE_TEXTO_PEQUENO);

            int largura = getWidth();
            int altura = getHeight();
            int baseY = altura - PADDING_BAIXO;
            int topoY = PADDING_TOPO;
            int graficoLargura = largura - PADDING_ESQUERDA - PADDING_DIREITA;
            int graficoAltura = baseY - topoY;

            if (pontos.isEmpty() || graficoLargura <= 0 || graficoAltura <= 0) {
                desenharMensagemSemDados(g2, largura, altura);
                g2.dispose();
                return;
            }

            double maiorLucro = 0;
            for (PontoGrafico ponto : pontos) {
                maiorLucro = Math.max(maiorLucro, ponto.valor);
            }

            if (maiorLucro <= 0) {
                desenharMensagemSemDados(g2, largura, altura);
                g2.dispose();
                return;
            }

            desenharEixos(g2, baseY, topoY, graficoLargura);
            desenharEscala(g2, baseY, topoY, maiorLucro);
            desenharBarras(g2, baseY, graficoLargura, graficoAltura, maiorLucro);

            g2.dispose();
        }

        private void desenharMensagemSemDados(Graphics2D g2, int largura, int altura) {
            String mensagem = "Sem dados financeiros por mes";
            FontMetrics metrics = g2.getFontMetrics();

            g2.setColor(Tema.COR_TEXTO_SECUNDARIO);
            g2.drawString(
                    mensagem,
                    (largura - metrics.stringWidth(mensagem)) / 2,
                    altura / 2
            );
        }

        private void desenharEixos(Graphics2D g2, int baseY, int topoY, int graficoLargura) {
            g2.setColor(Tema.COR_LINHA);
            g2.drawLine(PADDING_ESQUERDA, baseY, PADDING_ESQUERDA + graficoLargura, baseY);
            g2.drawLine(PADDING_ESQUERDA, topoY, PADDING_ESQUERDA, baseY);
        }

        private void desenharEscala(Graphics2D g2, int baseY, int topoY, double maiorLucro) {
            int linhas = 3;
            FontMetrics metrics = g2.getFontMetrics();

            for (int i = 0; i <= linhas; i++) {
                double valor = maiorLucro * i / linhas;
                int y = baseY - (int) ((baseY - topoY) * i / (double) linhas);

                g2.setColor(new Color(241, 245, 249));
                g2.drawLine(PADDING_ESQUERDA, y, getWidth() - PADDING_DIREITA, y);

                String label = formatarValorCurto(valor);
                g2.setColor(Tema.COR_TEXTO_SECUNDARIO);
                g2.drawString(label, PADDING_ESQUERDA - metrics.stringWidth(label) - 8, y + 4);
            }
        }

        private void desenharBarras(
                Graphics2D g2,
                int baseY,
                int graficoLargura,
                int graficoAltura,
                double maiorLucro
        ) {
            int quantidade = pontos.size();
            int espacoPorMes = graficoLargura / quantidade;
            int larguraBarra = Math.max(24, Math.min(54, espacoPorMes / 2));

            for (int i = 0; i < quantidade; i++) {
                PontoGrafico ponto = pontos.get(i);
                int centroX = PADDING_ESQUERDA + (espacoPorMes * i) + (espacoPorMes / 2);
                int alturaBarra = (int) Math.round((ponto.valor / maiorLucro) * (graficoAltura - 12));
                int x = centroX - (larguraBarra / 2);
                int y = baseY - alturaBarra;

                g2.setColor(Tema.CARD_TEXTO_AZUL);
                g2.fillRoundRect(x, y, larguraBarra, alturaBarra, 10, 10);

                g2.setColor(Tema.CARD_AZUL);
                g2.drawRoundRect(x, y, larguraBarra, alturaBarra, 10, 10);

                desenharLabelPonto(g2, ponto, centroX, baseY);
            }
        }

        private void desenharLabelPonto(Graphics2D g2, PontoGrafico ponto, int centroX, int baseY) {
            String labelValor = formatarValorCurto(ponto.valor);
            FontMetrics metrics = g2.getFontMetrics();

            g2.setColor(Tema.COR_TEXTO_PRINCIPAL);
            g2.drawString(ponto.label, centroX - metrics.stringWidth(ponto.label) / 2, baseY + 18);

            g2.setColor(Tema.COR_TEXTO_SECUNDARIO);
            g2.drawString(labelValor, centroX - metrics.stringWidth(labelValor) / 2, baseY + 34);
        }

        private String formatarValorCurto(double valor) {
            if (valor >= 1_000_000) {
                return String.format(Locale.GERMANY, "%.1fM", valor / 1_000_000);
            }

            if (valor >= 1_000) {
                return String.format(Locale.GERMANY, "%.0fk", valor / 1_000);
            }

            return String.format(Locale.GERMANY, "%.0f", valor);
        }
    }
}
