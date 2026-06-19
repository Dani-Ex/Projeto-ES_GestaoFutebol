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
    private static final ReceitaService INSTANCE = new ReceitaService();
    private final Path ficheiroReceitas;
    private final List<Receita> receitas = new ArrayList<>();

    private ReceitaService() {
        this(FICHEIRO_RECEITAS);
    }

    ReceitaService(Path ficheiroReceitas) {
        this.ficheiroReceitas = ficheiroReceitas;
        carregarReceitas();
    }

    public static ReceitaService getInstance() {
        return INSTANCE;
    }

    public List<Receita> listarReceitas() {
        return Collections.unmodifiableList(receitas);
    }

    private void carregarReceitas() {
        if (!Files.exists(ficheiroReceitas)) {
            return;
        }

        try {
            for (String linha : Files.readAllLines(ficheiroReceitas, StandardCharsets.UTF_8)) {
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
            receitas.clear();
        }
    }

    public boolean existeReceitaParaJogo(String idJogo) {
        for (Receita receita : listarReceitas()) {
            if (receita.getIdJogo().equalsIgnoreCase(idJogo)) {
                return true;
            }
        }

        return false;
    }

    public Receita procurarPorJogo(String idJogo) {
        for (Receita receita : listarReceitas()) {
            if (receita.getIdJogo().equalsIgnoreCase(idJogo)) {
                return receita;
            }
        }

        return null;
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

        receitas.add(receita);
        guardarReceitas();
    }

    public void atualizarReceita(Receita receitaAtualizada) {
        if (receitaAtualizada == null) {
            throw new IllegalArgumentException("A receita n\u00E3o pode ser vazia.");
        }

        boolean encontrada = false;

        for (int i = 0; i < receitas.size(); i++) {
            Receita receita = receitas.get(i);

            if (receita.getIdJogo().equalsIgnoreCase(receitaAtualizada.getIdJogo())) {
                receitas.set(i, receitaAtualizada);
                encontrada = true;
                break;
            }
        }

        if (!encontrada) {
            throw new IllegalArgumentException("A receita selecionada j\u00E1 n\u00E3o existe.");
        }

        guardarReceitas();
    }

    public void removerReceita(String idJogo) {
        boolean removida = receitas.removeIf(receita -> receita.getIdJogo().equalsIgnoreCase(idJogo));

        if (!removida) {
            throw new IllegalArgumentException("A receita selecionada j\u00E1 n\u00E3o existe.");
        }

        guardarReceitas();
    }

    private void guardarReceitas() {
        try {
            Files.createDirectories(ficheiroReceitas.getParent());

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

            Files.write(ficheiroReceitas, linhas, StandardCharsets.UTF_8);
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
