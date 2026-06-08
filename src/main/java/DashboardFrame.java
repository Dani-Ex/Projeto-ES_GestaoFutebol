import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    public DashboardFrame() {
        setTitle("Dashboard do Campeonato");
        setSize(1280, 760);
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
        add(main);

        menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);
        main.add(menuLateral, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

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
                "Resumo geral com indicadores, ranking das equipas, próximos jogos e últimos jogos."
        );
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        tituloBox.add(titulo);
        tituloBox.add(Box.createVerticalStrut(4));
        tituloBox.add(subtitulo);

        JButton btnMenu = new JButton("☰");
        btnMenu.setFont(Tema.FONTE_BOTAO_MENU);
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setBackground(Tema.COR_FUNDO);
        btnMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        btnMenu.setPreferredSize(new Dimension(55, 45));

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

        painel.add(criarCardResumo(
                "Patrocínios Totais",
                "€620.790",
                Tema.CARD_AZUL,
                Tema.CARD_TEXTO_AZUL
        ));

        painel.add(criarCardResumo(
                "Bilhetes Vendidos",
                "20.570",
                Tema.CARD_VERDE,
                Tema.CARD_TEXTO_VERDE
        ));

        painel.add(criarCardResumo(
                "Lucro médio por Jogo",
                "€504.220",
                Tema.CARD_ROXO,
                Tema.CARD_TEXTO_ROXO
        ));

        painel.add(criarCardResumo(
                "Lucro Total",
                "€245.680",
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

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JLabel titulo = new JLabel("Lucro por Jogo");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JComboBox<String> combo = new JComboBox<>(new String[]{"Todos os Jogos"});
        combo.setFont(Tema.FONTE_TEXTO);
        combo.setPreferredSize(new Dimension(150, 32));

        topo.add(titulo, BorderLayout.WEST);
        topo.add(combo, BorderLayout.EAST);

        JPanel areaGrafico = new JPanel(new GridBagLayout());
        areaGrafico.setBackground(new Color(248, 250, 252));
        areaGrafico.setBorder(BorderFactory.createLineBorder(Tema.COR_LINHA));

        JLabel placeholder = new JLabel("Área do gráfico");
        placeholder.setFont(Tema.FONTE_TEXTO);
        placeholder.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        areaGrafico.add(placeholder);

        card.add(topo, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(15), BorderLayout.WEST);
        card.add(areaGrafico, BorderLayout.CENTER);

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

        JLabel titulo = new JLabel("Grupo A");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {"#", "Equipa", "PD", "V", "E", "D", "DG", "Pts"};

        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        model.addRow(new Object[]{"1", "México", "0", "0", "0", "0", "0", "0"});
        model.addRow(new Object[]{"2", "África do Sul", "0", "0", "0", "0", "0", "0"});
        model.addRow(new Object[]{"3", "Coreia do Sul CP", "0", "0", "0", "0", "0", "0"});
        model.addRow(new Object[]{"4", "República Checa", "0", "0", "0", "0", "0", "0"});

        JTable tabela = new JTable(model);
        configurarTabela(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);

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
        RoundedPanel card = criarCardTabela("Próximos Jogos");

        String[] colunas = {"Data", "Jogo", "Estádio"};

        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        model.addRow(new Object[]{"25 Mai", "FC Porto vs SL Benfica", "Dragão"});
        model.addRow(new Object[]{"26 Mai", "Sporting CP vs SC Braga", "Alvalade"});
        model.addRow(new Object[]{"27 Mai", "Vitória SC vs Boavista", "D. Afonso"});
        model.addRow(new Object[]{"28 Mai", "Moreirense vs Gil Vicente", "Com. Moreira"});

        JTable tabela = new JTable(model);
        configurarTabela(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarTabelaUltimosJogos() {
        RoundedPanel card = criarCardTabela("Últimos Jogos");

        String[] colunas = {"Data", "Jogo", "Estádio", "Resultado"};

        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        model.addRow(new Object[]{"19 Mai", "FC Porto vs SC Braga", "Dragão", "2-1"});
        model.addRow(new Object[]{"18 Mai", "SL Benfica vs Vitória SC", "Luz", "3-0"});
        model.addRow(new Object[]{"17 Mai", "Sporting CP vs Boavista", "Alvalade", "1-1"});
        model.addRow(new Object[]{"16 Mai", "Moreirense vs Gil Vicente", "Com. Moreira", "2-0"});

        JTable tabela = new JTable(model);
        configurarTabela(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);

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

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        card.add(titulo, BorderLayout.NORTH);

        return card;
    }

    private void configurarTabela(JTable tabela) {
        tabela.setFont(Tema.FONTE_TEXTO_PEQUENO);
        tabela.setRowHeight(28);
        tabela.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setGridColor(Tema.COR_LINHA);
        tabela.setSelectionBackground(new Color(219, 234, 254));
        tabela.setSelectionForeground(Tema.COR_TEXTO_PRINCIPAL);

        tabela.getTableHeader().setFont(Tema.FONTE_TEXTO_PEQUENO);
        tabela.getTableHeader().setForeground(Tema.COR_TEXTO_SECUNDARIO);
        tabela.getTableHeader().setBackground(Tema.COR_CARD);
    }
}