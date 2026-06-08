

import java.time.LocalDate;

public class Jogador {

    private String nome;
    private int numero;
    private String posicao;
    private double peso;
    private double altura;
    private String peDominante;
    private LocalDate dataNascimento;
    private String paisOrigem;
    private String lugarNascimento;

    private int golos;
    private int assistencias;

    private boolean ativo = true;

    public Jogador(String nome, int numero, String posicao,
                   double peso, double altura,
                   String peDominante,
                   LocalDate dataNascimento,
                   String paisOrigem,
                   String lugarNascimento) {

        this.nome = nome;
        this.numero = numero;
        this.posicao = posicao;
        this.peso = peso;
        this.altura = altura;
        this.peDominante = peDominante;
        this.dataNascimento = dataNascimento;
        this.paisOrigem = paisOrigem;
        this.lugarNascimento = lugarNascimento;
    }

    public String getNome() {
        return nome;
    }

    public int getNumero() {
        return numero;
    }

    public String getPosicao() {
        return posicao;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void inativar() {
        this.ativo = false;
    }
}