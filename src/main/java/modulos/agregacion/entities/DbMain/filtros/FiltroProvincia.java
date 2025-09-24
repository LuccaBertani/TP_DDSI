package modulos.agregacion.entities.DbMain.filtros;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Path;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.DbMain.Hecho;
import modulos.agregacion.entities.DbMain.Provincia;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@Table(name = "filtro_provincia")
@Entity
public class FiltroProvincia extends Filtro{
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_provincia", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_filtro_provincia_provincia"))
    private Provincia provincia;

    public FiltroProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public FiltroProvincia() {

    }

    /*@Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getAtributosHecho().getUbicacion().getProvincia().getId().equals(this.provincia.getId());
    }*/

    @Override
    public Specification<Hecho> toSpecification() {
        return((root, query, cb) -> {
            Path<Long> pathId = root.get("atributosHecho").get("ubicacion").get("provincia").get("id");
            return cb.equal(pathId,this.provincia.getId());
        });
    }
}
