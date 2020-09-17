package com.internet.shop.web.filter;

import com.internet.shop.lib.Injector;
import com.internet.shop.model.Role;
import com.internet.shop.model.User;
import com.internet.shop.service.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthorizationFilter implements Filter {
    private static final String USER_ID = "user_id";
    private static final Injector injector = Injector.getInstance("com.internet.shop");
    private final UserService userService = (UserService) injector.getInstance(UserService.class);
    private final Map<String, List<Role.RoleName>> protectedUrls = new HashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        protectedUrls.put("/users/all", List.of(Role.RoleName.ADMIN));
        protectedUrls.put("/users/delete", List.of(Role.RoleName.ADMIN));
        protectedUrls.put("/admin/orders", List.of(Role.RoleName.ADMIN));
        protectedUrls.put("/orders/delete", List.of(Role.RoleName.ADMIN));
        protectedUrls.put("/products/manage", List.of(Role.RoleName.ADMIN));
        protectedUrls.put("/products/add", List.of(Role.RoleName.ADMIN));
        protectedUrls.put("/products/delete", List.of(Role.RoleName.ADMIN));
        protectedUrls.put("/shopping-cart/products", List.of(Role.RoleName.USER));
        protectedUrls.put("/shopping-cart/products/add", List.of(Role.RoleName.USER));
        protectedUrls.put("/shopping-cart/products/remove", List.of(Role.RoleName.USER));
        protectedUrls.put("/orders/complete", List.of(Role.RoleName.USER));
        protectedUrls.put("/users/orders", List.of(Role.RoleName.USER));
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String url = req.getServletPath();
        if (protectedUrls.get(url) == null) {
            filterChain.doFilter(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        Long userId = (Long) session.getAttribute("user_id");
        User user = userService.get(userId);
        if (isAuthorized(user, protectedUrls.get(url))) {
            filterChain.doFilter(req, resp);
        } else {
            req.getRequestDispatcher("/WEB-INF/views/access-denied.jsp").forward(req, resp);
        }
    }

    @Override
    public void destroy() {

    }

    private boolean isAuthorized(User user, List<Role.RoleName> authorizedRoles) {
        for (Role.RoleName authorizedRole : authorizedRoles) {
            for (Role userRole : user.getRoles()) {
                if (authorizedRole.equals(userRole.getRoleName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
