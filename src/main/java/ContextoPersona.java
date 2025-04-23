import lombok.Getter;

public class ContextoPersona {
    @Getter
    private Visualizador visualizador;

    public ContextoPersona(Visualizador visualizador){
        this.visualizador = visualizador;
    }

    public void ContribuyenteAVisualizador(){
        DatosPersonalesPublicador datos = this.visualizador.getDatosPersonales();
        this.visualizador = new Visualizador();
        this.visualizador.setDatosPersonales(datos);
    }

    public void VisualizadorAContribuyente(){
        DatosPersonalesPublicador datos = this.visualizador.getDatosPersonales();
        this.visualizador = new Contribuyente();
        this.visualizador.setDatosPersonales(datos);
    }

}
