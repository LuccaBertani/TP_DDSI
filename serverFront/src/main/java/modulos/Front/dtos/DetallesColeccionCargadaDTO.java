package modulos.Front.dtos;

import lombok.Data;
import modulos.Front.dtos.output.CategoriaDto;
import modulos.Front.dtos.output.ColeccionOutputDTO;
import modulos.Front.dtos.output.PaisDto;
import modulos.Front.dtos.output.ProvinciaDto;

import java.util.List;
@Data
public class DetallesColeccionCargadaDTO
{
    private ColeccionOutputDTO coleccionDto;
    private String descripcion;
    private String fechaAcontecimientoInicial;
    private String fechaAcontecimientoFinal;
    private String fechaCargaInicial;
    private String fechaCargaFinal;
    private String titulo;
    private List<CategoriaDto> categoria;
    private List<PaisDto> pais;
    private List<ContenidoMultimediaDto> contenidoMultimedia;
    private List<OrigenDto> virgenes;
    private Boolean navegacionCurada;
}
