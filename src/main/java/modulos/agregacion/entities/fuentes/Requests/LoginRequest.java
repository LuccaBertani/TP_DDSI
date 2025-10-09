package modulos.agregacion.entities.fuentes.Requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
