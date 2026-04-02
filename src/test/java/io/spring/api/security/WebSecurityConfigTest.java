package io.spring.api.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.spring.core.service.JwtService;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class WebSecurityConfigTest {

  @Autowired private MockMvc mvc;

  @MockBean private UserRepository userRepository;
  @MockBean private UserReadService userReadService;
  @MockBean private JwtService jwtService;

  @Test
  void should_allow_get_tags_without_auth() throws Exception {
    mvc.perform(get("/tags")).andExpect(status().isOk());
  }

  @Test
  void should_not_return_401_for_post_users() throws Exception {
    // POST /users is permitAll so it should not return 401.
    // It may throw a downstream exception (e.g. NoSuchElementException) because
    // mocked dependencies return empty, but the request is NOT blocked by security.
    try {
      MvcResult result =
          mvc.perform(
                  post("/users")
                      .contentType("application/json")
                      .content(
                          "{\"user\":{\"email\":\"t@t.com\",\"username\":\"t\",\"password\":\"p\"}}"))
              .andReturn();
      assertNotEquals(401, result.getResponse().getStatus());
    } catch (org.springframework.web.util.NestedServletException e) {
      // A NestedServletException means the request passed security but failed downstream
      assertFalse(
          e.getMessage().contains("401"), "Request should not be blocked by authentication");
    }
  }

  @Test
  void should_not_return_401_for_post_users_login() throws Exception {
    MvcResult result =
        mvc.perform(
                post("/users/login")
                    .contentType("application/json")
                    .content("{\"user\":{\"email\":\"t@t.com\",\"password\":\"p\"}}"))
            .andReturn();
    assertNotEquals(401, result.getResponse().getStatus());
  }

  @Test
  void should_allow_get_articles_without_auth() throws Exception {
    mvc.perform(get("/articles")).andExpect(status().isOk());
  }

  @Test
  void should_not_return_401_for_get_profiles() throws Exception {
    MvcResult result = mvc.perform(get("/profiles/testuser")).andReturn();
    assertNotEquals(401, result.getResponse().getStatus());
  }

  @Test
  void should_require_auth_for_articles_feed() throws Exception {
    mvc.perform(get("/articles/feed")).andExpect(status().isUnauthorized());
  }

  @Test
  void should_require_auth_for_current_user() throws Exception {
    mvc.perform(get("/user")).andExpect(status().isUnauthorized());
  }

  @Test
  void should_allow_graphql_without_auth() throws Exception {
    mvc.perform(post("/graphql").contentType("application/json").content("{\"query\":\"{tags}\"}"))
        .andExpect(status().isOk());
  }
}
