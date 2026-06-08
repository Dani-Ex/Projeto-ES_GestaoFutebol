
public class EquipaService {

    public void adicionarJogador(Equipa equipa,
                                 Jogador jogador) {

        if (equipa.getJogadores().size() >= 23) {
            throw new IllegalArgumentException(
                    "A equipa já possui 23 jogadores");
        }

        equipa.getJogadores().add(jogador);
    }

    public void removerJogador(Equipa equipa,
                               Jogador jogador,
                               boolean campeonatoIniciado) {

        if (campeonatoIniciado) {
            throw new IllegalStateException(
                    "Não é possível remover jogador durante a competição");
        }

        equipa.getJogadores().remove(jogador);
    }
}