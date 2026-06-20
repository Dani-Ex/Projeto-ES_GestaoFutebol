package Models;

import java.util.Locale;

public class EventoJogo {

    public static final String TIPO_GOLO = "GOLO";
    public static final String TIPO_CARTAO = "CARTAO";

    private final String id;
    private final String chaveJogo;
    private final String tipo;
    private final String equipa;
    private final String jogadorNome;
    private final int jogadorNumero;
    private final int minuto;
    private final int segundo;
    private final String detalhe;

    public EventoJogo(
            String id,
            String chaveJogo,
            String tipo,
            String equipa,
            String jogadorNome,
            int jogadorNumero,
            int minuto,
            int segundo,
            String detalhe
    ) {
        this.id = id;
        this.chaveJogo = chaveJogo;
        this.tipo = tipo;
        this.equipa = equipa;
        this.jogadorNome = jogadorNome;
        this.jogadorNumero = jogadorNumero;
        this.minuto = minuto;
        this.segundo = segundo;
        this.detalhe = detalhe;
    }

    public String getId() {
        return id;
    }

    public String getChaveJogo() {
        return chaveJogo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEquipa() {
        return equipa;
    }

    public String getJogadorNome() {
        return jogadorNome;
    }

    public int getJogadorNumero() {
        return jogadorNumero;
    }

    public int getMinuto() {
        return minuto;
    }

    public int getSegundo() {
        return segundo;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public boolean eGolo() {
        return TIPO_GOLO.equalsIgnoreCase(tipo);
    }

    public boolean eCartao() {
        return TIPO_CARTAO.equalsIgnoreCase(tipo);
    }

    public String getTempoFormatado() {
        return String.format(Locale.ROOT, "%02d:%02d", minuto, segundo);
    }

    public String getTipoApresentacao() {
        return eGolo() ? "Golo" : "Cartão";
    }
}