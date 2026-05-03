package hu.studentspace.main.common;

import java.util.List;
import java.util.UUID;

public interface IService<T, U> {
    List<T> getAll();

    T findById(UUID id);

    String save(U t);

    String update(U t);

    void delete(UUID id);
}
