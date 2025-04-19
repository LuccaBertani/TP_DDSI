import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FiltroDescripcionTest {
    @Test
    public void pasaDescripcion(){
        Hecho hecho = new Hecho();
        hecho.setDescripcion("InCendio");

        FiltroDescripcion filtroDescripcion = new FiltroDescripcion();
        filtroDescripcion.setDescripcion("incendio");

        assertEquals(true, filtroDescripcion.aprobarHecho(hecho));


    }
}