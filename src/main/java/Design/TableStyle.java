package Design;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class TableStyle {

    private TableStyle() {
    }

    public static void aplicarTabelaLimpa(JTable tabela, int colunaEsquerda) {
        tabela.setFont(Tema.FONTE_TEXTO_PEQUENO);
        tabela.setRowHeight(34);
        tabela.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setBackground(Tema.COR_CARD);
        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(false);
        tabela.setShowVerticalLines(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setBorder(BorderFactory.createEmptyBorder());
        tabela.setSelectionBackground(Tema.COR_SELECAO_NEUTRA);
        tabela.setSelectionForeground(Tema.COR_TEXTO_PRINCIPAL);
        tabela.setFocusable(false);

        configurarHeader(tabela, colunaEsquerda);
        aplicarRenderersPadrao(tabela, colunaEsquerda);
    }

    public static void configurarScrollLimpo(JScrollPane scroll, Color background) {
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(background);
        scroll.setBackground(background);

        if (scroll.getColumnHeader() != null) {
            scroll.getColumnHeader().setBorder(BorderFactory.createEmptyBorder());
            scroll.getColumnHeader().setBackground(background);
        }

        ModernScrollBarUI.aplicar(scroll);
    }

    public static DefaultTableCellRenderer rendererCentro() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setVerticalAlignment(SwingConstants.CENTER);
        renderer.setBorder(new EmptyBorder(0, 8, 0, 8));
        return renderer;
    }

    public static DefaultTableCellRenderer rendererEsquerda() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.LEFT);
        renderer.setVerticalAlignment(SwingConstants.CENTER);
        renderer.setBorder(new EmptyBorder(0, 8, 0, 8));
        return renderer;
    }

    public static void configurarHeader(JTable tabela, int colunaEsquerda) {
        JTableHeader header = tabela.getTableHeader();
        header.setFont(Tema.FONTE_CARD_TITULO);
        header.setForeground(Tema.COR_TEXTO_SECUNDARIO);
        header.setBackground(Tema.COR_CARD);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column
                );

                label.setFont(Tema.FONTE_CARD_TITULO);
                label.setForeground(Tema.COR_TEXTO_SECUNDARIO);
                label.setBackground(Tema.COR_CARD);
                label.setOpaque(true);
                label.setHorizontalAlignment(column == colunaEsquerda ? SwingConstants.LEFT : SwingConstants.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.COR_LINHA),
                        BorderFactory.createEmptyBorder(0, 8, 8, 8)
                ));

                return label;
            }
        });
    }

    private static void aplicarRenderersPadrao(JTable tabela, int colunaEsquerda) {
        DefaultTableCellRenderer centro = rendererCentro();
        DefaultTableCellRenderer esquerda = rendererEsquerda();

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centro);
        }

        if (colunaEsquerda >= 0 && colunaEsquerda < tabela.getColumnCount()) {
            tabela.getColumnModel().getColumn(colunaEsquerda).setCellRenderer(esquerda);
        }
    }
}
