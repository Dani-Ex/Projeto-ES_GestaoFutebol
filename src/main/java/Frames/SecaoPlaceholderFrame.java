package Frames;

import Design.MenuLateral;
import Design.RoundedPanel;
import Design.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SecaoPlaceholderFrame extends JFrame {

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    public SecaoPlaceholderFrame(String titulo) {
        setTitle(titulo);
        setSize(1280, 760);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Tema.COR_FUNDO);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);
        main.add(menuLateral, BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(criarLinhaMenu(main), BorderLayout.NORTH);
        centro.add(criarConteudo(titulo), BorderLayout.CENTER);

        main.add(centro, BorderLayout.CENTER);

        setContentPane(main);
        setVisible(true);
    }

    private JPanel criarLinhaMenu(JPanel main) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setOpaque(false);
        linha.setBorder(new EmptyBorder(0, 30, 0, 0));

        JButton btnMenu = new JButton("=");
        btnMenu.setFont(Tema.FONTE_BOTAO_MENU);
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenu.setPreferredSize(new Dimension(45, 45));

        btnMenu.addActionListener(e -> {
            menuAberto = !menuAberto;
            menuLateral.setVisible(menuAberto);
            main.revalidate();
            main.repaint();
        });

        linha.add(btnMenu);
        return linha;
    }

    private JPanel criarConteudo(String tituloPagina) {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(35, 35, 35, 35));

        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(28, 28, 28, 28));
        card.setPreferredSize(new Dimension(520, 160));

        JLabel titulo = new JLabel(tituloPagina);
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Esta area sera configurada numa proxima etapa.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        card.add(titulo, BorderLayout.NORTH);
        card.add(subtitulo, BorderLayout.CENTER);

        wrapper.add(card);
        return wrapper;
    }
}
