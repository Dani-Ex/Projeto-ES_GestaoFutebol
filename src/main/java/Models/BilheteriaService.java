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
import java.util.Map;
import java.util.UUID;

public class BilheteriaService {

    public static final String TIPO_NORMAL = "Normal";
    public static final String TIPO_VIP = "VIP";
    public static final String TIPO_PREMIUM = "Premium";

    private static final int CAPACIDADE_NORMAL_PADRAO = 500;
    private static final int CAPACIDADE_VIP_PADRAO = 100;
    private static final int CAPACIDADE_PREMIUM_PADRAO = 50;

    private static final double PRECO_NORMAL = 12.50;
    private static final double PRECO_VIP = 25.00;
    private static final double PRECO_PREMIUM = 40.00;

    private static final Path FICHEIRO_BILHETES =
            Paths.get("data", "bilhetes.tsv");

    private static final Path FICHEIRO_CAPACIDADES =
            Paths.get("data", "capacidades_bilhetes.tsv");

    private static final Path FICHEIRO_RECEITAS =
            Paths.get("data", "receitas.tsv");

    private final JogoService jogoService;

    public BilheteriaService() {
        this.jogoService = JogoService.getInstance();
    }

    public List<Jogo> listarJogosParaVenda() {
        List<Jogo> resultado = new ArrayList<>();

        for (Jogo jogo : jogoService.listarJogos()) {
            if (!"Realizado".equalsIgnoreCase(jogo.getEstado())
                    && !"Cancelado".equalsIgnoreCase(jogo.getEstado())) {
                resultado.add(jogo);
            }
        }

        return resultado;
    }

    public List<Bilhete> listarBilhetes() {
        if (!Files.exists(FICHEIRO_BILHETES)) {
            return Collections.emptyList();
        }

        List<Bilhete> bilhetes = new ArrayList<>();

        try {
            for (String linha : Files.readAllLines(FICHEIRO_BILHETES, StandardCharsets.UTF_8)) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split("\\t", -1);

                if (campos.length == 8) {
                    bilhetes.add(new Bilhete(
                            campos[0],
                            campos[1],
                            campos[2],
                            parseDouble(campos[3]),
                            parseInt(campos[4]),
                            parseDouble(campos[5]),
                            campos[6],
                            parseDataHora(campos[7])
                    ));
                    continue;
                }

                if (campos.length >= 10) {
                    bilhetes.add(new Bilhete(
                            campos[0],
                            campos[1],
                            campos[4],
                            parseDouble(campos[5]),
                            parseInt(campos[6]),
                            parseDouble(campos[7]),
                            campos[8],
                            parseDataHora(campos[9])
                    ));
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível ler os bilhetes guardados.",
                    e
            );
        }

        return bilhetes;
    }

    public double getPreco(String tipo) {
        if (TIPO_VIP.equals(tipo)) {
            return PRECO_VIP;
        }

        if (TIPO_PREMIUM.equals(tipo)) {
            return PRECO_PREMIUM;
        }

        return PRECO_NORMAL;
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
        return Math.max(
                0,
                getCapacidade(idJogo, tipo) - getVendidos(idJogo, tipo)
        );
    }

