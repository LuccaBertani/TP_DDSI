package modulos;

import modulos.agregacion.entities.Pais;
import modulos.agregacion.entities.PaisProvincias;
import modulos.agregacion.entities.Provincia;
import modulos.agregacion.entities.UbicacionString;
import modulos.agregacion.repositories.IProvinciaRepository;
import modulos.buscadores.BuscadorProvincia;
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
    }
}
