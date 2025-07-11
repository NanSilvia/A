package com.reservations.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.reservations.dtos.EntityDTO;
import com.reservations.util.DatabaseUtil;
import com.reservations.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/mostauthors")
public class MostAuthorsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = (int) request.getSession().getAttribute("userId");
        if (userId == 0) {
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        Map<Integer, Integer> documentFrequency = new HashMap<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT id, name, documentList, movieList FROM Authors";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        List<Integer> documentIds = Arrays.asList(rs.getString("documentList").split(",")).stream()
                                .map(p -> Integer.parseInt(p)).toList();

                        for (Integer docid : documentIds) {
                            if (!documentFrequency.containsKey(docid)) {
                                documentFrequency.put(docid, 1);
                            } else {
                                documentFrequency.put(docid, documentFrequency.get(docid) + 1);
                            }
                        }
                    }
                }
            }
            int maxDocumentId = documentFrequency.entrySet()
                    .stream()
                    .max((Entry<Integer, Integer> e1, Entry<Integer, Integer> e2) -> e1.getValue()
                            .compareTo(e2.getValue()))
                    .get().getValue();

            String sql1 = "SELECT id, name, contents FROM Documents where id=?";
            try (PreparedStatement s1 = conn.prepareStatement(sql1)) {
                s1.setInt(1, maxDocumentId);
                try (ResultSet r1 = s1.executeQuery()) {
                    if (r1.next()) {
                        EntityDTO mostAuthorsDoc = new EntityDTO();
                        mostAuthorsDoc.setId(maxDocumentId);
                        mostAuthorsDoc.setTitle(r1.getString("name"));
                        mostAuthorsDoc.setType("document");
                        mostAuthorsDoc.setDescription(r1.getString("contents"));
                        request.setAttribute("mostAuthorsDoc", mostAuthorsDoc);
                    }
                }
            }
            request.getRequestDispatcher("mostauthors.jsp").forward(request, response);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            JsonUtil.writeJsonResponse(response, e);
        }
    }
}
