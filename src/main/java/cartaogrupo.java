package ui.components;

import ui.theme.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class CartaoGrupo extends RoundedPanel {

    public CartaoGrupo(String nomeGrupo, String[] equipas) {
        super(18, Color.WHITE);

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel tituloGrupo = new JLabel("🏆  " + nomeGrupo);
        tituloGrupo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tituloGrupo.setForeground(AppColors.TEXT);

        String[] colunas = {
                "#",
                "Equipa",
                "PD",
                "V",
                "E",
                "D",
                "DG",
                "Pts"
        };

        DefaultTableModel modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        for (int i = 0; i < equipas.length; i++) {
            modeloTabela.addRow(new Object[]{
                    i + 1,
                    equipas[i],
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            });
        }

        JTable tabela = new JTable(modeloTabela);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setForeground(new Color(51, 65, 85));
        tabela.setGridColor(new Color(226, 232, 240));
        tabela.setShowVerticalLines(false);
        tabela.setFocusable(false);
        tabela.setSelectionBackground(new Color(220, 252, 231));
        tabela.setSelectionForeground(new Color(30, 41, 59));

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cabecalho.setForeground(new Color(100, 116, 139));
        cabecalho.setBackground(Color.WHITE);
        cabecalho.setBorder(null);

        tabela.getColumnModel().getColumn(0).setMaxWidth(35);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(170);

        if (tabela.getRowCount() >= 2) {
            tabela.addRowSelectionInterval(0, 1);
        }

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        add(tituloGrupo, BorderLayout.NORTH);
        add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);
    }
}