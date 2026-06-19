package Frames.SeccaoEquipas;

import Design.MenuLateral;
import Design.PlaceholderTextField;
import Design.RoundedBorder;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import GrupoEeleminatoria.CampeonatoRepositorio;
import Models.Campeonato;
import Models.Equipa;
import Models.EquipaService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class EquipasFrame extends JFrame {

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    private Campeonato campeonato;

    private JTable tabelaEquipas;
    private DefaultTableModel modeloEquipas;
    private PlaceholderTextField campoPesquisa;

    private final List<Equipa> equipasVisiveis = new ArrayList<>();
    private final EquipaService equipaService = new EquipaService();

    public EquipasFrame() {
        this(null);
    }

    public EquipasFrame(Campeonato campeonato) {
        this.campeonato = campeonato;

        setTitle(campeonato == null ? "Equipas" : "Equipas - " + campeonato.getNome());
        setSize(1920, 1080);
        setMinimumSize(new Dimension(1280, 760));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setFocusable(true);
        main.setBackground(Tema.COR_FUNDO);
        main.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_JANELA.top,
                Tema.PADDING_JANELA.left,
                Tema.PADDING_JANELA.bottom,
                Tema.PADDING_JANELA.right
        ));

        add(main);
        limparSelecaoAoClicar(main);

        menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);
        main.add(menuLateral, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setFocusable(true);
        content.setOpaque(false);
        content.setLayout(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(
                Tema.ESPACAMENTO_GRANDE + 5,
                Tema.ESPACAMENTO_GRANDE + 5,
                Tema.ESPACAMENTO_GRANDE + Tema.ESPACAMENTO_MEDIO,
                Tema.ESPACAMENTO_GRANDE + 5
        ));

        limparSelecaoAoClicar(content);

        content.add(criarCabecalho(), BorderLayout.NORTH);
        content.add(criarAreaPrincipal(), BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(criarLinhaMenu(main), BorderLayout.NORTH);
        centro.add(content, BorderLayout.CENTER);

        main.add(centro, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new GridBagLayout());
        cabecalho.setFocusable(true);
        cabecalho.setOpaque(false);
        limparSelecaoAoClicar(cabecalho);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, Tema.ESPACAMENTO_MEDIO, 0);
        cabecalho.add(criarTopo(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, Tema.ESPACAMENTO_MEDIO + 5, 0);
        cabecalho.add(criarPesquisa(), gbc);

        return cabecalho;
    }

    private JPanel criarLinhaMenu(JPanel main) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setFocusable(true);
        linha.setOpaque(false);
        linha.setBorder(BorderFactory.createEmptyBorder(0, Tema.ESPACAMENTO_GRANDE + 5, 0, 0));
        limparSelecaoAoClicar(linha);
        linha.add(criarBotaoMenu(main));

        return linha;
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setFocusable(true);
        topo.setOpaque(false);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        limparSelecaoAoClicar(topo);

        JPanel tituloBox = new JPanel();
        tituloBox.setFocusable(true);
        tituloBox.setOpaque(false);
        tituloBox.setLayout(new BoxLayout(tituloBox, BoxLayout.Y_AXIS));
        limparSelecaoAoClicar(tituloBox);

        JLabel titulo = new JLabel("Equipas");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String textoSubtitulo;

        if (campeonato == null) {
            textoSubtitulo = "Registo, consulta e validação das equipas participantes.";
        } else {
            textoSubtitulo = "Seleciona uma equipa registada e adiciona ao campeonato: " + campeonato.getNome();
        }

        JLabel subtitulo = new JLabel(textoSubtitulo);
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        tituloBox.add(titulo);
        tituloBox.add(Box.createVerticalStrut(4));
        tituloBox.add(subtitulo);

        JPanel botoesTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoesTopo.setOpaque(false);

        JButton btnNova = criarBotaoArredondado("+ Nova Equipa", Tema.COR_INFO, Tema.COR_TEXTO_CLARO, 150);
        btnNova.addActionListener(e -> new NovaEquipaFrame(this::atualizarTabela));
        botoesTopo.add(btnNova);

        if (campeonato != null) {
            JButton btnAdicionarCampeonato = criarBotaoArredondado(
                    "Adicionar ao Campeonato",
                    new Color(22, 163, 74),
                    Tema.COR_TEXTO_CLARO,
                    230
            );

            btnAdicionarCampeonato.addActionListener(e -> adicionarEquipaSelecionadaAoCampeonato());
            botoesTopo.add(btnAdicionarCampeonato);
        }

        topo.add(tituloBox, BorderLayout.WEST);
        topo.add(botoesTopo, BorderLayout.EAST);

        return topo;
    }

    private JButton criarBotaoMenu(JPanel main) {
        JButton btnMenu = new JButton("=");

        btnMenu.setFont(Tema.FONTE_BOTAO_MENU);
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        return btnMenu;
    }

    private JPanel criarPesquisa() {
        Dimension tamanhoCampo = new Dimension(250, 40);

        JPanel linha = new JPanel(new BorderLayout());
        linha.setFocusable(true);
        linha.setOpaque(false);
        linha.setPreferredSize(new Dimension(250, 45));
        linha.setMinimumSize(new Dimension(250, 45));
        limparSelecaoAoClicar(linha);

        campoPesquisa = new PlaceholderTextField("Pesquisar equipa...");
        campoPesquisa.setPreferredSize(tamanhoCampo);
        campoPesquisa.setMinimumSize(tamanhoCampo);
        campoPesquisa.setMaximumSize(tamanhoCampo);
        campoPesquisa.setFont(Tema.FONTE_TEXTO_PEQUENO);
        campoPesquisa.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        campoPesquisa.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        campoPesquisa.setOpaque(false);

        campoPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                atualizarTabela();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                atualizarTabela();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                atualizarTabela();
            }
        });

        RoundedPanel campo = new RoundedPanel(18, Tema.COR_INPUT);
        campo.setLayout(new BorderLayout());
        campo.setPreferredSize(tamanhoCampo);
        campo.setMinimumSize(tamanhoCampo);
        campo.setMaximumSize(tamanhoCampo);
        campo.setBorder(new RoundedBorder(Tema.COR_LINHA, 18));
        campo.add(campoPesquisa, BorderLayout.CENTER);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esquerda.setOpaque(false);
        esquerda.setPreferredSize(tamanhoCampo);
        esquerda.setMinimumSize(tamanhoCampo);
        esquerda.setMaximumSize(tamanhoCampo);
        esquerda.add(campo);

        linha.add(esquerda, BorderLayout.WEST);

        return linha;
    }

    private JPanel criarAreaPrincipal() {
        JPanel area = new JPanel(new BorderLayout(25, 0));
        area.setFocusable(true);
        area.setOpaque(false);
        limparSelecaoAoClicar(area);

        area.add(criarCardTabelaEquipas(), BorderLayout.CENTER);
        area.add(criarCardValidacao(), BorderLayout.EAST);

        return area;
    }

    private JPanel criarCardTabelaEquipas() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setFocusable(true);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_CARD.top,
                Tema.PADDING_CARD.left,
                Tema.PADDING_CARD.bottom,
                Tema.PADDING_CARD.right
        ));

        limparSelecaoAoClicar(card);

        JLabel titulo = new JLabel("Equipas Registadas");
        titulo.setFont(Tema.FONTE_TABELA_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        String[] colunas = {
                "Nome da Equipa",
                "Campeonato",
                "Grupo",
                "Estado",
                "Jogadores",
                "Pontos"
        };

        modeloEquipas = criarModeloNaoEditavel(colunas);

        tabelaEquipas = new JTable(modeloEquipas);
        configurarTabelaEquipas(tabelaEquipas);

        tabelaEquipas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirEquipaSelecionada();
                }
            }
        });

        atualizarTabela();

        tabelaEquipas.getColumnModel().getColumn(0).setPreferredWidth(170);
        tabelaEquipas.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabelaEquipas.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabelaEquipas.getColumnModel().getColumn(3).setPreferredWidth(85);
        tabelaEquipas.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabelaEquipas.getColumnModel().getColumn(5).setPreferredWidth(70);

        JScrollPane scroll = new JScrollPane(tabelaEquipas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Tema.COR_CARD);

        scroll.getViewport().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                limparSelecao();
            }
        });

        scroll.setBackground(Tema.COR_CARD);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setFocusable(true);
        topo.setOpaque(false);
        limparSelecaoAoClicar(topo);
        topo.add(titulo, BorderLayout.WEST);

        card.add(topo, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(10), BorderLayout.WEST);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardValidacao() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_VERDE_SUAVE);
        card.setFocusable(true);
        card.setPreferredSize(new Dimension(240, 0));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(
                Tema.ESPACAMENTO_MEDIO + 5,
                Tema.ESPACAMENTO_MEDIO + 5,
                Tema.ESPACAMENTO_MEDIO + 5,
                Tema.ESPACAMENTO_MEDIO + 5
        ));

        limparSelecaoAoClicar(card);

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
        texto.setFocusable(false);
        texto.setOpaque(false);

        texto.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                limparSelecao();
            }
        });

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

    private void atualizarTabela() {
        if (modeloEquipas == null) {
            return;
        }

        modeloEquipas.setRowCount(0);
        equipasVisiveis.clear();

        String termoPesquisa = campoPesquisa == null ? "" : campoPesquisa.getText();

        for (Equipa equipa : equipaService.pesquisarEquipas(termoPesquisa)) {
            equipasVisiveis.add(equipa);

            modeloEquipas.addRow(new Object[]{
                    equipa.getNome(),
                    equipa.getCampeonato(),
                    equipa.getGrupo(),
                    equipa.getEstadoTexto(),
                    equipa.getTotalJogadores(),
                    equipa.getPontos()
            });
        }
    }

    private void adicionarEquipaSelecionadaAoCampeonato() {
        if (campeonato == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Abre esta página através de um campeonato para poderes associar equipas.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!campeonato.isEmConfiguracao()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não é possível adicionar equipas depois do campeonato iniciado.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int linha = tabelaEquipas.getSelectedRow();

        if (linha < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleciona uma equipa da tabela para adicionar ao campeonato.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int linhaModelo = tabelaEquipas.convertRowIndexToModel(linha);

        if (linhaModelo < 0 || linhaModelo >= equipasVisiveis.size()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível identificar a equipa selecionada.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Equipa equipaSelecionada = equipasVisiveis.get(linhaModelo);
        String nomeEquipa = equipaSelecionada.getNome();

        if (campeonato.existeEquipaComNome(nomeEquipa)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Essa equipa já está associada a este campeonato.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (campeonato.getEquipas().size() >= campeonato.getNumeroEquipasNecessarias()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Este campeonato já atingiu o número máximo de equipas permitido.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        boolean adicionada = campeonato.adicionarEquipa(nomeEquipa);

        if (!adicionada) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível adicionar a equipa ao campeonato.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        CampeonatoRepositorio.salvar();

        JOptionPane.showMessageDialog(
                this,
                "Equipa adicionada ao campeonato com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void abrirEquipaSelecionada() {
        int linha = tabelaEquipas.getSelectedRow();

        if (linha < 0) {
            return;
        }

        int linhaModelo = tabelaEquipas.convertRowIndexToModel(linha);

        if (linhaModelo >= 0 && linhaModelo < equipasVisiveis.size()) {
            new ConsultaEquipaFrame(equipasVisiveis.get(linhaModelo), this::atualizarTabela);
        }
    }

    private void configurarTabelaEquipas(JTable tabela) {
        TableStyle.aplicarTabelaLimpa(tabela, 0);
        tabela.getColumnModel().getColumn(1).setCellRenderer(TableStyle.rendererEsquerda());
    }

    private void limparSelecao() {
        if (tabelaEquipas != null) {
            tabelaEquipas.clearSelection();
        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
    }

    private void limparSelecaoAoClicar(JComponent componente) {
        componente.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                limparSelecao();
            }
        });
    }

    private JButton criarBotaoArredondado(String texto, Color fundo, Color corTexto, int largura) {
        JButton botao = new RoundedButton(texto, fundo, corTexto, 14);
        botao.setPreferredSize(new Dimension(largura, 40));
        botao.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        return botao;
    }
}