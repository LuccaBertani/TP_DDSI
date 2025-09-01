package modulos.agregacion.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespuestaHttp<T> {
    private T datos;
    private Integer codigo;

    public RespuestaHttp(T datos, Integer codigo){
        this.datos = datos;
        this.codigo = codigo;
    }
}

