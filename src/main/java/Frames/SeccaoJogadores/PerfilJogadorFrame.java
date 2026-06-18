package Frames.SeccaoJogadores;

import Design.RoundedPanel;
import Models.Jogador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

public class PerfilJogadorFrame extends JFrame {

    private final Jogador jogador;
    private final Runnable onEstadoAlterado;

    private final Color BACKGROUND = new Color(245, 247, 251);
    private final Color CARD = Color.WHITE;
    private final Color TEXT = new Color(15, 23, 42);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color RED = new Color(220, 38, 38);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color ORANGE = new Color(249, 115, 22);
    private final Color PURPLE = new Color(124, 58, 237);

    private JLabel labelEstadoCard;

    public PerfilJogadorFrame(Jogador jogador, Runnable onEstadoAlterado) {
        this.jogador = jogador;
        this.onEstadoAlterado = onEstadoAlterado;

        setTitle("Perfil do Jogador - " + jogador.getNome());
        setSize(1250, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(BACKGROUND);

        JScrollPane scroll = new JScrollPane(criarConteudo());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.getHorizontalScrollBar().setUnitIncrement(18);

        fundo.add(scroll, BorderLayout.CENTER);
        setContentPane(fundo);

        setVisible(true);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(30, 35, 35, 35));

        content.add(criarTopo());
        content.add(Box.createVerticalStrut(25));
        content.add(criarTitulo());
        content.add(Box.createVerticalStrut(25));
        content.add(criarCardsPrincipais());
        content.add(Box.createVerticalStrut(25));
        content.add(criarCardsNumericos());
        content.add(Box.createVerticalStrut(25));
        content.add(criarAreaInferior());

        return content;
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JPanel voltar = criarBotaoPainel("← Voltar a Jogadores", BLUE, Color.WHITE, this::dispose);
        JPanel editar = criarBotaoPainel("Editar Jogador", CARD, BLUE, this::editarJogador);

        String textoEstado = jogador.isAtivo() ? "Inativar Jogador" : "Ativar Jogador";
        Color corEstado = jogador.isAtivo() ? RED : BLUE;

        JPanel estado = criarBotaoPainel(textoEstado, corEstado, Color.WHITE, this::alternarEstado);

        JPanel botoesDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        botoesDireita.setOpaque(false);
        botoesDireita.add(editar);
        botoesDireita.add(estado);

        topo.add(voltar, BorderLayout.WEST);
        topo.add(botoesDireita, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarTitulo() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel(jogador.getNome());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titulo.setForeground(TEXT);

        JLabel sub = new JLabel("Perfil completo do jogador, dados pessoais, equipa atual e desempenho no campeonato.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        sub.setForeground(MUTED);

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(sub);

        return painel;
    }

    private JPanel criarCardsPrincipais() {
        JPanel linha = new JPanel(new GridLayout(1, 2, 28, 0));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        linha.add(criarCardPerfil());
        linha.add(criarCardDadosPessoais());

        return linha;
    }

    private JPanel criarCardPerfil() {
        RoundedPanel card = new RoundedPanel(8, CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel parteSuperior = new JPanel(new BorderLayout(20, 0));
        parteSuperior.setOpaque(false);

        JLabel avatar = new JLabel(obterIniciais(jogador.getNome()), SwingConstants.CENTER);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(219, 234, 254));
        avatar.setForeground(BLUE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 30));
        avatar.setPreferredSize(new Dimension(105, 105));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);

        JLabel nome = new JLabel(jogador.getNome());
        nome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        nome.setForeground(TEXT);

        JLabel posicao = new JLabel(jogador.getPosicao() + " • Nº " + jogador.getNumero());
        posicao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        posicao.setForeground(MUTED);

        labelEstadoCard = new JLabel(jogador.getEstadoTexto(), SwingConstants.CENTER);
        labelEstadoCard.setOpaque(true);
        labelEstadoCard.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelEstadoCard.setBorder(new EmptyBorder(8, 30, 8, 30));
        atualizarEstadoCard();

        info.add(nome);
        info.add(Box.createVerticalStrut(8));
        info.add(posicao);
        info.add(Box.createVerticalStrut(16));
        info.add(labelEstadoCard);

        parteSuperior.add(avatar, BorderLayout.WEST);
        parteSuperior.add(info, BorderLayout.CENTER);

        JPanel detalhes = new JPanel(new GridLayout(1, 4, 20, 0));
        detalhes.setOpaque(false);
        detalhes.setBorder(new EmptyBorder(28, 0, 0, 0));

        detalhes.add(criarMiniInfo("Equipa", jogador.getEquipa()));
        detalhes.add(criarMiniInfo("Models.Campeonato", jogador.getCampeonato()));
        detalhes.add(criarMiniInfo("Grupo", jogador.getGrupo()));
        detalhes.add(criarMiniInfo("Ranking", jogador.getRanking()));

        card.add(parteSuperior, BorderLayout.NORTH);
        card.add(detalhes, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardDadosPessoais() {
        RoundedPanel card = new RoundedPanel(8, CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("Dados Pessoais");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT);

        JPanel grid = new JPanel(new GridLayout(3, 3, 25, 18));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(28, 0, 0, 0));

        grid.add(criarMiniInfo("Nome completo", jogador.getNome()));
        grid.add(criarMiniInfo("Data de nascimento", formatarData()));
        grid.add(criarMiniInfo("Lugar de nascimento", jogador.getCidadeNascimento()));

        grid.add(criarMiniInfo("País de origem", jogador.getPaisOrigem()));
        grid.add(criarMiniInfo("Altura", String.format("%.2f m", jogador.getAltura()).replace(".", ",")));
        grid.add(criarMiniInfo("Peso", jogador.getPeso() + " kg"));

        grid.add(criarMiniInfo("Pé dominante", jogador.getPeDominante()));
        grid.add(criarMiniInfo("Posição", jogador.getPosicao()));
        grid.add(criarMiniInfo("Idade", jogador.getIdade() + " anos"));

        card.add(titulo, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardsNumericos() {
        JPanel linha = new JPanel(new GridLayout(1, 5, 20, 0));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));

        linha.add(criarStatCard(String.valueOf(jogador.getJogos()), "Jogos", BLUE));
        linha.add(criarStatCard(String.valueOf(jogador.getGolos()), "Golos", GREEN));
        linha.add(criarStatCard(String.valueOf(jogador.getAssistencias()), "Assistências", ORANGE));
        linha.add(criarStatCard(String.valueOf(jogador.getCartoes()), "Cartões", RED));
        linha.add(criarStatCard(String.valueOf(jogador.getMinutos()), "Minutos", PURPLE));

        return linha;
    }

    private JPanel criarStatCard(String valor, String label, Color cor) {
        RoundedPanel card = new RoundedPanel(8, CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JLabel numero = new JLabel(valor);
        numero.setFont(new Font("Segoe UI", Font.BOLD, 28));
        numero.setForeground(TEXT);

        JLabel ponto = new JLabel("●");
        ponto.setFont(new Font("Segoe UI", Font.BOLD, 20));
        ponto.setForeground(cor);

        JLabel texto = new JLabel(label);
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        texto.setForeground(MUTED);

        topo.add(numero, BorderLayout.WEST);
        topo.add(ponto, BorderLayout.EAST);

        card.add(topo, BorderLayout.NORTH);
        card.add(texto, BorderLayout.SOUTH);

        return card;
    }

    private JPanel criarAreaInferior() {
        JPanel linha = new JPanel(new GridLayout(1, 2, 28, 0));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));

        linha.add(criarCardDesempenho());
        linha.add(criarCardUltimosJogos());

        return linha;
    }

