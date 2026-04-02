package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.TagsQueryService;
import io.spring.core.service.JwtService;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.readservice.UserReadService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({TagsApi.class})
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class TagsApiTest {

  @Autowired private MockMvc mvc;

  @MockBean private TagsQueryService tagsQueryService;
  @MockBean private UserRepository userRepository;
  @MockBean private UserReadService userReadService;
  @MockBean private JwtService jwtService;

  @BeforeEach
  void setUp() {
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  void should_return_tags_successfully() {
    when(tagsQueryService.allTags()).thenReturn(asList("java", "spring", "graphql"));

    given()
        .contentType("application/json")
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags[0]", equalTo("java"))
        .body("tags[1]", equalTo("spring"))
        .body("tags[2]", equalTo("graphql"))
        .body("tags.size()", equalTo(3));
  }

  @Test
  void should_return_empty_list_when_no_tags() {
    when(tagsQueryService.allTags()).thenReturn(Collections.emptyList());

    given()
        .contentType("application/json")
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(0));
  }
}
