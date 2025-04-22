public class ContextoPersona {

    private Publicador publicador;

    public ContextoPersona(Publicador publicador){
        this.publicador = publicador;
    }

    public void ContribuyenteAVisualizador(){
        DatosPersonalesPublicador datos = this.publicador.getDatosPersonales();
        this.publicador = new Visualizador();
        this.publicador.setDatosPersonales(datos);
    }
    public void VisualizadorAContribuyente(){
        DatosPersonalesPublicador datos = this.publicador.getDatosPersonales();
        this.publicador = new Contribuyente();
        this.publicador.setDatosPersonales(datos);
    }

}
