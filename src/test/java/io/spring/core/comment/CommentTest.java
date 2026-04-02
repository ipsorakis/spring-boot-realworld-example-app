package io.spring.core.comment;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CommentTest {

  @Test
  void should_create_comment_with_valid_fields() {
    Comment comment = new Comment("Great article!", "user-id", "article-id");

    assertNotNull(comment.getId());
    assertEquals("Great article!", comment.getBody());
    assertEquals("user-id", comment.getUserId());
    assertEquals("article-id", comment.getArticleId());
    assertNotNull(comment.getCreatedAt());
  }

  @Test
  void should_generate_unique_ids() {
    Comment comment1 = new Comment("body1", "user-id", "article-id");
    Comment comment2 = new Comment("body2", "user-id", "article-id");

    assertNotEquals(comment1.getId(), comment2.getId());
  }

  @Test
  void should_have_equality_based_on_id() {
    Comment comment1 = new Comment("body", "user-id", "article-id");
    Comment comment2 = new Comment("body", "user-id", "article-id");

    assertNotEquals(comment1, comment2);
    assertEquals(comment1, comment1);
  }

  @Test
  void should_have_consistent_hashcode() {
    Comment comment = new Comment("body", "user-id", "article-id");
    int hash1 = comment.hashCode();
    int hash2 = comment.hashCode();

    assertEquals(hash1, hash2);
  }
}
