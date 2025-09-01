package modulos.agregacion.entities.filtros;

import lombok.Data;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.Pais;

@Data
public class FiltroPais implements Filtro {

private Pais pais;

    public FiltroPais(Pais pais) {
        this.pais = pais;
    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getAtributosHecho().getUbicacion().getPais() == pais;
    }
}
