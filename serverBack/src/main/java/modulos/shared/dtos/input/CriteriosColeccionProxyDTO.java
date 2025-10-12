package modulos.shared.dtos.input;

import lombok.Data;

@Data
public class CriteriosColeccionProxyDTO {
    private String categoria;
    private String contenidoMultimedia;
    private String descripcion;
    private String fechaAcontecimientoInicial;
    private String fechaAcontecimientoFinal;
    private String fechaCargaInicial;
    private String fechaCargaFinal;
    private String origen;
    private String pais;
    private String provincia;
    private String titulo;
    private Double latitud;
    private Double longitud;

    public CriteriosColeccionProxyDTO(){
    }

}
