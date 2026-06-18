package GrupoEeleminatoria;

import Models.Campeonato;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CampeonatoRepositorio {

    private static final String PASTA_DADOS = "dados";
    private static final String FICHEIRO = PASTA_DADOS + File.separator + "campeonatos.dat";

    private static List<Campeonato> campeonatos = carregarDoFicheiro();

    public static List<Campeonato> listar() {
        return campeonatos;
    }

    public static void adicionar(Campeonato campeonato) {
        campeonatos.add(campeonato);
        salvar();
    }

    public static void remover(Campeonato campeonato) {
        campeonatos.remove(campeonato);
        salvar();
    }

    public static Campeonato procurarPorNome(String nome) {
        for (Campeonato campeonato : campeonatos) {
            if (campeonato.getNome().equalsIgnoreCase(nome)) {
                return campeonato;
            }
        }

        return null;
    }

    public static boolean existeCampeonatoComNome(String nome) {
        return procurarPorNome(nome) != null;
    }

    public static void salvar() {
        try {
            File pasta = new File(PASTA_DADOS);

            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            ObjectOutputStream output = new ObjectOutputStream(
                    new FileOutputStream(FICHEIRO)
            );

            output.writeObject(campeonatos);
            output.close();

        } catch (IOException e) {
            System.out.println("Erro ao guardar campeonatos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Campeonato> carregarDoFicheiro() {
        File ficheiro = new File(FICHEIRO);

        if (!ficheiro.exists()) {
            return new ArrayList<>();
        }

        try {
            ObjectInputStream input = new ObjectInputStream(
                    new FileInputStream(FICHEIRO)
            );

            List<Campeonato> lista = (List<Campeonato>) input.readObject();
            input.close();

            return lista;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar campeonatos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}