package Models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EquipaServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void adicionarEquipaImpedeDuplicadoNoMesmoCampeonato() {
        EquipaService service = new EquipaService(tempDir.resolve("equipas.tsv"));

        service.adicionarEquipa(equipa("FC Teste", "Liga Teste", "A", "Porto", "Portugal"));

        assertThrows(
                IllegalArgumentException.class,
                () -> service.adicionarEquipa(equipa("fc teste", "liga teste", "B", "Lisboa", "Portugal"))
        );

        assertEquals(1, service.listarEquipas().size());
    }

    @Test
    void pesquisarEquipasEncontraPorNomeCampeonatoCidadeOuPais() {
        EquipaService service = new EquipaService(tempDir.resolve("equipas.tsv"));
        Equipa equipaPorto = equipa("FC Teste", "Liga Norte", "A", "Porto", "Portugal");
        Equipa equipaMadrid = equipa("Madrid Teste", "Liga Iberica", "B", "Madrid", "Espanha");

        service.adicionarEquipa(equipaPorto);
        service.adicionarEquipa(equipaMadrid);

        List<Equipa> porCidade = service.pesquisarEquipas("porto");
        List<Equipa> porCampeonato = service.pesquisarEquipas("iberica");

        assertEquals(List.of(equipaPorto), porCidade);
        assertEquals(List.of(equipaMadrid), porCampeonato);
    }

    private Equipa equipa(String nome, String campeonato, String grupo, String cidade, String pais) {
        return new Equipa(
                nome,
                cidade,
                pais,
                "Treinador",
                "Capitao",
                campeonato,
                grupo,
                0,
                true
        );
    }
}
