package ru.yandex.practicum.filmorate.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDate {
    String message() default "Film release date is earlier than the birthday of cinema.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
