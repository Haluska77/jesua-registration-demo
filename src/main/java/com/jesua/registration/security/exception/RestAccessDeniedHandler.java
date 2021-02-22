package com.jesua.registration.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesua.registration.exception.ErrorResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException exception) throws IOException {

        ErrorResponse<Throwable> response = new ErrorResponse<>(exception.getCause(), "Access Denied !!!");
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        OutputStream out = httpServletResponse.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(out,response);
        out.flush();
    }

}
