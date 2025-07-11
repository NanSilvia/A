// LoginServlet.java
// Handles user login: session creation, authentication, and redirects.
package com.reservations.servlet;

import com.reservations.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    // Handles POST requests for login form submission
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String moviedocname = request.getParameter("moviedocname");

        // Try to authenticate user
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT id, name, documentList, movieList FROM Authors WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Login successful: create session and redirect to topics

                        boolean isLogged = false;
                        List<Integer> documentIds = Arrays.asList(rs.getString("documentList").split(",")).stream()
                                .map(p -> Integer.parseInt(p)).toList();
                        List<Integer> movieIds = Arrays.asList(rs.getString("movieList").split(",")).stream()
                                .map(p -> Integer.parseInt(p)).toList();
                        // compare the moviedocname with the db
                        PreparedStatement s1 = conn.prepareStatement("SELECT id from Documents WHERE name=?");
                        s1.setString(1, moviedocname);
                        try (ResultSet r1 = s1.executeQuery()) {
                            if (r1.next()) {
                                int docid = r1.getInt("id");
                                if (documentIds.contains(docid)) {
                                    isLogged = true;
                                }
                            }
                        }
                        if (isLogged == false) {
                            PreparedStatement s2 = conn.prepareStatement("SELECT id from Movies WHERE title=?");
                            s2.setString(1, moviedocname);
                            try (ResultSet r2 = s2.executeQuery()) {
                                if (r2.next()) {
                                    int movid = r2.getInt("id");
                                    if (movieIds.contains(movid)) {
                                        isLogged = true;
                                    }
                                }
                            }
                        }

                        if (isLogged == false) {
                            request.setAttribute("error", "Invalid document or movie name");
                            request.getRequestDispatcher("login.jsp").forward(request, response);
                            return;
                        } else {
                            HttpSession session = request.getSession();
                            session.setAttribute("userId", rs.getInt("id"));
                            session.setAttribute("username", rs.getString("name"));
                            response.sendRedirect("documentsmovies");
                        }

                    } else {
                        // Login failed: show error
                        request.setAttribute("error", "Invalid username");
                        request.getRequestDispatcher("login.jsp").forward(request, response);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error occurred");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    // Handles GET requests: show login page or redirect if already logged in
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            // Already logged in
            response.sendRedirect("documentsmovies");
        } else {
            // Show login page
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}