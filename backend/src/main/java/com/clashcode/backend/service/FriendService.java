package com.clashcode.backend.service;

import com.clashcode.backend.dto.FriendDto;
import com.clashcode.backend.enums.FriendRequestStatus;
import com.clashcode.backend.enums.FriendStatus;
import com.clashcode.backend.exception.FriendRequestExistsException;
import com.clashcode.backend.exception.FriendRequestNotFoundException;
import com.clashcode.backend.exception.UserNotFoundException;
import com.clashcode.backend.mapper.FriendMapper;
import com.clashcode.backend.mapper.FriendStatusMapper;
import com.clashcode.backend.model.Friend;
import com.clashcode.backend.model.User;
import com.clashcode.backend.repository.FriendRepository;
import com.clashcode.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Transactional
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendStatusMapper friendStatusMapper;
    private final FriendMapper friendMapper;

    public FriendService(
            UserRepository userRepository,
            FriendRepository friendRepository,
            FriendStatusMapper friendStatusMapper,
            FriendMapper friendMapper
    ) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.friendStatusMapper = friendStatusMapper;
        this.friendMapper = friendMapper;
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

        return friendStatusMapper.map(userOne, friendshipOpt.orElse(null));
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

    public Page<FriendDto> getSentFriendRequests(User user, Pageable pageable) {
        return friendRepository.findBySenderIdAndStatus(user.getId(), FriendRequestStatus.PENDING, pageable)
                .map(friendship -> friendMapper.toFriendDto(friendship, user));
    }

    public Page<FriendDto> getReceivedFriendRequests(User user, Pageable pageable) {
        return friendRepository.findByReceiverIdAndStatus(user.getId(), FriendRequestStatus.PENDING, pageable)
                .map(friendship -> friendMapper.toFriendDto(friendship, user));
    }

    public Page<FriendDto> getFriendsList(User user, Pageable pageable) {
        return friendRepository.findAllFriendsByUserId(user.getId(), pageable)
                .map(friendship -> friendMapper.toFriendDto(friendship, user));
    }
}
