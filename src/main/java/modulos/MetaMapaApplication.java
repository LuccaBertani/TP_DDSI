package modulos;

import modulos.agregacion.entities.Pais;
import modulos.agregacion.entities.PaisProvincias;
import modulos.agregacion.entities.Provincia;
import modulos.agregacion.entities.UbicacionString;
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
        UbicacionString ubi = Geocodificador.obtenerUbicacion(-34.371678, -57.573933);
        System.out.println(ubi.getPais());
        System.out.println(ubi.getProvincia());
    }
}
