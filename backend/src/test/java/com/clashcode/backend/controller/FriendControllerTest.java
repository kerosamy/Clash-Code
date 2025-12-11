package com.clashcode.backend.controller;

import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.exception.FriendRequestExistsException;
import com.clashcode.backend.exception.FriendRequestNotFoundException;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.FriendService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendController.class)
@ExtendWith(SpringExtension.class)
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

    private User makeUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    @Test
    void sendFriendRequest_success() throws Exception {
        User sender = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(sender, null);

        mockMvc.perform(post("/friends/send/bob")
                        .with(authentication(auth)))
                .andExpect(status().isOk());

        verify(friendService).sendFriendRequest(sender, "bob");
    }

    @Test
    void sendFriendRequest_conflict() throws Exception {
        User sender = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(sender, null);

        doThrow(new FriendRequestExistsException("exists"))
                .when(friendService).sendFriendRequest(sender, "bob");

        mockMvc.perform(post("/friends/send/bob")
                        .with(authentication(auth)))
                .andExpect(status().isConflict());
    }

    @Test
    void acceptFriendRequest_notFound() throws Exception {
        User u = makeUser(2L, "bob");
        Authentication auth = new UsernamePasswordAuthenticationToken(u, null);

        doThrow(new FriendRequestNotFoundException())
                .when(friendService).acceptFriendRequest(u, "alice");

        mockMvc.perform(post("/friends/accept/alice")
                        .with(authentication(auth)))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectFriendRequest_conflict() throws Exception {
        User u = makeUser(2L, "bob");
        Authentication auth = new UsernamePasswordAuthenticationToken(u, null);

        doThrow(new FriendRequestExistsException("already accepted"))
                .when(friendService).rejectFriendRequest(u, "alice");

        mockMvc.perform(delete("/friends/reject/alice")
                        .with(authentication(auth)))
                .andExpect(status().isConflict());
    }

    @Test
    void getStatus_success() throws Exception {
        User u = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(u, null);

        when(friendService.getStatus(u, "bob")).thenReturn(FriendStatus.FRIENDS);

        mockMvc.perform(get("/friends/status/bob")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("FRIENDS"));
    }

    @Test
    void getStatus_badRequest_illegalArgument() throws Exception {
        User u = makeUser(1L, "alice");
        Authentication auth = new UsernamePasswordAuthenticationToken(u, null);

        doThrow(new IllegalArgumentException("Cannot use your username"))
                .when(friendService).getStatus(u, "alice");

        mockMvc.perform(get("/friends/status/alice")
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot use your username"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
