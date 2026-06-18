package Models;

import java.util.ArrayList;
import java.util.List;

public class Equipa {

    private String nome;
    private String cidade;
    private String pais;
    private String treinador;
    private String capitao;
    private String campeonato;
    private String ranking;

    private int pontos;
    private int golos;
    private int totalJogadores;

    private String grupo;
    private boolean ativa;

    private List<Jogador> jogadores;

    public Equipa(String nome,
                  String cidade,
                  String pais,
                  String treinador,
                  String capitao) {
        this(nome, cidade, pais, treinador, capitao, "Campeonato Principal", "Sem grupo", 0, true);
    }

    public Equipa(String nome,
                  String cidade,
                  String pais,
                  String treinador,
                  String capitao,
                  String grupo,
                  int pontos,
                  boolean ativa) {
        this(nome, cidade, pais, treinador, capitao, "Campeonato Principal", grupo, pontos, ativa);
    }

    public Equipa(String nome,
                  String cidade,
                  String pais,
                  String treinador,
                  String capitao,
                  String campeonato,
                  String grupo,
                  int pontos,
                  boolean ativa) {

        this.nome = nome;
        this.cidade = cidade;
        this.pais = pais;
        this.treinador = treinador;
        this.capitao = capitao;
        this.campeonato = campeonato;
        this.grupo = grupo;
        this.pontos = pontos;
        this.ativa = ativa;

        this.jogadores = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTreinador() {
        return treinador;
    }

    public void setTreinador(String treinador) {
        this.treinador = treinador;
    }

    public String getCapitao() {
        return capitao;
    }

    public void setCapitao(String capitao) {
        this.capitao = capitao;
    }

    public String getCampeonato() {
        return campeonato;
    }

    public String getRanking() {
        return ranking;
    }

    public int getPontos() {
        return pontos;
    }

    public int getGolos() {
        return golos;
    }

    public int getTotalJogadores() {
        return Math.max(totalJogadores, jogadores.size());
    }

    public String getGrupo() {
        return grupo;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public String getEstadoTexto() {
        return ativa ? "Ativa" : "Inativa";
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public void setGolos(int golos) {
        this.golos = golos;
    }

    public void setTotalJogadores(int totalJogadores) {
        this.totalJogadores = totalJogadores;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public void setCampeonato(String campeonato) {
        this.campeonato = campeonato;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
}
