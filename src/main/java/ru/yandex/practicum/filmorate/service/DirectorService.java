package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controllers.DirectorController;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
public class DirectorService {

    @Autowired
    @Qualifier("directorDbStorage")
    private final DirectorStorage directorStorage;

    private static final Logger log = LoggerFactory.getLogger(DirectorService.class);

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director create(Director director) {
        Director newDirector = directorStorage.create(director);
        log.info("A new film with id " + newDirector.getId() + " has been created.");
        return newDirector;
    }

    public Director get(Integer id) {
        Director director = directorStorage.get(id);
        if (director == null) {
            log.error("A director with id " + id + " doesn't exist.");
            throw new NullPointerException("A director with id " + id + " doesn't exist.");
        }
        return director;
    }

    public Director update(Director director) {
        Director newDirector = directorStorage.update(director);
        log.info("A director with id  " + newDirector.getId() + " has been updated.");
        return newDirector;
    }

    public List<Director> getAll() {
        return directorStorage.getAll();
    }

    public void delete(Integer id) {
        directorStorage.delete(id);
    }
}
