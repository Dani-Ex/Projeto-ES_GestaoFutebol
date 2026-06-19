package Models;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardLogicTest {

    @Test
    void ordenarClassificacaoFiltraGrupoECampeonatoEOrdenaPorPontosGolosENome() {
        Equipa liderPorGolos = equipa("FC Norte", "Liga Teste", "A", 10, 8);
        Equipa segunda = equipa("AC Centro", "Liga Teste", "A", 10, 4);
        Equipa terceiro = equipa("BC Sul", "Liga Teste", "A", 7, 12);
        Equipa outroGrupo = equipa("Fora Grupo", "Liga Teste", "B", 30, 30);
        Equipa outroCampeonato = equipa("Fora Liga", "Liga Alternativa", "A", 30, 30);

        List<Equipa> classificacao = DashboardLogic.ordenarClassificacao(
                List.of(terceiro, outroGrupo, segunda, outroCampeonato, liderPorGolos),
                "Liga Teste",
                "A"
        );

        assertEquals(List.of(liderPorGolos, segunda, terceiro), classificacao);
    }

    @Test
    void listarJogosPorEstadoMostraRealizadosMaisRecentesRespeitandoLimite() {
        Jogo antigo = jogo("J001", "2026-05-20", "Realizado");
        Jogo recente = jogo("J002", "2026-05-30", "Realizado");
        Jogo meio = jogo("J003", "2026-05-25", "Realizado");
        Jogo agendado = jogo("J004", "2026-06-01", "Agendado");

        List<Jogo> realizados = DashboardLogic.listarJogosPorEstado(
                List.of(antigo, recente, agendado, meio),
                "Realizado",
                2
        );

        assertEquals(List.of(recente, meio), realizados);
    }

    @Test
    void listarJogosPorEstadoMostraAgendadosPorDataCrescente() {
        Jogo segundo = jogo("J001", "2026-06-20", "Agendado");
        Jogo primeiro = jogo("J002", "2026-06-10", "Agendado");
        Jogo realizado = jogo("J003", "2026-06-05", "Realizado");

        List<Jogo> agendados = DashboardLogic.listarJogosPorEstado(
                List.of(segundo, realizado, primeiro),
                "Agendado",
                4
        );

        assertEquals(List.of(primeiro, segundo), agendados);
    }

    private Equipa equipa(String nome, String campeonato, String grupo, int pontos, int golos) {
        Equipa equipa = new Equipa(
                nome,
                "Cidade",
                "Pais",
                "Treinador",
                "Capitao",
                campeonato,
                grupo,
                pontos,
                true
        );
        equipa.setGolos(golos);
        return equipa;
    }

    private Jogo jogo(String id, String data, String estado) {
        return new Jogo(
                id,
                data,
                "20:00",
                "Equipa A",
                "Equipa B",
                "Estadio",
                "Grupo A",
                estado,
                "-",
                "Liga Teste"
        );
    }
}
