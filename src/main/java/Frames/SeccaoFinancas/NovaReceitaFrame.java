package Frames.SeccaoFinancas;

import Design.FormUtils;
import Design.PlaceholderTextField;
import Design.RoundedBorder;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.Tema;
import Models.Jogo;
import Models.JogoService;
import Models.Receita;
import Models.ReceitaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NovaReceitaFrame extends JFrame {

    private final Runnable onReceitaCriada;
    private final JogoService jogoService = new JogoService();
    private final ReceitaService receitaService = new ReceitaService();
    private final List<JogoOpcao> jogosDisponiveis = new ArrayList<>();

    private JComboBox<JogoOpcao> campoJogo;
    private PlaceholderTextField campoCampeonato;
    private PlaceholderTextField campoDia;
    private PlaceholderTextField campoBilhetes;
    private PlaceholderTextField campoBilheteira;
    private PlaceholderTextField campoPatrocinio;
    private PlaceholderTextField campoDireitosTv;

    public NovaReceitaFrame(Runnable onReceitaCriada) {
        this.onReceitaCriada = onReceitaCriada;

        setTitle("Nova Receita");
        setSize(1260, 760);
        setMinimumSize(new Dimension(1080, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        carregarJogosDisponiveis();

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.COR_FUNDO);
        fundo.setBorder(new EmptyBorder(24, 26, 36, 26));
        fundo.add(criarConteudo(), BorderLayout.CENTER);

        setContentPane(fundo);
        setVisible(true);
    }

    private void carregarJogosDisponiveis() {
        jogosDisponiveis.clear();

        for (Jogo jogo : jogoService.listarJogos()) {
            if (jogoEstaDisponivel(jogo)) {
                jogosDisponiveis.add(new JogoOpcao(jogo));
            }
        }
    }

    private boolean jogoEstaDisponivel(Jogo jogo) {
        String estado = jogo.getEstado() == null ? "" : jogo.getEstado().trim();
        boolean finalizado = estado.equalsIgnoreCase("Realizado")
                || estado.equalsIgnoreCase("Finalizado");

        return finalizado && !receitaService.existeReceitaParaJogo(jogo.getId());
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

        JLabel titulo = new JLabel("Nova Receita");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Registo financeiro associado a um jogo finalizado.");
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

        JLabel titulo = new JLabel("Dados da Receita");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JPanel campos = new JPanel(new GridLayout(4, 2, 32, 22));
        campos.setOpaque(false);
        campos.setBorder(new EmptyBorder(22, 0, 0, 0));
        campos.setPreferredSize(new Dimension(650, 360));
        campos.setMinimumSize(new Dimension(610, 360));

        campoJogo = criarComboJogos();
        campoCampeonato = criarCampoBloqueado();
        campoDia = criarCampoBloqueado();
        campoBilhetes = criarCampo("Ex: 41532");
        campoBilheteira = criarCampo("Ex: 120000");
        campoPatrocinio = criarCampo("Ex: 50000");
        campoDireitosTv = criarCampo("Ex: 80000");

        campos.add(criarCampoComLabel("Jogo", campoJogo));
        campos.add(criarCampoComLabel("Campeonato", campoCampeonato));
        campos.add(criarCampoComLabel("Dia", campoDia));
        campos.add(criarCampoComLabel("Bilhetes Vendidos", campoBilhetes));
        campos.add(criarCampoComLabel("Bilheteira", campoBilheteira));
        campos.add(criarCampoComLabel("Patroc\u00EDnio", campoPatrocinio));
        campos.add(criarCampoComLabel("Direitos TV", campoDireitosTv));

        atualizarDadosDoJogoSelecionado();

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(campos, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        botoes.setOpaque(false);
        botoes.setBorder(new EmptyBorder(18, 0, 0, 0));
        botoes.add(criarBotao("Cancelar", Tema.COR_BOTAO_SECUNDARIO, Tema.COR_TEXTO_PRINCIPAL, this::dispose));
        botoes.add(criarBotao("Guardar Receita", Tema.COR_INFO, Tema.COR_TEXTO_CLARO, this::guardarReceita));

        card.add(titulo, BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        return card;
    }

    private RoundedPanel criarRegraCard() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.CARD_AZUL);
        card.setPreferredSize(new Dimension(230, 520));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(26, 20, 26, 20));

        JLabel titulo = new JLabel("Regra financeira");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(Tema.CARD_TEXTO_AZUL);

        JTextArea texto = new JTextArea(
                "A receita fica associada a um jogo finalizado.\n\n" +
                        "Cada jogo s\u00F3 pode ter uma receita registada.\n\n" +
                        "O lucro \u00E9 calculado automaticamente pela soma da bilheteira, patroc\u00EDnio e direitos TV."
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

    private JComboBox<JogoOpcao> criarComboJogos() {
        JComboBox<JogoOpcao> combo = new JComboBox<>(jogosDisponiveis.toArray(new JogoOpcao[0]));
        combo.setPreferredSize(new Dimension(305, 44));
        combo.setMinimumSize(new Dimension(210, 44));
        combo.setFont(Tema.FONTE_TEXTO_PEQUENO);
        combo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        combo.setBackground(Tema.COR_INPUT);
        combo.setFocusable(false);
        combo.setBorder(new RoundedBorder(Tema.COR_LINHA, 12));
        combo.setEnabled(!jogosDisponiveis.isEmpty());
        combo.addActionListener(e -> atualizarDadosDoJogoSelecionado());

        return combo;
    }

    private PlaceholderTextField criarCampo(String placeholder) {
        return FormUtils.criarCampo(placeholder, new Dimension(305, 44), 12);
    }

    private PlaceholderTextField criarCampoBloqueado() {
        PlaceholderTextField campo = FormUtils.criarCampo("", new Dimension(305, 44), 12);
        campo.setEditable(false);
        campo.setFocusable(false);
        campo.setBackground(new Color(241, 245, 249));
        return campo;
    }

    private JPanel criarCampoComLabel(String label, JComponent campo) {
        return FormUtils.criarCampoComLabel(label, campo);
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto, Runnable acao) {
        JButton botao = new RoundedButton(texto, fundo, corTexto, 12);
        botao.setPreferredSize(new Dimension(142, 36));
        botao.addActionListener(e -> acao.run());

        return botao;
    }

    private void atualizarDadosDoJogoSelecionado() {
        JogoOpcao opcao = (JogoOpcao) campoJogo.getSelectedItem();

        if (opcao == null) {
            campoCampeonato.setText("");
            campoDia.setText("");
            return;
        }

        campoCampeonato.setText(opcao.jogo.getCampeonato());
        campoDia.setText(opcao.jogo.getData());
    }

    private void guardarReceita() {
        try {
            JogoOpcao opcao = (JogoOpcao) campoJogo.getSelectedItem();

            if (opcao == null) {
                throw new IllegalArgumentException("N\u00E3o existem jogos finalizados dispon\u00EDveis para registar receita.");
            }

            Receita receita = new Receita(
                    opcao.jogo.getId(),
                    lerInteiroPositivo(campoBilhetes.getText(), "Bilhetes vendidos"),
                    lerValorPositivo(campoBilheteira.getText(), "Bilheteira"),
                    lerValorPositivo(campoPatrocinio.getText(), "Patroc\u00EDnio"),
                    lerValorPositivo(campoDireitosTv.getText(), "Direitos TV")
            );

            receitaService.adicionarReceita(receita);
            notificarReceitaCriada();

            JOptionPane.showMessageDialog(
                    this,
                    "Receita registada com sucesso.",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro de valida\u00E7\u00E3o",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int lerInteiroPositivo(String valor, String campo) {
        double numero = lerValorPositivo(valor, campo);

        if (numero != Math.rint(numero)) {
            throw new IllegalArgumentException(campo + " deve ser um n\u00FAmero inteiro.");
        }

        return (int) numero;
    }

    private double lerValorPositivo(String valor, String campo) {
        String normalizado = valor == null ? "" : valor.trim().replace(".", "").replace(",", ".");

        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
        }

        try {
            double numero = Double.parseDouble(normalizado);

            if (numero < 0) {
                throw new NumberFormatException();
            }

            return numero;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " deve ser um valor num\u00E9rico positivo.");
        }
    }

    private void notificarReceitaCriada() {
        if (onReceitaCriada != null) {
            onReceitaCriada.run();
        }
    }

    private static class JogoOpcao {
        private final Jogo jogo;

        private JogoOpcao(Jogo jogo) {
            this.jogo = jogo;
        }

        @Override
        public String toString() {
            return jogo.getNomeJogo();
        }
    }
}
