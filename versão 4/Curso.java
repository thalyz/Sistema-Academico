import java.util.List;

public class Curso {
    private String nome;
    private int codigo;
    private List<Atividade> atividades;

    public Curso() {
    }

    public Curso(String nome, int codigo, List<Atividade> atividades) {
        this.nome = nome;
        this.codigo = codigo;
        this.atividades = atividades;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public List<Atividade> getAtividades() {
        return atividades;
    }

    public void setAtividades(List<Atividade> atividades) {
        this.atividades = atividades;
    }
}
