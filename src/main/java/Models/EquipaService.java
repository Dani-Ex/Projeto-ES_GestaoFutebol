package Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class EquipaService {

    private static final List<Equipa> equipas = new ArrayList<>();

    static {
        carregarEquipasDemo();
    }

    public List<Equipa> listarEquipas() {
        return Collections.unmodifiableList(equipas);
    }

    public List<Equipa> pesquisarEquipas(String termo) {
        String termoNormalizado = termo == null
                ? ""
                : termo.trim().toLowerCase(Locale.ROOT);

        if (termoNormalizado.isEmpty()) {
            return listarEquipas();
        }

        List<Equipa> resultado = new ArrayList<>();

        for (Equipa equipa : equipas) {
            if (contem(equipa.getNome(), termoNormalizado)
                    || contem(equipa.getGrupo(), termoNormalizado)
                    || contem(equipa.getEstadoTexto(), termoNormalizado)
                    || contem(equipa.getPais(), termoNormalizado)
                    || contem(equipa.getCidade(), termoNormalizado)) {
                resultado.add(equipa);
            }
        }

        return resultado;
    }

    public void adicionarEquipa(Equipa equipa) {
        if (equipa == null) {
            throw new IllegalArgumentException("A equipa não pode ser nula.");
        }

        if (equipa.getNome() == null || equipa.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da equipa é obrigatório.");
        }

        equipas.add(equipa);
    }

    public void adicionarJogador(Equipa equipa,
                                 Jogador jogador) {

        if (equipa.getJogadores().size() >= 23) {
            throw new IllegalArgumentException(
                    "A equipa já possui 23 jogadores");
        }

        equipa.getJogadores().add(jogador);
    }

    public void removerJogador(Equipa equipa,
                               Jogador jogador,
                               boolean campeonatoIniciado) {

        if (campeonatoIniciado) {
            throw new IllegalStateException(
                    "Não é possível remover jogador durante a competição");
        }

        equipa.getJogadores().remove(jogador);
    }

    private boolean contem(String valor, String termo) {
        return valor != null
                && valor.toLowerCase(Locale.ROOT).contains(termo);
    }

    private static void carregarEquipasDemo() {
        if (!equipas.isEmpty()) {
            return;
        }

        adicionarEquipaDemo("Portugal", "Lisboa", "Portugal", "Roberto Martinez", "Cristiano Ronaldo", "Grupo A", 34, true, 23);
        adicionarEquipaDemo("Espanha", "Madrid", "Espanha", "Luis de la Fuente", "Álvaro Morata", "Grupo A", 31, true, 23);
        adicionarEquipaDemo("Argentina", "Buenos Aires", "Argentina", "Lionel Scaloni", "Lionel Messi", "Grupo B", 28, true, 23);
        adicionarEquipaDemo("Inglaterra", "Londres", "Inglaterra", "Gareth Southgate", "Harry Kane", "Grupo B", 23, true, 23);
        adicionarEquipaDemo("França", "Paris", "França", "Didier Deschamps", "Kylian Mbappé", "Grupo C", 21, true, 23);
        adicionarEquipaDemo("Brasil", "Brasília", "Brasil", "Dorival Júnior", "Marquinhos", "Grupo C", 18, true, 23);
        adicionarEquipaDemo("Bélgica", "Bruxelas", "Bélgica", "Domenico Tedesco", "Kevin De Bruyne", "Grupo D", 16, true, 23);
        adicionarEquipaDemo("Alemanha", "Berlim", "Alemanha", "Julian Nagelsmann", "İlkay Gündoğan", "Grupo D", 10, false, 20);
        adicionarEquipaDemo("Japão", "Tóquio", "Japão", "Hajime Moriyasu", "Wataru Endo", "Grupo A", 8, false, 22);
        adicionarEquipaDemo("Uruguai", "Montevidéu", "Uruguai", "Marcelo Bielsa", "Federico Valverde", "Grupo B", 14, true, 23);
    }

    private static void adicionarEquipaDemo(
            String nome,
            String cidade,
            String pais,
            String treinador,
            String capitao,
            String grupo,
            int pontos,
            boolean ativa,
            int totalJogadores
    ) {
        Equipa equipa = new Equipa(nome, cidade, pais, treinador, capitao, grupo, pontos, ativa);
        equipa.setTotalJogadores(totalJogadores);

        equipas.add(equipa);
    }
}
