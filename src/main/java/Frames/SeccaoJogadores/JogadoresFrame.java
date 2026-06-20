package Frames.SeccaoJogadores;

import Design.MenuLateral;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Models.Jogadores.Jogador;
import Models.Jogadores.JogadorService;
import Models.Equipas.EquipaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class JogadoresFrame extends JFrame {

    private static final String TODAS_EQUIPAS = "Todas as Equipas";
    private static final String TODAS_POSICOES = "Todas as Posições";
    private static final String TODOS_ESTADOS = "Todos os Estados";

    private final JogadorService jogadorService;
    private final List<Jogador> jogadoresFiltrados;

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    private JTable tabela;
    private DefaultTableModel modelo;

    private JComboBox<String> filtroEquipas;
    private JComboBox<String> filtroPosicoes;
    private JComboBox<String> filtroEstados;

    public JogadoresFrame() {
        jogadorService = JogadorService.getInstance();
        jogadoresFiltrados = new ArrayList<>();

        setTitle("Jogadores");
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
        main.add(criarConteudo(main), BorderLayout.CENTER);

        atualizarFiltros();
        atualizarTabela();

        setVisible(true);
    }

    private JPanel criarConteudo(JPanel main) {
        JPanel content = new JPanel(new BorderLayout(0, Tema.ESPACAMENTO_GRANDE));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 25, 0, 25));
        limparSelecaoAoClicar(content);

        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
        cabecalho.setOpaque(false);

        cabecalho.add(criarTopo(main));
        cabecalho.add(Box.createVerticalStrut(14));
        cabecalho.add(criarFiltros());

        content.add(cabecalho, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(Tema.ESPACAMENTO_MEDIO, 0));
        centro.setOpaque(false);
        limparSelecaoAoClicar(centro);

        centro.add(criarCardTabela(), BorderLayout.CENTER);
        centro.add(criarRegraCard(), BorderLayout.EAST);

        content.add(centro, BorderLayout.CENTER);

        return content;
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

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Jogadores");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel sub = new JLabel("Consulta, perfil e gestão do estado dos jogadores do campeonato.");
        sub.setFont(Tema.FONTE_SUBTITULO);
        sub.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(sub);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        esquerda.setOpaque(false);
        esquerda.add(btnMenu);
        esquerda.add(textos);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setOpaque(false);

        botoes.add(criarBotaoAcao(
                "Ver Perfil",
                Tema.COR_INFO,
                Tema.COR_TEXTO_CLARO,
                this::abrirSelecionado
        ));

        botoes.add(criarBotaoAcao(
                "Ativar / Inativar",
                Tema.COR_ERRO,
                Tema.COR_TEXTO_CLARO,
                this::alternarSelecionado
        ));

        topo.add(esquerda, BorderLayout.WEST);
        topo.add(botoes, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarFiltros() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        painel.setOpaque(false);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        limparSelecaoAoClicar(painel);

        filtroEquipas = criarComboFiltro();
        filtroPosicoes = criarComboFiltro();
        filtroEstados = criarComboFiltro();

        filtroEquipas.addActionListener(e -> atualizarTabela());
        filtroPosicoes.addActionListener(e -> atualizarTabela());
        filtroEstados.addActionListener(e -> atualizarTabela());

        painel.add(filtroEquipas);
        painel.add(filtroPosicoes);
        painel.add(filtroEstados);

        return painel;
    }

    private JComboBox<String> criarComboFiltro() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setPreferredSize(new Dimension(190, 36));
        combo.setFont(Tema.FONTE_TEXTO);
        combo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        combo.setBackground(Tema.COR_CARD);
        combo.setFocusable(false);
        combo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        combo.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return combo;
    }

    private void atualizarFiltros() {
        String equipaSelecionada = valorSelecionado(filtroEquipas, TODAS_EQUIPAS);
        String posicaoSelecionada = valorSelecionado(filtroPosicoes, TODAS_POSICOES);
        String estadoSelecionado = valorSelecionado(filtroEstados, TODOS_ESTADOS);

        preencherCombo(filtroEquipas, TODAS_EQUIPAS, listarEquipas(), equipaSelecionada);
        preencherCombo(filtroPosicoes, TODAS_POSICOES, listarPosicoes(), posicaoSelecionada);

        List<String> estados = new ArrayList<>();
        estados.add("Ativo");
        estados.add("Inativo");

        preencherCombo(filtroEstados, TODOS_ESTADOS, estados, estadoSelecionado);
    }

    private void preencherCombo(JComboBox<String> combo,
                                String primeiroItem,
                                List<String> opcoes,
                                String valorSelecionado) {
        combo.removeAllItems();
        combo.addItem(primeiroItem);

        for (String opcao : opcoes) {
            if (opcao != null && !opcao.trim().isEmpty()) {
                combo.addItem(opcao);
            }
        }

        if (valorSelecionado != null) {
            combo.setSelectedItem(valorSelecionado);
        }

        if (combo.getSelectedItem() == null) {
            combo.setSelectedItem(primeiroItem);
        }
    }

    private List<String> listarEquipas() {
        Set<String> equipas = new LinkedHashSet<>();

        for (Jogador jogador : jogadorService.listarJogadores()) {
            equipas.add(jogador.getEquipa());
        }

        return new ArrayList<>(equipas);
    }

    private List<String> listarPosicoes() {
        Set<String> posicoes = new LinkedHashSet<>();

        for (Jogador jogador : jogadorService.listarJogadores()) {
            posicoes.add(jogador.getPosicao());
        }

        return new ArrayList<>(posicoes);
    }

    private String valorSelecionado(JComboBox<String> combo, String valorDefault) {
        if (combo == null || combo.getSelectedItem() == null) {
            return valorDefault;
        }

        return String.valueOf(combo.getSelectedItem());
    }

    private JButton criarBotaoAcao(String texto, Color fundo, Color corTexto, Runnable acao) {
        RoundedButton botao = new RoundedButton(texto, fundo, corTexto, 12);
        botao.setPreferredSize(new Dimension(150, 40));

        botao.addActionListener(e -> {
            if (acao != null) {
                acao.run();
            }
        });

        return botao;
    }

    private JPanel criarCardTabela() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));

        limparSelecaoAoClicar(card);

        JLabel titulo = new JLabel("Lista de Jogadores");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {
                "Nome", "Equipa", "Posição", "Nº", "Idade",
                "Golos", "Assist.", "Cartões", "Estado"
        };

        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        TableStyle.aplicarTabelaLimpa(tabela, 0);
        tabela.setRowHeight(38);
        tabela.setAutoCreateRowSorter(true);

        tabela.getColumnModel().getColumn(1).setCellRenderer(TableStyle.rendererEsquerda());
        tabela.getColumnModel().getColumn(8).setCellRenderer(criarRendererEstado());

        tabela.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(45);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(8).setPreferredWidth(90);

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabela.rowAtPoint(e.getPoint());

                if (e.getClickCount() == 2 && linha != -1) {
                    tabela.setRowSelectionInterval(linha, linha);
                    abrirSelecionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private DefaultTableCellRenderer criarRendererEstado() {
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

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                label.setFont(Tema.FONTE_CARD_TITULO);
                label.setBorder(new EmptyBorder(0, 8, 0, 8));
                label.setOpaque(true);

                if (isSelected) {
                    label.setBackground(Tema.COR_SELECAO_NEUTRA);
                    label.setForeground(Tema.COR_TEXTO_PRINCIPAL);
                } else {
                    label.setBackground(Tema.COR_CARD);

                    if ("Ativo".equals(String.valueOf(value))) {
                        label.setForeground(Tema.COR_SUCESSO);
                    } else {
                        label.setForeground(Tema.COR_ERRO);
                    }
                }

                return label;
            }
        };
    }

    private JPanel criarRegraCard() {
        RoundedPanel side = new RoundedPanel(Tema.RAIO_CARD, Tema.CARD_AMARELO);
        side.setPreferredSize(new Dimension(285, 0));
        side.setLayout(new BorderLayout());
        side.setBorder(new EmptyBorder(24, 24, 24, 24));

        limparSelecaoAoClicar(side);

        JLabel titulo = new JLabel("Regra dos jogadores");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(Tema.CARD_TEXTO_LARANJA);

        JTextArea texto = new JTextArea(
                "Os jogadores são carregados do ficheiro data/jogadores.tsv.\n\n" +
                        "Usa os filtros para listar por equipa, posição ou estado.\n\n" +
                        "Ao editar, ativar ou inativar, os dados ficam guardados."
        );

        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setOpaque(false);
        texto.setFont(Tema.FONTE_TEXTO);
        texto.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        side.add(titulo, BorderLayout.NORTH);
        side.add(texto, BorderLayout.CENTER);

        return side;
    }

    private void atualizarTabela() {
        if (modelo == null) {
            return;
        }

        modelo.setRowCount(0);
        jogadoresFiltrados.clear();

        String equipaSelecionada = valorSelecionado(filtroEquipas, TODAS_EQUIPAS);
        String posicaoSelecionada = valorSelecionado(filtroPosicoes, TODAS_POSICOES);
        String estadoSelecionado = valorSelecionado(filtroEstados, TODOS_ESTADOS);

        for (Jogador j : jogadorService.listarJogadores()) {
            if (!TODAS_EQUIPAS.equals(equipaSelecionada)
                    && !equipaSelecionada.equals(j.getEquipa())) {
                continue;
            }

            if (!TODAS_POSICOES.equals(posicaoSelecionada)
                    && !posicaoSelecionada.equals(j.getPosicao())) {
                continue;
            }

            if (!TODOS_ESTADOS.equals(estadoSelecionado)
                    && !estadoSelecionado.equals(j.getEstadoTexto())) {
                continue;
            }

            jogadoresFiltrados.add(j);

            modelo.addRow(new Object[]{
                    j.getNome(),
                    j.getEquipa(),
                    j.getPosicao(),
                    j.getNumero(),
                    j.getIdade(),
                    j.getGolos(),
                    j.getAssistencias(),
                    j.getCartoes(),
                    j.getEstadoTexto()
            });
        }
    }

    private Jogador jogadorSelecionado() {
        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Seleciona primeiro um jogador.");
            return null;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linha);

        if (linhaModelo < 0 || linhaModelo >= jogadoresFiltrados.size()) {
            JOptionPane.showMessageDialog(this, "Não foi possível encontrar o jogador selecionado.");
            return null;
        }

        return jogadoresFiltrados.get(linhaModelo);
    }

    private void abrirSelecionado() {
        Jogador jogador = jogadorSelecionado();

        if (jogador != null) {
            new PerfilJogadorFrame(jogador, this::guardarEAtualizar);
        }
    }

    private void alternarSelecionado() {
        Jogador jogador = jogadorSelecionado();

        if (jogador == null) {
            return;
        }

        jogador.alternarEstado();
        guardarEAtualizar();
    }

    private void guardarEAtualizar() {
        jogadorService.guardarJogadores();
        EquipaService.getInstance().sincronizarEstatisticasComJogadores();
        atualizarFiltros();
        atualizarTabela();
    }

    private void limparSelecaoTabelas() {
        if (tabela != null) {
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
