package hu.educloud.main.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

public interface IController<T, U> {
    @GetMapping
    ResponseEntity<List<T>> getAll();

    @GetMapping("/:id")
    ResponseEntity<T> getById(String id);

    @PostMapping
    ResponseEntity<String> create(U dto);

    @PutMapping
    ResponseEntity<String> update(U dto);

    @DeleteMapping
    ResponseEntity<Void> delete(String id);
}
