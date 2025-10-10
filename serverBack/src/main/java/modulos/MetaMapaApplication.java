package modulos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling

// TODO: Para volver al estado en el que estábamos con una sola BDD: Algoritmos de consenso, filtros -> mapeado a colecciones por filtros, De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados?, ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
public class MetaMapaApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetaMapaApplication.class, args);
    }
}