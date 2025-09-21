package modulos.buscadores;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Getter
public class BuscadoresRegistry {
    private final BuscadorCategoria buscadorCategoria;
    private final BuscadorFiltro buscadorFiltro;
    private final BuscadorHecho buscadorHecho;
    private final BuscadorPais buscadorPais;
    private final BuscadorProvincia buscadorProvincia;
    private final BuscadorUbicacion buscadorUbicacion;
}

