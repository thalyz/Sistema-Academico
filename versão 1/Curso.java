import java.util.List;

public class Curso {
    private String nomeDoCurso;
    private int codigoDoCurso;
    private List<Atividade> requisitos;

    public void listarRequisitos() {
        for (Atividade r : requisitos) {
            System.out.println(r.getNomeDaAtividade());
        }
    }

    public String getNomeDoCurso() {
        return nomeDoCurso;
    }

    public void setNomeDoCurso(String nomeDoCurso) {
        this.nomeDoCurso = nomeDoCurso;
    }

    public int getCodigoDoCurso() {
        return codigoDoCurso;
    }

    public void setCodigoDoCurso(int codigoDoCurso) {
        this.codigoDoCurso = codigoDoCurso;
    }

    public List<Atividade> getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(List<Atividade> requisitos) {
        this.requisitos = requisitos;
    }
}
