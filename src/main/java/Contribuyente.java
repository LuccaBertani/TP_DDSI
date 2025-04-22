import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class Contribuyente extends Publicador {
    private Integer cantHechosSubidos; // Cuando llegue a 0, el contribuyente debería pasar a ser visualizador
    public Contribuyente(){
        cantHechosSubidos = 1;
        // Si el usuario se logueó y no está en la lista de contribuyentes de los administradores
        /*if (!this.getDatosPersonales().getNombre().isEmpty() && !Administrador.listaContribuyentesContains(this))
            Administrador.agregarContribuyente(this);*/
    }

    @Override
    public void subirHechos(List<Hecho> hechos){
        super.subirHechos(hechos);
        cantHechosSubidos++;
    }

    void solicitarEliminacionHecho(Hecho hecho){
        //TODO
    }


}
