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

    private static final Path FICHEIRO_JOGOS = Paths.get("data", "jogos.tsv");

    public List<Jogo> listarJogos() {
        if (!Files.exists(FICHEIRO_JOGOS)) {
            return Collections.emptyList();
        }

        List<Jogo> jogos = new ArrayList<>();

        try {
            for (String linha : Files.readAllLines(FICHEIRO_JOGOS, StandardCharsets.UTF_8)) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split("\t", -1);

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
                        campos.length > 9 ? campos[9] : "Campeonato Principal"
                ));
            }
        } catch (IOException e) {
            return Collections.emptyList();
        }

        return jogos;
    }

    public Jogo procurarPorId(String id) {
        for (Jogo jogo : listarJogos()) {
            if (jogo.getId().equalsIgnoreCase(id)) {
                return jogo;
            }
        }

        return null;
    }
}
