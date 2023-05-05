package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    private static final Logger log = LoggerFactory.getLogger(DirectorController.class);

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.info("Create a new director - request received.");
        return directorService.create(director);
    }

    @GetMapping("/{id}")
    public Director get(@PathVariable Integer id) {
        log.info("Get a director by id " + id + " - request received.");
        return directorService.get(id);
    }


    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.info("Update a director by id " + director.getId() + " - request received.");
        return directorService.update(director);
    }

    @GetMapping
    public List<Director> getAll() {
        log.info("Get a list of all directors - request received.");
        return directorService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        log.info("Delete a director by id " + id + " - request received.");
        directorService.delete(id);
    }
}
