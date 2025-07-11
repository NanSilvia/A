package com.reservations.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.reservations.dtos.EntityDTO;
import com.reservations.util.DatabaseUtil;
import com.reservations.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/documentsmovies")
public class DocumentsMoviesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = (int) request.getSession().getAttribute("userId");
        if (userId == 0) {
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        List<EntityDTO> documents = new ArrayList<>();
        List<EntityDTO> movies = new ArrayList<>();
        List<EntityDTO> entities = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT id, name, documentList, movieList FROM Authors WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        List<Integer> documentIds = Arrays.asList(rs.getString("documentList").split(",")).stream()
                                .map(p -> Integer.parseInt(p)).toList();
                        List<Integer> movieIds = Arrays.asList(rs.getString("movieList").split(",")).stream()
                                .map(p -> Integer.parseInt(p)).toList();
                        // get the documents form db for the user

                        for (int docid : documentIds) {
                            PreparedStatement s1 = conn
                                    .prepareStatement("SELECT id, name, contents from Documents WHERE id=?");

                            s1.setInt(1, docid);
                            try (ResultSet r1 = s1.executeQuery()) {
                                if (r1.next()) {
                                    String name = r1.getString("name");
                                    String contents = r1.getString("contents");
                                    EntityDTO entitate = new EntityDTO();
                                    entitate.setDescription(contents);
                                    entitate.setId(docid);
                                    entitate.setTitle(name);
                                    entitate.setType("document");
                                    documents.add(entitate);
                                }
                            }
                        }

                        for (int movid : movieIds) {
                            PreparedStatement s2 = conn
                                    .prepareStatement("SELECT id, title, duration from Movies WHERE id=?");

                            s2.setInt(1, movid);
                            try (ResultSet r2 = s2.executeQuery()) {
                                if (r2.next()) {
                                    String title = r2.getString("title");
                                    String duration = r2.getString("duration");
                                    EntityDTO entitate = new EntityDTO();
                                    entitate.setDescription(duration);
                                    entitate.setId(movid);
                                    entitate.setTitle(title);
                                    entitate.setType("movie");
                                    movies.add(entitate);
                                }
                            }
                        }

                        // interleave the 2 lists
                        Iterator<EntityDTO> it1 = documents.iterator();
                        Iterator<EntityDTO> it2 = movies.iterator();
                        while (it1.hasNext() || it2.hasNext()) {
                            if (it1.hasNext()) {
                                entities.add(it1.next());
                            }
                            if (it2.hasNext()) {
                                entities.add(it2.next());
                            }
                        }

                        request.setAttribute("documentsmovies", entities);

                        request.getRequestDispatcher("documentsmovies.jsp").forward(request, response);
                    }
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            JsonUtil.writeJsonResponse(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // verificam daca e ptr add sau delete
        if (request.getParameter("action") == "delete") {

            int movieid = Integer.parseInt(request.getParameter("id"));

            try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "delete from Movies where id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, movieid);

                    int updateCount = stmt.executeUpdate();
                    if (updateCount != 0) {
                        // delete the movie from all users

                        // get the users
                        String sql1 = "SELECT id, name, documentList, movieList FROM Authors";
                        try (PreparedStatement s1 = conn.prepareStatement(sql1)) {

                            try (ResultSet r1 = s1.executeQuery()) {
                                while (r1.next()) {
                                    List<Integer> movieIds = Arrays.asList(r1.getString("movieList").split(","))
                                            .stream()
                                            .map(p -> Integer.parseInt(p)).filter(p -> p != movieid).toList();
                                    String newMovieList = movieIds.stream().map(p -> p.toString())
                                            .collect(Collectors.joining(","));
                                    String sql2 = "Update Authors SET movieList=? where id=?";
                                    try (PreparedStatement s2 = conn.prepareStatement(sql2)) {
                                        s2.setString(1, newMovieList);
                                        s2.setInt(2, r1.getInt("id"));
                                        s2.executeUpdate();
                                    }
                                }
                            }
                        }
                        doGet(request, response);
                    }

                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                JsonUtil.writeJsonResponse(response, e);
                return;
            }
        } else {
            // e ptr add a document
            String name = request.getParameter("name");
            String content = request.getParameter("content");
            int userId = (int) request.getSession().getAttribute("userId");

            try (Connection conn = DatabaseUtil.getConnection()) {
                String sql = "insert into Documents(name, contents) Values(?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, name);
                    stmt.setString(2, content);

                    int updateCount = stmt.executeUpdate();

                    if (updateCount != 0) {
                        ResultSet rs = stmt.getGeneratedKeys();
                        rs.next();
                        int docid = rs.getInt(1);
                        // insert into the current user
                        String sql1 = "SELECT id, name, documentList, movieList FROM Authors where id=?";
                        try (PreparedStatement s1 = conn.prepareStatement(sql1)) {
                            s1.setInt(1, userId);
                            try (ResultSet r1 = s1.executeQuery()) {
                                if (r1.next()) {
                                    List<Integer> documentIds = Arrays.asList(r1.getString("documentList").split(","))
                                            .stream()
                                            .map(p -> Integer.parseInt(p)).toList();

                                    String newDocumentList = documentIds.stream().map(p -> p.toString())
                                            .collect(Collectors.joining(",")) + "," + docid;
                                    String sql2 = "Update Authors SET documentList=? where id=?";
                                    try (PreparedStatement s2 = conn.prepareStatement(sql2)) {
                                        s2.setString(1, newDocumentList);
                                        s2.setInt(2, userId);
                                        s2.executeUpdate();
                                    }
                                }
                            }
                        }
                        doGet(request, response);
                    }

                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                JsonUtil.writeJsonResponse(response, e);
                return;
            }
        }
    }
}
