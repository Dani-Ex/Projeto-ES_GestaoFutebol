package Models;

import java.util.ArrayList;
import java.util.List;

public class Campeonato {

    private String nome;

    private List<Equipa> equipas;
    private List<Estadio> estadios;

    private boolean iniciado;

    public Campeonato(String nome) {

        this.nome = nome;

        equipas = new ArrayList<>();
        estadios = new ArrayList<>();
    }

    public boolean isIniciado() {
        return iniciado;
    }

    public void setIniciado(boolean iniciado) {
        this.iniciado = iniciado;
    }

    public List<Equipa> getEquipas() {
        return equipas;
    }

    public List<Estadio> getEstadios() {
        return estadios;
    }
}
