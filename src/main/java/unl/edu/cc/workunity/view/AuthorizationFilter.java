package unl.edu.cc.workunity.view;

import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import unl.edu.cc.workunity.domain.security.User;
import unl.edu.cc.workunity.view.security.UserSession;

import java.io.IOException;
import java.util.logging.Logger;

@WebFilter("*.xhtml")
public class AuthorizationFilter implements Filter {

    private static Logger logger = Logger.getLogger(AuthorizationFilter.class.getName());

    @Inject
    UserSession userSession;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpReq = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResp = (HttpServletResponse) servletResponse;

        // Obtener la ruta solicitada
        String requestPath = httpReq.getRequestURI().substring(httpReq.getContextPath().length());
        String method = httpReq.getMethod();

        logger.info("-----> Request path: " + requestPath + " --> HTTP Method: " + method);

        // 1. Permitir recursos públicos
        if (requestPath.startsWith("/public/")
                || requestPath.startsWith("/resources/")
                || requestPath.startsWith("/jakarta.faces.resource/")
                || requestPath.equals("/login.xhtml")
                || requestPath.equals("/register.xhtml")
                || requestPath.equals("/index.xhtml")
                || requestPath.equals("/")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 2. Obtener usuario autenticado desde la sesión
        User user = userSession.getUser();

        // 3. Redirigir si no está autenticado
        if (user == null) {
            httpResp.sendRedirect(httpReq.getContextPath() + "/login.xhtml");
            return;
        }

        // 4. Usuario autenticado -> permitir acceso
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
