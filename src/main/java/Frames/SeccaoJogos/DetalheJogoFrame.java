package Frames.SeccaoJogos;

import Design.ModernScrollBarUI;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Models.Jogos.EventoJogo;
import Models.Jogos.EventoJogoService;
import Models.Jogadores.Jogador;
import Models.Jogadores.JogadorService;
import Models.Jogos.Jogo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class DetalheJogoFrame extends JFrame {

    private Jogo jogo;
    private final Runnable aoVoltar;
    private boolean retornoExecutado;

    private final EventoJogoService eventoService = EventoJogoService.getInstance();
    private final JogadorService jogadorService = JogadorService.getInstance();

    public DetalheJogoFrame(Jogo jogo, Runnable aoVoltar) {
        this.jogo = jogo;
        this.aoVoltar = aoVoltar;

        setTitle("Detalhe do Jogo - " + jogo.getNomeJogo());
        setSize(1680, 980);
        setMinimumSize(new Dimension(1180, 700));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                executarRetorno();
            }
        });

        reconstruirPagina();
        setVisible(true);
    }

    private void reconstruirPagina() {
        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.COR_FUNDO);

        JScrollPane scroll = new JScrollPane(criarConteudo());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Tema.COR_FUNDO);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        ModernScrollBarUI.aplicar(scroll);

        fundo.add(scroll, BorderLayout.CENTER);
        setContentPane(fundo);
        revalidate();
        repaint();
    }

    private JPanel criarConteudo() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Tema.COR_FUNDO);
        content.setBorder(new EmptyBorder(30, 35, 35, 35));

        content.add(criarTopo());
        content.add(Box.createVerticalStrut(25));
        content.add(criarTitulo());
        content.add(Box.createVerticalStrut(25));
        content.add(criarCardsResumo());
        content.add(Box.createVerticalStrut(25));
        content.add(criarCardDadosDoJogo());
        content.add(Box.createVerticalStrut(25));
        content.add(criarCardResultado());
        content.add(Box.createVerticalStrut(25));
        content.add(criarCardEventos());
        content.add(Box.createVerticalStrut(20));
        content.add(criarAvisoDadosTecnicos());

        return content;
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        RoundedButton btnVoltar = new RoundedButton(
                "← Voltar a Estatísticas",
                Tema.COR_INFO,
                Color.WHITE,
                12
        );

        btnVoltar.setPreferredSize(new Dimension(190, 40));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.addActionListener(e -> dispose());

        RoundedButton btnAtualizar = new RoundedButton(
                "Atualizar jogo",
                Tema.COR_SUCESSO,
                Color.WHITE,
                12
        );

        btnAtualizar.setPreferredSize(new Dimension(160, 40));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.addActionListener(e -> abrirDialogoAtualizarJogo());

        topo.add(btnVoltar, BorderLayout.WEST);
        topo.add(btnAtualizar, BorderLayout.EAST);

        return topo;
    }

    private JPanel criarTitulo() {
        JPanel painel = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, 80);
            }
        };

        painel.setOpaque(false);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;

        JLabel titulo = new JLabel(jogo.getNomeJogo());
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        titulo.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel subtitulo = new JLabel(
                textoOuTraco(jogo.getCampeonato())
                        + " • "
                        + textoOuTraco(jogo.getFaseGrupo())
        );

        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        subtitulo.setHorizontalAlignment(SwingConstants.LEFT);

        gbc.gridy = 0;
        painel.add(titulo, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(4, 0, 0, 0);
        painel.add(subtitulo, gbc);

        return painel;
    }

    private JPanel criarCardsResumo() {
        JPanel linha = new JPanel(new GridLayout(1, 4, 20, 0));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));

        int[] golos = golosDoResultado();
        String totalGolos = golos == null
                ? "-"
                : String.valueOf(golos[0] + golos[1]);

        linha.add(criarCardResumo(
                "Resultado",
                textoOuTraco(jogo.getResultado()),
                Tema.CARD_AZUL,
                Tema.CARD_TEXTO_AZUL
        ));

        linha.add(criarCardResumo(
                "Total de Golos",
                totalGolos,
                Tema.CARD_VERDE,
                Tema.CARD_TEXTO_VERDE
        ));

        linha.add(criarCardResumo(
                "Cartões",
                String.valueOf(eventoService.contarCartoesDoJogo(jogo)),
                Tema.CARD_ROXO,
                Tema.CARD_TEXTO_ROXO
        ));

        linha.add(criarCardResumo(
                "Estado",
                textoOuTraco(jogo.getEstado()),
                Tema.COR_LARANJA_SUAVE,
                Tema.CARD_TEXTO_LARANJA
        ));

        return linha;
    }

    private JPanel criarCardResumo(String titulo, String valor, Color fundo, Color corTitulo) {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, fundo);
        card.setLayout(new BorderLayout());

        card.setBorder(new EmptyBorder(
                Tema.PADDING_CARD_RESUMO.top,
                Tema.PADDING_CARD_RESUMO.left,
                Tema.PADDING_CARD_RESUMO.bottom,
                Tema.PADDING_CARD_RESUMO.right
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Tema.FONTE_CARD_TITULO);
        lblTitulo.setForeground(corTitulo);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(Tema.FONTE_CARD_VALOR);
        lblValor.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardDadosDoJogo() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 275));

        JLabel titulo = new JLabel("Dados do Jogo");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JPanel dados = new JPanel(new GridLayout(3, 3, 28, 20));
        dados.setOpaque(false);
        dados.setBorder(new EmptyBorder(28, 0, 0, 0));

        dados.add(criarInfo("Equipa casa", textoOuTraco(jogo.getEquipaA())));
        dados.add(criarInfo("Equipa visitante", textoOuTraco(jogo.getEquipaB())));
        dados.add(criarInfo("Estádio", textoOuTraco(jogo.getEstadio())));
        dados.add(criarInfo("Data", textoOuTraco(jogo.getData())));
        dados.add(criarInfo("Hora", textoOuTraco(jogo.getHora())));
        dados.add(criarInfo("Fase / Grupo", textoOuTraco(jogo.getFaseGrupo())));
        dados.add(criarInfo("Estado", textoOuTraco(jogo.getEstado())));
        dados.add(criarInfo("Resultado", textoOuTraco(jogo.getResultado())));
        dados.add(criarInfo("Campeonato", textoOuTraco(jogo.getCampeonato())));

        card.add(titulo, BorderLayout.NORTH);
        card.add(dados, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardResultado() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

        JLabel titulo = new JLabel("Resultado do Jogo");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JPanel resultado = new JPanel(new GridLayout(1, 3, 20, 0));
        resultado.setOpaque(false);
        resultado.setBorder(new EmptyBorder(28, 0, 0, 0));

        int[] golos = golosDoResultado();

        String golosCasa = golos == null ? "-" : String.valueOf(golos[0]);
        String golosFora = golos == null ? "-" : String.valueOf(golos[1]);

        resultado.add(criarCardPlacar(
                jogo.getEquipaA(),
                golosCasa,
                Tema.CARD_AZUL,
                Tema.CARD_TEXTO_AZUL
        ));

        resultado.add(criarCardPlacar(
                "Resultado",
                textoOuTraco(jogo.getResultado()),
                Tema.COR_BOTAO_SECUNDARIO,
                Tema.COR_TEXTO_SECUNDARIO
        ));

        resultado.add(criarCardPlacar(
                jogo.getEquipaB(),
                golosFora,
                Tema.CARD_VERDE,
                Tema.CARD_TEXTO_VERDE
        ));

        card.add(titulo, BorderLayout.NORTH);
        card.add(resultado, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardPlacar(String titulo, String valor, Color fundo, Color corTitulo) {
        RoundedPanel card = new RoundedPanel(14, fundo);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel conteudo = new JPanel(new GridLayout(2, 1));
        conteudo.setOpaque(false);

        JLabel nome = new JLabel(titulo, SwingConstants.LEFT);
        nome.setFont(Tema.FONTE_CARD_TITULO);
        nome.setForeground(corTitulo);
        nome.setHorizontalAlignment(SwingConstants.LEFT);
        nome.setVerticalAlignment(SwingConstants.CENTER);

        JLabel numero = new JLabel(valor, SwingConstants.LEFT);
        numero.setFont(Tema.FONTE_CARD_VALOR_GRANDE);
        numero.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        numero.setHorizontalAlignment(SwingConstants.LEFT);
        numero.setVerticalAlignment(SwingConstants.CENTER);

        conteudo.add(nome);
        conteudo.add(numero);
        card.add(conteudo, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardEventos() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));

        JLabel titulo = new JLabel("Eventos do Jogo");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        DefaultTableModel modelo = criarModeloEventos();
        preencherEventos(modelo);

        JTable tabela = criarTabelaEventos(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);
        scroll.setBorder(new EmptyBorder(20, 0, 0, 0));

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarAvisoDadosTecnicos() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.CARD_AMARELO);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 24, 22, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        JLabel titulo = new JLabel("Dados técnicos");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.CARD_TEXTO_LARANJA);

        JTextArea texto = new JTextArea(
                "Usa o botão “Atualizar jogo” para registar golos e cartões. "
                        + "Os eventos são guardados em data/eventos_jogo.tsv; "
                        + "o resultado é sincronizado com data/jogos.tsv e as estatísticas "
                        + "dos jogadores com data/jogadores.tsv."
        );

        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setFocusable(false);
        texto.setOpaque(false);
        texto.setFont(Tema.FONTE_TEXTO);
        texto.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        card.add(titulo, BorderLayout.NORTH);
        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarInfo(String etiqueta, String valor) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(Tema.FONTE_CARD_TITULO);
        lblEtiqueta.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(Tema.FONTE_TEXTO);
        lblValor.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        painel.add(lblEtiqueta);
        painel.add(Box.createVerticalStrut(6));
        painel.add(lblValor);

        return painel;
    }

    private void abrirDialogoAtualizarJogo() {
        JDialog dialog = new JDialog(this, "Atualizar jogo", true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(1020, 680);
        dialog.setMinimumSize(new Dimension(820, 560));
        dialog.setLocationRelativeTo(this);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.COR_FUNDO);
        fundo.setBorder(new EmptyBorder(22, 24, 22, 24));

        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel cabecalho = new JPanel(new BorderLayout(20, 0));
        cabecalho.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Atualizar Jogo");
        titulo.setFont(Tema.FONTE_TITULO);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel(
                jogo.getNomeJogo() + " • " + jogo.getData() + " " + jogo.getHora()
        );
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JComboBox<String> comboEstado = new JComboBox<>(new String[]{
                "Agendado", "Em curso", "Realizado", "Finalizado", "Cancelado"
        });
        comboEstado.setSelectedItem(jogo.getEstado());
        configurarCombo(comboEstado);

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(criarCampoFormulario("Estado do jogo", comboEstado), BorderLayout.EAST);

        DefaultTableModel modelo = criarModeloEventos();
        preencherEventos(modelo);

        JTable tabela = criarTabelaEventos(modelo);
        ocultarColunaId(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_CARD);
        scroll.setBorder(new EmptyBorder(20, 0, 14, 0));

        JPanel acoesEventos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acoesEventos.setOpaque(false);

        RoundedButton btnGolo = new RoundedButton("+ Golo", Tema.COR_SUCESSO, Color.WHITE, 12);
        RoundedButton btnCartao = new RoundedButton("+ Cartão", Tema.COR_AVISO, Color.WHITE, 12);
        RoundedButton btnEliminar = new RoundedButton("Eliminar evento", Tema.COR_ERRO, Color.WHITE, 12);

        btnGolo.addActionListener(e -> abrirDialogoNovoEvento(dialog, modelo, true));
        btnCartao.addActionListener(e -> abrirDialogoNovoEvento(dialog, modelo, false));
        btnEliminar.addActionListener(e -> eliminarEvento(dialog, tabela, modelo));

        acoesEventos.add(btnGolo);
        acoesEventos.add(btnCartao);
        acoesEventos.add(btnEliminar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(scroll, BorderLayout.CENTER);
        centro.add(acoesEventos, BorderLayout.SOUTH);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rodape.setOpaque(false);

        RoundedButton btnFechar = new RoundedButton(
                "Fechar",
                Tema.COR_BOTAO_SECUNDARIO,
                Tema.COR_TEXTO_PRINCIPAL,
                12
        );

        RoundedButton btnGuardarEstado = new RoundedButton(
                "Guardar estado",
                Tema.COR_INFO,
                Color.WHITE,
                12
        );

        btnFechar.addActionListener(e -> dialog.dispose());
        btnGuardarEstado.addActionListener(e -> {
            try {
                jogo = eventoService.atualizarEstadoDoJogo(
                        jogo,
                        String.valueOf(comboEstado.getSelectedItem())
                );
                reconstruirPagina();
                preencherEventos(modelo);

                JOptionPane.showMessageDialog(
                        dialog,
                        "Estado guardado em data/jogos.tsv.",
                        "Jogo atualizado",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarErro(dialog, ex.getMessage());
            }
        });

        rodape.add(btnFechar);
        rodape.add(btnGuardarEstado);

        card.add(cabecalho, BorderLayout.NORTH);
        card.add(centro, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);

        fundo.add(card, BorderLayout.CENTER);
        dialog.setContentPane(fundo);
        dialog.setVisible(true);
    }

    private void abrirDialogoNovoEvento(
            JDialog dialogoPrincipal,
            DefaultTableModel modeloEventos,
            boolean eGolo
    ) {
        JDialog dialog = new JDialog(
                dialogoPrincipal,
                eGolo ? "Registar golo" : "Registar cartão",
                true
        );

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setSize(560, eGolo ? 400 : 460);
        dialog.setLocationRelativeTo(dialogoPrincipal);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.COR_FUNDO);
        fundo.setBorder(new EmptyBorder(22, 24, 22, 24));

        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel formulario = new JPanel();
        formulario.setOpaque(false);
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));

        JComboBox<String> comboEquipa = new JComboBox<>(new String[]{
                jogo.getEquipaA(), jogo.getEquipaB()
        });
        configurarCombo(comboEquipa);

        JComboBox<Jogador> comboJogador = new JComboBox<>();
        configurarComboJogador(comboJogador);
        carregarJogadoresDaEquipa(comboJogador, String.valueOf(comboEquipa.getSelectedItem()));

        comboEquipa.addActionListener(e -> carregarJogadoresDaEquipa(
                comboJogador,
                String.valueOf(comboEquipa.getSelectedItem())
        ));

        JSpinner spinnerMinuto = criarSpinnerTempo(0, 130);
        JSpinner spinnerSegundo = criarSpinnerTempo(0, 59);

        formulario.add(criarCampoFormulario("Equipa", comboEquipa));
        formulario.add(Box.createVerticalStrut(12));
        formulario.add(criarCampoFormulario("Jogador", comboJogador));
        formulario.add(Box.createVerticalStrut(12));

        JComboBox<String> comboTipoCartao = null;

        if (!eGolo) {
            comboTipoCartao = new JComboBox<>(new String[]{
                    EventoJogoService.CARTAO_AMARELO,
                    EventoJogoService.CARTAO_VERMELHO,
                    EventoJogoService.CARTAO_SEGUNDO_AMARELO
            });
            configurarCombo(comboTipoCartao);
            formulario.add(criarCampoFormulario("Tipo de cartão", comboTipoCartao));
            formulario.add(Box.createVerticalStrut(12));
        }

        JPanel tempo = new JPanel(new GridLayout(1, 2, 12, 0));
        tempo.setOpaque(false);
        tempo.add(criarCampoFormulario("Minuto", spinnerMinuto));
        tempo.add(criarCampoFormulario("Segundo", spinnerSegundo));

        formulario.add(tempo);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rodape.setOpaque(false);

        RoundedButton btnCancelar = new RoundedButton(
                "Cancelar",
                Tema.COR_BOTAO_SECUNDARIO,
                Tema.COR_TEXTO_PRINCIPAL,
                12
        );

        RoundedButton btnGuardar = new RoundedButton(
                eGolo ? "Registar golo" : "Registar cartão",
                eGolo ? Tema.COR_SUCESSO : Tema.COR_AVISO,
                Color.WHITE,
                12
        );

        JComboBox<String> tipoCartaoFinal = comboTipoCartao;

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            try {
                Jogador jogador = (Jogador) comboJogador.getSelectedItem();

                if (jogador == null) {
                    throw new IllegalArgumentException(
                            "Não existem jogadores ativos desta equipa neste campeonato."
                    );
                }

                int minuto = ((Number) spinnerMinuto.getValue()).intValue();
                int segundo = ((Number) spinnerSegundo.getValue()).intValue();

                if (eGolo) {
                    jogo = eventoService.adicionarGolo(jogo, jogador, minuto, segundo);
                } else {
                    eventoService.adicionarCartao(
                            jogo,
                            jogador,
                            String.valueOf(tipoCartaoFinal.getSelectedItem()),
                            minuto,
                            segundo
                    );
                }

                preencherEventos(modeloEventos);
                reconstruirPagina();
                dialog.dispose();

            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarErro(dialog, ex.getMessage());
            }
        });

        rodape.add(btnCancelar);
        rodape.add(btnGuardar);

        card.add(formulario, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);

        fundo.add(card, BorderLayout.CENTER);
        dialog.setContentPane(fundo);
        dialog.setVisible(true);
    }

    private void eliminarEvento(
            JDialog dialog,
            JTable tabela,
            DefaultTableModel modelo
    ) {
        int linhaSelecionada = tabela.getSelectedRow();

        if (linhaSelecionada < 0) {
            mostrarErro(dialog, "Seleciona um evento na tabela.");
            return;
        }

        int linhaModelo = tabela.convertRowIndexToModel(linhaSelecionada);
        String idEvento = String.valueOf(modelo.getValueAt(linhaModelo, 5));

        int opcao = JOptionPane.showConfirmDialog(
                dialog,
                "Eliminar o evento selecionado?\nEsta ação também reverte os dados do jogador.",
                "Confirmar eliminação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opcao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Jogo jogoAtualizado = eventoService.removerEvento(jogo, idEvento);

            if (jogoAtualizado != null) {
                jogo = jogoAtualizado;
            }

            preencherEventos(modelo);
            reconstruirPagina();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarErro(dialog, ex.getMessage());
        }
    }

    private JPanel criarCampoFormulario(String label, JComponent componente) {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(Tema.FONTE_CARD_TITULO);
        lblLabel.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        lblLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        painel.add(lblLabel);
        painel.add(Box.createVerticalStrut(6));
        painel.add(componente);

        return painel;
    }

    private void configurarCombo(JComboBox<?> combo) {
        combo.setFont(Tema.FONTE_TEXTO);
        combo.setBackground(Tema.COR_INPUT);
        combo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        combo.setBorder(BorderFactory.createLineBorder(Tema.COR_LINHA));
    }

    private void configurarComboJogador(JComboBox<Jogador> combo) {
        configurarCombo(combo);

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
                        list,
                        value,
                        index,
                        isSelected,
                        cellHasFocus
                );

                if (value instanceof Jogador jogador) {
                    label.setText("#" + jogador.getNumero() + " - " + jogador.getNome());
                }

                return label;
            }
        });
    }

    private JSpinner criarSpinnerTempo(int minimo, int maximo) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(minimo, minimo, maximo, 1));
        spinner.setFont(Tema.FONTE_TEXTO);
        spinner.setBorder(BorderFactory.createLineBorder(Tema.COR_LINHA));
        return spinner;
    }

    private void carregarJogadoresDaEquipa(JComboBox<Jogador> combo, String equipa) {
        combo.removeAllItems();

        List<Jogador> jogadores = jogadorService.listarPorEquipa(equipa, jogo.getCampeonato());

        for (Jogador jogador : jogadores) {
            if (jogador.isAtivo()) {
                combo.addItem(jogador);
            }
        }
    }

    private DefaultTableModel criarModeloEventos() {
        return new DefaultTableModel(
                new String[]{"Tipo", "Tempo", "Equipa", "Jogador", "Detalhe", "ID"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable criarTabelaEventos(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        TableStyle.aplicarTabelaLimpa(tabela, 3);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ocultarColunaId(tabela);
        return tabela;
    }

    private void preencherEventos(DefaultTableModel modelo) {
        modelo.setRowCount(0);

        for (EventoJogo evento : eventoService.listarEventosDoJogo(jogo)) {
            modelo.addRow(new Object[]{
                    evento.getTipoApresentacao(),
                    evento.getTempoFormatado(),
                    evento.getEquipa(),
                    "#" + evento.getJogadorNumero() + " - " + evento.getJogadorNome(),
                    evento.getDetalhe(),
                    evento.getId()
            });
        }
    }

    private void ocultarColunaId(JTable tabela) {
        if (tabela.getColumnModel().getColumnCount() <= 5) {
            return;
        }

        tabela.getColumnModel().getColumn(5).setMinWidth(0);
        tabela.getColumnModel().getColumn(5).setMaxWidth(0);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(0);
    }

    private int[] golosDoResultado() {
        String resultado = jogo.getResultado();

        if (resultado == null || resultado.isBlank() || "-".equals(resultado.trim())) {
            return null;
        }

        String[] partes = resultado.trim().split("[-–]");

        if (partes.length != 2) {
            return null;
        }

        try {
            return new int[]{
                    Integer.parseInt(partes[0].trim()),
                    Integer.parseInt(partes[1].trim())
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String textoOuTraco(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private void mostrarErro(Component parent, String mensagem) {
        JOptionPane.showMessageDialog(
                parent,
                mensagem,
                "Atenção",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void executarRetorno() {
        if (retornoExecutado) {
            return;
        }

        retornoExecutado = true;

        if (aoVoltar != null) {
            aoVoltar.run();
        }
    }
}
