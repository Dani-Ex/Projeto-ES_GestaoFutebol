package Models;

import java.io.Serializable;

public class Jogo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final String data;
    private final String hora;
    private final String equipaA;
    private final String equipaB;
    private final String estadio;
    private final String faseGrupo;
    private final String estado;
    private final String resultado;
    private final String campeonato;

    public Jogo(
            String id,
            String data,
            String hora,
            String equipaA,
            String equipaB,
            String estadio,
            String faseGrupo,
            String estado,
            String resultado,
            String campeonato
    ) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.equipaA = equipaA;
        this.equipaB = equipaB;
        this.estadio = estadio;
        this.faseGrupo = faseGrupo;
        this.estado = estado;
        this.resultado = resultado;
        this.campeonato = campeonato;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public String getEquipaA() {
        return equipaA;
    }

    public String getEquipaB() {
        return equipaB;
    }

    public String getEstadio() {
        return estadio;
    }

    public String getFaseGrupo() {
        return faseGrupo;
    }

    public String getEstado() {
        return estado;
    }

    public String getResultado() {
        return resultado;
    }

    public String getCampeonato() {
        return campeonato;
    }

    public String getNomeJogo() {
        return equipaA + " vs " + equipaB;
    }
}