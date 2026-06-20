package Models.Jogos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JogoServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void procurarPorIdEncontraJogoSemDistinguirMaiusculas() throws IOException {
        Path ficheiro = tempDir.resolve("jogos.tsv");
        Files.writeString(
                ficheiro,
                "J001\t2026-05-25\t20:30\tFC Porto\tSL Benfica\tEstadio\tGrupo A\tRealizado\t2-1\tLiga Teste\n",
                StandardCharsets.UTF_8
        );

        Jogo jogo = new JogoService(ficheiro).procurarPorId("j001");

        assertEquals("FC Porto vs SL Benfica", jogo.getNomeJogo());
        assertEquals("Liga Teste", jogo.getCampeonato());
    }
}
