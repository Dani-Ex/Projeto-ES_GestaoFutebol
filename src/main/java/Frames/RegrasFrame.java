package Frames;

import Design.MenuLateral;
import Design.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegrasFrame extends JFrame {

    private MenuLateral menuLateral;
    private boolean menuAberto = false;

    public RegrasFrame() {
        setTitle("Regras do Torneio");
        setSize(1280, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Tema.COR_FUNDO);
        main.setBorder(BorderFactory.createEmptyBorder(
                Tema.PADDING_JANELA.top,
                Tema.PADDING_JANELA.left,
                Tema.PADDING_JANELA.bottom,
                Tema.PADDING_JANELA.right
        ));

        setContentPane(main);

        menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);
        main.add(menuLateral, BorderLayout.WEST);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(0, 25, 0, 25));

        content.add(criarTopo(main));
        content.add(Box.createVerticalStrut(35));
        content.add(criarCardRegras());
        content.add(Box.createVerticalStrut(28));
        content.add(criarObservacao());

        main.add(content, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarTopo(JPanel main) {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton btnMenu = new JButton("☰");
        btnMenu.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 22));
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        btnMenu.setPreferredSize(new Dimension(50, 45));
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnMenu.addActionListener(e -> {
            menuAberto = !menuAberto;
            menuLateral.setVisible(menuAberto);
            main.revalidate();
            main.repaint();
        });

        JPanel tituloBox = new JPanel();
        tituloBox.setOpaque(false);
        tituloBox.setLayout(new BoxLayout(tituloBox, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Regras do Torneio");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Regras funcionais e operacionais definidas pelo utilizador.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        tituloBox.add(titulo);
        tituloBox.add(Box.createVerticalStrut(3));
        tituloBox.add(subtitulo);

        JPanel esquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        esquerda.setOpaque(false);
        esquerda.add(btnMenu);
        esquerda.add(tituloBox);

        topo.add(esquerda, BorderLayout.WEST);

        return topo;
    }

    private JPanel criarCardRegras() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(26, 32, 26, 32));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));

        JLabel titulo = new JLabel("Regras configuradas");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JTextArea regras = new JTextArea();
        regras.setText(
                "1. Cada equipa deve possuir exatamente 23 jogadores antes do início do campeonato.\n\n" +
                        "2. Não é permitido remover jogadores após o início do campeonato.\n\n" +
                        "3. Jogadores apenas podem ser inativados após o término do campeonato.\n\n" +
                        "4. Os grupos devem ser gerados de forma imparcial.\n\n" +
                        "5. Cada grupo deve conter um número par de equipas.\n\n" +
                        "6. O 1.º e o 2.º lugar de cada grupo passam à fase mata-mata.\n\n" +
                        "7. O sistema deve garantir a criação de jogos entre equipas.\n\n" +
                        "8. Um estádio não pode ser utilizado mais do que uma vez no mesmo dia.\n\n" +
                        "9. O número máximo de jogos por dia não pode ultrapassar o número de estádios disponíveis.\n\n" +
                        "10. Cada equipa deve ter um intervalo mínimo de 72 horas entre jogos.\n\n" +
                        "11. O sistema deve impedir o campeonato se o intervalo de datas não permitir concluir todos os jogos.\n\n" +
                        "12. O sistema deve calcular corretamente bilheteira, patrocínios, direitos televisivos e lucro total."
        );

        regras.setEditable(false);
        regras.setFocusable(false);
        regras.setOpaque(false);
        regras.setFont(Tema.FONTE_TEXTO);
        regras.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        regras.setLineWrap(true);
        regras.setWrapStyleWord(true);
        regras.setBorder(new EmptyBorder(22, 4, 0, 4));

        card.add(titulo, BorderLayout.NORTH);
        card.add(regras, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarObservacao() {
        RoundedPanel obs = new RoundedPanel(Tema.RAIO_CARD, new Color(219, 234, 254));
        obs.setLayout(new BorderLayout());
        obs.setBorder(new EmptyBorder(18, 22, 18, 22));
        obs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel titulo = new JLabel("Observação");
        titulo.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 18));
        titulo.setForeground(new Color(37, 99, 235));

        JLabel texto = new JLabel("Estas regras servem como base de validação do sistema e como apoio visual no protótipo.");
        texto.setFont(Tema.FONTE_TEXTO);
        texto.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));

        box.add(titulo);
        box.add(Box.createVerticalStrut(8));
        box.add(texto);

        obs.add(box, BorderLayout.CENTER);

        return obs;
    }

    private static class RoundedPanel extends JPanel {

        private final int raio;
        private final Color corFundo;

        public RoundedPanel(int raio, Color corFundo) {
            this.raio = raio;
            this.corFundo = corFundo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(corFundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);

            g2.dispose();

            super.paintComponent(g);
        }
    }
}