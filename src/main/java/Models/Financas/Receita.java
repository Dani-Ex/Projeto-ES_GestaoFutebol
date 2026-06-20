package Models.Financas;

public class Receita {

    private final String idJogo;
    private final int bilhetes;
    private final double bilheteira;
    private final double patrocinio;
    private final double direitosTv;

    public Receita(String idJogo, int bilhetes, double bilheteira, double patrocinio, double direitosTv) {
        this.idJogo = idJogo;
        this.bilhetes = bilhetes;
        this.bilheteira = bilheteira;
        this.patrocinio = patrocinio;
        this.direitosTv = direitosTv;
    }

    public String getIdJogo() {
        return idJogo;
    }

    public int getBilhetes() {
        return bilhetes;
    }

    public double getBilheteira() {
        return bilheteira;
    }

    public double getPatrocinio() {
        return patrocinio;
    }

    public double getDireitosTv() {
        return direitosTv;
    }

    public double getLucro() {
        return bilheteira + patrocinio + direitosTv;
    }
}
