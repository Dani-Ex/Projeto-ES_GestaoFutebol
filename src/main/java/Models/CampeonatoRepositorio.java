package Models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CampeonatoRepositorio {

    private static final Path PASTA_DATA = Path.of("data");

    private static final Path FICHEIRO_CAMPEONATOS =
            PASTA_DATA.resolve("campeonatos.tsv");

    private static final Path FICHEIRO_EQUIPAS =
            PASTA_DATA.resolve("equipas.tsv");

    private static final Path FICHEIRO_JOGADORES =
            PASTA_DATA.resolve("jogadores.tsv");

    private static final Path FICHEIRO_JOGOS =
            PASTA_DATA.resolve("jogos.tsv");

    private static final Path FICHEIRO_ESTADIOS =
            PASTA_DATA.resolve("estadios.tsv");

    private static final Path FICHEIRO_ASSOCIACOES_ESTADIOS =
            PASTA_DATA.resolve("campeonato_estadios.tsv");

    private static final String SEM_GRUPO = "Sem grupo";

    private static final Map<String, Estadio> catalogoEstadios =
            new LinkedHashMap<>();

    private static final List<Campeonato> campeonatos = carregarDoTsv();

    private CampeonatoRepositorio() {
    }

    public static List<Campeonato> listar() {
        return campeonatos;
    }

    public static List<String> listarNomesCampeonatosGuardados() {
        List<String> nomes = new ArrayList<>();

        for (Campeonato campeonato : campeonatos) {
            if (campeonato == null) {
                continue;
            }

            if (campeonato.getNome() != null
                    && !campeonato.getNome().trim().isEmpty()) {

                nomes.add(campeonato.getNome().trim());
            }
        }

        nomes.sort(String.CASE_INSENSITIVE_ORDER);

        return nomes;
    }

    public static List<String> listarNomesCampeonatosEmPreparacaoComVagas() {
        List<String> nomes = new ArrayList<>();

        for (Campeonato campeonato : campeonatos) {
            if (campeonato == null || !campeonato.isEmConfiguracao()) {
                continue;
            }

            sincronizarEquipasDoTsv(campeonato);

            if (campeonato.getEquipas().size() >= campeonato.getNumeroEquipasNecessarias()) {
                continue;
            }

            String nome = campeonato.getNome();

            if (nome != null && !nome.trim().isEmpty()) {
                nomes.add(nome.trim());
            }
        }

        nomes.sort(String.CASE_INSENSITIVE_ORDER);

        return nomes;
    }

    public static boolean campeonatoPodeReceberEquipa(String nomeCampeonato) {
        Campeonato campeonato = procurarPorNome(nomeCampeonato);

        if (campeonato == null || !campeonato.isEmConfiguracao()) {
            return false;
        }

        sincronizarEquipasDoTsv(campeonato);
        return campeonato.getEquipas().size() < campeonato.getNumeroEquipasNecessarias();
    }

    public static boolean removerEquipaDoCampeonato(String nomeEquipa, String nomeCampeonato) {
        Campeonato campeonato = procurarPorNome(nomeCampeonato);

        if (campeonato == null || !campeonato.isEmConfiguracao()) {
            return false;
        }

        sincronizarEquipasDoTsv(campeonato);

        String nomeNormalizado = nomeEquipa == null ? "" : nomeEquipa.trim();
        boolean removida = campeonato.getEquipas().removeIf(
                equipa -> equipa.equalsIgnoreCase(nomeNormalizado)
        );

        for (List<String> equipasGrupo : campeonato.getGrupos().values()) {
            equipasGrupo.removeIf(
                    equipa -> equipa.equalsIgnoreCase(nomeNormalizado)
            );
        }

        return removida;
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

        nomes.sort(String.CASE_INSENSITIVE_ORDER);

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
        if (campeonato == null || existeCampeonatoComNome(campeonato.getNome())) {
            return;
        }

        campeonatos.add(campeonato);
        salvar();
    }

    /*
     * Um campeonato só pode ser eliminado enquanto ainda está em configuração.
     * Ao eliminar, as equipas e jogadores deixam de estar associados ao campeonato
     * e os jogos/agendamentos desse campeonato deixam de ser gravados.
     */
    public static boolean eliminarCampeonatoNaoIniciado(Campeonato campeonato) {
        if (campeonato == null
                || !campeonato.isEmConfiguracao()
                || campeonato.isGruposGerados()) {
            return false;
        }

        String nomeCampeonato = campeonato.getNome();

        if (!campeonatos.remove(campeonato)) {
            return false;
        }

        limparAssociacaoCampeonatoNoTsv(
                FICHEIRO_EQUIPAS,
                5,
                6,
                nomeCampeonato
        );

        limparAssociacaoCampeonatoNoTsv(
                FICHEIRO_JOGADORES,
                10,
                11,
                nomeCampeonato
        );

        salvar();
        return true;
    }

    /*
     * Mantém compatibilidade com chamadas antigas ao método remover().
     * A regra de segurança continua a ser aplicada.
     */
    public static void remover(Campeonato campeonato) {
        eliminarCampeonatoNaoIniciado(campeonato);
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

    public static boolean editarCampeonato(
            Campeonato campeonato,
            String novoNome,
            int numeroEquipas,
            int numeroEstadios,
            LocalDate dataInicio,
            LocalDate dataFimGrupos,
            LocalDate dataInicioEliminatoria,
            LocalDate dataFim
    ) {
        if (campeonato == null || !campeonato.isEmConfiguracao()
                || campeonato.isGruposGerados()) {
            return false;
        }

        if (novoNome == null || novoNome.trim().isEmpty()
                || numeroEquipas <= 0 || numeroEstadios <= 0
                || numeroEquipas % 4 != 0
                || dataInicio == null || dataFimGrupos == null
                || dataInicioEliminatoria == null || dataFim == null) {
            return false;
        }

        if (dataFimGrupos.isBefore(dataInicio)
                || dataInicioEliminatoria.isBefore(dataFimGrupos)
                || dataFim.isBefore(dataInicioEliminatoria)) {
            return false;
        }

        if (numeroEquipas < campeonato.getEquipas().size()
                || numeroEstadios < campeonato.getEstadios().size()) {
            return false;
        }

        String nomeLimpo = novoNome.trim();
        String nomeAntigo = campeonato.getNome();

        for (Campeonato existente : campeonatos) {
            if (existente != campeonato
                    && existente.getNome().equalsIgnoreCase(nomeLimpo)) {
                return false;
            }
        }

        campeonato.setNome(nomeLimpo);
        campeonato.setNumeroEquipasNecessarias(numeroEquipas);
        campeonato.setNumeroEstadiosNecessarios(numeroEstadios);
        campeonato.setDataInicioCampeonato(dataInicio);
        campeonato.setDataFimGrupos(dataFimGrupos);
        campeonato.setDataInicioEliminatoria(dataInicioEliminatoria);
        campeonato.setDataFimCampeonato(dataFim);

        if (!nomeAntigo.equalsIgnoreCase(nomeLimpo)) {
            atualizarNomeCampeonatoNoTsv(FICHEIRO_EQUIPAS, 5, nomeAntigo, nomeLimpo);
            atualizarNomeCampeonatoNoTsv(FICHEIRO_JOGADORES, 10, nomeAntigo, nomeLimpo);
            atualizarNomeCampeonatoNosJogos(nomeAntigo, nomeLimpo);
        }

        salvar();
        return true;
    }

    public static List<Estadio> listarEstadiosExistentes() {
        atualizarCatalogoComEstadiosDosCampeonatos();

        List<Estadio> resultado = new ArrayList<>();

        for (Estadio estadio : catalogoEstadios.values()) {
            resultado.add(copiarEstadio(estadio));
        }

        return resultado;
    }

    public static String motivoBloqueioEdicaoEstadio(String nomeEstadio) {
        if (nomeEstadio == null || nomeEstadio.trim().isEmpty()) {
            return "Seleciona um estádio válido.";
        }

        boolean encontrado = false;

        for (Campeonato campeonato : campeonatos) {
            if (campeonato.existeEstadioComNome(nomeEstadio)) {
                encontrado = true;

                if (!campeonato.isEmConfiguracao() || campeonato.isGruposGerados()) {
                    return "Não é possível editar este estádio porque está associado ao campeonato \""
                            + campeonato.getNome()
                            + "\", que já foi iniciado ou já tem grupos gerados.";
                }
            }
        }

        if (!encontrado && procurarEstadioNoCatalogo(nomeEstadio) == null) {
            return "Não foi possível encontrar o estádio selecionado.";
        }

        return "";
    }

    public static boolean editarEstadio(String nomeAtual, Estadio estadioAtualizado) {
        if (nomeAtual == null || estadioAtualizado == null
                || estadioAtualizado.getNome() == null
                || estadioAtualizado.getNome().trim().isEmpty()) {
            return false;
        }

        String bloqueio = motivoBloqueioEdicaoEstadio(nomeAtual);

        if (!bloqueio.isEmpty()) {
            return false;
        }

        String novoNome = estadioAtualizado.getNome().trim();
        String idEstadio = null;

        for (Map.Entry<String, Estadio> entrada : catalogoEstadios.entrySet()) {
            if (entrada.getValue().getNome().equalsIgnoreCase(nomeAtual.trim())) {
                idEstadio = entrada.getKey();
                break;
            }
        }

        if (idEstadio == null) {
            for (Campeonato campeonato : campeonatos) {
                for (Estadio estadio : campeonato.getEstadios()) {
                    if (estadio.getNome().equalsIgnoreCase(nomeAtual.trim())) {
                        idEstadio = obterOuCriarIdEstadio(estadio);
                        break;
                    }
                }
            }
        }

        if (idEstadio == null) {
            return false;
        }

        for (Map.Entry<String, Estadio> entrada : catalogoEstadios.entrySet()) {
            if (!entrada.getKey().equals(idEstadio)
                    && entrada.getValue().getNome().equalsIgnoreCase(novoNome)) {
                return false;
            }
        }

        Estadio copiaAtualizada = copiarEstadio(estadioAtualizado);
        catalogoEstadios.put(idEstadio, copiaAtualizada);

        for (Campeonato campeonato : campeonatos) {
            for (int i = 0; i < campeonato.getEstadios().size(); i++) {
                Estadio estadio = campeonato.getEstadios().get(i);

                if (estadio.getNome().equalsIgnoreCase(nomeAtual.trim())) {
                    campeonato.getEstadios().set(i, copiarEstadio(copiaAtualizada));
                }
            }
        }

        salvar();
        return true;
    }


    /*
     * Um estádio fica reservado desde que é associado a um campeonato e só volta
     * a poder ser associado a outro quando esse campeonato estiver finalizado.
     */
    public static String motivoBloqueioReservaEstadio(
            String nomeEstadio,
            Campeonato campeonatoDestino
    ) {
        if (nomeEstadio == null || nomeEstadio.trim().isEmpty()) {
            return "Seleciona um estádio válido.";
        }

        String nomeLimpo = nomeEstadio.trim();

        for (Campeonato campeonato : campeonatos) {
            if (campeonato == campeonatoDestino) {
                continue;
            }

            if (campeonato.existeEstadioComNome(nomeLimpo)
                    && !campeonato.isFinalizado()) {
                return "O estádio \"" + nomeLimpo
                        + "\" está reservado pelo campeonato \""
                        + campeonato.getNome()
                        + "\" e só ficará disponível quando esse campeonato terminar.";
            }
        }

        return "";
    }

    /*
     * A eliminação global do estádio só é permitida quando nenhum campeonato
     * associado foi iniciado, gerou grupos ou terminou.
     */
    public static String motivoBloqueioEliminacaoEstadio(String nomeEstadio) {
        if (nomeEstadio == null || nomeEstadio.trim().isEmpty()) {
            return "Seleciona um estádio válido.";
        }

        String nomeLimpo = nomeEstadio.trim();

        for (Campeonato campeonato : campeonatos) {
            if (campeonato.existeEstadioComNome(nomeLimpo)
                    && (!campeonato.isEmConfiguracao()
                    || campeonato.isGruposGerados())) {
                return "Não é possível eliminar o estádio \""
                        + nomeLimpo
                        + "\" porque está associado ao campeonato \""
                        + campeonato.getNome()
                        + "\", que já foi iniciado, tem grupos gerados ou foi finalizado.";
            }
        }

        return "";
    }

    /*
     * Elimina o estádio do catálogo e remove a associação dos campeonatos que
     * ainda se encontram em configuração.
     */
    public static boolean eliminarEstadio(String nomeEstadio) {
        if (nomeEstadio == null || nomeEstadio.trim().isEmpty()) {
            return false;
        }

        String bloqueio = motivoBloqueioEliminacaoEstadio(nomeEstadio);

        if (!bloqueio.isEmpty()) {
            return false;
        }

        String nomeLimpo = nomeEstadio.trim();
        boolean encontrado = false;

        for (Campeonato campeonato : campeonatos) {
            boolean removido = campeonato.getEstadios().removeIf(
                    estadio -> estadio.getNome().equalsIgnoreCase(nomeLimpo)
            );

            if (removido) {
                encontrado = true;
            }
        }

        String idParaRemover = null;

        for (Map.Entry<String, Estadio> entrada : catalogoEstadios.entrySet()) {
            if (entrada.getValue().getNome().equalsIgnoreCase(nomeLimpo)) {
                idParaRemover = entrada.getKey();
                encontrado = true;
                break;
            }
        }

        if (idParaRemover != null) {
            catalogoEstadios.remove(idParaRemover);
        }

        if (!encontrado) {
            return false;
        }

        salvar();
        return true;
    }

    public static List<String> listarEquipasExistentes() {
        List<String> equipas = new ArrayList<>();

        for (Campeonato campeonato : campeonatos) {
            sincronizarEquipasDoTsv(campeonato);

            for (String equipa : campeonato.getEquipas()) {
                boolean jaExiste = false;

                for (String existente : equipas) {
                    if (existente.equalsIgnoreCase(equipa)) {
                        jaExiste = true;
                        break;
                    }
                }

                if (!jaExiste) {
                    equipas.add(equipa);
                }
            }
        }

        return equipas;
    }

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
                String[] dados = removerBom(linha).split("\\t", -1);

                if (dados.length < 7) {
                    continue;
                }

                String nomeEquipa = dados[0].trim();
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
            System.out.println("Erro ao ler equipas.tsv: " + e.getMessage());
        }

        campeonato.setGrupos(grupos);
    }

    public static String proximoIdJogo() {
        int maior = 0;

        for (Campeonato campeonato : campeonatos) {
            for (Jogo jogo : campeonato.getJogos()) {
                String id = jogo.getId();

                if (id == null) {
                    continue;
                }

                String numero = id.replaceAll("[^0-9]", "");

                if (!numero.isEmpty()) {
                    try {
                        maior = Math.max(maior, Integer.parseInt(numero));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        return String.format("J%03d", maior + 1);
    }

    /*
     * Cria um jogo apenas quando as regras do campeonato o permitem.
     * Esta validação fica no repositório para impedir que outro ecrã
     * consiga contornar as regras de passagem direta ou fase fechada.
     */
    public static boolean adicionarJogo(Campeonato campeonato, Jogo jogo) {
        if (campeonato == null || jogo == null) {
            return false;
        }

        String bloqueio = motivoBloqueioCriacaoJogo(
                campeonato,
                jogo.getFaseGrupo(),
                jogo.getEquipaA(),
                jogo.getEquipaB()
        );

        if (!bloqueio.isEmpty()) {
            return false;
        }

        for (Campeonato existente : campeonatos) {
            if (existente == null || existente.getJogos() == null) {
                continue;
            }

            for (Jogo jogoExistente : existente.getJogos()) {
                if (jogoExistente != null
                        && jogoExistente.getId() != null
                        && jogo.getId() != null
                        && jogoExistente.getId().equalsIgnoreCase(jogo.getId())) {
                    return false;
                }
            }
        }

        campeonato.getJogos().add(jogo);
        salvar();
        return true;
    }

    public static String motivoBloqueioRegistoResultado(Jogo jogo) {
        if (jogo == null) {
            return "Seleciona um jogo válido.";
        }

        String estado = jogo.getEstado() == null ? "" : jogo.getEstado().trim();

        if (estado.equalsIgnoreCase("Realizado")
                || estado.equalsIgnoreCase("Finalizado")) {
            return "Este jogo já tem um resultado registado.";
        }

        if (!estado.equalsIgnoreCase("Agendado")) {
            return "Não é possível registar resultado num jogo com estado \""
                    + (estado.isEmpty() ? "-" : estado)
                    + "\".";
        }

        return "";
    }

    /*
     * Guarda o resultado do jogo. Quando o jogo for a Final e houver vencedor,
     * o campeonato passa automaticamente para o estado Finalizado.
     */
    public static boolean registarResultadoJogo(
            String idJogo,
            int golosEquipaA,
            int golosEquipaB
    ) {
        if (idJogo == null || idJogo.trim().isEmpty()
                || golosEquipaA < 0 || golosEquipaB < 0) {
            return false;
        }

        for (Campeonato campeonato : campeonatos) {
            if (campeonato == null || campeonato.getJogos() == null) {
                continue;
            }

            for (int i = 0; i < campeonato.getJogos().size(); i++) {
                Jogo jogo = campeonato.getJogos().get(i);

                if (jogo == null || jogo.getId() == null
                        || !jogo.getId().equalsIgnoreCase(idJogo.trim())) {
                    continue;
                }

                if (!motivoBloqueioRegistoResultado(jogo).isEmpty()) {
                    return false;
                }

                /*
                 * Em qualquer jogo eliminatório tem de existir vencedor.
                 * Introduz o resultado final após prolongamento ou penáltis.
                 */
                if (ehJogoEliminatorio(jogo) && golosEquipaA == golosEquipaB) {
                    return false;
                }

                Jogo atualizado = new Jogo(
                        jogo.getId(),
                        jogo.getData(),
                        jogo.getHora(),
                        jogo.getEquipaA(),
                        jogo.getEquipaB(),
                        jogo.getEstadio(),
                        jogo.getFaseGrupo(),
                        "Realizado",
                        golosEquipaA + "-" + golosEquipaB,
                        jogo.getCampeonato()
                );

                campeonato.getJogos().set(i, atualizado);

                /*
                 * Só a Final pode terminar o campeonato.
                 */
                if (ehFinalEliminatoria(atualizado)) {
                    finalizarCampeonatoPelaFinal(campeonato);
                }

                salvar();
                return true;
            }
        }

        return false;
    }

    /*
     * Devolve o nome do campeão quando a Final está realizada e tem vencedor.
     * Caso contrário devolve uma String vazia.
     */
    public static String obterCampeao(Campeonato campeonato) {
        if (campeonato == null || campeonato.getJogos() == null) {
            return "";
        }

        for (Jogo jogo : campeonato.getJogos()) {
            if (!ehFinalEliminatoria(jogo) || !jogoFoiRealizado(jogo)) {
                continue;
            }

            int[] resultado = lerResultado(jogo.getResultado());

            if (resultado == null || resultado[0] == resultado[1]) {
                return "";
            }

            return resultado[0] > resultado[1]
                    ? jogo.getEquipaA()
                    : jogo.getEquipaB();
        }

        return "";
    }

    /*
     * É chamado automaticamente depois de guardar o resultado da Final.
     * O Campeonato só muda para Finalizado quando a Final tiver vencedor.
     */
    public static boolean finalizarCampeonatoPelaFinal(Campeonato campeonato) {
        if (campeonato == null) {
            return false;
        }

        if (campeonato.isFinalizado()) {
            return true;
        }

        String campeao = obterCampeao(campeonato);

        if (campeao.isEmpty()) {
            return false;
        }

        /*
         * O próprio modelo garante que apenas um campeonato iniciado
         * pode ser finalizado.
         */
        return campeonato.finalizarCampeonato();
    }

    private static boolean ehJogoEliminatorio(Jogo jogo) {
        return jogo != null
                && obterTamanhoRondaEliminatoria(jogo.getFaseGrupo()) > 0;
    }

    private static boolean ehFinalEliminatoria(Jogo jogo) {
        return jogo != null
                && obterTamanhoRondaEliminatoria(jogo.getFaseGrupo()) == 2;
    }

    private static boolean jogoFoiRealizado(Jogo jogo) {
        if (jogo == null || jogo.getEstado() == null) {
            return false;
        }

        return jogo.getEstado().equalsIgnoreCase("Realizado")
                || jogo.getEstado().equalsIgnoreCase("Finalizado");
    }

    private static int[] lerResultado(String resultado) {
        if (resultado == null) {
            return null;
        }

        try {
            String[] partes = resultado.trim()
                    .replace(" ", "")
                    .split("-");

            if (partes.length != 2) {
                return null;
            }

            int golosA = Integer.parseInt(partes[0]);
            int golosB = Integer.parseInt(partes[1]);

            if (golosA < 0 || golosB < 0) {
                return null;
            }

            return new int[]{golosA, golosB};

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String motivoBloqueioEliminacaoJogo(Jogo jogo) {
        if (jogo == null) {
            return "Seleciona um jogo válido.";
        }

        String estado = jogo.getEstado() == null ? "" : jogo.getEstado().trim();

        if (!estado.equalsIgnoreCase("Agendado")) {
            return "Não é possível eliminar este jogo porque o seu estado é \""
                    + (estado.isEmpty() ? "-" : estado)
                    + "\".";
        }

        return "";
    }

    public static boolean eliminarJogoNaoRealizado(String idJogo) {
        if (idJogo == null || idJogo.trim().isEmpty()) {
            return false;
        }

        for (Campeonato campeonato : campeonatos) {
            if (campeonato == null || campeonato.getJogos() == null) {
                continue;
            }

            for (int i = 0; i < campeonato.getJogos().size(); i++) {
                Jogo jogo = campeonato.getJogos().get(i);

                if (jogo == null || jogo.getId() == null
                        || !jogo.getId().equalsIgnoreCase(idJogo.trim())) {
                    continue;
                }

                if (!motivoBloqueioEliminacaoJogo(jogo).isEmpty()) {
                    return false;
                }

                campeonato.getJogos().remove(i);
                salvar();
                return true;
            }
        }

        return false;
    }

    /*
     * Quando a fase de grupos é fechada, todos os jogos daquele grupo que ainda
     * estavam agendados passam para Encerrado. Não recebem resultado e deixam
     * de aparecer como próximos jogos.
     */
    public static int encerrarJogosDeGruposNaoRealizados(Campeonato campeonato) {
        if (campeonato == null
                || campeonato.getJogos() == null
                || campeonato.getGrupos() == null
                || campeonato.getGrupos().isEmpty()) {
            return 0;
        }

        int total = 0;

        for (int i = 0; i < campeonato.getJogos().size(); i++) {
            Jogo jogo = campeonato.getJogos().get(i);

            if (!ehJogoDaFaseDeGrupos(campeonato, jogo)
                    || !jogoEstaAgendado(jogo)) {
                continue;
            }

            Jogo encerrado = new Jogo(
                    jogo.getId(),
                    jogo.getData(),
                    jogo.getHora(),
                    jogo.getEquipaA(),
                    jogo.getEquipaB(),
                    jogo.getEstadio(),
                    jogo.getFaseGrupo(),
                    "Encerrado",
                    "-",
                    jogo.getCampeonato()
            );

            campeonato.getJogos().set(i, encerrado);
            total++;
        }

        if (total > 0) {
            salvar();
        }

        return total;
    }

    /*
     * As equipas no início da lista de classificadas são as que recebem a
     * passagem direta. Exemplo: 6 classificadas precisam de uma chave de 8,
     * por isso as 2 primeiras passam diretamente para as meias-finais.
     */
    public static List<String> obterEquipasComPassagemDireta(Campeonato campeonato) {
        List<String> diretas = new ArrayList<>();

        if (campeonato == null
                || campeonato.getEquipasEliminatorias() == null
                || campeonato.getEquipasEliminatorias().isEmpty()) {
            return diretas;
        }

        List<String> classificadas = new ArrayList<>(
                campeonato.getEquipasEliminatorias()
        );

        int tamanhoChave = proximaPotenciaDeDois(classificadas.size());
        int quantidadeDiretas = tamanhoChave - classificadas.size();

        for (int i = 0; i < quantidadeDiretas && i < classificadas.size(); i++) {
            String equipa = classificadas.get(i);

            if (equipa != null && !equipa.trim().isEmpty()) {
                diretas.add(equipa.trim());
            }
        }

        return diretas;
    }

    public static String motivoBloqueioCriacaoJogo(
            Campeonato campeonato,
            String fase,
            String equipaA,
            String equipaB
    ) {
        if (campeonato == null) {
            return "Seleciona um campeonato válido.";
        }

        if (ehFaseDeGrupos(campeonato, fase)
                && campeonato.isFaseGruposTerminada()) {
            return "A fase de grupos já terminou. Não podes criar mais jogos de grupos.";
        }

        int tamanhoRonda = obterTamanhoRondaEliminatoria(fase);

        if (tamanhoRonda <= 0) {
            return "";
        }

        List<String> diretas = obterEquipasComPassagemDireta(campeonato);

        if (diretas.isEmpty()) {
            return "";
        }

        int tamanhoPrimeiraRonda = proximaPotenciaDeDois(
                campeonato.getEquipasEliminatorias().size()
        );

        /*
         * A passagem direta bloqueia somente a primeira ronda. Com 6 equipas
         * classificadas, esta ronda é a chave de 8 / Quartos de Final.
         */
        if (tamanhoRonda != tamanhoPrimeiraRonda) {
            return "";
        }

        String equipaDireta = encontrarEquipaDireta(diretas, equipaA);

        if (equipaDireta == null) {
            equipaDireta = encontrarEquipaDireta(diretas, equipaB);
        }

        if (equipaDireta != null) {
            return "A equipa \"" + equipaDireta
                    + "\" tem passagem direta e só pode entrar na ronda seguinte.";
        }

        return "";
    }

    private static boolean ehJogoDaFaseDeGrupos(Campeonato campeonato, Jogo jogo) {
        return jogo != null && ehFaseDeGrupos(campeonato, jogo.getFaseGrupo());
    }

    private static boolean ehFaseDeGrupos(Campeonato campeonato, String fase) {
        if (campeonato == null
                || campeonato.getGrupos() == null
                || fase == null) {
            return false;
        }

        for (String nomeGrupo : campeonato.getGrupos().keySet()) {
            if (nomeGrupo != null && nomeGrupo.equalsIgnoreCase(fase.trim())) {
                return true;
            }
        }

        return false;
    }

    private static boolean jogoEstaAgendado(Jogo jogo) {
        return jogo != null
                && jogo.getEstado() != null
                && jogo.getEstado().trim().equalsIgnoreCase("Agendado");
    }

    private static String encontrarEquipaDireta(
            List<String> diretas,
            String equipa
    ) {
        if (equipa == null) {
            return null;
        }

        for (String direta : diretas) {
            if (direta.equalsIgnoreCase(equipa.trim())) {
                return direta;
            }
        }

        return null;
    }

    private static int obterTamanhoRondaEliminatoria(String fase) {
        if (fase == null || fase.trim().isEmpty()) {
            return -1;
        }

        String texto = fase.trim();

        if (texto.startsWith("ELIM:")) {
            String[] partes = texto.split(":", 3);

            if (partes.length >= 2) {
                try {
                    return Integer.parseInt(partes[1]);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        String normalizado = normalizarTexto(fase);

        if (normalizado.contains("dezasseis")
                || normalizado.contains("16 avos")
                || normalizado.contains("16-avos")) {
            return 32;
        }

        if (normalizado.contains("oitavo")) {
            return 16;
        }

        if (normalizado.contains("quarto")) {
            return 8;
        }

        if (normalizado.contains("meia") || normalizado.contains("semi")) {
            return 4;
        }

        if (normalizado.equals("final") || normalizado.endsWith(" final")) {
            return 2;
        }

        return -1;
    }

    private static int proximaPotenciaDeDois(int numero) {
        int potencia = 1;

        while (potencia < Math.max(2, numero)) {
            potencia *= 2;
        }

        return potencia;
    }

    private static String normalizarTexto(String texto) {
        return texto == null ? "" : texto
                .toLowerCase()
                .replace("á", "a")
                .replace("à", "a")
                .replace("ã", "a")
                .replace("â", "a")
                .replace("é", "e")
                .replace("ê", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ô", "o")
                .replace("õ", "o")
                .replace("ú", "u");
    }

    public static void salvar() {
        try {
            Files.createDirectories(PASTA_DATA);

            atualizarCatalogoComEstadiosDosCampeonatos();
            atualizarGruposNoTsvEquipas();
            guardarCampeonatos();
            guardarEstadios();
            guardarAssociacoesEstadios();
            guardarJogos();

        } catch (IOException e) {
            System.out.println("Erro ao guardar dados TSV: " + e.getMessage());
        }
    }

    private static List<Campeonato> carregarDoTsv() {
        List<Campeonato> lista = new ArrayList<>();

        try {
            Files.createDirectories(PASTA_DATA);
            carregarCampeonatos(lista);
            carregarCatalogoEstadios();

            for (Campeonato campeonato : lista) {
                sincronizarEquipasDoTsv(campeonato);
            }

            carregarAssociacoesEstadios(lista);
            carregarJogos(lista);

        } catch (IOException e) {
            System.out.println("Erro ao preparar pasta data: " + e.getMessage());
        }

        return lista;
    }

    private static void carregarCampeonatos(List<Campeonato> lista) {
        if (!Files.exists(FICHEIRO_CAMPEONATOS)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                FICHEIRO_CAMPEONATOS,
                StandardCharsets.UTF_8
        )) {
            String primeiraLinha = reader.readLine();

            if (primeiraLinha == null) {
                return;
            }

            primeiraLinha = removerBom(primeiraLinha);
            Map<String, Integer> colunas = criarMapaColunas(primeiraLinha);

            if (colunas.isEmpty()) {
                preencherColunasPadrao(colunas);
                carregarLinhaCampeonato(primeiraLinha, colunas, lista);
            }

            String linha;

            while ((linha = reader.readLine()) != null) {
                carregarLinhaCampeonato(removerBom(linha), colunas, lista);
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler campeonatos.tsv: " + e.getMessage());
        }
    }

    private static Map<String, Integer> criarMapaColunas(String linha) {
        Map<String, Integer> colunas = new HashMap<>();

        if (!linha.startsWith("nome\t")) {
            return colunas;
        }

        String[] cabecalhos = linha.split("\\t", -1);

        for (int i = 0; i < cabecalhos.length; i++) {
            colunas.put(cabecalhos[i].trim(), i);
        }

        return colunas;
    }

    private static void preencherColunasPadrao(Map<String, Integer> colunas) {
        colunas.put("nome", 0);
        colunas.put("numeroEquipas", 1);
        colunas.put("numeroEstadios", 2);
        colunas.put("dataInicio", 3);
        colunas.put("dataFimGrupos", 4);
        colunas.put("dataInicioEliminatoria", 5);
        colunas.put("dataFimCampeonato", 6);
        colunas.put("estado", 7);
        colunas.put("gruposGerados", 8);
        colunas.put("faseGruposTerminada", 9);
        colunas.put("eliminatoriasGeradas", 10);
        colunas.put("equipasEliminatorias", 11);
    }

    private static void carregarLinhaCampeonato(
            String linha,
            Map<String, Integer> colunas,
            List<Campeonato> lista
    ) {
        String[] dados = linha.split("\\t", -1);

        try {
            Campeonato campeonato = new Campeonato(
                    obterCampo(dados, colunas, "nome"),
                    Integer.parseInt(obterCampo(dados, colunas, "numeroEquipas")),
                    Integer.parseInt(obterCampo(dados, colunas, "numeroEstadios")),
                    LocalDate.parse(obterCampo(dados, colunas, "dataInicio")),
                    LocalDate.parse(obterCampo(dados, colunas, "dataFimGrupos")),
                    LocalDate.parse(obterCampo(dados, colunas, "dataInicioEliminatoria")),
                    LocalDate.parse(obterCampo(dados, colunas, "dataFimCampeonato"))
            );

            campeonato.setEstado(obterCampo(dados, colunas, "estado"));
            campeonato.setGruposGerados(Boolean.parseBoolean(
                    obterCampo(dados, colunas, "gruposGerados")
            ));
            campeonato.setFaseGruposTerminada(Boolean.parseBoolean(
                    obterCampo(dados, colunas, "faseGruposTerminada")
            ));
            campeonato.setEliminatoriasGeradas(Boolean.parseBoolean(
                    obterCampo(dados, colunas, "eliminatoriasGeradas")
            ));
            campeonato.setEquipasEliminatorias(
                    desserializarLista(obterCampo(dados, colunas, "equipasEliminatorias"))
            );

            lista.add(campeonato);

        } catch (Exception e) {
            System.out.println("Linha inválida em campeonatos.tsv.");
        }
    }

    private static String obterCampo(
            String[] dados,
            Map<String, Integer> colunas,
            String nomeColuna
    ) {
        Integer indice = colunas.get(nomeColuna);

        if (indice == null || indice < 0 || indice >= dados.length) {
            return "";
        }

        return limparCampo(dados[indice]);
    }

    private static void carregarCatalogoEstadios() {
        catalogoEstadios.clear();

        if (!Files.exists(FICHEIRO_ESTADIOS)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                FICHEIRO_ESTADIOS,
                StandardCharsets.UTF_8
        )) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                linha = removerBom(linha);

                if (primeiraLinha) {
                    primeiraLinha = false;

                    if (linha.startsWith("id\t")) {
                        continue;
                    }
                }

                String[] dados = linha.split("\\t", -1);

                if (dados.length < 7) {
                    continue;
                }

                try {
                    String id = limparCampo(dados[0]);

                    catalogoEstadios.put(id, new Estadio(
                            limparCampo(dados[1]),
                            limparCampo(dados[2]),
                            limparCampo(dados[3]),
                            Integer.parseInt(dados[4]),
                            Integer.parseInt(dados[5]),
                            Integer.parseInt(dados[6])
                    ));

                } catch (Exception e) {
                    System.out.println("Linha inválida em estadios.tsv.");
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler estadios.tsv: " + e.getMessage());
        }
    }

    private static void carregarAssociacoesEstadios(List<Campeonato> lista) {
        if (!Files.exists(FICHEIRO_ASSOCIACOES_ESTADIOS)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                FICHEIRO_ASSOCIACOES_ESTADIOS,
                StandardCharsets.UTF_8
        )) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                linha = removerBom(linha);

                if (primeiraLinha) {
                    primeiraLinha = false;

                    if (linha.startsWith("campeonato\t")) {
                        continue;
                    }
                }

                String[] dados = linha.split("\\t", -1);

                if (dados.length < 2) {
                    continue;
                }

                Campeonato campeonato = procurarNaLista(lista, dados[0]);
                Estadio estadio = catalogoEstadios.get(dados[1].trim());

                if (campeonato != null && estadio != null
                        && !campeonato.existeEstadioComNome(estadio.getNome())) {
                    campeonato.getEstadios().add(copiarEstadio(estadio));
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler campeonato_estadios.tsv: " + e.getMessage());
        }
    }

    private static void carregarJogos(List<Campeonato> lista) {
        if (!Files.exists(FICHEIRO_JOGOS)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                FICHEIRO_JOGOS,
                StandardCharsets.UTF_8
        )) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                linha = removerBom(linha);

                if (primeiraLinha) {
                    primeiraLinha = false;

                    if (linha.startsWith("id\t")) {
                        continue;
                    }
                }

                String[] dados = linha.split("\\t", -1);

                if (dados.length < 10) {
                    continue;
                }

                Campeonato campeonato = procurarNaLista(lista, dados[9]);

                if (campeonato == null || contemJogoComId(campeonato, dados[0])) {
                    continue;
                }

                campeonato.getJogos().add(new Jogo(
                        limparCampo(dados[0]),
                        limparCampo(dados[1]),
                        limparCampo(dados[2]),
                        limparCampo(dados[3]),
                        limparCampo(dados[4]),
                        limparCampo(dados[5]),
                        limparCampo(dados[6]),
                        limparCampo(dados[7]),
                        limparCampo(dados[8]),
                        limparCampo(dados[9])
                ));
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler jogos.tsv: " + e.getMessage());
        }
    }

    private static Campeonato procurarNaLista(List<Campeonato> lista, String nome) {
        for (Campeonato campeonato : lista) {
            if (campeonato.getNome().equalsIgnoreCase(limparCampo(nome))) {
                return campeonato;
            }
        }

        return null;
    }

    private static void guardarCampeonatos() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FICHEIRO_CAMPEONATOS,
                StandardCharsets.UTF_8
        )) {
            writer.write(
                    "nome\tnumeroEquipas\tnumeroEstadios\t"
                            + "dataInicio\tdataFimGrupos\t"
                            + "dataInicioEliminatoria\tdataFimCampeonato\t"
                            + "estado\tgruposGerados\tfaseGruposTerminada\t"
                            + "eliminatoriasGeradas\tequipasEliminatorias"
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
                                + serializarLista(campeonato.getEquipasEliminatorias())
                );
                writer.newLine();
            }
        }
    }

    private static void guardarEstadios() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FICHEIRO_ESTADIOS,
                StandardCharsets.UTF_8
        )) {
            writer.write(
                    "id\tnome\tcidade\tproprietario\t"
                            + "lugaresNormal\tlugaresVip\tlugaresPremium"
            );
            writer.newLine();

            for (Map.Entry<String, Estadio> entrada : catalogoEstadios.entrySet()) {
                Estadio estadio = entrada.getValue();

                writer.write(
                        entrada.getKey() + "\t"
                                + limparCampo(estadio.getNome()) + "\t"
                                + limparCampo(estadio.getCidade()) + "\t"
                                + limparCampo(estadio.getProprietario()) + "\t"
                                + estadio.getLugaresNormal() + "\t"
                                + estadio.getLugaresVip() + "\t"
                                + estadio.getLugaresPremium()
                );
                writer.newLine();
            }
        }
    }

    private static void guardarAssociacoesEstadios() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FICHEIRO_ASSOCIACOES_ESTADIOS,
                StandardCharsets.UTF_8
        )) {
            writer.write("campeonato\testadioId");
            writer.newLine();

            Set<String> associacoesGuardadas = new LinkedHashSet<>();

            for (Campeonato campeonato : campeonatos) {
                for (Estadio estadio : campeonato.getEstadios()) {
                    String idEstadio = obterOuCriarIdEstadio(estadio);
                    String chave = campeonato.getNome().toLowerCase() + "|" + idEstadio;

                    if (associacoesGuardadas.add(chave)) {
                        writer.write(limparCampo(campeonato.getNome()) + "\t" + idEstadio);
                        writer.newLine();
                    }
                }
            }
        }
    }

    private static void guardarJogos() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                FICHEIRO_JOGOS,
                StandardCharsets.UTF_8
        )) {
            for (Campeonato campeonato : campeonatos) {
                for (Jogo jogo : campeonato.getJogos()) {
                    writer.write(
                            limparCampo(jogo.getId()) + "\t"
                                    + limparCampo(jogo.getData()) + "\t"
                                    + limparCampo(jogo.getHora()) + "\t"
                                    + limparCampo(jogo.getEquipaA()) + "\t"
                                    + limparCampo(jogo.getEquipaB()) + "\t"
                                    + limparCampo(jogo.getEstadio()) + "\t"
                                    + limparCampo(jogo.getFaseGrupo()) + "\t"
                                    + limparCampo(jogo.getEstado()) + "\t"
                                    + limparCampo(jogo.getResultado()) + "\t"
                                    + limparCampo(campeonato.getNome())
                    );
                    writer.newLine();
                }
            }
        }
    }

    private static void atualizarCatalogoComEstadiosDosCampeonatos() {
        for (Campeonato campeonato : campeonatos) {
            for (Estadio estadio : campeonato.getEstadios()) {
                String id = obterOuCriarIdEstadio(estadio);
                catalogoEstadios.put(id, copiarEstadio(estadio));
            }
        }
    }

    private static Estadio procurarEstadioNoCatalogo(String nome) {
        for (Estadio estadio : catalogoEstadios.values()) {
            if (estadio.getNome().equalsIgnoreCase(nome.trim())) {
                return estadio;
            }
        }

        return null;
    }

    private static String obterOuCriarIdEstadio(Estadio estadio) {
        for (Map.Entry<String, Estadio> entrada : catalogoEstadios.entrySet()) {
            if (entrada.getValue().getNome().equalsIgnoreCase(estadio.getNome())) {
                return entrada.getKey();
            }
        }

        String base = normalizarParaId(estadio.getNome());
        String id = "EST-" + base;
        int contador = 2;

        while (catalogoEstadios.containsKey(id)) {
            id = "EST-" + base + "-" + contador;
            contador++;
        }

        return id;
    }

    private static String normalizarParaId(String texto) {
        String normalizado = texto == null ? "ESTADIO" : texto.trim()
                .toUpperCase()
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");

        return normalizado.isEmpty() ? "ESTADIO" : normalizado;
    }

    private static void atualizarGruposNoTsvEquipas() throws IOException {
        if (!Files.exists(FICHEIRO_EQUIPAS)) {
            return;
        }

        List<String> linhas = Files.readAllLines(FICHEIRO_EQUIPAS, StandardCharsets.UTF_8);
        List<String> atualizadas = new ArrayList<>();

        for (String linha : linhas) {
            String[] dados = removerBom(linha).split("\\t", -1);

            if (dados.length < 7) {
                atualizadas.add(linha);
                continue;
            }

            Campeonato campeonato = procurarPorNome(dados[5].trim());

            if (campeonato != null) {
                dados[6] = obterGrupoDaEquipa(campeonato, dados[0].trim());
            }

            atualizadas.add(String.join("\t", dados));
        }

        Files.write(FICHEIRO_EQUIPAS, atualizadas, StandardCharsets.UTF_8);
    }


    /*
     * Ao eliminar um campeonato em configuração, as equipas e os jogadores
     * continuam no sistema, mas deixam de estar associados ao campeonato removido.
     */
    private static void limparAssociacaoCampeonatoNoTsv(
            Path ficheiro,
            int indiceCampeonato,
            int indiceGrupo,
            String nomeCampeonato
    ) {
        if (!Files.exists(ficheiro) || nomeCampeonato == null) {
            return;
        }

        try {
            List<String> linhas = Files.readAllLines(
                    ficheiro,
                    StandardCharsets.UTF_8
            );

            List<String> atualizadas = new ArrayList<>();

            for (String linha : linhas) {
                String[] dados = removerBom(linha).split("\\t", -1);

                if (dados.length > indiceCampeonato
                        && dados[indiceCampeonato].trim()
                        .equalsIgnoreCase(nomeCampeonato)) {

                    dados[indiceCampeonato] = "";

                    if (dados.length > indiceGrupo) {
                        dados[indiceGrupo] = SEM_GRUPO;
                    }

                    atualizadas.add(String.join("\t", dados));
                } else {
                    atualizadas.add(linha);
                }
            }

            Files.write(ficheiro, atualizadas, StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.out.println(
                    "Erro ao remover associação de campeonato: "
                            + e.getMessage()
            );
        }
    }

    private static void atualizarNomeCampeonatoNoTsv(
            Path ficheiro,
            int indiceCampeonato,
            String nomeAntigo,
            String nomeNovo
    ) {
        if (!Files.exists(ficheiro)) {
            return;
        }

        try {
            List<String> linhas = Files.readAllLines(ficheiro, StandardCharsets.UTF_8);
            List<String> atualizadas = new ArrayList<>();

            for (String linha : linhas) {
                String[] dados = removerBom(linha).split("\\t", -1);

                if (dados.length > indiceCampeonato
                        && dados[indiceCampeonato].trim().equalsIgnoreCase(nomeAntigo)) {
                    dados[indiceCampeonato] = nomeNovo;
                    atualizadas.add(String.join("\t", dados));
                } else {
                    atualizadas.add(linha);
                }
            }

            Files.write(ficheiro, atualizadas, StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.out.println("Erro ao atualizar nome de campeonato: " + e.getMessage());
        }
    }

    private static void atualizarNomeCampeonatoNosJogos(
            String nomeAntigo,
            String nomeNovo
    ) {
        for (Campeonato campeonato : campeonatos) {
            for (int i = 0; i < campeonato.getJogos().size(); i++) {
                Jogo jogo = campeonato.getJogos().get(i);

                if (jogo.getCampeonato().equalsIgnoreCase(nomeAntigo)) {
                    campeonato.getJogos().set(i, copiarJogoComCampeonato(jogo, nomeNovo));
                }
            }
        }
    }

    private static String obterGrupoDaEquipa(Campeonato campeonato, String nomeEquipa) {
        if (!campeonato.isGruposGerados()) {
            return SEM_GRUPO;
        }

        for (Map.Entry<String, List<String>> entrada : campeonato.getGrupos().entrySet()) {
            for (String equipa : entrada.getValue()) {
                if (equipa.equalsIgnoreCase(nomeEquipa)) {
                    return entrada.getKey();
                }
            }
        }

        return SEM_GRUPO;
    }

    private static boolean contemJogoComId(Campeonato campeonato, String id) {
        for (Jogo jogo : campeonato.getJogos()) {
            if (jogo.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }

        return false;
    }

    private static Jogo copiarJogoComCampeonato(Jogo jogo, String campeonato) {
        return new Jogo(
                jogo.getId(),
                jogo.getData(),
                jogo.getHora(),
                jogo.getEquipaA(),
                jogo.getEquipaB(),
                jogo.getEstadio(),
                jogo.getFaseGrupo(),
                jogo.getEstado(),
                jogo.getResultado(),
                campeonato
        );
    }

    public static void limparTudo() {
        campeonatos.clear();
        catalogoEstadios.clear();
        salvar();
    }

    public static void apagarFicheiroDados() {
        campeonatos.clear();
        catalogoEstadios.clear();

        try {
            Files.deleteIfExists(FICHEIRO_CAMPEONATOS);
            Files.deleteIfExists(FICHEIRO_ESTADIOS);
            Files.deleteIfExists(FICHEIRO_ASSOCIACOES_ESTADIOS);
            Files.deleteIfExists(FICHEIRO_JOGOS);
        } catch (IOException e) {
            System.out.println("Erro ao apagar ficheiros TSV: " + e.getMessage());
        }
    }

    private static Estadio copiarEstadio(Estadio estadio) {
        return new Estadio(
                estadio.getNome(),
                estadio.getCidade(),
                estadio.getProprietario(),
                estadio.getLugaresNormal(),
                estadio.getLugaresVip(),
                estadio.getLugaresPremium()
        );
    }

    private static String serializarLista(List<String> lista) {
        List<String> limpa = new ArrayList<>();

        for (String valor : lista) {
            limpa.add(limparCampo(valor).replace(";", ","));
        }

        return String.join(";", limpa);
    }

    private static List<String> desserializarLista(String texto) {
        List<String> lista = new ArrayList<>();

        if (texto == null || texto.trim().isEmpty()) {
            return lista;
        }

        for (String valor : texto.split(";")) {
            if (!valor.trim().isEmpty()) {
                lista.add(valor.trim());
            }
        }

        return lista;
    }

    private static String limparCampo(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("\t", " ")
                .replace("\n", " ")
                .replace("\r", " ")
                .trim();
    }

    private static String removerBom(String texto) {
        return texto == null ? "" : texto.replace("\uFEFF", "");
    }
}
