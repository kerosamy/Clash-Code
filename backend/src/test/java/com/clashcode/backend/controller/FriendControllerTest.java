package com.clashcode.backend.controller;

import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.exception.FriendRequestExistsException;
import com.clashcode.backend.exception.FriendRequestNotFoundException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.FriendService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FriendService friendService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public FriendService friendService() {
            return Mockito.mock(FriendService.class);
        }
    }

    // helper to create a domain User for AuthenticationPrincipal
    private User makeUser(Long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    @Test
    @DisplayName("POST /friends/send/{username} - success (200 OK) and service invoked")
    void sendFriendRequest_success() throws Exception {
        User sender = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(sender, null);

        mockMvc.perform(post("/friends/send/bob")
                        .with(authentication(auth)))
                .andExpect(status().isOk());

        verify(friendService, times(1)).sendFriendRequest(sender, "bob");
    }

    @Test
    @DisplayName("POST /friends/send/{username} - when FriendRequestExistsException thrown -> 409 Conflict")
    void sendFriendRequest_alreadyExists_conflict() throws Exception {
        User sender = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(sender, null);

        doThrow(new FriendRequestExistsException("Already exists"))
                .when(friendService).sendFriendRequest(sender, "bob");

        mockMvc.perform(post("/friends/send/bob")
                        .with(authentication(auth)))
                .andExpect(status().isConflict());

        verify(friendService, times(1)).sendFriendRequest(sender, "bob");
    }

    @Test
    @DisplayName("POST /friends/accept/{username} - success (200 OK) and service invoked")
    void acceptFriendRequest_success() throws Exception {
        User user = makeUser(2L, "bob");
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

        mockMvc.perform(post("/friends/accept/alice")
                        .with(authentication(auth)))
                .andExpect(status().isOk());

        verify(friendService, times(1)).acceptFriendRequest(user, "alice");
    }

    @Test
    @DisplayName("POST /friends/accept/{username} - when FriendRequestNotFoundException -> 404 Not Found")
    void acceptFriendRequest_notFound() throws Exception {
        User user = makeUser(2L, "bob");
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

        doThrow(new FriendRequestNotFoundException())
                .when(friendService).acceptFriendRequest(user, "alice");

        mockMvc.perform(post("/friends/accept/alice")
                        .with(authentication(auth)))
                .andExpect(status().isNotFound());

        verify(friendService, times(1)).acceptFriendRequest(user, "alice");
    }

    @Test
    @DisplayName("DELETE /friends/reject/{username} - success (200 OK) and service invoked")
    void rejectFriendRequest_success() throws Exception {
        User user = makeUser(2L, "bob");
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

        mockMvc.perform(delete("/friends/reject/alice")
                        .with(authentication(auth)))
                .andExpect(status().isOk());

        verify(friendService, times(1)).rejectFriendRequest(user, "alice");
    }

    @Test
    @DisplayName("DELETE /friends/reject/{username} - when FriendRequestExistsException (already accepted) -> 409 Conflict")
    void rejectFriendRequest_alreadyAccepted_conflict() throws Exception {
        User user = makeUser(2L, "bob");
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

        doThrow(new FriendRequestExistsException("Already Accepted"))
                .when(friendService).rejectFriendRequest(user, "alice");

        mockMvc.perform(delete("/friends/reject/alice")
                        .with(authentication(auth)))
                .andExpect(status().isConflict());

        verify(friendService, times(1)).rejectFriendRequest(user, "alice");
    }

    @Test
    @DisplayName("DELETE /friends/remove/{username} - success (200 OK) and service invoked")
    void removeFriend_success() throws Exception {
        User user = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

        mockMvc.perform(delete("/friends/remove/bob")
                        .with(authentication(auth)))
                .andExpect(status().isOk());

        verify(friendService, times(1)).removeFriend(user, "bob");
    }

    @Test
    @DisplayName("DELETE /friends/remove/{username} - when FriendRequestNotFoundException -> 404 Not Found")
    void removeFriend_notFound() throws Exception {
        User user = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

        doThrow(new FriendRequestNotFoundException())
                .when(friendService).removeFriend(user, "bob");

        mockMvc.perform(delete("/friends/remove/bob")
                        .with(authentication(auth)))
                .andExpect(status().isNotFound());

        verify(friendService, times(1)).removeFriend(user, "bob");
    }

    @Test
    @DisplayName("GET /friends/status/{username} - success returns FriendStatus in body")
    void getStatus_success_returnsFriendStatus() throws Exception {
        User user = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

        when(friendService.getStatus(user, "bob")).thenReturn(FriendStatus.FRIENDS);

        mockMvc.perform(get("/friends/status/bob")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                // root JSON value should be the enum name
                .andExpect(jsonPath("$").value("FRIENDS"));

        verify(friendService, times(1)).getStatus(user, "bob");
    }

    @Test
    @DisplayName("GET /friends/status/{username} - when IllegalArgumentException thrown -> handled by controller and returns 400 with body")
    void getStatus_illegalArgument_handledAsBadRequestWithBody() throws Exception {
        User user = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null);

        doThrow(new IllegalArgumentException("Cannot use your username"))
                .when(friendService).getStatus(user, "alice");

        mockMvc.perform(get("/friends/status/alice")
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // "error" field contains the message
                .andExpect(jsonPath("$.error").value("Cannot use your username"))
                // "timestamp" field exists (value not asserted exactly)
                .andExpect(jsonPath("$.timestamp").exists());

        verify(friendService, times(1)).getStatus(user, "alice");
    }

    @Test
    @DisplayName("POST /friends/send/{username} - when service throws IllegalArgumentException -> handled and returns 400 with body")
    void sendFriendRequest_illegalArgument_handledAsBadRequest() throws Exception {
        User sender = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(sender, null);

        doThrow(new IllegalArgumentException("Cannot use your username"))
                .when(friendService).sendFriendRequest(sender, "alice");

        mockMvc.perform(post("/friends/send/alice")
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot use your username"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(friendService, times(1)).sendFriendRequest(sender, "alice");
    }
}

