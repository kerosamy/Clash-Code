package com.clashcode.backend.service;

import com.clashcode.backend.enums.FriendRequestStatus;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.exception.FriendRequestExistsException;
import com.clashcode.backend.exception.FriendRequestNotFoundException;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.FriendStatusMapper;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.FriendRepository;
import com.clashcode.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private FriendStatusMapper friendStatusMapper;

    @InjectMocks
    private FriendService friendService;

    // Helpers
    private User makeUser(Long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    @Test
    void testSendFriendRequest_Success_savesPendingFriend() {
        User sender = makeUser(1L, "alice");
        User receiver = makeUser(2L, "bob");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(receiver));
        when(friendRepository.findRelationshipBetweenUsers(sender.getId(), receiver.getId()))
                .thenReturn(Optional.empty());

        friendService.sendFriendRequest(sender, "bob");

        ArgumentCaptor<Friend> captor = ArgumentCaptor.forClass(Friend.class);
        verify(friendRepository, times(1)).save(captor.capture());
        Friend saved = captor.getValue();

        assertNotNull(saved);
        assertEquals(sender, saved.getSender());
        assertEquals(receiver, saved.getReceiver());
        assertEquals(FriendRequestStatus.PENDING, saved.getStatus());
    }

    @Test
    void testSendFriendRequest_UserNotFound_throwsUserNotFoundException() {
        User sender = makeUser(1L, "alice");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> friendService.sendFriendRequest(sender, "ghost"));

        verify(friendRepository, never()).save(any());
    }

    @Test
    void testSendFriendRequest_RequestAlreadyExists_throwsFriendRequestExistsException() {
        User sender = makeUser(1L, "alice");
        User receiver = makeUser(2L, "bob");
        Friend existing = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(receiver));
        when(friendRepository.findRelationshipBetweenUsers(sender.getId(), receiver.getId()))
                .thenReturn(Optional.of(existing));

        FriendRequestExistsException ex = assertThrows(FriendRequestExistsException.class,
                () -> friendService.sendFriendRequest(sender, "bob"));

        assertTrue(ex.getMessage().contains(existing.getStatus().name()));
        verify(friendRepository, never()).save(argThat(f -> f != existing));
    }

    @Test
    void testSendFriendRequest_SameUser_throwsIllegalArgumentException() {
        User sender = makeUser(1L, "alice");

        // repository returns the same user instance as sender
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> friendService.sendFriendRequest(sender, "alice"));

        assertEquals("Cannot use your username", ex.getMessage());
        verify(friendRepository, never()).save(any());
    }

    @Test
    void testAcceptFriendRequest_Success_changesStatusToAcceptedAndSaves() {
        User sender = makeUser(1L, "alice");
        User receiver = makeUser(2L, "bob");
        Friend friendship = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));
        when(friendRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId()))
                .thenReturn(Optional.of(friendship));

        friendService.acceptFriendRequest(receiver, "alice");

        assertEquals(FriendRequestStatus.ACCEPTED, friendship.getStatus());
        verify(friendRepository, times(1)).save(friendship);
    }

    @Test
    void testAcceptFriendRequest_NotFound_throwsFriendRequestNotFoundException() {
        User receiver = makeUser(2L, "bob");
        User sender = makeUser(1L, "alice");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));
        when(friendRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId()))
                .thenReturn(Optional.empty());

        assertThrows(FriendRequestNotFoundException.class,
                () -> friendService.acceptFriendRequest(receiver, "alice"));

        verify(friendRepository, never()).save(any());
    }

    @Test
    void testAcceptFriendRequest_AlreadyAccepted_throwsFriendRequestExistsException() {
        User sender = makeUser(1L, "alice");
        User receiver = makeUser(2L, "bob");
        Friend friendship = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.ACCEPTED)
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));
        when(friendRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId()))
                .thenReturn(Optional.of(friendship));

        FriendRequestExistsException ex = assertThrows(FriendRequestExistsException.class,
                () -> friendService.acceptFriendRequest(receiver, "alice"));

        assertEquals("Already Accepted", ex.getMessage());
        verify(friendRepository, never()).save(argThat(f -> f == friendship && f.getStatus().equals(FriendRequestStatus.ACCEPTED)));
    }

    @Test
    void testRejectFriendRequest_Success_deletesFriendship() {
        User sender = makeUser(1L, "alice");
        User receiver = makeUser(2L, "bob");
        Friend friendship = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));
        when(friendRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId()))
                .thenReturn(Optional.of(friendship));

        friendService.rejectFriendRequest(receiver, "alice");

        verify(friendRepository, times(1)).delete(friendship);
    }

    @Test
    void testRejectFriendRequest_NotFound_throwsFriendRequestNotFoundException() {
        User receiver = makeUser(2L, "bob");
        User sender = makeUser(1L, "alice");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));
        when(friendRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId()))
                .thenReturn(Optional.empty());

        assertThrows(FriendRequestNotFoundException.class,
                () -> friendService.rejectFriendRequest(receiver, "alice"));

        verify(friendRepository, never()).delete(any());
    }

    @Test
    void testRejectFriendRequest_AlreadyAccepted_throwsFriendRequestExistsException() {
        User sender = makeUser(1L, "alice");
        User receiver = makeUser(2L, "bob");
        Friend friendship = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.ACCEPTED)
                .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(sender));
        when(friendRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId()))
                .thenReturn(Optional.of(friendship));

        FriendRequestExistsException ex = assertThrows(FriendRequestExistsException.class,
                () -> friendService.rejectFriendRequest(receiver, "alice"));

        assertEquals("Already Accepted", ex.getMessage());
        verify(friendRepository, never()).delete(friendship);
    }

    @Test
    void testGetStatus_WithFriendship_usesMapperAndReturnsMappedStatus() {
        User userOne = makeUser(1L, "alice");
        User userTwo = makeUser(2L, "bob");
        Friend friendship = Friend.builder()
                .sender(userOne)
                .receiver(userTwo)
                .status(FriendRequestStatus.ACCEPTED)
                .build();

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(userTwo));
        when(friendRepository.findRelationshipBetweenUsers(userOne.getId(), userTwo.getId()))
                .thenReturn(Optional.of(friendship));
        when(friendStatusMapper.map(userOne, friendship)).thenReturn(FriendStatus.FRIENDS);

        FriendStatus status = friendService.getStatus(userOne, "bob");

        assertEquals(FriendStatus.FRIENDS, status);
        verify(friendStatusMapper, times(1)).map(userOne, friendship);
    }

    @Test
    void testGetStatus_NoFriendship_usesMapperWithNullAndReturnsMappedStatus() {
        User userOne = makeUser(1L, "alice");
        User userTwo = makeUser(2L, "bob");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(userTwo));
        when(friendRepository.findRelationshipBetweenUsers(userOne.getId(), userTwo.getId()))
                .thenReturn(Optional.empty());
        when(friendStatusMapper.map(userOne, null)).thenReturn(FriendStatus.NONE);

        FriendStatus status = friendService.getStatus(userOne, "bob");

        assertEquals(FriendStatus.NONE, status);
        verify(friendStatusMapper, times(1)).map(userOne, null);
    }

    @Test
    void testGetStatus_UserNotFound_throwsUserNotFoundException() {
        User userOne = makeUser(1L, "alice");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> friendService.getStatus(userOne, "ghost"));
        verify(friendStatusMapper, never()).map(any(), any());
    }

    @Test
    void testRemoveFriend_Success_deletesExistingFriendship() {
        User userOne = makeUser(1L, "alice");
        User userTwo = makeUser(2L, "bob");
        Friend friendship = Friend.builder()
                .sender(userOne)
                .receiver(userTwo)
                .status(FriendRequestStatus.ACCEPTED)
                .build();

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(userTwo));
        when(friendRepository.findRelationshipBetweenUsers(userOne.getId(), userTwo.getId()))
                .thenReturn(Optional.of(friendship));

        friendService.removeFriend(userOne, "bob");

        verify(friendRepository, times(1)).delete(friendship);
    }

    @Test
    void testRemoveFriend_NotFound_throwsFriendRequestNotFoundException() {
        User userOne = makeUser(1L, "alice");
        User userTwo = makeUser(2L, "bob");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(userTwo));
        when(friendRepository.findRelationshipBetweenUsers(userOne.getId(), userTwo.getId()))
                .thenReturn(Optional.empty());

        assertThrows(FriendRequestNotFoundException.class, () -> friendService.removeFriend(userOne, "bob"));

        verify(friendRepository, never()).delete(any());
    }

    @Test
    void testRemoveFriend_UserNotFound_throwsUserNotFoundException() {
        User userOne = makeUser(1L, "alice");

        when(userRepository.findByUsername("no-such-user")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> friendService.removeFriend(userOne, "no-such-user"));

        verify(friendRepository, never()).delete(any());
    }
}

