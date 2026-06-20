package Models;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Gestão persistente da bilheteira.
 *
 * Regras:
 * - Jogos e estádios são sempre escolhidos entre os já existentes.
 * - A capacidade de cada zona é lida do estádio (TSV/repositório), nunca definida na bilheteira.
 * - A bilheteira apenas guarda os preços Normal, VIP e Premium por jogo.
 * - Compras, jogo, estádio e preços só podem ser alterados antes de o jogo começar.
 */
public class BilheteriaService {

    public static final String TIPO_NORMAL = "Normal";
    public static final String TIPO_VIP = "VIP";
    public static final String TIPO_PREMIUM = "Premium";

    private static final double PRECO_NORMAL_PADRAO = 12.50;
    private static final double PRECO_VIP_PADRAO = 25.00;
    private static final double PRECO_PREMIUM_PADRAO = 40.00;

    private static final Path FICHEIRO_BILHETES = Paths.get("data", "bilhetes.tsv");
    private static final Path FICHEIRO_PRECOS = Paths.get("data", "precos_bilhetes.tsv");
    private static final Path FICHEIRO_ESTADIOS = Paths.get("data", "estadios.tsv");
    private static final Path FICHEIRO_RECEITAS = Paths.get("data", "receitas.tsv");

    private final JogoService jogoService;

    public BilheteriaService() {
        jogoService = JogoService.getInstance();
    }

    public String getChaveJogo(Jogo jogo) {
        return chaveJogo(jogo);
    }

    /** Jogos que já têm preços configurados para venda. */
    public List<Jogo> listarJogosBilheteira() {
        Map<String, Precos> precos = carregarPrecos();
        List<Jogo> resultado = new ArrayList<>();

        for (Jogo jogo : jogoService.listarJogos()) {
            if (precos.containsKey(chaveJogo(jogo))) {
                resultado.add(jogo);
            }
        }

        return resultado;
    }

    public List<Jogo> listarJogosParaVenda() {
        List<Jogo> resultado = new ArrayList<>();
        for (Jogo jogo : listarJogosBilheteira()) {
            if (!jogoJaComecou(jogo)) {
                resultado.add(jogo);
            }
        }
        return resultado;
    }

    /** Jogos existentes que ainda não foram configurados na bilheteira. */
    public List<Jogo> listarJogosDisponiveisParaAdicionar() {
        Map<String, Precos> configurados = carregarPrecos();
        List<Jogo> resultado = new ArrayList<>();

        for (Jogo jogo : jogoService.listarJogos()) {
            if (!configurados.containsKey(chaveJogo(jogo)) && !jogoJaComecou(jogo)) {
                resultado.add(jogo);
            }
        }

        return resultado;
    }

    /** Nomes dos estádios que já existem no projeto. */
    public List<String> listarEstadiosExistentes() {
        List<String> nomes = new ArrayList<>();
        for (Estadio estadio : listarEstadiosComCapacidades()) {
            if (!vazio(estadio.getNome())) {
                nomes.add(estadio.getNome().trim());
            }
        }
        return nomes;
    }

