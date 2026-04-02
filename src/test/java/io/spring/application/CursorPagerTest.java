package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.CursorPager.Direction;
import io.spring.application.data.ArticleData;
import io.spring.application.data.ProfileData;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

public class CursorPagerTest {

  private ArticleData createArticleData(String id) {
    DateTime now = new DateTime();
    return new ArticleData(
        id,
        "slug-" + id,
        "title",
        "desc",
        "body",
        false,
        0,
        now,
        now,
        Collections.emptyList(),
        new ProfileData("user-id", "user", "", "", false));
  }

  @Test
  void should_have_next_page_when_direction_next_and_has_extra() {
    List<ArticleData> data = Arrays.asList(createArticleData("1"));
    CursorPager<ArticleData> pager = new CursorPager<>(data, Direction.NEXT, true);

    assertTrue(pager.hasNext());
    assertFalse(pager.hasPrevious());
  }

  @Test
  void should_not_have_next_page_when_direction_next_and_no_extra() {
    List<ArticleData> data = Arrays.asList(createArticleData("1"));
    CursorPager<ArticleData> pager = new CursorPager<>(data, Direction.NEXT, false);

    assertFalse(pager.hasNext());
    assertFalse(pager.hasPrevious());
  }

  @Test
  void should_have_previous_page_when_direction_prev_and_has_extra() {
    List<ArticleData> data = Arrays.asList(createArticleData("1"));
    CursorPager<ArticleData> pager = new CursorPager<>(data, Direction.PREV, true);

    assertFalse(pager.hasNext());
    assertTrue(pager.hasPrevious());
  }

  @Test
  void should_not_have_previous_page_when_direction_prev_and_no_extra() {
    List<ArticleData> data = Arrays.asList(createArticleData("1"));
    CursorPager<ArticleData> pager = new CursorPager<>(data, Direction.PREV, false);

    assertFalse(pager.hasNext());
    assertFalse(pager.hasPrevious());
  }

  @Test
  void should_return_start_cursor_from_first_element() {
    ArticleData first = createArticleData("1");
    ArticleData second = createArticleData("2");
    CursorPager<ArticleData> pager =
        new CursorPager<>(Arrays.asList(first, second), Direction.NEXT, false);

    assertNotNull(pager.getStartCursor());
    assertEquals(first.getCursor().getData(), pager.getStartCursor().getData());
  }

  @Test
  void should_return_end_cursor_from_last_element() {
    ArticleData first = createArticleData("1");
    ArticleData second = createArticleData("2");
    CursorPager<ArticleData> pager =
        new CursorPager<>(Arrays.asList(first, second), Direction.NEXT, false);

    assertNotNull(pager.getEndCursor());
    assertEquals(second.getCursor().getData(), pager.getEndCursor().getData());
  }

  @Test
  void should_return_null_cursors_when_data_is_empty() {
    CursorPager<ArticleData> pager =
        new CursorPager<>(Collections.emptyList(), Direction.NEXT, false);

    assertNull(pager.getStartCursor());
    assertNull(pager.getEndCursor());
  }

  @Test
  void should_return_data() {
    ArticleData article = createArticleData("1");
    CursorPager<ArticleData> pager =
        new CursorPager<>(Arrays.asList(article), Direction.NEXT, false);

    assertEquals(1, pager.getData().size());
    assertEquals(article, pager.getData().get(0));
  }
}
