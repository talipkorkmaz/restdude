package com.restdude.domain.friends.service.impl;

import com.restdude.domain.base.service.AbstractModelServiceImpl;
import com.restdude.domain.friends.model.Friendship;
import com.restdude.domain.friends.model.FriendshipDTO;
import com.restdude.domain.friends.model.FriendshipId;
import com.restdude.domain.friends.model.FriendshipStatus;
import com.restdude.domain.friends.repository.FriendshipRepository;
import com.restdude.domain.friends.service.FriendshipService;
import com.restdude.domain.users.model.User;
import com.restdude.domain.users.model.UserDTO;
import com.restdude.util.exception.http.BadRequestException;
import com.restdude.util.exception.http.HttpException;
import com.restdude.websocket.Destinations;
import com.restdude.websocket.message.ActivityNotificationMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;

@Named(FriendshipService.BEAN_ID)
public class FriendshipServiceImpl extends AbstractModelServiceImpl<Friendship, FriendshipId, FriendshipRepository>
		implements FriendshipService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FriendshipServiceImpl.class);

	/**
	 * Create a friendship request
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
    public Friendship create(Friendship resource) throws HttpException {
        LOGGER.debug("create: {}", resource);
		return this.saveRelationship(resource);
	}

	/**
	 * Approve or reject a friendship request
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
    public Friendship update(Friendship resource) throws HttpException {
        LOGGER.debug("update: {}", resource);
		return this.saveRelationship(resource);
	}

	/**
	 * Delete the friendship and it's inverse
	 */
	@Override
	@Transactional(readOnly = false)
	@PreAuthorize("hasRole('ROLE_USER')")
    public void delete(Friendship resource) throws HttpException {
        resource.setStatus(FriendshipStatus.DELETE);
		this.saveRelationship(resource);
	}


    protected void validateSender(Friendship resource) throws HttpException {

		LOGGER.debug("validateSender resource: {}", resource);

		// get current principal
		String userDetailsId = this.getPrincipal().getId();

		// set current user as sender if the latter is empty
		if (resource.getId().getOwner() == null) {
			resource.getId().setOwner(new User(userDetailsId));
		}

		// verify principal == owner
		if (!userDetailsId.equals(resource.getId().getOwner().getId())) {
            throw new BadRequestException("Invalid friendship owner.");
        }

		// verify friend is set
		User friend = resource.getId().getFriend();
		if (friend == null || !StringUtils.isNotBlank(friend.getId())) {
            throw new BadRequestException("A (friend) id is required");
        }
		
		// verify principal != friend
		else if (userDetailsId.equals(friend.getId())) {
            throw new BadRequestException("Befriending yourself is not allowed.");
		}

		LOGGER.debug("validateSender returns resource: {}", resource);
	}

    protected Friendship saveRelationship(Friendship resource) throws HttpException {

		validateSender(resource);

		// get the persisted record, if any, null otherwise
		FriendshipStatus currentStatus = this.repository.getCurrentStatus(resource.getId());

		// validate next status
		boolean allowedNext = FriendshipStatus.isAllowedNext(currentStatus, resource.getStatus());
		LOGGER.debug("saveRelationship, allowedNext: {}", allowedNext);
		if (!allowedNext) {
			throw new IllegalArgumentException("Cannot save with given status: " + resource.getStatus());
		}

		resource = saveSingle(resource);

		// update inverse if needed
		FriendshipStatus inverseStatus = FriendshipStatus.getApplicableInverse(resource.getStatus());
		if (inverseStatus != null) {
			Friendship inverse = new Friendship(resource.getInverseId(), inverseStatus);
			saveSingle(inverse);
		}

		return resource;
	}

	protected Friendship saveSingle(Friendship resource) {
		LOGGER.debug("saveSingle: {}", resource);
		// if delete
		if (FriendshipStatus.DELETE.equals(resource.getStatus())) {
			this.repository.delete(resource);
		} else {
			// persist changes
			resource = this.repository.save(resource);

		}

		// notify this side's owner of appropriae statuses
		if (resource.getStatus().equals(FriendshipStatus.PENDING)
				|| resource.getStatus().equals(FriendshipStatus.CONFIRMED)) {
			// notify this side of pending request
			String username = this.userRepository.findCompactUserById(resource.getId().getOwner().getId())
					.getUsername();
			LOGGER.debug("Sending friendship DTO to " + username);
			this.messagingTemplate.convertAndSendToUser(username, Destinations.USERQUEUE_FRIENDSHIPS,
					new FriendshipDTO(resource));
		}
		return resource;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public Iterable<UserDTO> findAllMyFriends() {
		return repository.findAllFriends(this.getPrincipal().getId());
	}

	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	public void sendStompActivityMessageToOnlineFriends(ActivityNotificationMessage msg) {

		// get online friends
		Iterable<String> useernames = this.repository.findAllStompOnlineFriendUsernames(this.getPrincipal().getId());

		this.sendStompActivityMessage(msg, useernames);
	}

}