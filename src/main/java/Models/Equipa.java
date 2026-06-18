package Models;

import java.util.ArrayList;
import java.util.List;

public class Equipa {

    private String nome;
    private String cidade;
    private String pais;
    private String treinador;
    private String capitao;

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
        this(nome, cidade, pais, treinador, capitao, "Sem grupo", 0, true);
    }

    public Equipa(String nome,
                  String cidade,
                  String pais,
                  String treinador,
                  String capitao,
                  String grupo,
                  int pontos,
                  boolean ativa) {

        this.nome = nome;
        this.cidade = cidade;
        this.pais = pais;
        this.treinador = treinador;
        this.capitao = capitao;
        this.grupo = grupo;
        this.pontos = pontos;
        this.ativa = ativa;

        this.jogadores = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public String getCidade() {
        return cidade;
    }

    public String getPais() {
        return pais;
    }

    public String getTreinador() {
        return treinador;
    }

    public String getCapitao() {
        return capitao;
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

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
}
