package modulos.agregacion.repositories;

import modulos.agregacion.entities.Coleccion;

import java.util.List;

public interface IRepository<T> {
    List<T> findAll();
    void save(T entidad);
    void delete(T entidad);
    T findById(Long id);
    long getProxId();
    void update(T entidad);
}