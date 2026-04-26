package com.clashcode.backend.controller;

import com.clashcode.backend.dto.FriendDto;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.enums.UserStatus;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.FriendService;
import com.clashcode.backend.service.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList())
        );
    }

    @Test
    @DisplayName("POST /friends/send/{username} - Success")
    void sendFriendRequest_success() throws Exception {
        setupSecurityContext();
        doNothing().when(friendService).sendFriendRequest(any(User.class), eq("friend1"));

        mockMvc.perform(post("/friends/send/friend1"))
                .andExpect(status().isOk());

        verify(friendService, times(1)).sendFriendRequest(any(User.class), eq("friend1"));
    }

    @Test
    @DisplayName("POST /friends/accept/{username} - Success")
    void acceptFriendRequest_success() throws Exception {
        setupSecurityContext();
        doNothing().when(friendService).acceptFriendRequest(any(User.class), eq("friend1"));

        mockMvc.perform(post("/friends/accept/friend1"))
                .andExpect(status().isOk());

        verify(friendService, times(1)).acceptFriendRequest(any(User.class), eq("friend1"));
    }

    @Test
    @DisplayName("DELETE /friends/reject/{username} - Success")
    void rejectFriendRequest_success() throws Exception {
        setupSecurityContext();
        doNothing().when(friendService).rejectFriendRequest(any(User.class), eq("friend1"));

        mockMvc.perform(delete("/friends/reject/friend1"))
                .andExpect(status().isOk());

        verify(friendService, times(1)).rejectFriendRequest(any(User.class), eq("friend1"));
    }

    @Test
    @DisplayName("DELETE /friends/remove/{username} - Success")
    void removeFriend_success() throws Exception {
        setupSecurityContext();
        doNothing().when(friendService).removeFriend(any(User.class), eq("friend1"));

        mockMvc.perform(delete("/friends/remove/friend1"))
                .andExpect(status().isOk());

        verify(friendService, times(1)).removeFriend(any(User.class), eq("friend1"));
    }

    @Test
    @DisplayName("GET /friends/status/{username} - Success")
    void getFriendshipStatus_success() throws Exception {
        setupSecurityContext();
        when(friendService.getStatus(any(User.class), eq("friend1")))
                .thenReturn(FriendStatus.FRIENDS);

        mockMvc.perform(get("/friends/status/friend1"))
                .andExpect(status().isOk())
                .andExpect(content().json("\"FRIENDS\""));

        verify(friendService, times(1)).getStatus(any(User.class), eq("friend1"));
    }

    @Test
    @DisplayName("GET /friends/list - Success")
    void getFriendsList_success() throws Exception {
        setupSecurityContext();
        FriendDto friend = FriendDto.builder()
                .username("friend1")
                .currentRate(1500)
                .imgUrl(null)
                .userStatus(UserStatus.ONLINE)
                .status(FriendStatus.FRIENDS)
                .build();

        Page<FriendDto> page = new PageImpl<>(List.of(friend), PageRequest.of(0, 20), 1);

        when(friendService.getFriendsList(any(User.class), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/friends/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("friend1"));
    }

    @Test
    @DisplayName("GET /friends/list - Custom pagination")
    void getFriendsList_customPagination() throws Exception {
        setupSecurityContext();
        Page<FriendDto> page = new PageImpl<>(List.of(), PageRequest.of(2, 10), 0);

        when(friendService.getFriendsList(any(User.class), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/friends/list")
                        .param("page", "2")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("GET /friends/sent-requests - Success")
    void getSentFriendRequests_success() throws Exception {
        setupSecurityContext();
        FriendDto friend = FriendDto.builder()
                .username("friend2")
                .currentRate(1800)
                .imgUrl(null)
                .userStatus(UserStatus.ONLINE)
                .status(FriendStatus.PENDING_SENT)
                .build();

        Page<FriendDto> page = new PageImpl<>(List.of(friend), PageRequest.of(0, 20), 1);

        when(friendService.getSentFriendRequests(any(User.class), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/friends/sent-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("friend2"));
    }

    @Test
    @DisplayName("GET /friends/received-requests - Success")
    void getReceivedFriendRequests_success() throws Exception {
        setupSecurityContext();
        FriendDto friend = FriendDto.builder()
                .username("friend3")
                .currentRate(900)
                .imgUrl(null)
                .userStatus(UserStatus.ONLINE)
                .status(FriendStatus.PENDING_SENT)
                .build();

        Page<FriendDto> page = new PageImpl<>(List.of(friend), PageRequest.of(0, 20), 1);

        when(friendService.getReceivedFriendRequests(any(User.class), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/friends/received-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("friend3"));
    }

    @Test
    @DisplayName("GET /friends/search - Success")
    void searchFriends_success() throws Exception {
        setupSecurityContext();
        FriendDto friend = FriendDto.builder()
                .username("friendMatch")
                .currentRate(1200)
                .imgUrl(null)
                .userStatus(UserStatus.ONLINE)
                .status(FriendStatus.FRIENDS)
                .build();

        Page<FriendDto> page = new PageImpl<>(List.of(friend), PageRequest.of(0, 20), 1);

        when(friendService.searchFriendsByUsername(any(User.class), eq("friend"), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/friends/search")
                        .param("query", "friend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("friendMatch"));
    }

    @Test
    @DisplayName("GET /friends/search - Empty query")
    void searchFriends_emptyQuery() throws Exception {
        setupSecurityContext();

        mockMvc.perform(get("/friends/search")
                        .param("query", ""))
                .andExpect(status().isBadRequest());

        verify(friendService, never()).searchFriendsByUsername(any(), any(), any());
    }

    @Test
    @DisplayName("GET /friends/search - Null query")
    void searchFriends_nullQuery() throws Exception {
        setupSecurityContext();

        mockMvc.perform(get("/friends/search"))
                .andExpect(status().isBadRequest());
    }
}