    private JPanel criarCardDesempenho() {
        RoundedPanel card = new RoundedPanel(8, CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel titulo = new JLabel("Desempenho do Jogador");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT);

        JLabel sub = new JLabel("Indicadores principais no campeonato atual.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(MUTED);

        header.add(titulo);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        JPanel barras = new JPanel();
        barras.setLayout(new BoxLayout(barras, BoxLayout.Y_AXIS));
        barras.setOpaque(false);
        barras.setBorder(new EmptyBorder(30, 0, 0, 0));

        barras.add(criarBarra("Finalização", jogador.getFinalizacao(), GREEN));
        barras.add(Box.createVerticalStrut(22));
        barras.add(criarBarra("Passe e criação", jogador.getPasseCriacao(), BLUE));
        barras.add(Box.createVerticalStrut(22));
        barras.add(criarBarra("Disciplina", jogador.getDisciplina(), ORANGE));

        card.add(header, BorderLayout.NORTH);
        card.add(barras, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarBarra(String nome, int valor, Color cor) {
        JPanel panel = new JPanel(new BorderLayout(8, 6));
        panel.setOpaque(false);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JLabel label = new JLabel(nome);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);

        JLabel percent = new JLabel(valor + "%");
        percent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        percent.setForeground(MUTED);

        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue(valor);
        barra.setStringPainted(false);
        barra.setForeground(cor);
        barra.setBackground(new Color(229, 231, 235));
        barra.setPreferredSize(new Dimension(100, 12));

        topo.add(label, BorderLayout.WEST);
        topo.add(percent, BorderLayout.EAST);

        panel.add(topo, BorderLayout.NORTH);
        panel.add(barra, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarCardUltimosJogos() {
        RoundedPanel card = new RoundedPanel(8, CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("Últimos Jogos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT);

        String[] colunas = {"Jogo", "Golos", "Assist.", "Nota"};

        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modelo.addRow(new Object[]{jogador.getEquipa() + " vs Porto", jogador.getGolos() > 0 ? 1 : 0, 1, "6,4"});
        modelo.addRow(new Object[]{jogador.getEquipa() + " vs Lisboa", 0, 0, "6,5"});
        modelo.addRow(new Object[]{jogador.getEquipa() + " vs Braga", jogador.getGolos() > 2 ? 2 : 0, 1, "7,8"});
        modelo.addRow(new Object[]{jogador.getEquipa() + " vs Benfica", jogador.getGolos() > 4 ? 1 : 0, 0, "8,1"});

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(34);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.setGridColor(new Color(226, 232, 240));
        tabela.setShowVerticalLines(false);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarMiniInfo(String label, String valor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(MUTED);

        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.BOLD, 14));
        v.setForeground(TEXT);

        panel.add(l);
        panel.add(Box.createVerticalStrut(5));
        panel.add(v);

        return panel;
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

    private void alternarEstado() {
        jogador.alternarEstado();

        if (onEstadoAlterado != null) {
            onEstadoAlterado.run();
        }

        dispose();
        new PerfilJogadorFrame(jogador, onEstadoAlterado);
    }

    private void atualizarEstadoCard() {
        if (labelEstadoCard == null) {
            return;
        }

        labelEstadoCard.setText(jogador.getEstadoTexto());

        if (jogador.isAtivo()) {
            labelEstadoCard.setBackground(new Color(220, 252, 231));
            labelEstadoCard.setForeground(GREEN);
        } else {
            labelEstadoCard.setBackground(new Color(254, 226, 226));
            labelEstadoCard.setForeground(RED);
        }
    }

    private void editarJogador() {
        new EditarJogadorFrame(jogador, () -> {
            if (onEstadoAlterado != null) {
                onEstadoAlterado.run();
            }

            dispose();
            new PerfilJogadorFrame(jogador, onEstadoAlterado);
        });
    }

    private String obterIniciais(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return "?";
        }

        String[] partes = nome.trim().split("\\s+");

        if (partes.length == 1) {
            return partes[0].substring(0, 1).toUpperCase();
        }

        return (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }

    private String formatarData() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return jogador.getDataNascimento().format(formatter);
        } catch (Exception e) {
            return "Não definida";
        }
    }
}
