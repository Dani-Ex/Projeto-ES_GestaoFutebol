import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NovoCampeonatoFrame extends JFrame {

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(120, 130, 150);
    private final Color BLUE = new Color(37, 99, 235);

    private JTextField campoNome;
    private JTextField campoDataInicioCampeonato;
    private JTextField campoDataFimGrupos;
    private JTextField campoDataInicioEliminatoria;
    private JTextField campoDataFimCampeonato;
    private JTextField campoNumeroEquipas;
    private JTextField campoNumeroEstadios;

    public NovoCampeonatoFrame() {
        setTitle("Novo Campeonato");
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

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);

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

        JLabel subtitulo = new JLabel("Criação de um novo campeonato com fases, datas, grupos e regras.");
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
        JPanel card = new PainelArredondado(18, Color.WHITE);
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
        campoDataInicioCampeonato = criarCampoTexto("Selecionar data");
        campoDataFimGrupos = criarCampoTexto("Selecionar data");
        campoDataInicioEliminatoria = criarCampoTexto("Selecionar data");
        campoDataFimCampeonato = criarCampoTexto("Selecionar data");
        campoNumeroEquipas = criarCampoTexto("Ex: 24");
        campoNumeroEstadios = criarCampoTexto("Ex: 6");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formulario.add(criarCampoComLabel("Nome do Campeonato", campoNome), gbc);

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
        formulario.add(criarCampoComLabel("Número de Estádios", campoNumeroEstadios), gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botoes.setOpaque(false);

        JButton cancelar = criarBotaoCinza("Cancelar");
        cancelar.addActionListener(e -> {
            dispose();
            new CampeonatosFrame();
        });

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
        JPanel card = new PainelArredondado(18, new Color(231, 240, 253));
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

                • Cada grupo deve ter número par de equipas.

                • O sistema deve verificar se as datas permitem terminar todos os jogos.

                • O calendário depende da quantidade de estádios disponíveis.

                • 1.º e 2.º lugar passam para a fase mata-mata.
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
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        campo.setText("");
        campo.putClientProperty("placeholder", placeholder);

        campo.setToolTipText(placeholder);

        return campo;
    }

    private JButton criarBotaoAzul(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(BLUE);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(12, 20, 12, 20));

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
        botao.setBorder(new EmptyBorder(12, 20, 12, 20));

        return botao;
    }

    private void criarCampeonato() {
        String nome = campoNome.getText().trim();
        String inicioCampeonato = campoDataInicioCampeonato.getText().trim();
        String fimGrupos = campoDataFimGrupos.getText().trim();
        String inicioEliminatoria = campoDataInicioEliminatoria.getText().trim();
        String fimCampeonato = campoDataFimCampeonato.getText().trim();
        String numeroEquipasTexto = campoNumeroEquipas.getText().trim();
        String numeroEstadiosTexto = campoNumeroEstadios.getText().trim();

        if (nome.isEmpty()) {
            mostrarErro("O nome do campeonato é obrigatório.");
            return;
        }

        if (inicioCampeonato.isEmpty() || fimGrupos.isEmpty() || inicioEliminatoria.isEmpty() || fimCampeonato.isEmpty()) {
            mostrarErro("Todas as datas são obrigatórias.");
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

        CampeonatoRepositorio.adicionar(new Campeonato(nome));

        JOptionPane.showMessageDialog(
                this,
                "Campeonato criado com sucesso:\n" + nome,
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
        new CampeonatosFrame();

        dispose();
        new CampeonatosFrame();
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
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

            desenho.setColor(new Color(0, 0, 0, 18));
            desenho.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.dispose();

            super.paintComponent(g);
        }
    }
}