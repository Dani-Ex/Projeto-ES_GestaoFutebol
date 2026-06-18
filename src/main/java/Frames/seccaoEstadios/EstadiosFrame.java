package Frames.seccaoEstadios;

import Design.MenuLateral;
import Models.Campeonato;
import Models.Estadio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class EstadiosFrame extends JFrame {

    private Campeonato campeonato;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JComboBox<String> filtroCidade;
    private JComboBox<String> filtroOrdem;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color RED = new Color(239, 68, 68);
    private final Color CARD_REGRA = new Color(255, 241, 217);

    public EstadiosFrame(Campeonato campeonato) {
        this.campeonato = campeonato;

        setTitle("Estádios - " + campeonato.getNome());
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

        JButton botaoMenu = criarBotaoMenu(menuLateral);
        pagina.add(botaoMenu, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(20, 90, 20, 90));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Estádios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXT);

        JLabel subtitulo = new JLabel("Gestão dos estádios do campeonato: " + campeonato.getNome());
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(MUTED);

        textos.add(titulo);
        textos.add(subtitulo);

        JButton btnNovo = criarBotaoAzul("+ Novo Estádio");
        btnNovo.addActionListener(e -> {
            dispose();
            new NovoEstadioFrame(campeonato);
        });

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(btnNovo, BorderLayout.EAST);

        centro.add(cabecalho);
        centro.add(Box.createVerticalStrut(18));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filtros.setOpaque(false);
        filtros.setAlignmentX(Component.LEFT_ALIGNMENT);
        filtros.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        filtroCidade = new JComboBox<>();
        filtroCidade.setPreferredSize(new Dimension(160, 32));

        filtroOrdem = new JComboBox<>(new String[]{
                "Ordem Capacidade",
                "Capacidade ↑",
                "Capacidade ↓"
        });
        filtroOrdem.setPreferredSize(new Dimension(170, 32));

        atualizarFiltroCidades();

        filtroCidade.addActionListener(e -> atualizarTabela());
        filtroOrdem.addActionListener(e -> atualizarTabela());

        filtros.add(filtroCidade);
        filtros.add(filtroOrdem);

        centro.add(filtros);
        centro.add(Box.createVerticalStrut(10));

        JPanel conteudo = new JPanel(new BorderLayout(35, 0));
        conteudo.setOpaque(false);
        conteudo.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(criarCardTabela(), BorderLayout.CENTER);
        conteudo.add(criarCardRegra(), BorderLayout.EAST);

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

    private JPanel criarCardTabela() {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(720, 430));

        JLabel titulo = new JLabel("Lista de Estádios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(TEXT);

        String[] colunas = {
                "Nome",
                "Cidade",
                "Proprietário",
                "Normal",
                "VIP",
                "Premium",
                "Capacidade"
        };

        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        tabela.setRowHeight(36);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setForeground(new Color(51, 65, 85));
        tabela.setGridColor(new Color(226, 232, 240));
        tabela.setShowVerticalLines(false);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setForeground(MUTED);
        tabela.getTableHeader().setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        JButton btnRemover = criarBotaoVermelho("Remover Estádio Selecionado");
        btnRemover.addActionListener(e -> removerEstadioSelecionado());

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setOpaque(false);
        rodape.add(btnRemover);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);

        atualizarTabela();

        return card;
    }

    private JPanel criarCardRegra() {
        JPanel card = new PainelArredondado(18, CARD_REGRA);
        card.setPreferredSize(new Dimension(250, 430));
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
                Regra dos Estádios

                Após o início do campeonato,
                estádios não podem ser removidos.

                Apenas podem ser inativados
                após o término.
                """);

        card.add(texto, BorderLayout.CENTER);

        return card;
    }

    private void atualizarFiltroCidades() {
        filtroCidade.removeAllItems();
        filtroCidade.addItem("Todas as Cidades");

        for (Estadio estadio : campeonato.getEstadios()) {
            boolean existe = false;

            for (int i = 0; i < filtroCidade.getItemCount(); i++) {
                if (filtroCidade.getItemAt(i).equalsIgnoreCase(estadio.getCidade())) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                filtroCidade.addItem(estadio.getCidade());
            }
        }
    }

    private void atualizarTabela() {
        modelo.setRowCount(0);

        ArrayList<Estadio> lista = new ArrayList<>(campeonato.getEstadios());

        String cidadeSelecionada = filtroCidade.getSelectedItem() == null
                ? "Todas as Cidades"
                : filtroCidade.getSelectedItem().toString();

        if (!cidadeSelecionada.equals("Todas as Cidades")) {
            lista.removeIf(estadio -> !estadio.getCidade().equalsIgnoreCase(cidadeSelecionada));
        }

        String ordem = filtroOrdem.getSelectedItem() == null
                ? "Ordem Capacidade"
                : filtroOrdem.getSelectedItem().toString();

        if (ordem.equals("Capacidade ↑")) {
            lista.sort(Comparator.comparingInt(Estadio::getCapacidadeTotal));
        } else if (ordem.equals("Capacidade ↓")) {
            lista.sort(Comparator.comparingInt(Estadio::getCapacidadeTotal).reversed());
        }

        for (Estadio estadio : lista) {
            modelo.addRow(new Object[]{
                    estadio.getNome(),
                    estadio.getCidade(),
                    estadio.getProprietario(),
                    estadio.getLugaresNormal(),
                    estadio.getLugaresVip(),
                    estadio.getLugaresPremium(),
                    estadio.getCapacidadeTotal()
            });
        }
    }

    private void removerEstadioSelecionado() {
        if (!campeonato.isEmConfiguracao()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não é possível remover estádios depois do campeonato iniciado.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Seleciona um estádio para remover.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String nome = tabela.getValueAt(linha, 0).toString();

        Estadio estadioParaRemover = null;

        for (Estadio estadio : campeonato.getEstadios()) {
            if (estadio.getNome().equalsIgnoreCase(nome)) {
                estadioParaRemover = estadio;
                break;
            }
        }

        if (estadioParaRemover == null) {
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Tens a certeza que queres remover o estádio " + nome + "?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta == JOptionPane.YES_OPTION) {
            boolean removido = campeonato.removerEstadio(estadioParaRemover);

            if (!removido) {
                JOptionPane.showMessageDialog(
                        this,
                        "Não foi possível remover o estádio.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            atualizarFiltroCidades();
            atualizarTabela();
        }
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

    private JButton criarBotaoVermelho(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(RED);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 16, 10, 16));
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
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            desenho.setColor(new Color(0, 0, 0, 14));
            desenho.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.dispose();

            super.paintComponent(g);
        }
    }
}