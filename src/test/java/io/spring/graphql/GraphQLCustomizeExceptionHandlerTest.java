package io.spring.graphql;

import static org.junit.jupiter.api.Assertions.*;

import io.spring.graphql.exception.GraphQLCustomizeExceptionHandler;
import io.spring.graphql.types.Error;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GraphQLCustomizeExceptionHandlerTest {

  private GraphQLCustomizeExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GraphQLCustomizeExceptionHandler();
  }

  @Test
  void should_get_errors_as_data_from_empty_constraint_violations() {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    ConstraintViolationException cve = new ConstraintViolationException(violations);

    Error error = GraphQLCustomizeExceptionHandler.getErrorsAsData(cve);

    assertNotNull(error);
    assertEquals("BAD_REQUEST", error.getMessage());
    assertNotNull(error.getErrors());
    assertTrue(error.getErrors().isEmpty());
  }

  @Test
  void should_return_handler_instance() {
    assertNotNull(handler);
  }
}
