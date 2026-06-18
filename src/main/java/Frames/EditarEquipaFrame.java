package Frames;

import Design.PlaceholderTextField;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.Tema;
import Models.Equipa;
import Models.EquipaService;
import Models.JogadorService;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditarEquipaFrame extends JFrame {

    private final Equipa equipa;
    private final Runnable onEquipaAtualizada;
    private final EquipaService equipaService = new EquipaService();

    private PlaceholderTextField campoNome;
    private PlaceholderTextField campoCidade;
    private PlaceholderTextField campoPais;
    private JComboBox<String> campoCampeonato;
    private PlaceholderTextField campoTreinador;
    private PlaceholderTextField campoCapitao;

    public EditarEquipaFrame(Equipa equipa, Runnable onEquipaAtualizada) {
        this.equipa = equipa;
        this.onEquipaAtualizada = onEquipaAtualizada;

        setTitle("Editar Equipa - " + equipa.getNome());
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

        JLabel titulo = new JLabel("Editar Equipa");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Atualização dos dados da equipa selecionada.");
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

        campoNome = criarCampo(equipa.getNome());
        campoCidade = criarCampo(equipa.getCidade());
        campoPais = criarCampo(equipa.getPais());
        campoCampeonato = criarComboCampeonato();
        campoTreinador = criarCampo(equipa.getTreinador());
        campoCapitao = criarCampo(equipa.getCapitao());

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
        botoes.add(criarBotao("Cancelar", new Color(241, 245, 249), Tema.COR_TEXTO_PRINCIPAL, this::dispose));
        botoes.add(criarBotao("Guardar Alterações", Tema.COR_INFO, Tema.COR_TEXTO_CLARO, this::guardarAlteracoes));

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
                "A equipa pode ser editada antes do início do campeonato.\n\n" +
                        "Todos os campos devem estar preenchidos.\n\n" +
                        "Não pode existir outra equipa com o mesmo nome no mesmo campeonato."
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

    private PlaceholderTextField criarCampo(String valor) {
        PlaceholderTextField campo = new PlaceholderTextField("");
        campo.setText(valor == null ? "" : valor);
        campo.setPreferredSize(new Dimension(305, 44));
        campo.setMinimumSize(new Dimension(240, 44));
        campo.setFont(Tema.FONTE_TEXTO_PEQUENO);
        campo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        campo.setBackground(Tema.COR_INPUT);
        campo.setOpaque(true);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Tema.COR_LINHA, 12),
                new EmptyBorder(0, 13, 0, 13)
        ));

        return campo;
    }

    private JComboBox<String> criarComboCampeonato() {
        JComboBox<String> combo = new JComboBox<>(
                equipaService.listarCampeonatos().toArray(new String[0])
        );
        combo.setSelectedItem(equipa.getCampeonato());
        combo.setPreferredSize(new Dimension(305, 44));
        combo.setMinimumSize(new Dimension(240, 44));
        combo.setFont(Tema.FONTE_TEXTO_PEQUENO);
        combo.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        combo.setBackground(Tema.COR_INPUT);
        combo.setFocusable(false);
        combo.setBorder(new RoundedBorder(Tema.COR_LINHA, 12));

        return combo;
    }

    private JPanel criarCampoComLabel(String label, JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 12));
        lbl.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        painel.add(lbl, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);

        return painel;
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto, Runnable acao) {
        JButton botao = new RoundedButton(texto, fundo, corTexto, 12);
        botao.setPreferredSize(new Dimension(150, 38));
        botao.addActionListener(e -> acao.run());

        return botao;
    }

    private void guardarAlteracoes() {
        try {
            validarCamposObrigatorios();

            String novoNome = campoNome.getText().trim();
            String novoCampeonato = String.valueOf(campoCampeonato.getSelectedItem()).trim();
            String nomeAntigo = equipa.getNome();
            String campeonatoAntigo = equipa.getCampeonato();

            if (equipaService.outraEquipaExisteNoCampeonato(equipa, novoNome, novoCampeonato)) {
                throw new IllegalArgumentException(
                        "Essa equipa não pode ser registrada outra vez no mesmo campeonato."
                );
            }

            equipa.setNome(novoNome);
            equipa.setCidade(campoCidade.getText().trim());
            equipa.setPais(campoPais.getText().trim());
            equipa.setCampeonato(novoCampeonato);
            equipa.setTreinador(campoTreinador.getText().trim());
            equipa.setCapitao(campoCapitao.getText().trim());

            new JogadorService().atualizarEquipaDosJogadores(
                    nomeAntigo,
                    campeonatoAntigo,
                    novoNome,
                    novoCampeonato
            );
            equipaService.guardarAlteracoes();

            if (onEquipaAtualizada != null) {
                onEquipaAtualizada.run();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Equipa atualizada com sucesso.",
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

    private static class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int radius;

        private RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 1;
            insets.left = 1;
            insets.bottom = 1;
            insets.right = 1;
            return insets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}
