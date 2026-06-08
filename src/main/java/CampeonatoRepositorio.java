import java.util.ArrayList;
import java.util.List;

public class CampeonatoRepositorio {

    private static final List<Campeonato> campeonatos = new ArrayList<>();

    static {
        Campeonato ligaPrimavera = new Campeonato("Liga Primavera");
        Campeonato tacaRegional = new Campeonato("Taça Regional");
        Campeonato superLiga = new Campeonato("Super Liga");
        Campeonato championLeague = new Campeonato("Champion League");
        Campeonato torneioLitoral = new Campeonato("Torneio Litoral");
        Campeonato mundial = new Campeonato("Mundial FIFA 2026");

        // Exemplos: estes já podem aparecer como planeados, mas sem grupos gerados.
        campeonatos.add(ligaPrimavera);
        campeonatos.add(tacaRegional);
        campeonatos.add(superLiga);
        campeonatos.add(championLeague);
        campeonatos.add(torneioLitoral);
        campeonatos.add(mundial);
    }

    public static List<Campeonato> listar() {
        return campeonatos;
    }

    public static void adicionar(Campeonato campeonato) {
        campeonatos.add(campeonato);
    }

    public static Campeonato procurarPorNome(String nome) {
        for (Campeonato campeonato : campeonatos) {
            if (campeonato.getNome().equals(nome)) {
                return campeonato;
            }
        }

        return null;
    }
}