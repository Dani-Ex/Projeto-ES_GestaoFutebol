package Frames.SeccaoFinancas;

import Design.FormUtils;
import Design.PlaceholderTextField;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.Tema;
import Models.Jogos.Jogo;
import Models.Jogos.JogoService;
import Models.Bilheteria.Bilhete;
import Models.Bilheteria.BilheteriaService;
import Models.Financas.Receita;
import Models.Financas.ReceitaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditarReceitaFrame extends JFrame {

    private final String idJogo;
    private final Runnable onReceitaAlterada;
    private final JogoService jogoService = JogoService.getInstance();
    private final ReceitaService receitaService = ReceitaService.getInstance();
    private final BilheteriaService bilheteriaService = new BilheteriaService();

    private PlaceholderTextField campoJogo;
    private PlaceholderTextField campoCampeonato;
    private PlaceholderTextField campoDia;
    private PlaceholderTextField campoBilhetes;
    private PlaceholderTextField campoBilheteira;
    private PlaceholderTextField campoPatrocinio;
    private PlaceholderTextField campoDireitosTv;

    public EditarReceitaFrame(String idJogo, Runnable onReceitaAlterada) {
        this.idJogo = idJogo;
        this.onReceitaAlterada = onReceitaAlterada;

        setTitle("Editar Receita");
        setSize(1260, 760);
        setMinimumSize(new Dimension(1080, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.COR_FUNDO);
        fundo.setBorder(new EmptyBorder(24, 26, 36, 26));
        fundo.add(criarConteudo(), BorderLayout.CENTER);

        setContentPane(fundo);
        preencherCampos();
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

        JLabel titulo = new JLabel("Editar Receita");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Atualiza\u00E7\u00E3o dos valores financeiros de um jogo.");
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

        campoJogo = criarCampoBloqueado();
        campoCampeonato = criarCampoBloqueado();
        campoDia = criarCampoBloqueado();
        campoBilhetes = criarCampoBloqueado();
        campoBilheteira = criarCampoBloqueado();
        campoPatrocinio = criarCampo("Ex: 50000");
        campoDireitosTv = criarCampo("Ex: 80000");

        campos.add(criarCampoComLabel("Jogo", campoJogo));
        campos.add(criarCampoComLabel("Campeonato", campoCampeonato));
        campos.add(criarCampoComLabel("Dia", campoDia));
        campos.add(criarCampoComLabel("Bilhetes Vendidos", campoBilhetes));
        campos.add(criarCampoComLabel("Bilheteira", campoBilheteira));
        campos.add(criarCampoComLabel("Patroc\u00EDnio", campoPatrocinio));
        campos.add(criarCampoComLabel("Direitos TV", campoDireitosTv));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(campos, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new BorderLayout());
        botoes.setOpaque(false);
        botoes.setBorder(new EmptyBorder(18, 0, 0, 0));

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esquerda.setOpaque(false);
        esquerda.add(criarBotao("Eliminar Receita", Tema.COR_ERRO, Tema.COR_TEXTO_CLARO, this::eliminarReceita, 156));

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        direita.setOpaque(false);
        direita.add(criarBotao("Cancelar", Tema.COR_BOTAO_SECUNDARIO, Tema.COR_TEXTO_PRINCIPAL, this::dispose, 128));
        direita.add(criarBotao("Guardar Altera\u00E7\u00F5es", Tema.COR_INFO, Tema.COR_TEXTO_CLARO, this::guardarAlteracoes, 164));

        botoes.add(esquerda, BorderLayout.WEST);
        botoes.add(direita, BorderLayout.EAST);

        card.add(titulo, BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        return card;
    }

    private RoundedPanel criarRegraCard() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_ERRO_SUAVE);
        card.setPreferredSize(new Dimension(230, 520));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(26, 20, 26, 20));

        JLabel titulo = new JLabel("Edi\u00E7\u00E3o");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(Tema.COR_ERRO);

        JTextArea texto = new JTextArea(
                "Pode atualizar os valores financeiros da receita.\n\n" +
                        "Ao eliminar a receita, o jogo volta a ficar dispon\u00EDvel para uma nova receita.\n\n" +
                        "O lucro continua a ser calculado automaticamente."
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

    private JButton criarBotao(String texto, Color fundo, Color corTexto, Runnable acao, int largura) {
        JButton botao = new RoundedButton(texto, fundo, corTexto, 12);
        botao.setPreferredSize(new Dimension(largura, 36));
        botao.addActionListener(e -> acao.run());

        return botao;
    }

    private void preencherCampos() {
        Receita receita = receitaService.procurarPorJogo(idJogo);

        if (receita == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "A receita selecionada j\u00E1 n\u00E3o existe.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        Jogo jogo = procurarJogoDaReceita(idJogo);

        campoJogo.setText(jogo == null ? idJogo : jogo.getNomeJogo());
        campoCampeonato.setText(jogo == null ? "" : jogo.getCampeonato());
        campoDia.setText(jogo == null ? "" : jogo.getData());

        ResumoBilheteira bilheteira = carregarResumoBilheteira(idJogo);
        campoBilhetes.setText(String.valueOf(bilheteira == null ? 0 : bilheteira.bilhetes));
        campoBilheteira.setText(formatarCampo(bilheteira == null ? 0 : bilheteira.total));
        campoPatrocinio.setText(formatarCampo(receita.getPatrocinio()));
        campoDireitosTv.setText(formatarCampo(receita.getDireitosTv()));
    }

    private ResumoBilheteira carregarResumoBilheteira(String idJogo) {
        ResumoBilheteira resumo = null;

        for (Bilhete bilhete : bilheteriaService.listarBilhetesDoJogo(idJogo)) {
            if (resumo == null) {
                resumo = new ResumoBilheteira();
            }

            resumo.bilhetes += bilhete.getQuantidade();
            resumo.total += bilhete.getTotal();
        }

        return resumo;
    }

    private Jogo procurarJogoDaReceita(String idJogo) {
        if (idJogo == null) {
            return null;
        }

        String[] partes = idJogo.split("::", 2);
        if (partes.length == 2) {
            for (Jogo jogo : jogoService.listarJogos()) {
                if (jogo.getId().equalsIgnoreCase(partes[0])
                        && jogo.getCampeonato() != null
                        && jogo.getCampeonato().equalsIgnoreCase(partes[1])) {
                    return jogo;
                }
            }
        }

        return jogoService.procurarPorId(idJogo);
    }

    private void guardarAlteracoes() {
        try {
            ResumoBilheteira bilheteira = carregarResumoBilheteira(idJogo);
            Receita receita = new Receita(
                    idJogo,
                    bilheteira == null ? 0 : bilheteira.bilhetes,
                    bilheteira == null ? 0 : bilheteira.total,
                    lerValorPositivo(campoPatrocinio.getText(), "Patroc\u00EDnio"),
                    lerValorPositivo(campoDireitosTv.getText(), "Direitos TV")
            );

            receitaService.atualizarReceita(receita);
            notificarReceitaAlterada();

            JOptionPane.showMessageDialog(
                    this,
                    "Receita atualizada com sucesso.",
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

    private void eliminarReceita() {
        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Tem a certeza que pretende eliminar esta receita?",
                "Confirmar elimina\u00E7\u00E3o",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            receitaService.removerReceita(idJogo);
            notificarReceitaAlterada();

            JOptionPane.showMessageDialog(
                    this,
                    "Receita eliminada com sucesso.",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
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

    private String formatarCampo(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }

        return String.valueOf(valor);
    }

    private void notificarReceitaAlterada() {
        if (onReceitaAlterada != null) {
            onReceitaAlterada.run();
        }
    }

    private static class ResumoBilheteira {
        private int bilhetes;
        private double total;
    }
}
