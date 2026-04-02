package io.spring.core.service;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.core.article.Article;
import io.spring.core.comment.Comment;
import io.spring.core.user.User;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthorizationServiceTest {

  private User articleAuthor;
  private User otherUser;
  private Article article;

  @BeforeEach
  void setUp() {
    articleAuthor = new User("author@test.com", "author", "pass", "", "");
    otherUser = new User("other@test.com", "other", "pass", "", "");
    article =
        new Article("Test Title", "desc", "body", Arrays.asList("java"), articleAuthor.getId());
  }

  @Test
  void should_allow_article_author_to_write_article() {
    assertTrue(AuthorizationService.canWriteArticle(articleAuthor, article));
  }

  @Test
  void should_not_allow_other_user_to_write_article() {
    assertFalse(AuthorizationService.canWriteArticle(otherUser, article));
  }

  @Test
  void should_allow_article_author_to_write_comment() {
    Comment comment = new Comment("body", otherUser.getId(), article.getId());

    assertTrue(AuthorizationService.canWriteComment(articleAuthor, article, comment));
  }

  @Test
  void should_allow_comment_author_to_write_comment() {
    Comment comment = new Comment("body", otherUser.getId(), article.getId());

    assertTrue(AuthorizationService.canWriteComment(otherUser, article, comment));
  }

  @Test
  void should_not_allow_unrelated_user_to_write_comment() {
    User unrelated = new User("unrelated@test.com", "unrelated", "pass", "", "");
    Comment comment = new Comment("body", otherUser.getId(), article.getId());

    assertFalse(AuthorizationService.canWriteComment(unrelated, article, comment));
  }
}
