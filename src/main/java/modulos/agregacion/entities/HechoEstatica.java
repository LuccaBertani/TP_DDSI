package modulos.agregacion.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESTATICA")
public class HechoEstatica extends Hecho{
}
