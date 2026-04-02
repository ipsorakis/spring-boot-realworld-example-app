package io.spring.core.favorite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ArticleFavoriteTest {

  @Test
  void should_create_article_favorite() {
    ArticleFavorite favorite = new ArticleFavorite("article-id", "user-id");

    assertEquals("article-id", favorite.getArticleId());
    assertEquals("user-id", favorite.getUserId());
  }

  @Test
  void should_have_equality_based_on_all_fields() {
    ArticleFavorite fav1 = new ArticleFavorite("article-id", "user-id");
    ArticleFavorite fav2 = new ArticleFavorite("article-id", "user-id");

    assertEquals(fav1, fav2);
    assertEquals(fav1.hashCode(), fav2.hashCode());
  }

  @Test
  void should_not_be_equal_with_different_article_id() {
    ArticleFavorite fav1 = new ArticleFavorite("article-1", "user-id");
    ArticleFavorite fav2 = new ArticleFavorite("article-2", "user-id");

    assertNotEquals(fav1, fav2);
  }

  @Test
  void should_not_be_equal_with_different_user_id() {
    ArticleFavorite fav1 = new ArticleFavorite("article-id", "user-1");
    ArticleFavorite fav2 = new ArticleFavorite("article-id", "user-2");

    assertNotEquals(fav1, fav2);
  }

  @Test
  void should_create_with_no_arg_constructor() {
    ArticleFavorite favorite = new ArticleFavorite();

    assertNull(favorite.getArticleId());
    assertNull(favorite.getUserId());
  }
}
