import java.util.List;

public class Curso {
    private String nome;
    private List<Atividade> atividades;
    public Curso() {
    }
    public Curso(String nome,  List<Atividade> atividades) {
        this.nome = nome;
        this.atividades = atividades;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public List<Atividade> getAtividades() {
        return atividades;
    }

    public void setAtividades(List<Atividade> atividades) {
        this.atividades = atividades;
    }
}
