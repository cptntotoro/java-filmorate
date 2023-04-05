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
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    @Autowired
    private MockMvc mockMvc;
    private User userValid1;

    @BeforeEach
    void setup(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(new UserService(new InMemoryUserStorage()))).build();
    }

    @Test
    void addUserValid() throws Exception {
        userValid1 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid1);
        MvcResult userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);
        userValid1.setId(1);
        assertEquals(userValid1, userReceived);
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
        userValid1 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid1);
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
        userValid1.setId(1);
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

        userValid1 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid1);
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

    @Test
    void getById() throws Exception {
        userValid1 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid1);
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        MvcResult userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + 1))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);
        assertEquals(userReceived.getId(), 1);
    }

    @Test
    void addFriend() throws Exception {
        userValid1 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid1);
        MvcResult userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        User userFriendValid = new User("userFriend@ya.ru", "userFriendLogin", "userFriendName", LocalDate.of(1955, 4, 11));
        String userFriendString = OBJECT_MAPPER.writeValueAsString(userFriendValid);
        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userFriendString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userFriendReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        String id = String.valueOf(userReceived.getId());
        String friendId = String.valueOf(userFriendReceived.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + id + "/friends/" + friendId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + id))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + friendId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userFriendReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        assertTrue(userReceived.getFriends().contains(userFriendReceived.getId()));
        assertTrue(userFriendReceived.getFriends().contains(userReceived.getId()));
    }

    @Test
    void removeFriend() throws Exception {
        userValid1 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid1);
        MvcResult userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        User userFriendValid = new User("userFriend@ya.ru", "userFriendLogin", "userFriendName", LocalDate.of(1955, 4, 11));
        String userFriendString = OBJECT_MAPPER.writeValueAsString(userFriendValid);
        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userFriendString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userFriendReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        String id = String.valueOf(userReceived.getId());
        String friendId = String.valueOf(userFriendReceived.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + id + "/friends/"+friendId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + id))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + friendId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userFriendReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        assertTrue(userReceived.getFriends().contains(userFriendReceived.getId()));
        assertTrue(userFriendReceived.getFriends().contains(userReceived.getId()));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/" + id + "/friends/" + friendId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + id))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + friendId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userFriendReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        assertFalse(userReceived.getFriends().contains(userFriendReceived.getId()));
        assertFalse(userFriendReceived.getFriends().contains(userReceived.getId()));
    }

    @Test
    void getFriends() throws Exception {
        userValid1 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userString = OBJECT_MAPPER.writeValueAsString(userValid1);
        MvcResult userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        User userFriendValid = new User("userFriend@ya.ru", "userFriendLogin", "userFriendName", LocalDate.of(1955, 4, 11));
        String userFriendString = OBJECT_MAPPER.writeValueAsString(userFriendValid);
        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userFriendString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userFriendReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        String id = String.valueOf(userReceived.getId());
        String friendId = String.valueOf(userFriendReceived.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + id + "/friends/" + friendId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + id))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        userValidResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + friendId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userFriendReceived = OBJECT_MAPPER.readValue(userValidResult.getResponse().getContentAsString(), User.class);

        assertEquals(userReceived.getFriends().size(), 1);
        assertEquals(userFriendReceived.getFriends().size(), 1);
    }

    @Test
    void getCommonFriends() throws Exception {

        userValid1 = new User("user@ya.ru", "userLogin", "userName", LocalDate.of(1974, 3, 15));
        String userValid1String = OBJECT_MAPPER.writeValueAsString(userValid1);
        MvcResult userValid1Result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userValid1String))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User user1Received = OBJECT_MAPPER.readValue(userValid1Result.getResponse().getContentAsString(), User.class);

        User userValid2 = new User("userFriend@ya.ru", "userFriendLogin", "userFriendName", LocalDate.of(1955, 4, 11));
        String userValid2String = OBJECT_MAPPER.writeValueAsString(userValid2);
        MvcResult userValid2Result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userValid2String))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User user2Received = OBJECT_MAPPER.readValue(userValid2Result.getResponse().getContentAsString(), User.class);

        User userCommonFriend = new User("userCommonFriend@ya.ru", "userCommonFriendLogin", "userCommonFriendName", LocalDate.of(1985, 8, 18));
        String userCommonFriendString = OBJECT_MAPPER.writeValueAsString(userCommonFriend);
        MvcResult userCommonFriendResult = mockMvc.perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userCommonFriendString))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        User userCommonFriendReceived = OBJECT_MAPPER.readValue(userCommonFriendResult.getResponse().getContentAsString(), User.class);

        String user1ReceivedId = String.valueOf(user1Received.getId());
        String user2ReceivedId = String.valueOf(user2Received.getId());
        String userCommonFriendReceivedId = String.valueOf(userCommonFriendReceived.getId());

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + user1ReceivedId + "/friends/" + userCommonFriendReceivedId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/" + user2ReceivedId + "/friends/" + userCommonFriendReceivedId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        userValid1Result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + user1ReceivedId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        user1Received = OBJECT_MAPPER.readValue(userValid1Result.getResponse().getContentAsString(), User.class);

        userValid2Result = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + user2ReceivedId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        user2Received = OBJECT_MAPPER.readValue(userValid2Result.getResponse().getContentAsString(), User.class);

        user1ReceivedId = String.valueOf(user1Received.getId());
        user2ReceivedId = String.valueOf(user2Received.getId());

        MvcResult getCommonFriendsResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/" + user1ReceivedId + "/friends/common/" + user2ReceivedId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        List<User> usersReturned = OBJECT_MAPPER.readValue(getCommonFriendsResult.getResponse().getContentAsString(), new TypeReference<>() {});
        assertEquals(usersReturned.size(), 1);
        assertTrue(usersReturned.contains(userCommonFriendReceived));
    }
}
