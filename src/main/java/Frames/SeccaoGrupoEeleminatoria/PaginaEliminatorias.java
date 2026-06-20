package Frames.SeccaoGrupoEeleminatoria;

import Design.MenuLateral;
import Design.RoundedButton;
import Design.RoundedPanel;
import Design.TableStyle;
import Design.Tema;
import Models.Campeonato;
import Models.Jogo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class PaginaEliminatorias extends JPanel {

    private static final int LARGURA_CARD = 230;
    private static final int ALTURA_CARD = 132;
    private static final int ESPACO_HORIZONTAL = 122;
    private static final int ESPACO_VERTICAL = 42;
    private static final int MARGEM_X = 54;
    private static final int MARGEM_Y = 92;

    private final Campeonato campeonato;
    private final Runnable alternarMenu;
    private final Consumer<String> navegar;
    private final BracketCanvas bracketCanvas = new BracketCanvas();

    public PaginaEliminatorias(
            Campeonato campeonato,
            Runnable alternarMenu,
            Consumer<String> navegar
    ) {
        this.campeonato = campeonato;
        this.alternarMenu = alternarMenu;
        this.navegar = navegar;

        setLayout(new BorderLayout());
        setBackground(Tema.COR_FUNDO);
        setBorder(new EmptyBorder(
                Tema.PADDING_JANELA.top,
                Tema.PADDING_JANELA.left,
                Tema.PADDING_JANELA.bottom,
                Tema.PADDING_JANELA.right
        ));

        add(criarTopo(), BorderLayout.NORTH);
        add(criarAreaBracket(), BorderLayout.CENTER);
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setBorder(new EmptyBorder(0, 0, Tema.ESPACAMENTO_MEDIO, 0));

        JButton btnMenu = new JButton("=");
        btnMenu.setFont(Tema.FONTE_BOTAO_MENU);
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenu.addActionListener(e -> alternarMenu.run());

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Eliminatórias - " + campeonato.getNome());
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel(
                "Bracket visual. Os resultados são lidos dos jogos já guardados."
        );
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        esquerda.setOpaque(false);
        esquerda.add(btnMenu);
        esquerda.add(textos);

        JButton btnAtualizar = new RoundedButton(
                "Atualizar bracket",
                Tema.COR_INFO,
                Tema.COR_TEXTO_CLARO,
                12
        );
        btnAtualizar.addActionListener(e -> atualizarBracket());

        JButton btnVoltar = new RoundedButton(
                "Voltar aos Grupos",
                Tema.COR_BOTAO_SECUNDARIO,
                Tema.COR_TEXTO_PRINCIPAL,
                12
        );
        btnVoltar.addActionListener(e -> navegar.accept("grupos"));

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direita.setOpaque(false);
        direita.add(btnAtualizar);
        direita.add(btnVoltar);

        topo.add(esquerda, BorderLayout.WEST);
        topo.add(direita, BorderLayout.EAST);

        return topo;
    }

    private JComponent criarAreaBracket() {
        atualizarBracket();

        JScrollPane scroll = new JScrollPane(bracketCanvas);
        TableStyle.configurarScrollLimpo(scroll, Tema.COR_FUNDO);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        return scroll;
    }

    /*
     * Esta página é apenas visual:
     * - não cria jogos;
     * - não altera resultados;
     * - não grava no jogos.tsv.
     */
    private void atualizarBracket() {
        bracketCanvas.montar(construirModeloBracket());
        bracketCanvas.revalidate();
        bracketCanvas.repaint();
    }

    private ModeloBracket construirModeloBracket() {
        Map<Integer, List<Jogo>> jogosPorRonda = obterJogosEliminatoriaPorRonda();

        if (jogosPorRonda.isEmpty()) {
            return new ModeloBracket();
        }

        int tamanhoInicial = jogosPorRonda.keySet().iterator().next();
        int tamanhoMinimoPelosJogos = proximaPotenciaDeDois(
                jogosPorRonda.get(tamanhoInicial).size() * 2
        );

        tamanhoInicial = Math.max(tamanhoInicial, tamanhoMinimoPelosJogos);

        ModeloBracket modelo = new ModeloBracket();
        List<NoJogo> rondaAnterior = null;

        for (int tamanho = tamanhoInicial; tamanho >= 2; tamanho /= 2) {
            int quantidadeEsperada = Math.max(1, tamanho / 2);
            List<Jogo> jogosReais = new ArrayList<>(
                    jogosPorRonda.getOrDefault(tamanho, new ArrayList<>())
            );

            ordenarJogosDaRonda(jogosReais);

            List<NoJogo> ronda = new ArrayList<>();

            for (int indice = 0; indice < quantidadeEsperada; indice++) {
                Jogo jogoReal = indice < jogosReais.size()
                        ? jogosReais.get(indice)
                        : null;

                String equipaAInferida = "A definir";
                String equipaBInferida = "A definir";

                if (rondaAnterior != null) {
                    int primeiroJogoAnterior = indice * 2;
                    int segundoJogoAnterior = primeiroJogoAnterior + 1;

                    if (primeiroJogoAnterior < rondaAnterior.size()) {
                        equipaAInferida = nomeParaRondaSeguinte(
                                rondaAnterior.get(primeiroJogoAnterior)
                        );
                    }

                    if (segundoJogoAnterior < rondaAnterior.size()) {
                        equipaBInferida = nomeParaRondaSeguinte(
                                rondaAnterior.get(segundoJogoAnterior)
                        );
                    }
                }

                String equipaA = obterNomeEquipa(
                        jogoReal == null ? null : jogoReal.getEquipaA(),
                        equipaAInferida
                );

                String equipaB = obterNomeEquipa(
                        jogoReal == null ? null : jogoReal.getEquipaB(),
                        equipaBInferida
                );

                ronda.add(new NoJogo(
                        tamanho,
                        indice,
                        jogoReal,
                        equipaA,
                        equipaB
                ));
            }

            modelo.rondas.put(tamanho, ronda);
            rondaAnterior = ronda;
        }

        return modelo;
    }

    private Map<Integer, List<Jogo>> obterJogosEliminatoriaPorRonda() {
        Map<Integer, List<Jogo>> rondas = new TreeMap<>(Comparator.reverseOrder());

        if (campeonato == null || campeonato.getJogos() == null) {
            return rondas;
        }

        for (Jogo jogo : campeonato.getJogos()) {
            int tamanhoRonda = extrairTamanhoDaRonda(jogo.getFaseGrupo());

            if (tamanhoRonda > 0) {
                rondas.computeIfAbsent(tamanhoRonda, chave -> new ArrayList<>())
                        .add(jogo);
            }
        }

        return rondas;
    }

    private void ordenarJogosDaRonda(List<Jogo> jogos) {
        jogos.sort(
                Comparator.comparing(
                                (Jogo jogo) -> converterData(jogo.getData()),
                                Comparator.nullsLast(Comparator.naturalOrder())
                        )
                        .thenComparing(
                                Jogo::getHora,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                        )
                        .thenComparing(
                                Jogo::getId,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                        )
        );
    }

    /*
     * Aceita os formatos:
     * ELIM:8:Quartos de Final
     * Quartos de Final
     * Meias-Finais
     * Final
     */
    private int extrairTamanhoDaRonda(String fase) {
        if (fase == null) {
            return -1;
        }

        String texto = fase.trim();

        if (texto.startsWith("ELIM:")) {
            String[] partes = texto.split(":", 3);

            if (partes.length >= 2) {
                try {
                    return Integer.parseInt(partes[1]);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        String normalizado = normalizar(texto);

        if (normalizado.contains("dezasseis")
                || normalizado.contains("16 avos")
                || normalizado.contains("16-avos")) {
            return 32;
        }

        if (normalizado.contains("oitavo")) {
            return 16;
        }

        if (normalizado.contains("quarto")) {
            return 8;
        }

        if (normalizado.contains("meia") || normalizado.contains("semi")) {
            return 4;
        }

        if (normalizado.equals("final")
                || normalizado.endsWith(" final")) {
            return 2;
        }

        return -1;
    }

    private String nomeDaRonda(int tamanho) {
        return switch (tamanho) {
            case 2 -> "Final";
            case 4 -> "Meias-Finais";
            case 8 -> "Quartos de Final";
            case 16 -> "Oitavos de Final";
            case 32 -> "Dezasseis-avos de Final";
            default -> "Ronda de " + tamanho;
        };
    }

    private String obterNomeEquipa(String valorGuardado, String valorInferido) {
        if (equipaPorDefinir(valorGuardado)) {
            return valorInferido;
        }

        return valorGuardado.trim();
    }

    private boolean equipaPorDefinir(String equipa) {
        if (equipa == null || equipa.trim().isEmpty()) {
            return true;
        }

        String valor = normalizar(equipa);

        return valor.equals("por definir")
                || valor.equals("a definir")
                || valor.equals("tbd")
                || valor.equals("-");
    }

    private String nomeParaRondaSeguinte(NoJogo no) {
        String vencedor = obterVencedor(no);

        if (vencedor != null) {
            return vencedor;
        }

        return "A definir";
    }

    private String obterVencedor(NoJogo no) {
        if (no == null) {
            return null;
        }

        /*
         * Se existir BYE, a outra equipa avança automaticamente.
         * O BYE nunca aparece visualmente no bracket.
         */
        if (no.temBye()) {
            return no.equipaNaoBye();
        }

        if (no.jogoReal == null || no.jogoReal.getEstado() == null) {
            return null;
        }

        boolean realizado = no.jogoReal.getEstado().equalsIgnoreCase("Realizado")
                || no.jogoReal.getEstado().equalsIgnoreCase("Finalizado");

        if (!realizado) {
            return null;
        }

        int[] resultado = lerResultado(no.jogoReal.getResultado());

        if (resultado == null || resultado[0] == resultado[1]) {
            return null;
        }

        return resultado[0] > resultado[1] ? no.equipaA : no.equipaB;
    }

    private int[] lerResultado(String resultado) {
        if (resultado == null) {
            return null;
        }

        try {
            String[] partes = resultado.trim().replace(" ", "").split("-");

            if (partes.length != 2) {
                return null;
            }

            int golosA = Integer.parseInt(partes[0]);
            int golosB = Integer.parseInt(partes[1]);

            if (golosA < 0 || golosB < 0) {
                return null;
            }

            return new int[]{golosA, golosB};

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate converterData(String data) {
        try {
            return data == null ? null : LocalDate.parse(data);
        } catch (Exception e) {
            return null;
        }
    }

    private String formatarData(String data) {
        LocalDate dataConvertida = converterData(data);

        if (dataConvertida == null) {
            return "";
        }

        return dataConvertida.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("pt-PT"))
        );
    }

    private int proximaPotenciaDeDois(int numero) {
        int potencia = 1;

        while (potencia < Math.max(2, numero)) {
            potencia *= 2;
        }

        return potencia;
    }

    private String normalizar(String texto) {
        return texto
                .toLowerCase(Locale.ROOT)
                .replace("á", "a")
                .replace("à", "a")
                .replace("ã", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("õ", "o")
                .replace("ú", "u");
    }

    private static final class ModeloBracket {
        private final Map<Integer, List<NoJogo>> rondas = new LinkedHashMap<>();
    }

    private static final class NoJogo {
        private final int tamanhoRonda;
        private final int indice;
        private final Jogo jogoReal;
        private final String equipaA;
        private final String equipaB;

        private NoJogo(
                int tamanhoRonda,
                int indice,
                Jogo jogoReal,
                String equipaA,
                String equipaB
        ) {
            this.tamanhoRonda = tamanhoRonda;
            this.indice = indice;
            this.jogoReal = jogoReal;
            this.equipaA = equipaA == null ? "A definir" : equipaA;
            this.equipaB = equipaB == null ? "A definir" : equipaB;
        }

        private boolean temBye() {
            return "BYE".equalsIgnoreCase(equipaA)
                    || "BYE".equalsIgnoreCase(equipaB);
        }

        private String equipaNaoBye() {
            if ("BYE".equalsIgnoreCase(equipaA)) {
                return equipaB;
            }

            if ("BYE".equalsIgnoreCase(equipaB)) {
                return equipaA;
            }

            return null;
        }
    }

    private final class BracketCanvas extends JPanel {

        private final Map<Integer, List<Rectangle>> posicoesPorRonda =
                new LinkedHashMap<>();

        private final List<Integer> ordemRondas = new ArrayList<>();
        private ModeloBracket modeloAtual = new ModeloBracket();

        private BracketCanvas() {
            setLayout(null);
            setOpaque(true);
            setBackground(Tema.COR_FUNDO);
        }

        private void montar(ModeloBracket modelo) {
            removeAll();
            posicoesPorRonda.clear();
            ordemRondas.clear();
            modeloAtual = modelo;

            if (modelo.rondas.isEmpty()) {
                montarEstadoVazio();
                return;
            }

            ordemRondas.addAll(modelo.rondas.keySet());

            int numeroRondas = ordemRondas.size();
            int profundidadeLateral = Math.max(0, numeroRondas - 1);

            int xFinal = MARGEM_X
                    + profundidadeLateral * (LARGURA_CARD + ESPACO_HORIZONTAL);

            int largura = profundidadeLateral == 0
                    ? 960
                    : xFinal + LARGURA_CARD
                    + profundidadeLateral * (LARGURA_CARD + ESPACO_HORIZONTAL)
                    + MARGEM_X;

            int primeiraRonda = ordemRondas.get(0);
            int totalJogosPrimeiraRonda = modelo.rondas.get(primeiraRonda).size();
            int jogosPorLado = Math.max(1, totalJogosPrimeiraRonda / 2);

            int alturaJogos = jogosPorLado * ALTURA_CARD
                    + Math.max(0, jogosPorLado - 1) * ESPACO_VERTICAL;

            int altura = Math.max(630, MARGEM_Y + alturaJogos + 70);

            setPreferredSize(new Dimension(largura, altura));
            setMinimumSize(getPreferredSize());

            calcularPosicoes(largura, altura);

            for (Integer tamanhoRonda : ordemRondas) {
                List<NoJogo> jogos = modelo.rondas.get(tamanhoRonda);
                List<Rectangle> posicoes = posicoesPorRonda.get(tamanhoRonda);

                for (int i = 0; i < jogos.size(); i++) {
                    MatchCard card = new MatchCard(jogos.get(i));
                    card.setBounds(posicoes.get(i));
                    add(card);
                }
            }
        }

        private void montarEstadoVazio() {
            setPreferredSize(new Dimension(960, 620));
            setMinimumSize(getPreferredSize());

            RoundedPanel vazio = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
            vazio.setLayout(new BorderLayout());
            vazio.setBounds(230, 210, 500, 160);
            vazio.setBorder(new EmptyBorder(24, 24, 24, 24));

            JLabel mensagem = new JLabel(
                    "<html><b>Ainda não existem jogos de eliminatória.</b><br><br>"
                            + "No campo Fase dos jogos usa: Quartos de Final, "
                            + "Meias-Finais ou Final.</html>"
            );
            mensagem.setFont(Tema.FONTE_TEXTO);
            mensagem.setForeground(Tema.COR_TEXTO_SECUNDARIO);

            vazio.add(mensagem, BorderLayout.CENTER);
            add(vazio);
        }

        private void calcularPosicoes(int larguraCanvas, int alturaCanvas) {
            for (int indiceRonda = 0; indiceRonda < ordemRondas.size(); indiceRonda++) {
                int tamanhoRonda = ordemRondas.get(indiceRonda);
                List<NoJogo> jogos = modeloAtual.rondas.get(tamanhoRonda);
                List<Rectangle> posicoes = new ArrayList<>();

                boolean eFinal = jogos.size() == 1;

                if (eFinal) {
                    posicoes.add(new Rectangle(
                            (larguraCanvas - LARGURA_CARD) / 2,
                            (alturaCanvas - ALTURA_CARD) / 2,
                            LARGURA_CARD,
                            ALTURA_CARD
                    ));

                    posicoesPorRonda.put(tamanhoRonda, posicoes);
                    continue;
                }

                int jogosPorLado = jogos.size() / 2;

                int xEsquerda = MARGEM_X
                        + indiceRonda * (LARGURA_CARD + ESPACO_HORIZONTAL);

                int xDireita = larguraCanvas - MARGEM_X - LARGURA_CARD
                        - indiceRonda * (LARGURA_CARD + ESPACO_HORIZONTAL);

                if (indiceRonda == 0) {
                    int alturaBloco = jogosPorLado * ALTURA_CARD
                            + Math.max(0, jogosPorLado - 1) * ESPACO_VERTICAL;

                    int yInicial = Math.max(
                            MARGEM_Y,
                            (alturaCanvas - alturaBloco) / 2
                    );

                    for (int i = 0; i < jogosPorLado; i++) {
                        posicoes.add(new Rectangle(
                                xEsquerda,
                                yInicial + i * (ALTURA_CARD + ESPACO_VERTICAL),
                                LARGURA_CARD,
                                ALTURA_CARD
                        ));
                    }

                    for (int i = 0; i < jogosPorLado; i++) {
                        posicoes.add(new Rectangle(
                                xDireita,
                                yInicial + i * (ALTURA_CARD + ESPACO_VERTICAL),
                                LARGURA_CARD,
                                ALTURA_CARD
                        ));
                    }

                } else {
                    int tamanhoAnterior = ordemRondas.get(indiceRonda - 1);
                    List<Rectangle> anteriores = posicoesPorRonda.get(tamanhoAnterior);
                    int jogosAnterioresPorLado = anteriores.size() / 2;

                    for (int i = 0; i < jogosPorLado; i++) {
                        Rectangle primeiro = anteriores.get(i * 2);
                        Rectangle segundo = anteriores.get(i * 2 + 1);

                        int centro = (centroY(primeiro) + centroY(segundo)) / 2;

                        posicoes.add(new Rectangle(
                                xEsquerda,
                                centro - ALTURA_CARD / 2,
                                LARGURA_CARD,
                                ALTURA_CARD
                        ));
                    }

                    for (int i = 0; i < jogosPorLado; i++) {
                        int primeiroIndice = jogosAnterioresPorLado + i * 2;
                        int segundoIndice = primeiroIndice + 1;

                        Rectangle primeiro = anteriores.get(primeiroIndice);
                        Rectangle segundo = anteriores.get(segundoIndice);

                        int centro = (centroY(primeiro) + centroY(segundo)) / 2;

                        posicoes.add(new Rectangle(
                                xDireita,
                                centro - ALTURA_CARD / 2,
                                LARGURA_CARD,
                                ALTURA_CARD
                        ));
                    }
                }

                posicoesPorRonda.put(tamanhoRonda, posicoes);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (ordemRondas.isEmpty()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            desenharTitulosRondas(g2);
            desenharLinhasBracket(g2);

            g2.dispose();
        }

        private void desenharTitulosRondas(Graphics2D g2) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.setColor(Tema.COR_TEXTO_SECUNDARIO);

            for (Integer tamanhoRonda : ordemRondas) {
                List<Rectangle> posicoes = posicoesPorRonda.get(tamanhoRonda);

                if (posicoes == null || posicoes.isEmpty()) {
                    continue;
                }

                String titulo = nomeDaRonda(tamanhoRonda);

                if (posicoes.size() == 1) {
                    desenharTextoCentrado(
                            g2,
                            titulo,
                            centroX(posicoes.get(0)),
                            42
                    );
                } else {
                    int metade = posicoes.size() / 2;

                    desenharTextoCentrado(
                            g2,
                            titulo,
                            centroX(posicoes.get(0)),
                            42
                    );

                    desenharTextoCentrado(
                            g2,
                            titulo,
                            centroX(posicoes.get(metade)),
                            42
                    );
                }
            }
        }

        private void desenharLinhasBracket(Graphics2D g2) {
            if (ordemRondas.size() < 2) {
                return;
            }

            g2.setColor(Tema.COR_LINHA);
            g2.setStroke(new BasicStroke(2.3f));

            for (int indiceRonda = 0; indiceRonda < ordemRondas.size() - 1; indiceRonda++) {
                List<Rectangle> atuais = posicoesPorRonda.get(ordemRondas.get(indiceRonda));
                List<Rectangle> proximas = posicoesPorRonda.get(ordemRondas.get(indiceRonda + 1));

                if (atuais == null || proximas == null || proximas.isEmpty()) {
                    continue;
                }

                int metadeAtual = atuais.size() / 2;
                boolean proximaEFinal = proximas.size() == 1;
                int metadeProxima = proximas.size() / 2;

                for (int i = 0; i < atuais.size(); i++) {
                    Rectangle origem = atuais.get(i);
                    Rectangle destino;

                    boolean ladoEsquerdo = i < metadeAtual;

                    if (proximaEFinal) {
                        destino = proximas.get(0);
                    } else if (ladoEsquerdo) {
                        int indiceDestino = i / 2;
                        destino = proximas.get(indiceDestino);
                    } else {
                        int indiceNoLado = i - metadeAtual;
                        int indiceDestino = metadeProxima + indiceNoLado / 2;
                        destino = proximas.get(indiceDestino);
                    }

                    desenharLigacao(g2, origem, destino, ladoEsquerdo);
                }
            }
        }

        private void desenharLigacao(
                Graphics2D g2,
                Rectangle origem,
                Rectangle destino,
                boolean ladoEsquerdo
        ) {
            int inicioX = ladoEsquerdo
                    ? origem.x + origem.width
                    : origem.x;

            int fimX = ladoEsquerdo
                    ? destino.x
                    : destino.x + destino.width;

            int inicioY = centroY(origem);
            int fimY = centroY(destino);
            int meioX = (inicioX + fimX) / 2;

            g2.drawLine(inicioX, inicioY, meioX, inicioY);
            g2.drawLine(meioX, inicioY, meioX, fimY);
            g2.drawLine(meioX, fimY, fimX, fimY);
        }

        private void desenharTextoCentrado(
                Graphics2D g2,
                String texto,
                int centroX,
                int y
        ) {
            FontMetrics metrics = g2.getFontMetrics();
            int x = centroX - metrics.stringWidth(texto) / 2;
            g2.drawString(texto, x, y);
        }

        private int centroX(Rectangle retangulo) {
            return retangulo.x + retangulo.width / 2;
        }

        private int centroY(Rectangle retangulo) {
            return retangulo.y + retangulo.height / 2;
        }
    }

    private final class MatchCard extends RoundedPanel {

        private final NoJogo no;

        private MatchCard(NoJogo no) {
            super(
                    16,
                    obterVencedor(no) == null
                            ? Tema.COR_CARD
                            : Tema.COR_VERDE_SUAVE
            );

            this.no = no;

            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(13, 14, 13, 14));

            add(criarConteudo(), BorderLayout.CENTER);
        }

        private JPanel criarConteudo() {
            JPanel conteudo = new JPanel();
            conteudo.setOpaque(false);
            conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));

            JLabel data = new JLabel(obterTextoData());
            data.setFont(Tema.FONTE_TEXTO_PEQUENO);
            data.setForeground(Tema.COR_TEXTO_SECUNDARIO);
            data.setAlignmentX(Component.LEFT_ALIGNMENT);

            conteudo.add(data);
            conteudo.add(Box.createVerticalStrut(10));

            if (no.temBye()) {
                String equipa = no.equipaNaoBye();

                conteudo.add(criarLinhaEquipa(equipa, "", true));
                conteudo.add(Box.createVerticalStrut(12));

                JLabel passagem = new JLabel("Passagem direta");
                passagem.setFont(Tema.FONTE_TEXTO_PEQUENO);
                passagem.setForeground(Tema.COR_VERDE_FORTE);
                passagem.setAlignmentX(Component.LEFT_ALIGNMENT);
                conteudo.add(passagem);

            } else {
                int[] golos = no.jogoReal == null
                        ? null
                        : lerResultado(no.jogoReal.getResultado());

                String vencedor = obterVencedor(no);

                conteudo.add(criarLinhaEquipa(
                        no.equipaA,
                        golos == null ? "" : String.valueOf(golos[0]),
                        no.equipaA.equalsIgnoreCase(vencedor)
                ));

                conteudo.add(Box.createVerticalStrut(7));

                conteudo.add(criarLinhaEquipa(
                        no.equipaB,
                        golos == null ? "" : String.valueOf(golos[1]),
                        no.equipaB.equalsIgnoreCase(vencedor)
                ));

                conteudo.add(Box.createVerticalStrut(12));

                JLabel estado = new JLabel(obterTextoEstado(vencedor));
                estado.setFont(Tema.FONTE_TEXTO_PEQUENO);
                estado.setForeground(
                        vencedor == null
                                ? Tema.COR_TEXTO_SECUNDARIO
                                : Tema.COR_VERDE_FORTE
                );
                estado.setAlignmentX(Component.LEFT_ALIGNMENT);
                conteudo.add(estado);
            }

            return conteudo;
        }

        private JPanel criarLinhaEquipa(
                String equipa,
                String golos,
                boolean vencedora
        ) {
            JPanel linha = new JPanel(new BorderLayout());
            linha.setOpaque(false);
            linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

            JLabel nome = new JLabel(equipa == null ? "A definir" : equipa);
            nome.setFont(new Font(
                    "Segoe UI",
                    vencedora ? Font.BOLD : Font.PLAIN,
                    13
            ));
            nome.setForeground(
                    equipaPorDefinir(equipa)
                            ? Tema.COR_TEXTO_SECUNDARIO
                            : Tema.COR_TEXTO_PRINCIPAL
            );

            JLabel resultado = new JLabel(golos);
            resultado.setFont(new Font("Segoe UI", Font.BOLD, 15));
            resultado.setForeground(
                    vencedora
                            ? Tema.COR_VERDE_FORTE
                            : Tema.COR_TEXTO_PRINCIPAL
            );

            linha.add(nome, BorderLayout.WEST);
            linha.add(resultado, BorderLayout.EAST);

            return linha;
        }

        private String obterTextoData() {
            if (no.jogoReal == null) {
                return nomeDaRonda(no.tamanhoRonda);
            }

            String data = formatarData(no.jogoReal.getData());
            String hora = no.jogoReal.getHora() == null ? "" : no.jogoReal.getHora();

            if (data.isEmpty() && hora.isEmpty()) {
                return nomeDaRonda(no.tamanhoRonda);
            }

            return data + (hora.isEmpty() ? "" : "  " + hora);
        }

        private String obterTextoEstado(String vencedor) {
            if (vencedor != null) {
                return "Vencedor: " + vencedor;
            }

            if (no.jogoReal == null) {
                return "A aguardar resultados";
            }

            String estado = no.jogoReal.getEstado();

            if (estado == null || estado.trim().isEmpty()) {
                return "A aguardar resultado";
            }

            return estado;
        }
    }
}
