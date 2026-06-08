
public class Bilhete {

    private String tipo;
    private double preco;
    private int vendidos;

    public Bilhete(String tipo, double preco, int vendidos) {
        this.tipo = tipo;
        this.preco = preco;
        this.vendidos = vendidos;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPreco() {
        return preco;
    }

    public int getVendidos() {
        return vendidos;
    }

    public double getReceita() {
        return preco * vendidos;
    }
}