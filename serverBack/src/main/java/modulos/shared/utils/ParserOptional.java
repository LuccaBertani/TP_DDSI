package modulos.shared.utils;

import java.util.Optional;

public class ParserOptional {

    private ParserOptional() {} // para que no se instancie

    public static <T> T parsearOptional(Optional<T> optional) {
        return optional.orElse(null);
    }
}


