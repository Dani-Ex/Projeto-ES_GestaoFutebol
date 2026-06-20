package Models.Equipas;

import Models.Campeonatos.Campeonato;
import Models.Campeonatos.CampeonatoRepositorio;
import Models.Jogadores.JogadorService;
import Models.Utils.TextUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EquipaService {

    private static final Path FICHEIRO_EQUIPAS = Paths.get("data", "equipas.tsv");
    private static final EquipaService INSTANCE = new EquipaService();
    private final Path ficheiroEquipas;
    private final List<Equipa> equipas = new ArrayList<>();
    private final boolean validarCampeonatosGuardados;

    private EquipaService() {
        this(FICHEIRO_EQUIPAS);
    }

    EquipaService(Path ficheiroEquipas) {
        this.ficheiroEquipas = ficheiroEquipas;
        this.validarCampeonatosGuardados = FICHEIRO_EQUIPAS.equals(ficheiroEquipas);
        carregarEquipas();
    }

    public static EquipaService getInstance() {
        return INSTANCE;
    }

    public List<Equipa> listarEquipas() {
        return Collections.unmodifiableList(equipas);
    }

    public void sincronizarEstatisticasComJogadores() {
        atualizarEstatisticasComJogadores();
        guardarEquipas();
    }

    public List<String> listarCampeonatos() {
        List<String> campeonatos = new ArrayList<>();

        for (Equipa equipa : equipas) {
            String campeonato = equipa.getCampeonato();

            if (campeonato != null
                    && !campeonato.trim().isEmpty()
                    && !campeonatos.contains(campeonato)) {
                campeonatos.add(campeonato);
            }
        }

        if (campeonatos.isEmpty()) {
            campeonatos.add("Campeonato Principal");
        }

        return campeonatos;
    }

    public List<Equipa> pesquisarEquipas(String termo) {
        String termoNormalizado = TextUtils.normalizar(termo);

        if (termoNormalizado.isEmpty()) {
            return listarEquipas();
        }

        List<Equipa> resultado = new ArrayList<>();

        for (Equipa equipa : equipas) {
            if (contem(equipa.getNome(), termoNormalizado)
                    || contem(equipa.getCampeonato(), termoNormalizado)
                    || contem(equipa.getGrupo(), termoNormalizado)
                    || contem(equipa.getEstadoTexto(), termoNormalizado)
                    || contem(equipa.getPais(), termoNormalizado)
                    || contem(equipa.getCidade(), termoNormalizado)) {
                resultado.add(equipa);
            }
        }

        return resultado;
    }

    public void adicionarEquipa(Equipa equipa) {
        if (equipa == null) {
            throw new IllegalArgumentException("A equipa não pode ser nula.");
        }

        if (equipa.getNome() == null || equipa.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da equipa é obrigatório.");
        }

        if (validarCampeonatosGuardados) {
            validarCampeonatoPodeReceberEquipa(equipa.getCampeonato());
        }

        if (equipaExisteNoCampeonato(equipa.getNome(), equipa.getCampeonato())) {
            throw new IllegalArgumentException(
                    "Essa equipa não pode ser registrada outra vez no mesmo campeonato."
            );
        }

        equipa.setAtiva(equipa.getTotalJogadores() == 23);
        equipas.add(equipa);
        guardarEquipas();
    }

    public void removerEquipa(Equipa equipa) {
        if (equipa == null) {
            throw new IllegalArgumentException("Seleciona uma equipa para remover.");
        }

        if (validarCampeonatosGuardados) {
            Campeonato campeonato = CampeonatoRepositorio.procurarPorNome(equipa.getCampeonato());

            if (campeonato == null) {
                throw new IllegalArgumentException("O campeonato associado a esta equipa jÃ¡ nÃ£o existe.");
            }

            if (!campeonato.isEmConfiguracao()) {
                throw new IllegalArgumentException(
                        "A equipa sÃ³ pode ser apagada antes de o campeonato comeÃ§ar."
                );
            }

            CampeonatoRepositorio.removerEquipaDoCampeonato(equipa.getNome(), equipa.getCampeonato());
        }

        boolean removida = equipas.remove(equipa);

        if (!removida) {
            String nomeNormalizado = normalizar(equipa.getNome());
            String campeonatoNormalizado = normalizar(equipa.getCampeonato());

            removida = equipas.removeIf(existente ->
                    normalizar(existente.getNome()).equals(nomeNormalizado)
                            && normalizar(existente.getCampeonato()).equals(campeonatoNormalizado)
            );
        }

        if (!removida) {
            throw new IllegalArgumentException("A equipa selecionada jÃ¡ nÃ£o existe.");
        }

        guardarEquipas();
    }

    public boolean equipaExisteNoCampeonato(String nome, String campeonato) {
        String nomeNormalizado = normalizar(nome);
        String campeonatoNormalizado = normalizar(campeonato);

        for (Equipa equipa : equipas) {
            if (normalizar(equipa.getNome()).equals(nomeNormalizado)
                    && normalizar(equipa.getCampeonato()).equals(campeonatoNormalizado)) {
                return true;
            }
        }

        return false;
    }

    public boolean outraEquipaExisteNoCampeonato(Equipa equipaAtual, String nome, String campeonato) {
        String nomeNormalizado = normalizar(nome);
        String campeonatoNormalizado = normalizar(campeonato);

        for (Equipa equipa : equipas) {
            if (equipa == equipaAtual) {
                continue;
            }

            if (normalizar(equipa.getNome()).equals(nomeNormalizado)
                    && normalizar(equipa.getCampeonato()).equals(campeonatoNormalizado)) {
                return true;
            }
        }

        return false;
    }

    public void guardarAlteracoes() {
        guardarEquipas();
    }

    public void validarCampeonatoPodeReceberEquipa(String campeonato) {
        if (CampeonatoRepositorio.procurarPorNome(campeonato) == null) {
            throw new IllegalArgumentException(
                    "A equipa tem de estar associada a um campeonato existente e guardado."
            );
        }

        if (!CampeonatoRepositorio.campeonatoPodeReceberEquipa(campeonato)) {
            throw new IllegalArgumentException(
                    "Escolhe um campeonato em preparaÃ§Ã£o e com vagas disponÃ­veis."
            );
        }
    }

    public void atualizarEstatisticasDaEquipa(Equipa equipa) {
        if (equipa == null) {
            return;
        }

        JogadorService jogadorService = JogadorService.getInstance();
        int totalJogadores = jogadorService.contarJogadoresPorEquipa(
                equipa.getNome(),
                equipa.getCampeonato()
        );

        equipa.setTotalJogadores(totalJogadores);
        equipa.setGolos(jogadorService.somarGolosPorEquipa(
                equipa.getNome(),
                equipa.getCampeonato()
        ));
        equipa.setAtiva(totalJogadores == 23);
        guardarEquipas();
    }

    private boolean contem(String valor, String termo) {
        return valor != null
                && TextUtils.normalizar(valor).contains(termo);
    }

    private String normalizar(String valor) {
        return TextUtils.normalizar(valor);
    }

    private void carregarEquipas() {
        if (Files.exists(ficheiroEquipas)) {
            carregarEquipasGuardadas();
            atualizarEstatisticasComJogadores();
        }
    }

    private void carregarEquipasGuardadas() {
        try {
            for (String linha : Files.readAllLines(ficheiroEquipas, StandardCharsets.UTF_8)) {
                if (linha.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linha.split("\t", -1);

                if (campos.length < 10) {
                    continue;
                }

                boolean formatoComGolos = campos.length >= 12;

                Equipa equipa = new Equipa(
                        desescapar(campos[0]),
                        desescapar(campos[1]),
                        desescapar(campos[2]),
                        desescapar(campos[3]),
                        desescapar(campos[4]),
                        desescapar(campos[5]),
                        desescapar(campos[6]),
                        parseInt(campos[7]),
                        Boolean.parseBoolean(formatoComGolos ? campos[10] : campos[9])
                );

                if (formatoComGolos) {
                    equipa.setGolos(parseInt(campos[8]));
                    equipa.setTotalJogadores(parseInt(campos[9]));
                    equipa.setRanking(desescapar(campos[11]));
                } else {
                    equipa.setTotalJogadores(parseInt(campos[8]));
                    if (campos.length > 10) {
                        equipa.setRanking(desescapar(campos[10]));
                    }
                }

                equipas.add(equipa);
            }
        } catch (IOException e) {
            equipas.clear();
        }
    }

    private void guardarEquipas() {
        try {
            Files.createDirectories(ficheiroEquipas.getParent());

            List<String> linhas = new ArrayList<>();

            for (Equipa equipa : equipas) {
                equipa.setAtiva(equipa.getTotalJogadores() == 23);

                linhas.add(String.join("\t",
                        escapar(equipa.getNome()),
                        escapar(equipa.getCidade()),
                        escapar(equipa.getPais()),
                        escapar(equipa.getTreinador()),
                        escapar(equipa.getCapitao()),
                        escapar(equipa.getCampeonato()),
                        escapar(equipa.getGrupo()),
                        String.valueOf(equipa.getPontos()),
                        String.valueOf(equipa.getGolos()),
                        String.valueOf(equipa.getTotalJogadores()),
                        String.valueOf(equipa.isAtiva()),
                        escapar(equipa.getRanking())
                ));
            }

            Files.write(ficheiroEquipas, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível guardar os dados das equipas.");
        }
    }

    private static int parseInt(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void atualizarEstatisticasComJogadores() {
        JogadorService jogadorService = JogadorService.getInstance();

        for (Equipa equipa : equipas) {
            int totalJogadores = jogadorService.contarJogadoresPorEquipa(
                    equipa.getNome(),
                    equipa.getCampeonato()
            );

            equipa.setTotalJogadores(totalJogadores);
            equipa.setGolos(jogadorService.somarGolosPorEquipa(
                    equipa.getNome(),
                    equipa.getCampeonato()
            ));
            equipa.setAtiva(totalJogadores == 23);
        }
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

        return TextUtils.limparCaracteresInvisiveis(resultado.toString());
    }
}
