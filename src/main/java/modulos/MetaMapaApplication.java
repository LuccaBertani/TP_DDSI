package modulos;

import modulos.agregacion.entities.Pais;
import modulos.agregacion.entities.PaisProvincias;
import modulos.agregacion.entities.Provincia;
import modulos.shared.utils.Geocodificador;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceToOne;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class MetaMapaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetaMapaApplication.class, args);

        /*List<PaisProvincias> paisesxProvincias= Geocodificador.obtenerTodosLosPaises();
        for(PaisProvincias pais : paisesxProvincias){
            System.out.println(pais.getPais().getPais());
            for(Provincia provincia: pais.getProvincias()){
                System.out.println(provincia.getProvincia());
            }
        }*/
        System.out.println("PINGO1");
        List<PaisProvincias> paisesX = Geocodificador.obtenerTodosLosPaises();
        for (PaisProvincias pais: paisesX){
            System.out.println(pais.getPais().getPais());
            for(Provincia provincia: pais.getProvincias()){
                System.out.println("PROVINCIA!!" + provincia.getProvincia());
            }
        }

        System.out.println("PINGO2");
    }
}
