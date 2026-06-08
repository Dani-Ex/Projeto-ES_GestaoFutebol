import javax.swing.*;
import java.awt.*;

public class MenuLateral extends JPanel {

    private JPanel menu;
    private boolean aberto = false;

    public MenuLateral(JFrame frame) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JButton btnMenu = new JButton("☰");
        btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 22));
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setBackground(new Color(245, 247, 250));
        btnMenu.setForeground(new Color(15, 23, 42));
        btnMenu.setPreferredSize(new Dimension(55, 45));

        menu = criarMenu(frame);
        menu.setVisible(false);

        btnMenu.addActionListener(e -> {
            aberto = !aberto;
            menu.setVisible(aberto);
            revalidate();
            repaint();
        });

        add(btnMenu, BorderLayout.NORTH);
        add(menu, BorderLayout.CENTER);
    }

    private JPanel criarMenu(JFrame frame) {
        JPanel painel = new JPanel();
        painel.setPreferredSize(new Dimension(230, 0));
        painel.setBackground(new Color(30, 41, 59));
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel titulo = new JLabel("Campeonato");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnDashboard = criarBotaoMenu("Dashboard");
        JButton btnEquipas = criarBotaoMenu("Equipas");
        JButton btnJogadores = criarBotaoMenu("Jogadores");
        JButton btnEstadios = criarBotaoMenu("Estádios");
        JButton btnBilhetes = criarBotaoMenu("Bilhetes");

        btnDashboard.addActionListener(e -> {
            frame.dispose();
            new DashboardFrame();
        });

        btnEquipas.addActionListener(e -> {
            frame.dispose();
            new EquipasFrame();
        });

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(30));
        painel.add(btnDashboard);
        painel.add(Box.createVerticalStrut(10));
        painel.add(btnEquipas);
        painel.add(Box.createVerticalStrut(10));
        painel.add(btnJogadores);
        painel.add(Box.createVerticalStrut(10));
        painel.add(btnEstadios);
        painel.add(Box.createVerticalStrut(10));
        painel.add(btnBilhetes);

        return painel;
    }

    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(200, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(51, 65, 85));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return btn;
    }
}