package Frames.SeccaoEquipas;

import Design.PlaceholderTextField;
import Design.FormUtils;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.Tema;
import Models.CampeonatoRepositorio;
import Models.Equipa;
import Models.EquipaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class NovaEquipaFrame extends JFrame {

    private final Runnable onEquipaCriada;
    private final EquipaService equipaService = EquipaService.getInstance();

    private PlaceholderTextField campoNome;
    private PlaceholderTextField campoCidade;
    private PlaceholderTextField campoPais;
    private JComboBox<String> campoCampeonato;
    private PlaceholderTextField campoTreinador;
    private PlaceholderTextField campoCapitao;

    public NovaEquipaFrame(Runnable onEquipaCriada) {
        this.onEquipaCriada = onEquipaCriada;

        setTitle("Nova Equipa");
        setSize(1260, 760);
        setMinimumSize(new Dimension(1080, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.COR_FUNDO);
        fundo.setBorder(new EmptyBorder(24, 26, 36, 26));

        fundo.add(criarConteudo(), BorderLayout.CENTER);

        setContentPane(fundo);
        setVisible(true);
    }

    private JPanel criarConteudo() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));

        conteudo.add(criarTopo());
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(criarCorpo());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(18, 76, 0, 90);

        wrapper.add(conteudo, gbc);

        return wrapper;
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Nova Equipa");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Registo de equipa e associação ao campeonato.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        topo.add(titulo);
        topo.add(Box.createVerticalStrut(4));
        topo.add(subtitulo);

        return topo;
    }

    private JPanel criarCorpo() {
        JPanel corpo = new JPanel(new BorderLayout(34, 0));
        corpo.setOpaque(false);
        corpo.setAlignmentX(Component.LEFT_ALIGNMENT);
        corpo.setPreferredSize(new Dimension(960, 520));
        corpo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 535));

        corpo.add(criarFormularioCard(), BorderLayout.CENTER);
        corpo.add(criarRegraCard(), BorderLayout.EAST);

        return corpo;
    }

    private JPanel criarFormularioCard() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setPreferredSize(new Dimension(715, 520));
        card.setMinimumSize(new Dimension(680, 500));
        card.setBorder(new EmptyBorder(26, 26, 18, 26));

        JLabel titulo = new JLabel("Dados da Equipa");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JPanel campos = new JPanel(new GridLayout(3, 2, 32, 22));
        campos.setOpaque(false);
        campos.setBorder(new EmptyBorder(22, 0, 0, 0));
        campos.setPreferredSize(new Dimension(650, 250));
        campos.setMinimumSize(new Dimension(610, 250));

        campoNome = criarCampo("Ex: FC Porto");
        campoCidade = criarCampo("Ex: Porto");
        campoPais = criarCampo("Ex: Portugal");
        campoCampeonato = criarComboCampeonato();
        campoTreinador = criarCampo("Ex: Francesco Farioli");
        campoCapitao = criarCampo("Ex: Diogo Costa");

        campos.add(criarCampoComLabel("Nome da Equipa", campoNome));
        campos.add(criarCampoComLabel("Cidade", campoCidade));
        campos.add(criarCampoComLabel("Pais", campoPais));
        campos.add(criarCampoComLabel("Campeonato", campoCampeonato));
        campos.add(criarCampoComLabel("Treinador", campoTreinador));
        campos.add(criarCampoComLabel("Capitão", campoCapitao));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(campos, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        botoes.setOpaque(false);
        botoes.setBorder(new EmptyBorder(18, 0, 0, 0));
        botoes.add(criarBotao("Cancelar", Tema.COR_BOTAO_SECUNDARIO, Tema.COR_TEXTO_PRINCIPAL, this::dispose));
        botoes.add(criarBotao("Guardar Equipa", Tema.COR_INFO, Tema.COR_TEXTO_CLARO, this::guardarEquipa));

        card.add(titulo, BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        return card;
    }

    private RoundedPanel criarRegraCard() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_VERDE_SUAVE);
        card.setPreferredSize(new Dimension(230, 520));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(26, 20, 26, 20));

        JLabel titulo = new JLabel("Regra principal");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(Tema.COR_VERDE_FORTE);

        JTextArea texto = new JTextArea(
                "A equipa só pode participar oficialmente se tiver exatamente 23 jogadores registados.\n\n" +
                        "Antes do início do campeonato, a informação pode ser editada.\n\n" +
                        "Depois do início, alterações inválidas devem ser bloqueadas."
        );
        texto.setFont(Tema.FONTE_TEXTO);
        texto.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setOpaque(false);

        card.add(titulo, BorderLayout.NORTH);
        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private PlaceholderTextField criarCampo(String placeholder) {
        return FormUtils.criarCampo(placeholder, new Dimension(305, 44), 12);
    }

    private JComboBox<String> criarComboCampeonato() {
        List<String> campeonatos = CampeonatoRepositorio.listarNomesCampeonatosEmPreparacaoComVagas();

        return FormUtils.criarCombo(
                campeonatos.toArray(new String[0]),
                null,
                new Dimension(305, 44),
                12
        );
    }

    private JPanel criarCampoComLabel(String label, JComponent campo) {
        return FormUtils.criarCampoComLabel(label, campo);
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto, Runnable acao) {
        JButton botao = new RoundedButton(texto, fundo, corTexto, 12);
        botao.setPreferredSize(new Dimension(128, 36));
        botao.addActionListener(e -> acao.run());

        return botao;
    }

    private void guardarEquipa() {
        try {
            validarCamposObrigatorios();

            String campeonato = String.valueOf(campoCampeonato.getSelectedItem()).trim();
            validarCampeonatoGuardado(campeonato);

            Equipa equipa = new Equipa(
                    campoNome.getText().trim(),
                    campoCidade.getText().trim(),
                    campoPais.getText().trim(),
                    campoTreinador.getText().trim(),
                    campoCapitao.getText().trim(),
                    campeonato,
                    "Sem grupo",
                    0,
                    true
            );

            equipaService.adicionarEquipa(equipa);
            notificarEquipaCriada();

            JOptionPane.showMessageDialog(
                    this,
                    "Equipa registada com sucesso.",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro de validação",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void validarCampeonatoGuardado(String campeonato) {
        if (CampeonatoRepositorio.procurarPorNome(campeonato) == null) {
            throw new IllegalArgumentException(
                    "Escolhe um campeonato existente e guardado antes de registar a equipa."
            );
        }

        equipaService.validarCampeonatoPodeReceberEquipa(campeonato);
    }

    private void validarCamposObrigatorios() {
        if (campoNome.getText().trim().isEmpty()
                || campoCidade.getText().trim().isEmpty()
                || campoPais.getText().trim().isEmpty()
                || campoTreinador.getText().trim().isEmpty()
                || campoCapitao.getText().trim().isEmpty()
                || campoCampeonato.getSelectedItem() == null
                || String.valueOf(campoCampeonato.getSelectedItem()).trim().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
        }
    }

    private void notificarEquipaCriada() {
        if (onEquipaCriada != null) {
            onEquipaCriada.run();
        }
    }
}
