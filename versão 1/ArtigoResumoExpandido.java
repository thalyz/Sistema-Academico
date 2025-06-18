public class ArtigoResumoExpandido extends Atividade {
    @Override
    public boolean validar() {
        return getCargaHoraria() > 0;
    }
}
