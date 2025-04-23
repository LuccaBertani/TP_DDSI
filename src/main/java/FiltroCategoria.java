public class FiltroCategoria implements Filtro{

    Categoria categoria;

    public FiltroCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getCategoria() == this.categoria;
    }

}
