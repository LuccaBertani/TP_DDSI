public class FiltroCategoria implements Filtro{
    Categoria categoria;
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getCategoria() == this.categoria;
    }

    public FiltroCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
