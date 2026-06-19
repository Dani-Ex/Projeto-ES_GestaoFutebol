package Frames.seccaoEstadios;

import Design.MenuLateral;
import Frames.CampeonatosFrame;
import GrupoEeleminatoria.CampeonatoRepositorio;
import GrupoEeleminatoria.GruposFrame;
import Models.Campeonato;
import Models.Estadio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EstadiosFrame extends JFrame {

    private Campeonato campeonato;

    private JTable tabelaEstadiosCampeonato;
    private JTable tabelaEstadiosExistentes;

    private final Color BG = new Color(245, 247, 251);
    private final Color TEXT = new Color(30, 41, 59);
    private final Color MUTED = new Color(100, 116, 139);
    private final Color BLUE = new Color(37, 99, 235);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color RED = new Color(220, 38, 38);

    public EstadiosFrame() {
        this(null);
    }

    public EstadiosFrame(Campeonato campeonato) {
        this.campeonato = campeonato;

        setTitle(campeonato == null ? "Estádios" : "Estádios - " + campeonato.getNome());
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(campeonato == null ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        MenuLateral menuLateral = new MenuLateral(this);
        menuLateral.setVisible(false);

        JPanel pagina = criarPagina(menuLateral);

        add(menuLateral, BorderLayout.WEST);
        add(pagina, BorderLayout.CENTER);

        carregarTabelas();

        setVisible(true);
    }

    private JPanel criarPagina(JPanel menuLateral) {
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(BG);
        pagina.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barraSuperior.setOpaque(false);
        barraSuperior.add(criarBotaoMenu(menuLateral));
        pagina.add(barraSuperior, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(new EmptyBorder(15, 70, 20, 70));

        JLabel titulo = new JLabel("Estádios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(TEXT);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String textoSubtitulo = campeonato == null
                ? "Consulta os estádios existentes no sistema."
                : "Associa estádios ao campeonato: " + campeonato.getNome();

        JLabel subtitulo = new JLabel(textoSubtitulo);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitulo.setForeground(MUTED);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(titulo);
        centro.add(Box.createVerticalStrut(4));
        centro.add(subtitulo);
        centro.add(Box.createVerticalStrut(22));

        centro.add(criarBarraAcoes());
        centro.add(Box.createVerticalStrut(22));

        JPanel conteudo = new JPanel(new GridLayout(1, 2, 24, 0));
        conteudo.setOpaque(false);
        conteudo.setAlignmentX(Component.LEFT_ALIGNMENT);
        conteudo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 480));

        conteudo.add(criarCardEstadiosCampeonato());
        conteudo.add(criarCardEstadiosExistentes());

        centro.add(conteudo);

        pagina.add(centro, BorderLayout.CENTER);

        return pagina;
    }

    private JPanel criarBarraAcoes() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        barra.setOpaque(false);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        barra.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JButton btnNovo = criarBotaoAzul("Novo Estádio");
        JButton btnAdicionarExistente = criarBotaoVerde("Adicionar Existente");
        JButton btnRemover = criarBotaoVermelho("Remover do Campeonato");
        JButton btnVoltar = criarBotaoCinza("Voltar");

        btnNovo.addActionListener(e -> abrirNovoEstadio());
        btnAdicionarExistente.addActionListener(e -> adicionarEstadioExistente());
        btnRemover.addActionListener(e -> removerEstadioDoCampeonato());

        btnVoltar.addActionListener(e -> dispose());

        barra.add(btnNovo);
        barra.add(btnAdicionarExistente);
        barra.add(btnRemover);
        barra.add(btnVoltar);

        return barra;
    }

    private JPanel criarCardEstadiosCampeonato() {
        JPanel card = criarCard("Estádios do Campeonato");

        tabelaEstadiosCampeonato = criarTabela();
        JScrollPane scroll = new JScrollPane(tabelaEstadiosCampeonato);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCardEstadiosExistentes() {
        JPanel card = criarCard("Estádios Existentes");

        tabelaEstadiosExistentes = criarTabela();
        JScrollPane scroll = new JScrollPane(tabelaEstadiosExistentes);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarCard(String tituloTexto) {
        JPanel card = new PainelArredondado(18, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel(tituloTexto);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(TEXT);
        titulo.setBorder(new EmptyBorder(0, 0, 15, 0));

        card.add(titulo, BorderLayout.NORTH);

        return card;
    }

    private JTable criarTabela() {
        String[] colunas = {
                "Nome",
                "Cidade",
                "Proprietário",
                "Normal",
                "VIP",
                "Premium",
                "Total"
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

        return tabela;
    }

    private void carregarTabelas() {
        carregarEstadiosCampeonato();
        carregarEstadiosExistentes();
    }

    private void carregarEstadiosCampeonato() {
        DefaultTableModel modelo = (DefaultTableModel) tabelaEstadiosCampeonato.getModel();
        modelo.setRowCount(0);

        if (campeonato == null) {
            return;
        }

        for (Estadio estadio : campeonato.getEstadios()) {
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

    private void carregarEstadiosExistentes() {
        DefaultTableModel modelo = (DefaultTableModel) tabelaEstadiosExistentes.getModel();
        modelo.setRowCount(0);

        List<Estadio> estadiosExistentes = CampeonatoRepositorio.listarEstadiosExistentes();

        for (Estadio estadio : estadiosExistentes) {
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

    private void abrirNovoEstadio() {
        if (!validarCampeonatoAberto()) {
            return;
        }

        if (campeonato.getEstadios().size() >= campeonato.getNumeroEstadiosNecessarios()) {
            mostrarErro("Este campeonato já atingiu o número máximo de estádios permitido.");
            return;
        }

        new NovoEstadioFrame(campeonato, this::carregarTabelas);
    }

    private void adicionarEstadioExistente() {
        if (!validarCampeonatoAberto()) {
            return;
        }

        if (campeonato.getEstadios().size() >= campeonato.getNumeroEstadiosNecessarios()) {
            mostrarErro("Este campeonato já atingiu o número máximo de estádios permitido.");
            return;
        }

        int linha = tabelaEstadiosExistentes.getSelectedRow();

        if (linha == -1) {
            mostrarErro("Seleciona um estádio existente na tabela da direita.");
            return;
        }

        String nomeEstadio = tabelaEstadiosExistentes.getValueAt(linha, 0).toString();

        if (campeonato.existeEstadioComNome(nomeEstadio)) {
            mostrarErro("Esse estádio já está associado a este campeonato.");
            return;
        }

        Estadio estadioEscolhido = procurarEstadioExistentePorNome(nomeEstadio);

        if (estadioEscolhido == null) {
            mostrarErro("Não foi possível encontrar o estádio selecionado.");
            return;
        }

        Estadio copia = new Estadio(
                estadioEscolhido.getNome(),
                estadioEscolhido.getCidade(),
                estadioEscolhido.getProprietario(),
                estadioEscolhido.getLugaresNormal(),
                estadioEscolhido.getLugaresVip(),
                estadioEscolhido.getLugaresPremium()
        );

        boolean adicionado = campeonato.adicionarEstadio(copia);

        if (!adicionado) {
            mostrarErro("Não foi possível adicionar o estádio ao campeonato.");
            return;
        }

        CampeonatoRepositorio.salvar();
        carregarTabelas();

        JOptionPane.showMessageDialog(
                this,
                "Estádio existente adicionado ao campeonato.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private Estadio procurarEstadioExistentePorNome(String nome) {
        List<Estadio> estadiosExistentes = CampeonatoRepositorio.listarEstadiosExistentes();

        for (Estadio estadio : estadiosExistentes) {
            if (estadio.getNome().equalsIgnoreCase(nome)) {
                return estadio;
            }
        }

        return null;
    }

    private void removerEstadioDoCampeonato() {
        if (!validarCampeonatoAberto()) {
            return;
        }

        int linha = tabelaEstadiosCampeonato.getSelectedRow();

        if (linha == -1) {
            mostrarErro("Seleciona um estádio do campeonato para remover.");
            return;
        }

        String nomeEstadio = tabelaEstadiosCampeonato.getValueAt(linha, 0).toString();

        Estadio estadioParaRemover = null;

        for (Estadio estadio : campeonato.getEstadios()) {
            if (estadio.getNome().equalsIgnoreCase(nomeEstadio)) {
                estadioParaRemover = estadio;
                break;
            }
        }

        if (estadioParaRemover == null) {
            mostrarErro("Não foi possível encontrar o estádio selecionado.");
            return;
        }

        boolean removido = campeonato.removerEstadio(estadioParaRemover);

        if (!removido) {
            mostrarErro("Não foi possível remover o estádio.\nVerifica se o campeonato ainda está em configuração.");
            return;
        }

        CampeonatoRepositorio.salvar();
        carregarTabelas();

        JOptionPane.showMessageDialog(
                this,
                "Estádio removido do campeonato.",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private boolean validarCampeonatoAberto() {
        if (campeonato == null) {
            mostrarErro("Abre esta página através de um campeonato para poderes associar estádios.");
            return false;
        }

        if (!campeonato.isEmConfiguracao()) {
            mostrarErro("Não é possível alterar estádios depois do campeonato iniciado.");
            return false;
        }

        if (campeonato.isGruposGerados()) {
            mostrarErro("Não é possível alterar estádios depois dos grupos serem gerados.");
            return false;
        }

        return true;
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private JButton criarBotaoMenu(JPanel menuLateral) {
        JButton botao = new JButton("=");
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(false);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 24));
        botao.setForeground(TEXT);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.addActionListener(e -> {
            menuLateral.setVisible(!menuLateral.isVisible());
            revalidate();
            repaint();
        });

        return botao;
    }

    private JButton criarBotaoAzul(String texto) {
        return criarBotao(texto, BLUE, Color.WHITE);
    }

    private JButton criarBotaoVerde(String texto) {
        return criarBotao(texto, GREEN, Color.WHITE);
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

            desenho.setColor(new Color(0, 0, 0, 14));
            desenho.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.setColor(corFundo);
            desenho.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, raio, raio);

            desenho.dispose();

            super.paintComponent(g);
        }
    }
}
