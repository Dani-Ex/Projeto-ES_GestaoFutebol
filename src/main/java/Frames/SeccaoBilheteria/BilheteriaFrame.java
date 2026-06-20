package Frames.SeccaoBilheteria;

import Design.MenuLateral;
import Design.ModernScrollBarUI;
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

/**
 * Bilheteria usa apenas componentes do pacote Design.
 * Nesta secção não se criam jogos nem estádios: são sempre escolhidos os já existentes.
 */
public class BilheteriaFrame extends JFrame {

    private final BilheteriaService bilheteriaService;
    private final List<Jogo> jogosVisiveis = new ArrayList<>();
    private final List<Bilhete> comprasVisiveis = new ArrayList<>();

    private MenuLateral menuLateral;
    private boolean menuAberto;

    private JTable tabelaJogos;
    private JTable tabelaCompras;
    private DefaultTableModel modeloJogos;
    private DefaultTableModel modeloCompras;

    private JComboBox<String> comboComprador;
    private JComboBox<String> comboTipo;
    private JSpinner spinnerQuantidade;
    private JComboBox<String> comboPagamento;
    private RoundedButton btnComprar;

    private JLabel lblJogo;
    private JLabel lblPreco;
    private JLabel lblDisponiveis;
    private JLabel lblTotal;

    public BilheteriaFrame() {
        bilheteriaService = new BilheteriaService();

        setTitle("Bilheteria");
        setSize(1280, 820);
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

        JScrollPane pagina = new JScrollPane(criarConteudo(main));
        pagina.setBorder(null);
        pagina.getViewport().setBackground(Tema.COR_FUNDO);
        pagina.getVerticalScrollBar().setUnitIncrement(18);
        ModernScrollBarUI.aplicar(pagina);

        main.add(pagina, BorderLayout.CENTER);

        atualizarTudo(null);
        setVisible(true);
    }

