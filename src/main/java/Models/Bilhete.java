package Models;

import java.time.LocalDateTime;

public class Bilhete {

    private final String idTransacao;
    private final String idJogo;
    private final String nomeComprador;
    private final String tipo;
    private final double precoUnitario;
    private final int quantidade;
    private final double total;
    private final String metodoPagamento;
    private final LocalDateTime dataCompra;

    public Bilhete(
            String idTransacao,
            String idJogo,
            String nomeComprador,
            String tipo,
            double precoUnitario,
            int quantidade,
            double total,
            String metodoPagamento,
            LocalDateTime dataCompra
    ) {
        this.idTransacao = idTransacao;
        this.idJogo = idJogo;
        this.nomeComprador = nomeComprador;
        this.tipo = tipo;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.total = total;
        this.metodoPagamento = metodoPagamento;
        this.dataCompra = dataCompra;
    }

    /*
     * Mantém compatibilidade com as compras antigas que ainda não tinham
     * o nome do comprador guardado no ficheiro TSV.
     */
    public Bilhete(
            String idTransacao,
            String idJogo,
            String tipo,
            double precoUnitario,
            int quantidade,
            double total,
            String metodoPagamento,
            LocalDateTime dataCompra
    ) {
        this(
                idTransacao,
                idJogo,
                "Comprador não indicado",
                tipo,
                precoUnitario,
                quantidade,
                total,
                metodoPagamento,
                dataCompra
        );
    }

    public String getIdTransacao() {
        return idTransacao;
    }

    public String getIdJogo() {
        return idJogo;
    }

    public String getNomeComprador() {
        return nomeComprador;
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
