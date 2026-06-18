package Design;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class FormUtils {

    private FormUtils() {
    }

    public static PlaceholderTextField criarCampo(String placeholder, Dimension tamanho, int radius) {
        PlaceholderTextField campo = new PlaceholderTextField(placeholder == null ? "" : placeholder);
        aplicarEstiloCampo(campo, tamanho, radius);
        return campo;
    }

    public static PlaceholderTextField criarCampoComValor(String valor, Dimension tamanho, int radius) {
        PlaceholderTextField campo = criarCampo("", tamanho, radius);
        campo.setText(valor == null ? "" : valor);
        return campo;
    }

    public static JComboBox<String> criarCombo(String[] opcoes, String valorAtual, Dimension tamanho, int radius) {
        JComboBox<String> combo = new JComboBox<>(opcoes);
        combo.setPreferredSize(tamanho);
        combo.setMinimumSize(new Dimension(Math.min(210, tamanho.width), tamanho.height));
        combo.setFont(Tema.FONTE_TEXTO_PEQUENO);
        combo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        combo.setBackground(Tema.COR_INPUT);
        combo.setFocusable(false);
        combo.setBorder(new RoundedBorder(Tema.COR_LINHA, radius));

        if (valorAtual != null) {
            combo.setSelectedItem(valorAtual);
        }

        return combo;
    }

    public static JPanel criarCampoComLabel(String label, JComponent campo) {
        return criarCampoComLabel(label, campo, Tema.COR_TEXTO_PRINCIPAL, 8, null);
    }

    public static JPanel criarCampoComLabel(String label,
                                            JComponent campo,
                                            Color corLabel,
                                            int gap,
                                            Dimension tamanho) {
        JPanel painel = new JPanel(new BorderLayout(0, gap));
        painel.setOpaque(false);

        if (tamanho != null) {
            painel.setPreferredSize(tamanho);
            painel.setMinimumSize(new Dimension(Math.min(210, tamanho.width), tamanho.height));
        }

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(Tema.FONTE_PADRAO, Font.BOLD, 12));
        lbl.setForeground(corLabel);

        painel.add(lbl, BorderLayout.NORTH);
        painel.add(campo, BorderLayout.CENTER);

        return painel;
    }

    private static void aplicarEstiloCampo(PlaceholderTextField campo, Dimension tamanho, int radius) {
        campo.setPreferredSize(tamanho);
        campo.setMinimumSize(new Dimension(Math.min(210, tamanho.width), tamanho.height));
        campo.setFont(Tema.FONTE_TEXTO_PEQUENO);
        campo.setForeground(Tema.COR_TEXTO_PRINCIPAL);
        campo.setBackground(Tema.COR_INPUT);
        campo.setOpaque(true);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Tema.COR_LINHA, radius),
                new EmptyBorder(0, 13, 0, 13)
        ));
    }
}
