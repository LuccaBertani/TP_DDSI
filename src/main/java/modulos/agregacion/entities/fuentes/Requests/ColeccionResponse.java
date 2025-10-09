package modulos.agregacion.entities.fuentes.Requests;

import lombok.Data;
import modulos.shared.dtos.input.CriteriosColeccionProxyDTO;
import modulos.shared.dtos.input.ProxyDTO;

@Data
public class ColeccionResponse {
private String id; //este campo no tiene sentido recibirlo en proxy pero fue
private String titulo;
private String descripcion;
private ProxyDTO criterios;
}
