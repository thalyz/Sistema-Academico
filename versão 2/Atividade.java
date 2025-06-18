public abstract class Atividade {
    protected String nome;
    protected String tipo;
    protected int semestre;
    protected String periodo;

    public Atividade(String nome, String tipo, int semestre, String periodo) {
        this.nome = nome;
        this.tipo = tipo;
        this.semestre = semestre;
        this.periodo = periodo;
    }

    public abstract boolean validar();
    
    public String getNome(){
        return nome; 
    }
    public String getTipo(){ 
        return tipo; 
    }
    public int getSemestre(){
        return semestre;
    }
    public String getPeriodo(){
        return periodo;
    }
    public void setNome(String nome){ 
        this.nome = nome; 
    }
    public void setTipo(String tipo){ 
        this.tipo = tipo; 
    }
    public void setSemestre(int semestre){
        this.semestre = semestre;
    }
    public void setPeriodo(String periodo){
        this.periodo = periodo;
    }
}
