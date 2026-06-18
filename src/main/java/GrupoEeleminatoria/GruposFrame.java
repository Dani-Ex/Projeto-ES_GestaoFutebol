package GrupoEeleminatoria;

import Design.MenuLateral;
import Frames.CampeonatosFrame;
import Frames.SeccaoEquipas.EquipasFrame;
import Frames.seccaoEstadios.EstadiosFrame;
import Models.Campeonato;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GruposFrame extends JFrame {

    private final Campeonato campeonato;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color ORANGE = new Color(249, 115, 22);
    private final Color PURPLE = new Color(124, 58, 237);

    public GruposFrame(Campeonato campeonato) {
        this.campeonato = campeonato;

        setTitle("Campeonato - " + campeonato.getNome());
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
        centro.setBorder(new EmptyBorder(10, 70, 20, 70));

        JPanel cabecalho = criarCabecalho();
        centro.add(cabecalho);
        centro.add(Box.createVerticalStrut(20));

        centro.add(criarCardsResumo());
        centro.add(Box.createVerticalStrut(22));

        centro.add(criarBarraAcoes());
        centro.add(Box.createVerticalStrut(22));

        JPanel conteudo = new JPanel(new BorderLayout(30, 0));
        conteudo.setOpaque(false);
        conteudo.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(criarCardGrupos(), BorderLayout.CENTER);
        conteudo.add(criarCardEstado(), BorderLayout.EAST);

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

    private JPanel criarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(campeonato.getNome());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT);

        JLabel subtitulo = new JLabel("Estado: " + campeonato.getEstado());
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitulo.setForeground(MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton btnVoltar = criarBotaoCinza("Voltar aos Campeonatos");
        btnVoltar.addActionListener(e -> {
            dispose();
            new CampeonatosFrame();
        });

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(btnVoltar, BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel criarCardsResumo() {
        JPanel cards = new JPanel(new GridLayout(1, 4, 18, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);

        String equipas = campeonato.getEquipas().size()
                + "/"
                + campeonato.getNumeroEquipasNecessarias();

        String estadios = campeonato.getEstadios().size()
                + "/"
                + campeonato.getNumeroEstadiosNecessarios();

        String grupos = campeonato.isGruposGerados()
                ? String.valueOf(campeonato.getGrupos().size())
                : "Por gerar";

        cards.add(criarCartaoResumo(
                "Estado",
                campeonato.getEstado(),
                BLUE,
                new Color(231, 240, 253)
        ));

        cards.add(criarCartaoResumo(
                "Equipas",
                equipas,
                PURPLE,
                new Color(242, 235, 255)
        ));

        cards.add(criarCartaoResumo(
                "Estádios",
                estadios,
                GREEN,
                new Color(232, 248, 238)
        ));

        cards.add(criarCartaoResumo(
                "Grupos",
                grupos,
                ORANGE,
                new Color(255, 243, 224)
        ));

        return cards;
    }

    private JPanel criarCartaoResumo(String titulo, String valor, Color corTexto, Color corFundo) {
        JPanel card = new PainelArredondado(18, corFundo);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 20, 14, 20));

        JLabel labelTitulo = new JLabel(titulo);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelTitulo.setForeground(corTexto);

        JLabel labelValor = new JLabel(valor);
        labelValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelValor.setForeground(TEXT);

        card.add(labelTitulo);
        card.add(Box.createVerticalStrut(8));
        card.add(labelValor);

        return card;
    }

    private JPanel criarBarraAcoes() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        barra.setOpaque(false);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JButton btnEquipas = criarBotaoAzul("Equipas");
        btnEquipas.addActionListener(e -> abrirEquipas());

        JButton btnEstadios = criarBotaoAzul("Estádios");
        btnEstadios.addActionListener(e -> {
            dispose();
            new EstadiosFrame(campeonato);
        });

        JButton btnGerarGrupos = criarBotaoRoxo("Gerar Grupos");
        btnGerarGrupos.addActionListener(e -> gerarGrupos());

        JButton btnIniciar = criarBotaoVerde("Iniciar Campeonato");
        btnIniciar.addActionListener(e -> iniciarCampeonato());

        JButton btnEliminatorias = criarBotaoCinza("Eliminatórias");
        btnEliminatorias.addActionListener(e -> abrirEliminatorias());

        barra.add(btnEquipas);
        barra.add(btnEstadios);
        barra.add(btnGerarGrupos);
        barra.add(btnIniciar);
        barra.add(btnEliminatorias);

        return barra;
    }

    private JPanel criarCardGrupos() {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(760, 430));

        JLabel titulo = new JLabel("Grupos do Campeonato");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(TEXT);

        card.add(titulo, BorderLayout.NORTH);

        if (!campeonato.isGruposGerados() || campeonato.getGrupos().isEmpty()) {
            JTextArea vazio = new JTextArea();
            vazio.setEditable(false);
            vazio.setOpaque(false);
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            vazio.setForeground(MUTED);
            vazio.setLineWrap(true);
            vazio.setWrapStyleWord(true);

            vazio.setText("""
                    Ainda não existem grupos gerados.

                    Antes de iniciar o campeonato:
                    1. Adiciona as equipas necessárias.
                    2. Adiciona os estádios necessários.
                    3. Clica em "Gerar Grupos".
                    4. Depois clica em "Iniciar Campeonato".
                    """);

            card.add(vazio, BorderLayout.CENTER);
            return card;
        }

        JPanel painelGrupos = new JPanel();
        painelGrupos.setOpaque(false);
        painelGrupos.setLayout(new GridLayout(0, 2, 18, 18));

        for (Map.Entry<String, List<String>> entrada : campeonato.getGrupos().entrySet()) {
            painelGrupos.add(criarTabelaGrupo(entrada.getKey(), entrada.getValue()));
        }

        JScrollPane scroll = new JScrollPane(painelGrupos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarTabelaGrupo(String nomeGrupo, List<String> equipas) {
        JPanel painel = new PainelArredondado(16, new Color(248, 250, 252));
        painel.setLayout(new BorderLayout());
        painel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel(nomeGrupo);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(TEXT);

        String[] colunas = {"Equipa", "Pontos", "Golos"};

        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (String equipa : equipas) {
            modelo.addRow(new Object[]{
                    equipa,
                    0,
                    0
            });
        }

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setGridColor(new Color(226, 232, 240));
        tabela.setShowVerticalLines(false);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setForeground(MUTED);
        tabela.getTableHeader().setBackground(new Color(248, 250, 252));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(248, 250, 252));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarCardEstado() {
        JPanel card = new PainelArredondado(18, new Color(231, 240, 253));
        card.setPreferredSize(new Dimension(270, 430));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(24, 20, 20, 20));

        JTextArea texto = new JTextArea();
        texto.setEditable(false);
        texto.setOpaque(false);
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        texto.setForeground(TEXT);

        texto.setText("""
                Configuração do Campeonato

                Enquanto o campeonato estiver
                "Em configuração", podes:

                • Adicionar equipas
                • Adicionar estádios
                • Gerar grupos
                • Preparar o início

                Para iniciar, o sistema valida:
                • Equipas suficientes
                • Estádios suficientes
                • Grupos gerados
                """);

        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private void gerarGrupos() {
        if (!campeonato.isEmConfiguracao()) {
            mostrarErro("Não é possível gerar grupos depois do campeonato iniciado.");
            return;
        }

        if (campeonato.isGruposGerados()) {
            mostrarErro("Os grupos já foram gerados.");
            return;
        }

        if (!campeonato.temEquipasSuficientes()) {
            mostrarErro(
                    "Ainda faltam equipas.\nTens "
                            + campeonato.getEquipas().size()
                            + " de "
                            + campeonato.getNumeroEquipasNecessarias()
                            + " equipas necessárias."
            );
            return;
        }

        if (campeonato.getEquipas().size() % 4 != 0) {
            mostrarErro("Não é possível gerar grupos. O número de equipas tem de ser múltiplo de 4.");
            return;
        }

        List<String> equipasMisturadas = new ArrayList<>(campeonato.getEquipas());
        Collections.shuffle(equipasMisturadas);

        Map<String, List<String>> grupos = new LinkedHashMap<>();

        int numeroGrupo = 0;
        char letraGrupo = 'A';

        while (numeroGrupo < equipasMisturadas.size()) {
            List<String> equipasGrupo = new ArrayList<>();

            for (int i = 0; i < 4 && numeroGrupo < equipasMisturadas.size(); i++) {
                equipasGrupo.add(equipasMisturadas.get(numeroGrupo));
                numeroGrupo++;
            }

            grupos.put("Grupo " + letraGrupo, equipasGrupo);
            letraGrupo++;
        }

        campeonato.setGrupos(grupos);
        campeonato.setGruposGerados(true);

        JOptionPane.showMessageDialog(
                this,
                "Grupos gerados com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
        new GruposFrame(campeonato);
    }

    private void iniciarCampeonato() {
        if (!campeonato.isGruposGerados()) {
            mostrarErro("Primeiro tens de gerar os grupos antes de iniciar o campeonato.");
            return;
        }

        if (!campeonato.podeIniciarCampeonato()) {
            mostrarErro(campeonato.getMensagemBloqueioInicio());
            return;
        }

        boolean iniciado = campeonato.iniciarCampeonato();

        if (!iniciado) {
            mostrarErro("Não foi possível iniciar o campeonato.");
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Campeonato iniciado com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
        new GruposFrame(campeonato);
    }

    private void abrirEquipas() {
        try {
            Constructor<EquipasFrame> construtor = EquipasFrame.class.getConstructor(Campeonato.class);
            dispose();
            construtor.newInstance(campeonato);
        } catch (NoSuchMethodException e) {
            dispose();
            new EquipasFrame();
        } catch (Exception e) {
            mostrarErro("Não foi possível abrir a página de equipas.");
        }
    }

    private void abrirEliminatorias() {
        if (!campeonato.isIniciado()) {
            mostrarErro("Só podes abrir eliminatórias depois do campeonato iniciado.");
            return;
        }

        try {
            dispose();
            new EliminatoriasFrame(campeonato);
        } catch (Exception e) {
            mostrarErro("Não foi possível abrir as eliminatórias.");
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private JButton criarBotaoAzul(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(BLUE);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));

        return botao;
    }

    private JButton criarBotaoRoxo(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(PURPLE);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));

        return botao;
    }

    private JButton criarBotaoVerde(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(GREEN);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));

        return botao;
    }

    private JButton criarBotaoCinza(String texto) {
        JButton botao = new JButton(texto);

        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(new Color(241, 245, 249));
        botao.setForeground(TEXT);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));

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