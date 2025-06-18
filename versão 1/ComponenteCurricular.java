public class ComponenteCurricular extends Atividade {
    private int codigoComponente;
    private int periodo;
    private int nota;
    private int frequencia;

    @Override
    public boolean validar() {
        return nota >= 6 && frequencia >= 75;
    }

    public int getCodigoComponente() {
        return codigoComponente;
    }

    public void setCodigoComponente(int codigoComponente) {
        this.codigoComponente = codigoComponente;
    }

    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public int getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(int frequencia) {
        this.frequencia = frequencia;
    }
}
