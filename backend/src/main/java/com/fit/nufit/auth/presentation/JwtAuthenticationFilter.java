package com.fit.nufit.auth.presentation;

import com.fit.nufit.auth.application.JwtProvider;
import com.fit.nufit.auth.domain.AuthenticationToken;
import com.fit.nufit.auth.dto.LoginMember;
import com.fit.nufit.member.application.MemberService;
import com.fit.nufit.member.dto.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";

    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String accessToken = extractToken(request, accessHeader).orElse(null);
            String memberId = jwtProvider.getSubject(accessToken);
            if (accessToken != null) {
                jwtProvider.validateToken(accessToken);

                MemberResponse member = memberService.findById(Long.valueOf(memberId));
                saveAuthentication(member);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(HttpServletRequest request, String header) {
        return Optional.ofNullable(request.getHeader(header))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.replace(BEARER, ""));
    }

    private void saveAuthentication(MemberResponse response) {
        AuthenticationToken authentication = new AuthenticationToken(
                AuthorityUtils.NO_AUTHORITIES,
                new LoginMember(response.getId(), response.getName()),
                null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
