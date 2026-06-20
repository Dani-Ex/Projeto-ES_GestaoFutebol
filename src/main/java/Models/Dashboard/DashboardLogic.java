package Models.Dashboard;

import Models.Equipas.Equipa;
import Models.Jogos.Jogo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class DashboardLogic {

    private DashboardLogic() {
    }

    public static List<Equipa> ordenarClassificacao(List<Equipa> equipas,
                                                     String campeonato,
                                                     String grupo) {
        List<Equipa> resultado = new ArrayList<>();

        for (Equipa equipa : equipas) {
            if (textoIgual(equipa.getCampeonato(), campeonato)
                    && textoIgual(valorOuTraco(equipa.getGrupo()), grupo)) {
                resultado.add(equipa);
            }
        }

        resultado.sort(Comparator
                .comparingInt(Equipa::getPontos).reversed()
                .thenComparing(Comparator.comparingInt(Equipa::getGolos).reversed())
                .thenComparing(Equipa::getNome));

        return resultado;
    }

    public static List<Jogo> listarJogosPorEstado(List<Jogo> jogos, String estado, int limite) {
        List<Jogo> resultado = new ArrayList<>();

        for (Jogo jogo : jogos) {
            if (jogo.getEstado().equalsIgnoreCase(estado)) {
                resultado.add(jogo);
            }
        }

        if ("Realizado".equalsIgnoreCase(estado)) {
            resultado.sort(Comparator.comparing(Jogo::getData).reversed());
        } else {
            resultado.sort(Comparator.comparing(Jogo::getData));
        }

        if (resultado.size() > limite) {
            return new ArrayList<>(resultado.subList(0, limite));
        }

        return resultado;
    }

    private static boolean textoIgual(String valor, String esperado) {
        return valorOuTraco(valor).equalsIgnoreCase(valorOuTraco(esperado));
    }

    private static String valorOuTraco(String valor) {
        return valor == null || valor.trim().isEmpty() ? "-" : valor;
    }
}
