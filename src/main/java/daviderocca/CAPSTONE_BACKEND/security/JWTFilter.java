package daviderocca.CAPSTONE_BACKEND.security;




import daviderocca.CAPSTONE_BACKEND.entities.User;
import daviderocca.CAPSTONE_BACKEND.exceptions.UnauthorizedException;
import daviderocca.CAPSTONE_BACKEND.services.UserService;
import daviderocca.CAPSTONE_BACKEND.tools.JWTTools;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JWTFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTools jwtTools;

   @Autowired
   private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new UnauthorizedException("Inserire il token nell'Authorization Header nel formato corretto!");

        String accessToken = authHeader.replace("Bearer ", "");
        jwtTools.verifyToken(accessToken);

        UUID idUser = UUID.fromString(jwtTools.extractIdFromToken(accessToken));
        User activeUser = this.userService.findUserById(idUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(activeUser, null, activeUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return new AntPathMatcher().match("/noAuth/**", request.getServletPath());
    }
}
