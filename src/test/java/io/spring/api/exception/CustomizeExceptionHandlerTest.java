package io.spring.api.exception;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.CurrentUserApi;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.UserQueryService;
import io.spring.application.data.UserData;
import io.spring.application.user.UserService;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({CurrentUserApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class CustomizeExceptionHandlerTest {

  @Autowired private MockMvc mvc;

  @MockBean private UserRepository userRepository;
  @MockBean private UserReadService userReadService;
  @MockBean private JwtService jwtService;
  @MockBean private UserQueryService userQueryService;
  @MockBean private UserService userService;

  @BeforeEach
  void setUp() {
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  void should_return_401_for_unauthenticated_request() {
    given().contentType("application/json").when().get("/user").then().statusCode(401);
  }

  @Test
  void should_return_422_for_invalid_authentication() {
    User user = new User("test@test.com", "testuser", "password", "", "");
    String token = "valid-token";
    when(jwtService.getSubFromToken(token)).thenReturn(Optional.of(user.getId()));
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(userReadService.findById(user.getId()))
        .thenReturn(new UserData(user.getId(), "test@test.com", "testuser", "", ""));

    // Update with invalid email format to trigger validation error
    given()
        .contentType("application/json")
        .header("Authorization", "Token " + token)
        .body("{\"user\":{\"email\":\"not-an-email\"}}")
        .when()
        .put("/user")
        .then()
        .statusCode(422);
  }
}
