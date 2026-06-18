package Models;

import java.time.LocalDate;
import java.time.Period;

public class Jogador {

    private String nome;
    private int numero;
    private String posicao;
    private int peso;
    private double altura;
    private String peDominante;
    private LocalDate dataNascimento;
    private String paisOrigem;
    private String cidadeNascimento;
    private String equipa;
    private String grupo;
    private String ranking;

    private int jogos;
    private int golos;
    private int assistencias;
    private int cartoes;
    private int minutos;

    private int finalizacao;
    private int passeCriacao;
    private int disciplina;

    private boolean ativo;

    public Jogador(
            String nome,
            int numero,
            String posicao,
            int peso,
            double altura,
            String peDominante,
            LocalDate dataNascimento,
            String paisOrigem,
            String cidadeNascimento,
            String equipa,
            String grupo,
            String ranking,
            int jogos,
            int golos,
            int assistencias,
            int cartoes,
            int minutos,
            int finalizacao,
            int passeCriacao,
            int disciplina,
            boolean ativo
    ) {
        this.nome = nome;
        this.numero = numero;
        this.posicao = posicao;
        this.peso = peso;
        this.altura = altura;
        this.peDominante = peDominante;
        this.dataNascimento = dataNascimento;
        this.paisOrigem = paisOrigem;
        this.cidadeNascimento = cidadeNascimento;
        this.equipa = equipa;
        this.grupo = grupo;
        this.ranking = ranking;
        this.jogos = jogos;
        this.golos = golos;
        this.assistencias = assistencias;
        this.cartoes = cartoes;
        this.minutos = minutos;
        this.finalizacao = finalizacao;
        this.passeCriacao = passeCriacao;
        this.disciplina = disciplina;
        this.ativo = ativo;
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

    public int getPeso() {
        return peso;
    }

    public double getAltura() {
        return altura;
    }

    public String getPeDominante() {
        return peDominante;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getPaisOrigem() {
        return paisOrigem;
    }

    public String getCidadeNascimento() {
        return cidadeNascimento;
    }

    public String getEquipa() {
        return equipa;
    }

    public String getGrupo() {
        return grupo;
    }

    public String getRanking() {
        return ranking;
    }

    public int getJogos() {
        return jogos;
    }

    public int getGolos() {
        return golos;
    }

    public int getAssistencias() {
        return assistencias;
    }

    public int getCartoes() {
        return cartoes;
    }

    public int getMinutos() {
        return minutos;
    }

    public int getFinalizacao() {
        return finalizacao;
    }

    public int getPasseCriacao() {
        return passeCriacao;
    }

    public int getDisciplina() {
        return disciplina;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getEstadoTexto() {
        return ativo ? "Ativo" : "Inativo";
    }

    public int getIdade() {
        if (dataNascimento == null) {
            return 0;
        }

        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public void alternarEstado() {
        this.ativo = !this.ativo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public void setPeDominante(String peDominante) {
        this.peDominante = peDominante;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setPaisOrigem(String paisOrigem) {
        this.paisOrigem = paisOrigem;
    }

    public void setCidadeNascimento(String cidadeNascimento) {
        this.cidadeNascimento = cidadeNascimento;
    }

    public void setEquipa(String equipa) {
        this.equipa = equipa;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public void setJogos(int jogos) {
        this.jogos = jogos;
    }

    public void setGolos(int golos) {
        this.golos = golos;
    }

    public void setAssistencias(int assistencias) {
        this.assistencias = assistencias;
    }

    public void setCartoes(int cartoes) {
        this.cartoes = cartoes;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public void setFinalizacao(int finalizacao) {
        this.finalizacao = finalizacao;
    }

    public void setPasseCriacao(int passeCriacao) {
        this.passeCriacao = passeCriacao;
    }

    public void setDisciplina(int disciplina) {
        this.disciplina = disciplina;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
