import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class PaginaEliminatorias extends JPanel {

    private final Campeonato campeonato;
    private final Runnable alternarMenu;
    private final Consumer<String> mostrarPagina;

    public PaginaEliminatorias(
            Campeonato campeonato,
            Runnable alternarMenu,
            Consumer<String> mostrarPagina
    ) {
        this.campeonato = campeonato;
        this.alternarMenu = alternarMenu;
        this.mostrarPagina = mostrarPagina;

        setLayout(new BorderLayout());
        setBackground(new Color(32, 157, 171));
        setBorder(new EmptyBorder(22, 24, 22, 24));

        construirInterface();
    }

    private void construirInterface() {
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);

        JButton botaoMenu = new JButton("☰");
        botaoMenu.setFocusPainted(false);
        botaoMenu.setBorderPainted(false);
        botaoMenu.setContentAreaFilled(false);
        botaoMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        botaoMenu.setForeground(Color.WHITE);
        botaoMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botaoMenu.addActionListener(e -> alternarMenu.run());

        JLabel titulo = new JLabel("Fase Eliminatória - " + campeonato.getNome(), SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);

        JButton botaoVoltar = criarBotaoAzul("Ver Grupos");
        botaoVoltar.addActionListener(e -> mostrarPagina.accept("grupos"));

        barraSuperior.add(botaoMenu, BorderLayout.WEST);
        barraSuperior.add(titulo, BorderLayout.CENTER);
        barraSuperior.add(botaoVoltar, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        chaveamentoEliminatoria chaveamento = new chaveamentoEliminatoria();
        add(chaveamento, BorderLayout.CENTER);
    }

    private JButton criarBotaoAzul(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(new Color(37, 99, 235));
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));

        return botao;
    }
}