public class AtividadeExtra extends Atividade {
    private int cargaHorariaMinima;
    private int cargaHorariaCumprida;

    public AtividadeExtra(String nome, String tipo, int semestre, String periodo, int cargaHorariaMinima, int cargaHorariaCumprida) {
        super(nome, tipo, semestre, periodo);
        this.cargaHorariaMinima = cargaHorariaMinima;
        this.cargaHorariaCumprida = cargaHorariaCumprida;
    }
    @Override
    public boolean validar() {
        return cargaHorariaCumprida >= cargaHorariaMinima;
    }
    @Override
    public int getCargaCumprida() {
        return cargaHorariaCumprida;
    }
    public int getCargaHorariaMinima(){ 
        return cargaHorariaMinima; 
    }
    public void setCargaHorariaMinima(int cargaHorariaMinima){ 
        this.cargaHorariaMinima = cargaHorariaMinima; 
    }
    public void setCargaHorariaCumprida(int cargaHorariaCumprida){ 
        this.cargaHorariaCumprida = cargaHorariaCumprida; 
    }
}
