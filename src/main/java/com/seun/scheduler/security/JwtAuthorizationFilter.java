package com.seun.scheduler.security;

import com.seun.scheduler.domain.Member;
import com.seun.scheduler.dto.CommonResponse;
import com.seun.scheduler.repository.MemberRepository;
import com.seun.scheduler.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtil.getJwtFromHeader(request);
        if (StringUtils.hasText(token)) {

            if (!jwtUtil.validateToken(token)) {
                log.error("Token Error: 유효하지 않은 토큰입니다.");
                sendErrorResponse(response, "유효하지 않은 토큰입니다.");
                return;
            }

            String userId = jwtUtil.getUserId(token);

            try {
                log.info("인증 시도 중인 유저 ID: {}", userId);
                setAuthentication(userId);
            } catch (Exception e) {
                log.error(e.getMessage());
                sendErrorResponse(response, "인증 과정에서 오류가 발생했습니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);

        log.info("인증 성공! 유저: {}, 권한: {}", authentication.getName(), authentication.getAuthorities());

        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    private Authentication createAuthentication(String username) {
        Member member = memberRepository.findByMemberId(username).orElseThrow(() -> new UsernameNotFoundException("not find Id"));

        // UserDetails userDetails = userService.loadUserByUsername(username);
        UserDetails userDetails = new UserDetailsImpl(member);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        CommonResponse<Void> errorResponse = CommonResponse.<Void>builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .code(HttpStatus.UNAUTHORIZED.name())
                .message(message)
                .data(null)
                .build();

        String result = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(result);
    }
}
