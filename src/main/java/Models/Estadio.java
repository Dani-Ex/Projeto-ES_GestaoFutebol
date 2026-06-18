package Models;

import java.io.Serializable;

public class Estadio implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String cidade;
    private String proprietario;

    private int lugaresNormal;
    private int lugaresVip;
    private int lugaresPremium;

    public Estadio(String nome,
                   String cidade,
                   String proprietario,
                   int lugaresNormal,
                   int lugaresVip,
                   int lugaresPremium) {

        this.nome = nome;
        this.cidade = cidade;
        this.proprietario = proprietario;
        this.lugaresNormal = lugaresNormal;
        this.lugaresVip = lugaresVip;
        this.lugaresPremium = lugaresPremium;
    }

    public String getNome() {
        return nome;
    }

    public String getCidade() {
        return cidade;
    }

    public String getProprietario() {
        return proprietario;
    }

    public int getLugaresNormal() {
        return lugaresNormal;
    }

    public int getLugaresVip() {
        return lugaresVip;
    }

    public int getLugaresPremium() {
        return lugaresPremium;
    }

    public int getCapacidadeTotal() {
        return lugaresNormal + lugaresVip + lugaresPremium;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public void setLugaresNormal(int lugaresNormal) {
        this.lugaresNormal = lugaresNormal;
    }

    public void setLugaresVip(int lugaresVip) {
        this.lugaresVip = lugaresVip;
    }

    public void setLugaresPremium(int lugaresPremium) {
        this.lugaresPremium = lugaresPremium;
    }
}