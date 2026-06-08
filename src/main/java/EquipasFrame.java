import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class EquipasFrame extends JFrame {

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    private JTable tabelaEquipas;

    public EquipasFrame() {
        setTitle("Equipas");
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
        content.setBorder(BorderFactory.createEmptyBorder(45, 80, 45, 80));

        content.add(criarTopo(main));
        content.add(Box.createVerticalStrut(20));
        content.add(criarPesquisa());
        content.add(Box.createVerticalStrut(25));
        content.add(criarAreaPrincipal());

        main.add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarTopo(JPanel main) {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esquerda.setOpaque(false);

        JButton btnMenu = new JButton("=");

        btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        btnMenu.setPreferredSize(new Dimension(45, 45));
        btnMenu.setMinimumSize(new Dimension(45, 45));
        btnMenu.setMaximumSize(new Dimension(45, 45));

        btnMenu.setMargin(new Insets(0, 0, 0, 0));

        btnMenu.addActionListener(e -> {
            menuAberto = !menuAberto;
            menuLateral.setVisible(menuAberto);
            main.revalidate();
            main.repaint();
        });

        JPanel tituloBox = new JPanel();
        tituloBox.setOpaque(false);
        tituloBox.setLayout(new BoxLayout(tituloBox, BoxLayout.Y_AXIS));
        tituloBox.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        JLabel titulo = new JLabel("Equipas");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Registo, consulta e validação das equipas participantes.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        tituloBox.add(titulo);
        tituloBox.add(Box.createVerticalStrut(4));
        tituloBox.add(subtitulo);

        esquerda.add(btnMenu);
        esquerda.add(tituloBox);

        JButton btnNova = new JButton("+ Nova Equipa");
        btnNova.setFont(Tema.FONTE_TEXTO_PEQUENO);
        btnNova.setFocusPainted(false);
        btnNova.setBorderPainted(false);
        btnNova.setBackground(Tema.COR_INFO);
        btnNova.setForeground(Tema.COR_TEXTO_CLARO);
        btnNova.setPreferredSize(new Dimension(130, 40));

        topo.add(esquerda, BorderLayout.WEST);
        topo.add(btnNova, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarPesquisa() {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JTextField pesquisa = new JTextField();
        pesquisa.setPreferredSize(new Dimension(230, 38));
        pesquisa.setFont(Tema.FONTE_TEXTO_PEQUENO);
        pesquisa.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        pesquisa.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        RoundedPanel campo = new RoundedPanel(12, Tema.COR_INPUT);
        campo.setLayout(new BorderLayout());
        campo.setPreferredSize(new Dimension(230, 38));
        campo.setBorder(BorderFactory.createLineBorder(Tema.COR_LINHA));
        campo.add(pesquisa, BorderLayout.CENTER);

        pesquisa.setOpaque(false);

        pesquisa.setText("");
        pesquisa.putClientProperty("JTextField.placeholderText", "Pesquisar equipa...");

        linha.add(campo);

        return linha;
    }

    private JPanel criarAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout(25, 0));
        area.setOpaque(false);
        area.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));

        area.add(criarCardTabelaEquipas(), BorderLayout.CENTER);
        area.add(criarCardValidacao(), BorderLayout.EAST);

        return area;
    }

    private JPanel criarCardTabelaEquipas() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Equipas Registadas");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {
                "Nome da Equipa",
                "Grupo",
                "Estado",
                "Jogadores",
                "Pontos"
        };

        DefaultTableModel model = criarModeloNaoEditavel(colunas);

        model.addRow(new Object[]{"Portugal", "Grupo A", "Ativa", 23, 34});
        model.addRow(new Object[]{"Espanha", "Grupo A", "Ativa", 23, 31});
        model.addRow(new Object[]{"Argentina", "Grupo B", "Ativa", 23, 28});
        model.addRow(new Object[]{"Inglaterra", "Grupo B", "Ativa", 23, 23});
        model.addRow(new Object[]{"França", "Grupo C", "Ativa", 23, 21});
        model.addRow(new Object[]{"Brasil", "Grupo C", "Ativa", 23, 18});
        model.addRow(new Object[]{"Bélgica", "Grupo D", "Ativa", 23, 16});
        model.addRow(new Object[]{"Alemanha", "Grupo D", "Inativa", 20, 10});
        model.addRow(new Object[]{"Japão", "Grupo A", "Inativa", 22, 8});
        model.addRow(new Object[]{"Uruguai", "Grupo B", "Ativa", 23, 14});

        tabelaEquipas = new JTable(model);
        configurarTabelaEquipas(tabelaEquipas);

        tabelaEquipas.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabelaEquipas.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabelaEquipas.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabelaEquipas.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabelaEquipas.getColumnModel().getColumn(4).setPreferredWidth(70);

        JScrollPane scroll = new JScrollPane(tabelaEquipas);
        scroll.setBorder(null);
        scroll.setViewportBorder(null);
        scroll.getViewport().setBackground(Tema.COR_CARD);
        scroll.setBackground(Tema.COR_CARD);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(titulo, BorderLayout.WEST);

        card.add(topo, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(10), BorderLayout.WEST);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardValidacao() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_VERDE_SUAVE);
        card.setPreferredSize(new Dimension(240, 0));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("<html>Validação<br>obrigatória</html>");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_VERDE_FORTE);

        JTextArea texto = new JTextArea(
                "Cada equipa deve ter exatamente 23 jogadores " +
                        "antes do início do campeonato.\n\n" +
                        "Equipas fora desta regra não podem participar nos jogos oficiais."
        );

        texto.setFont(Tema.FONTE_TEXTO);
        texto.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setOpaque(false);

        card.add(titulo, BorderLayout.NORTH);
        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private DefaultTableModel criarModeloNaoEditavel(String[] colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void configurarTabelaEquipas(JTable tabela) {
        tabela.setFont(Tema.FONTE_TEXTO_PEQUENO);
        tabela.setRowHeight(34);

        tabela.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setBackground(Tema.COR_CARD);

        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(true);
        tabela.setShowVerticalLines(false);
        tabela.setGridColor(Tema.COR_LINHA);
        tabela.setIntercellSpacing(new Dimension(0, 1));

        tabela.setBorder(null);
        tabela.setFocusable(false);

        tabela.setSelectionBackground(Tema.COR_SELECAO_NEUTRA);
        tabela.setSelectionForeground(Tema.COR_TEXTO_PRINCIPAL);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(Tema.FONTE_TEXTO_PEQUENO);
        header.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        header.setBackground(Tema.COR_CARD);
        header.setBorder(null);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        centro.setVerticalAlignment(SwingConstants.CENTER);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        esquerda.setVerticalAlignment(SwingConstants.CENTER);
        esquerda.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centro);
        }

        tabela.getColumnModel().getColumn(0).setCellRenderer(esquerda);
    }
}