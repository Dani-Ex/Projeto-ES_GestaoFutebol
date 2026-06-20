package Models.Campeonatos;

import Models.Estadios.Estadio;
import Models.Jogos.Jogo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalendarioJogosService {

    public static ArrayList<Jogo> gerarJogosFaseGrupos(Campeonato campeonato) {
        validarDados(campeonato);

        ArrayList<Jogo> jogos = new ArrayList<>();

        LocalDate dataAtual = campeonato.getDataInicioCampeonato();
        LocalDate dataFim = campeonato.getDataFimGrupos();

        int indiceEstadio = 0;
        int numeroJogo = 1;

        for (Map.Entry<String, List<String>> entrada : campeonato.getGrupos().entrySet()) {
            String nomeGrupo = entrada.getKey();
            List<String> equipas = entrada.getValue();

            for (int i = 0; i < equipas.size(); i++) {
                for (int j = i + 1; j < equipas.size(); j++) {

                    if (dataAtual.isAfter(dataFim)) {
                        throw new IllegalArgumentException(
                                "Não existem dias suficientes para calendarizar todos os jogos com os estádios disponíveis."
                        );
                    }

                    Estadio estadio = campeonato.getEstadios().get(indiceEstadio);

                    Jogo jogo = new Jogo(
                            "J" + String.format("%03d", numeroJogo),
                            dataAtual.toString(),
                            "18:00",
                            equipas.get(i),
                            equipas.get(j),
                            estadio.getNome(),
                            nomeGrupo,
                            "Agendado",
                            "-",
                            campeonato.getNome()
                    );

                    jogos.add(jogo);

                    numeroJogo++;
                    indiceEstadio++;

                    if (indiceEstadio >= campeonato.getEstadios().size()) {
                        indiceEstadio = 0;
                        dataAtual = dataAtual.plusDays(1);
                    }
                }
            }
        }

        return jogos;
    }

    private static void validarDados(Campeonato campeonato) {
        if (campeonato == null) {
            throw new IllegalArgumentException("Campeonato inválido.");
        }

        if (campeonato.getEstadios() == null || campeonato.getEstadios().isEmpty()) {
            throw new IllegalArgumentException("Não existem estádios associados ao campeonato.");
        }

        if (campeonato.getGrupos() == null || campeonato.getGrupos().isEmpty()) {
            throw new IllegalArgumentException("Não existem grupos gerados.");
        }

        if (campeonato.getDataInicioCampeonato() == null || campeonato.getDataFimGrupos() == null) {
            throw new IllegalArgumentException("As datas da fase de grupos não estão definidas.");
        }

        if (campeonato.getDataFimGrupos().isBefore(campeonato.getDataInicioCampeonato())) {
            throw new IllegalArgumentException("A data de fim dos grupos não pode ser antes do início do campeonato.");
        }
    }
}