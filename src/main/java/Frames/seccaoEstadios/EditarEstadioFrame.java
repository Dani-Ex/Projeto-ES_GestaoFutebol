package Frames.SeccaoEstadios;

import Design.MenuLateral;
import Design.RoundedBorder;
import Design.RoundedButton;
import Design.RoundedPanel;
import Models.Campeonatos.CampeonatoRepositorio;
import Models.Estadios.Estadio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditarEstadioFrame extends JFrame {

    private final Runnable onEstadioEditado;
    private final String nomeOriginal;
    private final Estadio estadioOriginal;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);

    private JTextField campoNome;
    private JTextField campoCidade;
    private JTextField campoProprietario;
    private JTextField campoNormal;
    private JTextField campoVip;
    private JTextField campoPremium;

    public EditarEstadioFrame(String nomeEstadio) {
        this(nomeEstadio, null);
    }

    public EditarEstadioFrame(String nomeEstadio, Runnable onEstadioEditado) {
        this.onEstadioEditado = onEstadioEditado;
        this.nomeOriginal = nomeEstadio;
        this.estadioOriginal = procurarEstadio(nomeEstadio);

        if (estadioOriginal == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Não foi possível encontrar o estádio selecionado.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        String bloqueio = CampeonatoRepositorio.motivoBloqueioEdicaoEstadio(nomeEstadio);

        if (!bloqueio.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    bloqueio,
                    "Edição indisponível",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        setTitle("Editar Estádio - " + estadioOriginal.getNome());
        setSize(1040, 690);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        add(menuLateral, BorderLayout.WEST);
        add(criarPagina(menuLateral), BorderLayout.CENTER);

        setVisible(true);
    }

    private Estadio procurarEstadio(String nome) {
        for (Estadio estadio : CampeonatoRepositorio.listarEstadiosExistentes()) {
            if (estadio.getNome().equalsIgnoreCase(nome)) {
                return estadio;
            }
        }

        return null;
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

        JLabel titulo = new JLabel("Editar Estádio");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("A alteração será aplicada a todos os campeonatos associados a este estádio.");
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
        JPanel card = new RoundedPanel(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(28, 28, 20, 28));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 460));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Informações do Estádio");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 28);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        campoNome = criarCampo(estadioOriginal.getNome());
        campoCidade = criarCampo(estadioOriginal.getCidade());
        campoProprietario = criarCampo(estadioOriginal.getProprietario());
        campoNormal = criarCampo(String.valueOf(estadioOriginal.getLugaresNormal()));
        campoVip = criarCampo(String.valueOf(estadioOriginal.getLugaresVip()));
        campoPremium = criarCampo(String.valueOf(estadioOriginal.getLugaresPremium()));

        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(criarCampoComLabel("Nome do Estádio", campoNome), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formulario.add(criarCampoComLabel("Cidade", campoCidade), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(criarCampoComLabel("Proprietário", campoProprietario), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formulario.add(criarCampoComLabel("Lugares Normais", campoNormal), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formulario.add(criarCampoComLabel("Lugares VIP", campoVip), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formulario.add(criarCampoComLabel("Lugares Premium", campoPremium), gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setOpaque(false);

        JButton btnCancelar = criarBotaoCinza("Cancelar");
        JButton btnGuardar = criarBotaoAzul("Guardar Alterações");

        btnCancelar.addActionListener(e -> {
            dispose();
        });
        btnGuardar.addActionListener(e -> guardar());

        botoes.add(btnCancelar);
        botoes.add(btnGuardar);

        card.add(titulo, BorderLayout.NORTH);
        card.add(formulario, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);
        return card;
    }

    private JTextField criarCampo(String valor) {
        JTextField campo = new JTextField(valor);
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setForeground(TEXT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(203, 213, 225), 8),
                new EmptyBorder(10, 12, 10, 12)
        ));
        return campo;
    }

    private JPanel criarCampoComLabel(String texto, JComponent componente) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setOpaque(false);
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT);
        painel.add(label, BorderLayout.NORTH);
        painel.add(componente, BorderLayout.CENTER);
        return painel;
    }

    private void guardar() {
        String nome = campoNome.getText().trim();
        String cidade = campoCidade.getText().trim();
        String proprietario = campoProprietario.getText().trim();

        if (nome.isEmpty() || cidade.isEmpty() || proprietario.isEmpty()) {
            mostrarErro("Nome, cidade e proprietário são obrigatórios.");
            return;
        }

        int normal;
        int vip;
        int premium;

        try {
            normal = Integer.parseInt(campoNormal.getText().trim());
            vip = Integer.parseInt(campoVip.getText().trim());
            premium = Integer.parseInt(campoPremium.getText().trim());
        } catch (NumberFormatException e) {
            mostrarErro("As capacidades devem ser números inteiros.");
            return;
        }

        if (normal < 0 || vip < 0 || premium < 0 || normal + vip + premium <= 0) {
            mostrarErro("As capacidades não podem ser negativas e o estádio deve ter lugares disponíveis.");
            return;
        }

        Estadio atualizado = new Estadio(nome, cidade, proprietario, normal, vip, premium);

        if (!CampeonatoRepositorio.editarEstadio(nomeOriginal, atualizado)) {
            mostrarErro("Não foi possível editar o estádio. O novo nome pode já existir ou um campeonato associado pode ter iniciado.");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Estádio atualizado com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        if (onEstadioEditado != null) {
            onEstadioEditado.run();
        }

        dispose();
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private JButton criarBotaoAzul(String texto) {
        return criarBotao(texto, BLUE, Color.WHITE);
    }

    private JButton criarBotaoCinza(String texto) {
        return criarBotao(texto, new Color(241, 245, 249), TEXT);
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto) {
        JButton botao = new RoundedButton(texto, fundo, corTexto, 14);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setBorder(new EmptyBorder(11, 18, 11, 18));
        return botao;
    }
}
