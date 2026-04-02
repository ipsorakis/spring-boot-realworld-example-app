package io.spring.core.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  void should_create_user_with_valid_fields() {
    User user = new User("test@test.com", "testuser", "password", "bio", "image-url");

    assertNotNull(user.getId());
    assertEquals("test@test.com", user.getEmail());
    assertEquals("testuser", user.getUsername());
    assertEquals("password", user.getPassword());
    assertEquals("bio", user.getBio());
    assertEquals("image-url", user.getImage());
  }

  @Test
  void should_generate_unique_ids() {
    User user1 = new User("a@a.com", "user1", "pass", "", "");
    User user2 = new User("b@b.com", "user2", "pass", "", "");

    assertNotEquals(user1.getId(), user2.getId());
  }

  @Test
  void should_update_email() {
    User user = new User("old@test.com", "user", "pass", "bio", "image");
    user.update("new@test.com", "", "", "", "");

    assertEquals("new@test.com", user.getEmail());
    assertEquals("user", user.getUsername());
  }

  @Test
  void should_update_username() {
    User user = new User("test@test.com", "olduser", "pass", "bio", "image");
    user.update("", "newuser", "", "", "");

    assertEquals("newuser", user.getUsername());
    assertEquals("test@test.com", user.getEmail());
  }

  @Test
  void should_update_password() {
    User user = new User("test@test.com", "user", "old-pass", "bio", "image");
    user.update("", "", "new-pass", "", "");

    assertEquals("new-pass", user.getPassword());
  }

  @Test
  void should_update_bio() {
    User user = new User("test@test.com", "user", "pass", "old bio", "image");
    user.update("", "", "", "new bio", "");

    assertEquals("new bio", user.getBio());
  }

  @Test
  void should_update_image() {
    User user = new User("test@test.com", "user", "pass", "bio", "old-image");
    user.update("", "", "", "", "new-image");

    assertEquals("new-image", user.getImage());
  }

  @Test
  void should_not_update_empty_fields() {
    User user = new User("test@test.com", "user", "pass", "bio", "image");
    user.update("", "", "", "", "");

    assertEquals("test@test.com", user.getEmail());
    assertEquals("user", user.getUsername());
    assertEquals("pass", user.getPassword());
    assertEquals("bio", user.getBio());
    assertEquals("image", user.getImage());
  }

  @Test
  void should_not_update_null_fields() {
    User user = new User("test@test.com", "user", "pass", "bio", "image");
    user.update(null, null, null, null, null);

    assertEquals("test@test.com", user.getEmail());
    assertEquals("user", user.getUsername());
    assertEquals("pass", user.getPassword());
    assertEquals("bio", user.getBio());
    assertEquals("image", user.getImage());
  }

  @Test
  void should_update_multiple_fields_at_once() {
    User user = new User("old@test.com", "olduser", "old-pass", "old bio", "old-image");
    user.update("new@test.com", "newuser", "new-pass", "new bio", "new-image");

    assertEquals("new@test.com", user.getEmail());
    assertEquals("newuser", user.getUsername());
    assertEquals("new-pass", user.getPassword());
    assertEquals("new bio", user.getBio());
    assertEquals("new-image", user.getImage());
  }

  @Test
  void should_have_equality_based_on_id() {
    User user1 = new User("a@a.com", "user1", "pass", "", "");
    User user2 = new User("a@a.com", "user1", "pass", "", "");

    assertNotEquals(user1, user2);
    assertEquals(user1, user1);
  }
}
