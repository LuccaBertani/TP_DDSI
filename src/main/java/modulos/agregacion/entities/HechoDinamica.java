package modulos.agregacion.entities;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DINAMICA")
public class HechoDinamica extends Hecho{
}
