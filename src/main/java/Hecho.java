import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
@Getter
@Setter
public class Hecho {

    private String titulo;
    private String descripcion;
    private Categoria categoria;
    private TipoContenido contenidoMultimediaOpcional;
    private Pais pais;
    private ZonedDateTime fechaAcontecimiento;
    private ZonedDateTime fechaDeCarga;
    private Origen origen;
}
