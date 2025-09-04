package modulos.agregacion.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("PROXY")
public class HechoProxy extends Hecho {
}
