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
@Table(name = "filtro_titulo")
@Entity
public class FiltroTitulo extends Filtro {
    @Column(name = "titulo", length = 50)
    String titulo;

    public FiltroTitulo(String titulo) {
        this.titulo = titulo;
    }

    public FiltroTitulo() {

    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {
        List<String> palabrasHecho = Normalizador.normalizarSeparado(hecho.getAtributosHecho().getTitulo());
        List<String> palabrasFiltro = Normalizador.normalizarSeparado(this.titulo);

        // Si la descripcion del hecho enviado por parametro tiene todas sus palabras contenidas en el filtro de la descripcion
        return palabrasHecho.containsAll(palabrasFiltro);
    }
}