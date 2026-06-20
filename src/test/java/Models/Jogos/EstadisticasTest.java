package Models.Jogos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstatisticasTest {

    @Test
    void eventoDeGoloEIdentificadoCorretamente() {
        EventoJogo evento = evento(EventoJogo.TIPO_GOLO, 12, 8, "Golo");

        assertTrue(evento.eGolo());
        assertFalse(evento.eCartao());
        assertEquals("Golo", evento.getTipoApresentacao());
    }

    @Test
    void eventoDeCartaoEIdentificadoCorretamente() {
        EventoJogo evento = evento(EventoJogo.TIPO_CARTAO, 45, 0, "Amarelo");

        assertTrue(evento.eCartao());
        assertFalse(evento.eGolo());
        assertEquals("Cartão", evento.getTipoApresentacao());
        assertEquals("Amarelo", evento.getDetalhe());
    }

    @Test
    void tempoDoEventoEFormatadoComDoisDigitos() {
        EventoJogo evento = evento(EventoJogo.TIPO_GOLO, 5, 7, "Golo");

        assertEquals("05:07", evento.getTempoFormatado());
    }

    private EventoJogo evento(String tipo, int minuto, int segundo, String detalhe) {
        return new EventoJogo(
                "EV001", "J001|2026-06-20|18:00|FC A|SC B|Liga Teste",
                tipo, "FC A", "João Silva", 10, minuto, segundo, detalhe
        );
    }
}
