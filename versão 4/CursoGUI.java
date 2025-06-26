
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class CursoGUI extends JFrame {
    private JTextField nomeCursoField, matriculaField, totalHorasCCCGField;
    private JTable tabelaDisciplinas, tabelaAtividades, tabelacccg;
    private final JTable tabelaRequisitos;
    private DefaultTableModel disciplinasModel, atividadesModel, cccgModel, requisitosModel;
    private TableModelListener disciplinasListener, atividadesListener, cccgListener;
    private JCheckBox[] checkboxesDisciplinas, checkboxesAtividades, checkboxesCCCG;
    private JCheckBox checkDisciplinas, checkAtividades, checkCCCG;
    private JPanel painelCheckbox, painelIntegral, painelCheckboxFixo, painelCheckboxPersonalizado;
    private JLabel progressoLabel, percentualLabel, progressoIntegralLabel;
    private Grafico painelGrafico;
    private JList<String> listaDisponiveis;
    private List<JCheckBox> checkboxesPersonalizados;
    private final MenuInicial menu;
    private final int height = 900;
    private final int width = 600;
    String[] colDisciplinas = {"Nome", "Semestre", "Carga Horária", "Nota", "Período", "Concluído"};
    String[] colExtras = {"Nome", "Semestre", "Horas Mínimas", "Horas Cumpridas", "Periodo", "Concluído"};
    String [] colCCCG = {"Nome","Semestre", "Carga Horária", "Horas Cumpridas", "Nota", "Período", "Concluído"};

    public CursoGUI() {
        setTitle("Sistema Acadêmico - Curso");
        setSize(height, width);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        disciplinasModel = new DefaultTableModel(colDisciplinas, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 5 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        atividadesModel = new DefaultTableModel(colExtras, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 5 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 5;
            }
        };

        cccgModel = new DefaultTableModel(colCCCG, 0){
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 6 ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 6;
            }
        };

        requisitosModel = new DefaultTableModel(new String[]{"ID","Nome da Atividade", "IDs dos Pré-Requisitos"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; 
            }
        };
        iniciarCheckboxesIntegracao();
        inicializarCheckboxesFixos();

        tabelaDisciplinas = new JTable(disciplinasModel);
        tabelaAtividades = new JTable(atividadesModel);
        tabelacccg = new JTable(cccgModel);
        tabelaRequisitos = new JTable(requisitosModel) {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                Point p = e.getPoint();
                int row = rowAtPoint(p);
                int col = columnAtPoint(p);

                if (row >= 0 && col == 2) { 
                    String ids = getValueAt(row, col).toString();
                    if (!ids.isEmpty()) {
                        String[] idArray = ids.split(";");
                        List<String> nomes = new ArrayList<>();
                        for (String idStr : idArray) {
                            try {
                                int id = Integer.parseInt(idStr.trim());
                                String nome = buscarNomePorID(id);
                                nomes.add(nome != null ? nome : "ID " + id + " não encontrado");
                            } catch (NumberFormatException ex) {
                                nomes.add("ID inválido: " + idStr.trim());
                            }
                        }
                        return "<html>" + String.join("<br>", nomes) + "</html>";
                    }
                }
                return null;
            }
        };

        tabelaDisciplinas.removeColumn(tabelaDisciplinas.getColumnModel().getColumn(tabelaDisciplinas.getColumnCount() - 1));
        tabelaAtividades.removeColumn(tabelaAtividades.getColumnModel().getColumn(tabelaAtividades.getColumnCount() - 1));
        tabelacccg.removeColumn(tabelacccg.getColumnModel().getColumn(tabelacccg.getColumnCount() - 1));

        tabelaDisciplinas.setDefaultRenderer(Object.class, new Padrao());
        tabelaAtividades.setDefaultRenderer(Object.class, new Padrao());
        tabelacccg.setDefaultRenderer(Object.class, new Padrao());
        tabelaRequisitos.setDefaultRenderer(Object.class, new Padrao());

        tabelaDisciplinas.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaDisciplinas.setRowHeight(24);
        tabelaAtividades.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaAtividades.setRowHeight(24);
        tabelacccg.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelacccg.setRowHeight(24);
        tabelaRequisitos.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaRequisitos.setRowHeight(24);
        iniciaListener();
        JTabbedPane abas = new JTabbedPane();
        abas.add("Configurar Curso", criarAbaCriarCurso());
        abas.add("Visualizar Pré-requisitos",criarAbaRequisitos());
        abas.add("Gerenciar Horas", criarAbaGerenciar());
        abas.add("Integralização", criarAbaIntegralizacao());
        
        add(abas);
        menu = new MenuInicial(this);
        menu.setVisible(true);
    }
    
    protected void carregarCursoDeArquivo(File arquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            atividadesModel.removeTableModelListener(atividadesListener);

            disciplinasModel.setRowCount(0);
            atividadesModel.setRowCount(0);
            cccgModel.setRowCount(0);
            requisitosModel.setRowCount(0);
            if (painelCheckbox == null) {
                painelCheckbox = new JPanel();
            }
            painelCheckbox.removeAll(); 
            if (painelCheckboxPersonalizado == null) {
                painelCheckboxPersonalizado = new JPanel();
            }
            painelCheckboxPersonalizado.removeAll();

            if (checkboxesPersonalizados == null) {
                checkboxesPersonalizados = new ArrayList<>();
            }
            checkboxesPersonalizados.clear();

            String nomeCurso = br.readLine();
            String matricula = br.readLine();
            String horasCCCG = br.readLine();
            if (nomeCurso != null) {
                nomeCursoField.setText(nomeCurso);
            }
            if (matricula != null) {
                matriculaField.setText(matricula);
            }
            if (horasCCCG != null){
                totalHorasCCCGField.setText(horasCCCG);
            }

            String linha;
            boolean lendoIntegralizacao = false;
            int id = 1;

            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()){
                     continue;
                }

                if (linha.equals("###")) {
                    lendoIntegralizacao = true;
                    continue;
                }

                if (!lendoIntegralizacao) {
                    String[] partes = linha.split(":", 2);
                    String dadosParte = partes[0].trim();
                    String requisitos = partes.length > 1 ? partes[1].trim() : "";

                    String[] dados = dadosParte.split(";");
                    if (dados.length >= 7) {
                        String tipo = dados[0];
                        String nome = dados[1];
                        String semestre = dados[2];
                        boolean concluido = Boolean.parseBoolean(dados[6]);

                        if ("Disciplina".equalsIgnoreCase(tipo)) {
                            String carga = dados[3];
                            String nota = dados[4];
                            String periodo = dados[5];

                            Object[] dadosDisciplina = {nome, semestre, carga, nota, periodo, concluido};
                            disciplinasModel.addRow(dadosDisciplina);
                            requisitosModel.addRow(new Object[]{id, nome, requisitos});
                            id++;
                        } else if ("Atividade Extra".equalsIgnoreCase(tipo)) {
                            String horasMinimas = dados[3].isEmpty() ? "0" : dados[3];
                            String horasCumpridas = dados[4].isEmpty() ? "0" : dados[4];
                            String periodo = dados[5];

                            Object[] dadosExtra = {nome, semestre, horasMinimas, horasCumpridas, periodo, concluido};
                            atividadesModel.addRow(dadosExtra);
                            requisitosModel.addRow(new Object[]{id, nome, requisitos});
                            id++;
                        } else if ("CCCG".equalsIgnoreCase(tipo)) {
                            String carga = dados[3];
                            String horasCumpridas = dados[4];
                            String nota = dados[5];
                            String periodo = dados[6];
                            concluido = Boolean.parseBoolean(dados[7]);

                            Object[] dadosCCCG = {nome, semestre, carga, horasCumpridas, nota, periodo, concluido};
                            cccgModel.addRow(dadosCCCG);
                            requisitosModel.addRow(new Object[]{id, nome, requisitos});
                            id++;
                        }
                    }
                } else {
                    String[] partes = linha.split(";");
                    if (partes.length >= 2) {
                        String nome = partes[0].trim();
                        boolean marcado = Boolean.parseBoolean(partes[1].trim());

                        JCheckBox check = new JCheckBox(nome, marcado);
                        checkboxesPersonalizados.add(check);
                        painelIntegral.add(check);
                    }
                }
            }

            atividadesModel.fireTableDataChanged();
            for (int i = 0; i < atividadesModel.getRowCount(); i++) {
                TableModelEvent evento = new TableModelEvent(atividadesModel, i, i, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);
                atividadesListener.tableChanged(evento);
            }
            sincronizarCheckboxes();
            atualizarCheckboxesFixos(); 

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir curso.");
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Arquivo inválido ou corrompido.");
        } finally {
            atividadesModel.addTableModelListener(atividadesListener);
        }
    }

    private JPanel criarAbaCriarCurso() {
        JPanel painel = new JPanel(new BorderLayout());

        JPanel camposAux = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel camposCurso = new JPanel(new GridLayout(2, 2, 10, 10));
        nomeCursoField = new JTextField(15);
        matriculaField = new JTextField(15);

        JLabel nomeCurso = new JLabel("Nome do Curso:");
        JLabel matricula = new JLabel("Matrícula:");
        nomeCurso.setFont(new Font("Arial", Font.PLAIN, 16));
        matricula.setFont(new Font("Arial", Font.PLAIN, 16));

        camposCurso.add(nomeCurso);
        camposCurso.add(nomeCursoField);
        camposCurso.add(matricula);
        camposCurso.add(matriculaField);
        camposAux.add(camposCurso);

        painel.add(camposAux, BorderLayout.NORTH);

        painel.add(new JScrollPane(tabelaDisciplinas));
        painel.add(new JScrollPane(tabelaAtividades));
        painel.add(new JScrollPane(tabelacccg));

        tabelaDisciplinas.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaDisciplinas.setRowHeight(24);

        tabelaAtividades.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaAtividades.setRowHeight(24);

        tabelacccg.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaAtividades.setRowHeight(24);

        JPanel painelTabelas = new JPanel(new GridLayout(1,2));
        JPanel painelTabelasAUX = new JPanel(new GridLayout(2,1));
        
        JPanel painelDisciplinas = new JPanel(new BorderLayout());
        JPanel painelAtividades = new JPanel(new BorderLayout());
        JPanel painelCCCG = new JPanel(new BorderLayout());

        JLabel labelDisciplinas = new JLabel("Componentes Curriculares Obrigatórios");
        JLabel labelAtividades = new JLabel("Atividades Curriculares Extras");
        JLabel labelCCCG = new JLabel("Componentes Curriculares Complementares Graduação");

        labelDisciplinas.setFont(new Font("Arial", Font.PLAIN, 16));
        labelAtividades.setFont(new Font("Arial", Font.PLAIN, 16));
        labelCCCG.setFont(new Font("Arial", Font.PLAIN, 16));

        labelDisciplinas.setHorizontalAlignment(SwingConstants.CENTER);
        labelAtividades.setHorizontalAlignment(SwingConstants.CENTER);
        labelCCCG.setHorizontalAlignment(SwingConstants.CENTER);

        tabelaDisciplinas.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaAtividades.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));
        tabelacccg.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 16));

        painelDisciplinas.add(labelDisciplinas, BorderLayout.NORTH);
        painelDisciplinas.add(new JScrollPane(tabelaDisciplinas), BorderLayout.CENTER);

        painelAtividades.add(labelAtividades, BorderLayout.NORTH);
        painelAtividades.add(new JScrollPane(tabelaAtividades), BorderLayout.CENTER);

        JPanel camposCCCG = new JPanel(new GridLayout(2, 1, 10, 10));
        JPanel camposCCCGAux = new JPanel(new GridLayout(1,2,10,10));
        camposCCCG.add(labelCCCG);
        totalHorasCCCGField = new JTextField(15);
        JLabel totalCCCG = new JLabel("Carga horária Total de CCCGs: ");
        totalCCCG.setFont(new Font("Arial", Font.PLAIN, 14));
        camposCCCGAux.add(totalCCCG);
        camposCCCGAux.add(totalHorasCCCGField);
        camposCCCG.add(camposCCCGAux);
        painelCCCG.add(camposCCCG,BorderLayout.NORTH);
        painelCCCG.add(new JScrollPane(tabelacccg),BorderLayout.CENTER);
        

        painelTabelas.add(painelDisciplinas);
        painelTabelasAUX.add(painelCCCG);
        painelTabelasAUX.add(painelAtividades);
        painelTabelas.add(painelTabelasAUX);

        painel.add(painelTabelas, BorderLayout.CENTER);

        JPanel botoes = new JPanel();
        JButton criarbtn = new JButton("Criar Novo Curso");
        JButton carregarbtn = new JButton("Importar Curso Existente");
        JButton addBtn = new JButton("Adicionar Atividade");
        JButton removeBtn = new JButton("Remover Selecionada");
        JButton salvarBtn = new JButton("Salvar Curso");

        criarbtn.addActionListener(e -> new Intro(this, () -> {
                reset();
            }));
        carregarbtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File arquivo = fileChooser.getSelectedFile();
                carregarCursoDeArquivo(arquivo);
            }
        });
        addBtn.addActionListener(e -> {
            String[] opcoes = {"Componente Curricular", "CCCG", "Atividade Curricular Extra"};
            int escolha = JOptionPane.showOptionDialog(
                this,
                "Qual tipo de atividade deseja adicionar?",
                "Tipo de Atividade",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
            );

            if (escolha == 0) {
                disciplinasModel.addRow(new Object[]{"", "", "0", "0,00", "", false});
            } else if (escolha == 1) {
                cccgModel.addRow(new Object []{"","","0","0","0,00","", false});
            } else if (escolha == 2) {
                atividadesModel.addRow(new Object[]{"", "", "0", "0", "", false});
            }
        });

        removeBtn.addActionListener(e -> {
            removerAtividade();
        });

        salvarBtn.addActionListener(e -> salvarCurso());

        botoes.add(criarbtn);
        botoes.add(carregarbtn);
        botoes.add(addBtn);
        botoes.add(removeBtn);
        botoes.add(salvarBtn);
        painel.add(botoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarAbaGerenciar() {
        JPanel painel = new JPanel(new BorderLayout());

        painelCheckbox.setLayout(new BoxLayout(painelCheckbox, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(painelCheckbox);

        painelGrafico = new Grafico();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, painelGrafico);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);

        painel.add(splitPane, BorderLayout.CENTER);

        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelLateral.setPreferredSize(new Dimension(width*5 / 12, 0));

        JButton calcular = new JButton("Calcular Progresso");
        calcular.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcular.addActionListener(e -> calcularProgresso());

        String textoInicial = "<html>"
                + "Progresso Total: 0h de 0h<br>"
                + "Percentual Geral: 0,0%<br>"
                + "<br><b>Média das Notas: 0,00</b><br>"
                + "<br><b>Atividades Extras: 0,0% (0h de 0h)</b><br>"
                + "<br><b>Por Semestre:</b><br>"
                + "Nenhum semestre cadastrado<br>"
                + "</html>";

        progressoLabel = new JLabel(textoInicial);
        progressoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        percentualLabel = new JLabel("");
        percentualLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        painelLateral.add(calcular);
        painelLateral.add(Box.createVerticalStrut(15));
        painelLateral.add(progressoLabel);
        painelLateral.add(Box.createVerticalStrut(5));
        painelLateral.add(percentualLabel);

        painel.add(painelLateral, BorderLayout.EAST);

        sincronizarCheckboxes();
        atualizarCheckboxesFixos();

        return painel;
    }

    private JPanel criarAbaRequisitos() {
        JPanel painel = new JPanel(new BorderLayout());
        JPanel painelAux = new JPanel(new BorderLayout(10,10));

        JLabel labelRequisitos = new JLabel("Digite os IDs dos pré-requisitos separados por ponto e vírgula (;)");
        labelRequisitos.setFont(new Font("Arial", Font.PLAIN, 14));
        labelRequisitos.setHorizontalAlignment(SwingConstants.CENTER);

        painelAux.add(labelRequisitos, BorderLayout.NORTH);
        painelAux.add(new JScrollPane(tabelaRequisitos), BorderLayout.CENTER);
        
        listaDisponiveis = new JList<>();
        listaDisponiveis.setFont(new Font("Arial", Font.PLAIN, 16));

        JScrollPane scrollDisponiveis = new JScrollPane(listaDisponiveis);

        JButton atualizarBtn = new JButton("Atualizar Lista de Atividades Disponíveis");
        atualizarBtn.addActionListener(e -> {
            atualizarMapeamentoIDs();
            atualizarDisciplinasDisponiveis();
        });
        JPanel botoes = new JPanel();
        botoes.add(atualizarBtn);

        JPanel tabelas = new JPanel(new GridLayout(1, 3, 10, 10)); 
        tabelas.add(painelAux);
        tabelas.add(scrollDisponiveis); 

        painel.add(tabelas, BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarAbaIntegralizacao() {
        JPanel painel = new JPanel(new BorderLayout());
        
        painelIntegral.setLayout(new BoxLayout(painelIntegral, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(painelIntegral);

        JPanel painelLateral = new JPanel();
        painelLateral.setLayout(new BoxLayout(painelLateral, BoxLayout.Y_AXIS));
        painelLateral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton calcular = new JButton("Calcular Progresso de Integralização");
        calcular.addActionListener(e -> calcularProgressoIntegral());
        calcular.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelLateral.add(calcular);

        progressoIntegralLabel = new JLabel("Progresso de Integralização: 0,00% (0 de 0 concluídos)");
        progressoIntegralLabel.setFont(new Font("Arial", Font.BOLD, 16));
        progressoIntegralLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelLateral.add(Box.createVerticalStrut(15));
        painelLateral.add(progressoIntegralLabel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scroll, painelLateral);
        splitPane.setResizeWeight(0.8);
        splitPane.setDividerSize(5); 
        splitPane.setEnabled(false); 

        JPanel botoes = new JPanel();
        JButton criarbtn = new JButton("Criar Novo Curso");
        JButton carregarbtn = new JButton("Importar Curso Existente");
        JButton addBtn = new JButton("Adicionar Requisito de Integralização");
        JButton removeBtn = new JButton("Remover Requisito");
        JButton salvarBtn = new JButton("Salvar Curso");

        criarbtn.addActionListener(e -> new Intro(this, () -> {
            reset();
        }));
        carregarbtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File arquivo = fileChooser.getSelectedFile();
                carregarCursoDeArquivo(arquivo);
            }
        });
        addBtn.addActionListener(e -> adicionarCheckboxPersonalizado());
        removeBtn.addActionListener(e -> removerCheckboxPersonalizado());
        salvarBtn.addActionListener(e -> salvarCurso());

        botoes.add(criarbtn);
        botoes.add(carregarbtn);
        botoes.add(addBtn);
        botoes.add(removeBtn);
        botoes.add(salvarBtn);

        painel.add(splitPane, BorderLayout.CENTER);
        painel.add(botoes, BorderLayout.SOUTH);
        return painel;
    }

    private void salvarCurso() {
        String nome = nomeCursoField.getText().trim();
        String matricula = matriculaField.getText().trim();
        String horasCCCG = totalHorasCCCGField.getText().trim();

        if (nome.isEmpty() || matricula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os dados.");
            return;
        }

        String nomeArquivo = nome.replaceAll("\\s+", "") + "_" + matricula + ".txt";

        try (FileWriter fw = new FileWriter(nomeArquivo)) {
            fw.write(nome + "\n" + matricula + "\n" + horasCCCG + "\n");

            for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
                String tipo = "Disciplina";
                String nomeAtiv = disciplinasModel.getValueAt(i, 0).toString();
                String semestre = disciplinasModel.getValueAt(i, 1).toString();
                String carga = disciplinasModel.getValueAt(i, 2).toString();
                String nota = disciplinasModel.getValueAt(i, 3).toString();
                String periodo = disciplinasModel.getValueAt(i, 4).toString();

                Object concluidoObj = disciplinasModel.getValueAt(i, 5);
                String concluido = (concluidoObj instanceof Boolean && (Boolean) concluidoObj) ? "true" : "false";

                String requisitos = buscarRequisitosPorNome(nomeAtiv);

                fw.write(tipo + ";" + nomeAtiv + ";" + semestre + ";" + carga + ";" + nota + ";" + periodo + ";" + concluido + ":" + requisitos + "\n");
            }

            for (int i = 0; i < atividadesModel.getRowCount(); i++) {
                String tipo = "Atividade Extra";
                String nomeAtiv = atividadesModel.getValueAt(i, 0).toString();
                String semestre = atividadesModel.getValueAt(i, 1).toString();
                String horasMinimas = atividadesModel.getValueAt(i, 2).toString();
                String horasCumpridas = atividadesModel.getValueAt(i, 3).toString();
                String periodo = atividadesModel.getValueAt(i, 4).toString();

                Object concluidoObj = atividadesModel.getValueAt(i, 5);
                String concluido = (concluidoObj instanceof Boolean && (Boolean) concluidoObj) ? "true" : "false";

                String requisitos = buscarRequisitosPorNome(nomeAtiv);

                fw.write(tipo + ";" + nomeAtiv + ";" + semestre + ";" + horasMinimas + ";" + horasCumpridas + ";;" + concluido + ":" + requisitos + "\n");
            }

            for (int i = 0; i < cccgModel.getRowCount(); i++) {
                String tipo = "CCCG";
                String nomeCCCG = cccgModel.getValueAt(i, 0).toString();
                String semestre = cccgModel.getValueAt(i, 1).toString();
                String carga = cccgModel.getValueAt(i, 2).toString();
                String horasCumpridas = cccgModel.getValueAt(i, 3).toString();
                String nota = cccgModel.getValueAt(i, 4).toString();
                String periodo = cccgModel.getValueAt(i, 5).toString();

                Object concluidoObj = cccgModel.getValueAt(i, 6);
                String concluido = (concluidoObj instanceof Boolean && (Boolean) concluidoObj) ? "true" : "false";

                String requisitos = buscarRequisitosPorNome(nomeCCCG);

                fw.write(tipo + ";" + nomeCCCG + ";" + semestre + ";" + carga + ";" + horasCumpridas + ";" + nota + ";" + periodo + ";" + concluido + ":" + requisitos + "\n");
            }

            fw.write("###\n");
            for (JCheckBox check : checkboxesPersonalizados) {
                fw.write(check.getText() + ";" + check.isSelected() + "\n");
            }

            JOptionPane.showMessageDialog(this, "Curso salvo como: " + nomeArquivo);

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar curso.");
        }
    }

    private String buscarRequisitosPorNome(String nomeAtividade) {
        for (int i = 0; i < requisitosModel.getRowCount(); i++) {
            String nome = requisitosModel.getValueAt(i, 1).toString();
            if (nome.equals(nomeAtividade)) {
                return requisitosModel.getValueAt(i, 2).toString(); 
            }
        }
        return "";
    }

    private void calcularProgresso() {
        int totalHorasDisciplinas = 0;
        int feitasHorasDisciplinas = 0;

        int totalHorasAtividades = 0;
        int feitasHorasAtividades = 0;

        int totalHorasCCCG = Integer.parseInt(totalHorasCCCGField.getText().trim());
        int feitasHorasCCCG = 0;

        double somaNotas = 0;
        int countNotas = 0;

        Map<String, Integer> totalPorSemestre = new HashMap<>();
        Map<String, Integer> concluidoPorSemestre = new HashMap<>();

        for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
            String semestre = disciplinasModel.getValueAt(i, 1).toString().trim();
            String cargaStr = disciplinasModel.getValueAt(i, 2).toString().trim();
            String notaStr = disciplinasModel.getValueAt(i, 3).toString().trim();

            int carga = 0;
            try {
                carga = Integer.parseInt(cargaStr);
            } catch (NumberFormatException e) {
                System.err.println("Carga inválida na linha " + i + ": " + cargaStr);
            }

            totalHorasDisciplinas += carga;
            totalPorSemestre.put(semestre, totalPorSemestre.getOrDefault(semestre, 0) + carga);

            if (checkboxesDisciplinas[i].isSelected()) {
                feitasHorasDisciplinas += carga;
                concluidoPorSemestre.put(semestre, concluidoPorSemestre.getOrDefault(semestre, 0) + carga);

                if (!notaStr.isEmpty()) {
                    try {
                        somaNotas += Double.parseDouble(notaStr.replace(",", "."));
                        countNotas++;
                    } catch (NumberFormatException e) {
                        System.err.println("Nota inválida na linha " + i + ": " + notaStr);
                    }
                }
            }
        }

        for (int i = 0; i < atividadesModel.getRowCount(); i++) {
            String horasMinimasStr = atividadesModel.getValueAt(i, 2).toString();
            String horasCumpridasStr = atividadesModel.getValueAt(i, 3).toString();

            int horasMin = 0;
            int horasCumpr = 0;
            try {
                horasMin = Integer.parseInt(horasMinimasStr);
                horasCumpr = Integer.parseInt(horasCumpridasStr);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter horas em atividades extras");
            }

            totalHorasAtividades += horasMin;
            feitasHorasAtividades += Math.min(horasCumpr, horasMin);
        }

        for (int i = 0; i < cccgModel.getRowCount(); i++) {
            String semestre = cccgModel.getValueAt(i, 1).toString().trim();
            String cargaStr = cccgModel.getValueAt(i, 2).toString().trim();
            String horasCumpridasStr = cccgModel.getValueAt(i, 3).toString().trim();
            String notaStr = cccgModel.getValueAt(i, 4).toString().trim();

            int carga = 0;
            int horasCumpr = 0;

            try {
                carga = Integer.parseInt(cargaStr);
                horasCumpr = Integer.parseInt(horasCumpridasStr);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter carga/horas em CCCG");
            }

            feitasHorasCCCG += Math.min(horasCumpr, carga);

            totalPorSemestre.put(semestre, totalPorSemestre.getOrDefault(semestre, 0) + carga);

            Object concluidoObj = cccgModel.getValueAt(i, 6);
            if (concluidoObj instanceof Boolean && (Boolean) concluidoObj) {
                concluidoPorSemestre.put(semestre, concluidoPorSemestre.getOrDefault(semestre, 0) + carga);
                if (!notaStr.isEmpty()) {
                    try {
                        somaNotas += Double.parseDouble(notaStr.replace(",", "."));
                        countNotas++;
                    } catch (NumberFormatException e) {
                        System.err.println("Nota inválida na CCCG linha " + i + ": " + notaStr);
                    }
                }
            }
        }

        int totalHorasGeral = totalHorasDisciplinas + totalHorasAtividades + totalHorasCCCG;
        int feitasHorasGeral = feitasHorasDisciplinas + feitasHorasAtividades + feitasHorasCCCG;

        double percDisciplinas = totalHorasDisciplinas > 0 ? (feitasHorasDisciplinas * 100.0 / totalHorasDisciplinas) : 0.0;
        double percAtividades = totalHorasAtividades > 0 ? (feitasHorasAtividades * 100.0 / totalHorasAtividades) : 0.0;
        double percCCCG = totalHorasCCCG > 0 ? (feitasHorasCCCG * 100.0 / totalHorasCCCG) : 0.0;
        double percGeral = totalHorasGeral > 0 ? (feitasHorasGeral * 100.0 / totalHorasGeral) : 0.0;

        double mediaNota = countNotas > 0 ? (somaNotas / countNotas) : 0.0;

        StringBuilder sb = new StringBuilder("<html>");
        sb.append("<b>Disciplinas Obrigatórias:</b><br>");
        sb.append(String.format("Concluído: %dh de %dh (%.1f%%)<br><br>", feitasHorasDisciplinas, totalHorasDisciplinas, percDisciplinas));

        sb.append("<b>Atividades Extras:</b><br>");
        sb.append(String.format("Concluído: %dh de %dh (%.1f%%)<br><br>", feitasHorasAtividades, totalHorasAtividades, percAtividades));

        sb.append("<b>CCCG:</b><br>");
        sb.append(String.format("Concluído: %dh de %dh (%.1f%%)<br><br>", feitasHorasCCCG, totalHorasCCCG, percCCCG));

        sb.append("<b>Progresso Geral:</b><br>");
        sb.append(String.format("Total: %dh de %dh (%.1f%%)<br><br>", feitasHorasGeral, totalHorasGeral, percGeral));

        if (countNotas > 0) {
            sb.append(String.format("<b>Média das Notas:</b> %.2f<br><br>", mediaNota));
        }

        sb.append("<b>Por Semestre:</b><br>");
        for (String s : totalPorSemestre.keySet()) {
            int t = totalPorSemestre.get(s);
            int f = concluidoPorSemestre.getOrDefault(s, 0);
            double p = t > 0 ? (f * 100.0 / t) : 0.0;
            sb.append(String.format("Semestre %s: %.1f%% (%dh de %dh)<br>", s, p, f, t));
        }
        sb.append("</html>");

        progressoLabel.setText("");
        percentualLabel.setText(sb.toString());

        concluidoPorSemestre.put("AtividadesTotal", totalHorasAtividades + totalHorasCCCG);
        concluidoPorSemestre.put("AtividadesFeitas", feitasHorasAtividades + feitasHorasCCCG);

        if (painelGrafico != null) {
            painelGrafico.setDados(totalPorSemestre, concluidoPorSemestre);
        }
    }

    private void calcularProgressoIntegral() {
        int total = 0;
        int concluido = 0;

        if (checkDisciplinas != null) {
            total++;
            if (checkDisciplinas.isSelected()) concluido++;
        }
        if (checkCCCG != null) {
            total++;
            if (checkCCCG.isSelected()) concluido++;
        }
        if (checkAtividades != null) {
            total++;
            if (checkAtividades.isSelected()) concluido++;
        }

        for (JCheckBox check : checkboxesPersonalizados) {
            total++;
            if (check.isSelected()) concluido++;
        }

        if (total == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum requisito de integralização cadastrado.");
            return;
        }

        double percentual = (concluido * 100.0) / total;

        progressoIntegralLabel.setText(String.format("Progresso de Integralização: %.2f%% (%d de %d concluídos)", percentual, concluido, total));
    }

    private void sincronizarCheckboxes() {
        if (painelCheckbox == null || disciplinasModel == null || atividadesModel == null || cccgModel == null) return;
        painelCheckbox.removeAll();

        List<JCheckBox> requisitos = new ArrayList();
        List<JCheckBox> listaDisciplinas = new ArrayList<>();
        List<JCheckBox> listaAtividades = new ArrayList<>();
        List<JCheckBox> listaCCCG = new ArrayList<>();

        for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
            String nome = disciplinasModel.getValueAt(i, 0).toString();
            String carga = disciplinasModel.getValueAt(i, 2).toString();

            Object concluidoObj = disciplinasModel.getValueAt(i, 5);
            boolean concluido = (concluidoObj instanceof Boolean) && (Boolean) concluidoObj;

            JCheckBox check = new JCheckBox(nome + " (" + carga + "h)", concluido);
            final int index = i;

            check.addActionListener(e -> {
                disciplinasModel.setValueAt(check.isSelected(), index, 5);
                calcularProgresso();
            });

            listaDisciplinas.add(check);
            painelCheckbox.add(check);
        }

        for (int i = 0; i < atividadesModel.getRowCount(); i++) {
            String nome = atividadesModel.getValueAt(i, 0).toString();
            String horasMinStr = atividadesModel.getValueAt(i, 2).toString();
            String horasCumprStr = atividadesModel.getValueAt(i, 3).toString();

            int horasMin = 0, horasCumpr = 0;
            try {
                horasMin = Integer.parseInt(horasMinStr);
                horasCumpr = Integer.parseInt(horasCumprStr);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter horas: " + e.getMessage());
            }

            boolean concluido = horasCumpr >= horasMin;

            JCheckBox check = new JCheckBox(nome + " (" + horasCumpr + "h / " + horasMin + "h)", concluido);
            check.setEnabled(false);

            listaAtividades.add(check);
            painelCheckbox.add(check);
        }

        for (int i = 0; i < cccgModel.getRowCount(); i++) {
            String nome = cccgModel.getValueAt(i, 0).toString();
            String cargaStr = cccgModel.getValueAt(i, 2).toString();
            String horasCumprStr = cccgModel.getValueAt(i, 3).toString();
            String nota = cccgModel.getValueAt(i, 4).toString();

            int carga = 0, horasCumpr = 0;
            try {
                carga = Integer.parseInt(cargaStr);
                horasCumpr = Integer.parseInt(horasCumprStr);
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter horas: " + e.getMessage());
            }

            boolean concluido = horasCumpr >= carga;

            JCheckBox check = new JCheckBox(nome + " (" + horasCumpr + "h / " + carga + "h)", concluido);
            check.setEnabled(false);

            listaCCCG.add(check);
            painelCheckbox.add(check);
        }

        checkboxesDisciplinas = listaDisciplinas.toArray(new JCheckBox[0]);
        checkboxesAtividades = listaAtividades.toArray(new JCheckBox[0]);
        checkboxesCCCG = listaCCCG.toArray(new JCheckBox[0]);

        painelCheckbox.revalidate();
        painelCheckbox.repaint();
    }

    private void removerAtividade(){
        String[] tipos = {"Disciplina", "Atividade Extra", "CCCG"};
        String tipoEscolhido = (String) JOptionPane.showInputDialog(
                this,
                "Selecione o tipo de atividade que deseja remover:",
                "Remover Atividade",
                JOptionPane.PLAIN_MESSAGE,
                null,
                tipos,
                tipos[0]);

        if (tipoEscolhido == null) return;

        String[] nomes = {};
        DefaultTableModel modeloSelecionado = null;

        if (tipoEscolhido.equals("Disciplina")) {
            modeloSelecionado = disciplinasModel;
        } else if (tipoEscolhido.equals("Atividade Extra")) {
            modeloSelecionado = atividadesModel;
        } else if (tipoEscolhido.equals("CCCG")) {
            modeloSelecionado = cccgModel;
        }

        if (modeloSelecionado != null && modeloSelecionado.getRowCount() > 0) {
            nomes = new String[modeloSelecionado.getRowCount()];
            for (int i = 0; i < modeloSelecionado.getRowCount(); i++) {
                nomes[i] = modeloSelecionado.getValueAt(i, 0).toString();
            }

            String nomeEscolhido = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecione a atividade que deseja remover:",
                    "Remover Atividade",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    nomes,
                    nomes[0]);

            if (nomeEscolhido != null) {
                for (int i = 0; i < modeloSelecionado.getRowCount(); i++) {
                    if (modeloSelecionado.getValueAt(i, 0).toString().equals(nomeEscolhido)) {
                        modeloSelecionado.removeRow(i);
                        break;
                    }
                }

                // Remover da tabela de requisitos
                for (int i = 0; i < requisitosModel.getRowCount(); i++) {
                    if (requisitosModel.getValueAt(i, 1).toString().equals(nomeEscolhido)) { // Coluna 1 é o nome da atividade
                        requisitosModel.removeRow(i);
                        break;
                    }
                }

                // Atualizar checkboxes e outros cálculos
                sincronizarCheckboxes();
                atualizarCheckboxesFixos();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Não há atividades cadastradas nesse tipo.");
        }
    }

    private void atualizarMapeamentoIDs() {
        int id = 1;

        for (int i = 0; i < disciplinasModel.getRowCount(); i++, id++) {
            requisitosModel.setValueAt(id, i, 0);
        }

        for (int i = 0; i < atividadesModel.getRowCount(); i++, id++) {
            requisitosModel.setValueAt(id, disciplinasModel.getRowCount() + i, 0); 
        }

        for (int i = 0; i < cccgModel.getRowCount(); i++, id++) {
            requisitosModel.setValueAt(id, disciplinasModel.getRowCount() + atividadesModel.getRowCount() + i, 0); 
        }
    }
    
    private void atualizarDisciplinasDisponiveis() {
        List<String> disponiveis = new ArrayList<>();
        Set<Integer> concluidos = new HashSet<>();
        int idCounter = 1;

        for (int i = 0; i < disciplinasModel.getRowCount(); i++, idCounter++) {
            if (checkboxesDisciplinas != null && checkboxesDisciplinas[i].isSelected()) {
                concluidos.add(idCounter);
            }
        }

        for (int i = 0; i < atividadesModel.getRowCount(); i++, idCounter++) {
            if (checkboxesAtividades != null && checkboxesAtividades[i].isSelected()) {
                concluidos.add(idCounter);
            }
        }

        for (int i = 0; i < cccgModel.getRowCount(); i++, idCounter++) {
            if (checkboxesCCCG != null && checkboxesCCCG[i].isSelected()) {
                concluidos.add(idCounter);
            }
        }

        for (int i = 0; i < requisitosModel.getRowCount(); i++) {
            int idAtividade = Integer.parseInt(requisitosModel.getValueAt(i, 0).toString());
            String nomeAtividade = requisitosModel.getValueAt(i, 1).toString();
            String requisitosStr = requisitosModel.getValueAt(i, 2).toString();

            if (concluidos.contains(idAtividade)) {
                continue;
            }

            if (requisitosStr.isEmpty()) {
                disponiveis.add(nomeAtividade);
                continue;
            }

            String[] ids = requisitosStr.split(";");
            boolean podeCursar = true;

            for (String idReq : ids) {
                if (!idReq.trim().isEmpty() && !concluidos.contains(Integer.parseInt(idReq.trim()))) {
                    podeCursar = false;
                    break;
                }
            }

            if (podeCursar) {
                disponiveis.add(nomeAtividade);
            }
        }

        listaDisponiveis.setListData(disponiveis.toArray(new String[0]));
    }
   
    private String buscarNomePorID(int id) {
        if (id <= 0 || id > requisitosModel.getRowCount()) {
            return "ID " + id + " inválido";
        }
    return id + " - " + requisitosModel.getValueAt(id - 1, 1).toString(); 
    }

    private void inicializarCheckboxesFixos() {
        checkDisciplinas = new JCheckBox("Componentes Curriculares Obrigatórios");
        checkDisciplinas.setEnabled(false);

        checkAtividades = new JCheckBox("Atividades Curriculares Extras");
        checkAtividades.setEnabled(false);

        checkCCCG = new JCheckBox("Componentes Curriculares Complementares de Graduação - CCCGs");
        checkCCCG.setEnabled(false);

        painelCheckboxFixo.add(checkDisciplinas);
        painelCheckboxFixo.add(checkCCCG);
        painelCheckboxFixo.add(checkAtividades);
    };

    private void adicionarCheckboxPersonalizado() {
        String nome = JOptionPane.showInputDialog(this, "Digite o nome do novo requisito de integralização:");
        if (nome != null && !nome.trim().isEmpty()) {
            JCheckBox novoCheck = new JCheckBox(nome.trim());
            checkboxesPersonalizados.add(novoCheck);
            painelIntegral.add(novoCheck);
            painelIntegral.revalidate();
            painelIntegral.repaint();
        }
    };

    private void removerCheckboxPersonalizado() {
        if (checkboxesPersonalizados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há requisitos personalizados para remover.");
            return;
        }

        String[] opcoes = checkboxesPersonalizados.stream().map(JCheckBox::getText).toArray(String[]::new);
        String escolha = (String) JOptionPane.showInputDialog(this, "Selecione o requisito a ser removido:", "Remover Requisito", JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha != null) {
            JCheckBox paraRemover = checkboxesPersonalizados.stream().filter(cb -> cb.getText().equals(escolha)).findFirst().orElse(null);
            if (paraRemover != null) {
                painelIntegral.remove(paraRemover);
                checkboxesPersonalizados.remove(paraRemover);
                painelIntegral.revalidate();
                painelIntegral.repaint();
            }
        }
    }

    private void atualizarCheckboxesFixos() {
        boolean todasDisciplinasConcluidas = true;
        for (int i = 0; i < disciplinasModel.getRowCount(); i++) {
            Object concluidoObj = disciplinasModel.getValueAt(i, 5);
            if (!(concluidoObj instanceof Boolean) || !((Boolean) concluidoObj)) {
                todasDisciplinasConcluidas = false;
                break;
            }
        }
        checkDisciplinas.setSelected(todasDisciplinasConcluidas);

        boolean todasAtividadesConcluidas = true;
        for (int i = 0; i < atividadesModel.getRowCount(); i++) {
            Object concluidoObj = atividadesModel.getValueAt(i, 5);
            if (!(concluidoObj instanceof Boolean) || !((Boolean) concluidoObj)) {
                todasAtividadesConcluidas = false;
                break;
            }
        }
        checkAtividades.setSelected(todasAtividadesConcluidas);

        boolean todasCCCGsConcluidas = true;
        for (int i = 0; i < cccgModel.getRowCount(); i++) {
            Object concluidoObj = cccgModel.getValueAt(i, 6);
            if (!(concluidoObj instanceof Boolean) || !((Boolean) concluidoObj)) {
                todasCCCGsConcluidas = false;
                break;
            }
        }
        checkCCCG.setSelected(todasCCCGsConcluidas);
    }
    
    public void reset(){
        nomeCursoField.setText("");
        matriculaField.setText("");
        totalHorasCCCGField.setText("");
        disciplinasModel.setRowCount(0);
        atividadesModel.setRowCount(0);
        cccgModel.setRowCount(0);
        requisitosModel.setRowCount(0);
        painelCheckbox.removeAll();
        painelCheckbox.revalidate();
        painelCheckbox.repaint();
        checkboxesPersonalizados.clear();
        painelIntegral.removeAll();
        painelIntegral.revalidate();
        painelIntegral.repaint();
        painelIntegral.add(painelCheckboxFixo);
        painelGrafico.resetarGrafico();
    }
    
    private void iniciarCheckboxesIntegracao(){
        painelCheckbox = new JPanel();
        painelCheckboxFixo = new JPanel();
        painelCheckboxFixo.setLayout(new BoxLayout(painelCheckboxFixo, BoxLayout.Y_AXIS));
        painelCheckboxPersonalizado = new JPanel();
        painelCheckboxPersonalizado.setLayout(new BoxLayout(painelCheckboxPersonalizado, BoxLayout.Y_AXIS));
        painelIntegral = new JPanel();
        painelIntegral.setLayout(new BoxLayout(painelIntegral, BoxLayout.Y_AXIS));
        painelIntegral.add(painelCheckboxFixo);
        painelIntegral.add(painelCheckboxPersonalizado);
        checkboxesPersonalizados = new ArrayList<>();
    }

    private void iniciaListener(){
        disciplinasListener = e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 2 || col == 3) {
                    try {
                        String cargaHorariaStr = disciplinasModel.getValueAt(row, 2).toString();
                        String notaStr = disciplinasModel.getValueAt(row, 3).toString();

                        double cargaHoraria = Double.parseDouble(cargaHorariaStr.isEmpty() ? "0" : cargaHorariaStr);
                        double nota = Double.parseDouble(notaStr.isEmpty() ? "0" : notaStr);
                        boolean concluido = nota >= 6.0;

                        if (col < disciplinasModel.getColumnCount()) {
                            Object atual = disciplinasModel.getValueAt(row, 5);
                            if (!(atual instanceof Boolean) || (Boolean) atual != concluido) {
                                disciplinasModel.setValueAt(concluido, row, 5);
                            }
                        }

                        sincronizarCheckboxes();
                        atualizarCheckboxesFixos();

                    } catch (NumberFormatException ex) {
                        System.err.println("Valor inválido para carga horária ou nota: " + ex.getMessage());
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.err.println("Tentativa de acessar coluna inexistente no modelo Disciplinas: " + ex.getMessage());
                    }
                }
            }
        };
        atividadesListener = e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();

                if (col == 2 || col == 3) {
                    try {
                        String horasMinStr = atividadesModel.getValueAt(row, 2).toString();
                        String horasCumprStr = atividadesModel.getValueAt(row, 3).toString();

                        double horasMin = Double.parseDouble(horasMinStr.isEmpty() ? "0" : horasMinStr);
                        double horasCumpr = Double.parseDouble(horasCumprStr.isEmpty() ? "0" : horasCumprStr);

                        boolean concluido = horasCumpr >= horasMin;
                        if (col < atividadesModel.getColumnCount()) {
                            Object atual = atividadesModel.getValueAt(row, 5);
                            if (!(atual instanceof Boolean) || (Boolean) atual != concluido) {
                                atividadesModel.setValueAt(concluido, row, 5);
                            }
                        };
                        sincronizarCheckboxes();
                        atualizarCheckboxesFixos();
                    } catch (NumberFormatException ex) {
                        System.err.println("Valor inválido para horas: " + ex.getMessage());
                    }
                }
            }
        };
        cccgListener = e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();

                if (col == 2 || col == 3) {
                    try {
                        String cargaHorariaStr = cccgModel.getValueAt(row, 2).toString();
                        String horasCumprStr = cccgModel.getValueAt(row, 3).toString();

                        double cargaHoraria = Double.parseDouble(cargaHorariaStr.isEmpty() ? "0" : cargaHorariaStr);
                        double horasCumpr = Double.parseDouble(horasCumprStr.isEmpty() ? "0" : horasCumprStr);

                        boolean concluido = horasCumpr >= cargaHoraria;

                        if (col < cccgModel.getColumnCount()) { // Aqui estava errado
                            Object atual = cccgModel.getValueAt(row, 6);
                            if (!(atual instanceof Boolean) || (Boolean) atual != concluido) {
                                cccgModel.setValueAt(concluido, row, 6);
                            }
                        }

                        sincronizarCheckboxes();
                        atualizarCheckboxesFixos();
                    } catch (NumberFormatException ex) {
                        System.err.println("Valor inválido para horas: " + ex.getMessage());
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        System.err.println("Tentativa de acessar coluna inexistente no modelo CCCG: " + ex.getMessage());
                    }
                }
            }
        };
        disciplinasModel.addTableModelListener(disciplinasListener);
        atividadesModel.addTableModelListener(atividadesListener);
        cccgModel.addTableModelListener(cccgListener);
    }
}