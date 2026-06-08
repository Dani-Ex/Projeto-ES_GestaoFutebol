import java.time.LocalDate;
import java.time.Period;

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

    private boolean ativo = true;

    public Jogador(String nome, int numero, String posicao,
                   double peso, double altura,
                   String peDominante,
                   LocalDate dataNascimento,
                   String paisOrigem,
                   String lugarNascimento) {

        this(nome, numero, posicao, peso, altura, peDominante, dataNascimento, paisOrigem, lugarNascimento,
                "Sem equipa", "Grupo A", "Regular", 0, 0, 0, 0, 0, 50, 50, 80, true);
    }

    public Jogador(String nome, int numero, String posicao,
                   double peso, double altura,
                   String peDominante,
                   LocalDate dataNascimento,
                   String paisOrigem,
                   String lugarNascimento,
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
                   boolean ativo) {

        this.nome = nome;
        this.numero = numero;
        this.posicao = posicao;
        this.peso = peso;
        this.altura = altura;
        this.peDominante = peDominante;
        this.dataNascimento = dataNascimento;
        this.paisOrigem = paisOrigem;
        this.lugarNascimento = lugarNascimento;
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

    public String getNome() { return nome; }
    public int getNumero() { return numero; }
    public String getPosicao() { return posicao; }
    public double getPeso() { return peso; }
    public double getAltura() { return altura; }
    public String getPeDominante() { return peDominante; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getPaisOrigem() { return paisOrigem; }
    public String getLugarNascimento() { return lugarNascimento; }
    public String getEquipa() { return equipa; }
    public String getGrupo() { return grupo; }
    public String getRanking() { return ranking; }
    public int getJogos() { return jogos; }
    public int getGolos() { return golos; }
    public int getAssistencias() { return assistencias; }
    public int getCartoes() { return cartoes; }
    public int getMinutos() { return minutos; }
    public int getFinalizacao() { return finalizacao; }
    public int getPasseCriacao() { return passeCriacao; }
    public int getDisciplina() { return disciplina; }
    public boolean isAtivo() { return ativo; }

    public int getIdade() {
        if (dataNascimento == null) return 0;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public String getEstadoTexto() {
        return ativo ? "Ativo" : "Inativo";
    }

    public String getIniciais() {
        if (nome == null || nome.isBlank()) return "?";
        String[] partes = nome.trim().split("\\s+");
        if (partes.length == 1) return partes[0].substring(0, 1).toUpperCase();
        return (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }

    public void inativar() { this.ativo = false; }
    public void ativar() { this.ativo = true; }
    public void alternarEstado() { this.ativo = !this.ativo; }
}