    public Bilhete comprarBilhetes(Jogo jogo,
                                   String tipo,
                                   int quantidade,
                                   String metodoPagamento) {
        if (jogo == null) {
            throw new IllegalArgumentException("Seleciona primeiro um jogo.");
        }

        if ("Realizado".equalsIgnoreCase(jogo.getEstado())
                || "Cancelado".equalsIgnoreCase(jogo.getEstado())) {
            throw new IllegalArgumentException(
                    "Não é possível comprar bilhetes para este jogo."
            );
        }

        validarTipo(tipo);

        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser superior a zero."
            );
        }

        if (metodoPagamento == null || metodoPagamento.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Seleciona um método de pagamento."
            );
        }

        int disponiveis = getDisponiveis(jogo.getId(), tipo);

        if (quantidade > disponiveis) {
            throw new IllegalArgumentException(
                    "Só existem " + disponiveis
                            + " bilhetes " + tipo
                            + " disponíveis para este jogo."
            );
        }

        double precoUnitario = getPreco(tipo);
        double total = precoUnitario * quantidade;

        Bilhete compra = new Bilhete(
                gerarIdTransacao(),
                jogo.getId(),
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
        atualizarReceitaDoJogo(jogo.getId(), quantidade, total);

        return compra;
    }

    public void adicionarJogoDisponivel(String data,
                                        String hora,
                                        String equipaA,
                                        String equipaB,
                                        String estadio,
                                        String faseGrupo,
                                        String estado,
                                        String resultado,
                                        String campeonato,
                                        int lugaresNormal,
                                        int lugaresVip,
                                        int lugaresPremium) {
        validarDadosJogo(
                data,
                hora,
                equipaA,
                equipaB,
                estadio,
                campeonato,
                lugaresNormal,
                lugaresVip,
                lugaresPremium
        );

        String id = jogoService.gerarNovoId();

        Jogo jogo = new Jogo(
                id,
                data.trim(),
                hora.trim(),
                equipaA.trim(),
                equipaB.trim(),
                estadio.trim(),
                textoOuPadrao(faseGrupo, "Fase de grupos"),
                textoOuPadrao(estado, "Agendado"),
                textoOuPadrao(resultado, "-"),
                campeonato.trim()
        );

        jogoService.adicionarJogo(jogo);
        guardarCapacidades(id, lugaresNormal, lugaresVip, lugaresPremium);
    }

    public void atualizarJogoDisponivel(Jogo jogoOriginal,
                                        String data,
                                        String hora,
                                        String equipaA,
                                        String equipaB,
                                        String estadio,
                                        String faseGrupo,
                                        String estado,
                                        String resultado,
                                        String campeonato,
                                        int lugaresNormalDisponiveis,
                                        int lugaresVipDisponiveis,
                                        int lugaresPremiumDisponiveis) {
        if (jogoOriginal == null) {
            throw new IllegalArgumentException("Seleciona primeiro um jogo.");
        }

        validarDadosJogo(
                data,
                hora,
                equipaA,
                equipaB,
                estadio,
                campeonato,
                lugaresNormalDisponiveis,
                lugaresVipDisponiveis,
                lugaresPremiumDisponiveis
        );

        Jogo jogoAtualizado = new Jogo(
                jogoOriginal.getId(),
                data.trim(),
                hora.trim(),
                equipaA.trim(),
                equipaB.trim(),
                estadio.trim(),
                textoOuPadrao(faseGrupo, "Fase de grupos"),
                textoOuPadrao(estado, "Agendado"),
                textoOuPadrao(resultado, "-"),
                campeonato.trim()
        );

        jogoService.atualizarJogo(jogoAtualizado);

        int normalTotal = getVendidos(
                jogoOriginal.getId(),
                TIPO_NORMAL
        ) + lugaresNormalDisponiveis;

        int vipTotal = getVendidos(
                jogoOriginal.getId(),
                TIPO_VIP
        ) + lugaresVipDisponiveis;

        int premiumTotal = getVendidos(
                jogoOriginal.getId(),
                TIPO_PREMIUM
        ) + lugaresPremiumDisponiveis;

        guardarCapacidades(
                jogoOriginal.getId(),
                normalTotal,
                vipTotal,
                premiumTotal
        );
    }

    private void validarDadosJogo(String data,
                                  String hora,
                                  String equipaA,
                                  String equipaB,
                                  String estadio,
                                  String campeonato,
                                  int lugaresNormal,
                                  int lugaresVip,
                                  int lugaresPremium) {
        if (vazio(data)
                || vazio(hora)
                || vazio(equipaA)
                || vazio(equipaB)
                || vazio(estadio)
                || vazio(campeonato)) {
            throw new IllegalArgumentException(
                    "Preenche data, hora, equipas, estádio e campeonato."
            );
        }

        if (lugaresNormal < 0
                || lugaresVip < 0
                || lugaresPremium < 0) {
            throw new IllegalArgumentException(
                    "Os lugares disponíveis não podem ser negativos."
            );
        }
    }

    private void validarTipo(String tipo) {
        if (!TIPO_NORMAL.equals(tipo)
                && !TIPO_VIP.equals(tipo)
                && !TIPO_PREMIUM.equals(tipo)) {
            throw new IllegalArgumentException(
                    "Seleciona um tipo de bilhete válido."
            );
        }
    }

    private int getCapacidade(String idJogo, String tipo) {
        Capacidades capacidades = carregarCapacidades().get(idJogo);

        if (capacidades == null) {
            return capacidadePadrao(tipo);
        }

        if (TIPO_VIP.equals(tipo)) {
            return capacidades.vip;
        }

        if (TIPO_PREMIUM.equals(tipo)) {
            return capacidades.premium;
        }

        return capacidades.normal;
    }

    private int capacidadePadrao(String tipo) {
        if (TIPO_VIP.equals(tipo)) {
            return CAPACIDADE_VIP_PADRAO;
        }

        if (TIPO_PREMIUM.equals(tipo)) {
            return CAPACIDADE_PREMIUM_PADRAO;
        }

        return CAPACIDADE_NORMAL_PADRAO;
    }

    private Map<String, Capacidades> carregarCapacidades() {
        Map<String, Capacidades> capacidades = new LinkedHashMap<>();

        if (!Files.exists(FICHEIRO_CAPACIDADES)) {
            return capacidades;
        }

        try {
            for (String linha : Files.readAllLines(
                    FICHEIRO_CAPACIDADES,
                    StandardCharsets.UTF_8
            )) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split("\\t", -1);

                if (campos.length < 4) {
                    continue;
                }

                capacidades.put(
                        campos[0],
                        new Capacidades(
                                parseInt(campos[1]),
                                parseInt(campos[2]),
                                parseInt(campos[3])
                        )
                );
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível ler as capacidades dos jogos.",
                    e
            );
        }

        return capacidades;
    }

    private void guardarCapacidades(String idJogo,
                                    int normal,
                                    int vip,
                                    int premium) {
        Map<String, Capacidades> capacidades = carregarCapacidades();

        capacidades.put(
                idJogo,
                new Capacidades(normal, vip, premium)
        );

        try {
            Files.createDirectories(FICHEIRO_CAPACIDADES.getParent());

            List<String> linhas = new ArrayList<>();

            for (Map.Entry<String, Capacidades> entry : capacidades.entrySet()) {
                Capacidades capacidade = entry.getValue();

                linhas.add(
                        entry.getKey() + "\t"
                                + capacidade.normal + "\t"
                                + capacidade.vip + "\t"
                                + capacidade.premium
                );
            }

            Files.write(
                    FICHEIRO_CAPACIDADES,
                    linhas,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível guardar as capacidades dos jogos.",
                    e
            );
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

            Files.write(
                    FICHEIRO_BILHETES,
                    linhas,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível guardar a compra dos bilhetes.",
                    e
            );
        }
    }

    private void atualizarReceitaDoJogo(String idJogo,
                                        int quantidade,
                                        double total) {
        List<String> linhas = new ArrayList<>();
        boolean atualizada = false;

        try {
            if (Files.exists(FICHEIRO_RECEITAS)) {
                for (String linha : Files.readAllLines(
                        FICHEIRO_RECEITAS,
                        StandardCharsets.UTF_8
                )) {
                    if (linha.trim().isEmpty()) {
                        continue;
                    }

                    String[] campos = linha.split("\\t", -1);

                    if (campos.length >= 5
                            && campos[0].equalsIgnoreCase(idJogo)) {

                        int bilhetesAtuais = parseInt(campos[1]);
                        double bilheteiraAtual = parseDouble(campos[2]);
                        double patrocinio = parseDouble(campos[3]);
                        double direitosTv = parseDouble(campos[4]);

                        linhas.add(
                                idJogo + "\t"
                                        + (bilhetesAtuais + quantidade) + "\t"
                                        + formatarNumero(bilheteiraAtual + total) + "\t"
                                        + formatarNumero(patrocinio) + "\t"
                                        + formatarNumero(direitosTv)
                        );

                        atualizada = true;
                    } else {
                        linhas.add(linha);
                    }
                }
            }

            if (!atualizada) {
                linhas.add(
                        idJogo + "\t"
                                + quantidade + "\t"
                                + formatarNumero(total)
                                + "\t0\t0"
                );
            }

            Files.createDirectories(FICHEIRO_RECEITAS.getParent());

            Files.write(
                    FICHEIRO_RECEITAS,
                    linhas,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível atualizar a receita da bilheteira.",
                    e
            );
        }
    }

    private String gerarIdTransacao() {
        return "BIL-" + System.currentTimeMillis() + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 4)
                .toUpperCase();
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
            return Double.parseDouble(
                    valor.trim().replace(",", ".")
            );
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

    private String textoOuPadrao(String valor, String padrao) {
        return vazio(valor) ? padrao : valor.trim();
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String limpar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor
                .replace("\t", " ")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    private String formatarNumero(double valor) {
        if (valor == Math.rint(valor)) {
            return String.valueOf((long) valor);
        }

        return String.valueOf(valor);
    }

    private static class Capacidades {

        private final int normal;
        private final int vip;
        private final int premium;

        private Capacidades(int normal, int vip, int premium) {
            this.normal = normal;
            this.vip = vip;
            this.premium = premium;
        }
    }
}