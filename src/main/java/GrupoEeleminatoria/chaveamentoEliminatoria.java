package GrupoEeleminatoria;

import javax.swing.*;
import java.awt.*;

public class chaveamentoEliminatoria extends JPanel {

    private final String[] equipasEsquerda = {
            "Itália",
            "Áustria",
            "Bélgica",
            "Portugal",
            "França",
            "Suíça",
            "Croácia",
            "Espanha"
    };

    private final String[] equipasDireita = {
            "Suécia",
            "Ucrânia",
            "Inglaterra",
            "Alemanha",
            "Países Baixos",
            "República Checa",
            "País de Gales",
            "Dinamarca"
    };

    public chaveamentoEliminatoria() {
        setOpaque(false);
        setPreferredSize(new Dimension(1000, 560));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D desenho = (Graphics2D) g.create();
        desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        desenho.setStroke(new BasicStroke(3));
        desenho.setColor(new Color(220, 245, 248));

        int larguraPainel = getWidth();

        int larguraCaixa = 160;
        int alturaCaixa = 38;

        int esquerdaX = 40;
        int direitaX = larguraPainel - larguraCaixa - 40;

        int inicioY = 60;
        int espacoY = 58;

        int[] posicoesYEsquerda = new int[equipasEsquerda.length];
        int[] posicoesYDireita = new int[equipasDireita.length];

        for (int i = 0; i < equipasEsquerda.length; i++) {
            posicoesYEsquerda[i] = inicioY + i * espacoY;

            desenharCaixaEquipa(
                    desenho,
                    esquerdaX,
                    posicoesYEsquerda[i],
                    larguraCaixa,
                    alturaCaixa,
                    equipasEsquerda[i],
                    true
            );
        }

        for (int i = 0; i < equipasDireita.length; i++) {
            posicoesYDireita[i] = inicioY + i * espacoY;

            desenharCaixaEquipa(
                    desenho,
                    direitaX,
                    posicoesYDireita[i],
                    larguraCaixa,
                    alturaCaixa,
                    equipasDireita[i],
                    false
            );
        }

        desenharChaveEsquerda(desenho, esquerdaX + larguraCaixa, posicoesYEsquerda, alturaCaixa);
        desenharChaveDireita(desenho, direitaX, posicoesYDireita, alturaCaixa);
        desenharFinal(desenho, larguraPainel);

        desenho.dispose();
    }

    private void desenharCaixaEquipa(
            Graphics2D desenho,
            int x,
            int y,
            int largura,
            int altura,
            String nome,
            boolean alinhadoEsquerda
    ) {
        desenho.setColor(new Color(255, 255, 255, 35));
        desenho.fillRoundRect(x, y, largura, altura, 6, 6);

        desenho.setColor(new Color(225, 250, 252));
        desenho.setStroke(new BasicStroke(2));
        desenho.drawRoundRect(x, y, largura, altura, 6, 6);

        desenho.setFont(new Font("Segoe UI", Font.BOLD, 13));
        desenho.setColor(Color.WHITE);

        if (alinhadoEsquerda) {
            desenho.drawString(nome, x + 15, y + 24);
        } else {
            FontMetrics medidas = desenho.getFontMetrics();
            int larguraTexto = medidas.stringWidth(nome);
            desenho.drawString(nome, x + largura - larguraTexto - 15, y + 24);
        }
    }

    private void desenharChaveEsquerda(
            Graphics2D desenho,
            int inicioX,
            int[] y,
            int alturaCaixa
    ) {
        int ronda1X = inicioX;
        int ronda2X = inicioX + 90;
        int ronda3X = inicioX + 180;
        int finalX = inicioX + 270;

        for (int i = 0; i < y.length; i += 2) {
            int y1 = y[i] + alturaCaixa / 2;
            int y2 = y[i + 1] + alturaCaixa / 2;
            int meio = (y1 + y2) / 2;

            desenho.drawLine(ronda1X, y1, ronda2X, y1);
            desenho.drawLine(ronda1X, y2, ronda2X, y2);
            desenho.drawLine(ronda2X, y1, ronda2X, y2);
            desenho.drawLine(ronda2X, meio, ronda2X + 40, meio);
        }

        ligarParesEsquerda(desenho, ronda2X + 40, ronda3X, y, alturaCaixa, 4);
        ligarParesEsquerda(desenho, ronda3X + 40, finalX, y, alturaCaixa, 8);
    }

