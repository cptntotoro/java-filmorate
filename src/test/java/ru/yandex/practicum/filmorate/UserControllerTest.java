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
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
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
public class UserControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mockMvc;
    private User userValid;

    @BeforeEach
    void setup(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(
                new UserService(
                        new InMemoryUserStorage()
                ))).build();
    }

    @Test
    void addUserValid() throws Exception {
        userValid = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid);
        MvcResult userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);
        userValid.setId(1);
        assertEquals(userValid, userReceived);
    }

    @Test
    void addUserEmptyName() throws Exception {
        User userEmptyName = new User("user@ya.ru", "userLogin", "", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userEmptyName);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userReceived = OBJECT_MAPPER.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        assertEquals(userReceived.getName(), userEmptyName.getLogin());
    }

    @Test
    void addUserInvalidLogin() throws Exception {
        User userInvalidLogin = new User("user@ya.ru", "user InvalidLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userInvalidLogin);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }

    @Test
    void addUserInvalidEmail() throws Exception {
        User userInvalidEmail = new User("ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userInvalidEmail);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }

    @Test
    void addUserInvalidBirthday() throws Exception {
        User userInvalidBirthday = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(2040, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userInvalidBirthday);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andReturn();
    }

    @Test
    void updateUserValid() throws Exception {
        userValid = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userValidUpdate = new User(1, "userUpdate@ya.ru", "userUpdateLogin", "userUpdateName", LocalDate.of(1978, 3, 15));
        String userValidUpdateString = OBJECT_MAPPER.writeValueAsString(userValidUpdate);
        MvcResult userValidUpdateResult = mockMvc.perform(MockMvcRequestBuilders
                        .put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userValidUpdateString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userReceived = OBJECT_MAPPER.readValue(userValidUpdateResult.getResponse().getContentAsString(), User.class);
        userValid.setId(1);
        assertEquals(userValidUpdate, userReceived);
    }

    @Test
    void updateUserInvalid() throws Exception {
        User userInvalidUpdate = new User(10, "userUpdate@ya.ru", "userUpdateLogin", "userUpdateName", LocalDate.of(1978, 3, 15));
        String userInvalidUpdateString = OBJECT_MAPPER.writeValueAsString(userInvalidUpdate);
        assertThrows(NestedServletException.class, () -> mockMvc.perform(MockMvcRequestBuilders
                .put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userInvalidUpdateString)));
    }

    @Test
    void getAllUsers() throws Exception {

        userValid = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        MvcResult getAllUsersResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users"))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        List<User> usersReturned = OBJECT_MAPPER.readValue(getAllUsersResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(usersReturned.size(), 2);
    }
}
