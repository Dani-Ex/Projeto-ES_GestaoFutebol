import javax.swing.*;
import java.awt.*;

public class MenuLateral extends JPanel {

    public MenuLateral(JFrame frame) {
        setPreferredSize(new Dimension(Tema.LARGURA_MENU, 0));
        setBackground(Tema.COR_MENU);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_MENU.top,
                Tema.PADDING_MENU.left,
                Tema.PADDING_MENU.bottom,
                Tema.PADDING_MENU.right
        ));

        JLabel titulo = new JLabel("Menu");
        titulo.setForeground(Tema.COR_TEXTO_CLARO);
        titulo.setFont(Tema.FONTE_MENU_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnCampeonato = criarBotaoMenu("Campeonato");
        JButton btnEquipas = criarBotaoMenu("Equipas");
        JButton btnJogadores = criarBotaoMenu("Jogadores");
        JButton btnEstadios = criarBotaoMenu("Estádios");
        JButton btnBilhetes = criarBotaoMenu("Bilhetes");

        btnCampeonato.addActionListener(e -> {
            frame.dispose();
            new CampeonatosFrame();
        });

        btnEquipas.addActionListener(e -> {
            frame.dispose();
            new EquipasFrame();
        });

        btnJogadores.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Página de jogadores ainda não implementada."
        ));

        btnEstadios.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Página de estádios ainda não implementada."
        ));

        btnBilhetes.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Página de bilhetes ainda não implementada."
        ));

        add(titulo);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_GRANDE));

        add(btnCampeonato);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));

        add(btnEquipas);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));

        add(btnJogadores);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));

        add(btnEstadios);
        add(Box.createVerticalStrut(Tema.ESPACAMENTO_PEQUENO));

        add(btnBilhetes);
    }

    private JButton criarBotaoMenu(String texto) {
        JButton btn = new JButton(texto);

        btn.setMaximumSize(new Dimension(200, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);

        btn.setBackground(Tema.COR_MENU_BOTAO);
        btn.setForeground(Tema.COR_TEXTO_CLARO);
        btn.setFont(Tema.FONTE_MENU);

        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }
}