package Models;

import java.io.Serializable;
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

    private int numeroEquipasNecessarias;
    private int numeroEstadiosNecessarios;

    private String estado;

    public Campeonato(String nome) {
        this(nome, 0, 0);
    }

    public Campeonato(String nome, int numeroEquipasNecessarias, int numeroEstadiosNecessarios) {
        this.nome = nome;

        this.gruposGerados = false;
        this.faseGruposTerminada = false;
        this.eliminatoriasGeradas = false;

        this.equipas = new ArrayList<>();
        this.grupos = new LinkedHashMap<>();
        this.equipasEliminatorias = new ArrayList<>();

        this.estadios = new ArrayList<>();

        this.numeroEquipasNecessarias = numeroEquipasNecessarias;
        this.numeroEstadiosNecessarios = numeroEstadiosNecessarios;

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

        if (equipa == null || equipa.trim().isEmpty()) {
            return false;
        }

        for (String equipaExistente : equipas) {
            if (equipaExistente.equalsIgnoreCase(equipa.trim())) {
                return false;
            }
        }

        equipas.add(equipa.trim());
        return true;
    }

    public boolean removerEquipa(String equipa) {
        if (!isEmConfiguracao()) {
            return false;
        }

        return equipas.remove(equipa);
    }

    public Map<String, List<String>> getGrupos() {
        return grupos;
    }

    public void setGrupos(Map<String, List<String>> grupos) {
        this.grupos = grupos;
    }

    public List<String> getEquipasEliminatorias() {
        return equipasEliminatorias;
    }

    public void setEquipasEliminatorias(List<String> equipasEliminatorias) {
        this.equipasEliminatorias = equipasEliminatorias;
    }

    public ArrayList<Estadio> getEstadios() {
        return estadios;
    }

    public boolean adicionarEstadio(Estadio estadio) {
        if (!isEmConfiguracao()) {
            return false;
        }

        if (estadio == null) {
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

        return estadios.remove(estadio);
    }

    public boolean existeEstadioComNome(String nome) {
        if (nome == null) {
            return false;
        }

        for (Estadio estadio : estadios) {
            if (estadio.getNome().equalsIgnoreCase(nome.trim())) {
                return true;
            }
        }

        return false;
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

    public boolean temEquipasSuficientes() {
        return equipas.size() >= numeroEquipasNecessarias;
    }

    public boolean temEstadiosSuficientes() {
        return estadios.size() >= numeroEstadiosNecessarios;
    }

    public boolean podeIniciarCampeonato() {
        return isEmConfiguracao()
                && temEquipasSuficientes()
                && temEstadiosSuficientes();
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