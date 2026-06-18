package Frames.seccaoEstadios;

import Design.MenuLateral;
import GrupoEeleminatoria.CampeonatoRepositorio;
import Models.Campeonato;
import Models.Estadio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NovoEstadioFrame extends JFrame {

    private Campeonato campeonato;

    private JTextField campoNome;
    private JTextField campoCidade;
    private JTextField campoProprietario;
    private JTextField campoNormal;
    private JTextField campoVip;
    private JTextField campoPremium;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color CARD_REGRA = new Color(255, 241, 217);

    public NovoEstadioFrame(Campeonato campeonato) {
        this.campeonato = campeonato;

        setTitle("Novo Estádio - " + campeonato.getNome());
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        JPanel pagina = criarPagina(menuLateral);

        add(menuLateral, BorderLayout.WEST);
        add(pagina, BorderLayout.CENTER);

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
        centro.setBorder(new EmptyBorder(20, 90, 20, 90));

        JLabel titulo = new JLabel("Novo Estádio");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Registo de estádio associado ao campeonato: " + campeonato.getNome());
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(MUTED);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(titulo);
        centro.add(Box.createVerticalStrut(4));
        centro.add(subtitulo);
        centro.add(Box.createVerticalStrut(35));

        JPanel conteudo = new JPanel(new BorderLayout(35, 0));
        conteudo.setOpaque(false);
        conteudo.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(criarCardFormulario(), BorderLayout.CENTER);
        conteudo.add(criarCardRegra(), BorderLayout.EAST);

        centro.add(conteudo);

        pagina.add(centro, BorderLayout.CENTER);

        return pagina;
    }

    private JPanel criarCardFormulario() {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(26, 26, 20, 26));
        card.setPreferredSize(new Dimension(720, 460));

        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Dados do Estádio");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        container.add(titulo);
        container.add(Box.createVerticalStrut(22));

        JPanel grid = new JPanel(new GridLayout(3, 2, 30, 18));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoNome = criarCampo("Ex: Estádio da Luz");
        campoCidade = criarCampo("Ex: Lisboa");
        campoProprietario = criarCampo("Ex: SL Benfica");
        campoNormal = criarCampo("Ex: 50000");
        campoVip = criarCampo("Ex: 10000");
        campoPremium = criarCampo("Ex: 5000");

        grid.add(criarCampoComLabel("Nome do estádio", campoNome));
        grid.add(criarCampoComLabel("Cidade", campoCidade));
        grid.add(criarCampoComLabel("Proprietário", campoProprietario));
        grid.add(criarCampoComLabel("Lugares Normal", campoNormal));
        grid.add(criarCampoComLabel("Lugares VIP", campoVip));
        grid.add(criarCampoComLabel("Lugares Premium", campoPremium));

        container.add(grid);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setOpaque(false);

        JButton btnCancelar = criarBotaoCinza("Cancelar");
        JButton btnGuardar = criarBotaoAzul("Guardar Estádio");

        btnCancelar.addActionListener(e -> {
            dispose();
            new EstadiosFrame(campeonato);
        });

        btnGuardar.addActionListener(e -> guardarEstadio());

        botoes.add(btnCancelar);
        botoes.add(btnGuardar);

        card.add(container, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        return card;
    }

    private JPanel criarCampoComLabel(String label, JTextField campo) {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        JLabel texto = new JLabel(label);
        texto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        texto.setForeground(TEXT);

        painel.add(texto);
        painel.add(Box.createVerticalStrut(8));
        painel.add(campo);

        return painel;
    }

    private JTextField criarCampo(String placeholder) {
        JTextField campo = new JTextField();

        campo.setPreferredSize(new Dimension(260, 38));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setForeground(TEXT);
        campo.setToolTipText(placeholder);

        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                new EmptyBorder(8, 12, 8, 12)
        ));

        return campo;
    }

    private JPanel criarCardRegra() {
        JPanel card = new PainelArredondado(18, CARD_REGRA);
        card.setPreferredSize(new Dimension(250, 460));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 20, 20, 20));

        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setOpaque(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        texto.setForeground(TEXT);

        texto.setText("""
                Regra dos Estádios

                Só podes adicionar estádios
                enquanto o campeonato estiver
                em configuração.

                O nome do estádio não pode
                repetir dentro do mesmo
                campeonato.

                Os lugares devem ser
                valores positivos.
                """);

        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private void guardarEstadio() {
        if (!campeonato.isEmConfiguracao()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não é possível adicionar estádios depois do campeonato iniciado.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String nome = campoNome.getText().trim();
        String cidade = campoCidade.getText().trim();
        String proprietario = campoProprietario.getText().trim();
        String normalTxt = campoNormal.getText().trim();
        String vipTxt = campoVip.getText().trim();
        String premiumTxt = campoPremium.getText().trim();

        if (nome.isEmpty()
                || cidade.isEmpty()
                || proprietario.isEmpty()
                || normalTxt.isEmpty()
                || vipTxt.isEmpty()
                || premiumTxt.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Faltam campos por preencher do estadio",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (campeonato.existeEstadioComNome(nome)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não é permitido estadio repetidos no mesmo campeonato",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int normal;
        int vip;
        int premium;

        try {
            normal = Integer.parseInt(normalTxt);
            vip = Integer.parseInt(vipTxt);
            premium = Integer.parseInt(premiumTxt);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Os lugares devem ser números válidos.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (normal < 0 || vip < 0 || premium < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não é permitido valores negativos nos lugares.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Estadio estadio = new Estadio(
                nome,
                cidade,
                proprietario,
                normal,
                vip,
                premium
        );

        boolean adicionado = campeonato.adicionarEstadio(estadio);

        if (!adicionado) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível adicionar o estádio.\nVerifica se o campeonato ainda está em configuração.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        CampeonatoRepositorio.salvar();

        JOptionPane.showMessageDialog(
                this,
                "Estádio adicionado com sucesso ao campeonato " + campeonato.getNome() + ".",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
        new EstadiosFrame(campeonato);
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
        botao.setBorder(new EmptyBorder(11, 20, 11, 20));

        return botao;
    }

    private JButton criarBotaoCinza(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(new Color(241, 245, 249));
        botao.setForeground(TEXT);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(11, 20, 11, 20));

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
            desenho.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.dispose();

            super.paintComponent(g);
        }
    }
}