    private JPanel criarConteudo(JPanel main) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Tema.COR_FUNDO);
        content.setBorder(new EmptyBorder(0, 25, 25, 25));

        content.add(criarTopo(main));
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_GRANDE));

        JPanel linhaSuperior = new JPanel(new BorderLayout(Tema.ESPACAMENTO_MEDIO, 0));
        linhaSuperior.setOpaque(false);
        linhaSuperior.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Altura suficiente para mostrar todos os campos e o botão "Confirmar compra".
        linhaSuperior.setMaximumSize(new Dimension(Integer.MAX_VALUE, 560));

        linhaSuperior.add(criarCardJogos(), BorderLayout.CENTER);
        linhaSuperior.add(criarCardCompra(), BorderLayout.EAST);

        content.add(linhaSuperior);
        content.add(Box.createVerticalStrut(Tema.ESPACAMENTO_GRANDE));
        content.add(criarCardCompras());

        return content;
    }

    private JPanel criarTopo(JPanel main) {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setAlignmentX(Component.LEFT_ALIGNMENT);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

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
                "Configura jogos existentes, vende bilhetes e corrige compras antes do início do jogo."
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
                "+ Adicionar à bilheteria",
                Tema.COR_INFO,
                Color.WHITE,
                12
        );
        btnAdicionar.setPreferredSize(new Dimension(195, 40));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.addActionListener(e -> abrirDialogoAdicionarJogo());

        topo.add(esquerda, BorderLayout.WEST);
        topo.add(btnAdicionar, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarCardJogos() {
        RoundedPanel card = criarCardBase();
        card.setLayout(new BorderLayout());

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Jogos com bilheteira");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel ajuda = new JLabel("Duplo clique para gerir jogo, estádio e preços.");
        ajuda.setFont(Tema.FONTE_TEXTO_PEQUENO);
        ajuda.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        cabecalho.add(titulo, BorderLayout.NORTH);
        cabecalho.add(ajuda, BorderLayout.SOUTH);

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
        TableStyle.aplicarTabelaLimpa(tabelaJogos, 2);
        tabelaJogos.setRowHeight(38);
        tabelaJogos.setAutoCreateRowSorter(true);
        tabelaJogos.setToolTipText("Duplo clique para gerir este jogo de bilheteira.");

        tabelaJogos.getColumnModel().getColumn(0).setPreferredWidth(85);
        tabelaJogos.getColumnModel().getColumn(1).setPreferredWidth(60);
        tabelaJogos.getColumnModel().getColumn(2).setPreferredWidth(185);
        tabelaJogos.getColumnModel().getColumn(3).setPreferredWidth(165);
        tabelaJogos.getColumnModel().getColumn(4).setPreferredWidth(65);
        tabelaJogos.getColumnModel().getColumn(5).setPreferredWidth(55);
        tabelaJogos.getColumnModel().getColumn(6).setPreferredWidth(75);
        tabelaJogos.getColumnModel().getColumn(7).setPreferredWidth(85);
        tabelaJogos.getColumnModel().getColumn(7).setCellRenderer(criarRendererEstado());

        tabelaJogos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabelaJogos.rowAtPoint(e.getPoint());

                if (linha < 0) {
                    return;
                }

                tabelaJogos.setRowSelectionInterval(linha, linha);

                if (e.getClickCount() == 2) {
                    abrirDialogoGerirJogo(jogoSelecionado());
                } else {
                    atualizarResumoCompra();
                    atualizarTabelaCompras();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaJogos);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);
        scroll.setBorder(new EmptyBorder(16, 0, 0, 0));

        card.add(cabecalho, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardCompra() {
        RoundedPanel card = criarCardBase();

        // Evita que o formulário de compra seja cortado antes do botão.
        card.setPreferredSize(new Dimension(330, 520));
        card.setMinimumSize(new Dimension(330, 520));
        card.setLayout(new BorderLayout());

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

        comboComprador = criarComboCompradores(null);

        comboTipo = new JComboBox<>(new String[]{
                BilheteriaService.TIPO_NORMAL,
                BilheteriaService.TIPO_VIP,
                BilheteriaService.TIPO_PREMIUM
        });
        configurarCombo(comboTipo);
        comboTipo.addActionListener(e -> atualizarResumoCompra());

        spinnerQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        configurarSpinner(spinnerQuantidade);
        spinnerQuantidade.setEnabled(false);
        spinnerQuantidade.addChangeListener(e -> atualizarTotal());

        comboPagamento = new JComboBox<>(new String[]{
                "Cartão", "MB WAY", "Multibanco", "Dinheiro"
        });
        configurarCombo(comboPagamento);

        btnComprar = new RoundedButton(
                "Confirmar compra",
                Tema.COR_SUCESSO,
                Color.WHITE,
                12
        );

        btnComprar.setFont(Tema.FONTE_TEXTO);
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setPreferredSize(new Dimension(1, 52));
        btnComprar.setMinimumSize(new Dimension(1, 52));
        btnComprar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btnComprar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnComprar.setEnabled(false);
        btnComprar.addActionListener(e -> confirmarCompra());

        content.add(titulo);
        content.add(Box.createVerticalStrut(18));
        content.add(lblJogo);
        content.add(Box.createVerticalStrut(8));
        content.add(lblPreco);
        content.add(Box.createVerticalStrut(8));
        content.add(lblDisponiveis);
        content.add(Box.createVerticalStrut(18));
        content.add(criarCampoComLabel("Comprador", comboComprador));
        content.add(Box.createVerticalStrut(14));
        content.add(criarCampoComLabel("Tipo de bilhete", comboTipo));
        content.add(Box.createVerticalStrut(14));
        content.add(criarCampoComLabel("Quantidade", spinnerQuantidade));
        content.add(Box.createVerticalStrut(14));
        content.add(criarCampoComLabel("Pagamento", comboPagamento));
        content.add(Box.createVerticalStrut(18));
        content.add(lblTotal);
        content.add(Box.createVerticalStrut(18));

        JPanel linhaBotaoComprar = new JPanel(new BorderLayout());
        linhaBotaoComprar.setOpaque(false);
        linhaBotaoComprar.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaBotaoComprar.setPreferredSize(new Dimension(1, 52));
        linhaBotaoComprar.setMinimumSize(new Dimension(1, 52));
        linhaBotaoComprar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        linhaBotaoComprar.add(btnComprar, BorderLayout.CENTER);

        content.add(linhaBotaoComprar);

        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardCompras() {
        RoundedPanel card = criarCardBase();
        card.setLayout(new BorderLayout());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Compras registadas do jogo selecionado");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel ajuda = new JLabel("Duplo clique para corrigir quantidade, tipo, pagamento ou jogo. Só antes do início.");
        ajuda.setFont(Tema.FONTE_TEXTO_PEQUENO);
        ajuda.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(ajuda);

        RoundedButton btnEditar = new RoundedButton("Editar compra", Tema.COR_INFO, Color.WHITE, 12);
        btnEditar.setPreferredSize(new Dimension(130, 38));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.addActionListener(e -> abrirDialogoEditarCompra(compraSelecionada()));

        RoundedButton btnEliminar = new RoundedButton("Eliminar", Tema.COR_ERRO, Color.WHITE, 12);
        btnEliminar.setPreferredSize(new Dimension(100, 38));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> eliminarCompraSelecionada());

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acoes.setOpaque(false);
        acoes.add(btnEditar);
        acoes.add(btnEliminar);

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(acoes, BorderLayout.EAST);

        String[] colunas = {
                "Transação", "Comprador", "Tipo", "Qtd.", "Preço", "Total", "Pagamento", "Data"
        };
        modeloCompras = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaCompras = new JTable(modeloCompras);
        TableStyle.aplicarTabelaLimpa(tabelaCompras, 0);
        tabelaCompras.setRowHeight(36);
        tabelaCompras.setAutoCreateRowSorter(true);
        tabelaCompras.setToolTipText("Duplo clique para editar esta compra.");

        tabelaCompras.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabelaCompras.rowAtPoint(e.getPoint());
                if (linha >= 0) {
                    tabelaCompras.setRowSelectionInterval(linha, linha);
                    if (e.getClickCount() == 2) {
                        abrirDialogoEditarCompra(compraSelecionada());
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaCompras);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);
        scroll.setBorder(new EmptyBorder(16, 0, 0, 0));

        card.add(cabecalho, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private RoundedPanel criarCardBase() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setBorder(new EmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));
        return card;
    }

    private void abrirDialogoAdicionarJogo() {
        List<Jogo> jogos = bilheteriaService.listarJogosDisponiveisParaAdicionar();

        if (jogos.isEmpty()) {
            mostrarErro("Não existem jogos agendados disponíveis para adicionar à bilheteira.");
            return;
        }

        JDialog dialog = criarDialogo("Adicionar jogo à bilheteira", 650, 470);

        JComboBox<Jogo> comboJogos = criarComboJogos(jogos, null);
        JComboBox<String> comboEstadios = criarComboEstadios(null);
        JSpinner precoNormal = criarSpinnerPreco(12.50);
        JSpinner precoVip = criarSpinnerPreco(25.00);
        JSpinner precoPremium = criarSpinnerPreco(40.00);

        comboJogos.addActionListener(e -> {
            Jogo escolhido = (Jogo) comboJogos.getSelectedItem();
            if (escolhido != null) {
                comboEstadios.setSelectedItem(escolhido.getEstadio());
            }
        });

        Jogo primeiro = (Jogo) comboJogos.getSelectedItem();
        if (primeiro != null) {
            comboEstadios.setSelectedItem(primeiro.getEstadio());
        }

        JPanel formulario = criarFormularioConfiguracao(
                comboJogos,
                comboEstadios,
                precoNormal,
                precoVip,
                precoPremium,
                "Jogo existente",
                "Estádio existente"
        );

        RoundedButton btnCancelar = new RoundedButton("Cancelar", Tema.COR_BOTAO_SECUNDARIO, Tema.COR_TEXTO_PRINCIPAL, 12);
        RoundedButton btnGuardar = new RoundedButton("Adicionar", Tema.COR_INFO, Color.WHITE, 12);
        btnGuardar.setForeground(Color.WHITE);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                bilheteriaService.adicionarJogoABilheteira(
                        (Jogo) comboJogos.getSelectedItem(),
                        String.valueOf(comboEstadios.getSelectedItem()),
                        valorSpinnerPreco(precoNormal),
                        valorSpinnerPreco(precoVip),
                        valorSpinnerPreco(precoPremium)
                );

                dialog.dispose();
                atualizarTudo((Jogo) comboJogos.getSelectedItem());
            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        mostrarDialogoComAcoes(dialog, formulario, btnCancelar, btnGuardar, null);
    }

    private void abrirDialogoGerirJogo(Jogo jogoOriginal) {
        if (jogoOriginal == null) {
            return;
        }

        List<Jogo> jogos = new ArrayList<>();
        jogos.add(jogoOriginal);
        for (Jogo jogo : bilheteriaService.listarJogosDisponiveisParaAdicionar()) {
            jogos.add(jogo);
        }

        JDialog dialog = criarDialogo("Gerir jogo de bilheteira", 680, 500);

        JComboBox<Jogo> comboJogos = criarComboJogos(jogos, jogoOriginal);
        JComboBox<String> comboEstadios = criarComboEstadios(jogoOriginal.getEstadio());
        JSpinner precoNormal = criarSpinnerPreco(
                bilheteriaService.getPreco(jogoOriginal.getId(), BilheteriaService.TIPO_NORMAL)
        );
        JSpinner precoVip = criarSpinnerPreco(
                bilheteriaService.getPreco(jogoOriginal.getId(), BilheteriaService.TIPO_VIP)
        );
        JSpinner precoPremium = criarSpinnerPreco(
                bilheteriaService.getPreco(jogoOriginal.getId(), BilheteriaService.TIPO_PREMIUM)
        );

        comboJogos.addActionListener(e -> {
            Jogo escolhido = (Jogo) comboJogos.getSelectedItem();
            if (escolhido != null) {
                comboEstadios.setSelectedItem(escolhido.getEstadio());
            }
        });

        JPanel formulario = criarFormularioConfiguracao(
                comboJogos,
                comboEstadios,
                precoNormal,
                precoVip,
                precoPremium,
                "Jogo da bilheteira",
                "Estádio existente"
        );

        RoundedButton btnEliminar = new RoundedButton("Eliminar", Tema.COR_ERRO, Color.WHITE, 12);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> {
            int confirmar = JOptionPane.showConfirmDialog(
                    dialog,
                    "Eliminar apenas a configuração da bilheteira?\nO jogo e o estádio não serão apagados.",
                    "Confirmar eliminação",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                bilheteriaService.removerJogoDaBilheteira(jogoOriginal);
                dialog.dispose();
                atualizarTudo(null);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        RoundedButton btnCancelar = new RoundedButton("Cancelar", Tema.COR_BOTAO_SECUNDARIO, Tema.COR_TEXTO_PRINCIPAL, 12);
        RoundedButton btnGuardar = new RoundedButton("Guardar alterações", Tema.COR_INFO, Color.WHITE, 12);
        btnGuardar.setForeground(Color.WHITE);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                Jogo novoJogo = (Jogo) comboJogos.getSelectedItem();

                bilheteriaService.atualizarJogoDaBilheteira(
                        jogoOriginal,
                        novoJogo,
                        String.valueOf(comboEstadios.getSelectedItem()),
                        valorSpinnerPreco(precoNormal),
                        valorSpinnerPreco(precoVip),
                        valorSpinnerPreco(precoPremium)
                );

                dialog.dispose();
                atualizarTudo(novoJogo);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        mostrarDialogoComAcoes(dialog, formulario, btnCancelar, btnGuardar, btnEliminar);
    }

    private void abrirDialogoEditarCompra(Bilhete compra) {
        if (compra == null) {
            mostrarErro("Seleciona uma compra primeiro.");
            return;
        }

        List<Jogo> jogos = bilheteriaService.listarJogosBilheteira();
        Jogo jogoAtual = encontrarJogo(compra.getIdJogo());

        if (jogoAtual == null) {
            mostrarErro("O jogo desta compra já não existe.");
            return;
        }

        JDialog dialog = criarDialogo("Editar compra de bilhetes", 660, 510);

        JComboBox<Jogo> comboJogos = criarComboJogos(jogos, jogoAtual);
        JComboBox<String> comboCompradorEdicao = criarComboCompradores(compra.getNomeComprador());

        JComboBox<String> comboTipoEdicao = new JComboBox<>(new String[]{
                BilheteriaService.TIPO_NORMAL,
                BilheteriaService.TIPO_VIP,
                BilheteriaService.TIPO_PREMIUM
        });
        configurarCombo(comboTipoEdicao);
        comboTipoEdicao.setSelectedItem(compra.getTipo());

        // O máximo inicial precisa aceitar pelo menos a quantidade já comprada.
        // Depois atualizarLimite() calcula o máximo real conforme jogo e zona.
        JSpinner quantidade = criarSpinnerNumero(
                compra.getQuantidade(),
                1,
                Math.max(1, compra.getQuantidade())
        );

        JComboBox<String> comboPagamentoEdicao = new JComboBox<>(new String[]{
                "Cartão", "MB WAY", "Multibanco", "Dinheiro"
        });
        configurarCombo(comboPagamentoEdicao);
        comboPagamentoEdicao.setSelectedItem(compra.getMetodoPagamento());

        JLabel lblLimite = criarLabelResumo("");
        JLabel lblNovoTotal = criarLabelTotal("");

        Runnable atualizarLimite = () -> {
            Jogo jogoEscolhido = (Jogo) comboJogos.getSelectedItem();
            String tipo = String.valueOf(comboTipoEdicao.getSelectedItem());

            if (jogoEscolhido == null) {
                return;
            }

            int maximo = bilheteriaService.getDisponiveisParaEdicao(
                    jogoEscolhido.getId(),
                    tipo,
                    compra.getIdTransacao()
            );

            int atual = Math.min(Math.max(1, valorSpinner(quantidade)), Math.max(1, maximo));
            quantidade.setModel(new SpinnerNumberModel(atual, 1, Math.max(1, maximo), 1));
            lblLimite.setText("Disponíveis após correção: " + maximo);
            lblNovoTotal.setText("Novo total: " + formatarEuros(bilheteriaService.getPreco(jogoEscolhido.getId(), tipo) * atual));
        };

        comboJogos.addActionListener(e -> atualizarLimite.run());
        comboTipoEdicao.addActionListener(e -> atualizarLimite.run());
        quantidade.addChangeListener(e -> {
            Jogo jogoEscolhido = (Jogo) comboJogos.getSelectedItem();
            String tipo = String.valueOf(comboTipoEdicao.getSelectedItem());
            if (jogoEscolhido != null) {
                lblNovoTotal.setText(
                        "Novo total: " + formatarEuros(
                                bilheteriaService.getPreco(jogoEscolhido.getId(), tipo)
                                        * valorSpinner(quantidade)
                        )
                );
            }
        });
        atualizarLimite.run();

        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(new EmptyBorder(24, 24, 8, 24));

        formulario.add(criarCampoComLabel("Comprador", comboCompradorEdicao));
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(criarCampoComLabel("Jogo da compra", comboJogos));
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(criarCampoComLabel("Tipo", comboTipoEdicao));
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(criarCampoComLabel("Quantidade", quantidade));
        formulario.add(Box.createVerticalStrut(8));
        formulario.add(lblLimite);
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(criarCampoComLabel("Pagamento", comboPagamentoEdicao));
        formulario.add(Box.createVerticalStrut(16));
        formulario.add(lblNovoTotal);

        RoundedButton btnEliminar = new RoundedButton("Eliminar", Tema.COR_ERRO, Color.WHITE, 12);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> {
            int confirmar = JOptionPane.showConfirmDialog(
                    dialog,
                    "Eliminar esta compra? A quantidade e a receita serão corrigidas.",
                    "Confirmar eliminação",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirmar != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                bilheteriaService.eliminarCompra(compra.getIdTransacao());
                dialog.dispose();
                atualizarTudo(jogoSelecionado());
            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        RoundedButton btnCancelar = new RoundedButton("Cancelar", Tema.COR_BOTAO_SECUNDARIO, Tema.COR_TEXTO_PRINCIPAL, 12);
        RoundedButton btnGuardar = new RoundedButton("Guardar compra", Tema.COR_INFO, Color.WHITE, 12);
        btnGuardar.setForeground(Color.WHITE);

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                bilheteriaService.editarCompra(
                        compra.getIdTransacao(),
                        valorComboEditavel(comboCompradorEdicao),
                        (Jogo) comboJogos.getSelectedItem(),
                        String.valueOf(comboTipoEdicao.getSelectedItem()),
                        valorSpinner(quantidade),
                        String.valueOf(comboPagamentoEdicao.getSelectedItem())
                );

                dialog.dispose();
                atualizarTudo((Jogo) comboJogos.getSelectedItem());
            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarErro(ex.getMessage());
            }
        });

        mostrarDialogoComAcoes(dialog, formulario, btnCancelar, btnGuardar, btnEliminar);
    }

    private void eliminarCompraSelecionada() {
        Bilhete compra = compraSelecionada();

        if (compra == null) {
            mostrarErro("Seleciona uma compra primeiro.");
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "Eliminar a compra selecionada? A receita e os lugares vendidos serão corrigidos.",
                "Confirmar eliminação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            bilheteriaService.eliminarCompra(compra.getIdTransacao());
            atualizarTudo(jogoSelecionado());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarErro(ex.getMessage());
        }
    }

    private JPanel criarFormularioConfiguracao(JComboBox<Jogo> comboJogos,
                                               JComboBox<String> comboEstadios,
                                               JSpinner precoNormal,
                                               JSpinner precoVip,
                                               JSpinner precoPremium,
                                               String labelJogo,
                                               String labelEstadio) {
        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBorder(new EmptyBorder(24, 24, 8, 24));

        formulario.add(criarCampoComLabel(labelJogo, comboJogos));
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(criarCampoComLabel(labelEstadio, comboEstadios));
        formulario.add(Box.createVerticalStrut(16));

        JLabel aviso = new JLabel(
                "Os lugares disponíveis são lidos automaticamente do estádio selecionado."
        );
        aviso.setFont(Tema.FONTE_TEXTO_PEQUENO);
        aviso.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        aviso.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.add(aviso);
        formulario.add(Box.createVerticalStrut(18));

        JLabel tituloPrecos = new JLabel("Preços por zona (€)");
        tituloPrecos.setFont(Tema.FONTE_CARD_TITULO);
        tituloPrecos.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        tituloPrecos.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.add(tituloPrecos);
        formulario.add(Box.createVerticalStrut(10));

        formulario.add(criarCampoComLabel("Preço Normal (€)", precoNormal));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(criarCampoComLabel("Preço VIP (€)", precoVip));
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(criarCampoComLabel("Preço Premium (€)", precoPremium));

        return formulario;
    }

    private JDialog criarDialogo(String titulo, int largura, int altura) {
        JDialog dialog = new JDialog(this, titulo, true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(largura, altura);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Tema.COR_FUNDO);
        return dialog;
    }

    private void mostrarDialogoComAcoes(JDialog dialog,
                                        JPanel formulario,
                                        RoundedButton btnCancelar,
                                        RoundedButton btnGuardar,
                                        RoundedButton btnEliminar) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel acoes = new JPanel(new BorderLayout());
        acoes.setOpaque(false);
        acoes.setBorder(new EmptyBorder(16, 24, 0, 24));

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esquerda.setOpaque(false);
        if (btnEliminar != null) {
            esquerda.add(btnEliminar);
        }

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direita.setOpaque(false);
        direita.add(btnCancelar);
        direita.add(btnGuardar);

        acoes.add(esquerda, BorderLayout.WEST);
        acoes.add(direita, BorderLayout.EAST);

        card.add(formulario, BorderLayout.CENTER);
        card.add(acoes, BorderLayout.SOUTH);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(Tema.COR_FUNDO);
        fundo.setBorder(new EmptyBorder(20, 20, 20, 20));
        fundo.add(card);

        dialog.setContentPane(fundo);
        dialog.setVisible(true);
    }

    private JComboBox<Jogo> criarComboJogos(List<Jogo> jogos, Jogo selecionado) {
        JComboBox<Jogo> combo = new JComboBox<>();
        for (Jogo jogo : jogos) {
            combo.addItem(jogo);
        }
        configurarCombo(combo);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Jogo jogo) {
                    label.setText(jogo.getData() + " " + jogo.getHora() + "  •  " + jogo.getNomeJogo());
                }

                return label;
            }
        });

        if (selecionado != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                Jogo jogo = combo.getItemAt(i);
                if (jogo.getId().equalsIgnoreCase(selecionado.getId())) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }

        return combo;
    }

    private JComboBox<String> criarComboCompradores(String selecionado) {
        JComboBox<String> combo = new JComboBox<>();

        for (String comprador : bilheteriaService.listarCompradores()) {
            combo.addItem(comprador);
        }

        configurarCombo(combo);
        combo.setEditable(true);
        combo.setFocusable(true);

        Component editor = combo.getEditor().getEditorComponent();
        if (editor instanceof JTextField campo) {
            configurarCampoTexto(campo);
        }

        if (selecionado != null) {
            combo.setSelectedItem(selecionado);
        }

        return combo;
    }

    private String valorComboEditavel(JComboBox<String> combo) {
        Object valor = combo.isEditable()
                ? combo.getEditor().getItem()
                : combo.getSelectedItem();

        return valor == null ? "" : valor.toString().trim();
    }

    private void atualizarComboCompradores() {
        if (comboComprador == null) {
            return;
        }

        String nomeAtual = valorComboEditavel(comboComprador);

        comboComprador.removeAllItems();
        for (String comprador : bilheteriaService.listarCompradores()) {
            comboComprador.addItem(comprador);
        }

        comboComprador.setSelectedItem(nomeAtual);
        comboComprador.getEditor().setItem(nomeAtual);
    }

    private JComboBox<String> criarComboEstadios(String selecionado) {
        JComboBox<String> combo = new JComboBox<>();
        for (String estadio : bilheteriaService.listarEstadiosExistentes()) {
            combo.addItem(estadio);
        }
        configurarCombo(combo);

        if (selecionado != null) {
            combo.setSelectedItem(selecionado);
        }

        return combo;
    }

    private JSpinner criarSpinnerNumero(int valor, int minimo, int maximo) {
        // SpinnerNumberModel exige: mínimo <= valor <= máximo.
        int minimoSeguro = Math.max(0, minimo);
        int maximoSeguro = Math.max(minimoSeguro, maximo);
        int valorSeguro = Math.max(minimoSeguro, Math.min(valor, maximoSeguro));

        JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(valorSeguro, minimoSeguro, maximoSeguro, 1)
        );

        configurarSpinner(spinner);
        return spinner;
    }

    private JSpinner criarSpinnerPreco(double valor) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(valor, 0.01, 100000.00, 0.50));
        configurarSpinner(spinner);
        return spinner;
    }

    private void atualizarTudo(Jogo jogoASelecionar) {
        String idSelecionado = jogoASelecionar == null ? null : jogoASelecionar.getId();

        atualizarTabelaJogos();
        selecionarJogoPorId(idSelecionado);
        atualizarComboCompradores();
        atualizarResumoCompra();
        atualizarTabelaCompras();
    }

    private void atualizarTabelaJogos() {
        if (modeloJogos == null) {
            return;
        }

        modeloJogos.setRowCount(0);
        jogosVisiveis.clear();

        for (Jogo jogo : bilheteriaService.listarJogosBilheteira()) {
            jogosVisiveis.add(jogo);
            modeloJogos.addRow(new Object[]{
                    jogo.getData(),
                    jogo.getHora(),
                    jogo.getNomeJogo(),
                    jogo.getEstadio(),
                    bilheteriaService.getDisponiveis(jogo.getId(), BilheteriaService.TIPO_NORMAL),
                    bilheteriaService.getDisponiveis(jogo.getId(), BilheteriaService.TIPO_VIP),
                    bilheteriaService.getDisponiveis(jogo.getId(), BilheteriaService.TIPO_PREMIUM),
                    jogo.getEstado()
            });
        }
    }

    private void atualizarTabelaCompras() {
        if (modeloCompras == null) {
            return;
        }

        modeloCompras.setRowCount(0);
        comprasVisiveis.clear();

        Jogo jogo = jogoSelecionado();
        if (jogo == null) {
            return;
        }

        for (Bilhete compra : bilheteriaService.listarBilhetesDoJogo(jogo.getId())) {
            comprasVisiveis.add(compra);
            modeloCompras.addRow(new Object[]{
                    compra.getIdTransacao(),
                    compra.getNomeComprador(),
                    compra.getTipo(),
                    compra.getQuantidade(),
                    formatarEuros(compra.getPrecoUnitario()),
                    formatarEuros(compra.getTotal()),
                    compra.getMetodoPagamento(),
                    compra.getDataCompra().toLocalDate().toString()
            });
        }
    }

    private void selecionarJogoPorId(String idJogo) {
        if (idJogo == null || tabelaJogos == null) {
            return;
        }

        for (int i = 0; i < jogosVisiveis.size(); i++) {
            if (jogosVisiveis.get(i).getId().equalsIgnoreCase(idJogo)) {
                int linhaView = tabelaJogos.convertRowIndexToView(i);
                if (linhaView >= 0) {
                    tabelaJogos.setRowSelectionInterval(linhaView, linhaView);
                }
                return;
            }
        }
    }

    private Jogo jogoSelecionado() {
        if (tabelaJogos == null) {
            return null;
        }

        int linha = tabelaJogos.getSelectedRow();
        if (linha < 0) {
            return null;
        }

        int linhaModelo = tabelaJogos.convertRowIndexToModel(linha);
        if (linhaModelo < 0 || linhaModelo >= jogosVisiveis.size()) {
            return null;
        }

        return jogosVisiveis.get(linhaModelo);
    }

    private Bilhete compraSelecionada() {
        if (tabelaCompras == null) {
            return null;
        }

        int linha = tabelaCompras.getSelectedRow();
        if (linha < 0) {
            return null;
        }

        int linhaModelo = tabelaCompras.convertRowIndexToModel(linha);
        if (linhaModelo < 0 || linhaModelo >= comprasVisiveis.size()) {
            return null;
        }

        return comprasVisiveis.get(linhaModelo);
    }

    private Jogo encontrarJogo(String idJogo) {
        for (Jogo jogo : bilheteriaService.listarJogosBilheteira()) {
            if (jogo.getId().equalsIgnoreCase(idJogo)) {
                return jogo;
            }
        }
        return null;
    }

    private void atualizarResumoCompra() {
        Jogo jogo = jogoSelecionado();

        if (jogo == null) {
            lblJogo.setText("Seleciona um jogo na tabela");
            lblPreco.setText("Preço unitário: -");
            lblDisponiveis.setText("Disponíveis: -");
            lblTotal.setText("Total: 0,00 €");
            spinnerQuantidade.setModel(new SpinnerNumberModel(1, 1, 1, 1));
            spinnerQuantidade.setEnabled(false);
            btnComprar.setEnabled(false);
            return;
        }

        String tipo = String.valueOf(comboTipo.getSelectedItem());
        int disponiveis = bilheteriaService.getDisponiveis(jogo.getId(), tipo);
        double preco = bilheteriaService.getPreco(jogo.getId(), tipo);

        lblJogo.setText(jogo.getNomeJogo());
        lblPreco.setText("Preço unitário: " + formatarEuros(preco));
        lblDisponiveis.setText("Disponíveis " + tipo + ": " + disponiveis);

        if (disponiveis <= 0) {
            spinnerQuantidade.setModel(new SpinnerNumberModel(0, 0, 0, 1));
            spinnerQuantidade.setEnabled(false);
            btnComprar.setEnabled(false);
            lblTotal.setText("Total: 0,00 €");
            return;
        }

        int atual = Math.max(1, Math.min(valorSpinner(spinnerQuantidade), disponiveis));
        spinnerQuantidade.setModel(new SpinnerNumberModel(atual, 1, disponiveis, 1));
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

        double total = bilheteriaService.getPreco(jogo.getId(), String.valueOf(comboTipo.getSelectedItem()))
                * valorSpinner(spinnerQuantidade);
        lblTotal.setText("Total: " + formatarEuros(total));
    }

    private void confirmarCompra() {
        try {
            Jogo jogo = jogoSelecionado();
            if (jogo == null) {
                throw new IllegalArgumentException("Seleciona primeiro um jogo na tabela.");
            }

            String nomeComprador = valorComboEditavel(comboComprador);

            Bilhete compra = bilheteriaService.comprarBilhetes(
                    jogo,
                    nomeComprador,
                    String.valueOf(comboTipo.getSelectedItem()),
                    valorSpinner(spinnerQuantidade),
                    String.valueOf(comboPagamento.getSelectedItem())
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Compra realizada com sucesso.\n\n"
                            + "Transação: " + compra.getIdTransacao() + "\n"
                            + "Comprador: " + compra.getNomeComprador() + "\n"
                            + "Jogo: " + jogo.getNomeJogo() + "\n"
                            + "Bilhetes: " + compra.getQuantidade() + " " + compra.getTipo() + "\n"
                            + "Total: " + formatarEuros(compra.getTotal()),
                    "Compra confirmada",
                    JOptionPane.INFORMATION_MESSAGE
            );

            comboComprador.getEditor().setItem("");
            atualizarTudo(jogo);
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarErro(e.getMessage());
        }
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
                        table, value, isSelected, hasFocus, row, column
                );

                String estado = String.valueOf(value);
                label.setFont(Tema.FONTE_CARD_TITULO);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBorder(new EmptyBorder(0, 8, 0, 8));

                if (isSelected) {
                    label.setBackground(Tema.COR_SELECAO_NEUTRA);
                    label.setForeground(Tema.COR_TEXTO_PRINCIPAL);
                } else {
                    label.setBackground(Tema.COR_CARD);
                    if ("Agendado".equalsIgnoreCase(estado)) {
                        label.setForeground(Tema.COR_INFO);
                    } else if ("Em curso".equalsIgnoreCase(estado)) {
                        label.setForeground(Tema.COR_AVISO);
                    } else if ("Cancelado".equalsIgnoreCase(estado)) {
                        label.setForeground(Tema.COR_ERRO);
                    } else {
                        label.setForeground(Tema.COR_SUCESSO);
                    }
                }

                return label;
            }
        };
    }

    private JPanel criarCampoComLabel(String label, JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 7));
        painel.setOpaque(false);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JLabel lbl = new JLabel(label);
        lbl.setFont(Tema.FONTE_CARD_TITULO);
        lbl.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        campo.setPreferredSize(new Dimension(100, 38));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        painel.add(lbl, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);
        return painel;
    }

    private void configurarCampoTexto(JTextField campo) {
        campo.setFont(Tema.FONTE_TEXTO);
        campo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        campo.setBackground(Tema.COR_INPUT);
        campo.setBorder(new RoundedBorder(Tema.COR_LINHA, 12));
        campo.setCaretColor(Tema.COR_TEXTO_PRINCIPAL);
    }

    private void configurarCombo(JComboBox<?> combo) {
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

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
        editor.getTextField().setFont(Tema.FONTE_TEXTO);
        editor.getTextField().setForeground(Tema.COR_TEXTO_PRINCIPAL);
        editor.getTextField().setBackground(Tema.COR_INPUT);
        editor.getTextField().setHorizontalAlignment(SwingConstants.LEFT);
        spinner.setBorder(new RoundedBorder(Tema.COR_LINHA, 12));
    }

    private JLabel criarLabelResumo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(Tema.FONTE_CARD_TITULO);
        label.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel criarLabelTotal(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(Tema.FONTE_CARD_VALOR_GRANDE);
        label.setForeground(Tema.COR_SUCESSO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private int valorSpinner(JSpinner spinner) {
        return ((Number) spinner.getValue()).intValue();
    }

    private double valorSpinnerPreco(JSpinner spinner) {
        return ((Number) spinner.getValue()).doubleValue();
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private String formatarEuros(double valor) {
        return String.format("%.2f €", valor).replace('.', ',');
    }
}
