import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Dashboard do Campeonato");
        setSize(1280, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setBackground(new Color(241, 245, 249));
        main.setLayout(new BorderLayout());
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(main);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(criarTopo());
        content.add(Box.createVerticalStrut(20));
        content.add(criarCardsResumo());
        content.add(Box.createVerticalStrut(20));
        content.add(criarSecaoMeio());
        content.add(Box.createVerticalStrut(20));
        content.add(criarSecaoInferior());

        main.add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Dashboard do Campeonato");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(15, 23, 42));

        JLabel subtitulo = new JLabel("Resumo geral com indicadores, ranking das equipas, próximos jogos e últimos jogos.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(100, 116, 139));

        topo.add(titulo);
        topo.add(Box.createVerticalStrut(5));
        topo.add(subtitulo);

        return topo;
    }

    private JPanel criarCardsResumo() {
        JPanel painel = new JPanel(new GridLayout(1, 4, 18, 0));
        painel.setOpaque(false);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        painel.add(criarCardResumo("Patrocínios Totais", "€620.790", new Color(219, 234, 254)));
        painel.add(criarCardResumo("Bilhetes Vendidos", "20.570", new Color(220, 252, 231)));
        painel.add(criarCardResumo("Lucro médio por Jogo", "€504.220", new Color(233, 213, 255)));
        painel.add(criarCardResumo("Lucro Total", "€245.680", new Color(254, 240, 138)));

        return painel;
    }

    private JPanel criarCardResumo(String titulo, String valor, Color corFundo) {
        RoundedPanel card = new RoundedPanel(20, corFundo);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));
        card.setPreferredSize(new Dimension(260, 85));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitulo.setForeground(new Color(79, 70, 229));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValor.setForeground(new Color(15, 23, 42));

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
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JLabel titulo = new JLabel("Lucro por Jogo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JComboBox<String> combo = new JComboBox<>(new String[]{"Todos os Jogos"});
        combo.setPreferredSize(new Dimension(140, 30));

        topo.add(titulo, BorderLayout.WEST);
        topo.add(combo, BorderLayout.EAST);

        JPanel areaGrafico = new JPanel();
        areaGrafico.setBackground(new Color(248, 250, 252));
        areaGrafico.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        areaGrafico.setLayout(new GridBagLayout());

        JLabel placeholder = new JLabel("Área do gráfico");
        placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        placeholder.setForeground(new Color(100, 116, 139));

        areaGrafico.add(placeholder);

        card.add(topo, BorderLayout.NORTH);
        card.add(areaGrafico, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardGrupo() {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Grupo A");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        String[] colunas = {"#", "Equipa", "PD", "V", "E", "D", "DG", "Pts"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        model.addRow(new Object[]{"1", "México", "0", "0", "0", "0", "0", "0"});
        model.addRow(new Object[]{"2", "África do Sul", "0", "0", "0", "0", "0", "0"});
        model.addRow(new Object[]{"3", "Coreia do Sul CP", "0", "0", "0", "0", "0", "0"});
        model.addRow(new Object[]{"4", "República Checa", "0", "0", "0", "0", "0", "0"});

        JTable tabela = new JTable(model);
        tabela.setRowHeight(28);
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

        baixo.add(criarTabelaJogos("Próximos Jogos"));
        baixo.add(criarTabelaResultados("Últimos Jogos"));

        return baixo;
    }

    private JPanel criarTabelaJogos(String tituloTexto) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        String[] colunas = {"Data", "Jogo", "Estádio"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        model.addRow(new Object[]{"25 Mai", "FC Porto vs SL Benfica", "Dragão"});
        model.addRow(new Object[]{"26 Mai", "Sporting CP vs SC Braga", "Alvalade"});
        model.addRow(new Object[]{"27 Mai", "Vitória SC vs Boavista", "D. Afonso"});
        model.addRow(new Object[]{"28 Mai", "Moreirense vs Gil Vicente", "Com. Moreira"});

        JTable tabela = new JTable(model);
        tabela.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarTabelaResultados(String tituloTexto) {
        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        String[] colunas = {"Data", "Jogo", "Estádio", "Resultado"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        model.addRow(new Object[]{"19 Mai", "FC Porto vs SC Braga", "Dragão", "2-1"});
        model.addRow(new Object[]{"18 Mai", "SL Benfica vs Vitória SC", "Luz", "3-0"});
        model.addRow(new Object[]{"17 Mai", "Sporting CP vs Boavista", "Alvalade", "1-1"});
        model.addRow(new Object[]{"16 Mai", "Moreirense vs Gil Vicente", "Com. Moreira", "2-0"});

        JTable tabela = new JTable(model);
        tabela.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }
}