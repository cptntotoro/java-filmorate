package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mockMvc;
    private Film filmValid;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new FilmController(
                new FilmService(
                        new InMemoryFilmStorage(),
                        new InMemoryUserStorage()
                ))).build();
    }

    @Test
    void addFilmValid() throws Exception {
        filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121);
        String filmString = OBJECT_MAPPER.writeValueAsString(filmValid);
        MvcResult filmValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();
        Film filmReceived = OBJECT_MAPPER.readValue(filmValidResult.getResponse().getContentAsString(), Film.class);
        filmValid.setId(1);
        assertEquals(filmValid, filmReceived);
    }

    @Test
    void addFilmEmptyName() throws Exception {
        Film filmEmptyName = new Film("", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121);
        String filmString = OBJECT_MAPPER.writeValueAsString(filmEmptyName);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmString))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }

    @Test
    void addFilmInvalidDescription() throws Exception {
        Film filmInvalidDescription = new Film("Some Like It Hot", "After two male musicians witness a mob hit, " +
                "they flee the state in an all-female band disguised as women, but further complications set in. " +
                "After two Chicago musicians, Joe and Jerry, witness the the St. Valentine's Day massacre...",
                LocalDate.of(1959, 3, 19), 121);
        String filmString = OBJECT_MAPPER.writeValueAsString(filmInvalidDescription);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmString))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }

    @Test
    void addFilmInvalidReleaseDate() throws Exception {
        Film filmInvalidReleaseDate = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1800, 3, 19), 121);
        String filmString = OBJECT_MAPPER.writeValueAsString(filmInvalidReleaseDate);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmString))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }

    @Test
    void addFilmInvalidDuration() throws Exception {
        Film filmInvalidDuration = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), -10);
        String filmString = OBJECT_MAPPER.writeValueAsString(filmInvalidDuration);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmString))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }

    @Test
    void updateFilmValid() throws Exception {
        filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121);
        String filmString = OBJECT_MAPPER.writeValueAsString(filmValid);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        Film filmValidUpdate = new Film(1, "Around the World in 80 Days", "To win a bet, an eccentric British inventor...",
                LocalDate.of(2004, 6, 13), 120);
        String filmValidUpdateString = OBJECT_MAPPER.writeValueAsString(filmValidUpdate);
        MvcResult filmValidUpdateResult = mockMvc.perform(MockMvcRequestBuilders
                        .put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmValidUpdateString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        Film filmReceived = OBJECT_MAPPER.readValue(filmValidUpdateResult.getResponse().getContentAsString(), Film.class);
        assertEquals(filmValidUpdate, filmReceived);
    }

    @Test
    void updateFilmInvalid() throws Exception {
        Film filmInvalidUpdate = new Film(10, "Around the World in 80 Days", "To win a bet, an eccentric British inventor...",
                LocalDate.of(2004, 6, 13), 120);
        String filmInvalidUpdateString = OBJECT_MAPPER.writeValueAsString(filmInvalidUpdate);
        assertThrows(NestedServletException.class, () -> mockMvc.perform(MockMvcRequestBuilders
                .put("/films")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filmInvalidUpdateString)));
    }

    @Test
    void getAllFilms() throws Exception {

        filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
                LocalDate.of(1959, 3, 19), 121);
        String filmString = OBJECT_MAPPER.writeValueAsString(filmValid);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        MvcResult getAllFilmsResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/films"))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        List<Film> filmsReturned = OBJECT_MAPPER.readValue(getAllFilmsResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(filmsReturned.size(), 2);
    }
}

