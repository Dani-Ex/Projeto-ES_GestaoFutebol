package Frames.seccaoEstadios;

import Design.MenuLateral;
import Frames.CampeonatosFrame;
import GrupoEeleminatoria.CampeonatoRepositorio;
import Models.Campeonato;
import Models.Estadio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EstadiosFrame extends JFrame {

    private JTable tabelaEstadios;
    private final List<AssociacaoEstadio> associacoesVisiveis = new ArrayList<>();

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color ORANGE = new Color(249, 115, 22);
    private final Color RED = new Color(220, 38, 38);

    public EstadiosFrame() {
        inicializar();
    }

    /* Mantém compatibilidade com chamadas antigas: new EstadiosFrame(campeonato). */
    public EstadiosFrame(Campeonato campeonatoIgnorado) {
        inicializar();
    }

    private void inicializar() {
        setTitle("Estádios");
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        add(menuLateral, BorderLayout.WEST);
        add(criarPagina(menuLateral), BorderLayout.CENTER);

        carregarTabela();
        setVisible(true);
    }

    private JPanel criarPagina(JPanel menuLateral) {
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(BG);
        pagina.setBorder(new EmptyBorder(22, 24, 22, 24));

        pagina.add(criarTopo(menuLateral), BorderLayout.NORTH);
        pagina.add(criarConteudo(), BorderLayout.CENTER);

        return pagina;
    }

    private JPanel criarTopo(JPanel menuLateral) {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);

        JButton btnMenu = new JButton("☰");
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        btnMenu.setContentAreaFilled(false);
        btnMenu.setFont(new Font("Segoe UI", Font.BOLD, 24));
        btnMenu.setForeground(TEXT);
        btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenu.addActionListener(e -> {
            menuLateral.setVisible(!menuLateral.isVisible());
            revalidate();
            repaint();
        });

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Estádios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT);

        JLabel subtitulo = new JLabel("Consulta, cria, associa, edita e elimina estádios antes de o campeonato iniciar.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitulo.setForeground(MUTED);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton btnVoltar = criarBotaoCinza("Campeonatos");
        btnVoltar.addActionListener(e -> {
            dispose();
            new CampeonatosFrame();
        });

        topo.add(btnMenu, BorderLayout.WEST);
        topo.add(textos, BorderLayout.CENTER);
        topo.add(btnVoltar, BorderLayout.EAST);
        return topo;
    }

    private JPanel criarConteudo() {
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(28, 70, 20, 70));

        centro.add(criarBarraAcoes());
        centro.add(Box.createVerticalStrut(20));
        centro.add(criarCardTabela());

        return centro;
    }

    private JPanel criarBarraAcoes() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        barra.setOpaque(false);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton btnNovo = criarBotaoAzul("Novo Estádio");
        JButton btnAssociar = criarBotaoVerde("Associar Existente");
        JButton btnEditar = criarBotaoLaranja("Editar Estádio");
        JButton btnEliminar = criarBotaoVermelho("Eliminar Estádio");

        btnNovo.addActionListener(e -> abrirNovoEstadio());
        btnAssociar.addActionListener(e -> associarEstadioExistente());
        btnEditar.addActionListener(e -> editarEstadioSelecionado());
        btnEliminar.addActionListener(e -> eliminarEstadioSelecionado());

        barra.add(btnNovo);
        barra.add(btnAssociar);
        barra.add(btnEditar);
        barra.add(btnEliminar);
        return barra;
    }

    private JPanel criarCardTabela() {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 540));

        JLabel titulo = new JLabel("Estádios e Associações");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT);
        titulo.setBorder(new EmptyBorder(0, 0, 16, 0));

        tabelaEstadios = criarTabela();
        JScrollPane scroll = new JScrollPane(tabelaEstadios);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(titulo, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JTable criarTabela() {
        String[] colunas = {
                "Estádio", "Cidade", "Proprietário", "Normal", "VIP", "Premium",
                "Total", "Campeonato", "Estado"
        };

        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(34);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setForeground(TEXT);
        tabela.setGridColor(new Color(226, 232, 240));
        tabela.setShowVerticalLines(false);
        tabela.setSelectionBackground(new Color(226, 232, 240));
        tabela.setSelectionForeground(TEXT);

        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setForeground(MUTED);
        tabela.getTableHeader().setBackground(Color.WHITE);
        tabela.getTableHeader().setReorderingAllowed(false);

        tabela.getColumnModel().getColumn(0).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(65);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(55);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(75);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(7).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(8).setPreferredWidth(105);

        return tabela;
    }

    private void carregarTabela() {
        if (tabelaEstadios == null) {
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) tabelaEstadios.getModel();
        modelo.setRowCount(0);
        associacoesVisiveis.clear();

        for (Campeonato campeonato : CampeonatoRepositorio.listar()) {
            for (Estadio estadio : campeonato.getEstadios()) {
                associacoesVisiveis.add(new AssociacaoEstadio(campeonato, estadio));

                modelo.addRow(new Object[]{
                        estadio.getNome(),
                        estadio.getCidade(),
                        estadio.getProprietario(),
                        estadio.getLugaresNormal(),
                        estadio.getLugaresVip(),
                        estadio.getLugaresPremium(),
                        estadio.getCapacidadeTotal(),
                        campeonato.getNome(),
                        campeonato.getEstado()
                });
            }
        }
    }

    private void abrirNovoEstadio() {
        if (CampeonatoRepositorio.listar().isEmpty()) {
            mostrarErro("Ainda não existe nenhum campeonato criado.");
            return;
        }

        dispose();
        new NovoEstadioFrame();
    }

    private void associarEstadioExistente() {
        AssociacaoEstadio associacao = obterAssociacaoSelecionada();

        if (associacao == null) {
            mostrarErro("Seleciona primeiro um estádio na tabela.");
            return;
        }

        Campeonato campeonatoDestino = escolherCampeonato();

        if (campeonatoDestino == null) {
            return;
        }

        if (!validarCampeonatoParaEstadio(campeonatoDestino)) {
            return;
        }

        if (campeonatoDestino.existeEstadioComNome(associacao.estadio.getNome())) {
            mostrarErro("Este estádio já está associado ao campeonato selecionado.");
            return;
        }

        String bloqueioReserva =
                CampeonatoRepositorio.motivoBloqueioReservaEstadio(
                        associacao.estadio.getNome(),
                        campeonatoDestino
                );

        if (!bloqueioReserva.isEmpty()) {
            mostrarErro(bloqueioReserva);
            return;
        }

        if (!campeonatoDestino.adicionarEstadio(copiarEstadio(associacao.estadio))) {
            mostrarErro("Não foi possível associar o estádio ao campeonato.");
            return;
        }

        CampeonatoRepositorio.salvar();
        carregarTabela();

        JOptionPane.showMessageDialog(
                this,
                "Estádio associado ao campeonato " + campeonatoDestino.getNome() + " com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private void eliminarEstadioSelecionado() {
        AssociacaoEstadio associacao = obterAssociacaoSelecionada();

        if (associacao == null) {
            mostrarErro("Seleciona um estádio na tabela para eliminar.");
            return;
        }

        String nomeEstadio = associacao.estadio.getNome();

        String bloqueio = CampeonatoRepositorio.motivoBloqueioEliminacaoEstadio(
                nomeEstadio
        );

        if (!bloqueio.isEmpty()) {
            mostrarErro(bloqueio);
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Queres mesmo eliminar o estádio \""
                        + nomeEstadio
                        + "\"?\n\n"
                        + "O estádio será removido do sistema e de todos os "
                        + "campeonatos que ainda estejam em configuração.",
                "Eliminar Estádio",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        if (!CampeonatoRepositorio.eliminarEstadio(nomeEstadio)) {
            mostrarErro(
                    "Não foi possível eliminar o estádio. "
                            + "Confirma que nenhum campeonato associado foi iniciado."
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Estádio eliminado com sucesso.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );

        carregarTabela();
    }

    private void editarEstadioSelecionado() {
        AssociacaoEstadio associacao = obterAssociacaoSelecionada();

        if (associacao == null) {
            mostrarErro("Seleciona um estádio na tabela para editar.");
            return;
        }

        String bloqueio = CampeonatoRepositorio.motivoBloqueioEdicaoEstadio(
                associacao.estadio.getNome()
        );

        if (!bloqueio.isEmpty()) {
            mostrarErro(bloqueio);
            return;
        }

        dispose();
        new EditarEstadioFrame(associacao.estadio.getNome());
    }

    private AssociacaoEstadio obterAssociacaoSelecionada() {
        int linha = tabelaEstadios.getSelectedRow();

        if (linha < 0 || linha >= associacoesVisiveis.size()) {
            return null;
        }

        return associacoesVisiveis.get(linha);
    }

    private Campeonato escolherCampeonato() {
        List<Campeonato> campeonatos = CampeonatoRepositorio.listar();

        if (campeonatos.isEmpty()) {
            mostrarErro("Ainda não existe nenhum campeonato criado.");
            return null;
        }

        JComboBox<Campeonato> combo = new JComboBox<>();

        for (Campeonato campeonato : campeonatos) {
            combo.addItem(campeonato);
        }

        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus
            ) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                if (value instanceof Campeonato campeonato) {
                    label.setText(campeonato.getNome() + " | "
                            + campeonato.getEstadios().size() + "/"
                            + campeonato.getNumeroEstadiosNecessarios() + " estádios");
                }

                return label;
            }
        });

        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.add(new JLabel("Seleciona o campeonato para associar o estádio:"), BorderLayout.NORTH);
        painel.add(combo, BorderLayout.CENTER);

        int resultado = JOptionPane.showConfirmDialog(
                this, painel, "Associar Estádio",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        return resultado == JOptionPane.OK_OPTION
                ? (Campeonato) combo.getSelectedItem()
                : null;
    }

    private boolean validarCampeonatoParaEstadio(Campeonato campeonato) {
        if (!campeonato.isEmConfiguracao() || campeonato.isGruposGerados()) {
            mostrarErro("Não é possível alterar estádios depois de o campeonato iniciar ou gerar os grupos.");
            return false;
        }

        if (campeonato.getEstadios().size() >= campeonato.getNumeroEstadiosNecessarios()) {
            mostrarErro("O campeonato selecionado já atingiu o número máximo de estádios.");
            return false;
        }

        return true;
    }

    private Estadio copiarEstadio(Estadio estadio) {
        return new Estadio(
                estadio.getNome(), estadio.getCidade(), estadio.getProprietario(),
                estadio.getLugaresNormal(), estadio.getLugaresVip(), estadio.getLugaresPremium()
        );
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private JButton criarBotaoAzul(String texto) {
        return criarBotao(texto, BLUE, Color.WHITE);
    }

    private JButton criarBotaoVerde(String texto) {
        return criarBotao(texto, GREEN, Color.WHITE);
    }

    private JButton criarBotaoLaranja(String texto) {
        return criarBotao(texto, ORANGE, Color.WHITE);
    }

    private JButton criarBotaoVermelho(String texto) {
        return criarBotao(texto, RED, Color.WHITE);
    }

    private JButton criarBotaoCinza(String texto) {
        return criarBotao(texto, new Color(241, 245, 249), TEXT);
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setBackground(fundo);
        botao.setForeground(corTexto);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.setBorder(new EmptyBorder(10, 16, 10, 16));
        return botao;
    }

    private static class AssociacaoEstadio {
        private final Campeonato campeonato;
        private final Estadio estadio;

        private AssociacaoEstadio(Campeonato campeonato, Estadio estadio) {
            this.campeonato = campeonato;
            this.estadio = estadio;
        }
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
