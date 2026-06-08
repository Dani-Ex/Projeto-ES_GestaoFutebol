import Design.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JogadoresFrame extends JFrame {

    private final Color BACKGROUND = new Color(245, 247, 251);
    private final Color CARD = Color.WHITE;
    private final Color TEXT = new Color(15, 23, 42);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color RED = new Color(220, 38, 38);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color ORANGE_LIGHT = new Color(255, 247, 237);
    private final Color ORANGE = new Color(234, 88, 12);

    private final List<Jogador> jogadores;
    private JTable tabela;
    private DefaultTableModel modelo;

    public JogadoresFrame() {
        jogadores = criarJogadoresDemo();

        setTitle("Jogadores");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BACKGROUND);
        add(main);

        main.add(new MenuLateral(this), BorderLayout.WEST);
        main.add(criarConteudo(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel(new BorderLayout(0, 24));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 35, 30, 35));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Jogadores");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT);

        JLabel sub = new JLabel("Consulta, perfil e gestão do estado dos jogadores do campeonato.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(sub);

        JPanel verPerfil = criarBotaoPainel("Ver Perfil", BLUE, Color.WHITE, this::abrirSelecionado);
        JPanel alternar = criarBotaoPainel("Ativar / Inativar", RED, Color.WHITE, this::alternarSelecionado);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setOpaque(false);
        botoes.add(verPerfil);
        botoes.add(alternar);

        topo.add(textos, BorderLayout.WEST);
        topo.add(botoes, BorderLayout.EAST);

        JPanel centro = new JPanel(new BorderLayout(20, 0));
        centro.setOpaque(false);
        centro.add(criarCardTabela(), BorderLayout.CENTER);
        centro.add(criarRegraCard(), BorderLayout.EAST);

        content.add(topo, BorderLayout.NORTH);
        content.add(centro, BorderLayout.CENTER);

        return content;
    }

    private JPanel criarBotaoPainel(String texto, Color fundo, Color corTexto, Runnable acao) {
        RoundedPanel botao = new RoundedPanel(8, fundo);
        botao.setLayout(new BorderLayout());
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(corTexto);

        botao.add(label, BorderLayout.CENTER);

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (acao != null) {
                    acao.run();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBorder(new EmptyBorder(9, 17, 9, 17));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBorder(new EmptyBorder(10, 18, 10, 18));
            }
        });

        return botao;
    }

    private JPanel criarCardTabela() {
        JPanel card = new RoundedPanel(8, CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Lista de Jogadores");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(TEXT);

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
        tabela.setRowHeight(36);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setForeground(MUTED);
        tabela.setSelectionBackground(new Color(219, 234, 254));
        tabela.setSelectionForeground(TEXT);
        tabela.setGridColor(new Color(226, 232, 240));
        tabela.setShowVerticalLines(false);

        DefaultTableCellRenderer estadoRenderer = new DefaultTableCellRenderer() {
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
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                if (!isSelected) {
                    if ("Ativo".equals(String.valueOf(value))) {
                        label.setForeground(GREEN);
                    } else {
                        label.setForeground(RED);
                    }
                }

                return label;
            }
        };

        tabela.getColumnModel().getColumn(8).setCellRenderer(estadoRenderer);

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirSelecionado();
                }
            }
        });

        atualizarTabela();

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarRegraCard() {
        RoundedPanel side = new RoundedPanel(8, ORANGE_LIGHT);
        side.setPreferredSize(new Dimension(270, 0));
        side.setLayout(new BorderLayout());
        side.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel titulo = new JLabel("Regra dos jogadores");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(ORANGE);

        JTextArea texto = new JTextArea(
                "Depois do campeonato iniciar, o jogador não deve ser removido.\n\n" +
                        "Usa o botão de estado para inativar ou ativar o jogador sem apagar o registo.\n\n" +
                        "Duplo clique numa linha abre o perfil completo."
        );

        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setOpaque(false);
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        texto.setForeground(TEXT);

        side.add(titulo, BorderLayout.NORTH);
        side.add(texto, BorderLayout.CENTER);

        return side;
    }

    private void atualizarTabela() {
        modelo.setRowCount(0);

        for (Jogador j : jogadores) {
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
        return jogadores.get(linhaModelo);
    }

    private void abrirSelecionado() {
        Jogador jogador = jogadorSelecionado();

        if (jogador != null) {
            new PerfilJogadorFrame(jogador, this::atualizarTabela);
        }
    }

    private void alternarSelecionado() {
        Jogador jogador = jogadorSelecionado();

        if (jogador == null) {
            return;
        }

        jogador.alternarEstado();
        atualizarTabela();
    }

    private List<Jogador> criarJogadoresDemo() {
        List<Jogador> lista = new ArrayList<>();

        lista.add(new Jogador(
                "Cristiano Ronaldo",
                7,
                "Avançado",
                83,
                1.87,
                "Direito",
                LocalDate.of(1985, 2, 5),
                "Portugal",
                "Funchal, Madeira",
                "Portugal",
                "Grupo A",
                "Top marcador",
                8,
                8,
                2,
                1,
                720,
                86,
                74,
                92,
                true
        ));

        lista.add(new Jogador(
                "Pedro Lima",
                4,
                "Defesa",
                83,
                1.88,
                "Direito",
                LocalDate.of(2000, 1, 20),
                "Brasil",
                "São Paulo",
                "Leiria FC",
                "Grupo A",
                "Mais cartões",
                2,
                1,
                1,
                3,
                650,
                52,
                53,
                61,
                true
        ));

        lista.add(new Jogador(
                "Rafa Silva",
                27,
                "Avançado",
                66,
                1.72,
                "Direito",
                LocalDate.of(1993, 5, 17),
                "Portugal",
                "Vila Franca de Xira",
                "Benfica",
                "Grupo B",
                "Mais assistências",
                7,
                8,
                4,
                1,
                610,
                81,
                88,
                79,
                true
        ));

        lista.add(new Jogador(
                "Otávio Monteiro",
                25,
                "Médio",
                65,
                1.72,
                "Direito",
                LocalDate.of(1995, 2, 9),
                "Portugal",
                "João Pessoa",
                "FC Porto",
                "Grupo C",
                "Criador de jogo",
                8,
                4,
                7,
                1,
                690,
                69,
                91,
                85,
                true
        ));

        lista.add(new Jogador(
                "Bruno Lourenço",
                12,
                "Guarda-Redes",
                85,
                1.90,
                "Direito",
                LocalDate.of(1998, 3, 21),
                "Portugal",
                "Porto",
                "Boavista",
                "Grupo D",
                "Suplente",
                2,
                0,
                0,
                1,
                180,
                35,
                50,
                76,
                false
        ));

        return lista;
    }
}