package models.repositories;

import java.util.List;

public interface IRepository<T> {
    List<T> findAll();
    void save(T entidad);
    void delete(T entidad);
    T findById(Long id);
}
