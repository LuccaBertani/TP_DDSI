package modulos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling

// TODO: 401 UNAUTHORIZED CUANDO EL USUARIO NO ESTÁ AUTENTICADO. 403 FORBIDDEN CUANDO EL USUARIO ESTÁ AUTENTICADO PERO NO TIENE PERMISOS
public class ServerBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerBackApplication.class, args);
    }
}