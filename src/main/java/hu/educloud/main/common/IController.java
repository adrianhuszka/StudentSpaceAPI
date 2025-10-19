package hu.educloud.main.common;

import hu.educloud.main.users.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.UUID;

public interface IController<T, U> {
    @GetMapping
    ResponseEntity<List<Users>> getAll();

    @GetMapping("/:id")
    ResponseEntity<Users> getById(String id);

    @PostMapping
    ResponseEntity<String> create(U dto);

    @PutMapping
    ResponseEntity<String> update(U dto);

    @DeleteMapping
    ResponseEntity<Void> delete(String id);
}
