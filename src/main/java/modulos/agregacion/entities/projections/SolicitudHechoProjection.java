package modulos.agregacion.entities.projections;

public interface SolicitudHechoProjection {
    Long getId();
    Long getUsuarioId();
    Long getHechoId();
    String getJustificacion();
    Boolean getProcesada();
    Boolean getRechazadaPorSpam();
}
