package com.clashcode.backend.controller;

import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.exception.FriendRequestExistsException;
import com.clashcode.backend.exception.FriendRequestNotFoundException;
import com.clashcode.backend.exception.GlobalExceptionHandler;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FriendControllerTest {

    @Mock
    private FriendService friendService;

    @InjectMocks
    private FriendController friendController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(friendController)
                .setControllerAdvice(new GlobalExceptionHandler()) // matches project style
                .build();

        SecurityContextHolder.clearContext();
    }

    private void authenticate(User user) {
        TestingAuthenticationToken auth = new TestingAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // --------------------------------------------------------
    // 1) SEND FRIEND REQUEST - SUCCESS
    // --------------------------------------------------------
    @Test
    void sendFriendRequest_success() throws Exception {
        User sender = new User();
        sender.setId(1L);
        sender.setUsername("alice");

        authenticate(sender);

        doNothing().when(friendService).sendFriendRequest(sender, "bob");

        mockMvc.perform(post("/friends/send/bob")
                        .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isOk());

        verify(friendService, times(1)).sendFriendRequest(sender, "bob");
    }

    // --------------------------------------------------------
    // 2) SEND FRIEND REQUEST - CONFLICT
    // --------------------------------------------------------
    @Test
    void sendFriendRequest_conflict() throws Exception {
        User sender = new User();
        sender.setId(1L);
        sender.setUsername("alice");

        authenticate(sender);

        doThrow(new FriendRequestExistsException("Request already exists"))
                .when(friendService).sendFriendRequest(sender, "bob");

        mockMvc.perform(post("/friends/send/bob")
                        .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isConflict());
    }

    // --------------------------------------------------------
    // 3) ACCEPT FRIEND REQUEST - NOT FOUND
    // --------------------------------------------------------
    @Test
    void acceptFriendRequest_notFound() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setUsername("bob");

        authenticate(user);

        doThrow(new FriendRequestNotFoundException())
                .when(friendService).acceptFriendRequest(user, "alice");

        mockMvc.perform(post("/friends/accept/alice")
                        .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isNotFound());
    }

    // --------------------------------------------------------
    // 4) REJECT FRIEND REQUEST - CONFLICT
    // --------------------------------------------------------
    @Test
    void rejectFriendRequest_conflict() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setUsername("bob");

        authenticate(user);

        doThrow(new FriendRequestExistsException("Already processed"))
                .when(friendService).rejectFriendRequest(user, "alice");

        mockMvc.perform(delete("/friends/reject/alice")
                        .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isConflict());
    }

    // --------------------------------------------------------
    // 5) GET STATUS - SUCCESS
    // --------------------------------------------------------
    @Test
    void getStatus_success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");

        authenticate(user);

        when(friendService.getStatus(user, "bob"))
                .thenReturn(FriendStatus.FRIENDS);

        mockMvc.perform(get("/friends/status/bob")
                        .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isOk())
                .andExpect(content().string("FRIENDS"));
    }

    // --------------------------------------------------------
    // 6) GET STATUS - ILLEGAL ARG (BAD REQUEST)
    // --------------------------------------------------------
    @Test
    void getStatus_illegalArgument() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");

        authenticate(user);

        doThrow(new IllegalArgumentException("Cannot use your own username"))
                .when(friendService).getStatus(user, "alice");

        mockMvc.perform(get("/friends/status/alice")
                        .principal(SecurityContextHolder.getContext().getAuthentication()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot use your own username"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
