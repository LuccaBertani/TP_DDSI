package modulos.agregacion.entities.DbProxy;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import modulos.agregacion.entities.DbMain.Hecho;

@Entity
@Table(name = "hecho_proxy")
public class HechoProxy extends Hecho {
}
