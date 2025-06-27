/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package app.pooi.workflow.oauth2login;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

/**
 * @author ch4mp&#64;c4-soft.com
 */
@Controller
public class UiController {
    @GetMapping("/")
    public String getIndex(Model model, Authentication auth) {
        model.addAttribute(
                "name",
                auth instanceof OAuth2AuthenticationToken oauth && oauth.getPrincipal() instanceof OidcUser oidc ?
                        oidc.getPreferredUsername() :
                        "");
        model.addAttribute("isAuthenticated", auth != null && auth.isAuthenticated());
        model.addAttribute("isNice", auth != null && auth.getAuthorities().stream().anyMatch(authority -> Objects.equals("role1", authority.getAuthority())));
        return "index.html";
    }

    @GetMapping("/nice")
    public String getNice(Model model, Authentication auth) {
        return "nice.html";
    }
}