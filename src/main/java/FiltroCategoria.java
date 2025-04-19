public class FiltroCategoria implements Filtro{
    Categoria categoria;
    public Boolean aprobarHecho(Hecho hecho){
        if (hecho.getCategoria() == this.categoria){
            return true;
        }
        return false;
    }
}
