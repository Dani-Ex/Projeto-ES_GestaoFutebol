package Models;

import java.time.LocalDateTime;

public class Bilhete {

    private final String idTransacao;
    private final String idJogo;
    private final String tipo;
    private final double precoUnitario;
    private final int quantidade;
    private final double total;
    private final String metodoPagamento;
    private final LocalDateTime dataCompra;

    public Bilhete(String idTransacao,
                   String idJogo,
                   String tipo,
                   double precoUnitario,
                   int quantidade,
                   double total,
                   String metodoPagamento,
                   LocalDateTime dataCompra) {

        this.idTransacao = idTransacao;
        this.idJogo = idJogo;
        this.tipo = tipo;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.total = total;
        this.metodoPagamento = metodoPagamento;
        this.dataCompra = dataCompra;
    }

    public String getIdTransacao() {
        return idTransacao;
    }

    public String getIdJogo() {
        return idJogo;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getTotal() {
        return total;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }
}