package io.spring.application;

import static org.junit.jupiter.api.Assertions.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

public class DateTimeCursorTest {

  @Test
  void should_create_cursor_with_datetime() {
    DateTime dateTime = new DateTime(2023, 1, 15, 10, 30, 0, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(dateTime);

    assertEquals(dateTime, cursor.getData());
  }

  @Test
  void should_convert_to_string_as_millis() {
    DateTime dateTime = new DateTime(2023, 1, 15, 10, 30, 0, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(dateTime);

    String result = cursor.toString();

    assertEquals(String.valueOf(dateTime.getMillis()), result);
  }

  @Test
  void should_parse_cursor_string_to_datetime() {
    DateTime dateTime = new DateTime(2023, 1, 15, 10, 30, 0, DateTimeZone.UTC);
    String millis = String.valueOf(dateTime.getMillis());

    DateTime result = DateTimeCursor.parse(millis);

    assertNotNull(result);
    assertEquals(dateTime.getMillis(), result.getMillis());
    assertEquals(DateTimeZone.UTC, result.getZone());
  }

  @Test
  void should_return_null_when_parsing_null() {
    DateTime result = DateTimeCursor.parse(null);

    assertNull(result);
  }

  @Test
  void should_roundtrip_correctly() {
    DateTime original = new DateTime(2023, 6, 15, 14, 0, 0, DateTimeZone.UTC);
    DateTimeCursor cursor = new DateTimeCursor(original);
    String serialized = cursor.toString();
    DateTime parsed = DateTimeCursor.parse(serialized);

    assertEquals(original.getMillis(), parsed.getMillis());
  }

  @Test
  void should_throw_when_parsing_invalid_string() {
    assertThrows(NumberFormatException.class, () -> DateTimeCursor.parse("not-a-number"));
  }
}
