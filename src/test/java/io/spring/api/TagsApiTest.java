package io.spring.api;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.spring.JacksonCustomizations;
import io.spring.api.security.WebSecurityConfig;
import io.spring.application.TagsQueryService;
import io.spring.core.service.JwtService;
import io.spring.core.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TagsApi.class)
@Import({WebSecurityConfig.class, JacksonCustomizations.class})
public class TagsApiTest {
  @Autowired private MockMvc mvc;

  @MockBean private TagsQueryService tagsQueryService;

  @MockBean private UserRepository userRepository;

  @MockBean private JwtService jwtService;

  @BeforeEach
  public void setUp() {
    RestAssuredMockMvc.mockMvc(mvc);
  }

  @Test
  public void should_get_tags_success() {
    when(tagsQueryService.allTags()).thenReturn(asList("reactjs", "angularjs", "dragons"));

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags.size()", equalTo(3))
        .body("tags", hasItems("reactjs", "angularjs", "dragons"));
  }

  @Test
  public void should_get_empty_tags() {
    when(tagsQueryService.allTags()).thenReturn(emptyList());

    given().when().get("/tags").then().statusCode(200).body("tags", hasSize(0));
  }

  @Test
  public void should_get_single_tag() {
    when(tagsQueryService.allTags()).thenReturn(singletonList("reactjs"));

    given()
        .when()
        .get("/tags")
        .then()
        .statusCode(200)
        .body("tags", hasSize(1))
        .body("tags[0]", equalTo("reactjs"));
  }
}
