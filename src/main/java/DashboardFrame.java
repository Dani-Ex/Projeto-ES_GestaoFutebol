import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Dashboard do Campeonato");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 250));
        add(main);

        // Conteúdo principal
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("Dashboard");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JPanel cards = criarCardsResumo();

        content.add(titulo, BorderLayout.NORTH);
        content.add(cards, BorderLayout.CENTER);

        main.add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarCardsResumo() {
        JPanel cards = new JPanel(new GridLayout(2, 3, 20, 20));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        cards.add(criarCard("Equipas", "3", "Equipas registadas"));
        cards.add(criarCard("Jogadores", "69", "Total de jogadores"));
        cards.add(criarCard("Estádios", "0", "Estádios disponíveis"));
        cards.add(criarCard("Bilhetes", "0 €", "Receita total"));
        cards.add(criarCard("Jogos", "0", "Jogos realizados"));
        cards.add(criarCard("Estado", "Não iniciado", "Estado do campeonato"));

        return cards;
    }

    private JPanel criarCard(String titulo, String valor, String descricao) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(71, 85, 105));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValor.setForeground(new Color(15, 23, 42));

        JLabel lblDescricao = new JLabel(descricao);
        lblDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDescricao.setForeground(new Color(100, 116, 139));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        card.add(lblDescricao, BorderLayout.SOUTH);

        return card;
    }
}