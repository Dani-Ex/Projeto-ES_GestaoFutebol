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
