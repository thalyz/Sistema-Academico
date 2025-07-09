import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import javax.swing.*;

public class MenuInicial extends JFrame {

    public MenuInicial(JFrame parent) {
        setTitle("Início");
        setSize(500, 100);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel mensagem = new JLabel("Deseja abrir um curso existente ou criar um novo?");
        mensagem.setFont(new Font("Arial", Font.PLAIN,16));
        mensagem.setHorizontalAlignment(SwingConstants.CENTER);
        add(mensagem, BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new FlowLayout());

        JButton botaoNovo = new JButton("Criar Novo Curso");
        JButton botaoImportar = new JButton("Importar Curso Existente");

        botoesPanel.add(botaoNovo);
        botoesPanel.add(botaoImportar);

        add(botoesPanel, BorderLayout.SOUTH);

        botaoNovo.addActionListener(e -> {
            new Intro(parent, () -> ((CursoGUI) parent).reset()); 
            ((CursoGUI) parent).setVisible(true);
            dispose(); 
        });

        botaoImportar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(".");
            int result = fileChooser.showOpenDialog(parent);
            if (result == JFileChooser.APPROVE_OPTION) {
                File arquivo = fileChooser.getSelectedFile();
                ((CursoGUI) parent).carregarCursoDeArquivo(arquivo);
                ((CursoGUI) parent).setVisible(true);
            }
            dispose(); 
        });

        setVisible(true);
    }
}
