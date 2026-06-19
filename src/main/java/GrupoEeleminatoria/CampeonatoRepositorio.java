package GrupoEeleminatoria;

import Models.Campeonato;
import Models.Estadio;

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
        if (nome == null) {
            return null;
        }

        for (Campeonato campeonato : campeonatos) {
            if (campeonato.getNome().equalsIgnoreCase(nome.trim())) {
                return campeonato;
            }
        }

        return null;
    }

    public static boolean existeCampeonatoComNome(String nome) {
        return procurarPorNome(nome) != null;
    }

    public static List<String> listarEquipasExistentes() {
        List<String> equipasExistentes = new ArrayList<>();

        for (Campeonato campeonato : campeonatos) {
            for (String equipa : campeonato.getEquipas()) {
                boolean jaExiste = false;

                for (String equipaExistente : equipasExistentes) {
                    if (equipaExistente.equalsIgnoreCase(equipa)) {
                        jaExiste = true;
                        break;
                    }
                }

                if (!jaExiste) {
                    equipasExistentes.add(equipa);
                }
            }
        }

        return equipasExistentes;
    }

    public static List<Estadio> listarEstadiosExistentes() {
        List<Estadio> estadiosExistentes = new ArrayList<>();

        for (Campeonato campeonato : campeonatos) {
            for (Estadio estadio : campeonato.getEstadios()) {
                boolean jaExiste = false;

                for (Estadio estadioExistente : estadiosExistentes) {
                    if (estadioExistente.getNome().equalsIgnoreCase(estadio.getNome())) {
                        jaExiste = true;
                        break;
                    }
                }

                if (!jaExiste) {
                    estadiosExistentes.add(estadio);
                }
            }
        }

        return estadiosExistentes;
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

    public static void limparTudo() {
        campeonatos.clear();
        salvar();
    }

    public static void apagarFicheiroDados() {
        campeonatos.clear();

        File ficheiro = new File(FICHEIRO);

        if (ficheiro.exists()) {
            ficheiro.delete();
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