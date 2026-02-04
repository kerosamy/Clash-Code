package com.clashcode.backend.controller;

import com.clashcode.backend.dto.NotificationDto;
import com.clashcode.backend.mapper.NotificationMapper;
import com.clashcode.backend.model.Notification;
import com.clashcode.backend.model.User;
import com.clashcode.backend.service.JwtService;
import com.clashcode.backend.service.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private NotificationMapper notificationMapper;

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
    @DisplayName("GET /notifications - Success")
    void getUserNotifications_success() throws Exception {
        setupSecurityContext();

        Notification notification = new Notification();
        notification.setId(1L);
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(1L);

        Page<Notification> notificationPage = new PageImpl<>(List.of(notification));
        when(notificationService.getUserNotificationsPaginated(eq(1L), eq(0), eq(10)))
                .thenReturn(notificationPage);
        when(notificationMapper.toDto(any(Notification.class))).thenReturn(notificationDto);

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("GET /notifications - With category filter")
    void getUserNotifications_withCategory() throws Exception {
        setupSecurityContext();

        Notification notification = new Notification();
        notification.setId(1L);
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(1L);

        Page<Notification> notificationPage = new PageImpl<>(List.of(notification));
        when(notificationService.getUserNotificationsByCategory(eq(1L), eq("FRIEND_REQUEST"), eq(0), eq(10)))
                .thenReturn(notificationPage);
        when(notificationMapper.toDto(any(Notification.class))).thenReturn(notificationDto);

        mockMvc.perform(get("/notifications")
                        .param("category", "FRIEND_REQUEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @DisplayName("GET /notifications - Custom pagination")
    void getUserNotifications_customPagination() throws Exception {
        setupSecurityContext();

        Page<Notification> notificationPage = new PageImpl<>(List.of());
        when(notificationService.getUserNotificationsPaginated(eq(1L), eq(2), eq(20)))
                .thenReturn(notificationPage);

        mockMvc.perform(get("/notifications")
                        .param("page", "2")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("GET /notifications - Unauthorized")
    void getUserNotifications_unauthorized() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /notifications/unread-count - Success")
    void getUnreadCount_success() throws Exception {
        setupSecurityContext();
        when(notificationService.getUnreadCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/notifications/unread-count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    @DisplayName("GET /notifications/unread-count - Unauthorized")
    void getUnreadCount_unauthorized() throws Exception {
        mockMvc.perform(get("/notifications/unread-count"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /notifications/{notificationId}/read - Success")
    void markAsRead_success() throws Exception {
        setupSecurityContext();
        doNothing().when(notificationService).markAsRead(1L, 1L);

        mockMvc.perform(patch("/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAsRead(1L, 1L);
    }

    @Test
    @DisplayName("PATCH /notifications/{notificationId}/read - Unauthorized")
    void markAsRead_unauthorized() throws Exception {
        mockMvc.perform(patch("/notifications/1/read"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /notifications/{notificationId} - Success")
    void getNotification_success() throws Exception {
        setupSecurityContext();

        Notification notification = new Notification();
        notification.setId(1L);
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(1L);

        when(notificationService.getNotificationById(1L, 1L)).thenReturn(notification);
        when(notificationMapper.toDto(notification)).thenReturn(notificationDto);

        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /notifications/{notificationId} - Unauthorized")
    void getNotification_unauthorized() throws Exception {
        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isUnauthorized());
    }
}
