package modulos.agregacion.entities.DbMain.filtros;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Path;
import lombok.Getter;
import modulos.agregacion.entities.DbMain.Hecho;
import modulos.agregacion.entities.atributosHecho.TipoContenido;
import org.springframework.data.jpa.domain.Specification;

@Getter

@Table(name = "filtro_contenido_multimedia")
@Entity
public class FiltroContenidoMultimedia extends Filtro {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoContenidoMultimedia", nullable = false, length = 20)
    TipoContenido tipoContenido;

    public FiltroContenidoMultimedia(TipoContenido tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public FiltroContenidoMultimedia() {

    }

    @Override
    public Boolean aprobarHecho(Hecho hecho) {
        return tipoContenido.equals(hecho.getAtributosHecho().getContenidoMultimedia());
    }

    @Override
    public <T> Specification<T> toSpecification(Class<T> clazz) {
        return((root, query, cb) -> {
            Path<Long> pathId = root.get("atributosHecho").get("contenidoMultimedia");
            return cb.equal(pathId,this.tipoContenido);
        });
    }

}
