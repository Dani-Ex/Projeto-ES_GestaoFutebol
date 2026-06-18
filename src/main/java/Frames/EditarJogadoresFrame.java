package Frames;

import Design.ModernScrollBarUI;
import Design.PlaceholderTextField;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.Tema;
import Models.Equipa;
import Models.EquipaService;
import Models.Jogador;
import Models.JogadorService;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EditarJogadoresFrame extends JFrame {

    private final Equipa equipa;
    private final Runnable onGuardar;
    private final JogadorService jogadorService = new JogadorService();
    private final EquipaService equipaService = new EquipaService();

    private List<Jogador> jogadoresEquipa = new ArrayList<>();
    private DefaultTableModel modeloTabela;
    private JLabel subtituloTabela;

    private PlaceholderTextField campoNome;
    private PlaceholderTextField campoNumero;
    private JComboBox<String> campoPosicao;
    private PlaceholderTextField campoPeso;
    private PlaceholderTextField campoAltura;
    private JComboBox<String> campoPeDominante;
    private PlaceholderTextField campoDataNascimento;
    private PlaceholderTextField campoPaisOrigem;
    private PlaceholderTextField campoCidadeNascimento;

    public EditarJogadoresFrame(Equipa equipa, Runnable onGuardar) {
        this.equipa = equipa;
        this.onGuardar = onGuardar;
        carregarJogadores();

        setTitle("Editar Jogadores - " + equipa.getNome());
        setSize(1260, 760);
        setMinimumSize(new Dimension(1080, 680));
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
        content.setBorder(new EmptyBorder(34, 82, 40, 82));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 24, 0);
        content.add(criarTopo(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        content.add(criarAreaAdicionar(), gbc);

        gbc.gridy = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(criarTabelaCard(), gbc);

        return content;
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JPanel esquerda = new JPanel();
        esquerda.setOpaque(false);
        esquerda.setLayout(new BoxLayout(esquerda, BoxLayout.Y_AXIS));

        JButton voltar = criarBotao("← Voltar a Equipa", Tema.COR_CARD, Tema.COR_TEXTO_PRINCIPAL, this::guardarEFechar);

        JLabel titulo = new JLabel("Editar Jogador: " + equipa.getNome());
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Atualizacao dos dados da equipa e gestao dos jogadores associados.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        esquerda.add(voltar);
        esquerda.add(Box.createVerticalStrut(22));
        esquerda.add(titulo);
        esquerda.add(Box.createVerticalStrut(4));
        esquerda.add(subtitulo);

        JButton guardar = criarBotao("Guardar Alteracoes", Tema.COR_INFO, Tema.COR_TEXTO_CLARO, this::guardarEFechar);
        guardar.setPreferredSize(new Dimension(165, 42));

        topo.add(esquerda, BorderLayout.WEST);
        topo.add(guardar, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarAreaAdicionar() {
        JPanel area = new JPanel(new BorderLayout(30, 0));
        area.setOpaque(false);

        area.add(criarFormularioAdicionar(), BorderLayout.CENTER);
        area.add(criarRegraCard(), BorderLayout.EAST);

        return area;
    }

    private RoundedPanel criarFormularioAdicionar() {
        RoundedPanel card = new RoundedPanel(12, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 22, 12, 22));
        card.setPreferredSize(new Dimension(820, 250));

        JLabel titulo = new JLabel("Adicionar Jogador");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JPanel campos = new JPanel(new GridLayout(3, 3, 34, 10));
        campos.setOpaque(false);
        campos.setBorder(new EmptyBorder(16, 0, 16, 0));

        campoNome = criarCampo("Novo Jogador");
        campoNumero = criarCampo("24");
        campoPosicao = criarCombo(new String[]{"Guarda-Redes", "Defesa", "Medio", "Avancado"});
        campoPeso = criarCampo("75");
        campoAltura = criarCampo("180");
        campoPeDominante = criarCombo(new String[]{"Direito", "Esquerdo", "Ambos"});
        campoDataNascimento = criarCampo("2000-01-20");
        campoPaisOrigem = criarCampo(equipa.getPais());
        campoCidadeNascimento = criarCampo(equipa.getCidade());

        campos.add(criarCampoComLabel("Nome", campoNome));
        campos.add(criarCampoComLabel("Peso", campoPeso));
        campos.add(criarCampoComLabel("Data nascimento", campoDataNascimento));
        campos.add(criarCampoComLabel("Numero", campoNumero));
        campos.add(criarCampoComLabel("Altura", campoAltura));
        campos.add(criarCampoComLabel("Pais de origem", campoPaisOrigem));
        campos.add(criarCampoComLabel("Posicao", campoPosicao));
        campos.add(criarCampoComLabel("Pe dominante", campoPeDominante));
        campos.add(criarCampoComLabel("Lugar de nascimento", campoCidadeNascimento));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rodape.setOpaque(false);
        JButton adicionar = criarBotao("+ Adicionar", Tema.COR_INFO, Tema.COR_TEXTO_CLARO, this::adicionarJogador);
        adicionar.setPreferredSize(new Dimension(124, 38));
        rodape.add(adicionar);

        card.add(titulo, BorderLayout.NORTH);
        card.add(campos, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);

        return card;
    }

    private RoundedPanel criarRegraCard() {
        RoundedPanel card = new RoundedPanel(12, Tema.COR_VERDE_SUAVE);
        card.setPreferredSize(new Dimension(255, 250));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 22, 24, 22));

        JLabel titulo = new JLabel("Regra da equipa");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(Tema.COR_VERDE_FORTE);

        JTextArea texto = new JTextArea(
                "A equipa deve ter exatamente 23 jogadores ativos.\n\n" +
                        "Antes do inicio do campeonato e possivel adicionar ou remover jogadores.\n\n" +
                        "Apos o inicio, os jogadores removidos passam apenas a inativos."
        );
        texto.setFont(Tema.FONTE_TEXTO);
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

    private RoundedPanel criarTabelaCard() {
        RoundedPanel card = new RoundedPanel(12, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(820, 310));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel titulo = new JLabel("Jogadores da Equipa");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        subtituloTabela = new JLabel(textoContagemJogadores());
        subtituloTabela.setFont(Tema.FONTE_TEXTO_PEQUENO);
        subtituloTabela.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(titulo);
        header.add(Box.createVerticalStrut(3));
        header.add(subtituloTabela);

        JTable tabela = criarTabela();
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Tema.COR_CARD);
        scroll.setBackground(Tema.COR_CARD);
        if (scroll.getColumnHeader() != null) {
            scroll.getColumnHeader().setBorder(BorderFactory.createEmptyBorder());
            scroll.getColumnHeader().setBackground(Tema.COR_CARD);
        }
        ModernScrollBarUI.aplicar(scroll);

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JTable criarTabela() {
        String[] colunas = {"Nº", "Nome", "Posicao", "Estado", "Acao"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 && !jogadoresEquipa.isEmpty();
            }
        };

        JTable tabela = new JTable(modeloTabela);
        tabela.setFont(Tema.FONTE_TEXTO_PEQUENO);
        tabela.setRowHeight(34);
        tabela.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setBackground(Tema.COR_CARD);
        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(false);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setBorder(BorderFactory.createEmptyBorder());
        tabela.setSelectionBackground(Tema.COR_SELECAO_NEUTRA);
        tabela.setSelectionForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setFocusable(false);

        configurarHeader(tabela);
        configurarRenderers(tabela);
        preencherTabela();

        return tabela;
    }

    private void configurarHeader(JTable tabela) {
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
    }

    private void configurarRenderers(JTable tabela) {
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
        tabela.getColumnModel().getColumn(4).setCellRenderer(new BotaoRemoverRenderer());
        tabela.getColumnModel().getColumn(4).setCellEditor(new BotaoRemoverEditor(tabela));
    }

    private void preencherTabela() {
        modeloTabela.setRowCount(0);

        if (jogadoresEquipa.isEmpty()) {
            modeloTabela.addRow(new Object[]{"-", "Sem jogadores atribuidos", "-", "-", ""});
            atualizarSubtituloTabela();
            return;
        }

        for (Jogador jogador : jogadoresEquipa) {
            modeloTabela.addRow(new Object[]{
                    jogador.getNumero(),
                    jogador.getNome(),
                    jogador.getPosicao(),
                    jogador.getEstadoTexto(),
                    jogador.isAtivo() ? "Remover" : "Inativo"
            });
        }

        atualizarSubtituloTabela();
    }

    private PlaceholderTextField criarCampo(String valor) {
        PlaceholderTextField campo = new PlaceholderTextField("");
        campo.setText(valor == null ? "" : valor);
        campo.setPreferredSize(new Dimension(210, 30));
        campo.setFont(Tema.FONTE_TEXTO_PEQUENO);
        campo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        campo.setBackground(Tema.COR_INPUT);
        campo.setOpaque(true);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Tema.COR_LINHA, 8),
                new EmptyBorder(0, 10, 0, 10)
        ));

        return campo;
    }

    private JComboBox<String> criarCombo(String[] opcoes) {
        JComboBox<String> combo = new JComboBox<>(opcoes);
        combo.setPreferredSize(new Dimension(210, 30));
        combo.setFont(Tema.FONTE_TEXTO_PEQUENO);
        combo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        combo.setBackground(Tema.COR_INPUT);
        combo.setFocusable(false);
        combo.setBorder(new RoundedBorder(Tema.COR_LINHA, 8));
        return combo;
    }

    private JPanel criarCampoComLabel(String label, JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 5));
        painel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 12));
        lbl.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        painel.add(lbl, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);

        return painel;
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto, Runnable acao) {
        JButton botao = new RoundedButton(texto, fundo, corTexto, 12);
        botao.addActionListener(e -> acao.run());
        return botao;
    }

    private void adicionarJogador() {
        try {
            validarCamposAdicionar();

            Jogador jogador = new Jogador(
                    campoNome.getText().trim(),
                    Integer.parseInt(campoNumero.getText().trim()),
                    String.valueOf(campoPosicao.getSelectedItem()),
                    Integer.parseInt(campoPeso.getText().trim()),
                    normalizarAltura(campoAltura.getText().trim()),
                    String.valueOf(campoPeDominante.getSelectedItem()),
                    parseData(campoDataNascimento.getText().trim()),
                    campoPaisOrigem.getText().trim(),
                    campoCidadeNascimento.getText().trim(),
                    equipa.getNome(),
                    equipa.getCampeonato(),
                    equipa.getGrupo(),
                    "",
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    100,
                    true
            );

            jogadorService.adicionarJogador(jogador);
            atualizarDepoisDeAlteracao();
            limparFormularioAdicionar();
        } catch (NumberFormatException e) {
            mostrarErro("Verifica os campos numericos: numero, peso e altura.");
        } catch (DateTimeParseException e) {
            mostrarErro("A data deve estar no formato AAAA-MM-DD ou DD/MM/AAAA.");
        } catch (IllegalArgumentException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void removerJogador(int linhaModelo) {
        if (linhaModelo < 0 || linhaModelo >= jogadoresEquipa.size()) {
            return;
        }

        Jogador jogador = jogadoresEquipa.get(linhaModelo);
        String acao = campeonatoIniciado() ? "inativar" : "remover";
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Queres " + acao + " o jogador " + jogador.getNome() + "?",
                "Confirmar alteracao",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        jogadorService.removerOuInativarJogador(jogador, campeonatoIniciado());
        atualizarDepoisDeAlteracao();
    }

    private void atualizarDepoisDeAlteracao() {
        equipaService.atualizarEstatisticasDaEquipa(equipa);
        carregarJogadores();
        preencherTabela();
    }

    private void guardarEFechar() {
        equipaService.atualizarEstatisticasDaEquipa(equipa);

        if (onGuardar != null) {
            onGuardar.run();
        }

        dispose();
    }

    private void carregarJogadores() {
        jogadoresEquipa = jogadorService.listarPorEquipa(equipa.getNome(), equipa.getCampeonato());
    }

    private void atualizarSubtituloTabela() {
        if (subtituloTabela != null) {
            subtituloTabela.setText(textoContagemJogadores());
        }
    }

    private String textoContagemJogadores() {
        return contarAtivos() + " jogadores ativos, " + jogadoresEquipa.size() + " registados";
    }

    private int contarAtivos() {
        int total = 0;

        for (Jogador jogador : jogadoresEquipa) {
            if (jogador.isAtivo()) {
                total++;
            }
        }

        return total;
    }

    private void validarCamposAdicionar() {
        if (campoNome.getText().trim().isEmpty()
                || campoNumero.getText().trim().isEmpty()
                || campoPeso.getText().trim().isEmpty()
                || campoAltura.getText().trim().isEmpty()
                || campoDataNascimento.getText().trim().isEmpty()
                || campoPaisOrigem.getText().trim().isEmpty()
                || campoCidadeNascimento.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos do jogador devem ser preenchidos.");
        }
    }

    private LocalDate parseData(String valor) {
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException ignored) {
            return LocalDate.parse(valor, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    }

    private double normalizarAltura(String valor) {
        double altura = Double.parseDouble(valor.replace(",", "."));
        return altura > 3 ? altura / 100 : altura;
    }

    private void limparFormularioAdicionar() {
        campoNome.setText("");
        campoNumero.setText("");
        campoPeso.setText("");
        campoAltura.setText("");
        campoDataNascimento.setText("");
        campoPaisOrigem.setText(equipa.getPais());
        campoCidadeNascimento.setText(equipa.getCidade());
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro de validacao",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private boolean campeonatoIniciado() {
        return false;
    }

    private class BotaoRemoverRenderer extends RoundedButton implements javax.swing.table.TableCellRenderer {

        private BotaoRemoverRenderer() {
            super("Remover", new Color(254, 226, 226), Tema.COR_ERRO, 8);
            setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            setText(String.valueOf(value));
            setForeground("Inativo".equals(value) ? Tema.COR_TEXTO_SECUNDARIO : Tema.COR_ERRO);
            setBackground("Inativo".equals(value) ? new Color(241, 245, 249) : new Color(254, 226, 226));
            return this;
        }
    }

    private class BotaoRemoverEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {

        private final JButton button;
        private final JTable table;
        private int linhaModelo;

        private BotaoRemoverEditor(JTable table) {
            this.table = table;
            this.button = new RoundedButton("Remover", new Color(254, 226, 226), Tema.COR_ERRO, 8);
            this.button.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 11));
            this.button.addActionListener(e -> {
                fireEditingStopped();
                removerJogador(linhaModelo);
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column
        ) {
            linhaModelo = this.table.convertRowIndexToModel(row);
            button.setText(String.valueOf(value));
            button.setEnabled(!"Inativo".equals(value));
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Remover";
        }
    }

    private static class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int radius;

        private RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 1;
            insets.left = 1;
            insets.bottom = 1;
            insets.right = 1;
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
