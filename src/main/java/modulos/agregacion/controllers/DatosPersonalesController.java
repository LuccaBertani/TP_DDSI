package modulos.agregacion.controllers;

import modulos.agregacion.services.impl.DatosPersonalesService;
import modulos.shared.dtos.output.DatosPersonalesOutputDTO;
import modulos.shared.RespuestaHttp;
import modulos.usuario.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/datos-personales")
public class DatosPersonalesController {

    private DatosPersonalesService datosPersonalesService;

    public DatosPersonalesController(DatosPersonalesService datosPersonalesService){
        this.datosPersonalesService = datosPersonalesService;
    }


    @GetMapping("/contribuyentes")
    public ResponseEntity<List<DatosPersonalesOutputDTO>> buscarTodos(@RequestParam Long id_usuario) {
        RespuestaHttp<List<Usuario>> respuestaHttp = datosPersonalesService.obtenerListaContribuyentes(id_usuario);

        Integer codigo = respuestaHttp.getCodigo();

        if (codigo.equals(HttpStatus.UNAUTHORIZED.value())) {
            return ResponseEntity.status(codigo).build();
        }

        List<Usuario> usuarios = respuestaHttp.getDatos();
        List<DatosPersonalesOutputDTO> datos = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            DatosPersonalesOutputDTO dto = new DatosPersonalesOutputDTO();
            dto.setId(usuario.getId());
            dto.setApellido(usuario.getDatosPersonales().getApellido());
            dto.setNombre(usuario.getDatosPersonales().getNombre());
            dto.setEdad(usuario.getDatosPersonales().getEdad());
            datos.add(dto);
        }
        return ResponseEntity.status(codigo).body(datos); // Asumo que es un 200 OK
    }
}
