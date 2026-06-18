package Frames;

import Design.MenuLateral;
import GrupoEeleminatoria.CampeonatoRepositorio;
import GrupoEeleminatoria.GruposFrame;
import GrupoEeleminatoria.NovoCampeonatoFrame;
import Models.Campeonato;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CampeonatosFrame extends JFrame {

    private JTable tabelaCampeonatos;
    private DefaultTableModel modelo;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(120, 130, 150);
    private final Color BLUE = new Color(37, 99, 235);

    public CampeonatosFrame() {
        setTitle("Campeonatos");
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        JPanel pagina = criarPagina(menuLateral);

        add(menuLateral, BorderLayout.WEST);
        add(pagina, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarPagina(JPanel menuLateral) {
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(BG);
        pagina.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);

        JButton botaoMenu = criarBotaoMenu(menuLateral);
        barraSuperior.add(botaoMenu, BorderLayout.WEST);

        pagina.add(barraSuperior, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(10, 90, 20, 90));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Campeonatos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXT);

        JLabel subtitulo = new JLabel("Gestão de campeonatos, equipas, estádios, grupos e estado.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton btnNovo = criarBotaoAzul("+ Novo Campeonato");
        btnNovo.addActionListener(e -> {
            dispose();
            new NovoCampeonatoFrame();
        });

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(btnNovo, BorderLayout.EAST);

        centro.add(cabecalho);
        centro.add(Box.createVerticalStrut(24));

        centro.add(criarCardsEstatisticas());
        centro.add(Box.createVerticalStrut(28));

        JPanel conteudo = new JPanel(new BorderLayout(35, 0));
        conteudo.setOpaque(false);
        conteudo.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(criarCardListaCampeonatos(), BorderLayout.CENTER);
        conteudo.add(criarCardRegras(), BorderLayout.EAST);

        centro.add(conteudo);

        pagina.add(centro, BorderLayout.CENTER);

        return pagina;
    }

    private JButton criarBotaoMenu(JPanel menuLateral) {
        JButton botaoMenu = new JButton("☰");

        botaoMenu.setFocusPainted(false);
        botaoMenu.setBorderPainted(false);
        botaoMenu.setContentAreaFilled(false);
        botaoMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        botaoMenu.setForeground(TEXT);
        botaoMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botaoMenu.addActionListener(e -> {
            menuLateral.setVisible(!menuLateral.isVisible());
            revalidate();
            repaint();
        });

        return botaoMenu;
    }

    private JPanel criarCardsEstatisticas() {
        JPanel estatisticas = new JPanel(new GridLayout(1, 4, 18, 0));
        estatisticas.setOpaque(false);
        estatisticas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        estatisticas.setAlignmentX(Component.LEFT_ALIGNMENT);

        int total = CampeonatoRepositorio.listar().size();
        int configuracao = 0;
        int iniciados = 0;
        int finalizados = 0;

        for (Campeonato campeonato : CampeonatoRepositorio.listar()) {
            if (campeonato.isEmConfiguracao()) {
                configuracao++;
            } else if (campeonato.isIniciado()) {
                iniciados++;
            } else if (campeonato.isFinalizado()) {
                finalizados++;
            }
        }

        estatisticas.add(criarCartaoEstatistica(
                "Campeonatos Totais",
                String.valueOf(total),
                BLUE,
                new Color(231, 240, 253)
        ));

        estatisticas.add(criarCartaoEstatistica(
                "Em Configuração",
                String.valueOf(configuracao),
                new Color(124, 58, 237),
                new Color(242, 235, 255)
        ));

        estatisticas.add(criarCartaoEstatistica(
                "Iniciados",
                String.valueOf(iniciados),
                new Color(22, 163, 74),
                new Color(232, 248, 238)
        ));

        estatisticas.add(criarCartaoEstatistica(
                "Finalizados",
                String.valueOf(finalizados),
                new Color(249, 115, 22),
                new Color(255, 243, 224)
        ));

        return estatisticas;
    }

    private JPanel criarCardListaCampeonatos() {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(760, 430));

        JLabel titulo = new JLabel("Lista de Campeonatos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(TEXT);

        String[] colunas = {
                "Campeonato",
                "Estado",
                "Equipas",
                "Estádios",
                "Grupos",
                "Fase"
        };

        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        carregarTabela();

        tabelaCampeonatos = new JTable(modelo);
        tabelaCampeonatos.setRowHeight(36);
        tabelaCampeonatos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaCampeonatos.setForeground(new Color(51, 65, 85));
        tabelaCampeonatos.setGridColor(new Color(226, 232, 240));
        tabelaCampeonatos.setShowVerticalLines(false);
        tabelaCampeonatos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelaCampeonatos.getTableHeader().setForeground(new Color(100, 116, 139));
        tabelaCampeonatos.getTableHeader().setBackground(Color.WHITE);

        tabelaCampeonatos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabelaCampeonatos.getSelectedRow();

                if (linha >= 0) {
                    String nomeCampeonato = tabelaCampeonatos.getValueAt(linha, 0).toString();
                    Campeonato campeonato = CampeonatoRepositorio.procurarPorNome(nomeCampeonato);

                    if (campeonato != null) {
                        dispose();
                        new GruposFrame(campeonato);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaCampeonatos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void carregarTabela() {
        modelo.setRowCount(0);

        for (Campeonato campeonato : CampeonatoRepositorio.listar()) {
            String equipas = campeonato.getEquipas().size()
                    + "/"
                    + campeonato.getNumeroEquipasNecessarias();

            String estadios = campeonato.getEstadios().size()
                    + "/"
                    + campeonato.getNumeroEstadiosNecessarios();

            String grupos = campeonato.isGruposGerados()
                    ? String.valueOf(campeonato.getGrupos().size())
                    : "Por gerar";

            String fase;

            if (campeonato.isFinalizado()) {
                fase = "Finalizado";
            } else if (campeonato.isIniciado()) {
                fase = "A decorrer";
            } else {
                fase = "Configuração";
            }

            modelo.addRow(new Object[]{
                    campeonato.getNome(),
                    campeonato.getEstado(),
                    equipas,
                    estadios,
                    grupos,
                    fase
            });
        }
    }

    private JPanel criarCardRegras() {
        JPanel card = new PainelArredondado(18, new Color(231, 240, 253));
        card.setPreferredSize(new Dimension(250, 430));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 20, 20, 20));

        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setOpaque(false);
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        texto.setForeground(TEXT);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);

        texto.setText("""
                Regras do Campeonato

                • O campeonato nasce em configuração.

                • Enquanto estiver em configuração,
                  podes adicionar equipas e estádios.

                • Só podes iniciar quando tiver
                  equipas e estádios suficientes.

                • Depois de iniciado, não podes
                  remover equipas nem estádios.
                """);

        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCartaoEstatistica(String titulo, String valor, Color corTexto, Color corFundo) {
        JPanel card = new PainelArredondado(18, corFundo);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 20, 14, 20));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelTitulo.setForeground(corTexto);

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 28));
        labelValor.setForeground(TEXT);

        card.add(labelTitulo);
        card.add(Box.createVerticalStrut(8));
        card.add(labelValor);

        return card;
    }

    private JButton criarBotaoAzul(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(BLUE);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(11, 20, 11, 20));

        return botao;
    }

    static class PainelArredondado extends JPanel {

        private final int raio;
        private final Color corFundo;

        public PainelArredondado(int raio, Color corFundo) {
            this.raio = raio;
            this.corFundo = corFundo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D desenho = (Graphics2D) g.create();

            desenho.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            desenho.setColor(new Color(0, 0, 0, 18));
            desenho.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.dispose();

            super.paintComponent(g);
        }
    }
}