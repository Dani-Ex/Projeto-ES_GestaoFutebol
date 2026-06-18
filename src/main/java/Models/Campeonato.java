import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Campeonato {

    private String nome;

    private boolean gruposGerados;
    private boolean faseGruposTerminada;
    private boolean eliminatoriasGeradas;

    private List<String> equipas;
    private Map<String, List<String>> grupos;
    private List<String> equipasEliminatorias;

    public Campeonato(String nome) {
        this.nome = nome;
        this.gruposGerados = false;
        this.faseGruposTerminada = false;
        this.eliminatoriasGeradas = false;

        this.equipas = new ArrayList<>();
        this.grupos = new LinkedHashMap<>();
        this.equipasEliminatorias = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public void adicionarEquipa(String equipa) {
        equipas.add(equipa);
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
}