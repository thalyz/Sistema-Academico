public class Disciplina extends Atividade {
    private int cargaHoraria;
    private int nota;

    public Disciplina(String nome, String tipo, int cargaHoraria, String periodo, int semestre) {
        super(nome, tipo, semestre, periodo);
        this.cargaHoraria = cargaHoraria;
    }
    @Override
    public boolean validar(){
        if (nota > 6){
            return true;
        }
        else{
            return false;
        }
    }
    public int getCargaHoraria(){ 
        return cargaHoraria; 
    }
    public void setCargaHoraria(int cargaHoraria) { 
        this.cargaHoraria = cargaHoraria; 
    }
    public int getNota() { 
        return nota; 
    }
    public void setNota(int nota) { 
        this.nota = nota; 
    }
}
