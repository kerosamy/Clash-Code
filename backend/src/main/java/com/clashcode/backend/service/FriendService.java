package com.clashcode.backend.service;

import com.clashcode.backend.enums.FriendRequestStatus;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.exception.FriendRequestExistsException;
import com.clashcode.backend.exception.FriendRequestNotFoundException;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.FriendRepository;
import com.clashcode.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Transactional
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public FriendService(UserRepository userRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    public void sendFriendRequest(User sender, String receiverUsername) {
        User receiver = getUserOrElseThrowException(receiverUsername);
        validateRequest(sender, receiver);
        friendRepository.findRelationshipBetweenUsers(sender.getId(), receiver.getId()).ifPresent(friend -> {
            throw new FriendRequestExistsException("Relationship already exists with status: " + friend.getStatus());
        });

        Friend newFriendship = Friend.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .build();

        friendRepository.save(newFriendship);
    }

    public void acceptFriendRequest(User receiver, String senderUsername) {
        User sender = getUserOrElseThrowException(senderUsername);
        validateRequest(sender, receiver);
        Friend friendship = friendRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId())
                .orElseThrow(FriendRequestNotFoundException::new);
        if (friendship.getStatus().equals(FriendRequestStatus.ACCEPTED))
            throw new FriendRequestExistsException("Already Accepted");
        friendship.setStatus(FriendRequestStatus.ACCEPTED);
        friendRepository.save(friendship);
    }

    public void rejectFriendRequest(User receiver, String senderUsername) {
        User sender = getUserOrElseThrowException(senderUsername);
        validateRequest(sender, receiver);
        Friend friendship = friendRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId())
                .orElseThrow(FriendRequestNotFoundException::new);
        if (friendship.getStatus().equals(FriendRequestStatus.ACCEPTED))
            throw new FriendRequestExistsException("Already Accepted");
        friendRepository.delete(friendship);
    }

    public FriendStatus getStatus(User userOne, String userTwoUsername) {
        User userTwo = getUserOrElseThrowException(userTwoUsername);
        validateRequest(userOne, userTwo);
        Optional<Friend> friendshipOpt =
                friendRepository.findRelationshipBetweenUsers(userOne.getId(), userTwo.getId());

        if (friendshipOpt.isEmpty()) {
            return FriendStatus.NONE;
        }

        Friend friendship = friendshipOpt.get();
        FriendRequestStatus status = friendship.getStatus();

        if (status == FriendRequestStatus.ACCEPTED) {
            return FriendStatus.FRIENDS;
        }

        return userOne.equals(friendship.getSender())
                ? FriendStatus.PENDING_SENT
                : FriendStatus.PENDING_RECEIVED;
    }

    public void removeFriend(User userOne, String userTwoUsername) {
        User userTwo = getUserOrElseThrowException(userTwoUsername);
        validateRequest(userOne,userTwo);
        Friend friendship = friendRepository.findRelationshipBetweenUsers(userOne.getId(), userTwo.getId())
                .orElseThrow(FriendRequestNotFoundException::new);
        friendRepository.delete(friendship);
    }

    private void validateRequest(User userOne, User userTwo) {
        if (userOne.getId().equals(userTwo.getId())) {
            throw new IllegalArgumentException("Cannot use your username");
        }
    }

    private User getUserOrElseThrowException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
