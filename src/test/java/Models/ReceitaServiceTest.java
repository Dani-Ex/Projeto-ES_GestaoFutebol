package Models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceitaServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void carregarReceitasCalculaLucroTotalDoJogo() throws IOException {
        Path ficheiro = tempDir.resolve("receitas.tsv");
        Files.writeString(ficheiro, "J001\t120\t1000.5\t300\t200\n", StandardCharsets.UTF_8);

        ReceitaService service = new ReceitaService(ficheiro);
        Receita receita = service.procurarPorJogo("j001");

        assertNotNull(receita);
        assertEquals(1500.5, receita.getLucro());
    }

    @Test
    void adicionarReceitaImpedeRegistarMesmoJogoDuasVezes() {
        Path ficheiro = tempDir.resolve("receitas.tsv");
        ReceitaService service = new ReceitaService(ficheiro);

        service.adicionarReceita(new Receita("J010", 50, 1000, 200, 300));

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> service.adicionarReceita(new Receita("j010", 40, 900, 100, 100))
        );

        assertTrue(erro.getMessage().contains("jogo"));
        assertEquals(1, service.listarReceitas().size());
    }
}
