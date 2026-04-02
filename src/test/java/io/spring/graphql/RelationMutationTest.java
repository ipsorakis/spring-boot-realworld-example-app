package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ProfileQueryService;
import io.spring.application.data.ProfileData;
import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.graphql.types.ProfilePayload;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class RelationMutationTest {

  @Mock private UserRepository userRepository;
  @Mock private ProfileQueryService profileQueryService;

  @InjectMocks private RelationMutation relationMutation;

  private User currentUser;
  private User targetUser;
  private ProfileData targetProfileData;

  @BeforeEach
  void setUp() {
    currentUser = new User("current@test.com", "currentuser", "password", "", "");
    targetUser = new User("target@test.com", "targetuser", "password", "", "");
    targetProfileData = new ProfileData(targetUser.getId(), "targetuser", "bio", "image", true);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private void setAuthenticated(User user) {
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  void should_follow_user_successfully() {
    setAuthenticated(currentUser);
    when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
    when(profileQueryService.findByUsername(eq("targetuser"), eq(currentUser)))
        .thenReturn(Optional.of(targetProfileData));

    ProfilePayload result = relationMutation.follow("targetuser");

    assertNotNull(result);
    assertNotNull(result.getProfile());
    assertEquals("targetuser", result.getProfile().getUsername());
    assertTrue(result.getProfile().getFollowing());
    verify(userRepository).saveRelation(any(FollowRelation.class));
  }

  @Test
  void should_throw_when_following_non_existent_user() {
    setAuthenticated(currentUser);
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> relationMutation.follow("nonexistent"));
  }

  @Test
  void should_throw_when_following_without_auth() {
    SecurityContextHolder.clearContext();

    assertThrows(NullPointerException.class, () -> relationMutation.follow("targetuser"));
  }

  @Test
  void should_unfollow_user_successfully() {
    setAuthenticated(currentUser);
    when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
    FollowRelation relation = new FollowRelation(currentUser.getId(), targetUser.getId());
    when(userRepository.findRelation(currentUser.getId(), targetUser.getId()))
        .thenReturn(Optional.of(relation));
    ProfileData unfollowedProfile =
        new ProfileData(targetUser.getId(), "targetuser", "bio", "image", false);
    when(profileQueryService.findByUsername(eq("targetuser"), eq(currentUser)))
        .thenReturn(Optional.of(unfollowedProfile));

    ProfilePayload result = relationMutation.unfollow("targetuser");

    assertNotNull(result);
    assertNotNull(result.getProfile());
    assertEquals("targetuser", result.getProfile().getUsername());
    verify(userRepository).removeRelation(relation);
  }

  @Test
  void should_throw_when_unfollowing_non_existent_user() {
    setAuthenticated(currentUser);
    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> relationMutation.unfollow("nonexistent"));
  }

  @Test
  void should_throw_when_unfollowing_user_not_followed() {
    setAuthenticated(currentUser);
    when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
    when(userRepository.findRelation(currentUser.getId(), targetUser.getId()))
        .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> relationMutation.unfollow("targetuser"));
  }

  @Test
  void should_throw_when_unfollowing_without_auth() {
    SecurityContextHolder.clearContext();

    assertThrows(NullPointerException.class, () -> relationMutation.unfollow("targetuser"));
  }
}
