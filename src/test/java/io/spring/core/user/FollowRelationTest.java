package io.spring.core.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FollowRelationTest {

  @Test
  void should_create_follow_relation() {
    FollowRelation relation = new FollowRelation("user-id", "target-id");

    assertEquals("user-id", relation.getUserId());
    assertEquals("target-id", relation.getTargetId());
  }

  @Test
  void should_have_equality_based_on_all_fields() {
    FollowRelation relation1 = new FollowRelation("user-id", "target-id");
    FollowRelation relation2 = new FollowRelation("user-id", "target-id");

    assertEquals(relation1, relation2);
    assertEquals(relation1.hashCode(), relation2.hashCode());
  }

  @Test
  void should_not_be_equal_with_different_user_id() {
    FollowRelation relation1 = new FollowRelation("user-1", "target-id");
    FollowRelation relation2 = new FollowRelation("user-2", "target-id");

    assertNotEquals(relation1, relation2);
  }

  @Test
  void should_not_be_equal_with_different_target_id() {
    FollowRelation relation1 = new FollowRelation("user-id", "target-1");
    FollowRelation relation2 = new FollowRelation("user-id", "target-2");

    assertNotEquals(relation1, relation2);
  }

  @Test
  void should_create_with_no_arg_constructor() {
    FollowRelation relation = new FollowRelation();

    assertNull(relation.getUserId());
    assertNull(relation.getTargetId());
  }

  @Test
  void should_set_fields_via_setters() {
    FollowRelation relation = new FollowRelation();
    relation.setUserId("user-id");
    relation.setTargetId("target-id");

    assertEquals("user-id", relation.getUserId());
    assertEquals("target-id", relation.getTargetId());
  }
}
