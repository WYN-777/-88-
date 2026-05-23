package com.kxw.ta.web;

import com.kxw.ta.model.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        String fileName = request.getPathInfo() == null ? "" : request.getPathInfo().replaceFirst("^/", "");
        if (fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Path file = service.store().getUploadsDir().resolve(fileName).normalize();
        if (!file.startsWith(service.store().getUploadsDir()) || Files.notExists(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!service.canViewCv(user, fileName)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) {
            response.setContentType("application/pdf");
        } else if (lower.endsWith(".doc")) {
            response.setContentType("application/msword");
        } else if (lower.endsWith(".docx")) {
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        } else {
            response.setContentType("application/octet-stream");
        }
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName.replace("\"", "") + "\"");
        try (ServletOutputStream output = response.getOutputStream()) {
            Files.copy(file, output);
        }
    }
}
