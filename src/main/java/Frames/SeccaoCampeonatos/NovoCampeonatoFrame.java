package Frames.SeccaoCampeonatos;

import Design.MenuLateral;
import Design.RoundedBorder;
import Design.RoundedButton;
import Design.RoundedPanel;
import Models.Campeonatos.Campeonato;
import Models.Campeonatos.CampeonatoRepositorio;

import java.time.temporal.ChronoUnit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class NovoCampeonatoFrame extends JFrame {

    private final Runnable onCampeonatoCriado;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(120, 130, 150);
    private final Color BLUE = new Color(37, 99, 235);

    private final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private JTextField campoNome;
    private JTextField campoDataInicioCampeonato;
    private JTextField campoDataFimGrupos;
    private JTextField campoDataInicioEliminatoria;
    private JTextField campoDataFimCampeonato;
    private JTextField campoNumeroEquipas;
    private JTextField campoNumeroEstadios;

    public NovoCampeonatoFrame() {
        this(null);
    }

    public NovoCampeonatoFrame(Runnable onCampeonatoCriado) {
        this.onCampeonatoCriado = onCampeonatoCriado;

        setTitle("Novo Campeonato");
        setSize(1260, 760);
        setMinimumSize(new Dimension(1080, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);

        JButton botaoMenu = new JButton("=");
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

        barraSuperior.add(botaoMenu, BorderLayout.WEST);
        pagina.add(barraSuperior, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(10, 130, 20, 130));

        JLabel titulo = new JLabel("Novo Campeonato");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Criação de campeonato em estado de configuração.");
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

        conteudo.add(criarFormulario(), BorderLayout.CENTER);
        conteudo.add(criarCardValidacoes(), BorderLayout.EAST);

        centro.add(conteudo);

        pagina.add(centro, BorderLayout.CENTER);

        return pagina;
    }

    private JPanel criarFormulario() {
        JPanel card = new RoundedPanel(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(28, 28, 20, 28));
        card.setPreferredSize(new Dimension(700, 520));

        JLabel tituloCard = new JLabel("Dados do Campeonato");
        tituloCard.setFont(new Font("Segoe UI", Font.BOLD, 22));
        tituloCard.setForeground(TEXT);

        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 32);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        campoNome = criarCampoTexto("Ex: Liga Primavera 2026");
        campoDataInicioCampeonato = criarCampoTexto("Ex: 2026-07-01");
        campoDataFimGrupos = criarCampoTexto("Ex: 2026-07-10");
        campoDataInicioEliminatoria = criarCampoTexto("Ex: 2026-07-12");
        campoDataFimCampeonato = criarCampoTexto("Ex: 2026-07-22");
        campoNumeroEquipas = criarCampoTexto("Ex: 24");
        campoNumeroEstadios = criarCampoTexto("Ex: 6");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formulario.add(criarCampoComLabel("Nome do Campeonato", campoNome), gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 1;
        formulario.add(criarCampoComLabel("Data de Início do Campeonato", campoDataInicioCampeonato), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formulario.add(criarCampoComLabel("Data de Fim da Fase de Grupos", campoDataFimGrupos), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formulario.add(criarCampoComLabel("Data de Início da Fase Eliminatória", campoDataInicioEliminatoria), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        formulario.add(criarCampoComLabel("Data de Fim do Campeonato", campoDataFimCampeonato), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formulario.add(criarCampoComLabel("Número de Equipas", campoNumeroEquipas), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        formulario.add(criarCampoComLabel("Número de Estádios Necessários", campoNumeroEstadios), gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setOpaque(false);

        JButton cancelar = criarBotaoCinza("Cancelar");
        cancelar.addActionListener(e -> dispose());

        JButton criar = criarBotaoAzul("Criar Campeonato");
        criar.addActionListener(e -> criarCampeonato());

        botoes.add(cancelar);
        botoes.add(criar);

        card.add(tituloCard, BorderLayout.NORTH);
        card.add(formulario, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        return card;
    }

    private JPanel criarCardValidacoes() {
        JPanel card = new RoundedPanel(18, new Color(231, 240, 253));
        card.setPreferredSize(new Dimension(230, 520));
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
                Validações

                Formato obrigatório:
                yyyy-MM-dd

                Exemplo:
                2026-07-01

                Ordem correta:

                • Início do campeonato
                • Fim da fase de grupos
                • Início da eliminatória
                • Fim do campeonato

                O número de equipas deve ser
                múltiplo de 4.
                """);

        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCampoComLabel(String label, JTextField campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setOpaque(false);

        JLabel texto = new JLabel(label);
        texto.setFont(new Font("Segoe UI", Font.BOLD, 13));
        texto.setForeground(TEXT);

        painel.add(texto, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);

        return painel;
    }

    private JTextField criarCampoTexto(String placeholder) {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setForeground(TEXT);
        campo.setToolTipText(placeholder);

        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(203, 213, 225), 8),
                new EmptyBorder(10, 12, 10, 12)
        ));

        return campo;
    }

    private void criarCampeonato() {
        String nome = campoNome.getText().trim();
        String inicioCampeonatoTexto = campoDataInicioCampeonato.getText().trim();
        String fimGruposTexto = campoDataFimGrupos.getText().trim();
        String inicioEliminatoriaTexto = campoDataInicioEliminatoria.getText().trim();
        String fimCampeonatoTexto = campoDataFimCampeonato.getText().trim();
        String numeroEquipasTexto = campoNumeroEquipas.getText().trim();
        String numeroEstadiosTexto = campoNumeroEstadios.getText().trim();

        if (nome.isEmpty()) {
            mostrarErro("O nome do campeonato é obrigatório.");
            return;
        }

        if (inicioCampeonatoTexto.isEmpty()
                || fimGruposTexto.isEmpty()
                || inicioEliminatoriaTexto.isEmpty()
                || fimCampeonatoTexto.isEmpty()) {
            mostrarErro("Todas as datas são obrigatórias.");
            return;
        }

        LocalDate inicioCampeonato;
        LocalDate fimGrupos;
        LocalDate inicioEliminatoria;
        LocalDate fimCampeonato;

        try {
            inicioCampeonato = LocalDate.parse(inicioCampeonatoTexto, FORMATO_DATA);
            fimGrupos = LocalDate.parse(fimGruposTexto, FORMATO_DATA);
            inicioEliminatoria = LocalDate.parse(inicioEliminatoriaTexto, FORMATO_DATA);
            fimCampeonato = LocalDate.parse(fimCampeonatoTexto, FORMATO_DATA);
        } catch (DateTimeParseException e) {
            mostrarErro("As datas devem estar no formato correto: yyyy-MM-dd.\nExemplo: 2026-07-01");
            return;
        }

        if (fimGrupos.isBefore(inicioCampeonato)) {
            mostrarErro("A data de fim da fase de grupos não pode ser antes da data de início do campeonato.");
            return;
        }

        if (inicioEliminatoria.isBefore(fimGrupos)) {
            mostrarErro("A data de início da fase eliminatória não pode ser antes do fim da fase de grupos.");
            return;
        }

        if (fimCampeonato.isBefore(inicioEliminatoria)) {
            mostrarErro("A data de fim do campeonato não pode ser antes do início da fase eliminatória.");
            return;
        }

        if (fimCampeonato.isBefore(inicioCampeonato)) {
            mostrarErro("A data de fim do campeonato não pode ser antes da data de início do campeonato.");
            return;
        }

        if (numeroEquipasTexto.isEmpty() || numeroEstadiosTexto.isEmpty()) {
            mostrarErro("O número de equipas e o número de estádios são obrigatórios.");
            return;
        }

        int numeroEquipas;
        int numeroEstadios;

        try {
            numeroEquipas = Integer.parseInt(numeroEquipasTexto);
            numeroEstadios = Integer.parseInt(numeroEstadiosTexto);
        } catch (NumberFormatException e) {
            mostrarErro("O número de equipas e o número de estádios devem ser valores numéricos.");
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

        if (!validarCapacidadeJogosPorEstadios(
                numeroEquipas,
                numeroEstadios,
                inicioCampeonato,
                fimGrupos
        )) {
            return;
        }

        if (CampeonatoRepositorio.existeCampeonatoComNome(nome)) {
            mostrarErro("Já existe um campeonato com esse nome.");
            return;
        }

        Campeonato campeonato = new Campeonato(
                nome,
                numeroEquipas,
                numeroEstadios,
                inicioCampeonato,
                fimGrupos,
                inicioEliminatoria,
                fimCampeonato
        );

        CampeonatoRepositorio.adicionar(campeonato);

        JOptionPane.showMessageDialog(
                this,
                "Campeonato criado com sucesso.\n\nEstado: Em configuração\n\nAgora adiciona equipas e estádios antes de iniciar.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        if (onCampeonatoCriado != null) {
            onCampeonatoCriado.run();
        }

        new GruposFrame(campeonato);
        dispose();
    }

    private boolean validarCapacidadeJogosPorEstadios(
            int numeroEquipas,
            int numeroEstadios,
            LocalDate inicioCampeonato,
            LocalDate fimGrupos
    ) {
        int numeroGrupos = numeroEquipas / 4;

        // Cada grupo de 4 equipas gera 6 jogos:
        // A vs B, A vs C, A vs D, B vs C, B vs D, C vs D
        int jogosPorGrupo = 6;

        int totalJogosFaseGrupos = numeroGrupos * jogosPorGrupo;

        long diasDisponiveis = ChronoUnit.DAYS.between(inicioCampeonato, fimGrupos) + 1;

        if (diasDisponiveis <= 0) {
            mostrarErro("O intervalo da fase de grupos é inválido.");
            return false;
        }

        long capacidadeMaximaJogos = diasDisponiveis * numeroEstadios;

        if (totalJogosFaseGrupos > capacidadeMaximaJogos) {
            long estadiosMinimosNecessarios = (long) Math.ceil((double) totalJogosFaseGrupos / diasDisponiveis);
            long diasMinimosNecessarios = (long) Math.ceil((double) totalJogosFaseGrupos / numeroEstadios);

            mostrarErro(
                    "Não é possível calendarizar a fase de grupos com estes dados.\n\n"
                            + "Total de jogos da fase de grupos: " + totalJogosFaseGrupos + "\n"
                            + "Dias disponíveis: " + diasDisponiveis + "\n"
                            + "Estádios disponíveis: " + numeroEstadios + "\n"
                            + "Capacidade máxima: " + capacidadeMaximaJogos + " jogos\n\n"
                            + "Para cumprir as regras dos estádios, precisas de pelo menos:\n"
                            + "- " + estadiosMinimosNecessarios + " estádios nesse intervalo de datas; ou\n"
                            + "- " + diasMinimosNecessarios + " dias com " + numeroEstadios + " estádios."
            );

            return false;
        }

        return true;
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

    private JButton criarBotao(String texto, Color fundo, Color corTexto) {
        JButton botao = new RoundedButton(texto, fundo, corTexto, 14);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setBorder(new EmptyBorder(12, 20, 12, 20));
        return botao;
    }
}
