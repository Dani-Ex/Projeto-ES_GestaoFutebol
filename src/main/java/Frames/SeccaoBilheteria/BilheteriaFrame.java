package Frames.SeccaoBilheteria;

import Design.MenuLateral;
import Design.RoundedBorder;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Models.Bilhete;
import Models.BilheteriaService;
import Models.Jogo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BilheteriaFrame extends JFrame {

    private final BilheteriaService bilheteriaService;
    private final List<Jogo> jogosVisiveis;

    private MenuLateral menuLateral;
    private boolean menuAberto;

    private JTable tabelaJogos;
    private DefaultTableModel modeloJogos;

    private JComboBox<String> comboTipo;
    private JSpinner spinnerQuantidade;
    private JComboBox<String> comboPagamento;
    private JButton btnComprar;

    private JLabel lblJogo;
    private JLabel lblPreco;
    private JLabel lblDisponiveis;
    private JLabel lblTotal;

    public BilheteriaFrame() {
        bilheteriaService = new BilheteriaService();
        jogosVisiveis = new ArrayList<>();

        setTitle("Bilheteria");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1180, 700));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Tema.COR_FUNDO);
        main.setBorder(new EmptyBorder(
                Tema.PADDING_JANELA.top,
                Tema.PADDING_JANELA.left,
                Tema.PADDING_JANELA.bottom,
                Tema.PADDING_JANELA.right
        ));

        setContentPane(main);

        menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        main.add(menuLateral, BorderLayout.WEST);
        main.add(criarConteudo(main), BorderLayout.CENTER);

        atualizarTabelaJogos();
        atualizarResumoCompra();

        setVisible(true);
    }

    private JPanel criarConteudo(JPanel main) {
        JPanel content = new JPanel(new BorderLayout(0, Tema.ESPACAMENTO_GRANDE));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(0, 25, 0, 25));

        content.add(criarTopo(main), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(Tema.ESPACAMENTO_MEDIO, 0));
        centro.setOpaque(false);

        centro.add(criarCardJogos(), BorderLayout.CENTER);
        centro.add(criarCardCompra(), BorderLayout.EAST);

        content.add(centro, BorderLayout.CENTER);

        return content;
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

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Bilheteria");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel(
                "Compra vários bilhetes numa transação. Clica no estádio para editar um jogo."
        );
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        esquerda.setOpaque(false);
        esquerda.add(btnMenu);
        esquerda.add(textos);

        RoundedButton btnAdicionar = new RoundedButton(
                "+ Adicionar jogo",
                Tema.COR_INFO,
                Color.WHITE,
                12
        );

        btnAdicionar.setPreferredSize(new Dimension(170, 40));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.addActionListener(e -> abrirDialogoAdicionarJogo());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        direita.setOpaque(false);
        direita.add(btnAdicionar);

        topo.add(esquerda, BorderLayout.WEST);
        topo.add(direita, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarCardJogos() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());

        card.setBorder(new EmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));

        JLabel titulo = new JLabel("Jogos disponíveis");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {
                "Data", "Hora", "Jogo", "Estádio",
                "Normal", "VIP", "Premium", "Estado"
        };

        modeloJogos = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaJogos = new JTable(modeloJogos);
        TableStyle.aplicarTabelaLimpa(tabelaJogos, 0);
        tabelaJogos.setRowHeight(40);
        tabelaJogos.setAutoCreateRowSorter(true);

        tabelaJogos.getColumnModel().getColumn(2).setPreferredWidth(190);
        tabelaJogos.getColumnModel().getColumn(3).setPreferredWidth(180);
        tabelaJogos.getColumnModel().getColumn(4).setPreferredWidth(70);
        tabelaJogos.getColumnModel().getColumn(5).setPreferredWidth(55);
        tabelaJogos.getColumnModel().getColumn(6).setPreferredWidth(80);
        tabelaJogos.getColumnModel().getColumn(7).setPreferredWidth(85);

        tabelaJogos.getColumnModel()
                .getColumn(7)
                .setCellRenderer(criarRendererEstado());

        tabelaJogos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabelaJogos.rowAtPoint(e.getPoint());
                int coluna = tabelaJogos.columnAtPoint(e.getPoint());

                if (linha == -1) {
                    return;
                }

                tabelaJogos.setRowSelectionInterval(linha, linha);

                if (coluna == 3) {
                    abrirDialogoEditarJogo(jogoSelecionado());
                    return;
                }

                atualizarResumoCompra();
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaJogos);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);
        scroll.setBorder(new EmptyBorder(16, 0, 0, 0));

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardCompra() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setPreferredSize(new Dimension(340, 0));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Comprar bilhetes");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblJogo = criarLabelResumo("Seleciona um jogo na tabela");
        lblPreco = criarLabelResumo("Preço unitário: -");
        lblDisponiveis = criarLabelResumo("Disponíveis: -");
        lblTotal = criarLabelTotal("Total: 0,00 €");

        comboTipo = new JComboBox<>(new String[]{
                BilheteriaService.TIPO_NORMAL,
                BilheteriaService.TIPO_VIP,
                BilheteriaService.TIPO_PREMIUM
        });

        configurarCombo(comboTipo);
        comboTipo.addActionListener(e -> atualizarResumoCompra());

        spinnerQuantidade = new JSpinner(
                new SpinnerNumberModel(1, 1, 1, 1)
        );

        configurarSpinner(spinnerQuantidade);
        spinnerQuantidade.setEnabled(false);
        spinnerQuantidade.addChangeListener(e -> atualizarTotal());

        comboPagamento = new JComboBox<>(new String[]{
                "Cartão",
                "MB WAY",
                "Multibanco",
                "Dinheiro"
        });

        configurarCombo(comboPagamento);

        btnComprar = new RoundedButton(
                "Confirmar compra",
                Tema.COR_SUCESSO,
                Color.WHITE,
                12
        );

        btnComprar.setPreferredSize(new Dimension(210, 42));
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setEnabled(false);
        btnComprar.addActionListener(e -> confirmarCompra());

        content.add(titulo);
        content.add(Box.createVerticalStrut(20));
        content.add(lblJogo);
        content.add(Box.createVerticalStrut(8));
        content.add(lblPreco);
        content.add(Box.createVerticalStrut(8));
        content.add(lblDisponiveis);
        content.add(Box.createVerticalStrut(20));

        content.add(criarCampoComLabel("Tipo de bilhete", comboTipo));
        content.add(Box.createVerticalStrut(14));

        content.add(criarCampoComLabel("Quantidade", spinnerQuantidade));
        content.add(Box.createVerticalStrut(14));

        content.add(criarCampoComLabel("Pagamento", comboPagamento));
        content.add(Box.createVerticalStrut(20));

        content.add(lblTotal);
        content.add(Box.createVerticalStrut(20));

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        painelBotao.setOpaque(false);
        painelBotao.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBotao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        painelBotao.add(btnComprar);

        content.add(painelBotao);

        card.add(content, BorderLayout.NORTH);

        return card;
    }

    private void abrirDialogoAdicionarJogo() {
        JTextField campoData = criarCampoFormulario("");
        JTextField campoHora = criarCampoFormulario("");
        JTextField campoEquipaA = criarCampoFormulario("");
        JTextField campoEquipaB = criarCampoFormulario("");
        JTextField campoEstadio = criarCampoFormulario("");
        JTextField campoFase = criarCampoFormulario("Fase de grupos");
        JComboBox<String> comboEstado = criarComboEstado("Agendado");
        JTextField campoResultado = criarCampoFormulario("-");
        JTextField campoCampeonato = criarCampoFormulario("Campeonato Principal");
        JTextField campoNormal = criarCampoFormulario("500");
        JTextField campoVip = criarCampoFormulario("100");
        JTextField campoPremium = criarCampoFormulario("50");

        JPanel formulario = criarFormularioJogo(
                campoData,
                campoHora,
                campoEquipaA,
                campoEquipaB,
                campoEstadio,
                campoFase,
                comboEstado,
                campoResultado,
                campoCampeonato,
                campoNormal,
                campoVip,
                campoPremium
        );

        int opcao = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Adicionar jogo disponível",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            bilheteriaService.adicionarJogoDisponivel(
                    campoData.getText(),
                    campoHora.getText(),
                    campoEquipaA.getText(),
                    campoEquipaB.getText(),
                    campoEstadio.getText(),
                    campoFase.getText(),
                    String.valueOf(comboEstado.getSelectedItem()),
                    campoResultado.getText(),
                    campoCampeonato.getText(),
                    inteiroPositivoOuZero(campoNormal, "Lugares Normal"),
                    inteiroPositivoOuZero(campoVip, "Lugares VIP"),
                    inteiroPositivoOuZero(campoPremium, "Lugares Premium")
            );

            atualizarTabelaJogos();
            atualizarResumoCompra();

            JOptionPane.showMessageDialog(
                    this,
                    "Jogo adicionado e guardado com sucesso.",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void abrirDialogoEditarJogo(Jogo jogo) {
        if (jogo == null) {
            return;
        }

        JTextField campoData = criarCampoFormulario(jogo.getData());
        JTextField campoHora = criarCampoFormulario(jogo.getHora());
        JTextField campoEquipaA = criarCampoFormulario(jogo.getEquipaA());
        JTextField campoEquipaB = criarCampoFormulario(jogo.getEquipaB());
        JTextField campoEstadio = criarCampoFormulario(jogo.getEstadio());
        JTextField campoFase = criarCampoFormulario(jogo.getFaseGrupo());
        JComboBox<String> comboEstado = criarComboEstado(jogo.getEstado());
        JTextField campoResultado = criarCampoFormulario(jogo.getResultado());
        JTextField campoCampeonato = criarCampoFormulario(jogo.getCampeonato());

        JTextField campoNormal = criarCampoFormulario(String.valueOf(
                bilheteriaService.getDisponiveis(
                        jogo.getId(),
                        BilheteriaService.TIPO_NORMAL
                )
        ));

        JTextField campoVip = criarCampoFormulario(String.valueOf(
                bilheteriaService.getDisponiveis(
                        jogo.getId(),
                        BilheteriaService.TIPO_VIP
                )
        ));

        JTextField campoPremium = criarCampoFormulario(String.valueOf(
                bilheteriaService.getDisponiveis(
                        jogo.getId(),
                        BilheteriaService.TIPO_PREMIUM
                )
        ));

        JPanel formulario = criarFormularioJogo(
                campoData,
                campoHora,
                campoEquipaA,
                campoEquipaB,
                campoEstadio,
                campoFase,
                comboEstado,
                campoResultado,
                campoCampeonato,
                campoNormal,
                campoVip,
                campoPremium
        );

        int opcao = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Editar jogo - " + jogo.getNomeJogo(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcao != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            bilheteriaService.atualizarJogoDisponivel(
                    jogo,
                    campoData.getText(),
                    campoHora.getText(),
                    campoEquipaA.getText(),
                    campoEquipaB.getText(),
                    campoEstadio.getText(),
                    campoFase.getText(),
                    String.valueOf(comboEstado.getSelectedItem()),
                    campoResultado.getText(),
                    campoCampeonato.getText(),
                    inteiroPositivoOuZero(
                            campoNormal,
                            "Lugares Normal disponíveis"
                    ),
                    inteiroPositivoOuZero(
                            campoVip,
                            "Lugares VIP disponíveis"
                    ),
                    inteiroPositivoOuZero(
                            campoPremium,
                            "Lugares Premium disponíveis"
                    )
            );

            tabelaJogos.clearSelection();
            atualizarTabelaJogos();
            atualizarResumoCompra();

            JOptionPane.showMessageDialog(
                    this,
                    "Jogo e lugares disponíveis atualizados.",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarErro(e.getMessage());
        }
    }

    private JPanel criarFormularioJogo(JTextField campoData,
                                       JTextField campoHora,
                                       JTextField campoEquipaA,
                                       JTextField campoEquipaB,
                                       JTextField campoEstadio,
                                       JTextField campoFase,
                                       JComboBox<String> comboEstado,
                                       JTextField campoResultado,
                                       JTextField campoCampeonato,
                                       JTextField campoNormal,
                                       JTextField campoVip,
                                       JTextField campoPremium) {
        JPanel formulario = new JPanel(new GridLayout(0, 2, 14, 10));
        formulario.setPreferredSize(new Dimension(640, 360));
        formulario.setBorder(new EmptyBorder(8, 8, 8, 8));

        adicionarCampoFormulario(formulario, "Data (AAAA-MM-DD)", campoData);
        adicionarCampoFormulario(formulario, "Hora (HH:MM)", campoHora);
        adicionarCampoFormulario(formulario, "Equipa casa", campoEquipaA);
        adicionarCampoFormulario(formulario, "Equipa visitante", campoEquipaB);
        adicionarCampoFormulario(formulario, "Estádio", campoEstadio);
        adicionarCampoFormulario(formulario, "Fase / grupo", campoFase);
        adicionarCampoFormulario(formulario, "Estado", comboEstado);
        adicionarCampoFormulario(formulario, "Resultado", campoResultado);
        adicionarCampoFormulario(formulario, "Campeonato", campoCampeonato);
        adicionarCampoFormulario(formulario, "Lugares Normal disponíveis", campoNormal);
        adicionarCampoFormulario(formulario, "Lugares VIP disponíveis", campoVip);
        adicionarCampoFormulario(
                formulario,
                "Lugares Premium disponíveis",
                campoPremium
        );

        return formulario;
    }

    private void adicionarCampoFormulario(JPanel formulario,
                                          String etiqueta,
                                          JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 4));
        painel.setOpaque(false);

        JLabel label = new JLabel(etiqueta);
        label.setFont(Tema.FONTE_TEXTO_PEQUENO);
        label.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        campo.setPreferredSize(new Dimension(100, 34));

        painel.add(label, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);

        formulario.add(painel);
    }

    private JTextField criarCampoFormulario(String valor) {
        JTextField campo = new JTextField(valor == null ? "" : valor);

        campo.setFont(Tema.FONTE_TEXTO);
        campo.setBackground(Tema.COR_INPUT);
        campo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Tema.COR_LINHA, 10),
                new EmptyBorder(7, 10, 7, 10)
        ));

        return campo;
    }

    private JComboBox<String> criarComboEstado(String estadoAtual) {
        JComboBox<String> combo = new JComboBox<>(new String[]{
                "Agendado",
                "Em curso",
                "Realizado",
                "Cancelado"
        });

        configurarCombo(combo);
        combo.setSelectedItem(estadoAtual);

        if (combo.getSelectedItem() == null) {
            combo.setSelectedItem("Agendado");
        }

        return combo;
    }

    private int inteiroPositivoOuZero(JTextField campo, String nomeCampo) {
        try {
            int valor = Integer.parseInt(campo.getText().trim());

            if (valor < 0) {
                throw new IllegalArgumentException(
                        nomeCampo + " não pode ser negativo."
                );
            }

            return valor;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    nomeCampo + " deve ser um número inteiro."
            );
        }
    }

    private JTextField criarCampo() {
        JTextField campo = new JTextField();

        campo.setFont(Tema.FONTE_TEXTO);
        campo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        campo.setBackground(Tema.COR_INPUT);

        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Tema.COR_LINHA, 12),
                new EmptyBorder(8, 12, 8, 12)
        ));

        return campo;
    }

    private void configurarCombo(JComboBox<String> combo) {
        combo.setFont(Tema.FONTE_TEXTO);
        combo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        combo.setBackground(Tema.COR_INPUT);
        combo.setBorder(new RoundedBorder(Tema.COR_LINHA, 12));
        combo.setFocusable(false);
    }

    private void configurarSpinner(JSpinner spinner) {
        spinner.setFont(Tema.FONTE_TEXTO);
        spinner.setPreferredSize(new Dimension(100, 38));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JSpinner.DefaultEditor editor =
                (JSpinner.DefaultEditor) spinner.getEditor();

        editor.getTextField().setFont(Tema.FONTE_TEXTO);
        editor.getTextField().setForeground(Tema.COR_TEXTO_PRINCIPAL);
        editor.getTextField().setBackground(Tema.COR_INPUT);
        editor.getTextField().setHorizontalAlignment(SwingConstants.LEFT);
    }

    private JLabel criarLabelResumo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(Tema.FONTE_CARD_TITULO);
        label.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JLabel criarLabelTotal(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(Tema.FONTE_CARD_VALOR_GRANDE);
        label.setForeground(Tema.COR_SUCESSO);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    private JPanel criarCampoComLabel(String label, JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 7));
        painel.setOpaque(false);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Tema.FONTE_CARD_TITULO);
        lbl.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);

        campo.setPreferredSize(new Dimension(100, 38));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        painel.add(lbl, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);

        return painel;
    }

    private DefaultTableCellRenderer criarRendererEstado() {
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

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(Tema.FONTE_CARD_TITULO);
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(0, 8, 0, 8));

                if (isSelected) {
                    label.setBackground(Tema.COR_SELECAO_NEUTRA);
                    label.setForeground(Tema.COR_TEXTO_PRINCIPAL);
                } else {
                    label.setBackground(Tema.COR_CARD);
                    label.setForeground(Tema.COR_SUCESSO);
                }

                return label;
            }
        };
    }

    private void atualizarTabelaJogos() {
        modeloJogos.setRowCount(0);
        jogosVisiveis.clear();

        for (Jogo jogo : bilheteriaService.listarJogosParaVenda()) {
            jogosVisiveis.add(jogo);

            modeloJogos.addRow(new Object[]{
                    jogo.getData(),
                    jogo.getHora(),
                    jogo.getNomeJogo(),
                    jogo.getEstadio(),

                    bilheteriaService.getDisponiveis(
                            jogo.getId(),
                            BilheteriaService.TIPO_NORMAL
                    ),

                    bilheteriaService.getDisponiveis(
                            jogo.getId(),
                            BilheteriaService.TIPO_VIP
                    ),

                    bilheteriaService.getDisponiveis(
                            jogo.getId(),
                            BilheteriaService.TIPO_PREMIUM
                    ),

                    jogo.getEstado()
            });
        }
    }

    private Jogo jogoSelecionado() {
        int linhaSelecionada = tabelaJogos.getSelectedRow();

        if (linhaSelecionada < 0) {
            return null;
        }

        int linhaModelo = tabelaJogos.convertRowIndexToModel(linhaSelecionada);

        if (linhaModelo < 0 || linhaModelo >= jogosVisiveis.size()) {
            return null;
        }

        return jogosVisiveis.get(linhaModelo);
    }

    private void atualizarResumoCompra() {
        Jogo jogo = jogoSelecionado();

        if (jogo == null) {
            lblJogo.setText("Seleciona um jogo na tabela");
            lblPreco.setText("Preço unitário: -");
            lblDisponiveis.setText("Disponíveis: -");
            lblTotal.setText("Total: 0,00 €");

            spinnerQuantidade.setModel(
                    new SpinnerNumberModel(1, 1, 1, 1)
            );

            spinnerQuantidade.setEnabled(false);
            btnComprar.setEnabled(false);

            return;
        }

        String tipo = String.valueOf(comboTipo.getSelectedItem());

        int disponiveis = bilheteriaService.getDisponiveis(
                jogo.getId(),
                tipo
        );

        double preco = bilheteriaService.getPreco(tipo);

        lblJogo.setText(jogo.getNomeJogo());
        lblPreco.setText("Preço unitário: " + formatarEuros(preco));
        lblDisponiveis.setText(
                "Disponíveis " + tipo + ": " + disponiveis
        );

        if (disponiveis <= 0) {
            spinnerQuantidade.setModel(
                    new SpinnerNumberModel(0, 0, 0, 1)
            );

            spinnerQuantidade.setEnabled(false);
            btnComprar.setEnabled(false);
            lblTotal.setText("Total: 0,00 €");

            return;
        }

        int quantidadeAtual = 1;

        if (spinnerQuantidade.getValue() instanceof Number) {
            quantidadeAtual =
                    ((Number) spinnerQuantidade.getValue()).intValue();
        }

        quantidadeAtual = Math.max(
                1,
                Math.min(quantidadeAtual, disponiveis)
        );

        spinnerQuantidade.setModel(
                new SpinnerNumberModel(
                        quantidadeAtual,
                        1,
                        disponiveis,
                        1
                )
        );

        spinnerQuantidade.setEnabled(true);
        btnComprar.setEnabled(true);
        btnComprar.setForeground(Color.WHITE);

        atualizarTotal();
    }

    private void atualizarTotal() {
        Jogo jogo = jogoSelecionado();

        if (jogo == null || !spinnerQuantidade.isEnabled()) {
            lblTotal.setText("Total: 0,00 €");
            return;
        }

        int quantidade =
                ((Number) spinnerQuantidade.getValue()).intValue();

        String tipo = String.valueOf(comboTipo.getSelectedItem());

        double total = bilheteriaService.getPreco(tipo) * quantidade;

        lblTotal.setText("Total: " + formatarEuros(total));
    }

    private void confirmarCompra() {
        try {
            Jogo jogo = jogoSelecionado();

            if (jogo == null) {
                throw new IllegalArgumentException(
                        "Seleciona primeiro um jogo na tabela."
                );
            }

            String tipo = String.valueOf(comboTipo.getSelectedItem());

            int quantidade =
                    ((Number) spinnerQuantidade.getValue()).intValue();

            Bilhete compra = bilheteriaService.comprarBilhetes(
                    jogo,
                    tipo,
                    quantidade,
                    String.valueOf(comboPagamento.getSelectedItem())
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Compra realizada com sucesso.\n\n"
                            + "Transação: " + compra.getIdTransacao() + "\n"
                            + "Jogo: " + jogo.getNomeJogo() + "\n"
                            + "Bilhetes: " + compra.getQuantidade()
                            + " " + compra.getTipo() + "\n"
                            + "Total: " + formatarEuros(compra.getTotal()),
                    "Compra confirmada",
                    JOptionPane.INFORMATION_MESSAGE
            );

            atualizarTabelaJogos();
            atualizarResumoCompra();

        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String formatarEuros(double valor) {
        return String.format("%.2f €", valor).replace('.', ',');
    }
}