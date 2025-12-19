package modulos.Front;

@FunctionalInterface
public interface ApiCall<T> {
    T execute(String accessToken) throws Exception;
}
