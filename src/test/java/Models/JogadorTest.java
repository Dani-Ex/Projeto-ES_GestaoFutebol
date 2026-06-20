package Models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JogadorTest {

    @Test
    void calculaIdadeComBaseNaDataNascimento() {
        Jogador jogador = criarJogador(LocalDate.now().minusYears(20).minusDays(1), true);

        assertEquals(20, jogador.getIdade());
    }

    @Test
    void alternarEstadoMudaJogadorDeAtivoParaInativo() {
        Jogador jogador = criarJogador(LocalDate.of(2000, 5, 20), true);

        jogador.alternarEstado();

        assertFalse(jogador.isAtivo());
        assertEquals("Inativo", jogador.getEstadoTexto());
    }

    @Test
    void guardaEstatisticasIndividuaisDoJogador() {
        Jogador jogador = criarJogador(LocalDate.of(2000, 5, 20), true);

        jogador.setGolos(4);
        jogador.setAssistencias(2);
        jogador.setCartoes(1);
        jogador.setMinutos(360);

        assertEquals(4, jogador.getGolos());
        assertEquals(2, jogador.getAssistencias());
        assertEquals(1, jogador.getCartoes());
        assertEquals(360, jogador.getMinutos());
    }

    private Jogador criarJogador(LocalDate nascimento, boolean ativo) {
        return new Jogador(
                "João Silva", 10, "Avançado", 72, 1.80,
                "Direito", nascimento, "Portugal", "Porto",
                "FC Teste", "Liga Teste", "A", "1",
                0, 0, 0, 0, 0, 0, 0, 0, ativo
        );
    }
}
