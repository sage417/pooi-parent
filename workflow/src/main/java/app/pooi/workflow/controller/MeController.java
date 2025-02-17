package app.pooi.workflow.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RestController
public class MeController {

    @Resource
    private ThreadPoolExecutor eventPush;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("/me")
    public UserInfoDto getGreeting(JwtAuthenticationToken auth) {
        MDC.put("username", auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME));

        log.info("greeting with traceId");

        eventPush.execute(() -> {
            stringRedisTemplate.boundValueOps("key").get();
            log.info("greeting form executor");
        });
        MDC.clear();
        return new UserInfoDto(
                auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

    public record UserInfoDto(String name, List<String> roles) {}
}
