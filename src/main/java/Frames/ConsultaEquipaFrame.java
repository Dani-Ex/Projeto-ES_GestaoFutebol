package Frames;

import Design.ModernScrollBarUI;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.Tema;
import Models.Equipa;
import Models.Jogador;
import Models.JogadorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ConsultaEquipaFrame extends JFrame {

    private final Equipa equipa;
    private final java.util.List<Jogador> jogadoresEquipa;

    public ConsultaEquipaFrame(Equipa equipa) {
        this.equipa = equipa;
        this.jogadoresEquipa = new JogadorService().listarPorEquipa(
                equipa.getNome(),
                equipa.getCampeonato()
        );

        setTitle("Consulta de Equipa - " + equipa.getNome());
        setSize(1280, 760);
        setMinimumSize(new Dimension(1120, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.COR_FUNDO);

        JScrollPane scroll = new JScrollPane(criarConteudo());
        scroll.setBorder(null);
        scroll.setViewportBorder(null);
        scroll.getViewport().setBackground(Tema.COR_FUNDO);
        ModernScrollBarUI.aplicar(scroll);

        fundo.add(scroll, BorderLayout.CENTER);

        setContentPane(fundo);
        setVisible(true);
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Tema.COR_FUNDO);
        content.setBorder(new EmptyBorder(28, 38, 38, 38));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 24, 0);
        content.add(criarTopo(), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 24, 0);
        content.add(criarCardsResumo(), gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 24, 0);
        content.add(criarAreaDetalhes(), gbc);

        gbc.gridy = 3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(criarAreaInferior(), gbc);

        return content;
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JButton voltar = criarBotao("← Voltar a Equipas", Tema.COR_CARD, Tema.COR_TEXTO_PRINCIPAL, this::dispose);
        JButton editar = criarBotao("Editar Equipa", Tema.COR_INFO, Tema.COR_TEXTO_CLARO, () -> mostrarFuncionalidadePendente("Editar equipa"));
        JButton editarJogadores = criarBotao("Editar Jogadores", Tema.COR_CARD, Tema.COR_INFO, () -> mostrarFuncionalidadePendente("Editar jogadores"));

        JPanel esquerda = new JPanel();
        esquerda.setOpaque(false);
        esquerda.setLayout(new BoxLayout(esquerda, BoxLayout.Y_AXIS));
        esquerda.add(voltar);
        esquerda.add(Box.createVerticalStrut(14));
        esquerda.add(criarTitulo());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        direita.setOpaque(false);
        direita.add(editarJogadores);
        direita.add(editar);

        topo.add(esquerda, BorderLayout.WEST);
        topo.add(direita, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarTitulo() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(equipa.getNome());
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 34));
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Consulta detalhada da equipa selecionada, integrantes, desempenho e validação.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(4));
        painel.add(subtitulo);

        return painel;
    }

    private JPanel criarCardsResumo() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 18, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(650, 82));

        cards.add(criarCardResumo(totalJogadores() + "/23", "Jogadores"));
        cards.add(criarCardResumo(String.valueOf(equipa.getPontos()), "Pontos"));
        cards.add(criarCardResumo(String.valueOf(totalGolos()), "Golos"));
        cards.add(criarCardResumo(normalizarGrupo(equipa.getGrupo()), "Grupo"));

        return cards;
    }

    private RoundedPanel criarCardResumo(String valor, String label) {
        RoundedPanel card = new RoundedPanel(12, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(14, 16, 12, 16));

        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(Tema.FONTE_CARD_VALOR_GRANDE);
        valorLabel.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel textoLabel = new JLabel(label);
        textoLabel.setFont(Tema.FONTE_TEXTO_PEQUENO);
        textoLabel.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        card.add(valorLabel, BorderLayout.CENTER);
        card.add(textoLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel criarAreaDetalhes() {
        JPanel area = new JPanel(new BorderLayout(28, 0));
        area.setOpaque(false);
        area.setPreferredSize(new Dimension(900, 175));

        area.add(criarCardInformacoes(), BorderLayout.CENTER);
        area.add(criarCardValidacao(), BorderLayout.EAST);

        return area;
    }

    private RoundedPanel criarCardInformacoes() {
        RoundedPanel card = new RoundedPanel(12, Tema.COR_CARD);
        card.setPreferredSize(new Dimension(430, 170));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 18, 20));

        JLabel titulo = new JLabel("Informações da Equipa");
        titulo.setFont(Tema.FONTE_CARD_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JPanel grid = new JPanel(new GridLayout(2, 3, 18, 12));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(18, 0, 0, 0));

        grid.add(criarMiniInfo("País da equipa", equipa.getPais()));
        grid.add(criarMiniInfo("Ranking", equipa.getRanking()));
        grid.add(criarMiniInfo("Estado", equipa.getEstadoTexto()));
        grid.add(criarMiniInfo("Treinador", equipa.getTreinador()));
        grid.add(criarMiniInfo("Capitão", equipa.getCapitao()));
        grid.add(criarMiniInfo("Campeonato", equipa.getCampeonato()));

        card.add(titulo, BorderLayout.NORTH);
        card.add(grid, BorderLayout.CENTER);

        return card;
    }

    private RoundedPanel criarCardValidacao() {
        boolean plantelCompleto = totalJogadores() == 23;
        RoundedPanel card = new RoundedPanel(12, plantelCompleto ? Tema.COR_VERDE_SUAVE : new Color(254, 226, 226));
        card.setPreferredSize(new Dimension(290, 170));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel titulo = new JLabel("Validação obrigatória");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(plantelCompleto ? Tema.COR_VERDE_FORTE : Tema.COR_ERRO);

        JTextArea texto = new JTextArea(
                "Cada equipa deve ter exatamente 23 jogadores antes do início do campeonato.\n\n" +
                        "Estado atual: " + totalJogadores() + " jogadores registados.\n" +
                        (plantelCompleto ? "Equipa apta para participar." : "Equipa ainda não está apta para participar.")
        );
        texto.setFont(Tema.FONTE_TEXTO_PEQUENO);
        texto.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setOpaque(false);

        card.add(titulo, BorderLayout.NORTH);
        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarAreaInferior() {
        JPanel area = new JPanel(new BorderLayout(28, 0));
        area.setOpaque(false);
        area.setPreferredSize(new Dimension(900, 280));

        area.add(criarCardIntegrantes(), BorderLayout.CENTER);
        area.add(criarCardProximosJogos(), BorderLayout.EAST);

        return area;
    }

    private RoundedPanel criarCardIntegrantes() {
        RoundedPanel card = new RoundedPanel(12, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 18, 20));

        JLabel titulo = new JLabel("Integrantes da Equipa");
        titulo.setFont(Tema.FONTE_CARD_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Jogadores associados à equipa selecionada.");
        subtitulo.setFont(Tema.FONTE_TEXTO_PEQUENO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(titulo);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitulo);

        JTable tabela = criarTabelaJogadores();
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Tema.COR_CARD);
        if (scroll.getColumnHeader() != null) {
            scroll.getColumnHeader().setBorder(BorderFactory.createEmptyBorder());
            scroll.getColumnHeader().setBackground(Tema.COR_CARD);
        }
        scroll.setBackground(Tema.COR_CARD);
        ModernScrollBarUI.aplicar(scroll);

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JTable criarTabelaJogadores() {
        String[] colunas = {"Nº", "Nome", "Posição", "Golos", "Assist.", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Jogador jogador : jogadoresEquipa) {
            modelo.addRow(new Object[]{
                    jogador.getNumero(),
                    jogador.getNome(),
                    jogador.getPosicao(),
                    jogador.getGolos(),
                    jogador.getAssistencias(),
                    jogador.getEstadoTexto()
            });
        }

        if (modelo.getRowCount() == 0) {
            modelo.addRow(new Object[]{"-", "Sem jogadores atribuídos", "-", "-", "-", "-"});
        }

        JTable tabela = new JTable(modelo);
        tabela.setFont(Tema.FONTE_TEXTO_PEQUENO);
        tabela.setRowHeight(34);
        tabela.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setBackground(Tema.COR_CARD);
        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(false);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setBorder(BorderFactory.createEmptyBorder());
        tabela.setTableHeader(tabela.getTableHeader());
        tabela.setSelectionBackground(Tema.COR_SELECAO_NEUTRA);
        tabela.setSelectionForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setFocusable(false);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(Tema.FONTE_CARD_TITULO);
        header.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        header.setBackground(Tema.COR_CARD);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
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

                label.setFont(Tema.FONTE_CARD_TITULO);
                label.setForeground(Tema.COR_TEXTO_SECUNDARIO);
                label.setBackground(Tema.COR_CARD);
                label.setOpaque(true);
                label.setHorizontalAlignment(column == 1 ? SwingConstants.LEFT : SwingConstants.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.COR_LINHA),
                        BorderFactory.createEmptyBorder(0, 8, 8, 8)
                ));

                return label;
            }
        });

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        centro.setBorder(new EmptyBorder(0, 8, 0, 8));

        DefaultTableCellRenderer esquerda = new DefaultTableCellRenderer();
        esquerda.setHorizontalAlignment(SwingConstants.LEFT);
        esquerda.setBorder(new EmptyBorder(0, 8, 0, 8));

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centro);
        }

        tabela.getColumnModel().getColumn(1).setCellRenderer(esquerda);

        return tabela;
    }

    private RoundedPanel criarCardProximosJogos() {
        RoundedPanel card = new RoundedPanel(12, Tema.COR_CARD);
        card.setPreferredSize(new Dimension(290, 250));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Próximos Jogos");
        titulo.setFont(Tema.FONTE_CARD_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JTextArea texto = new JTextArea(
                equipa.getNome() + " vs adversário por definir\n" +
                        "Data por definir\n\n" +
                        equipa.getNome() + " vs adversário por definir\n" +
                        "Data por definir"
        );
        texto.setFont(Tema.FONTE_TEXTO_PEQUENO);
        texto.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setOpaque(false);

        card.add(titulo, BorderLayout.NORTH);
        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarMiniInfo(String label, String valor) {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel labelTexto = new JLabel(label);
        labelTexto.setFont(Tema.FONTE_TEXTO_PEQUENO);
        labelTexto.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        JLabel valorTexto = new JLabel(valor == null || valor.trim().isEmpty() ? "-" : valor);
        valorTexto.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 12));
        valorTexto.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        painel.add(labelTexto);
        painel.add(Box.createVerticalStrut(4));
        painel.add(valorTexto);

        return painel;
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto, Runnable acao) {
        JButton botao = new RoundedButton(texto, fundo, corTexto, 12);
        botao.addActionListener(e -> acao.run());

        return botao;
    }

    private String normalizarGrupo(String grupo) {
        if (grupo == null || grupo.trim().isEmpty() || grupo.equalsIgnoreCase("Sem grupo")) {
            return "-";
        }

        return grupo.replace("Grupo ", "");
    }

    private int totalJogadores() {
        return jogadoresEquipa.isEmpty()
                ? equipa.getTotalJogadores()
                : jogadoresEquipa.size();
    }

    private int totalGolos() {
        if (jogadoresEquipa.isEmpty()) {
            return equipa.getGolos();
        }

        int total = 0;

        for (Jogador jogador : jogadoresEquipa) {
            total += jogador.getGolos();
        }

        return total;
    }

    private void mostrarFuncionalidadePendente(String nome) {
        JOptionPane.showMessageDialog(
                this,
                nome + " será implementado numa próxima etapa.",
                "Funcionalidade pendente",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
