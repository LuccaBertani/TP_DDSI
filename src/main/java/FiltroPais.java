public class FiltroPais implements Filtro{

private Pais pais;

public Boolean aprobarHecho(Hecho hecho){
    return hecho.getPais() == pais;
}
}
