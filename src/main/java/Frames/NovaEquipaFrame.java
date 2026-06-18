package Frames;

import Design.RoundedPanel;
import Design.Tema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NovaEquipaFrame extends JFrame {

    private final Runnable onEquipaCriada;

    public NovaEquipaFrame(Runnable onEquipaCriada) {
        this.onEquipaCriada = onEquipaCriada;

        setTitle("Nova Equipa");
        setSize(720, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel fundo = new JPanel(new BorderLayout());
        fundo.setBackground(Tema.COR_FUNDO);
        fundo.setBorder(new EmptyBorder(28, 35, 28, 35));

        fundo.add(criarTopo(), BorderLayout.NORTH);
        fundo.add(criarConteudo(), BorderLayout.CENTER);

        setContentPane(fundo);
        setVisible(true);
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel();
        topo.setOpaque(false);
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBorder(new EmptyBorder(0, 0, 22, 0));

        JLabel titulo = new JLabel("Nova Equipa");
        titulo.setFont(Tema.FONTE_TITULO_GRANDE);
        titulo.setForeground(Tema.COR_TEXTO_PRINCIPAL);

        JLabel subtitulo = new JLabel("Registo de uma nova equipa participante.");
        subtitulo.setFont(Tema.FONTE_SUBTITULO);
        subtitulo.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        topo.add(titulo);
        topo.add(Box.createVerticalStrut(4));
        topo.add(subtitulo);

        return topo;
    }

    private JPanel criarConteudo() {
        RoundedPanel card = new RoundedPanel(Tema.RAIO_CARD, Tema.COR_CARD);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel texto = new JLabel("Formulario da equipa a configurar no proximo passo.", SwingConstants.CENTER);
        texto.setFont(Tema.FONTE_TEXTO);
        texto.setForeground(Tema.COR_TEXTO_SECUNDARIO);

        JButton fechar = new JButton("Fechar");
        fechar.setFont(Tema.FONTE_CARD_TITULO);
        fechar.setFocusPainted(false);
        fechar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        botoes.setOpaque(false);
        botoes.add(fechar);

        card.add(texto, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        return card;
    }

    private void notificarEquipaCriada() {
        if (onEquipaCriada != null) {
            onEquipaCriada.run();
        }
    }
}
