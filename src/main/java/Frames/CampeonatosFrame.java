package Frames;

import Design.MenuLateral;
import Models.CampeonatoRepositorio;
import Frames.SeccaoGrupoEeleminatoria.EditarCampeonatoFrame;
import Frames.SeccaoGrupoEeleminatoria.GruposFrame;
import Frames.SeccaoGrupoEeleminatoria.NovoCampeonatoFrame;
import Models.Campeonato;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CampeonatosFrame extends JFrame {

    private JTable tabelaCampeonatos;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(120, 130, 150);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color ORANGE = new Color(249, 115, 22);
    private final Color PURPLE = new Color(124, 58, 237);
    private final Color RED = new Color(220, 38, 38);

    public CampeonatosFrame() {
        setTitle("Campeonatos");
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        add(menuLateral, BorderLayout.WEST);
        add(criarPagina(menuLateral), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel criarPagina(JPanel menuLateral) {
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(BG);
        pagina.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);

        JButton botaoMenu = new JButton("=");
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

        barraSuperior.add(botaoMenu, BorderLayout.WEST);
        pagina.add(barraSuperior, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(10, 90, 20, 90));

        centro.add(criarCabecalho());
        centro.add(Box.createVerticalStrut(24));
        centro.add(criarEstatisticas());
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

    private JPanel criarCabecalho() {
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

        JLabel subtitulo = new JLabel("Gestão de torneios, fases, grupos e estado do campeonato.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        JButton btnEditar = criarBotaoCinza("Editar Campeonato");
        btnEditar.addActionListener(e -> editarCampeonatoSelecionado());

        JButton btnEliminar = criarBotaoVermelho("Eliminar Campeonato");
        btnEliminar.addActionListener(e -> eliminarCampeonatoSelecionado());

        JButton btnNovo = criarBotaoAzul("+ Novo Campeonato");
        btnNovo.addActionListener(e -> {
            dispose();
            new NovoCampeonatoFrame();
        });

        botoes.add(btnEditar);
        botoes.add(btnEliminar);
        botoes.add(btnNovo);

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(botoes, BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel criarEstatisticas() {
        List<Campeonato> campeonatos = CampeonatoRepositorio.listar();
        int total = campeonatos.size();
        int emCurso = 0;
        int finalizados = 0;
        int equipas = 0;

        for (Campeonato campeonato : campeonatos) {
            CampeonatoRepositorio.sincronizarEquipasDoTsv(campeonato);
            equipas += campeonato.getEquipas().size();

            if (campeonato.isIniciado()) {
                emCurso++;
            }

            if (campeonato.isFinalizado()) {
                finalizados++;
            }
        }

        JPanel estatisticas = new JPanel(new GridLayout(1, 4, 18, 0));
        estatisticas.setOpaque(false);
        estatisticas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        estatisticas.setAlignmentX(Component.LEFT_ALIGNMENT);

        estatisticas.add(criarCartaoEstatistica("Campeonatos Totais", String.valueOf(total), BLUE, new Color(231, 240, 253)));
        estatisticas.add(criarCartaoEstatistica("A Decorrer", String.valueOf(emCurso), GREEN, new Color(232, 248, 238)));
        estatisticas.add(criarCartaoEstatistica("Finalizados", String.valueOf(finalizados), ORANGE, new Color(255, 243, 224)));
        estatisticas.add(criarCartaoEstatistica("Equipas Participantes", String.valueOf(equipas), PURPLE, new Color(242, 235, 255)));

        return estatisticas;
    }

    private JPanel criarCardListaCampeonatos() {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(720, 430));

        JLabel titulo = new JLabel("Lista de Campeonatos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(TEXT);

        String[] colunas = {
                "Campeonato", "Início", "Fim", "Equipas", "Grupos", "Fase", "Estado"
        };

        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd MMM");

        for (Campeonato campeonato : CampeonatoRepositorio.listar()) {
            CampeonatoRepositorio.sincronizarEquipasDoTsv(campeonato);

            modelo.addRow(new Object[]{
                    campeonato.getNome(),
                    campeonato.getDataInicioCampeonato().format(formato),
                    campeonato.getDataFimCampeonato().format(formato),
                    campeonato.getEquipas().size() + "/" + campeonato.getNumeroEquipasNecessarias(),
                    campeonato.isGruposGerados() ? campeonato.getGrupos().size() : "-",
                    obterFase(campeonato),
                    campeonato.getEstado()
            });
        }

        tabelaCampeonatos = new JTable(modelo);
        tabelaCampeonatos.setRowHeight(34);
        tabelaCampeonatos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaCampeonatos.setForeground(new Color(51, 65, 85));
        tabelaCampeonatos.setGridColor(new Color(226, 232, 240));
        tabelaCampeonatos.setShowVerticalLines(false);
        tabelaCampeonatos.setSelectionBackground(new Color(226, 232, 240));
        tabelaCampeonatos.setSelectionForeground(TEXT);
        tabelaCampeonatos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabelaCampeonatos.getTableHeader().setForeground(new Color(100, 116, 139));
        tabelaCampeonatos.getTableHeader().setBackground(Color.WHITE);
        tabelaCampeonatos.getTableHeader().setReorderingAllowed(false);

        tabelaCampeonatos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }

                abrirCampeonatoSelecionado();
            }
        });

        JScrollPane scroll = new JScrollPane(tabelaCampeonatos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private String obterFase(Campeonato campeonato) {
        if (campeonato.isFaseGruposTerminada()) {
            return "Eliminatórias";
        }

        if (campeonato.isGruposGerados()) {
            return "Fase de grupos";
        }

        return "Configuração";
    }

    private void abrirCampeonatoSelecionado() {
        Campeonato campeonato = obterCampeonatoSelecionado();

        if (campeonato == null) {
            return;
        }

        dispose();
        new GruposFrame(campeonato);
    }

    private void editarCampeonatoSelecionado() {
        Campeonato campeonato = obterCampeonatoSelecionado();

        if (campeonato == null) {
            mostrarErro("Seleciona um campeonato na tabela.");
            return;
        }

        if (!campeonato.isEmConfiguracao() || campeonato.isGruposGerados()) {
            mostrarErro("Só podes editar o campeonato antes de iniciar e antes de gerar os grupos.");
            return;
        }

        dispose();
        new EditarCampeonatoFrame(campeonato);
    }


    private void eliminarCampeonatoSelecionado() {
        Campeonato campeonato = obterCampeonatoSelecionado();

        if (campeonato == null) {
            mostrarErro("Seleciona um campeonato na tabela.");
            return;
        }

        if (!campeonato.isEmConfiguracao() || campeonato.isGruposGerados()) {
            mostrarErro(
                    "Só podes eliminar um campeonato antes de ele iniciar "
                            + "e antes de gerar os grupos."
            );
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Queres mesmo eliminar o campeonato \""
                        + campeonato.getNome()
                        + "\"?\n\n"
                        + "As equipas e jogadores deixam de estar associados "
                        + "a este campeonato. Os jogos agendados também serão removidos.",
                "Eliminar Campeonato",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        if (!CampeonatoRepositorio.eliminarCampeonatoNaoIniciado(campeonato)) {
            mostrarErro(
                    "Não foi possível eliminar o campeonato. "
                            + "Confirma que ele ainda não foi iniciado."
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Campeonato eliminado com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
        new CampeonatosFrame();
    }

    private Campeonato obterCampeonatoSelecionado() {
        int linha = tabelaCampeonatos.getSelectedRow();

        if (linha < 0) {
            return null;
        }

        String nome = tabelaCampeonatos.getValueAt(linha, 0).toString();
        return CampeonatoRepositorio.procurarPorNome(nome);
    }

    private JPanel criarCardRegras() {
        JPanel card = new PainelArredondado(18, new Color(231, 240, 253));
        card.setPreferredSize(new Dimension(220, 430));
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

                • Equipas divididas por grupos

                • 1.º e 2.º lugar passam à fase mata-mata

                • O número de equipas deve ser múltiplo de 4

                • A edição fica bloqueada após iniciar o campeonato
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

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private JButton criarBotaoAzul(String texto) {
        return criarBotao(texto, BLUE, Color.WHITE);
    }

    private JButton criarBotaoCinza(String texto) {
        return criarBotao(texto, new Color(241, 245, 249), TEXT);
    }

    private JButton criarBotaoVermelho(String texto) {
        return criarBotao(texto, RED, Color.WHITE);
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(fundo);
        botao.setForeground(corTexto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(11, 18, 11, 18));
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
            desenho.setColor(new Color(0, 0, 0, 18));
            desenho.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, raio, raio);
            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, raio, raio);
            desenho.dispose();
            super.paintComponent(g);
        }
    }
}
