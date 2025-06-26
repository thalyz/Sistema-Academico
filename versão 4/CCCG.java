public class CCCG extends Atividade {
    private int cargaHorariaCumprida;
    private int nota;
    private int cargaHorariaMinima;

    public CCCG(String nome, String tipo, int cargaHorariaCumprida, int cargaHorariaMinima, String periodo, int semestre) {
        super(nome, tipo, semestre, periodo);
        this.cargaHorariaCumprida = cargaHorariaCumprida;
        this.cargaHorariaMinima = cargaHorariaMinima;
    }
    @Override
    public boolean validar(){
        if (nota > 6 && cargaHorariaCumprida >= cargaHorariaMinima){
            return true;
        }
        else{
            return false;
        }
    }
    @Override
    public int getCargaCumprida(){ 
        return cargaHorariaCumprida; 
    }
    public void setCargaHoraria(int cargaHorariaCumprida) { 
        this.cargaHorariaCumprida = cargaHorariaCumprida; 
    }
    public int getCargaMinima(){
        return cargaHorariaMinima;
    }
    public void setCargaMinima(int cargaHorariaMinima){
        this.cargaHorariaMinima = cargaHorariaMinima;
    }
    public int getNota() { 
        return nota; 
    }
    public void setNota(int nota) { 
        this.nota = nota; 
    }
}
