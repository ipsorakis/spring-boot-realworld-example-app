package io.spring.api.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import java.lang.reflect.Field;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class JwtTokenFilterTest {

  @Mock private UserRepository userRepository;
  @Mock private JwtService jwtService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  private JwtTokenFilter jwtTokenFilter;

  @BeforeEach
  void setUp() throws Exception {
    jwtTokenFilter = new JwtTokenFilter();
    Field userRepoField = JwtTokenFilter.class.getDeclaredField("userRepository");
    userRepoField.setAccessible(true);
    userRepoField.set(jwtTokenFilter, userRepository);
    Field jwtField = JwtTokenFilter.class.getDeclaredField("jwtService");
    jwtField.setAccessible(true);
    jwtField.set(jwtTokenFilter, jwtService);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void should_set_authentication_with_valid_token() throws Exception {
    User user = new User("test@test.com", "testuser", "password", "", "");
    when(request.getHeader("Authorization")).thenReturn("Token valid-token");
    when(jwtService.getSubFromToken("valid-token")).thenReturn(Optional.of(user.getId()));
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void should_pass_through_when_no_authorization_header() throws Exception {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void should_pass_through_when_invalid_token() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Token invalid-token");
    when(jwtService.getSubFromToken("invalid-token")).thenReturn(Optional.empty());

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void should_pass_through_when_user_not_found() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Token valid-token");
    when(jwtService.getSubFromToken("valid-token")).thenReturn(Optional.of("non-existent-id"));
    when(userRepository.findById("non-existent-id")).thenReturn(Optional.empty());

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void should_pass_through_when_header_has_no_token_part() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Token");

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    assertNull(SecurityContextHolder.getContext().getAuthentication());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void should_always_continue_filter_chain() throws Exception {
    when(request.getHeader("Authorization")).thenReturn(null);

    jwtTokenFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }
}
