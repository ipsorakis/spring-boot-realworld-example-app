package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import graphql.execution.DataFetcherResult;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.user.User;
import io.spring.graphql.types.CommentPayload;
import io.spring.graphql.types.DeletionStatus;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.joda.time.DateTime;
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
public class CommentMutationTest {

  @Mock private ArticleRepository articleRepository;
  @Mock private CommentRepository commentRepository;
  @Mock private CommentQueryService commentQueryService;

  @InjectMocks private CommentMutation commentMutation;

  private User user;
  private Article article;

  @BeforeEach
  void setUp() {
    user = new User("test@test.com", "testuser", "password", "", "");
    article = new Article("Test Title", "desc", "body", Arrays.asList("java"), user.getId());
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
  void should_create_comment_successfully() {
    setAuthenticated(user);
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(article));
    CommentData commentData =
        new CommentData(
            "comment-id",
            "Great article!",
            article.getId(),
            new DateTime(),
            new DateTime(),
            new ProfileData(user.getId(), user.getUsername(), "", "", false));
    when(commentQueryService.findById(any(), eq(user))).thenReturn(Optional.of(commentData));

    DataFetcherResult<CommentPayload> result =
        commentMutation.createComment("test-title", "Great article!");

    assertNotNull(result);
    assertEquals(commentData, result.getLocalContext());
    verify(commentRepository).save(any(Comment.class));
  }

  @Test
  void should_throw_when_creating_comment_without_auth() {
    SecurityContextHolder.clearContext();

    assertThrows(
        NullPointerException.class, () -> commentMutation.createComment("test-title", "body"));
  }

  @Test
  void should_throw_when_creating_comment_on_non_existent_article() {
    setAuthenticated(user);
    when(articleRepository.findBySlug("non-existent")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> commentMutation.createComment("non-existent", "body"));
  }

  @Test
  void should_delete_comment_by_article_author() {
    setAuthenticated(user);
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(article));
    Comment comment = new Comment("body", "other-user-id", article.getId());
    when(commentRepository.findById(article.getId(), comment.getId()))
        .thenReturn(Optional.of(comment));

    DeletionStatus result = commentMutation.removeComment("test-title", comment.getId());

    assertTrue(result.getSuccess());
    verify(commentRepository).remove(comment);
  }

  @Test
  void should_delete_comment_by_comment_author() {
    User commentAuthor = new User("author@test.com", "commentauthor", "pass", "", "");
    setAuthenticated(commentAuthor);
    User articleOwner = new User("owner@test.com", "owner", "pass", "", "");
    Article otherArticle =
        new Article("Title", "desc", "body", Arrays.asList("java"), articleOwner.getId());
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(otherArticle));
    Comment comment = new Comment("body", commentAuthor.getId(), otherArticle.getId());
    when(commentRepository.findById(otherArticle.getId(), comment.getId()))
        .thenReturn(Optional.of(comment));

    DeletionStatus result = commentMutation.removeComment("test-title", comment.getId());

    assertTrue(result.getSuccess());
    verify(commentRepository).remove(comment);
  }

  @Test
  void should_throw_when_deleting_comment_without_authorization() {
    User unauthorized = new User("unauth@test.com", "unauth", "pass", "", "");
    setAuthenticated(unauthorized);
    User articleOwner = new User("owner@test.com", "owner", "pass", "", "");
    Article otherArticle =
        new Article("Title", "desc", "body", Arrays.asList("java"), articleOwner.getId());
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(otherArticle));
    Comment comment = new Comment("body", "some-other-user-id", otherArticle.getId());
    when(commentRepository.findById(otherArticle.getId(), comment.getId()))
        .thenReturn(Optional.of(comment));

    assertThrows(
        NoAuthorizationException.class,
        () -> commentMutation.removeComment("test-title", comment.getId()));
  }

  @Test
  void should_throw_when_deleting_non_existent_comment() {
    setAuthenticated(user);
    when(articleRepository.findBySlug("test-title")).thenReturn(Optional.of(article));
    when(commentRepository.findById(article.getId(), "non-existent")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> commentMutation.removeComment("test-title", "non-existent"));
  }

  @Test
  void should_throw_when_deleting_comment_without_auth() {
    SecurityContextHolder.clearContext();

    assertThrows(
        NullPointerException.class,
        () -> commentMutation.removeComment("test-title", "comment-id"));
  }
}
