package GrupoEeleminatoria;

import Design.MenuLateral;
import Frames.CampeonatosFrame;
import Models.Campeonato;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;

public class EditarCampeonatoFrame extends JFrame {

    private final Campeonato campeonato;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);

    private final DateTimeFormatter FORMATO_DATA = DateTimeFormatter
            .ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private JTextField campoNome;
    private JTextField campoDataInicio;
    private JTextField campoDataFimGrupos;
    private JTextField campoDataInicioEliminatoria;
    private JTextField campoDataFim;
    private JTextField campoNumeroEquipas;
    private JTextField campoNumeroEstadios;

    public EditarCampeonatoFrame(Campeonato campeonato) {
        this.campeonato = campeonato;

        if (campeonato == null || !campeonato.isEmConfiguracao()
                || campeonato.isGruposGerados()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Só é possível editar um campeonato antes de ele iniciar e antes de gerar grupos.",
                    "Edição indisponível",
                    JOptionPane.ERROR_MESSAGE
            );
            new CampeonatosFrame();
            return;
        }

        setTitle("Editar Campeonato - " + campeonato.getNome());
        setSize(1120, 740);
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

        JButton btnMenu = new JButton("☰");
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
        centro.setBorder(new EmptyBorder(14, 120, 20, 120));

        JLabel titulo = new JLabel("Editar Campeonato");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Podes alterar estes dados enquanto o campeonato estiver em configuração.");
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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 530));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Dados do Campeonato");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 28);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        campoNome = criarCampo(campeonato.getNome());
        campoDataInicio = criarCampo(campeonato.getDataInicioCampeonato().toString());
        campoDataFimGrupos = criarCampo(campeonato.getDataFimGrupos().toString());
        campoDataInicioEliminatoria = criarCampo(campeonato.getDataInicioEliminatoria().toString());
        campoDataFim = criarCampo(campeonato.getDataFimCampeonato().toString());
        campoNumeroEquipas = criarCampo(String.valueOf(campeonato.getNumeroEquipasNecessarias()));
        campoNumeroEstadios = criarCampo(String.valueOf(campeonato.getNumeroEstadiosNecessarios()));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formulario.add(criarCampoComLabel("Nome do Campeonato", campoNome), gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(criarCampoComLabel("Data de Início", campoDataInicio), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formulario.add(criarCampoComLabel("Fim da Fase de Grupos", campoDataFimGrupos), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formulario.add(criarCampoComLabel("Início da Eliminatória", campoDataInicioEliminatoria), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formulario.add(criarCampoComLabel("Fim do Campeonato", campoDataFim), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formulario.add(criarCampoComLabel("Número de Equipas", campoNumeroEquipas), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formulario.add(criarCampoComLabel("Número de Estádios", campoNumeroEstadios), gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setOpaque(false);

        JButton btnCancelar = criarBotaoCinza("Cancelar");
        JButton btnGuardar = criarBotaoAzul("Guardar Alterações");

        btnCancelar.addActionListener(e -> {
            dispose();
            new CampeonatosFrame();
        });

        btnGuardar.addActionListener(e -> guardarAlteracoes());

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
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
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

    private void guardarAlteracoes() {
        String nome = campoNome.getText().trim();

        if (nome.isEmpty()) {
            mostrarErro("O nome do campeonato é obrigatório.");
            return;
        }

        int numeroEquipas;
        int numeroEstadios;

        try {
            numeroEquipas = Integer.parseInt(campoNumeroEquipas.getText().trim());
            numeroEstadios = Integer.parseInt(campoNumeroEstadios.getText().trim());
        } catch (NumberFormatException e) {
            mostrarErro("O número de equipas e de estádios deve ser numérico.");
            return;
        }

        if (numeroEquipas <= 0 || numeroEstadios <= 0) {
            mostrarErro("O número de equipas e de estádios deve ser maior que zero.");
            return;
        }

        if (numeroEquipas % 4 != 0) {
            mostrarErro("O número de equipas deve ser múltiplo de 4.");
            return;
        }

        if (numeroEquipas < campeonato.getEquipas().size()) {
            mostrarErro("Não podes definir menos equipas do que as já associadas ao campeonato.");
            return;
        }

        if (numeroEstadios < campeonato.getEstadios().size()) {
            mostrarErro("Não podes definir menos estádios do que os já associados ao campeonato.");
            return;
        }

        LocalDate inicio;
        LocalDate fimGrupos;
        LocalDate inicioEliminatoria;
        LocalDate fim;

        try {
            inicio = LocalDate.parse(campoDataInicio.getText().trim(), FORMATO_DATA);
            fimGrupos = LocalDate.parse(campoDataFimGrupos.getText().trim(), FORMATO_DATA);
            inicioEliminatoria = LocalDate.parse(campoDataInicioEliminatoria.getText().trim(), FORMATO_DATA);
            fim = LocalDate.parse(campoDataFim.getText().trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            mostrarErro("As datas devem ter o formato yyyy-MM-dd.");
            return;
        }

        if (fimGrupos.isBefore(inicio)
                || inicioEliminatoria.isBefore(fimGrupos)
                || fim.isBefore(inicioEliminatoria)) {
            mostrarErro("A ordem das datas do campeonato não é válida.");
            return;
        }

        if (!validarCapacidade(numeroEquipas, numeroEstadios, inicio, fimGrupos)) {
            return;
        }

        boolean editado = CampeonatoRepositorio.editarCampeonato(
                campeonato,
                nome,
                numeroEquipas,
                numeroEstadios,
                inicio,
                fimGrupos,
                inicioEliminatoria,
                fim
        );

        if (!editado) {
            mostrarErro("Não foi possível guardar. Confirma se o nome não está repetido e se o campeonato continua em configuração.");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Campeonato atualizado com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
        new CampeonatosFrame();
    }

    private boolean validarCapacidade(
            int numeroEquipas,
            int numeroEstadios,
            LocalDate inicio,
            LocalDate fimGrupos
    ) {
        int totalJogos = (numeroEquipas / 4) * 6;
        long dias = ChronoUnit.DAYS.between(inicio, fimGrupos) + 1;

        if (dias <= 0) {
            mostrarErro("O intervalo da fase de grupos é inválido.");
            return false;
        }

        if (totalJogos > dias * numeroEstadios) {
            mostrarErro("Com estas datas e estádios não é possível calendarizar todos os jogos da fase de grupos.");
            return false;
        }

        return true;
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
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            desenho.setColor(new Color(0, 0, 0, 18));
            desenho.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, raio, raio);
            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, raio, raio);
            desenho.dispose();
            super.paintComponent(g);
        }
    }
}