    private void desenharChaveDireita(
            Graphics2D desenho,
            int inicioX,
            int[] y,
            int alturaCaixa
    ) {
        int ronda1X = inicioX;
        int ronda2X = inicioX - 90;
        int ronda3X = inicioX - 180;
        int finalX = inicioX - 270;

        for (int i = 0; i < y.length; i += 2) {
            int y1 = y[i] + alturaCaixa / 2;
            int y2 = y[i + 1] + alturaCaixa / 2;
            int meio = (y1 + y2) / 2;

            desenho.drawLine(ronda1X, y1, ronda2X, y1);
            desenho.drawLine(ronda1X, y2, ronda2X, y2);
            desenho.drawLine(ronda2X, y1, ronda2X, y2);
            desenho.drawLine(ronda2X, meio, ronda2X - 40, meio);
        }

        ligarParesDireita(desenho, ronda2X - 40, ronda3X, y, alturaCaixa, 4);
        ligarParesDireita(desenho, ronda3X - 40, finalX, y, alturaCaixa, 8);
    }

    private void ligarParesEsquerda(
            Graphics2D desenho,
            int origemX,
            int destinoX,
            int[] y,
            int alturaCaixa,
            int passo
    ) {
        for (int i = 0; i < y.length; i += passo) {
            int y1 = calcularMediaY(y, i, passo / 2, alturaCaixa);
            int y2 = calcularMediaY(y, i + passo / 2, passo / 2, alturaCaixa);
            int meio = (y1 + y2) / 2;

            desenho.drawLine(origemX, y1, destinoX, y1);
            desenho.drawLine(origemX, y2, destinoX, y2);
            desenho.drawLine(destinoX, y1, destinoX, y2);
            desenho.drawLine(destinoX, meio, destinoX + 40, meio);
        }
    }

    private void ligarParesDireita(
            Graphics2D desenho,
            int origemX,
            int destinoX,
            int[] y,
            int alturaCaixa,
            int passo
    ) {
        for (int i = 0; i < y.length; i += passo) {
            int y1 = calcularMediaY(y, i, passo / 2, alturaCaixa);
            int y2 = calcularMediaY(y, i + passo / 2, passo / 2, alturaCaixa);
            int meio = (y1 + y2) / 2;

            desenho.drawLine(origemX, y1, destinoX, y1);
            desenho.drawLine(origemX, y2, destinoX, y2);
            desenho.drawLine(destinoX, y1, destinoX, y2);
            desenho.drawLine(destinoX, meio, destinoX - 40, meio);
        }
    }

    private int calcularMediaY(
            int[] y,
            int inicio,
            int quantidade,
            int alturaCaixa
    ) {
        int soma = 0;

        for (int i = inicio; i < inicio + quantidade; i++) {
            soma += y[i] + alturaCaixa / 2;
        }

        return soma / quantidade;
    }

    private void desenharFinal(Graphics2D desenho, int larguraPainel) {
        int centroX = larguraPainel / 2;
        int centroY = 300;

        desenho.setColor(new Color(255, 255, 255, 45));
        desenho.fillRoundRect(centroX - 80, centroY - 24, 160, 48, 8, 8);

        desenho.setColor(new Color(225, 250, 252));
        desenho.setStroke(new BasicStroke(2));
        desenho.drawRoundRect(centroX - 80, centroY - 24, 160, 48, 8, 8);

        desenho.setFont(new Font("Segoe UI", Font.BOLD, 15));
        desenho.setColor(Color.WHITE);

        String texto = "FINAL";
        FontMetrics medidas = desenho.getFontMetrics();
        int larguraTexto = medidas.stringWidth(texto);

        desenho.drawString(texto, centroX - larguraTexto / 2, centroY + 6);
    }
}