import lombok.Getter;
import lombok.Setter;

@Getter
public class Categoria {
    private String titulo;

    public Categoria(String titulo) {
        this.titulo = titulo;
    }

}
