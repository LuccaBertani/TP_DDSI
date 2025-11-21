package modulos.Front.services;

import modulos.Front.dtos.input.CategoriaInputDTO;
import modulos.Front.dtos.input.SinonimoInputDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GestionService {

    private final WebApiCallerService webApiCallerService;
    private final String categoriasUrl = "/api/categoria";
    private final String sinonimosUrl = "/api/sinonimo";


    public ResponseEntity<?> crearCategoria(CategoriaInputDTO inputDTO) {

        return webApiCallerService.postEntity(this.categoriasUrl + "/crear", inputDTO, Void.class);
    }


    public ResponseEntity<?> crearSinonimo(SinonimoInputDTO inputDTO) {

        return webApiCallerService.postEntity(this.sinonimosUrl + "/crear", inputDTO, Void.class);
    }
}