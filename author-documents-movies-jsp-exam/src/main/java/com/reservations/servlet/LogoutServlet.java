// LogoutServlet.java
// Handles user logout: session invalidation and redirect to front page.
package com.reservations.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    // Handles GET requests for logout
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Invalidate session to log out user
            session.invalidate();
        }
        // Redirect to front page (index.jsp)
        response.sendRedirect("index.jsp");
    }
}