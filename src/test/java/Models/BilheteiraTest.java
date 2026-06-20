package Models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BilheteiraTest {

    @Test
    void estadioCalculaCapacidadeTotalDasTresZonas() {
        Estadio estadio = new Estadio("Estádio Teste", "Leiria", "IPL", 100, 25, 10);

        assertEquals(135, estadio.getCapacidadeTotal());
    }

    @Test
    void bilheteiraAplicaPrecosPadraoQuandoJogoNaoTemPrecosConfigurados() {
        BilheteriaService service = new BilheteriaService();
        String idInexistente = "J_TESTE_SEM_PRECOS_98431";

        assertEquals(12.50, service.getPreco(idInexistente, BilheteriaService.TIPO_NORMAL), 0.001);
        assertEquals(25.00, service.getPreco(idInexistente, BilheteriaService.TIPO_VIP), 0.001);
        assertEquals(40.00, service.getPreco(idInexistente, BilheteriaService.TIPO_PREMIUM), 0.001);
    }

    @Test
    void comprarBilhetesSemJogoSelecionadoLancaErro() {
        BilheteriaService service = new BilheteriaService();

        assertThrows(
                IllegalArgumentException.class,
                () -> service.comprarBilhetes(
                        null,
                        BilheteriaService.TIPO_NORMAL,
                        1,
                        "Cartão"
                )
        );
    }
}
