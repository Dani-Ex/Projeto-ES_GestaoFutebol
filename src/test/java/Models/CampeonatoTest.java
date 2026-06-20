package Models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CampeonatoTest {

    @Test
    void criaCampeonatoEmConfiguracaoComDadosCorretos() {
        Campeonato campeonato = criarCampeonato();

        assertEquals("Liga Teste", campeonato.getNome());
        assertEquals(8, campeonato.getNumeroEquipasNecessarias());
        assertEquals(2, campeonato.getNumeroEstadiosNecessarios());

        assertEquals(
                LocalDate.of(2026, 7, 1),
                campeonato.getDataInicioCampeonato()
        );

        assertEquals(
                LocalDate.of(2026, 7, 15),
                campeonato.getDataFimGrupos()
        );

        assertEquals(
                LocalDate.of(2026, 7, 18),
                campeonato.getDataInicioEliminatoria()
        );

        assertEquals(
                LocalDate.of(2026, 7, 31),
                campeonato.getDataFimCampeonato()
        );

        assertTrue(campeonato.isEmConfiguracao());
        assertFalse(campeonato.isIniciado());
        assertFalse(campeonato.isFinalizado());
        assertFalse(campeonato.isGruposGerados());
        assertFalse(campeonato.isFaseGruposTerminada());
        assertFalse(campeonato.isEliminatoriasGeradas());
    }

    @Test
    void guardaInformacaoDasFasesDoCampeonato() {
        Campeonato campeonato = criarCampeonato();

        campeonato.setGruposGerados(true);
        campeonato.setFaseGruposTerminada(true);
        campeonato.setEliminatoriasGeradas(true);

        assertTrue(campeonato.isGruposGerados());
        assertTrue(campeonato.isFaseGruposTerminada());
        assertTrue(campeonato.isEliminatoriasGeradas());
    }

    @Test
    void alteraDadosEditaveisDoCampeonato() {
        Campeonato campeonato = criarCampeonato();

        campeonato.setNome("Liga Atualizada");
        campeonato.setNumeroEquipasNecessarias(16);
        campeonato.setNumeroEstadiosNecessarios(4);

        assertEquals("Liga Atualizada", campeonato.getNome());
        assertEquals(16, campeonato.getNumeroEquipasNecessarias());
        assertEquals(4, campeonato.getNumeroEstadiosNecessarios());
    }

    private Campeonato criarCampeonato() {
        return new Campeonato(
                "Liga Teste",
                8,
                2,
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 15),
                LocalDate.of(2026, 7, 18),
                LocalDate.of(2026, 7, 31)
        );
    }
}
