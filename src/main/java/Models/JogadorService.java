package Models;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class JogadorService {

    private static final Path FICHEIRO_JOGADORES = Paths.get("data", "jogadores.tsv");
    private static final List<Jogador> jogadores = new ArrayList<>();

    static {
        carregarJogadores();
    }

    public List<Jogador> listarJogadores() {
        return Collections.unmodifiableList(jogadores);
    }

    public List<Jogador> listarPorEquipa(String equipa, String campeonato) {
        List<Jogador> resultado = new ArrayList<>();
        String equipaNormalizada = normalizar(equipa);
        String campeonatoNormalizado = normalizar(campeonato);

        for (Jogador jogador : jogadores) {
            if (normalizar(jogador.getEquipa()).equals(equipaNormalizada)
                    && normalizar(jogador.getCampeonato()).equals(campeonatoNormalizado)) {
                resultado.add(jogador);
            }
        }

        return resultado;
    }

    public int contarJogadoresPorEquipa(String equipa, String campeonato) {
        return listarPorEquipa(equipa, campeonato).size();
    }

    public int somarGolosPorEquipa(String equipa, String campeonato) {
        int total = 0;

        for (Jogador jogador : listarPorEquipa(equipa, campeonato)) {
            total += jogador.getGolos();
        }

        return total;
    }

    public void atualizarGolos(Jogador jogador, int golos) {
        if (jogador == null) {
            throw new IllegalArgumentException("O jogador não pode ser nulo.");
        }

        if (golos < 0) {
            throw new IllegalArgumentException("O número de golos não pode ser negativo.");
        }

        jogador.setGolos(golos);
        guardarJogadores();
    }

    public void guardarJogadores() {
        try {
            Files.createDirectories(FICHEIRO_JOGADORES.getParent());

            List<String> linhas = new ArrayList<>();

            for (Jogador jogador : jogadores) {
                linhas.add(String.join("\t",
                        escapar(jogador.getNome()),
                        String.valueOf(jogador.getNumero()),
                        escapar(jogador.getPosicao()),
                        String.valueOf(jogador.getPeso()),
                        String.valueOf(jogador.getAltura()),
                        escapar(jogador.getPeDominante()),
                        jogador.getDataNascimento() == null ? "" : jogador.getDataNascimento().toString(),
                        escapar(jogador.getPaisOrigem()),
                        escapar(jogador.getCidadeNascimento()),
                        escapar(jogador.getEquipa()),
                        escapar(jogador.getCampeonato()),
                        escapar(jogador.getGrupo()),
                        escapar(jogador.getRanking()),
                        String.valueOf(jogador.getJogos()),
                        String.valueOf(jogador.getGolos()),
                        String.valueOf(jogador.getAssistencias()),
                        String.valueOf(jogador.getCartoes()),
                        String.valueOf(jogador.getMinutos()),
                        String.valueOf(jogador.getFinalizacao()),
                        String.valueOf(jogador.getPasseCriacao()),
                        String.valueOf(jogador.getDisciplina()),
                        String.valueOf(jogador.isAtivo())
                ));
            }

            Files.write(FICHEIRO_JOGADORES, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível guardar os dados dos jogadores.");
        }
    }

    private static void carregarJogadores() {
        if (!Files.exists(FICHEIRO_JOGADORES)) {
            return;
        }

        try {
            for (String linha : Files.readAllLines(FICHEIRO_JOGADORES, StandardCharsets.UTF_8)) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split("\t", -1);

                if (campos.length < 22) {
                    continue;
                }

                jogadores.add(new Jogador(
                        desescapar(campos[0]),
                        parseInt(campos[1]),
                        desescapar(campos[2]),
                        parseInt(campos[3]),
                        parseDouble(campos[4]),
                        desescapar(campos[5]),
                        parseData(campos[6]),
                        desescapar(campos[7]),
                        desescapar(campos[8]),
                        desescapar(campos[9]),
                        desescapar(campos[10]),
                        desescapar(campos[11]),
                        desescapar(campos[12]),
                        parseInt(campos[13]),
                        parseInt(campos[14]),
                        parseInt(campos[15]),
                        parseInt(campos[16]),
                        parseInt(campos[17]),
                        parseInt(campos[18]),
                        parseInt(campos[19]),
                        parseInt(campos[20]),
                        Boolean.parseBoolean(campos[21])
                ));
            }
        } catch (IOException e) {
            jogadores.clear();
        }
    }

    private static int parseInt(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static double parseDouble(String valor) {
        try {
            return Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static LocalDate parseData(String valor) {
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String normalizar(String valor) {
        return valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
    }

    private static String escapar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String desescapar(String valor) {
        StringBuilder resultado = new StringBuilder();
        boolean escape = false;

        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);

            if (escape) {
                if (c == 't') {
                    resultado.append('\t');
                } else if (c == 'n') {
                    resultado.append('\n');
                } else if (c == 'r') {
                    resultado.append('\r');
                } else {
                    resultado.append(c);
                }

                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else {
                resultado.append(c);
            }
        }

        if (escape) {
            resultado.append('\\');
        }

        return resultado.toString();
    }
}
