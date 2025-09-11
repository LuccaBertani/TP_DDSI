package modulos.servicioEstadistica.controller;

import modulos.servicioEstadistica.ServicioDeEstadistica;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {
    private ServicioDeEstadistica servicioDeEstadistica;

    public EstadisticasController(ServicioDeEstadistica servicioDeEstadistica) {
        this.servicioDeEstadistica = servicioDeEstadistica;
    }

    @GetMapping(value = "/reporte/{id}.csv", produces = "text/csv")
    public ResponseEntity<StreamingResponseBody> exportarCSV(@PathVariable int id) throws Exception {

        Path csv = servicioDeEstadistica.obtenerEstadistica(id);

        if (csv == null){
            return ResponseEntity.noContent().build();
        }

        String filename = csv.getFileName().toString();

        StreamingResponseBody body = output -> {
            try (var in = java.nio.file.Files.newInputStream(csv)) {
                in.transferTo(output);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(body);
    }
}
