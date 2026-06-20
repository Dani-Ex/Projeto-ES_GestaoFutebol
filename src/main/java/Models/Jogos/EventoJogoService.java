package Models.Jogos;

import Models.Jogadores.Jogador;
import Models.Jogadores.JogadorService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EventoJogoService {

    public static final String CARTAO_AMARELO = "Amarelo";
    public static final String CARTAO_VERMELHO = "Vermelho";
    public static final String CARTAO_SEGUNDO_AMARELO = "Segundo amarelo";

    private static final Path FICHEIRO_EVENTOS =
            Paths.get("data", "eventos_jogo.tsv");

    private static final EventoJogoService INSTANCE =
            new EventoJogoService();

    private final JogoService jogoService = JogoService.getInstance();
    private final JogadorService jogadorService = JogadorService.getInstance();

    private EventoJogoService() {
    }

    public static EventoJogoService getInstance() {
        return INSTANCE;
    }

    public synchronized List<EventoJogo> listarEventosDoJogo(Jogo jogo) {
        List<EventoJogo> resultado = new ArrayList<>();

        if (jogo == null) {
            return resultado;
        }

        String chave = criarChaveJogo(jogo);

        for (EventoJogo evento : listarTodosEventos()) {
            if (chave.equals(evento.getChaveJogo())) {
                resultado.add(evento);
            }
        }

        resultado.sort(Comparator
                .comparingInt(EventoJogo::getMinuto)
                .thenComparingInt(EventoJogo::getSegundo)
                .thenComparing(EventoJogo::getId));

        return resultado;
    }

    public synchronized int[] calcularPlacar(Jogo jogo) {
        int golosCasa = 0;
        int golosVisitante = 0;

        for (EventoJogo evento : listarEventosDoJogo(jogo)) {
            if (!evento.eGolo()) {
                continue;
            }

            if (igual(evento.getEquipa(), jogo.getEquipaA())) {
                golosCasa++;
            } else if (igual(evento.getEquipa(), jogo.getEquipaB())) {
                golosVisitante++;
            }
        }

        return new int[]{golosCasa, golosVisitante};
    }

    public synchronized int contarCartoesDoJogo(Jogo jogo) {
        int total = 0;

        for (EventoJogo evento : listarEventosDoJogo(jogo)) {
            if (evento.eCartao()) {
                total++;
            }
        }

        return total;
    }

    public synchronized Jogo adicionarGolo(
            Jogo jogo,
            Jogador jogador,
            int minuto,
            int segundo
    ) {
        validarJogoJogadorETempo(jogo, jogador, minuto, segundo);

        EventoJogo evento = new EventoJogo(
                gerarNovoId(),
                criarChaveJogo(jogo),
                EventoJogo.TIPO_GOLO,
                jogador.getEquipa(),
                jogador.getNome(),
                jogador.getNumero(),
                minuto,
                segundo,
                "Golo"
        );

        adicionarEvento(evento);

        jogadorService.ajustarEstatisticasDoEvento(
                jogador,
                1,
                0
        );

        return atualizarResultadoPelosEventos(jogo);
    }

    public synchronized void adicionarCartao(
            Jogo jogo,
            Jogador jogador,
            String tipoCartao,
            int minuto,
            int segundo
    ) {
        validarJogoJogadorETempo(jogo, jogador, minuto, segundo);
        validarTipoCartao(tipoCartao);

        EventoJogo evento = new EventoJogo(
                gerarNovoId(),
                criarChaveJogo(jogo),
                EventoJogo.TIPO_CARTAO,
                jogador.getEquipa(),
                jogador.getNome(),
                jogador.getNumero(),
                minuto,
                segundo,
                tipoCartao.trim()
        );

        adicionarEvento(evento);

        jogadorService.ajustarEstatisticasDoEvento(
                jogador,
                0,
                1
        );
    }

    public synchronized Jogo removerEvento(Jogo jogo, String idEvento) {
        if (jogo == null) {
            throw new IllegalArgumentException("O jogo selecionado não é válido.");
        }

        List<EventoJogo> eventos = listarTodosEventos();
        EventoJogo removido = null;

        for (EventoJogo evento : eventos) {
            if (idEvento.equalsIgnoreCase(evento.getId())
                    && criarChaveJogo(jogo).equals(evento.getChaveJogo())) {
                removido = evento;
                break;
            }
        }

        if (removido == null) {
            throw new IllegalArgumentException("O evento selecionado já não existe.");
        }

        eventos.remove(removido);
        guardarTodosEventos(eventos);

        Jogador jogador = jogadorService.procurarJogador(
                removido.getJogadorNome(),
                removido.getJogadorNumero(),
                removido.getEquipa(),
                jogo.getCampeonato()
        );

        if (jogador != null) {
            jogadorService.ajustarEstatisticasDoEvento(
                    jogador,
                    removido.eGolo() ? -1 : 0,
                    removido.eCartao() ? -1 : 0
            );
        }

        if (removido.eGolo()) {
            return atualizarResultadoPelosEventos(jogo);
        }

        return jogoService.procurarJogo(jogo);
    }

    public synchronized Jogo atualizarEstadoDoJogo(Jogo jogo, String novoEstado) {
        if (jogo == null) {
            throw new IllegalArgumentException("O jogo selecionado não é válido.");
        }

        String resultado = listarEventosDoJogo(jogo).isEmpty()
                ? jogo.getResultado()
                : calcularResultadoParaEstado(jogo, novoEstado);

        return jogoService.atualizarEstadoEResultado(
                jogo,
                novoEstado,
                resultado
        );
    }

    private Jogo atualizarResultadoPelosEventos(Jogo jogo) {
        String resultado = calcularResultadoParaEstado(
                jogo,
                jogo.getEstado()
        );

        return jogoService.atualizarEstadoEResultado(
                jogo,
                jogo.getEstado(),
                resultado
        );
    }

    private String calcularResultadoParaEstado(Jogo jogo, String estado) {
        int[] placar = calcularPlacar(jogo);

        if (placar[0] > 0 || placar[1] > 0) {
            return placar[0] + "-" + placar[1];
        }

        if ("Realizado".equalsIgnoreCase(estado)
                || "Finalizado".equalsIgnoreCase(estado)) {
            return "0-0";
        }

        return "-";
    }

    private void validarJogoJogadorETempo(
            Jogo jogo,
            Jogador jogador,
            int minuto,
            int segundo
    ) {
        if (jogador == null) {
            throw new IllegalArgumentException(
                    "Seleciona o jogador responsável pelo evento."
            );
        }

        if (minuto < 0 || minuto > 130) {
            throw new IllegalArgumentException(
                    "O minuto deve estar entre 0 e 130."
            );
        }

        if (segundo < 0 || segundo > 59) {
            throw new IllegalArgumentException(
                    "Os segundos devem estar entre 0 e 59."
            );
        }

        boolean pertenceAoJogo =
                igual(jogador.getEquipa(), jogo.getEquipaA())
                        || igual(jogador.getEquipa(), jogo.getEquipaB());

        if (!pertenceAoJogo) {
            throw new IllegalArgumentException(
                    "O jogador selecionado não pertence a este jogo."
            );
        }

        if (!igual(jogador.getCampeonato(), jogo.getCampeonato())) {
            throw new IllegalArgumentException(
                    "O jogador selecionado não pertence a este campeonato."
            );
        }
    }

    private void validarTipoCartao(String tipoCartao) {
        if (CARTAO_AMARELO.equalsIgnoreCase(tipoCartao)
                || CARTAO_VERMELHO.equalsIgnoreCase(tipoCartao)
                || CARTAO_SEGUNDO_AMARELO.equalsIgnoreCase(tipoCartao)) {
            return;
        }

        throw new IllegalArgumentException("Tipo de cartão inválido.");
    }

    private void adicionarEvento(EventoJogo evento) {
        List<EventoJogo> eventos = listarTodosEventos();
        eventos.add(evento);
        guardarTodosEventos(eventos);
    }

    private List<EventoJogo> listarTodosEventos() {
        List<EventoJogo> eventos = new ArrayList<>();

        if (!Files.exists(FICHEIRO_EVENTOS)) {
            return eventos;
        }

        try {
            for (String linha : Files.readAllLines(
                    FICHEIRO_EVENTOS,
                    StandardCharsets.UTF_8
            )) {
                EventoJogo evento = converterLinha(linha);

                if (evento != null) {
                    eventos.add(evento);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível ler data/eventos_jogo.tsv.",
                    e
            );
        }

        return eventos;
    }

    private void guardarTodosEventos(List<EventoJogo> eventos) {
        try {
            Files.createDirectories(FICHEIRO_EVENTOS.getParent());

            List<String> linhas = new ArrayList<>();

            for (EventoJogo evento : eventos) {
                linhas.add(String.join("\t",
                        limpar(evento.getId()),
                        limpar(evento.getChaveJogo()),
                        limpar(evento.getTipo()),
                        limpar(evento.getEquipa()),
                        limpar(evento.getJogadorNome()),
                        String.valueOf(evento.getJogadorNumero()),
                        String.valueOf(evento.getMinuto()),
                        String.valueOf(evento.getSegundo()),
                        limpar(evento.getDetalhe())
                ));
            }

            Files.write(
                    FICHEIRO_EVENTOS,
                    linhas,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível guardar data/eventos_jogo.tsv.",
                    e
            );
        }
    }

    private EventoJogo converterLinha(String linha) {
        if (linha == null || linha.isBlank()) {
            return null;
        }

        String[] campos = linha.split("\\t", -1);

        if (campos.length < 9) {
            return null;
        }

        try {
            return new EventoJogo(
                    campos[0],
                    campos[1],
                    campos[2],
                    campos[3],
                    campos[4],
                    Integer.parseInt(campos[5]),
                    Integer.parseInt(campos[6]),
                    Integer.parseInt(campos[7]),
                    campos[8]
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String gerarNovoId() {
        int maior = 0;

        for (EventoJogo evento : listarTodosEventos()) {
            String numeros = evento.getId().replaceAll("\\D", "");

            try {
                maior = Math.max(maior, Integer.parseInt(numeros));
            } catch (NumberFormatException ignored) {
            }
        }

        return String.format("EV%05d", maior + 1);
    }

    private String criarChaveJogo(Jogo jogo) {
        return limpar(jogo.getId()) + "|"
                + limpar(jogo.getData()) + "|"
                + limpar(jogo.getHora()) + "|"
                + limpar(jogo.getEquipaA()) + "|"
                + limpar(jogo.getEquipaB()) + "|"
                + limpar(jogo.getCampeonato());
    }

    private boolean igual(String a, String b) {
        return normalizar(a).equals(normalizar(b));
    }

    private String normalizar(String valor) {
        return valor == null
                ? ""
                : valor.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private String limpar(String valor) {
        return valor == null
                ? ""
                : valor.replace("\t", " ")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}