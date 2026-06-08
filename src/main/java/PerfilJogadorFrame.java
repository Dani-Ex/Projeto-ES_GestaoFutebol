import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class PerfilJogadorFrame extends JFrame {

    private final Jogador jogador;
    private final Runnable onEstadoAlterado;

    private final Color BACKGROUND = new Color(245, 247, 251);
    private final Color CARD = Color.WHITE;
    private final Color TEXT = new Color(15, 23, 42);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BORDER = new Color(220, 226, 235);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color RED = new Color(220, 38, 38);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color ORANGE = new Color(249, 115, 22);
    private final Color PURPLE = new Color(124, 58, 237);

    private JLabel estadoLabel;
    private RoundedButton estadoButton;

    public PerfilJogadorFrame(Jogador jogador, Runnable onEstadoAlterado) {
        this.jogador = jogador;
        this.onEstadoAlterado = onEstadoAlterado;

        setTitle("Perfil do Jogador - " + jogador.getNome());
        setSize(1180, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JScrollPane scroll = new JScrollPane(criarConteudo());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getHorizontalScrollBar().setUnitIncrement(18);
        setContentPane(scroll);

        setVisible(true);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(35, 40, 35, 40));

        content.add(criarTopo());
        content.add(Box.createVerticalStrut(24));
        content.add(criarTitulo());
        content.add(Box.createVerticalStrut(24));
        content.add(criarCardsSuperiores());
        content.add(Box.createVerticalStrut(24));
        content.add(criarStats());
        content.add(Box.createVerticalStrut(24));
        content.add(criarInferior());

        return content;
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        RoundedButton voltar = new RoundedButton("← Voltar a Jogadores", new Color(219, 234, 254), BLUE);
        voltar.addActionListener(e -> dispose());

        RoundedButton editar = new RoundedButton("Editar Jogador", Color.WHITE, BLUE);
        editar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), new EmptyBorder(10, 18, 10, 18)));

        estadoButton = new RoundedButton("", RED, Color.WHITE);
        atualizarBotaoEstado();
        estadoButton.addActionListener(e -> {
            jogador.alternarEstado();
            atualizarBotaoEstado();
            atualizarEstadoLabel();
            if (onEstadoAlterado != null) onEstadoAlterado.run();
        });

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        botoes.setOpaque(false);
        botoes.add(editar);
        botoes.add(estadoButton);

        topo.add(voltar, BorderLayout.WEST);
        topo.add(botoes, BorderLayout.EAST);
        return topo;
    }

    private JPanel criarTitulo() {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nome = new JLabel(jogador.getNome());
        nome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        nome.setForeground(TEXT);
        nome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Perfil completo do jogador, dados pessoais, equipa atual e desempenho no campeonato.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(nome);
        box.add(Box.createVerticalStrut(6));
        box.add(sub);
        return box;
    }

    private JPanel criarCardsSuperiores() {
        JPanel grid = new JPanel(new GridLayout(1, 2, 26, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        grid.add(criarCardPrincipal());
        grid.add(criarCardDados());
        return grid;
    }

    private JPanel criarCardPrincipal() {
        JPanel card = criarCard();

        JPanel top = new JPanel(new BorderLayout(24, 0));
        top.setOpaque(false);

        JLabel avatar = new JLabel(jogador.getIniciais(), SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(219, 234, 254));
        avatar.setForeground(BLUE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 30));
        avatar.setPreferredSize(new Dimension(100, 100));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel nome = new JLabel(jogador.getNome());
        nome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        nome.setForeground(TEXT);

        JLabel pos = new JLabel(jogador.getPosicao() + " • Nº " + jogador.getNumero());
        pos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pos.setForeground(MUTED);

        estadoLabel = new JLabel();
        estadoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        estadoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        estadoLabel.setOpaque(true);
        estadoLabel.setBorder(new EmptyBorder(8, 32, 8, 32));
        estadoLabel.setMaximumSize(new Dimension(145, 38));
        atualizarEstadoLabel();

        info.add(nome);
        info.add(Box.createVerticalStrut(6));
        info.add(pos);
        info.add(Box.createVerticalStrut(16));
        info.add(estadoLabel);

        top.add(avatar, BorderLayout.WEST);
        top.add(info, BorderLayout.CENTER);

        JPanel detalhes = new JPanel(new GridLayout(1, 3, 22, 0));
        detalhes.setOpaque(false);
        detalhes.setBorder(new EmptyBorder(28, 0, 0, 0));
        detalhes.add(infoPequena("Equipa", jogador.getEquipa()));
        detalhes.add(infoPequena("Grupo", jogador.getGrupo()));
        detalhes.add(infoPequena("Ranking", jogador.getRanking()));

        card.add(top, BorderLayout.NORTH);
        card.add(detalhes, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardDados() {
        JPanel card = criarCard();
        JLabel titulo = new JLabel("Dados Pessoais");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT);

        JPanel grid = new JPanel(new GridLayout(3, 3, 22, 20));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(26, 0, 0, 0));

        DecimalFormat dfPeso = new DecimalFormat("0");
        DecimalFormat dfAltura = new DecimalFormat("0.00");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        grid.add(infoPequena("Nome completo", jogador.getNome()));
        grid.add(infoPequena("Data de nascimento", jogador.getDataNascimento().format(fmt)));
        grid.add(infoPequena("Lugar de nascimento", jogador.getLugarNascimento()));
        grid.add(infoPequena("País de origem", jogador.getPaisOrigem()));
        grid.add(infoPequena("Altura", dfAltura.format(jogador.getAltura()) + " m"));
        grid.add(infoPequena("Peso", dfPeso.format(jogador.getPeso()) + " kg"));
        grid.add(infoPequena("Pé dominante", jogador.getPeDominante()));
        grid.add(infoPequena("Posição", jogador.getPosicao()));
        grid.add(infoPequena("Idade", jogador.getIdade() + " anos"));

        card.add(titulo, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarStats() {
        JPanel stats = new JPanel(new GridLayout(1, 5, 20, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        stats.add(statCard(String.valueOf(jogador.getJogos()), "Jogos", BLUE));
        stats.add(statCard(String.valueOf(jogador.getGolos()), "Golos", GREEN));
        stats.add(statCard(String.valueOf(jogador.getAssistencias()), "Assistências", ORANGE));
        stats.add(statCard(String.valueOf(jogador.getCartoes()), "Cartões", RED));
        stats.add(statCard(String.valueOf(jogador.getMinutos()), "Minutos", PURPLE));
        return stats;
    }

    private JPanel criarInferior() {
        JPanel grid = new JPanel(new GridLayout(1, 2, 26, 0));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));
        grid.add(criarDesempenho());
        grid.add(criarUltimosJogos());
        return grid;
    }

    private JPanel criarDesempenho() {
        JPanel card = criarCard();
        JLabel titulo = new JLabel("Desempenho do Jogador");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT);

        JLabel sub = new JLabel("Indicadores principais no campeonato atual.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(MUTED);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(titulo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        JPanel barras = new JPanel();
        barras.setOpaque(false);
        barras.setLayout(new BoxLayout(barras, BoxLayout.Y_AXIS));
        barras.setBorder(new EmptyBorder(28, 0, 0, 0));
        barras.add(barra("Finalização", jogador.getFinalizacao(), GREEN));
        barras.add(Box.createVerticalStrut(20));
        barras.add(barra("Passe e criação", jogador.getPasseCriacao(), BLUE));
        barras.add(Box.createVerticalStrut(20));
        barras.add(barra("Disciplina", jogador.getDisciplina(), ORANGE));

        card.add(header, BorderLayout.NORTH);
        card.add(barras, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarUltimosJogos() {
        JPanel card = criarCard();
        JLabel titulo = new JLabel("Últimos Jogos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT);

        String[] cols = {"Jogo", "Golos", "Assist.", "Nota"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        model.addRow(new Object[]{jogador.getEquipa() + " vs Porto", Math.min(jogador.getGolos(), 2), Math.min(jogador.getAssistencias(), 1), "8,1"});
        model.addRow(new Object[]{jogador.getEquipa() + " vs Lisboa", 0, 0, "6,5"});
        model.addRow(new Object[]{jogador.getEquipa() + " vs Braga", Math.min(jogador.getGolos(), 1), 1, "7,4"});
        model.addRow(new Object[]{jogador.getEquipa() + " vs Coimbra", 0, Math.min(jogador.getAssistencias(), 2), "7,0"});

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setGridColor(new Color(226, 232, 240));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel infoPequena(String label, String valor) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(MUTED);
        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        v.setForeground(TEXT);
        p.add(l);
        p.add(Box.createVerticalStrut(4));
        p.add(v);
        return p;
    }

    private JPanel statCard(String numero, String texto, Color cor) {
        JPanel card = criarCard();
        JLabel n = new JLabel(numero);
        n.setFont(new Font("Segoe UI", Font.BOLD, 28));
        n.setForeground(TEXT);
        JLabel dot = new JLabel("●");
        dot.setForeground(cor);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(MUTED);
        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.add(n, BorderLayout.WEST);
        linha.add(dot, BorderLayout.EAST);
        card.add(linha, BorderLayout.NORTH);
        card.add(label, BorderLayout.CENTER);
        return card;
    }

    private JPanel barra(String nome, int valor, Color cor) {
        JPanel p = new JPanel(new BorderLayout(10, 6));
        p.setOpaque(false);
        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        JLabel l = new JLabel(nome);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(TEXT);
        JLabel v = new JLabel(valor + "%");
        v.setFont(new Font("Segoe UI", Font.BOLD, 12));
        v.setForeground(MUTED);
        linha.add(l, BorderLayout.WEST);
        linha.add(v, BorderLayout.EAST);
        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(valor);
        progress.setStringPainted(false);
        progress.setForeground(cor);
        progress.setBackground(new Color(226, 232, 240));
        progress.setPreferredSize(new Dimension(100, 12));
        p.add(linha, BorderLayout.NORTH);
        p.add(progress, BorderLayout.CENTER);
        return p;
    }

    private JPanel criarCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }

    private void atualizarEstadoLabel() {
        estadoLabel.setText(jogador.getEstadoTexto());
        if (jogador.isAtivo()) {
            estadoLabel.setBackground(new Color(220, 252, 231));
            estadoLabel.setForeground(GREEN);
        } else {
            estadoLabel.setBackground(new Color(254, 226, 226));
            estadoLabel.setForeground(RED);
        }
    }

    private void atualizarBotaoEstado() {
        if (estadoButton == null) return;
        if (jogador.isAtivo()) {
            estadoButton.setText("Inativar Jogador");
            estadoButton.setButtonColor(RED, Color.WHITE);
        } else {
            estadoButton.setText("Ativar Jogador");
            estadoButton.setButtonColor(BLUE, Color.WHITE);
        }
    }
}
