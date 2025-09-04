package modulos.agregacion.entities.filtros;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;
import modulos.buscadores.Normalizador;

import java.util.List;

@Getter
@Setter
@Table(name = "filtro_descripcion")
@Entity
public class FiltroDescripcion extends Filtro {
    @Column(name = "descripcion", length = 50)
    String descripcion;

    public FiltroDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public FiltroDescripcion() {

    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        List<String> palabrasHecho = Normalizador.normalizarSeparado(hecho.getAtributosHecho().getDescripcion());
        List<String> palabrasFiltro = Normalizador.normalizarSeparado(this.descripcion);

        // Si la descripcion del hecho enviado por parametro tiene todas sus palabras contenidas en el filtro de la descripcion
        return palabrasHecho.containsAll(palabrasFiltro);
    }
}
