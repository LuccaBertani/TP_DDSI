import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;

public class Hecho {
    @Getter
    @Setter
    private String titulo;
    @Getter
    @Setter
    private String descripcion;
    private Categoria categoria;
    private ContenidoMultimedia contenidoMultimediaOpcional;
    @Getter
    @Setter
    private Pais pais;
    @Getter
    @Setter
    private ZonedDateTime fechaAcontecimiento;
    @Getter
    @Setter
    private ZonedDateTime fechaDeCarga;
    @Getter
    @Setter
    private Origen origen;


}
