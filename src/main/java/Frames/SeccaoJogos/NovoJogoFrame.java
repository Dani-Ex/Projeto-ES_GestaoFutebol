package Frames.SeccaoJogos;

import Design.MenuLateral;
import Design.RoundedBorder;
import Design.RoundedButton;
import Design.RoundedPanel;
import Models.CampeonatoRepositorio;
import Models.Campeonato;
import Models.Estadio;
import Models.Jogo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class NovoJogoFrame extends JFrame {

    private final Runnable onJogoCriado;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);

    private final DateTimeFormatter FORMATO_DATA = DateTimeFormatter
            .ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);

    private final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private JComboBox<Campeonato> comboCampeonato;
    private JComboBox<String> comboEquipaA;
    private JComboBox<String> comboEquipaB;
    private JComboBox<String> comboEstadio;
    private JTextField campoData;
    private JTextField campoHora;
    private JTextField campoFase;

    public NovoJogoFrame() {
        this(null);
    }

    public NovoJogoFrame(Runnable onJogoCriado) {
        this.onJogoCriado = onJogoCriado;

        setTitle("Criar Jogo");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        JLabel titulo = new JLabel("Criar Jogo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Escolhe o campeonato, equipas, estádio, data e hora do novo jogo.");
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
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 510));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Dados do Jogo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 28);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        comboCampeonato = criarComboCampeonatos();
        comboEquipaA = criarComboTexto();
        comboEquipaB = criarComboTexto();
        comboEstadio = criarComboTexto();
        campoData = criarCampo(LocalDate.now().toString());
        campoHora = criarCampo("20:00");
        campoFase = criarCampo("Fase de grupos");

        comboCampeonato.addActionListener(e -> carregarDadosDoCampeonatoSelecionado());
        carregarDadosDoCampeonatoSelecionado();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formulario.add(criarCampoComLabel("Campeonato", comboCampeonato), gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(criarCampoComLabel("Equipa A", comboEquipaA), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formulario.add(criarCampoComLabel("Equipa B", comboEquipaB), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formulario.add(criarCampoComLabel("Estádio", comboEstadio), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formulario.add(criarCampoComLabel("Fase / Grupo", campoFase), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formulario.add(criarCampoComLabel("Data (yyyy-MM-dd)", campoData), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formulario.add(criarCampoComLabel("Hora (HH:mm)", campoHora), gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setOpaque(false);

        JButton btnCancelar = criarBotaoCinza("Cancelar");
        JButton btnCriar = criarBotaoAzul("Criar Jogo");

        btnCancelar.addActionListener(e -> {
            dispose();
        });
        btnCriar.addActionListener(e -> criarJogo());

        botoes.add(btnCancelar);
        botoes.add(btnCriar);

        card.add(titulo, BorderLayout.NORTH);
        card.add(formulario, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        return card;
    }

    private JComboBox<Campeonato> criarComboCampeonatos() {
        JComboBox<Campeonato> combo = new JComboBox<>();

        for (Campeonato campeonato : CampeonatoRepositorio.listar()) {
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
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Campeonato campeonato) {
                    label.setText(campeonato.getNome() + " | " + campeonato.getEstado());
                }

                return label;
            }
        });

        return combo;
    }

    private JComboBox<String> criarComboTexto() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return combo;
    }

    private void carregarDadosDoCampeonatoSelecionado() {
        Campeonato campeonato = (Campeonato) comboCampeonato.getSelectedItem();

        comboEquipaA.removeAllItems();
        comboEquipaB.removeAllItems();
        comboEstadio.removeAllItems();

        if (campeonato == null) {
            return;
        }

        CampeonatoRepositorio.sincronizarEquipasDoTsv(campeonato);

        for (String equipa : campeonato.getEquipas()) {
            comboEquipaA.addItem(equipa);
            comboEquipaB.addItem(equipa);
        }

        for (Estadio estadio : campeonato.getEstadios()) {
            comboEstadio.addItem(estadio.getNome());
        }

        campoData.setText(campeonato.getDataInicioCampeonato().toString());

        if (campeonato.isGruposGerados() && !campeonato.getGrupos().isEmpty()) {
            campoFase.setText(campeonato.getGrupos().keySet().iterator().next());
        } else {
            campoFase.setText("Fase de grupos");
        }
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

    private void criarJogo() {
        Campeonato campeonato = (Campeonato) comboCampeonato.getSelectedItem();
        String equipaA = (String) comboEquipaA.getSelectedItem();
        String equipaB = (String) comboEquipaB.getSelectedItem();
        String estadio = (String) comboEstadio.getSelectedItem();
        String fase = campoFase.getText().trim();

        if (campeonato == null || equipaA == null || equipaB == null || estadio == null) {
            mostrarErro("O campeonato precisa de ter equipas e estádios associados antes de criar um jogo.");
            return;
        }

        if (campeonato.isFinalizado()) {
            mostrarErro("Não podes criar jogos num campeonato finalizado.");
            return;
        }

        if (equipaA.equalsIgnoreCase(equipaB)) {
            mostrarErro("A Equipa A e a Equipa B têm de ser diferentes.");
            return;
        }

        if (fase.isEmpty()) {
            mostrarErro("Indica a fase ou o grupo do jogo.");
            return;
        }

        LocalDate data;
        LocalTime hora;

        try {
            data = LocalDate.parse(campoData.getText().trim(), FORMATO_DATA);
            hora = LocalTime.parse(campoHora.getText().trim(), FORMATO_HORA);
        } catch (DateTimeParseException e) {
            mostrarErro("A data deve usar yyyy-MM-dd e a hora deve usar HH:mm.");
            return;
        }

        if (data.isBefore(campeonato.getDataInicioCampeonato())
                || data.isAfter(campeonato.getDataFimCampeonato())) {
            mostrarErro("A data do jogo tem de estar dentro do período do campeonato.");
            return;
        }

        String conflito = validarConflitos(campeonato, data.toString(), hora.toString(), estadio, equipaA, equipaB);

        if (!conflito.isEmpty()) {
            mostrarErro(conflito);
            return;
        }

        Jogo jogo = new Jogo(
                CampeonatoRepositorio.proximoIdJogo(),
                data.toString(),
                hora.toString(),
                equipaA,
                equipaB,
                estadio,
                fase,
                "Agendado",
                "-",
                campeonato.getNome()
        );

        if (!CampeonatoRepositorio.adicionarJogo(campeonato, jogo)) {
            mostrarErro("Não foi possível guardar o jogo.");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Jogo criado e guardado em data/jogos.tsv.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        if (onJogoCriado != null) {
            onJogoCriado.run();
        }

        dispose();
    }

    private String validarConflitos(
            Campeonato campeonatoSelecionado,
            String data,
            String hora,
            String estadio,
            String equipaA,
            String equipaB
    ) {
        for (Campeonato campeonato : CampeonatoRepositorio.listar()) {
            for (Jogo jogo : campeonato.getJogos()) {
                if (!jogo.getData().equals(data) || !jogo.getHora().equals(hora)) {
                    continue;
                }

                if (jogo.getEstadio().equalsIgnoreCase(estadio)) {
                    return "Este estádio já tem um jogo agendado nessa data e hora.";
                }

                if (campeonato == campeonatoSelecionado
                        && (jogo.getEquipaA().equalsIgnoreCase(equipaA)
                        || jogo.getEquipaB().equalsIgnoreCase(equipaA)
                        || jogo.getEquipaA().equalsIgnoreCase(equipaB)
                        || jogo.getEquipaB().equalsIgnoreCase(equipaB))) {
                    return "Uma das equipas já tem um jogo agendado nessa data e hora.";
                }
            }
        }

        return "";
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
