import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecurityFilterTest {

    private final SecurityFilter securityFilter = new SecurityFilter();
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    private final FilterChain filterChain = Mockito.mock(FilterChain.class);

    @ParameterizedTest
    @ValueSource(strings = {"invalidToken", "", "token", "null"})
    void doFilterInternal_withVariousTokens_shouldNotSetAuthentication(String token) throws ServletException, IOException {
        if ("null".equals(token)) {
            when(request.getHeader("Authorization")).thenReturn(null);
        } else {
            when(request.getHeader("Authorization")).thenReturn(token);
        }

        securityFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
