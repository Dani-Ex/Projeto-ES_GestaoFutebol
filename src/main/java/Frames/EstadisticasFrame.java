package Frames;

import Design.MenuLateral;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Frames.SeccaoJogadores.PerfilJogadorFrame;
import Models.Jogador;
import Models.JogadorService;
import Models.Jogo;
import Models.JogoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EstadisticasFrame extends JFrame {

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    private final List<JTable> tabelas = new ArrayList<>();
    private final List<Jogador> jogadoresVisiveis = new ArrayList<>();
    private final List<Jogo> jogosVisiveis = new ArrayList<>();

    private final JogadorService jogadorService = JogadorService.getInstance();
    private final JogoService jogoService = JogoService.getInstance();

    private DefaultTableModel modeloJogadores;
    private DefaultTableModel modeloJogos;

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

        atualizarTabelas();
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

        JLabel subtitulo = new JLabel(
                "Dados registados do campeonato, por jogador e por jogo. Clica numa linha para ver o detalhe."
        );
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

        List<Jogador> jogadores = jogadorService.listarJogadores();
        List<Jogo> jogos = jogoService.listarJogos();

        int golos = jogos.stream().mapToInt(this::totalGolosDoJogo).sum();
        int assistencias = jogadores.stream().mapToInt(Jogador::getAssistencias).sum();
        int cartoes = jogadores.stream().mapToInt(Jogador::getCartoes).sum();

        int jogosRealizados = (int) jogos.stream()
                .filter(jogo -> "Realizado".equalsIgnoreCase(jogo.getEstado())
                        || "Finalizado".equalsIgnoreCase(jogo.getEstado()))
                .count();

        int jogosAgendados = (int) jogos.stream()
                .filter(jogo -> "Agendado".equalsIgnoreCase(jogo.getEstado()))
                .count();

        int jogadoresAtivos = (int) jogadores.stream()
                .filter(Jogador::isAtivo)
                .count();

        painel.add(criarCardResumo(
                "Total de Golos",
                String.valueOf(golos),
                Tema.CARD_AZUL,
                Tema.CARD_TEXTO_AZUL
        ));

        painel.add(criarCardResumo(
                "Assistências",
                String.valueOf(assistencias),
                Tema.CARD_VERDE,
                Tema.CARD_TEXTO_VERDE
        ));

        painel.add(criarCardResumo(
                "Cartões",
                String.valueOf(cartoes),
                Tema.COR_LARANJA_SUAVE,
                Tema.CARD_TEXTO_LARANJA
        ));

        painel.add(criarCardResumo(
                "Jogos Realizados",
                String.valueOf(jogosRealizados),
                Tema.CARD_ROXO,
                Tema.CARD_TEXTO_ROXO
        ));

        painel.add(criarCardResumo(
                "Jogos Agendados",
                String.valueOf(jogosAgendados),
                Tema.COR_ERRO_SUAVE,
                Tema.COR_ERRO
        ));

        painel.add(criarCardResumo(
                "Jogadores Ativos",
                String.valueOf(jogadoresAtivos),
                Tema.COR_BOTAO_SECUNDARIO,
                Tema.COR_TEXTO_PRINCIPAL
        ));

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

        String[] colunas = {
                "Jogador",
                "Equipa",
                "Golos",
                "Assist.",
                "Cartões",
                "Estado"
        };

        modeloJogadores = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = criarTabela(modeloJogadores, 0);

        tabela.getColumnModel()
                .getColumn(1)
                .setCellRenderer(TableStyle.rendererEsquerda());

        tabela.setToolTipText("Clica num jogador para abrir o seu perfil.");

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabela.rowAtPoint(e.getPoint());

                if (linha >= 0) {
                    tabela.setRowSelectionInterval(linha, linha);
                    abrirPerfilJogador(tabela);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);
        scroll.setBorder(new EmptyBorder(16, 0, 0, 0));

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardTabelaJogo() {
        RoundedPanel card = criarCardTabela("Estatísticas por Jogo");

        String[] colunas = {
                "Jogo",
                "Resultado",
                "Golos",
                "Data",
                "Estádio",
                "Fase",
                "Estado"
        };

        modeloJogos = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = criarTabela(modeloJogos, 0);
        tabela.setToolTipText("Clica num jogo para ver todos os dados registados.");

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabela.rowAtPoint(e.getPoint());

                if (linha >= 0) {
                    tabela.setRowSelectionInterval(linha, linha);
                    abrirDetalheJogo(tabela);
                }
            }
        });

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

    private JTable criarTabela(DefaultTableModel modelo, int colunaEsquerda) {
        JTable tabela = new JTable(modelo);

        TableStyle.aplicarTabelaLimpa(tabela, colunaEsquerda);

        tabela.setRowHeight(38);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabela.setDefaultRenderer(Object.class, criarRendererPadrao(colunaEsquerda));

        tabelas.add(tabela);

        return tabela;
    }

    private void atualizarTabelas() {
        preencherTabelaJogadores();
        preencherTabelaJogos();
    }

    private void preencherTabelaJogadores() {
        if (modeloJogadores == null) {
            return;
        }

        modeloJogadores.setRowCount(0);
        jogadoresVisiveis.clear();

        List<Jogador> jogadores = new ArrayList<>(jogadorService.listarJogadores());

        jogadores.sort(
                Comparator.comparingInt(Jogador::getGolos).reversed()
                        .thenComparing(
                                Comparator.comparingInt(Jogador::getAssistencias).reversed()
                        )
                        .thenComparing(
                                Jogador::getNome,
                                String.CASE_INSENSITIVE_ORDER
                        )
        );

        for (Jogador jogador : jogadores) {
            jogadoresVisiveis.add(jogador);

            modeloJogadores.addRow(new Object[]{
                    jogador.getNome(),
                    jogador.getEquipa(),
                    jogador.getGolos(),
                    jogador.getAssistencias(),
                    jogador.getCartoes(),
                    jogador.getEstadoTexto()
            });
        }
    }

    private void preencherTabelaJogos() {
        if (modeloJogos == null) {
            return;
        }

        modeloJogos.setRowCount(0);
        jogosVisiveis.clear();

        List<Jogo> jogos = new ArrayList<>(jogoService.listarJogos());

        jogos.sort(
                Comparator.comparing(Jogo::getData)
                        .thenComparing(Jogo::getHora)
        );

        for (Jogo jogo : jogos) {
            jogosVisiveis.add(jogo);

            modeloJogos.addRow(new Object[]{
                    jogo.getNomeJogo(),
                    textoOuTraco(jogo.getResultado()),
                    valorOuTraco(totalGolosDoJogo(jogo)),
                    textoOuTraco(jogo.getData()),
                    textoOuTraco(jogo.getEstadio()),
                    textoOuTraco(jogo.getFaseGrupo()),
                    textoOuTraco(jogo.getEstado())
            });
        }
    }

    private void abrirPerfilJogador(JTable tabela) {
        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada < 0) {
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linhaSelecionada);

        if (linhaModelo < 0 || linhaModelo >= jogadoresVisiveis.size()) {
            return;
        }

        Jogador jogador = jogadoresVisiveis.get(linhaModelo);

        setVisible(false);

        PerfilJogadorFrame perfil = new PerfilJogadorFrame(
                jogador,
                this::atualizarTabelas
        );

        perfil.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    atualizarTabelas();
                    setVisible(true);
                    toFront();
                });
            }
        });
    }

    private void abrirDetalheJogo(JTable tabela) {
        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada < 0) {
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linhaSelecionada);

        if (linhaModelo < 0 || linhaModelo >= jogosVisiveis.size()) {
            return;
        }

        Jogo jogo = jogosVisiveis.get(linhaModelo);

        setVisible(false);

        new DetalheJogoFrame(jogo, () -> SwingUtilities.invokeLater(() -> {
            atualizarTabelas();
            setVisible(true);
            toFront();
        }));
    }

    private int totalGolosDoJogo(Jogo jogo) {
        String resultado = jogo.getResultado();

        if (resultado == null
                || resultado.isBlank()
                || "-".equals(resultado.trim())) {
            return -1;
        }

        String[] partes = resultado.trim().split("[-–]");

        if (partes.length != 2) {
            return -1;
        }

        try {
            return Integer.parseInt(partes[0].trim())
                    + Integer.parseInt(partes[1].trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String valorOuTraco(int valor) {
        return valor < 0 ? "-" : String.valueOf(valor);
    }

    private String textoOuTraco(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
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
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );

                label.setFont(
                        column == colunaEsquerda
                                ? Tema.FONTE_CARD_TITULO
                                : Tema.FONTE_TEXTO_PEQUENO
                );

                label.setHorizontalAlignment(
                        column == colunaEsquerda
                                ? SwingConstants.LEFT
                                : SwingConstants.CENTER
                );

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