package Frames.SeccaoJogos;

import Design.FocusUtils;
import Design.MenuLateral;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Frames.DetalheJogoFrame;
import Models.CampeonatoRepositorio;
import Models.Campeonato;
import Models.Jogo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class JogosFrame extends JFrame {

    private JTable tabelaProximos;
    private JTable tabelaRecentes;

    private final List<Jogo> jogosProximosVisiveis = new ArrayList<>();
    private final List<Jogo> jogosRecentesVisiveis = new ArrayList<>();

    public JogosFrame() {
        setTitle("Jogos");
        setSize(1920, 1080);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        add(menuLateral, BorderLayout.WEST);
        add(criarPagina(menuLateral), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarPagina(JPanel menuLateral) {
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(Tema.COR_FUNDO);
        pagina.setBorder(new EmptyBorder(
                Tema.PADDING_JANELA.top,
                Tema.PADDING_JANELA.left,
                Tema.PADDING_JANELA.bottom,
                Tema.PADDING_JANELA.right
        ));

        FocusUtils.limparFocoAoClicar(pagina);

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);

        JButton botaoMenu = criarBotaoMenu(menuLateral);
        barraSuperior.add(botaoMenu, BorderLayout.WEST);

        pagina.add(barraSuperior, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(15, 60, 20, 60));

        centro.add(criarCabecalho());
        centro.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO));
        centro.add(criarCardsResumo());
        centro.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO));
        centro.add(criarCardProximosJogos());
        centro.add(Box.createVerticalStrut(Tema.ESPACAMENTO_MEDIO));
        centro.add(criarCardJogosRecentes());

        JScrollPane scrollPrincipal = new JScrollPane(centro);
        TableStyle.configurarScrollLimpo(scrollPrincipal, Tema.COR_FUNDO);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(16);

        pagina.add(scrollPrincipal, BorderLayout.CENTER);

        carregarTabelas();

        return pagina;
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Jogos");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel(
                "Cria jogos, regista resultados e acompanha o calendário dos campeonatos."
        );
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        JButton btnResultado = new RoundedButton(
                "Registar Resultado",
                Tema.COR_SUCESSO,
                Tema.COR_TEXTO_CLARO,
                12
        );
        btnResultado.addActionListener(e -> registarResultadoSelecionado());

        JButton btnEliminar = new RoundedButton(
                "Eliminar Jogo",
                Tema.COR_ERRO,
                Tema.COR_TEXTO_CLARO,
                12
        );
        btnEliminar.addActionListener(e -> eliminarJogoSelecionado());

        JButton btnNovo = new RoundedButton(
                "+ Criar Jogo",
                Tema.COR_INFO,
                Tema.COR_TEXTO_CLARO,
                12
        );
        btnNovo.addActionListener(e -> abrirNovoJogo());

        botoes.add(btnResultado);
        botoes.add(btnEliminar);
        botoes.add(btnNovo);

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(botoes, BorderLayout.EAST);

        return cabecalho;
    }

    private void abrirNovoJogo() {
        if (CampeonatoRepositorio.listar().isEmpty()) {
            mostrarErro("Ainda não existe nenhum campeonato criado.");
            return;
        }

        new NovoJogoFrame(this::atualizarPagina);
    }

    private JPanel criarCardsResumo() {
        List<Jogo> todosJogos = obterTodosJogos();

        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, Tema.ALTURA_CARD_RESUMO));
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);

        cards.add(criarCartaoResumo(
                "Total de Jogos",
                String.valueOf(todosJogos.size()),
                Tema.CARD_AZUL,
                Tema.CARD_TEXTO_AZUL
        ));

        cards.add(criarCartaoResumo(
                "Próximos Jogos",
                String.valueOf(obterProximosJogos(todosJogos).size()),
                Tema.CARD_VERDE,
                Tema.CARD_TEXTO_VERDE
        ));

        cards.add(criarCartaoResumo(
                "Jogos Realizados",
                String.valueOf(contarJogosRealizados(todosJogos)),
                Tema.CARD_AMARELO,
                Tema.CARD_TEXTO_LARANJA
        ));

        return cards;
    }

    private RoundedPanel criarCartaoResumo(
            String titulo,
            String valor,
            Color corFundo,
            Color corTitulo
    ) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, corFundo);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(
                Tema.PADDING_CARD_RESUMO.top,
                Tema.PADDING_CARD_RESUMO.left,
                Tema.PADDING_CARD_RESUMO.bottom,
                Tema.PADDING_CARD_RESUMO.right
        ));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(Tema.FONTE_CARD_TITULO);
        labelTitulo.setForeground(corTitulo);

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(Tema.FONTE_CARD_VALOR_GRANDE);
        labelValor.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        card.add(labelTitulo, BorderLayout.NORTH);
        card.add(labelValor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardProximosJogos() {
        return criarCardTabela("Próximos Jogos", true);
    }

    private JPanel criarCardJogosRecentes() {
        return criarCardTabela("Jogos Recentes e Resultados", false);
    }

    private JPanel criarCardTabela(String tituloTexto, boolean proximos) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));
        card.setPreferredSize(new Dimension(1000, 285));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(Tema.FONTE_TABELA_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        titulo.setBorder(new EmptyBorder(0, 0, Tema.ESPACAMENTO_PEQUENO, 0));

        JTable tabela = criarTabela();

        if (proximos) {
            tabelaProximos = tabela;
        } else {
            tabelaRecentes = tabela;
        }

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabela.rowAtPoint(e.getPoint());

                if (linha < 0) {
                    return;
                }

                tabela.setRowSelectionInterval(linha, linha);

                if (proximos && tabelaRecentes != null) {
                    tabelaRecentes.clearSelection();
                } else if (!proximos && tabelaProximos != null) {
                    tabelaProximos.clearSelection();
                }

                if (e.getClickCount() == 2) {
                    abrirDetalheJogo(tabela, proximos);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JTable criarTabela() {
        String[] colunas = {
                "Data", "Hora", "Equipa A", "Equipa B", "Estádio",
                "Fase", "Estado", "Resultado", "Campeonato"
        };

        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(modelo);
        TableStyle.aplicarTabelaLimpa(tabela, 2);
        tabela.setRowHeight(34);

        return tabela;
    }

    private void carregarTabelas() {
        List<Jogo> todosJogos = obterTodosJogos();

        jogosProximosVisiveis.clear();
        jogosProximosVisiveis.addAll(obterProximosJogos(todosJogos));

        jogosRecentesVisiveis.clear();
        jogosRecentesVisiveis.addAll(obterJogosRecentes(todosJogos));

        preencherTabela(tabelaProximos, jogosProximosVisiveis);
        preencherTabela(tabelaRecentes, jogosRecentesVisiveis);
    }

    private List<Jogo> obterTodosJogos() {
        List<Jogo> jogos = new ArrayList<>();

        for (Campeonato campeonato : CampeonatoRepositorio.listar()) {
            if (campeonato != null && campeonato.getJogos() != null) {
                jogos.addAll(campeonato.getJogos());
            }
        }

        return jogos;
    }

    private List<Jogo> obterProximosJogos(List<Jogo> todosJogos) {
        List<Jogo> proximos = new ArrayList<>();

        for (Jogo jogo : todosJogos) {
            /*
             * Só jogos realmente agendados são próximos.
             * Jogos "Encerrado", "Cancelado", "Realizado" e "Finalizado"
             * deixam de aparecer nesta tabela.
             */
            if (jogoEstaPendente(jogo)) {
                proximos.add(jogo);
            }
        }

        proximos.sort(
                Comparator.comparing(
                        (Jogo jogo) -> converterData(jogo.getData()),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).thenComparing(
                        Jogo::getHora,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                )
        );

        return proximos;
    }

    private List<Jogo> obterJogosRecentes(List<Jogo> todosJogos) {
        List<Jogo> recentes = new ArrayList<>();

        for (Jogo jogo : todosJogos) {
            /*
             * Os jogos fechados sem resultado também ficam no histórico,
             * com o estado "Encerrado", mas nunca em Próximos Jogos.
             */
            if (!jogoEstaPendente(jogo)) {
                recentes.add(jogo);
            }
        }

        recentes.sort(
                Comparator.comparing(
                        (Jogo jogo) -> converterData(jogo.getData()),
                        Comparator.nullsLast(Comparator.reverseOrder())
                ).thenComparing(
                        Jogo::getHora,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
        );

        return recentes;
    }

    private int contarJogosRealizados(List<Jogo> jogos) {
        int total = 0;

        for (Jogo jogo : jogos) {
            if (jogoEstaRealizado(jogo)) {
                total++;
            }
        }

        return total;
    }

    private boolean jogoEstaPendente(Jogo jogo) {
        if (jogo == null || jogo.getEstado() == null) {
            return false;
        }

        return jogo.getEstado().equalsIgnoreCase("Agendado");
    }

    private boolean jogoEstaRealizado(Jogo jogo) {
        if (jogo == null || jogo.getEstado() == null) {
            return false;
        }

        return jogo.getEstado().equalsIgnoreCase("Realizado")
                || jogo.getEstado().equalsIgnoreCase("Finalizado");
    }

    private void preencherTabela(JTable tabela, List<Jogo> jogos) {
        if (tabela == null) {
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
        modelo.setRowCount(0);

        for (Jogo jogo : jogos) {
            modelo.addRow(new Object[]{
                    formatarData(jogo.getData()),
                    valorOuTraco(jogo.getHora()),
                    valorOuTraco(jogo.getEquipaA()),
                    valorOuTraco(jogo.getEquipaB()),
                    valorOuTraco(jogo.getEstadio()),
                    valorOuTraco(jogo.getFaseGrupo()),
                    valorOuTraco(jogo.getEstado()),
                    valorOuTraco(jogo.getResultado()),
                    valorOuTraco(jogo.getCampeonato())
            });
        }
    }

    private void registarResultadoSelecionado() {
        Jogo jogo = obterJogoSelecionado();

        if (jogo == null) {
            mostrarErro("Seleciona um jogo em Próximos Jogos.");
            return;
        }

        String bloqueio = CampeonatoRepositorio.motivoBloqueioRegistoResultado(jogo);

        if (!bloqueio.isEmpty()) {
            mostrarErro(bloqueio);
            return;
        }

        JSpinner golosA = criarSpinnerGolos();
        JSpinner golosB = criarSpinnerGolos();

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(new EmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;

        JLabel titulo = new JLabel(
                jogo.getEquipaA() + "  vs  " + jogo.getEquipaB()
        );
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        painel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        painel.add(new JLabel(jogo.getEquipaA()), gbc);

        gbc.gridx = 1;
        painel.add(new JLabel("Resultado"), gbc);

        gbc.gridx = 2;
        painel.add(new JLabel(jogo.getEquipaB()), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        painel.add(golosA, gbc);

        gbc.gridx = 1;
        JLabel separador = new JLabel("-");
        separador.setHorizontalAlignment(SwingConstants.CENTER);
        separador.setFont(Tema.FONTE_TITULO);
        painel.add(separador, gbc);

        gbc.gridx = 2;
        painel.add(golosB, gbc);

        JLabel aviso = new JLabel(
                ehJogoEliminatorio(jogo)
                        ? "Nas eliminatórias, o resultado não pode terminar empatado."
                        : "O resultado será guardado como Realizado."
        );
        aviso.setFont(Tema.FONTE_TEXTO_PEQUENO);
        aviso.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        painel.add(aviso, gbc);

        int resposta = JOptionPane.showConfirmDialog(
                this,
                painel,
                "Registar Resultado",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resposta != JOptionPane.OK_OPTION) {
            return;
        }

        int resultadoA = (Integer) golosA.getValue();
        int resultadoB = (Integer) golosB.getValue();

        if (ehJogoEliminatorio(jogo) && resultadoA == resultadoB) {
            mostrarErro(
                    "Nas eliminatórias não pode haver empate. "
                            + "Indica o resultado final depois de prolongamento ou penáltis."
            );
            return;
        }

        boolean atualizado = CampeonatoRepositorio.registarResultadoJogo(
                jogo.getId(),
                resultadoA,
                resultadoB
        );

        if (!atualizado) {
            mostrarErro("Não foi possível guardar o resultado deste jogo.");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Resultado guardado com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        reabrirJogosFrame();
    }

    private void eliminarJogoSelecionado() {
        Jogo jogo = obterJogoSelecionado();

        if (jogo == null) {
            mostrarErro("Seleciona um jogo em Próximos Jogos.");
            return;
        }

        String bloqueio = CampeonatoRepositorio.motivoBloqueioEliminacaoJogo(jogo);

        if (!bloqueio.isEmpty()) {
            mostrarErro(bloqueio);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Queres mesmo eliminar o jogo:\n\n"
                        + jogo.getEquipaA() + " vs " + jogo.getEquipaB()
                        + "\n"
                        + formatarData(jogo.getData()) + " às " + jogo.getHora() + "?",
                "Eliminar Jogo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        boolean eliminado = CampeonatoRepositorio.eliminarJogoNaoRealizado(jogo.getId());

        if (!eliminado) {
            mostrarErro("Não foi possível eliminar o jogo.");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Jogo eliminado com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        reabrirJogosFrame();
    }

    private Jogo obterJogoSelecionado() {
        if (tabelaProximos != null && tabelaProximos.getSelectedRow() >= 0) {
            int linha = tabelaProximos.convertRowIndexToModel(tabelaProximos.getSelectedRow());

            if (linha >= 0 && linha < jogosProximosVisiveis.size()) {
                return jogosProximosVisiveis.get(linha);
            }
        }

        if (tabelaRecentes != null && tabelaRecentes.getSelectedRow() >= 0) {
            int linha = tabelaRecentes.convertRowIndexToModel(tabelaRecentes.getSelectedRow());

            if (linha >= 0 && linha < jogosRecentesVisiveis.size()) {
                return jogosRecentesVisiveis.get(linha);
            }
        }

        return null;
    }

    private JSpinner criarSpinnerGolos() {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        spinner.setFont(Tema.FONTE_TEXTO);
        spinner.setPreferredSize(new Dimension(90, 34));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField()
                    .setHorizontalAlignment(SwingConstants.CENTER);
        }

        return spinner;
    }

    private boolean ehJogoEliminatorio(Jogo jogo) {
        if (jogo == null || jogo.getFaseGrupo() == null) {
            return false;
        }

        String fase = jogo.getFaseGrupo().toLowerCase();

        return fase.startsWith("elim:")
                || fase.contains("quartos")
                || fase.contains("meias")
                || fase.contains("semi")
                || fase.equals("final")
                || fase.endsWith(" final")
                || fase.contains("oitavos");
    }

    private void reabrirJogosFrame() {
        atualizarPagina();
    }

    private void abrirDetalheJogo(JTable tabela, boolean proximos) {
        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada < 0) {
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linhaSelecionada);
        List<Jogo> jogos = proximos ? jogosProximosVisiveis : jogosRecentesVisiveis;

        if (linhaModelo < 0 || linhaModelo >= jogos.size()) {
            return;
        }

        new DetalheJogoFrame(jogos.get(linhaModelo), () -> SwingUtilities.invokeLater(this::atualizarPagina));
    }

    private void atualizarPagina() {
        getContentPane().removeAll();

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        add(menuLateral, BorderLayout.WEST);
        add(criarPagina(menuLateral), BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private LocalDate converterData(String dataTexto) {
        if (dataTexto == null || dataTexto.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dataTexto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String formatarData(String dataTexto) {
        LocalDate data = converterData(dataTexto);

        if (data == null) {
            return valorOuTraco(dataTexto);
        }

        String texto = data.format(
                DateTimeFormatter.ofPattern("dd MMM", new Locale("pt", "PT"))
        );

        return texto.isEmpty()
                ? texto
                : texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private String valorOuTraco(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }

    private JButton criarBotaoMenu(JPanel menuLateral) {
        JButton botaoMenu = new JButton("=");
        botaoMenu.setFont(Tema.FONTE_BOTAO_MENU);
        botaoMenu.setFocusPainted(false);
        botaoMenu.setBorderPainted(false);
        botaoMenu.setContentAreaFilled(false);
        botaoMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        botaoMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botaoMenu.addActionListener(e -> {
            menuLateral.setVisible(!menuLateral.isVisible());
            revalidate();
            repaint();
        });

        return botaoMenu;
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
