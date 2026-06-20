package Models.Estadios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstadioTest {

    @Test
    void criaEstadioComTodosOsDadosCorretos() {
        Estadio estadio = new Estadio(
                "Estádio Nacional",
                "Lisboa",
                "Federação Portuguesa de Futebol",
                45000,
                2500,
                500
        );

        assertEquals("Estádio Nacional", estadio.getNome());
        assertEquals("Lisboa", estadio.getCidade());
        assertEquals("Federação Portuguesa de Futebol", estadio.getProprietario());
        assertEquals(45000, estadio.getLugaresNormal());
        assertEquals(2500, estadio.getLugaresVip());
        assertEquals(500, estadio.getLugaresPremium());
    }

    @Test
    void calculaCapacidadeTotalPelosLugaresGuardados() {
        Estadio estadio = new Estadio(
                "Estádio Teste",
                "Porto",
                "Clube Teste",
                10000,
                1000,
                200
        );

        int capacidadeTotal = estadio.getLugaresNormal()
                + estadio.getLugaresVip()
                + estadio.getLugaresPremium();

        assertEquals(11200, capacidadeTotal);
    }
}
