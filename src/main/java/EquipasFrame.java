

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EquipasFrame extends JFrame {

    public EquipasFrame() {
        setTitle("Equipas");
        setSize(1100, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 250));
        add(main);

        // 🔝 Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel titulo = new JLabel("Equipas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel botoes = new JPanel();
        botoes.setOpaque(false);

        JButton consultar = new JButton("Consultar");
        JButton editar = new JButton("Editar Equipa");
        JButton nova = new JButton("+ Nova Equipa");

        nova.setBackground(new Color(0, 120, 255));
        nova.setForeground(Color.WHITE);

        botoes.add(consultar);
        botoes.add(editar);
        botoes.add(nova);

        header.add(titulo, BorderLayout.WEST);
        header.add(botoes, BorderLayout.EAST);

        main.add(header, BorderLayout.NORTH);

        // 📦 Conteúdo
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        // 📊 Card tabela
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel sub = new JLabel("Equipas Registadas");
        sub.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // tabela
        String[] cols = {"Nome", "Grupo", "Estado", "Jogadores", "Pontos"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        model.addRow(new Object[]{"Portugal", "A", "Ativa", 23, 34});
        model.addRow(new Object[]{"Espanha", "A", "Ativa", 23, 31});
        model.addRow(new Object[]{"Argentina", "B", "Ativa", 23, 28});

        JTable table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scroll = new JScrollPane(table);

        card.add(sub, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        // 🟢 Sidebar
        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(260, 0));
        side.setBackground(new Color(220, 240, 230));
        side.setLayout(new BorderLayout());
        side.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tituloSide = new JLabel("Validação obrigatória");
        tituloSide.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JTextArea texto = new JTextArea(
                "Cada equipa deve ter exatamente 23 jogadores.\n\n" +
                        "Equipas fora desta regra não podem participar."
        );
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setOpaque(false);

        side.add(tituloSide, BorderLayout.NORTH);
        side.add(texto, BorderLayout.CENTER);

        // layout lado a lado
        JPanel centerWrap = new JPanel(new BorderLayout(20, 0));
        centerWrap.setOpaque(false);
        centerWrap.add(card, BorderLayout.CENTER);
        centerWrap.add(side, BorderLayout.EAST);

        content.add(centerWrap, BorderLayout.CENTER);
        main.add(content, BorderLayout.CENTER);

        setVisible(true);
    }
}