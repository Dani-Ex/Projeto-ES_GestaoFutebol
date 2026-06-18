package Models;

public class Estadio {

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
}
