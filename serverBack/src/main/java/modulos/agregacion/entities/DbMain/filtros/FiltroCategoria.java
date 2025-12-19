package modulos.agregacion.entities.DbMain.filtros;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Path;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.DbMain.Categoria;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@Entity
@Table(name = "filtro_categoria")
public class FiltroCategoria extends Filtro {

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_categoria", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_filtro_categoria_categoria"))
    private Categoria categoria;

    public FiltroCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public FiltroCategoria() {

    }

    public <T> Specification<T> toSpecification(Class<T> clazz) {
        return (root, query, cb) -> {
            Path<Long> pathId = root.get("atributosHecho").get("categoria_id");
            return cb.equal(pathId, this.categoria.getId());
        };
    }

}