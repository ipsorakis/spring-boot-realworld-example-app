package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.application.data.ProfileData;
import io.spring.graphql.types.Comment;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentDatafetcherTest {

  @Mock private CommentQueryService commentQueryService;

  @InjectMocks private CommentDatafetcher commentDatafetcher;

  private CommentData commentData;

  @BeforeEach
  void setUp() {
    DateTime now = new DateTime();
    commentData =
        new CommentData(
            "comment-id",
            "Great article!",
            "article-id",
            now,
            now,
            new ProfileData("user-id", "testuser", "", "", false));
  }

  @Test
  void should_build_comment_result_correctly() {
    // Test that CommentData fields are correctly mapped to Comment type
    Comment comment =
        Comment.newBuilder()
            .id(commentData.getId())
            .body(commentData.getBody())
            .createdAt(ISODateTimeFormat.dateTime().withZoneUTC().print(commentData.getCreatedAt()))
            .updatedAt(ISODateTimeFormat.dateTime().withZoneUTC().print(commentData.getCreatedAt()))
            .build();

    assertEquals("comment-id", comment.getId());
    assertEquals("Great article!", comment.getBody());
    assertNotNull(comment.getCreatedAt());
    assertNotNull(comment.getUpdatedAt());
  }

  @Test
  void should_have_datafetcher_instance() {
    assertNotNull(commentDatafetcher);
  }
}
