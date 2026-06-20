package Models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegrasCampeonatoTest {

    @Test
    void naoPermiteAdicionarEquipaRepetidaNoMesmoCampeonato() {
        Campeonato campeonato = campeonato(4, 1);

        assertTrue(campeonato.adicionarEquipa("FC Porto"));
        assertFalse(campeonato.adicionarEquipa("fc porto"));
    }

    @Test
    void naoPermiteUltrapassarNumeroDeEquipasDefinido() {
        Campeonato campeonato = campeonato(2, 1);

        assertTrue(campeonato.adicionarEquipa("Equipa A"));
        assertTrue(campeonato.adicionarEquipa("Equipa B"));
        assertFalse(campeonato.adicionarEquipa("Equipa C"));
    }

    @Test
    void campeonatoSoIniciaComEquipasEstadioEGruposGerados() {
        Campeonato campeonato = campeonato(2, 1);

        campeonato.adicionarEquipa("Equipa A");
        campeonato.adicionarEquipa("Equipa B");
        campeonato.adicionarEstadio(new Estadio("Estádio A", "Leiria", "IPL", 100, 20, 10));
        campeonato.setGruposGerados(true);

        assertTrue(campeonato.podeIniciarCampeonato());
        assertTrue(campeonato.iniciarCampeonato());
        assertFalse(campeonato.adicionarEquipa("Equipa C"));
    }

    private Campeonato campeonato(int numeroEquipas, int numeroEstadios) {
        return new Campeonato(
                "Liga Teste",
                numeroEquipas,
                numeroEstadios,
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 10),
                LocalDate.of(2026, 6, 11),
                LocalDate.of(2026, 6, 20)
        );
    }
}
