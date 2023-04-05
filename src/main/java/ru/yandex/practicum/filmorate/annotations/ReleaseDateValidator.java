package ru.yandex.practicum.filmorate.annotations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

import static ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage.BIRTHDAY_OF_CINEMA;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {

    @Override
    public void initialize(ReleaseDate releaseDate) {}

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext cxt) {
        if(releaseDate == null) {
            return false;
        }
        return releaseDate.isAfter(BIRTHDAY_OF_CINEMA);
    }
}
