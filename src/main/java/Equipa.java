
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

    private String grupo;

    private List<Jogador> jogadores;

    public Equipa(String nome,
                  String cidade,
                  String pais,
                  String treinador,
                  String capitao) {

        this.nome = nome;
        this.cidade = cidade;
        this.pais = pais;
        this.treinador = treinador;
        this.capitao = capitao;

        this.jogadores = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }
}