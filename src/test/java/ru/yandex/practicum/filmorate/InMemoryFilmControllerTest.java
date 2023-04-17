//package ru.yandex.practicum.filmorate;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.util.NestedServletException;
//import ru.yandex.practicum.filmorate.controllers.FilmController;
//import ru.yandex.practicum.filmorate.controllers.UserController;
//import ru.yandex.practicum.filmorate.models.Film;
//import ru.yandex.practicum.filmorate.models.User;
//import ru.yandex.practicum.filmorate.service.FilmService;
//import ru.yandex.practicum.filmorate.service.UserService;
//import ru.yandex.practicum.filmorate.storage.FilmStorage;
//import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
//import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
//import ru.yandex.practicum.filmorate.storage.UserStorage;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class FilmControllerTest {
//
//    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
//    @Autowired
//    private MockMvc mockMvc;
//    private Film filmValid;
//
//    FilmStorage filmStorage;
//    UserStorage userStorage;
//
//    @BeforeEach
//    public void setup() {
//        filmStorage = new InMemoryFilmStorage();
//        userStorage = new InMemoryUserStorage();
//
//        this.mockMvc = MockMvcBuilders.standaloneSetup(new FilmController(
//                new FilmService(
//                        filmStorage,
//                        userStorage
//                )),
//                new UserController(new UserService(userStorage))).build();
//    }
//
//    @Test
//    void addFilmValid() throws Exception {
//        filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), 121);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmValid);
//        MvcResult filmValidResult = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//        Film filmReceived = OBJECT_MAPPER.readValue(filmValidResult.getResponse().getContentAsString(), Film.class);
//        filmValid.setId(1);
//        assertEquals(filmValid, filmReceived);
//    }
//
//    @Test
//    void addFilmEmptyName() throws Exception {
//        Film filmEmptyName = new Film("", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), 121);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmEmptyName);
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is4xxClientError())
//                .andDo(print())
//                .andReturn();
//    }
//
//    @Test
//    void addFilmInvalidDescription() throws Exception {
//        Film filmInvalidDescription = new Film("Some Like It Hot", "After two male musicians witness a mob hit, " +
//                "they flee the state in an all-female band disguised as women, but further complications set in. " +
//                "After two Chicago musicians, Joe and Jerry, witness the the St. Valentine's Day massacre...",
//                LocalDate.of(1959, 3, 19), 121);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmInvalidDescription);
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is4xxClientError())
//                .andDo(print())
//                .andReturn();
//    }
//
//    @Test
//    void addFilmInvalidReleaseDate() throws Exception {
//        Film filmInvalidReleaseDate = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1800, 3, 19), 121);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmInvalidReleaseDate);
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is4xxClientError())
//                .andDo(print())
//                .andReturn();
//    }
//
//    @Test
//    void addFilmInvalidDuration() throws Exception {
//        Film filmInvalidDuration = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), -10);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmInvalidDuration);
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is4xxClientError())
//                .andDo(print())
//                .andReturn();
//    }
//
//    @Test
//    void updateFilmValid() throws Exception {
//        filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), 121);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmValid);
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        Film filmValidUpdate = new Film(1, "Around the World in 80 Days", "To win a bet, an eccentric British inventor...",
//                LocalDate.of(2004, 6, 13), 120);
//        String filmValidUpdateString = OBJECT_MAPPER.writeValueAsString(filmValidUpdate);
//        MvcResult filmValidUpdateResult = mockMvc.perform(MockMvcRequestBuilders
//                        .put("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmValidUpdateString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        Film filmReceived = OBJECT_MAPPER.readValue(filmValidUpdateResult.getResponse().getContentAsString(), Film.class);
//        assertEquals(filmValidUpdate, filmReceived);
//    }
//
//    @Test
//    void updateFilmInvalid() throws Exception {
//        Film filmInvalidUpdate = new Film(10, "Around the World in 80 Days", "To win a bet, an eccentric British inventor...",
//                LocalDate.of(2004, 6, 13), 120);
//        String filmInvalidUpdateString = OBJECT_MAPPER.writeValueAsString(filmInvalidUpdate);
//        assertThrows(NestedServletException.class, () -> mockMvc.perform(MockMvcRequestBuilders
//                .put("/films")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(filmInvalidUpdateString)));
//    }
//
//    @Test
//    void getAllFilms() throws Exception {
//        filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), 121);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmValid);
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        MvcResult getAllFilmsResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/films"))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        List<Film> filmsReturned = OBJECT_MAPPER.readValue(getAllFilmsResult.getResponse().getContentAsString(), new TypeReference<>() {});
//        assertEquals(filmsReturned.size(), 2);
//    }
//    @Test
//    void getById() throws Exception {
//        filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), 121);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmValid);
//        MvcResult filmResult = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        Film filmReceived = OBJECT_MAPPER.readValue(filmResult.getResponse().getContentAsString(), Film.class);
//        String filmId = String.valueOf(filmReceived.getId());
//
//        filmResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/films/" + filmId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        Film filmReceivedWithGetById = OBJECT_MAPPER.readValue(filmResult.getResponse().getContentAsString(), Film.class);
//
//        assertEquals(filmReceived, filmReceivedWithGetById);
//    }
//
//    @Test
//    void addLike() throws Exception {
//        filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), 121);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmValid);
//        MvcResult filmResult = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        Film filmReceived = OBJECT_MAPPER.readValue(filmResult.getResponse().getContentAsString(), Film.class);
//
//        User user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
//        String userString = OBJECT_MAPPER.writeValueAsString(user);
//        MvcResult userResult = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        User userReceived = OBJECT_MAPPER.readValue(userResult.getResponse().getContentAsString(), User.class);
//
//        String userId = String.valueOf(userReceived.getId());
//        String filmId = String.valueOf(filmReceived.getId());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put("/films/" + filmId + "/like/" + userId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        filmResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/films/" + filmId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        filmReceived = OBJECT_MAPPER.readValue(filmResult.getResponse().getContentAsString(), Film.class);
//
//        userResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/users/" + userId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        userReceived = OBJECT_MAPPER.readValue(userResult.getResponse().getContentAsString(), User.class);
//
//        assertTrue(filmReceived.getWhoLiked().contains(userReceived.getId()));
//        assertTrue(userReceived.getFilmsLiked().contains(filmReceived.getId()));
//    }
//
//    @Test
//    void removeLike() throws Exception {
//        Film filmValid = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), 121);
//        String filmString = OBJECT_MAPPER.writeValueAsString(filmValid);
//        MvcResult filmResult = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(filmString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        Film filmReceived = OBJECT_MAPPER.readValue(filmResult.getResponse().getContentAsString(), Film.class);
//
//        User user = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
//        String userString = OBJECT_MAPPER.writeValueAsString(user);
//        MvcResult userResult = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userString))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        User userReceived = OBJECT_MAPPER.readValue(userResult.getResponse().getContentAsString(), User.class);
//
//        String userId = String.valueOf(userReceived.getId());
//        String filmId = String.valueOf(filmReceived.getId());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put("/films/" + filmId + "/like/" + userId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        filmResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/films/" + filmId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        filmReceived = OBJECT_MAPPER.readValue(filmResult.getResponse().getContentAsString(), Film.class);
//
//        userResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/users/" + userId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        userReceived = OBJECT_MAPPER.readValue(userResult.getResponse().getContentAsString(), User.class);
//
//        assertTrue(filmReceived.getWhoLiked().contains(userReceived.getId()));
//        assertTrue(userReceived.getFilmsLiked().contains(filmReceived.getId()));
//
//        userId = String.valueOf(userReceived.getId());
//        filmId = String.valueOf(filmReceived.getId());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .DELETE("/films/" + filmId + "/like/" + userId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        filmResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/films/" + filmId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        filmReceived = OBJECT_MAPPER.readValue(filmResult.getResponse().getContentAsString(), Film.class);
//
//        userResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/users/" + userId))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        userReceived = OBJECT_MAPPER.readValue(userResult.getResponse().getContentAsString(), User.class);
//
//        assertFalse(filmReceived.getWhoLiked().contains(userReceived.getId()));
//        assertFalse(userReceived.getFilmsLiked().contains(filmReceived.getId()));
//    }
//
//    @Test
//    void getPopularFilms() throws Exception {
//        Film film1 = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), 121);
//        String film1String = OBJECT_MAPPER.writeValueAsString(film1);
//        MvcResult film1Result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(film1String))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        Film film1Received = OBJECT_MAPPER.readValue(film1Result.getResponse().getContentAsString(), Film.class);
//
//        Film film2 = new Film("Some Like It Hot", "After two male musicians witness a mob hit...",
//                LocalDate.of(1959, 3, 19), 121);
//        String film2String = OBJECT_MAPPER.writeValueAsString(film2);
//        MvcResult film2Result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/films")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(film2String))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        Film film2Received = OBJECT_MAPPER.readValue(film2Result.getResponse().getContentAsString(), Film.class);
//
//        User user1 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
//        String user1String = OBJECT_MAPPER.writeValueAsString(user1);
//        MvcResult user1Result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(user1String))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        User user1Received = OBJECT_MAPPER.readValue(user1Result.getResponse().getContentAsString(), User.class);
//
//        User user2 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
//        String user2String = OBJECT_MAPPER.writeValueAsString(user2);
//        MvcResult user2Result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(user2String))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        User user2Received = OBJECT_MAPPER.readValue(user2Result.getResponse().getContentAsString(), User.class);
//
//        String user1Id = String.valueOf(user1Received.getId());
//        String user2Id = String.valueOf(user2Received.getId());
//
//        String film1Id = String.valueOf(film1Received.getId());
//        String film2Id = String.valueOf(film2Received.getId());
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put("/films/" + film1Id + "/like/" + user1Id))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put("/films/" + film1Id + "/like/" + user2Id))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        mockMvc.perform(MockMvcRequestBuilders
//                        .put("/films/" + film2Id + "/like/" + user1Id))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        MvcResult getPopularWithoutValueResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/films/popular"))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        List<Film> filmsPopularWithoutValueReturned = OBJECT_MAPPER.readValue(getPopularWithoutValueResult.getResponse().getContentAsString(), new TypeReference<>() {});
//        assertEquals(filmsPopularWithoutValueReturned.size(), 2);
//
//        MvcResult getPopularWithValueResult = mockMvc.perform(MockMvcRequestBuilders
//                        .get("/films/popular")
//                        .param("count","1"))
//                .andExpect(status().is2xxSuccessful())
//                .andDo(print())
//                .andReturn();
//
//        List<Film> filmsPopularWithValueReturned = OBJECT_MAPPER.readValue(getPopularWithValueResult.getResponse().getContentAsString(), new TypeReference<>() {});
//        assertEquals(filmsPopularWithValueReturned.size(), 1);
//    }
//}
//
