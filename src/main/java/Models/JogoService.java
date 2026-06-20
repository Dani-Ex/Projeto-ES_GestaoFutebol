package Models;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JogoService {

    private static final Path FICHEIRO_JOGOS =
            Paths.get("data", "jogos.tsv");

    private static final JogoService INSTANCE =
            new JogoService();

    private final Path ficheiroJogos;

    public JogoService() {
        this(FICHEIRO_JOGOS);
    }

    JogoService(Path ficheiroJogos) {
        this.ficheiroJogos = ficheiroJogos;
    }

    public static JogoService getInstance() {
        return INSTANCE;
    }

    public synchronized List<Jogo> listarJogos() {
        if (!Files.exists(ficheiroJogos)) {
            return Collections.emptyList();
        }

        List<Jogo> jogos = new ArrayList<>();

        try {
            for (String linha : Files.readAllLines(
                    ficheiroJogos,
                    StandardCharsets.UTF_8
            )) {
                if (linha == null || linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split("\\t", -1);

                if (campos.length < 9) {
                    continue;
                }

                jogos.add(new Jogo(
                        campos[0],
                        campos[1],
                        campos[2],
                        campos[3],
                        campos[4],
                        campos[5],
                        campos[6],
                        campos[7],
                        campos[8],
                        campos.length > 9
                                ? campos[9]
                                : "Campeonato Principal"
                ));
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível ler os jogos.",
                    e
            );
        }

        return jogos;
    }

    public synchronized Jogo procurarPorId(String id) {
        if (id == null) {
            return null;
        }

        for (Jogo jogo : listarJogos()) {
            if (jogo.getId().equalsIgnoreCase(id)) {
                return jogo;
            }
        }

        return null;
    }

    public synchronized Jogo procurarJogo(Jogo jogoProcurado) {
        if (jogoProcurado == null) {
            return null;
        }

        for (Jogo jogo : listarJogos()) {
            if (eMesmoJogo(jogo, jogoProcurado)) {
                return jogo;
            }
        }

        return null;
    }

    public synchronized void adicionarJogo(Jogo jogo) {
        if (jogo == null) {
            throw new IllegalArgumentException(
                    "O jogo não pode ser vazio."
            );
        }

        if (procurarPorId(jogo.getId()) != null) {
            throw new IllegalArgumentException(
                    "Já existe um jogo com esse identificador."
            );
        }

        List<Jogo> jogos = new ArrayList<>(listarJogos());
        jogos.add(jogo);

        guardarJogos(jogos);
    }

    public synchronized void atualizarJogo(Jogo jogoAtualizado) {
        if (jogoAtualizado == null) {
            throw new IllegalArgumentException(
                    "O jogo não pode ser vazio."
            );
        }

        List<Jogo> jogos = new ArrayList<>(listarJogos());
        boolean encontrado = false;

        for (int i = 0; i < jogos.size(); i++) {
            if (eMesmoJogo(jogos.get(i), jogoAtualizado)) {
                jogos.set(i, jogoAtualizado);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            throw new IllegalArgumentException(
                    "O jogo selecionado já não existe."
            );
        }

        guardarJogos(jogos);
    }

    public synchronized Jogo atualizarEstadoEResultado(
            Jogo jogoOriginal,
            String estado,
            String resultado
    ) {
        if (jogoOriginal == null) {
            throw new IllegalArgumentException(
                    "O jogo selecionado já não existe."
            );
        }

        List<Jogo> jogos = new ArrayList<>(listarJogos());
        Jogo atualizado = null;

        for (int i = 0; i < jogos.size(); i++) {
            Jogo atual = jogos.get(i);

            if (eMesmoJogo(atual, jogoOriginal)) {
                atualizado = new Jogo(
                        atual.getId(),
                        atual.getData(),
                        atual.getHora(),
                        atual.getEquipaA(),
                        atual.getEquipaB(),
                        atual.getEstadio(),
                        atual.getFaseGrupo(),
                        limpar(estado),
                        limpar(resultado),
                        atual.getCampeonato()
                );

                jogos.set(i, atualizado);
                break;
            }
        }

        if (atualizado == null) {
            throw new IllegalArgumentException(
                    "O jogo selecionado já não existe."
            );
        }

        guardarJogos(jogos);
        return atualizado;
    }

    public synchronized String gerarNovoId() {
        int maior = 0;

        for (Jogo jogo : listarJogos()) {
            String numeros = jogo.getId()
                    .replaceAll("\\D", "");

            try {
                maior = Math.max(
                        maior,
                        Integer.parseInt(numeros)
                );
            } catch (NumberFormatException ignored) {
            }
        }

        return String.format("J%03d", maior + 1);
    }

    private void guardarJogos(List<Jogo> jogos) {
        try {
            Path pasta = ficheiroJogos.getParent();

            if (pasta != null) {
                Files.createDirectories(pasta);
            }

            List<String> linhas = new ArrayList<>();

            for (Jogo jogo : jogos) {
                linhas.add(String.join("\t",
                        limpar(jogo.getId()),
                        limpar(jogo.getData()),
                        limpar(jogo.getHora()),
                        limpar(jogo.getEquipaA()),
                        limpar(jogo.getEquipaB()),
                        limpar(jogo.getEstadio()),
                        limpar(jogo.getFaseGrupo()),
                        limpar(jogo.getEstado()),
                        limpar(jogo.getResultado()),
                        limpar(jogo.getCampeonato())
                ));
            }

            Files.write(
                    ficheiroJogos,
                    linhas,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível guardar os jogos.",
                    e
            );
        }
    }

    private boolean eMesmoJogo(Jogo jogoA, Jogo jogoB) {
        return igual(jogoA.getId(), jogoB.getId())
                && igual(jogoA.getData(), jogoB.getData())
                && igual(jogoA.getHora(), jogoB.getHora())
                && igual(jogoA.getEquipaA(), jogoB.getEquipaA())
                && igual(jogoA.getEquipaB(), jogoB.getEquipaB())
                && igual(jogoA.getCampeonato(), jogoB.getCampeonato());
    }

    private boolean igual(String primeiro, String segundo) {
        return normalizar(primeiro).equals(normalizar(segundo));
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