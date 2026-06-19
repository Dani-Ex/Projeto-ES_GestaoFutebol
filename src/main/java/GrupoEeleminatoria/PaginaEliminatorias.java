package GrupoEeleminatoria;

import Models.Campeonato;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PaginaEliminatorias extends JPanel {

    private final Campeonato campeonato;
    private final Runnable alternarMenu;
    private final Consumer<String> navegar;

    private final Color FUNDO = new Color(29, 158, 168);
    private final Color TEXTO_CLARO = Color.WHITE;

    public PaginaEliminatorias(
            Campeonato campeonato,
            Runnable alternarMenu,
            Consumer<String> navegar
    ) {
        this.campeonato = campeonato;
        this.alternarMenu = alternarMenu;
        this.navegar = navegar;

        setLayout(new BorderLayout());
        setBackground(FUNDO);
        setBorder(new EmptyBorder(20, 24, 24, 24));

        add(criarTopo(), BorderLayout.NORTH);
        add(criarBracket(), BorderLayout.CENTER);
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.setBorder(new EmptyBorder(0, 0, 18, 0));

        JButton btnMenu = new JButton("☰");
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnMenu.setForeground(TEXTO_CLARO);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenu.addActionListener(e -> alternarMenu.run());

        JLabel titulo = new JLabel("Eliminatórias - " + campeonato.getNome());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXTO_CLARO);

        JButton btnVoltar = new JButton("Voltar aos Grupos");
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setBackground(new Color(60, 170, 180));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.setBorder(new EmptyBorder(10, 18, 10, 18));
        btnVoltar.addActionListener(e -> navegar.accept("grupos"));

        topo.add(btnMenu, BorderLayout.WEST);
        topo.add(titulo, BorderLayout.CENTER);
        topo.add(btnVoltar, BorderLayout.EAST);

        return topo;
    }

    private JComponent criarBracket() {
        List<String> classificadas = getClassificadas();

        if (classificadas.size() < 2) {
            JTextArea vazio = new JTextArea("""
                    Ainda não existem equipas suficientes para criar eliminatórias.

                    São necessárias pelo menos 2 equipas classificadas.
                    """);

            vazio.setEditable(false);
            vazio.setOpaque(false);
            vazio.setFont(new Font("Segoe UI", Font.BOLD, 18));
            vazio.setForeground(Color.WHITE);
            vazio.setLineWrap(true);
            vazio.setWrapStyleWord(true);

            return vazio;
        }

        BracketPanel bracketPanel = new BracketPanel(classificadas);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(FUNDO);
        wrapper.add(bracketPanel);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.setViewportBorder(null);
        scroll.getViewport().setBackground(FUNDO);
        scroll.setBackground(FUNDO);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        return scroll;
    }

    private List<String> getClassificadas() {
        if (campeonato.getEquipasEliminatorias() == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(campeonato.getEquipasEliminatorias());
    }

    private static class BracketPanel extends JPanel {

        private final List<String> equipasOriginais;

        private final Color FUNDO = new Color(29, 158, 168);
        private final Color CAIXA = new Color(60, 170, 180);
        private final Color CAIXA_BYE = new Color(95, 185, 193);
        private final Color LINHA = new Color(198, 244, 250);
        private final Color TEXTO = Color.WHITE;

        private final int BOX_W = 190;
        private final int BOX_H = 50;

        private final int FINAL_W = 190;
        private final int FINAL_H = 60;

        private final int MARGIN_X = 40;
        private final int GAP_Y = 28;

        public BracketPanel(List<String> equipas) {
            this.equipasOriginais = new ArrayList<>(equipas);

            int tamanhoBracket = proximaPotenciaDeDois(equipasOriginais.size());
            int equipasPorLado = Math.max(1, tamanhoBracket / 2);

            int largura = calcularLargura(tamanhoBracket);
            int altura = calcularAltura(equipasPorLado);

            setPreferredSize(new Dimension(largura, altura));
            setMinimumSize(new Dimension(largura, altura));
            setBackground(FUNDO);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(FUNDO);
            g2.fillRect(0, 0, getWidth(), getHeight());

            List<String> slots = criarSlotsComBye();

            int tamanhoBracket = slots.size();
            int equipasPorLado = Math.max(1, tamanhoBracket / 2);

            List<String> ladoEsquerdo = new ArrayList<>();
            List<String> ladoDireito = new ArrayList<>();

            for (int i = 0; i < equipasPorLado; i++) {
                ladoEsquerdo.add(slots.get(i));
            }

            for (int i = equipasPorLado; i < slots.size(); i++) {
                ladoDireito.add(slots.get(i));
            }

            int leftX = MARGIN_X;
            int rightX = getWidth() - MARGIN_X - BOX_W;

            int finalX = (getWidth() / 2) - (FINAL_W / 2);
            int finalY = (getHeight() / 2) - (FINAL_H / 2);

            List<Integer> yEsquerda = calcularYDasEquipas(ladoEsquerdo.size());
            List<Integer> yDireita = calcularYDasEquipas(ladoDireito.size());

            desenharLinhasLado(
                    g2,
                    true,
                    leftX,
                    BOX_W,
                    finalX,
                    FINAL_W,
                    yEsquerda
            );

            desenharLinhasLado(
                    g2,
                    false,
                    rightX,
                    BOX_W,
                    finalX,
                    FINAL_W,
                    yDireita
            );

            desenharFinal(g2, finalX, finalY);

            for (int i = 0; i < ladoEsquerdo.size(); i++) {
                desenharEquipa(g2, ladoEsquerdo.get(i), leftX, yEsquerda.get(i), false);
            }

            for (int i = 0; i < ladoDireito.size(); i++) {
                desenharEquipa(g2, ladoDireito.get(i), rightX, yDireita.get(i), true);
            }

            g2.dispose();
        }

        private void desenharLinhasLado(
                Graphics2D g2,
                boolean esquerdo,
                int boxX,
                int boxW,
                int finalX,
                int finalW,
                List<Integer> centrosY
        ) {
            if (centrosY.isEmpty()) {
                return;
            }

            g2.setColor(LINHA);
            g2.setStroke(new BasicStroke(2.3f));

            int startX = esquerdo ? boxX + boxW : boxX;
            int finalEdgeX = esquerdo ? finalX : finalX + finalW;

            int rounds = log2(centrosY.size());

            if (rounds == 0) {
                int y = centrosY.get(0);
                g2.drawLine(startX, y, finalEdgeX, y);
                return;
            }

            double step = (finalEdgeX - startX) / (double) (rounds + 1);

            List<Integer> atuaisY = new ArrayList<>(centrosY);
            int atualX = startX;

            for (int round = 0; round < rounds; round++) {
                int proximoX = (int) Math.round(startX + step * (round + 1));

                List<Integer> proximosY = new ArrayList<>();

                for (int i = 0; i < atuaisY.size(); i += 2) {
                    int y1 = atuaisY.get(i);
                    int y2 = atuaisY.get(i + 1);
                    int meioY = (y1 + y2) / 2;

                    g2.drawLine(atualX, y1, proximoX, y1);
                    g2.drawLine(atualX, y2, proximoX, y2);
                    g2.drawLine(proximoX, y1, proximoX, y2);

                    proximosY.add(meioY);
                }

                atuaisY = proximosY;
                atualX = proximoX;
            }

            if (!atuaisY.isEmpty()) {
                int yFinalLado = atuaisY.get(0);
                g2.drawLine(atualX, yFinalLado, finalEdgeX, yFinalLado);
            }
        }

        private void desenharFinal(Graphics2D g2, int x, int y) {
            g2.setColor(CAIXA);
            g2.fillRoundRect(x, y, FINAL_W, FINAL_H, 8, 8);

            g2.setColor(LINHA);
            g2.setStroke(new BasicStroke(2.4f));
            g2.drawRoundRect(x, y, FINAL_W, FINAL_H, 8, 8);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.setColor(TEXTO);

            String texto = "FINAL";
            FontMetrics fm = g2.getFontMetrics();

            int textoX = x + (FINAL_W - fm.stringWidth(texto)) / 2;
            int textoY = y + ((FINAL_H - fm.getHeight()) / 2) + fm.getAscent();

            g2.drawString(texto, textoX, textoY);
        }

        private void desenharEquipa(Graphics2D g2, String equipa, int x, int centroY, boolean direita) {
            int y = centroY - (BOX_H / 2);

            boolean bye = equipa.equalsIgnoreCase("BYE");

            g2.setColor(bye ? CAIXA_BYE : CAIXA);
            g2.fillRoundRect(x, y, BOX_W, BOX_H, 8, 8);

            g2.setColor(LINHA);
            g2.setStroke(new BasicStroke(2.2f));
            g2.drawRoundRect(x, y, BOX_W, BOX_H, 8, 8);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.setColor(TEXTO);

            String texto = limitarTexto(equipa, 20);
            FontMetrics fm = g2.getFontMetrics();

            int textoY = y + ((BOX_H - fm.getHeight()) / 2) + fm.getAscent();

            int textoX;

            if (direita) {
                textoX = x + BOX_W - fm.stringWidth(texto) - 16;
            } else {
                textoX = x + 16;
            }

            g2.drawString(texto, textoX, textoY);
        }

        private List<Integer> calcularYDasEquipas(int quantidade) {
            List<Integer> ys = new ArrayList<>();

            if (quantidade <= 1) {
                ys.add(getHeight() / 2);
                return ys;
            }

            int totalAltura = quantidade * BOX_H + (quantidade - 1) * GAP_Y;
            int inicioY = (getHeight() - totalAltura) / 2;

            for (int i = 0; i < quantidade; i++) {
                int y = inicioY + i * (BOX_H + GAP_Y) + BOX_H / 2;
                ys.add(y);
            }

            return ys;
        }

        private List<String> criarSlotsComBye() {
            int tamanhoBracket = proximaPotenciaDeDois(equipasOriginais.size());

            List<String> slots = new ArrayList<>(equipasOriginais);

            while (slots.size() < tamanhoBracket) {
                slots.add("BYE");
            }

            return slots;
        }

        private int calcularAltura(int equipasPorLado) {
            if (equipasPorLado <= 1) {
                return 270;
            }

            if (equipasPorLado == 2) {
                return 340;
            }

            int altura = 100 + equipasPorLado * BOX_H + (equipasPorLado - 1) * GAP_Y;

            return Math.max(430, altura);
        }

        private int calcularLargura(int tamanhoBracket) {
            if (tamanhoBracket <= 2) {
                return 720;
            }

            if (tamanhoBracket == 4) {
                return 780;
            }

            if (tamanhoBracket == 8) {
                return 950;
            }

            if (tamanhoBracket == 16) {
                return 1180;
            }

            return 1350;
        }

        private int proximaPotenciaDeDois(int numero) {
            int potencia = 1;

            while (potencia < numero) {
                potencia *= 2;
            }

            return Math.max(2, potencia);
        }

        private int log2(int numero) {
            int resultado = 0;

            while (numero > 1) {
                numero = numero / 2;
                resultado++;
            }

            return resultado;
        }

        private String limitarTexto(String texto, int limite) {
            if (texto == null) {
                return "";
            }

            if (texto.length() <= limite) {
                return texto;
            }

            return texto.substring(0, limite - 3) + "...";
        }
    }
}