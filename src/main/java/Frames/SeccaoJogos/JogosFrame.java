package Frames.SeccaoJogos;

import Design.MenuLateral;
import GrupoEeleminatoria.CampeonatoRepositorio;
import Models.Campeonato;
import Models.Jogo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class JogosFrame extends JFrame {

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color GREEN = new Color(22, 163, 74);

    private JTable tabelaProximos;
    private JTable tabelaRecentes;

    public JogosFrame() {
        setTitle("Jogos");
        setSize(1250, 780);
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
        pagina.setBackground(BG);
        pagina.setBorder(new EmptyBorder(22, 24, 22, 24));

        JButton botaoMenu = criarBotaoMenu(menuLateral);
        pagina.add(botaoMenu, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(15, 60, 20, 60));

        centro.add(criarCabecalho());
        centro.add(Box.createVerticalStrut(22));
        centro.add(criarCardsResumo());
        centro.add(Box.createVerticalStrut(22));
        centro.add(criarCardProximosJogos());
        centro.add(Box.createVerticalStrut(22));
        centro.add(criarCardJogosRecentes());

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        pagina.add(scroll, BorderLayout.CENTER);
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
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT);

        JLabel subtitulo = new JLabel("Consulta e cria jogos para os campeonatos existentes.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitulo.setForeground(MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton btnNovoJogo = criarBotaoAzul("+ Criar Jogo");
        btnNovoJogo.addActionListener(e -> abrirNovoJogo());

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(btnNovoJogo, BorderLayout.EAST);

        return cabecalho;
    }

    private void abrirNovoJogo() {
        if (CampeonatoRepositorio.listar().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ainda não existe nenhum campeonato criado.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        dispose();
        new NovoJogoFrame();
    }

    private JPanel criarCardsResumo() {
        List<Jogo> todosJogos = obterTodosJogos();

        int total = todosJogos.size();
        int proximos = obterProximosJogos(todosJogos).size();
        int recentes = obterJogosRecentes(todosJogos).size();

        JPanel cards = new JPanel(new GridLayout(1, 3, 18, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);

        cards.add(criarCartaoResumo("Total de Jogos", String.valueOf(total), BLUE, new Color(231, 240, 253)));
        cards.add(criarCartaoResumo("Próximos Jogos", String.valueOf(proximos), GREEN, new Color(232, 248, 238)));
        cards.add(criarCartaoResumo("Jogos Recentes", String.valueOf(recentes), new Color(249, 115, 22), new Color(255, 243, 224)));

        return cards;
    }

    private JPanel criarCartaoResumo(String titulo, String valor, Color corTexto, Color corFundo) {
        JPanel card = new PainelArredondado(18, corFundo);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 20, 14, 20));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelTitulo.setForeground(corTexto);

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        labelValor.setForeground(TEXT);

        card.add(labelTitulo);
        card.add(Box.createVerticalStrut(8));
        card.add(labelValor);
        return card;
    }

    private JPanel criarCardProximosJogos() {
        JPanel card = criarCardTabela("Próximos Jogos");
        tabelaProximos = criarTabela();

        JScrollPane scroll = new JScrollPane(tabelaProximos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardJogosRecentes() {
        JPanel card = criarCardTabela("Jogos Recentes");
        tabelaRecentes = criarTabela();

        JScrollPane scroll = new JScrollPane(tabelaRecentes);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarCardTabela(String tituloTexto) {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(1000, 290));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT);
        titulo.setBorder(new EmptyBorder(0, 0, 14, 0));

        card.add(titulo, BorderLayout.NORTH);
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
        tabela.setRowHeight(32);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setForeground(TEXT);
        tabela.setGridColor(new Color(226, 232, 240));
        tabela.setShowVerticalLines(false);
        tabela.setSelectionBackground(new Color(226, 232, 240));
        tabela.setSelectionForeground(TEXT);

        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setForeground(MUTED);
        tabela.getTableHeader().setBackground(Color.WHITE);
        tabela.getTableHeader().setReorderingAllowed(false);

        return tabela;
    }

    private void carregarTabelas() {
        List<Jogo> todosJogos = obterTodosJogos();
        preencherTabela(tabelaProximos, obterProximosJogos(todosJogos));
        preencherTabela(tabelaRecentes, obterJogosRecentes(todosJogos));
    }

    private List<Jogo> obterTodosJogos() {
        List<Jogo> jogos = new ArrayList<>();

        for (Campeonato campeonato : CampeonatoRepositorio.listar()) {
            jogos.addAll(campeonato.getJogos());
        }

        return jogos;
    }

    private List<Jogo> obterProximosJogos(List<Jogo> todosJogos) {
        LocalDate hoje = LocalDate.now();
        List<Jogo> proximos = new ArrayList<>();

        for (Jogo jogo : todosJogos) {
            LocalDate data = converterData(jogo.getData());
            boolean finalizado = jogo.getEstado() != null
                    && jogo.getEstado().equalsIgnoreCase("Finalizado");

            if (data != null && !data.isBefore(hoje) && !finalizado) {
                proximos.add(jogo);
            }
        }

        proximos.sort(Comparator
                .comparing((Jogo jogo) -> converterData(jogo.getData()))
                .thenComparing(Jogo::getHora));

        return proximos;
    }

    private List<Jogo> obterJogosRecentes(List<Jogo> todosJogos) {
        LocalDate hoje = LocalDate.now();
        List<Jogo> recentes = new ArrayList<>();

        for (Jogo jogo : todosJogos) {
            LocalDate data = converterData(jogo.getData());
            boolean finalizado = jogo.getEstado() != null
                    && jogo.getEstado().equalsIgnoreCase("Finalizado");

            if (data != null && (data.isBefore(hoje) || finalizado)) {
                recentes.add(jogo);
            }
        }

        recentes.sort(Comparator
                .comparing((Jogo jogo) -> converterData(jogo.getData()), Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Jogo::getHora));

        return recentes;
    }

    private void preencherTabela(JTable tabela, List<Jogo> jogos) {
        DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
        modelo.setRowCount(0);

        for (Jogo jogo : jogos) {
            modelo.addRow(new Object[]{
                    formatarData(jogo.getData()),
                    jogo.getHora(),
                    jogo.getEquipaA(),
                    jogo.getEquipaB(),
                    jogo.getEstadio(),
                    jogo.getFaseGrupo(),
                    jogo.getEstado(),
                    jogo.getResultado(),
                    jogo.getCampeonato()
            });
        }
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
            return dataTexto;
        }

        String texto = data.format(DateTimeFormatter.ofPattern("dd MMM", new Locale("pt", "PT")));
        return texto.isEmpty() ? texto : texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private JButton criarBotaoMenu(JPanel menuLateral) {
        JButton botaoMenu = new JButton("☰");
        botaoMenu.setFocusPainted(false);
        botaoMenu.setBorderPainted(false);
        botaoMenu.setContentAreaFilled(false);
        botaoMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        botaoMenu.setForeground(TEXT);
        botaoMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botaoMenu.addActionListener(e -> {
            menuLateral.setVisible(!menuLateral.isVisible());
            revalidate();
            repaint();
        });

        return botaoMenu;
    }

    private JButton criarBotaoAzul(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(BLUE);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(11, 18, 11, 18));
        return botao;
    }

    static class PainelArredondado extends JPanel {
        private final int raio;
        private final Color corFundo;

        public PainelArredondado(int raio, Color corFundo) {
            this.raio = raio;
            this.corFundo = corFundo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D desenho = (Graphics2D) g.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            desenho.setColor(new Color(0, 0, 0, 14));
            desenho.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, raio, raio);
            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, raio, raio);
            desenho.dispose();
            super.paintComponent(g);
        }
    }
}
