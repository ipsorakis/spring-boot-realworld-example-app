package io.spring.application.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private UserService userService;

  @BeforeEach
  void setUp() {
    userService =
        new UserService(userRepository, "https://default-image.com/avatar.png", passwordEncoder);
  }

  @Test
  void should_create_user_successfully() {
    when(passwordEncoder.encode("password")).thenReturn("encoded-password");
    RegisterParam param = new RegisterParam("test@test.com", "testuser", "password");

    User user = userService.createUser(param);

    assertNotNull(user);
    assertEquals("test@test.com", user.getEmail());
    assertEquals("testuser", user.getUsername());
    assertEquals("encoded-password", user.getPassword());
    assertEquals("https://default-image.com/avatar.png", user.getImage());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void should_update_user_successfully() {
    User user = new User("old@test.com", "olduser", "old-password", "old bio", "old-image");
    UpdateUserParam param =
        UpdateUserParam.builder()
            .email("new@test.com")
            .username("newuser")
            .bio("new bio")
            .image("new-image")
            .password("")
            .build();
    UpdateUserCommand command = new UpdateUserCommand(user, param);

    userService.updateUser(command);

    assertEquals("new@test.com", user.getEmail());
    assertEquals("newuser", user.getUsername());
    assertEquals("new bio", user.getBio());
    assertEquals("new-image", user.getImage());
    verify(userRepository).save(user);
  }

  @Test
  void should_not_update_empty_fields() {
    User user = new User("old@test.com", "olduser", "old-password", "old bio", "old-image");
    UpdateUserParam param =
        UpdateUserParam.builder().email("").username("").bio("").image("").password("").build();
    UpdateUserCommand command = new UpdateUserCommand(user, param);

    userService.updateUser(command);

    assertEquals("old@test.com", user.getEmail());
    assertEquals("olduser", user.getUsername());
    assertEquals("old bio", user.getBio());
    assertEquals("old-image", user.getImage());
    verify(userRepository).save(user);
  }
}
