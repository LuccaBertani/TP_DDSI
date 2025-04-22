import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FiltroDescripcionTest {
    @Test
    public void pasaDescripcion(){
        Hecho hecho = new Hecho();
        hecho.setTitulo("InCendio");

        FiltroTitulo filtroTitulo = new FiltroTitulo("incendio jijo");

        assertEquals(true, filtroTitulo.aprobarHecho(hecho));


    }
}