    public List<Bilhete> listarBilhetes() {
        if (!Files.exists(FICHEIRO_BILHETES)) {
            return Collections.emptyList();
        }

        List<Bilhete> bilhetes = new ArrayList<>();

        try {
            for (String linha : Files.readAllLines(FICHEIRO_BILHETES, StandardCharsets.UTF_8)) {
                if (linha == null || linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split("\\t", -1);

                if (campos.length == 8) {
                    bilhetes.add(new Bilhete(
                            campos[0], campos[1], campos[2],
                            parseDouble(campos[3]), parseInt(campos[4]), parseDouble(campos[5]),
                            campos[6], parseDataHora(campos[7])
                    ));
                    continue;
                }

                if (campos.length == 9) {
                    bilhetes.add(new Bilhete(
                            campos[0], campos[1], campos[3],
                            parseDouble(campos[4]), parseInt(campos[5]), parseDouble(campos[6]),
                            campos[7], parseDataHora(campos[8])
                    ));
                    continue;
                }

                if (campos.length >= 10) {
                    bilhetes.add(new Bilhete(
                            campos[0], campos[1], campos[4],
                            parseDouble(campos[5]), parseInt(campos[6]), parseDouble(campos[7]),
                            campos[8], parseDataHora(campos[9])
                    ));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel ler os bilhetes guardados.", e);
        }

        return bilhetes;
    }

    public List<Bilhete> listarBilhetesDoJogo(String idJogo) {
        List<Bilhete> resultado = new ArrayList<>();
        for (Bilhete bilhete : listarBilhetes()) {
            if (bilhete.getIdJogo().equalsIgnoreCase(idJogo)) {
                resultado.add(bilhete);
            }
        }
        return resultado;
    }

    /** Preço configurado para uma zona num jogo específico. */
    public double getPreco(String idJogo, String tipo) {
        Precos precos = carregarPrecos().get(idJogo);
        if (precos == null) {
            return precoPadrao(tipo);
        }
        return precos.get(tipo);
    }

    /** Mantido apenas para compatibilidade com código antigo. */
    public double getPreco(String tipo) {
        return precoPadrao(tipo);
    }

    /** A capacidade vem do estádio associado ao jogo, não da bilheteira. */
    public int getCapacidadeTotal(String idJogo, String tipo) {
        Jogo jogo = procurarJogoPorChave(idJogo);
        if (jogo == null) {
            return 0;
        }

        Estadio estadio = procurarEstadio(jogo.getEstadio());
        if (estadio == null) {
            return 0;
        }

        return capacidadeDoEstadio(estadio, tipo);
    }

    public int getVendidos(String idJogo, String tipo) {
        int total = 0;
        for (Bilhete bilhete : listarBilhetes()) {
            if (bilhete.getIdJogo().equalsIgnoreCase(idJogo)
                    && bilhete.getTipo().equalsIgnoreCase(tipo)) {
                total += bilhete.getQuantidade();
            }
        }
        return total;
    }

    public int getDisponiveis(String idJogo, String tipo) {
        return Math.max(0, getCapacidadeTotal(idJogo, tipo) - getVendidos(idJogo, tipo));
    }

    /** Disponibilidade sem contar a compra que está a ser corrigida. */
    public int getDisponiveisParaEdicao(String idJogo, String tipo, String idTransacaoIgnorada) {
        int vendidos = 0;

        for (Bilhete bilhete : listarBilhetes()) {
            if (bilhete.getIdTransacao().equalsIgnoreCase(idTransacaoIgnorada)) {
                continue;
            }
            if (bilhete.getIdJogo().equalsIgnoreCase(idJogo)
                    && bilhete.getTipo().equalsIgnoreCase(tipo)) {
                vendidos += bilhete.getQuantidade();
            }
        }

        return Math.max(0, getCapacidadeTotal(idJogo, tipo) - vendidos);
    }

    /** Configura preços de um jogo existente, usando um estádio existente. */
    public void adicionarJogoABilheteira(Jogo jogo,
                                         String estadio,
                                         double precoNormal,
                                         double precoVip,
                                         double precoPremium) {
        if (jogo == null) {
            throw new IllegalArgumentException("Seleciona um jogo existente.");
        }
        String chaveJogo = chaveJogo(jogo);
        if (carregarPrecos().containsKey(chaveJogo)) {
            throw new IllegalArgumentException("Este jogo já está configurado na bilheteira.");
        }

        validarJogoAindaNaoComecou(jogo);
        validarEstadioExistente(estadio);
        validarPrecos(precoNormal, precoVip, precoPremium);

        atualizarEstadioDoJogo(jogo, estadio);
        guardarPrecos(chaveJogo, precoNormal, precoVip, precoPremium);
    }

    /** Altera jogo, estádio e preços antes do início do jogo. */
    public void atualizarJogoDaBilheteira(Jogo jogoOriginal,
                                          Jogo novoJogo,
                                          String estadio,
                                          double precoNormal,
                                          double precoVip,
                                          double precoPremium) {
        if (jogoOriginal == null || novoJogo == null) {
            throw new IllegalArgumentException("Seleciona um jogo válido.");
        }

        Map<String, Precos> precos = carregarPrecos();
        String chaveOriginal = chaveJogo(jogoOriginal);
        String chaveNovo = chaveJogo(novoJogo);

        if (!precos.containsKey(chaveOriginal)) {
            throw new IllegalArgumentException("O jogo não está configurado na bilheteira.");
        }

        validarJogoAindaNaoComecou(jogoOriginal);
        validarJogoAindaNaoComecou(novoJogo);
        Estadio estadioEscolhido = validarEstadioExistente(estadio);
        validarPrecos(precoNormal, precoVip, precoPremium);

        // Nunca permite trocar para um estádio com menos lugares do que os bilhetes já vendidos.
        validarCapacidadeDoEstadioAcimaDasVendas(chaveOriginal, estadioEscolhido);

        boolean mudouJogo = !chaveOriginal.equalsIgnoreCase(chaveNovo);
        if (mudouJogo && precos.containsKey(chaveNovo)) {
            throw new IllegalArgumentException("O jogo escolhido já está configurado na bilheteira.");
        }

        atualizarEstadioDoJogo(novoJogo, estadio);

        if (mudouJogo) {
            transferirComprasParaNovoJogo(chaveOriginal, chaveNovo);
            precos.remove(chaveOriginal);
            precos.put(chaveNovo, new Precos(precoNormal, precoVip, precoPremium));
        } else {
            precos.put(chaveOriginal, new Precos(precoNormal, precoVip, precoPremium));
        }

        guardarTodosPrecos(precos);
    }

    /** Remove somente a configuração da bilheteira. Nunca elimina jogo ou estádio. */
    public void removerJogoDaBilheteira(Jogo jogo) {
        if (jogo == null) {
            throw new IllegalArgumentException("Seleciona um jogo.");
        }
        validarJogoAindaNaoComecou(jogo);
        String chaveJogo = chaveJogo(jogo);

        if (temCompras(chaveJogo)) {
            throw new IllegalArgumentException(
                    "Não é possível eliminar este jogo da bilheteira porque já tem compras registadas."
            );
        }

        Map<String, Precos> precos = carregarPrecos();
        precos.remove(chaveJogo);
        guardarTodosPrecos(precos);
    }

    public Bilhete comprarBilhetes(
            Jogo jogo,
            String tipo,
            int quantidade,
            String metodoPagamento
    ) {
        if (jogo == null) {
            throw new IllegalArgumentException("Seleciona primeiro um jogo.");
        }
        String chaveJogo = chaveJogo(jogo);
        if (!carregarPrecos().containsKey(chaveJogo)) {
            throw new IllegalArgumentException("Este jogo ainda nao foi configurado na bilheteira.");
        }

        validarJogoAindaNaoComecou(jogo);
        validarTipo(tipo);

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser superior a zero.");
        }
        if (vazio(metodoPagamento)) {
            throw new IllegalArgumentException("Seleciona um metodo de pagamento.");
        }

        int disponiveis = getDisponiveis(chaveJogo, tipo);
        if (quantidade > disponiveis) {
            throw new IllegalArgumentException(
                    "So existem " + disponiveis + " bilhetes " + tipo + " disponiveis para este jogo."
            );
        }

        double precoUnitario = getPreco(chaveJogo, tipo);
        double total = precoUnitario * quantidade;

        Bilhete compra = new Bilhete(
                gerarIdTransacao(),
                chaveJogo,
                tipo,
                precoUnitario,
                quantidade,
                total,
                metodoPagamento,
                LocalDateTime.now()
        );

        List<Bilhete> bilhetes = new ArrayList<>(listarBilhetes());
        bilhetes.add(compra);
        guardarBilhetes(bilhetes);
        ajustarReceitaDoJogo(chaveJogo, quantidade, total);

        return compra;
    }
    /** Corrige uma compra antes de o jogo original e o novo jogo comecarem. */
    public void editarCompra(
            String idTransacao,
            Jogo novoJogo,
            String novoTipo,
            int novaQuantidade,
            String novoMetodoPagamento
    ) {
        if (vazio(idTransacao)) {
            throw new IllegalArgumentException("Compra invalida.");
        }
        if (novoJogo == null) {
            throw new IllegalArgumentException("Seleciona o jogo da compra.");
        }

        validarTipo(novoTipo);
        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser superior a zero.");
        }
        if (vazio(novoMetodoPagamento)) {
            throw new IllegalArgumentException("Seleciona um metodo de pagamento.");
        }

        List<Bilhete> bilhetes = new ArrayList<>(listarBilhetes());
        int indice = indiceCompra(bilhetes, idTransacao);
        if (indice < 0) {
            throw new IllegalArgumentException("A compra selecionada ja nao existe.");
        }

        Bilhete compraOriginal = bilhetes.get(indice);
        Jogo jogoOriginal = procurarJogoPorChave(compraOriginal.getIdJogo());
        if (jogoOriginal == null) {
            throw new IllegalArgumentException("O jogo original da compra nao existe.");
        }
        String chaveOriginal = compraOriginal.getIdJogo();
        String chaveNovo = chaveJogo(novoJogo);

        validarJogoAindaNaoComecou(jogoOriginal);
        validarJogoAindaNaoComecou(novoJogo);

        if (!carregarPrecos().containsKey(chaveNovo)) {
            throw new IllegalArgumentException("O novo jogo nao esta configurado na bilheteira.");
        }

        int disponiveis = getDisponiveisParaEdicao(
                chaveNovo, novoTipo, compraOriginal.getIdTransacao()
        );
        if (novaQuantidade > disponiveis) {
            throw new IllegalArgumentException(
                    "So existem " + disponiveis + " bilhetes " + novoTipo + " disponiveis apos esta alteracao."
            );
        }

        double novoPreco = getPreco(chaveNovo, novoTipo);
        double novoTotal = novoPreco * novaQuantidade;

        Bilhete compraAtualizada = new Bilhete(
                compraOriginal.getIdTransacao(),
                chaveNovo,
                novoTipo,
                novoPreco,
                novaQuantidade,
                novoTotal,
                novoMetodoPagamento,
                compraOriginal.getDataCompra()
        );

        bilhetes.set(indice, compraAtualizada);
        guardarBilhetes(bilhetes);

        ajustarReceitaDoJogo(chaveOriginal, -compraOriginal.getQuantidade(), -compraOriginal.getTotal());
        ajustarReceitaDoJogo(chaveNovo, novaQuantidade, novoTotal);
    }
    public void eliminarCompra(String idTransacao) {
        List<Bilhete> bilhetes = new ArrayList<>(listarBilhetes());
        int indice = indiceCompra(bilhetes, idTransacao);
        if (indice < 0) {
            throw new IllegalArgumentException("A compra selecionada já não existe.");
        }

        Bilhete compra = bilhetes.get(indice);
        Jogo jogo = procurarJogoPorChave(compra.getIdJogo());
        if (jogo == null) {
            throw new IllegalArgumentException("O jogo da compra não existe.");
        }

        validarJogoAindaNaoComecou(jogo);
        bilhetes.remove(indice);
        guardarBilhetes(bilhetes);
        ajustarReceitaDoJogo(compra.getIdJogo(), -compra.getQuantidade(), -compra.getTotal());
    }

    private void transferirComprasParaNovoJogo(String idOriginal, String idNovo) {
        List<Bilhete> bilhetes = new ArrayList<>(listarBilhetes());
        int quantidadeTransferida = 0;
        double totalTransferido = 0;
        boolean alterou = false;

        for (int i = 0; i < bilhetes.size(); i++) {
            Bilhete bilhete = bilhetes.get(i);
            if (bilhete.getIdJogo().equalsIgnoreCase(idOriginal)) {
                quantidadeTransferida += bilhete.getQuantidade();
                totalTransferido += bilhete.getTotal();

                bilhetes.set(i, new Bilhete(
                        bilhete.getIdTransacao(),
                        idNovo,
                        bilhete.getTipo(),
                        bilhete.getPrecoUnitario(),
                        bilhete.getQuantidade(),
                        bilhete.getTotal(),
                        bilhete.getMetodoPagamento(),
                        bilhete.getDataCompra()
                ));
                alterou = true;
            }
        }

        if (alterou) {
            guardarBilhetes(bilhetes);
            ajustarReceitaDoJogo(idOriginal, -quantidadeTransferida, -totalTransferido);
            ajustarReceitaDoJogo(idNovo, quantidadeTransferida, totalTransferido);
        }
    }

    private String chaveJogo(Jogo jogo) {
        if (jogo == null) {
            return "";
        }
        return limpar(jogo.getId()) + "::" + limpar(jogo.getCampeonato());
    }

    private Jogo procurarJogoPorChave(String chave) {
        if (vazio(chave)) {
            return null;
        }

        for (Jogo jogo : jogoService.listarJogos()) {
            if (chaveJogo(jogo).equalsIgnoreCase(chave)) {
                return jogo;
            }
        }

        // Compatibilidade com compras antigas guardadas apenas com o id do jogo.
        return jogoService.procurarPorId(chave);
    }

    private void atualizarEstadioDoJogo(Jogo jogo, String estadio) {
        if (jogo.getEstadio() != null && jogo.getEstadio().equalsIgnoreCase(estadio.trim())) {
            return;
        }

        jogoService.atualizarJogo(new Jogo(
                jogo.getId(), jogo.getData(), jogo.getHora(), jogo.getEquipaA(), jogo.getEquipaB(),
                estadio.trim(), jogo.getFaseGrupo(), jogo.getEstado(), jogo.getResultado(), jogo.getCampeonato()
        ));
    }

    private boolean temCompras(String idJogo) {
        for (Bilhete bilhete : listarBilhetes()) {
            if (bilhete.getIdJogo().equalsIgnoreCase(idJogo)) {
                return true;
            }
        }
        return false;
    }

    private int indiceCompra(List<Bilhete> bilhetes, String idTransacao) {
        for (int i = 0; i < bilhetes.size(); i++) {
            if (bilhetes.get(i).getIdTransacao().equalsIgnoreCase(idTransacao)) {
                return i;
            }
        }
        return -1;
    }

    private void validarJogoAindaNaoComecou(Jogo jogo) {
        if (jogoJaComecou(jogo)) {
            throw new IllegalArgumentException(
                    "Este jogo já começou, foi realizado ou foi cancelado. Não é possível alterar a bilheteira."
            );
        }
    }

    private boolean jogoJaComecou(Jogo jogo) {
        String estado = jogo.getEstado() == null ? "" : jogo.getEstado().trim();
        return estado.equalsIgnoreCase("Em curso")
                || estado.equalsIgnoreCase("Realizado")
                || estado.equalsIgnoreCase("Finalizado")
                || estado.equalsIgnoreCase("Cancelado");
    }

    private Estadio validarEstadioExistente(String nomeEstadio) {
        if (vazio(nomeEstadio)) {
            throw new IllegalArgumentException("Seleciona um estádio existente.");
        }

        Estadio estadio = procurarEstadio(nomeEstadio);
        if (estadio == null) {
            throw new IllegalArgumentException("O estádio escolhido não existe ou não tem capacidade guardada nos TSV.");
        }
        return estadio;
    }

    private void validarCapacidadeDoEstadioAcimaDasVendas(String idJogo, Estadio estadio) {
        validarCapacidadeDaZona(idJogo, TIPO_NORMAL, estadio.getLugaresNormal());
        validarCapacidadeDaZona(idJogo, TIPO_VIP, estadio.getLugaresVip());
        validarCapacidadeDaZona(idJogo, TIPO_PREMIUM, estadio.getLugaresPremium());
    }

    private void validarCapacidadeDaZona(String idJogo, String tipo, int capacidadeEstadio) {
        int vendidos = getVendidos(idJogo, tipo);
        if (capacidadeEstadio < vendidos) {
            throw new IllegalArgumentException(
                    "O estádio escolhido só tem " + capacidadeEstadio + " lugares " + tipo
                            + ", mas já existem " + vendidos + " bilhetes vendidos."
            );
        }
    }

    private void validarPrecos(double normal, double vip, double premium) {
        if (normal <= 0 || vip <= 0 || premium <= 0) {
            throw new IllegalArgumentException("Os preços Normal, VIP e Premium devem ser superiores a zero.");
        }
    }

    private void validarTipo(String tipo) {
        if (!TIPO_NORMAL.equals(tipo) && !TIPO_VIP.equals(tipo) && !TIPO_PREMIUM.equals(tipo)) {
            throw new IllegalArgumentException("Seleciona um tipo de bilhete válido.");
        }
    }

    private double precoPadrao(String tipo) {
        if (TIPO_VIP.equals(tipo)) return PRECO_VIP_PADRAO;
        if (TIPO_PREMIUM.equals(tipo)) return PRECO_PREMIUM_PADRAO;
        return PRECO_NORMAL_PADRAO;
    }

    private int capacidadeDoEstadio(Estadio estadio, String tipo) {
        if (TIPO_VIP.equals(tipo)) return estadio.getLugaresVip();
        if (TIPO_PREMIUM.equals(tipo)) return estadio.getLugaresPremium();
        return estadio.getLugaresNormal();
    }

    private Estadio procurarEstadio(String nome) {
        if (vazio(nome)) return null;
        for (Estadio estadio : listarEstadiosComCapacidades()) {
            if (estadio.getNome().equalsIgnoreCase(nome.trim())) {
                return estadio;
            }
        }
        return null;
    }

    /**
     * Carrega a capacidade diretamente do TSV dos estádios.
     *
     * Formato atual:
     * id | nome | cidade | proprietario | lugaresNormal | lugaresVip | lugaresPremium
     *
     * O TSV é lido depois do repositório para que os lugares guardados em
     * data/estadios.tsv sejam sempre os valores usados pela bilheteira.
     */
    private List<Estadio> listarEstadiosComCapacidades() {
        Map<String, Estadio> porNome = new LinkedHashMap<>();

        for (Estadio estadio : CampeonatoRepositorio.listarEstadiosExistentes()) {
            adicionarEstadio(porNome, estadio);
        }

        if (!Files.exists(FICHEIRO_ESTADIOS)) {
            return new ArrayList<>(porNome.values());
        }

        try {
            for (String linha : Files.readAllLines(FICHEIRO_ESTADIOS, StandardCharsets.UTF_8)) {
                if (linha == null || linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split("\\t", -1);

                // id | nome | cidade | proprietário | normal | vip | premium
                if (campos.length >= 7) {
                    if ("id".equalsIgnoreCase(campos[0].trim())) {
                        continue;
                    }

                    Estadio estadio = new Estadio(
                            campos[1].trim(),
                            campos[2].trim(),
                            campos[3].trim(),
                            parseInt(campos[4]),
                            parseInt(campos[5]),
                            parseInt(campos[6])
                    );

                    porNome.put(estadio.getNome().trim().toLowerCase(Locale.ROOT), estadio);
                    continue;
                }

                // Formato alternativo sem id:
                // nome | cidade | proprietário | normal | vip | premium
                if (campos.length >= 6) {
                    if ("nome".equalsIgnoreCase(campos[0].trim())) {
                        continue;
                    }

                    Estadio estadio = new Estadio(
                            campos[0].trim(),
                            campos[1].trim(),
                            campos[2].trim(),
                            parseInt(campos[3]),
                            parseInt(campos[4]),
                            parseInt(campos[5])
                    );

                    porNome.put(estadio.getNome().trim().toLowerCase(Locale.ROOT), estadio);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler os estádios guardados.", e);
        }

        return new ArrayList<>(porNome.values());
    }

    private void adicionarEstadio(Map<String, Estadio> porNome, Estadio estadio) {
        if (estadio == null || vazio(estadio.getNome())) return;
        porNome.putIfAbsent(estadio.getNome().trim().toLowerCase(Locale.ROOT), estadio);
    }

    private Map<String, Precos> carregarPrecos() {
        Map<String, Precos> precos = new LinkedHashMap<>();

        if (Files.exists(FICHEIRO_PRECOS)) {
            try {
                for (String linha : Files.readAllLines(FICHEIRO_PRECOS, StandardCharsets.UTF_8)) {
                    if (linha == null || linha.trim().isEmpty()) continue;
                    String[] campos = linha.split("\\t", -1);
                    if (campos.length < 4) continue;
                    precos.put(campos[0], new Precos(
                            parseDouble(campos[1]), parseDouble(campos[2]), parseDouble(campos[3])
                    ));
                }
            } catch (IOException e) {
                throw new IllegalStateException("Não foi possível ler os preços dos bilhetes.", e);
            }
        }

        return precos;
    }

    private void guardarPrecos(String idJogo, double normal, double vip, double premium) {
        Map<String, Precos> precos = carregarPrecos();
        precos.put(idJogo, new Precos(normal, vip, premium));
        guardarTodosPrecos(precos);
    }

    private void guardarTodosPrecos(Map<String, Precos> precos) {
        try {
            Files.createDirectories(FICHEIRO_PRECOS.getParent());
            List<String> linhas = new ArrayList<>();
            for (Map.Entry<String, Precos> entrada : precos.entrySet()) {
                Precos preco = entrada.getValue();
                linhas.add(entrada.getKey() + "\t"
                        + formatarNumero(preco.normal) + "\t"
                        + formatarNumero(preco.vip) + "\t"
                        + formatarNumero(preco.premium));
            }
            Files.write(FICHEIRO_PRECOS, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível guardar os preços dos bilhetes.", e);
        }
    }

    private void guardarBilhetes(List<Bilhete> bilhetes) {
        try {
            Files.createDirectories(FICHEIRO_BILHETES.getParent());
            List<String> linhas = new ArrayList<>();
            for (Bilhete bilhete : bilhetes) {
                linhas.add(String.join("\t",
                        limpar(bilhete.getIdTransacao()),
                        limpar(bilhete.getIdJogo()),
                        limpar(bilhete.getTipo()),
                        String.valueOf(bilhete.getPrecoUnitario()),
                        String.valueOf(bilhete.getQuantidade()),
                        String.valueOf(bilhete.getTotal()),
                        limpar(bilhete.getMetodoPagamento()),
                        bilhete.getDataCompra().toString()
                ));
            }
            Files.write(FICHEIRO_BILHETES, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível guardar a compra dos bilhetes.", e);
        }
    }

    private void ajustarReceitaDoJogo(String idJogo, int deltaBilhetes, double deltaBilheteira) {
        List<String> linhas = new ArrayList<>();

        try {
            if (Files.exists(FICHEIRO_RECEITAS)) {
                for (String linha : Files.readAllLines(FICHEIRO_RECEITAS, StandardCharsets.UTF_8)) {
                    if (linha == null || linha.trim().isEmpty()) continue;
                    String[] campos = linha.split("\\t", -1);

                    if (campos.length >= 5 && campos[0].equalsIgnoreCase(idJogo)) {
                        int bilhetesAtuais = parseInt(campos[1]);
                        double bilheteiraAtual = parseDouble(campos[2]);
                        double patrocinio = parseDouble(campos[3]);
                        double direitosTv = parseDouble(campos[4]);

                        linhas.add(idJogo + "\t"
                                + Math.max(0, bilhetesAtuais + deltaBilhetes) + "\t"
                                + formatarNumero(Math.max(0, bilheteiraAtual + deltaBilheteira)) + "\t"
                                + formatarNumero(patrocinio) + "\t"
                                + formatarNumero(direitosTv));
                    } else {
                        linhas.add(linha);
                    }
                }
            }

            Files.createDirectories(FICHEIRO_RECEITAS.getParent());
            Files.write(FICHEIRO_RECEITAS, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível atualizar a receita da bilheteira.", e);
        }
    }

    private String gerarIdTransacao() {
        return "BIL-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private int parseInt(String valor) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String valor) {
        try {
            return Double.parseDouble(valor.trim().replace(",", "."));
        } catch (Exception e) {
            return 0;
        }
    }

    private LocalDateTime parseDataHora(String valor) {
        try {
            return LocalDateTime.parse(valor);
        } catch (DateTimeParseException e) {
            return LocalDateTime.now();
        }
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String limpar(String valor) {
        if (valor == null) return "";
        return valor.replace("\t", " ").replace("\n", " ").replace("\r", " ");
    }

    private String formatarNumero(double valor) {
        if (valor == Math.rint(valor)) return String.valueOf((long) valor);
        return String.valueOf(valor);
    }

    private static class Precos {
        private final double normal;
        private final double vip;
        private final double premium;

        private Precos(double normal, double vip, double premium) {
            this.normal = normal;
            this.vip = vip;
            this.premium = premium;
        }

        private double get(String tipo) {
            if (TIPO_VIP.equals(tipo)) return vip;
            if (TIPO_PREMIUM.equals(tipo)) return premium;
            return normal;
        }
    }
}
