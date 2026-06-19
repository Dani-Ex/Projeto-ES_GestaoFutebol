package Models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Campeonato implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ESTADO_CONFIGURACAO = "Em configuração";
    public static final String ESTADO_INICIADO = "Iniciado";
    public static final String ESTADO_FINALIZADO = "Finalizado";

    private String nome;

    private boolean gruposGerados;
    private boolean faseGruposTerminada;
    private boolean eliminatoriasGeradas;

    private List<String> equipas;
    private Map<String, List<String>> grupos;
    private List<String> equipasEliminatorias;

    private ArrayList<Estadio> estadios;
    private ArrayList<Jogo> jogos;

    private int numeroEquipasNecessarias;
    private int numeroEstadiosNecessarios;

    private LocalDate dataInicioCampeonato;
    private LocalDate dataFimGrupos;
    private LocalDate dataInicioEliminatoria;
    private LocalDate dataFimCampeonato;

    private String estado;

    public Campeonato(String nome) {
        this(
                nome,
                0,
                0,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(8),
                LocalDate.now().plusDays(14)
        );
    }

    public Campeonato(String nome, int numeroEquipasNecessarias, int numeroEstadiosNecessarios) {
        this(
                nome,
                numeroEquipasNecessarias,
                numeroEstadiosNecessarios,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                LocalDate.now().plusDays(8),
                LocalDate.now().plusDays(14)
        );
    }

    public Campeonato(
            String nome,
            int numeroEquipasNecessarias,
            int numeroEstadiosNecessarios,
            LocalDate dataInicioCampeonato,
            LocalDate dataFimGrupos,
            LocalDate dataInicioEliminatoria,
            LocalDate dataFimCampeonato
    ) {
        this.nome = nome;

        this.numeroEquipasNecessarias = numeroEquipasNecessarias;
        this.numeroEstadiosNecessarios = numeroEstadiosNecessarios;

        this.dataInicioCampeonato = dataInicioCampeonato;
        this.dataFimGrupos = dataFimGrupos;
        this.dataInicioEliminatoria = dataInicioEliminatoria;
        this.dataFimCampeonato = dataFimCampeonato;

        this.gruposGerados = false;
        this.faseGruposTerminada = false;
        this.eliminatoriasGeradas = false;

        this.equipas = new ArrayList<>();
        this.grupos = new LinkedHashMap<>();
        this.equipasEliminatorias = new ArrayList<>();

        this.estadios = new ArrayList<>();
        this.jogos = new ArrayList<>();

        this.estado = ESTADO_CONFIGURACAO;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (isEmConfiguracao()) {
            this.nome = nome;
        }
    }

    public boolean isGruposGerados() {
        return gruposGerados;
    }

    public void setGruposGerados(boolean gruposGerados) {
        this.gruposGerados = gruposGerados;
    }

    public boolean isFaseGruposTerminada() {
        return faseGruposTerminada;
    }

    public void setFaseGruposTerminada(boolean faseGruposTerminada) {
        this.faseGruposTerminada = faseGruposTerminada;
    }

    public boolean isEliminatoriasGeradas() {
        return eliminatoriasGeradas;
    }

    public void setEliminatoriasGeradas(boolean eliminatoriasGeradas) {
        this.eliminatoriasGeradas = eliminatoriasGeradas;
    }

    public List<String> getEquipas() {
        return equipas;
    }

    public boolean adicionarEquipa(String equipa) {
        if (!isEmConfiguracao()) {
            return false;
        }

        if (gruposGerados) {
            return false;
        }

        if (equipa == null || equipa.trim().isEmpty()) {
            return false;
        }

        if (equipas.size() >= numeroEquipasNecessarias) {
            return false;
        }

        if (existeEquipaComNome(equipa)) {
            return false;
        }

        equipas.add(equipa.trim());
        return true;
    }

    public boolean removerEquipa(String equipa) {
        if (!isEmConfiguracao()) {
            return false;
        }

        if (gruposGerados) {
            return false;
        }

        return equipas.remove(equipa);
    }

    public boolean existeEquipaComNome(String nome) {
        if (nome == null) {
            return false;
        }

        String nomeNormalizado = nome.trim();

        for (String equipa : equipas) {
            if (equipa.equalsIgnoreCase(nomeNormalizado)) {
                return true;
            }
        }

        return false;
    }

    public Map<String, List<String>> getGrupos() {
        return grupos;
    }

    public void setGrupos(Map<String, List<String>> grupos) {
        if (grupos == null) {
            this.grupos = new LinkedHashMap<>();
        } else {
            this.grupos = grupos;
        }
    }

    public List<String> getEquipasEliminatorias() {
        return equipasEliminatorias;
    }

    public void setEquipasEliminatorias(List<String> equipasEliminatorias) {
        if (equipasEliminatorias == null) {
            this.equipasEliminatorias = new ArrayList<>();
        } else {
            this.equipasEliminatorias = equipasEliminatorias;
        }
    }

    public ArrayList<Estadio> getEstadios() {
        return estadios;
    }

    public boolean adicionarEstadio(Estadio estadio) {
        if (!isEmConfiguracao()) {
            return false;
        }

        if (gruposGerados) {
            return false;
        }

        if (estadio == null) {
            return false;
        }

        if (estadios.size() >= numeroEstadiosNecessarios) {
            return false;
        }

        if (existeEstadioComNome(estadio.getNome())) {
            return false;
        }

        estadios.add(estadio);
        return true;
    }

    public boolean removerEstadio(Estadio estadio) {
        if (!isEmConfiguracao()) {
            return false;
        }

        if (gruposGerados) {
            return false;
        }

        return estadios.remove(estadio);
    }

    public boolean existeEstadioComNome(String nome) {
        if (nome == null) {
            return false;
        }

        String nomeNormalizado = nome.trim();

        for (Estadio estadio : estadios) {
            if (estadio.getNome().equalsIgnoreCase(nomeNormalizado)) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<Jogo> getJogos() {
        return jogos;
    }

    public void setJogos(ArrayList<Jogo> jogos) {
        if (jogos == null) {
            this.jogos = new ArrayList<>();
        } else {
            this.jogos = jogos;
        }
    }

    public int getNumeroEquipasNecessarias() {
        return numeroEquipasNecessarias;
    }

    public void setNumeroEquipasNecessarias(int numeroEquipasNecessarias) {
        this.numeroEquipasNecessarias = numeroEquipasNecessarias;
    }

    public int getNumeroEstadiosNecessarios() {
        return numeroEstadiosNecessarios;
    }

    public void setNumeroEstadiosNecessarios(int numeroEstadiosNecessarios) {
        this.numeroEstadiosNecessarios = numeroEstadiosNecessarios;
    }

    public LocalDate getDataInicioCampeonato() {
        return dataInicioCampeonato;
    }

    public void setDataInicioCampeonato(LocalDate dataInicioCampeonato) {
        this.dataInicioCampeonato = dataInicioCampeonato;
    }

    public LocalDate getDataFimGrupos() {
        return dataFimGrupos;
    }

    public void setDataFimGrupos(LocalDate dataFimGrupos) {
        this.dataFimGrupos = dataFimGrupos;
    }

    public LocalDate getDataInicioEliminatoria() {
        return dataInicioEliminatoria;
    }

    public void setDataInicioEliminatoria(LocalDate dataInicioEliminatoria) {
        this.dataInicioEliminatoria = dataInicioEliminatoria;
    }

    public LocalDate getDataFimCampeonato() {
        return dataFimCampeonato;
    }

    public void setDataFimCampeonato(LocalDate dataFimCampeonato) {
        this.dataFimCampeonato = dataFimCampeonato;
    }

    public boolean temEquipasSuficientes() {
        return equipas.size() >= numeroEquipasNecessarias;
    }

    public boolean temEstadiosSuficientes() {
        return estadios.size() >= numeroEstadiosNecessarios;
    }

    public boolean podeIniciarCampeonato() {
        return isEmConfiguracao()
                && temEquipasSuficientes()
                && temEstadiosSuficientes()
                && gruposGerados;
    }

    public String getMensagemBloqueioInicio() {
        if (!isEmConfiguracao()) {
            return "O campeonato já foi iniciado ou finalizado.";
        }

        if (!temEquipasSuficientes()) {
            return "Ainda faltam equipas.\nTens "
                    + equipas.size()
                    + " de "
                    + numeroEquipasNecessarias
                    + " equipas necessárias.";
        }

        if (!temEstadiosSuficientes()) {
            return "Ainda faltam estádios.\nTens "
                    + estadios.size()
                    + " de "
                    + numeroEstadiosNecessarios
                    + " estádios necessários.";
        }

        if (!gruposGerados) {
            return "Primeiro tens de gerar os grupos antes de iniciar o campeonato.";
        }

        return "";
    }

    public boolean iniciarCampeonato() {
        if (!podeIniciarCampeonato()) {
            return false;
        }

        estado = ESTADO_INICIADO;
        return true;
    }

    public boolean finalizarCampeonato() {
        if (!isIniciado()) {
            return false;
        }

        estado = ESTADO_FINALIZADO;
        return true;
    }

    public boolean isEmConfiguracao() {
        return ESTADO_CONFIGURACAO.equals(estado);
    }

    public boolean isIniciado() {
        return ESTADO_INICIADO.equals(estado);
    }

    public boolean isFinalizado() {
        return ESTADO_FINALIZADO.equals(estado);
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return;
        }

        this.estado = estado;
    }
}