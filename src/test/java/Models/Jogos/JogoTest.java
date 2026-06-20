package Models.Jogos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JogoTest {

    @Test
    void criaJogoComTodosOsDadosCorretos() {
        Jogo jogo = new Jogo(
                "J001",
                "2026-07-10",
                "20:00",
                "Portugal",
                "Brasil",
                "Estádio Nacional",
                "Grupo A",
                "Agendado",
                "-",
                "Mundial 2026"
        );

        assertEquals("J001", jogo.getId());
        assertEquals("2026-07-10", jogo.getData());
        assertEquals("20:00", jogo.getHora());
        assertEquals("Portugal", jogo.getEquipaA());
        assertEquals("Brasil", jogo.getEquipaB());
        assertEquals("Estádio Nacional", jogo.getEstadio());
        assertEquals("Grupo A", jogo.getFaseGrupo());
        assertEquals("Agendado", jogo.getEstado());
        assertEquals("-", jogo.getResultado());
        assertEquals("Mundial 2026", jogo.getCampeonato());
    }

    @Test
    void criaJogoDeFinalComResultadoRealizado() {
        Jogo jogo = new Jogo(
                "J010",
                "2026-07-30",
                "20:00",
                "Portugal",
                "Espanha",
                "Estádio Nacional",
                "Final",
                "Realizado",
                "2-1",
                "Mundial 2026"
        );

        assertEquals("Final", jogo.getFaseGrupo());
        assertEquals("Realizado", jogo.getEstado());
        assertEquals("2-1", jogo.getResultado());
    }
}
