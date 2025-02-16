package app.pooi.workflow.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class MeController {
    @GetMapping("/me")
    public UserInfoDto getGreeting(JwtAuthenticationToken auth) {
        log.info("greeting with traceId");
        return new UserInfoDto(
                auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

    public record UserInfoDto(String name, List<String> roles) {}
}
