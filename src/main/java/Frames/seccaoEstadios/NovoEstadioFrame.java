package Frames.seccaoEstadios;

import Design.MenuLateral;
import GrupoEeleminatoria.CampeonatoRepositorio;
import Models.Campeonato;
import Models.Estadio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class NovoEstadioFrame extends JFrame {

    private JTextField campoNome;
    private JTextField campoCidade;
    private JTextField campoProprietario;
    private JTextField campoLugaresNormal;
    private JTextField campoLugaresVip;
    private JTextField campoLugaresPremium;

    private JComboBox<Campeonato> comboCampeonatos;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);

    public NovoEstadioFrame() {
        setTitle("Novo Estádio");
        setSize(1050, 700);
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

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JButton btnMenu = new JButton("=");
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnMenu.setForeground(TEXT);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnMenu.addActionListener(e -> {
            menuLateral.setVisible(!menuLateral.isVisible());
            revalidate();
            repaint();
        });

        topo.add(btnMenu, BorderLayout.WEST);
        pagina.add(topo, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(15, 120, 20, 120));

        JLabel titulo = new JLabel("Novo Estádio");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Cria um estádio e associa-o a um campeonato já existente."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitulo.setForeground(MUTED);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(titulo);
        centro.add(Box.createVerticalStrut(4));
        centro.add(subtitulo);
        centro.add(Box.createVerticalStrut(28));
        centro.add(criarFormulario());

        pagina.add(centro, BorderLayout.CENTER);

        return pagina;
    }

    private JPanel criarFormulario() {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(28, 28, 20, 28));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tituloCard = new JLabel("Informações do Estádio");
        tituloCard.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tituloCard.setForeground(TEXT);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 28);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        campoNome = criarCampoTexto();
        campoCidade = criarCampoTexto();
        campoProprietario = criarCampoTexto();
        campoLugaresNormal = criarCampoTexto();
        campoLugaresVip = criarCampoTexto();
        campoLugaresPremium = criarCampoTexto();

        comboCampeonatos = criarComboCampeonatos();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formulario.add(
                criarCampoComLabel("Campeonato a associar", comboCampeonatos),
                gbc
        );

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(
                criarCampoComLabel("Nome do Estádio", campoNome),
                gbc
        );

        gbc.gridx = 1;
        gbc.gridy = 1;
        formulario.add(
                criarCampoComLabel("Cidade", campoCidade),
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy = 2;
        formulario.add(
                criarCampoComLabel("Proprietário", campoProprietario),
                gbc
        );

        gbc.gridx = 1;
        gbc.gridy = 2;
        formulario.add(
                criarCampoComLabel("Lugares Normais", campoLugaresNormal),
                gbc
        );

        gbc.gridx = 0;
        gbc.gridy = 3;
        formulario.add(
                criarCampoComLabel("Lugares VIP", campoLugaresVip),
                gbc
        );

        gbc.gridx = 1;
        gbc.gridy = 3;
        formulario.add(
                criarCampoComLabel("Lugares Premium", campoLugaresPremium),
                gbc
        );

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setOpaque(false);

        JButton btnCancelar = criarBotaoCinza("Cancelar");
        JButton btnCriar = criarBotaoAzul("Criar Estádio");

        btnCancelar.addActionListener(e -> {
            dispose();
            new EstadiosFrame();
        });

        btnCriar.addActionListener(e -> criarEstadio());

        botoes.add(btnCancelar);
        botoes.add(btnCriar);

        card.add(tituloCard, BorderLayout.NORTH);
        card.add(formulario, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        return card;
    }

    private JComboBox<Campeonato> criarComboCampeonatos() {
        JComboBox<Campeonato> combo = new JComboBox<>();

        List<Campeonato> campeonatos = CampeonatoRepositorio.listar();

        for (Campeonato campeonato : campeonatos) {
            combo.addItem(campeonato);
        }

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

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
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                if (value instanceof Campeonato campeonato) {
                    label.setText(
                            campeonato.getNome()
                                    + "  |  "
                                    + campeonato.getEstadios().size()
                                    + "/"
                                    + campeonato.getNumeroEstadiosNecessarios()
                                    + " estádios"
                    );
                }

                return label;
            }
        });

        return combo;
    }

    private JPanel criarCampoComLabel(
            String textoLabel,
            JComponent componente
    ) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setOpaque(false);

        JLabel label = new JLabel(textoLabel);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);

        painel.add(label, BorderLayout.NORTH);
        painel.add(componente, BorderLayout.CENTER);

        return painel;
    }

    private JTextField criarCampoTexto() {
        JTextField campo = new JTextField();

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setForeground(TEXT);

        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        return campo;
    }

    private void criarEstadio() {
        Campeonato campeonato =
                (Campeonato) comboCampeonatos.getSelectedItem();

        if (campeonato == null) {
            mostrarErro("Seleciona um campeonato.");
            return;
        }

        if (!campeonato.isEmConfiguracao()) {
            mostrarErro(
                    "Não é possível adicionar estádios num campeonato já iniciado."
            );
            return;
        }

        if (campeonato.isGruposGerados()) {
            mostrarErro(
                    "Não é possível adicionar estádios depois dos grupos serem gerados."
            );
            return;
        }

        if (campeonato.getEstadios().size()
                >= campeonato.getNumeroEstadiosNecessarios()) {
            mostrarErro(
                    "Este campeonato já atingiu o número máximo de estádios."
            );
            return;
        }

        String nome = campoNome.getText().trim();
        String cidade = campoCidade.getText().trim();
        String proprietario = campoProprietario.getText().trim();

        if (nome.isEmpty() || cidade.isEmpty() || proprietario.isEmpty()) {
            mostrarErro(
                    "Nome, cidade e proprietário do estádio são obrigatórios."
            );
            return;
        }

        int lugaresNormal;
        int lugaresVip;
        int lugaresPremium;

        try {
            lugaresNormal = Integer.parseInt(
                    campoLugaresNormal.getText().trim()
            );

            lugaresVip = Integer.parseInt(
                    campoLugaresVip.getText().trim()
            );

            lugaresPremium = Integer.parseInt(
                    campoLugaresPremium.getText().trim()
            );

        } catch (NumberFormatException e) {
            mostrarErro(
                    "Os lugares Normal, VIP e Premium devem ser números inteiros."
            );
            return;
        }

        if (lugaresNormal < 0
                || lugaresVip < 0
                || lugaresPremium < 0) {
            mostrarErro("Os lugares não podem ter valores negativos.");
            return;
        }

        if (lugaresNormal + lugaresVip + lugaresPremium <= 0) {
            mostrarErro("O estádio deve ter pelo menos um lugar disponível.");
            return;
        }

        if (campeonato.existeEstadioComNome(nome)) {
            mostrarErro(
                    "Este estádio já está associado ao campeonato selecionado."
            );
            return;
        }

        if (existeEstadioNoSistema(nome)) {
            mostrarErro(
                    "Já existe um estádio com esse nome no sistema.\n"
                            + "Usa o botão \"Associar Existente\" para o ligar a outro campeonato."
            );
            return;
        }

        Estadio estadio = new Estadio(
                nome,
                cidade,
                proprietario,
                lugaresNormal,
                lugaresVip,
                lugaresPremium
        );

        boolean adicionado = campeonato.adicionarEstadio(estadio);

        if (!adicionado) {
            mostrarErro("Não foi possível criar o estádio.");
            return;
        }

        CampeonatoRepositorio.salvar();

        JOptionPane.showMessageDialog(
                this,
                "Estádio criado e associado ao campeonato "
                        + campeonato.getNome()
                        + ".",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
        new EstadiosFrame();
    }

    private boolean existeEstadioNoSistema(String nome) {
        for (Estadio estadio : CampeonatoRepositorio.listarEstadiosExistentes()) {
            if (estadio.getNome().equalsIgnoreCase(nome.trim())) {
                return true;
            }
        }

        return false;
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private JButton criarBotaoAzul(String texto) {
        return criarBotao(texto, BLUE, Color.WHITE);
    }

    private JButton criarBotaoCinza(String texto) {
        return criarBotao(texto, new Color(241, 245, 249), TEXT);
    }

    private JButton criarBotao(
            String texto,
            Color fundo,
            Color corTexto
    ) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(fundo);
        botao.setForeground(corTexto);
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

            desenho.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            desenho.setColor(new Color(0, 0, 0, 14));
            desenho.fillRoundRect(
                    4,
                    6,
                    getWidth() - 8,
                    getHeight() - 8,
                    raio,
                    raio
            );

            desenho.setColor(corFundo);
            desenho.fillRoundRect(
                    0,
                    0,
                    getWidth() - 8,
                    getHeight() - 8,
                    raio,
                    raio
            );

            desenho.dispose();

            super.paintComponent(g);
        }
    }
}