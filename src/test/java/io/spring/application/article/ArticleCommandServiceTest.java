package io.spring.application.article;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.user.User;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArticleCommandServiceTest {

  @Mock private ArticleRepository articleRepository;

  @InjectMocks private ArticleCommandService articleCommandService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "", "");
  }

  @Test
  void should_create_article_successfully() {
    NewArticleParam param =
        NewArticleParam.builder()
            .title("Test Title")
            .description("Test Description")
            .body("Test Body")
            .tagList(Arrays.asList("java", "spring"))
            .build();

    Article article = articleCommandService.createArticle(param, user);

    assertNotNull(article);
    assertEquals("test-title", article.getSlug());
    assertEquals("Test Title", article.getTitle());
    assertEquals("Test Description", article.getDescription());
    assertEquals("Test Body", article.getBody());
    assertEquals(user.getId(), article.getUserId());
    verify(articleRepository).save(any(Article.class));
  }

  @Test
  void should_update_article_successfully() {
    Article article =
        new Article("Original Title", "desc", "body", Arrays.asList("java"), user.getId());
    UpdateArticleParam param = new UpdateArticleParam("New Title", "new body", "new desc");

    Article updated = articleCommandService.updateArticle(article, param);

    assertEquals("New Title", updated.getTitle());
    assertEquals("new-title", updated.getSlug());
    assertEquals("new body", updated.getBody());
    assertEquals("new desc", updated.getDescription());
    verify(articleRepository).save(article);
  }

  @Test
  void should_update_article_with_partial_fields() {
    Article article =
        new Article(
            "Original Title",
            "original desc",
            "original body",
            Arrays.asList("java"),
            user.getId());
    UpdateArticleParam param = new UpdateArticleParam("New Title", "", "");

    Article updated = articleCommandService.updateArticle(article, param);

    assertEquals("New Title", updated.getTitle());
    assertEquals("original desc", updated.getDescription());
    assertEquals("original body", updated.getBody());
    verify(articleRepository).save(article);
  }
}
