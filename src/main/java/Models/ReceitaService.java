package Models;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReceitaService {

    private static final Path FICHEIRO_RECEITAS = Paths.get("data", "receitas.tsv");

    public List<Receita> listarReceitas() {
        if (!Files.exists(FICHEIRO_RECEITAS)) {
            return Collections.emptyList();
        }

        List<Receita> receitas = new ArrayList<>();

        try {
            for (String linha : Files.readAllLines(FICHEIRO_RECEITAS, StandardCharsets.UTF_8)) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split("\t", -1);

                if (campos.length < 5) {
                    continue;
                }

                receitas.add(new Receita(
                        campos[0],
                        parseInt(campos[1]),
                        parseDouble(campos[2]),
                        parseDouble(campos[3]),
                        parseDouble(campos[4])
                ));
            }
        } catch (IOException e) {
            return Collections.emptyList();
        }

        return receitas;
    }

    public boolean existeReceitaParaJogo(String idJogo) {
        for (Receita receita : listarReceitas()) {
            if (receita.getIdJogo().equalsIgnoreCase(idJogo)) {
                return true;
            }
        }

        return false;
    }

    public void adicionarReceita(Receita receita) {
        if (receita == null) {
            throw new IllegalArgumentException("A receita n\u00E3o pode ser vazia.");
        }

        if (receita.getIdJogo() == null || receita.getIdJogo().trim().isEmpty()) {
            throw new IllegalArgumentException("Deve selecionar um jogo.");
        }

        if (existeReceitaParaJogo(receita.getIdJogo())) {
            throw new IllegalArgumentException("Esse jogo j\u00E1 tem uma receita registada.");
        }

        List<Receita> receitas = new ArrayList<>(listarReceitas());
        receitas.add(receita);
        guardarReceitas(receitas);
    }

    private void guardarReceitas(List<Receita> receitas) {
        try {
            Files.createDirectories(FICHEIRO_RECEITAS.getParent());

            List<String> linhas = new ArrayList<>();

            for (Receita receita : receitas) {
                linhas.add(String.join("\t",
                        receita.getIdJogo(),
                        String.valueOf(receita.getBilhetes()),
                        formatarNumero(receita.getBilheteira()),
                        formatarNumero(receita.getPatrocinio()),
                        formatarNumero(receita.getDireitosTv())
                ));
            }

            Files.write(FICHEIRO_RECEITAS, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("N\u00E3o foi poss\u00EDvel guardar os dados da receita.");
        }
    }

    private String formatarNumero(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }

        return String.valueOf(valor);
    }

    private int parseInt(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDouble(String valor) {
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
