package Frames.SeccaoJogadores;

import Design.ModernScrollBarUI;
import Design.RoundedBorder;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.Tema;
import Models.Jogador;
import Models.JogadorService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EditarJogadorFrame extends JFrame {

    private final Jogador jogador;
    private final Runnable onGuardar;

    private JTextField campoNome;
    private JTextField campoNumero;
    private JComboBox<String> campoPosicao;
    private JTextField campoPeso;
    private JTextField campoAltura;
    private JComboBox<String> campoPeDominante;
    private JTextField campoDataNascimento;
    private JTextField campoPaisOrigem;
    private JTextField campoCidadeNascimento;
    private JTextField campoEquipa;
    private JTextField campoGrupo;
    private JTextField campoRanking;

    private JTextField campoJogos;
    private JTextField campoGolos;
    private JTextField campoAssistencias;
    private JTextField campoCartoes;
    private JTextField campoMinutos;

    private JTextField campoFinalizacao;
    private JTextField campoPasseCriacao;
    private JTextField campoDisciplina;

    private JComboBox<String> campoEstado;

    public EditarJogadorFrame(Jogador jogador, Runnable onGuardar) {
        this.jogador = jogador;
        this.onGuardar = onGuardar;

        setTitle("Editar Jogador - " + jogador.getNome());
        setSize(1250, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.COR_FUNDO);
        fundo.setBorder(new EmptyBorder(24, 35, 24, 35));

        fundo.add(criarTopo(), BorderLayout.NORTH);
        fundo.add(criarCentro(), BorderLayout.CENTER);

        setContentPane(fundo);
        setVisible(true);

        SwingUtilities.invokeLater(() -> {
            campoNome.requestFocusInWindow();
            campoNome.selectAll();
        });
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setBorder(new EmptyBorder(0, 0, 22, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Editar Jogador");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Atualização dos dados do jogador selecionado.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        topo.add(textos, BorderLayout.WEST);

        return topo;
    }

    private JPanel criarCentro() {
        JPanel centro = new JPanel(new BorderLayout(28, 0));
        centro.setOpaque(false);

        centro.add(criarFormulario(), BorderLayout.CENTER);
        centro.add(criarRegraCard(), BorderLayout.EAST);

        return centro;
    }

    private JPanel criarFormulario() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("Dados do Jogador");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JPanel campos = new JPanel();
        campos.setOpaque(false);
        campos.setLayout(new GridLayout(0, 2, 28, 16));
        campos.setBorder(new EmptyBorder(24, 0, 24, 0));

        campoNome = criarCampo(jogador.getNome());
        campoNumero = criarCampo(String.valueOf(jogador.getNumero()));

        campoPosicao = criarCombo(
                new String[]{"Guarda-Redes", "Defesa", "Médio", "Avançado"},
                jogador.getPosicao()
        );

        campoPeso = criarCampo(String.valueOf(jogador.getPeso()));
        campoAltura = criarCampo(String.valueOf(jogador.getAltura()));

        campoPeDominante = criarCombo(
                new String[]{"Direito", "Esquerdo", "Ambos"},
                jogador.getPeDominante()
        );

        campoDataNascimento = criarCampo(
                jogador.getDataNascimento() != null
                        ? jogador.getDataNascimento().toString()
                        : ""
        );

        campoPaisOrigem = criarCampo(jogador.getPaisOrigem());
        campoCidadeNascimento = criarCampo(jogador.getCidadeNascimento());
        campoEquipa = criarCampo(jogador.getEquipa());
        campoGrupo = criarCampo(jogador.getGrupo());
        campoRanking = criarCampo(jogador.getRanking());

        campoJogos = criarCampo(String.valueOf(jogador.getJogos()));
        campoGolos = criarCampo(String.valueOf(jogador.getGolos()));
        campoAssistencias = criarCampo(String.valueOf(jogador.getAssistencias()));
        campoCartoes = criarCampo(String.valueOf(jogador.getCartoes()));
        campoMinutos = criarCampo(String.valueOf(jogador.getMinutos()));

        campoFinalizacao = criarCampo(String.valueOf(jogador.getFinalizacao()));
        campoPasseCriacao = criarCampo(String.valueOf(jogador.getPasseCriacao()));
        campoDisciplina = criarCampo(String.valueOf(jogador.getDisciplina()));

        campoEstado = criarCombo(
                new String[]{"Ativo", "Inativo"},
                jogador.isAtivo() ? "Ativo" : "Inativo"
        );

        campos.add(criarCampoComLabel("Nome completo", campoNome));
        campos.add(criarCampoComLabel("Número", campoNumero));

        campos.add(criarCampoComLabel("Posição", campoPosicao));
        campos.add(criarCampoComLabel("Peso", campoPeso));

        campos.add(criarCampoComLabel("Altura", campoAltura));
        campos.add(criarCampoComLabel("Pé dominante", campoPeDominante));

        campos.add(criarCampoComLabel("Data de nascimento", campoDataNascimento));
        campos.add(criarCampoComLabel("País de origem", campoPaisOrigem));

        campos.add(criarCampoComLabel("Cidade de nascimento", campoCidadeNascimento));
        campos.add(criarCampoComLabel("Equipa", campoEquipa));

        campos.add(criarCampoComLabel("Grupo", campoGrupo));
        campos.add(criarCampoComLabel("Ranking", campoRanking));

        campos.add(criarCampoComLabel("Jogos", campoJogos));
        campos.add(criarCampoComLabel("Golos", campoGolos));

        campos.add(criarCampoComLabel("Assistências", campoAssistencias));
        campos.add(criarCampoComLabel("Cartões", campoCartoes));

        campos.add(criarCampoComLabel("Minutos", campoMinutos));
        campos.add(criarCampoComLabel("Estado", campoEstado));

        campos.add(criarCampoComLabel("Finalização (%)", campoFinalizacao));
        campos.add(criarCampoComLabel("Passe e criação (%)", campoPasseCriacao));

        campos.add(criarCampoComLabel("Disciplina (%)", campoDisciplina));

        JScrollPane scroll = new JScrollPane(campos);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        ModernScrollBarUI.aplicar(scroll);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        botoes.setOpaque(false);

        JButton cancelar = criarBotao(
                "Cancelar",
                Tema.COR_BOTAO_SECUNDARIO,
                Tema.COR_TEXTO_PRINCIPAL
        );

        JButton guardar = criarBotao(
                "Guardar Alterações",
                Tema.COR_INFO,
                Color.WHITE
        );

        cancelar.addActionListener(e -> voltarParaPerfil());
        guardar.addActionListener(e -> guardarAlteracoes());

        botoes.add(cancelar);
        botoes.add(guardar);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        return card;
    }

    private JPanel criarRegraCard() {
        RoundedPanel side = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_VERDE_SUAVE);
        side.setPreferredSize(new Dimension(260, 0));
        side.setLayout(new BorderLayout());
        side.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("Regra principal");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(Tema.COR_VERDE_FORTE);

        JTextArea texto = new JTextArea(
                "O jogador pode ser editado antes do início do campeonato.\n\n" +
                        "Depois do campeonato iniciar, o ideal é bloquear dados principais como equipa, número e posição.\n\n" +
                        "O estado Ativo/Inativo pode continuar a ser alterado sem apagar o registo."
        );

        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setOpaque(false);
        texto.setFont(Tema.FONTE_TEXTO);
        texto.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        side.add(titulo, BorderLayout.NORTH);
        side.add(texto, BorderLayout.CENTER);

        return side;
    }

    private JTextField criarCampo(String valor) {
        JTextField campo = new JTextField(valor == null ? "" : valor);

        campo.setEditable(true);
        campo.setEnabled(true);
        campo.setFocusable(true);

        campo.setFont(Tema.FONTE_TEXTO);
        campo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        campo.setBackground(Tema.COR_INPUT);
        campo.setOpaque(true);
        campo.setCaretColor(Tema.COR_TEXTO_PRINCIPAL);
        campo.setSelectionColor(Tema.COR_AZUL_SUAVE);
        campo.setSelectedTextColor(Tema.COR_TEXTO_PRINCIPAL);

        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Tema.COR_LINHA, 12),
                new EmptyBorder(8, 12, 8, 12)
        ));

        return campo;
    }

    private JComboBox<String> criarCombo(String[] opcoes, String valorAtual) {
        JComboBox<String> combo = new JComboBox<>(opcoes);

        combo.setEnabled(true);
        combo.setFocusable(true);
        combo.setFont(Tema.FONTE_TEXTO);
        combo.setBackground(Tema.COR_INPUT);
        combo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        combo.setBorder(new RoundedBorder(Tema.COR_LINHA, 12));

        if (valorAtual != null) {
            combo.setSelectedItem(valorAtual);
        }

        return combo;
    }

    private JPanel criarCampoComLabel(String label, JComponent campo) {
        JPanel painel = new JPanel(new BorderLayout(0, 7));
        painel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 12));
        lbl.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        campo.setPreferredSize(new Dimension(100, 38));

        painel.add(lbl, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);

        return painel;
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto) {
        RoundedButton botao = new RoundedButton(texto, fundo, corTexto, 12);
        botao.setPreferredSize(new Dimension(175, 40));
        return botao;
    }

    private void guardarAlteracoes() {
        try {
            validarCamposObrigatorios();

            jogador.setNome(campoNome.getText().trim());
            jogador.setNumero(Integer.parseInt(campoNumero.getText().trim()));
            jogador.setPosicao(String.valueOf(campoPosicao.getSelectedItem()));
            jogador.setPeso(Integer.parseInt(campoPeso.getText().trim()));

            String alturaTexto = campoAltura.getText().trim().replace(",", ".");
            jogador.setAltura(Double.parseDouble(alturaTexto));

            jogador.setPeDominante(String.valueOf(campoPeDominante.getSelectedItem()));
            jogador.setDataNascimento(LocalDate.parse(campoDataNascimento.getText().trim()));

            jogador.setPaisOrigem(campoPaisOrigem.getText().trim());
            jogador.setCidadeNascimento(campoCidadeNascimento.getText().trim());
            jogador.setEquipa(campoEquipa.getText().trim());
            jogador.setGrupo(campoGrupo.getText().trim());
            jogador.setRanking(campoRanking.getText().trim());

            jogador.setJogos(Integer.parseInt(campoJogos.getText().trim()));
            jogador.setGolos(Integer.parseInt(campoGolos.getText().trim()));
            jogador.setAssistencias(Integer.parseInt(campoAssistencias.getText().trim()));
            jogador.setCartoes(Integer.parseInt(campoCartoes.getText().trim()));
            jogador.setMinutos(Integer.parseInt(campoMinutos.getText().trim()));

            int finalizacao = Integer.parseInt(campoFinalizacao.getText().trim());
            int passeCriacao = Integer.parseInt(campoPasseCriacao.getText().trim());
            int disciplina = Integer.parseInt(campoDisciplina.getText().trim());

            validarPercentagem(finalizacao, "Finalização");
            validarPercentagem(passeCriacao, "Passe e criação");
            validarPercentagem(disciplina, "Disciplina");

            jogador.setFinalizacao(finalizacao);
            jogador.setPasseCriacao(passeCriacao);
            jogador.setDisciplina(disciplina);

            jogador.setAtivo("Ativo".equals(campoEstado.getSelectedItem()));

            JogadorService.getInstance().guardarJogadores();

            if (onGuardar != null) {
                onGuardar.run();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Jogador atualizado com sucesso.",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            voltarParaPerfil();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Verifica os campos numéricos.\n\n" +
                            "Número, peso, jogos, golos, assistências, cartões, minutos e percentagens têm de ser números.",
                    "Erro de validação",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "A data deve estar no formato AAAA-MM-DD.\n\n" +
                            "Exemplo: 2000-01-20",
                    "Erro de data",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro de validação",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void voltarParaPerfil() {
        abrirNaMesmaJanela(new PerfilJogadorFrame(jogador, onGuardar));
    }

    private void abrirNaMesmaJanela(JFrame novoFrame) {
        Dimension tamanhoAtual = getSize();
        Point posicaoAtual = getLocation();
        int estadoAtual = getExtendedState();

        novoFrame.setSize(tamanhoAtual);
        novoFrame.setMinimumSize(new Dimension(1180, 700));
        novoFrame.setLocation(posicaoAtual);
        novoFrame.setExtendedState(estadoAtual);

        dispose();
        novoFrame.setVisible(true);
    }

    private void validarCamposObrigatorios() {
        if (campoNome.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do jogador é obrigatório.");
        }

        if (campoNumero.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("O número do jogador é obrigatório.");
        }

        if (campoDataNascimento.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        }

        if (campoEquipa.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("A equipa é obrigatória.");
        }
    }

    private void validarPercentagem(int valor, String campo) {
        if (valor < 0 || valor > 100) {
            throw new IllegalArgumentException(campo + " deve estar entre 0 e 100.");
        }
    }

}
