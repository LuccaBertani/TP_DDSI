package modulos.agregacion.entities.filtros;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import modulos.agregacion.entities.Categoria;
import modulos.agregacion.entities.Hecho;
import modulos.buscadores.Normalizador;

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

    @Override
    public Boolean aprobarHecho(Hecho hecho){
        return hecho.getAtributosHecho().getCategoria().getId().equals(this.categoria.getId());
    }

}
