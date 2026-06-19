package Design;

import java.awt.*;

public class Tema {

    // =========================
    // CORES PRINCIPAIS
    // =========================

    public static final Color COR_FUNDO = new Color(241, 245, 249);
    public static final Color COR_CARD = Color.WHITE;

    public static final Color COR_TEXTO_PRINCIPAL = new Color(15, 23, 42);
    public static final Color COR_TEXTO_SECUNDARIO = new Color(100, 116, 139);
    public static final Color COR_TEXTO_CLARO = Color.WHITE;

    public static final Color COR_LINHA = new Color(226, 232, 240);

    public static final Color COR_SELECAO_NEUTRA = new Color(226, 232, 240);

    // =========================
    // CORES DO MENU LATERAL
    // =========================

    public static final Color COR_MENU = new Color(30, 41, 59);
    public static final Color COR_MENU_BOTAO = new Color(51, 65, 85);
    public static final Color COR_MENU_BOTAO_HOVER = new Color(71, 85, 105);

    // =========================
    // CORES DOS CARDS DO DASHBOARD
    // =========================

    public static final Color CARD_AZUL = new Color(219, 234, 254);
    public static final Color CARD_VERDE = new Color(220, 252, 231);
    public static final Color CARD_ROXO = new Color(233, 213, 255);
    public static final Color CARD_AMARELO = new Color(254, 240, 138);

    public static final Color COR_INPUT = Color.WHITE;
    public static final Color COR_BOTAO_SECUNDARIO = new Color(241, 245, 249);
    public static final Color COR_ERRO_SUAVE = new Color(254, 226, 226);
    public static final Color COR_VERDE_SUAVE = new Color(220, 252, 231);
    public static final Color COR_VERDE_FORTE = new Color(22, 163, 74);

    public static final Color CARD_TEXTO_AZUL = new Color(79, 70, 229);
    public static final Color CARD_TEXTO_VERDE = new Color(22, 101, 52);
    public static final Color CARD_TEXTO_ROXO = new Color(109, 40, 217);
    public static final Color CARD_TEXTO_LARANJA = new Color(194, 65, 12);

    // =========================
    // CORES DE ESTADO
    // =========================

    public static final Color COR_SUCESSO = new Color(34, 197, 94);
    public static final Color COR_AVISO = new Color(245, 158, 11);
    public static final Color COR_ERRO = new Color(239, 68, 68);
    public static final Color COR_INFO = new Color(59, 130, 246);

/// ========================
/// CORES DE JOGADOR
/// ========================

    public static final Color COR_AZUL_SUAVE = new Color(219, 234, 254);
    public static final Color COR_LARANJA_SUAVE = new Color(255, 237, 213);
    public static final Color COR_ROXO_FORTE = new Color(124, 58, 237);

    // =========================
    // FONTES
    // =========================

    public static final String FONTE_PADRAO = "Segoe UI";

    public static final Font FONTE_TITULO_GRANDE =
            new Font(FONTE_PADRAO, Font.BOLD, 30);

    public static final Font FONTE_TITULO =
            new Font(FONTE_PADRAO, Font.BOLD, 24);

    public static final Font FONTE_TABELA_TITULO = new Font(FONTE_PADRAO, Font.BOLD, 20);

    public static final Font FONTE_SUBTITULO =
            new Font(FONTE_PADRAO, Font.PLAIN, 14);

    public static final Font FONTE_CARD_TITULO =
            new Font(FONTE_PADRAO, Font.BOLD, 13);

    public static final Font FONTE_CARD_VALOR =
            new Font(FONTE_PADRAO, Font.BOLD, 22);

    public static final Font FONTE_CARD_VALOR_GRANDE =
            new Font(FONTE_PADRAO, Font.BOLD, 28);

    public static final Font FONTE_TEXTO =
            new Font(FONTE_PADRAO, Font.PLAIN, 14);

    public static final Font FONTE_TEXTO_PEQUENO =
            new Font(FONTE_PADRAO, Font.PLAIN, 12);

    public static final Font FONTE_MENU =
            new Font(FONTE_PADRAO, Font.PLAIN, 15);

    public static final Font FONTE_MENU_TITULO =
            new Font(FONTE_PADRAO, Font.BOLD, 20);

    public static final Font FONTE_BOTAO_MENU =
            new Font(FONTE_PADRAO, Font.BOLD, 22);

    // =========================
    // TAMANHOS
    // =========================

    public static final int RAIO_CARD = 20;
    public static final int ALTURA_CARD_RESUMO = 90;
    public static final int LARGURA_MENU = 230;

    public static final int ESPACAMENTO_PEQUENO = 10;
    public static final int ESPACAMENTO_MEDIO = 20;
    public static final int ESPACAMENTO_GRANDE = 30;

    // =========================
    // BORDAS / PADDING
    // =========================

    public static final Insets PADDING_JANELA =
            new Insets(20, 20, 20, 20);

    public static final Insets PADDING_CARD =
            new Insets(18, 18, 18, 18);

    public static final Insets PADDING_CARD_RESUMO =
            new Insets(15, 18, 15, 18);

    public static final Insets PADDING_MENU =
            new Insets(20, 15, 20, 15);
}
