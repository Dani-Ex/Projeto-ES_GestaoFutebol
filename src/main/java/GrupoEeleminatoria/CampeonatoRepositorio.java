package GrupoEeleminatoria;

import Models.Campeonato;
import Models.Estadio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CampeonatoRepositorio {

    private static final Path PASTA_DATA = Path.of("data");

    private static final Path FICHEIRO_CAMPEONATOS =
            PASTA_DATA.resolve("campeonatos.tsv");

    private static final Path FICHEIRO_EQUIPAS =
            PASTA_DATA.resolve("equipas.tsv");

    private static final String SEM_GRUPO = "Sem grupo";

    private static List<Campeonato> campeonatos = carregarDoTsv();

    public static List<Campeonato> listar() {
        return campeonatos;
    }

    public static List<String> listarNomesCampeonatosGuardados() {
        List<String> nomes = new ArrayList<>();

        for (Campeonato campeonato : campeonatos) {
            if (campeonato == null
                    || campeonato.getNome() == null
                    || campeonato.getNome().trim().isEmpty()) {
                continue;
            }

            nomes.add(campeonato.getNome().trim());
        }

        return nomes;
    }

    public static List<String> listarNomesCampeonatosIniciados() {
        List<String> nomes = new ArrayList<>();

        for (Campeonato campeonato : campeonatos) {
            if (campeonato == null || !campeonato.isIniciado()) {
                continue;
            }

            String nome = campeonato.getNome();

            if (nome != null && !nome.trim().isEmpty()) {
                nomes.add(nome.trim());
            }
        }

        return nomes;
    }

    public static List<String> listarNomesCampeonatosParaClassificacao() {
        List<String> nomes = listarNomesCampeonatosIniciados();

        if (nomes.isEmpty()) {
            return listarNomesCampeonatosGuardados();
        }

        return nomes;
    }

    public static void adicionar(Campeonato campeonato) {
        if (campeonato == null) {
            return;
        }

        campeonatos.add(campeonato);
        salvar();
    }

    public static void remover(Campeonato campeonato) {
        if (campeonato == null) {
            return;
        }

        campeonatos.remove(campeonato);
        salvar();
    }

    public static Campeonato procurarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
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

    /*
     * Atualiza as equipas e grupos do campeonato a partir de data/equipas.tsv.
     *
     * Colunas usadas no teu equipas.tsv:
     * 0 = nome da equipa
     * 5 = campeonato
     * 6 = grupo
     */
    public static void sincronizarEquipasDoTsv(Campeonato campeonato) {
        if (campeonato == null || !Files.exists(FICHEIRO_EQUIPAS)) {
            return;
        }

        campeonato.getEquipas().clear();

        Map<String, List<String>> grupos = new LinkedHashMap<>();

        try (BufferedReader reader = Files.newBufferedReader(
                FICHEIRO_EQUIPAS,
                StandardCharsets.UTF_8
        )) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split("\t", -1);

                if (dados.length < 7) {
                    continue;
                }

                String nomeEquipa = removerBom(dados[0]).trim();
                String nomeCampeonato = dados[5].trim();
                String grupo = dados[6].trim();

                if (!nomeCampeonato.equalsIgnoreCase(campeonato.getNome())) {
                    continue;
                }

                if (!nomeEquipa.isEmpty()
                        && !campeonato.existeEquipaComNome(nomeEquipa)) {
                    campeonato.getEquipas().add(nomeEquipa);
                }

                if (!grupo.isEmpty() && !grupo.equalsIgnoreCase(SEM_GRUPO)) {
                    grupos.computeIfAbsent(grupo, chave -> new ArrayList<>())
                            .add(nomeEquipa);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar equipas TSV: " + e.getMessage());
        }

        campeonato.setGrupos(grupos);
    }

    public static List<String> listarEquipasExistentes() {
        List<String> equipasExistentes = new ArrayList<>();

        for (Campeonato campeonato : campeonatos) {
            sincronizarEquipasDoTsv(campeonato);

            for (String equipa : campeonato.getEquipas()) {
                boolean jaExiste = false;

                for (String existente : equipasExistentes) {
                    if (existente.equalsIgnoreCase(equipa)) {
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

                for (Estadio existente : estadiosExistentes) {
                    if (existente.getNome().equalsIgnoreCase(estadio.getNome())) {
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
            Files.createDirectories(PASTA_DATA);

            atualizarGruposNoTsvEquipas();
            guardarCampeonatosNoTsv();

        } catch (IOException e) {
            System.out.println("Erro ao guardar campeonatos TSV: " + e.getMessage());
        }
    }

    /*
     * Atualiza apenas a coluna "Grupo" das equipas.
     * Não cria outro ficheiro de equipas.
     */
    private static void atualizarGruposNoTsvEquipas() throws IOException {
        if (!Files.exists(FICHEIRO_EQUIPAS)) {
            return;
        }

        List<String> linhasOriginais = Files.readAllLines(
                FICHEIRO_EQUIPAS,
                StandardCharsets.UTF_8
        );

        List<String> linhasAtualizadas = new ArrayList<>();

        for (String linha : linhasOriginais) {
            String[] dados = linha.split("\t", -1);

            if (dados.length < 7) {
                linhasAtualizadas.add(linha);
                continue;
            }

            String nomeEquipa = removerBom(dados[0]).trim();
            String nomeCampeonato = dados[5].trim();

            Campeonato campeonato = procurarPorNome(nomeCampeonato);

            if (campeonato != null) {
                dados[6] = obterGrupoDaEquipa(campeonato, nomeEquipa);
            }

            linhasAtualizadas.add(String.join("\t", dados));
        }

        Files.write(
                FICHEIRO_EQUIPAS,
                linhasAtualizadas,
                StandardCharsets.UTF_8
        );
    }

    private static String obterGrupoDaEquipa(
            Campeonato campeonato,
            String nomeEquipa
    ) {
        if (!campeonato.isGruposGerados()) {
            return SEM_GRUPO;
        }

        for (Map.Entry<String, List<String>> entrada
                : campeonato.getGrupos().entrySet()) {

            for (String equipa : entrada.getValue()) {
                if (equipa.equalsIgnoreCase(nomeEquipa)) {
                    return entrada.getKey();
                }
            }
        }

        return SEM_GRUPO;
    }

    private static void guardarCampeonatosNoTsv() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FICHEIRO_CAMPEONATOS,
                StandardCharsets.UTF_8
        )) {
            writer.write(
                    "nome\tnumeroEquipas\tnumeroEstadios\t"
                            + "dataInicio\tdataFimGrupos\t"
                            + "dataInicioEliminatoria\tdataFimCampeonato\t"
                            + "estado\tgruposGerados\tfaseGruposTerminada\t"
                            + "eliminatoriasGeradas\testadios\tequipasEliminatorias"
            );

            writer.newLine();

            for (Campeonato campeonato : campeonatos) {
                writer.write(
                        limparCampo(campeonato.getNome()) + "\t"
                                + campeonato.getNumeroEquipasNecessarias() + "\t"
                                + campeonato.getNumeroEstadiosNecessarios() + "\t"
                                + campeonato.getDataInicioCampeonato() + "\t"
                                + campeonato.getDataFimGrupos() + "\t"
                                + campeonato.getDataInicioEliminatoria() + "\t"
                                + campeonato.getDataFimCampeonato() + "\t"
                                + limparCampo(campeonato.getEstado()) + "\t"
                                + campeonato.isGruposGerados() + "\t"
                                + campeonato.isFaseGruposTerminada() + "\t"
                                + campeonato.isEliminatoriasGeradas() + "\t"
                                + serializarEstadios(campeonato.getEstadios()) + "\t"
                                + serializarEquipasEliminatorias(
                                campeonato.getEquipasEliminatorias()
                        )
                );

                writer.newLine();
            }
        }
    }

    private static List<Campeonato> carregarDoTsv() {
        List<Campeonato> lista = new ArrayList<>();

        if (!Files.exists(FICHEIRO_CAMPEONATOS)) {
            return lista;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                FICHEIRO_CAMPEONATOS,
                StandardCharsets.UTF_8
        )) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                linha = removerBom(linha);

                if (primeiraLinha) {
                    primeiraLinha = false;

                    if (linha.startsWith("nome\t")) {
                        continue;
                    }
                }

                String[] dados = linha.split("\t", -1);

                if (dados.length < 11) {
                    continue;
                }

                Campeonato campeonato = new Campeonato(
                        dados[0].trim(),
                        Integer.parseInt(dados[1]),
                        Integer.parseInt(dados[2]),
                        LocalDate.parse(dados[3]),
                        LocalDate.parse(dados[4]),
                        LocalDate.parse(dados[5]),
                        LocalDate.parse(dados[6])
                );

                campeonato.setEstado(dados[7]);
                campeonato.setGruposGerados(Boolean.parseBoolean(dados[8]));
                campeonato.setFaseGruposTerminada(Boolean.parseBoolean(dados[9]));
                campeonato.setEliminatoriasGeradas(Boolean.parseBoolean(dados[10]));

                if (dados.length >= 12) {
                    campeonato.getEstadios().addAll(
                            desserializarEstadios(dados[11])
                    );
                }

                if (dados.length >= 13) {
                    campeonato.setEquipasEliminatorias(
                            desserializarEquipasEliminatorias(dados[12])
                    );
                }

                sincronizarEquipasDoTsv(campeonato);

                lista.add(campeonato);
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar campeonatos TSV: " + e.getMessage());
        }

        return lista;
    }

    public static void limparTudo() {
        campeonatos.clear();
        salvar();
    }

    public static void apagarFicheiroDados() {
        campeonatos.clear();

        try {
            Files.deleteIfExists(FICHEIRO_CAMPEONATOS);
        } catch (IOException e) {
            System.out.println("Erro ao apagar campeonatos.tsv: " + e.getMessage());
        }
    }

    private static String serializarEstadios(List<Estadio> estadios) {
        List<String> lista = new ArrayList<>();

        for (Estadio estadio : estadios) {
            String linha = codificar(estadio.getNome()) + "|"
                    + codificar(estadio.getCidade()) + "|"
                    + codificar(estadio.getProprietario()) + "|"
                    + estadio.getLugaresNormal() + "|"
                    + estadio.getLugaresVip() + "|"
                    + estadio.getLugaresPremium();

            lista.add(linha);
        }

        return String.join(";", lista);
    }

    private static List<Estadio> desserializarEstadios(String texto) {
        List<Estadio> estadios = new ArrayList<>();

        if (texto == null || texto.trim().isEmpty()) {
            return estadios;
        }

        String[] linhas = texto.split(";");

        for (String linha : linhas) {
            String[] dados = linha.split("\\|", -1);

            if (dados.length != 6) {
                continue;
            }

            try {
                Estadio estadio = new Estadio(
                        descodificar(dados[0]),
                        descodificar(dados[1]),
                        descodificar(dados[2]),
                        Integer.parseInt(dados[3]),
                        Integer.parseInt(dados[4]),
                        Integer.parseInt(dados[5])
                );

                estadios.add(estadio);

            } catch (Exception e) {
                System.out.println("Erro ao carregar estádio TSV.");
            }
        }

        return estadios;
    }

    private static String serializarEquipasEliminatorias(List<String> equipas) {
        List<String> lista = new ArrayList<>();

        for (String equipa : equipas) {
            lista.add(codificar(equipa));
        }

        return String.join(";", lista);
    }

    private static List<String> desserializarEquipasEliminatorias(String texto) {
        List<String> equipas = new ArrayList<>();

        if (texto == null || texto.trim().isEmpty()) {
            return equipas;
        }

        String[] dados = texto.split(";");

        for (String dado : dados) {
            String equipa = descodificar(dado);

            if (!equipa.isBlank()) {
                equipas.add(equipa);
            }
        }

        return equipas;
    }

    private static String codificar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }

    private static String descodificar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }

        try {
            byte[] bytes = Base64.getUrlDecoder().decode(texto);
            return new String(bytes, StandardCharsets.UTF_8);

        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    private static String limparCampo(String texto) {
        if (texto == null) {
            return "";
        }

        return texto
                .replace("\t", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }

    private static String removerBom(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("\uFEFF", "");
    }
}
