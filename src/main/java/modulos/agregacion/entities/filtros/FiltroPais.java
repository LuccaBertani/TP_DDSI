package modulos.agregacion.entities.filtros;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Path;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Hecho;
import modulos.agregacion.entities.Pais;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@Table(name = "filtro_pais")
@Entity
public class FiltroPais extends Filtro {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_pais", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_filtro_pais_pais"))
    private Pais pais;

    public FiltroPais(Pais pais) {
        this.pais = pais;
    }

    public FiltroPais() {

    }

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getAtributosHecho().getUbicacion().getPais().getId().equals(this.pais.getId());
    }

    @Override
    public Specification<Hecho> toSpecification() {
        return((root, query, cb) -> {
            Path<Long> pathId = root.get("atributosHecho").get("ubicacion").get("pais").get("id");
            return cb.equal(pathId,this.pais.getId());
        });
    }
}
