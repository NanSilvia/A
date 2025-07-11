<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
  <head>
    <title>documentsmovies</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        background: #f5f5f5;
        height: 100vh;
        margin: 0;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      .container {
        background: #fff;
        padding: 2rem 3rem;
        border-radius: 10px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        text-align: center;
        overflow-y: scroll;
      }
      h1 {
        margin-bottom: 2rem;
      }
      .btn {
        display: inline-block;
        margin: 0 1rem;
        padding: 0.5rem 1rem;
        font-size: 0.75rem;
        border: none;
        border-radius: 3px;
        background: #007bff;
        color: #fff;
        cursor: pointer;
        transition: background 0.2s;
        text-decoration: none;
      }
      .btn:hover {
        background: #0056b3;
      }
      .post-container{
        display: flex;
        flex-direction: row;
        align-items: center;
      }
      .posts{
        flex-direction: column;
        display:flex;
        gap: 4px;
      }
      
    </style>
  </head>
  <body>
    <div class="container">
    <div class="header">
        <h1>Registrations</h1>
        <div>
            <a href="logout" class="btn btn-danger">Logout</a>
        </div>
    </div>

    <div class="post-form">
        <h3>Add document</h3>
        <form action="documentsmovies" method="post" onsubmit="return doGet()">
            <div class="form-group">
                <label for="name">Name: </label>
                <input id="name" name="name" required>
                <label for="content">Content: </label>
                <input id="content" name="content" required>
                <input type="hidden" name="action" value="add">
            </div>
            <button type="submit" class="btn btn-primary">ADD</button>
            ${error}
        </form>
    </div>

    <iframe src="/reservations/mostauthors" width="400" height="100"></iframe><br>

    <div class="posts">
        <h3>Documents Movies List</h3>
        <c:forEach items="${documentsmovies}" var="element">
            <div class="post-container">
                <div class="post-content">
                        Type: ${element.getType()},
                        ID: ${element.getId()},
                        ${element.getType()=="movie" ? "Title": "Name"}: ${element.getTitle()},
                        ${element.getType()=="movie" ? "Duration" : "Content"}: ${element.getDescription()},
                        
                </div>
                <div class="post-actions">
                  <c:if test="${element.getType()=='movie'}">
                          <form action="documentsmovies" method="post" style="display: inline;" class="submit-reservation-form">
                        <input type="hidden" name="id" value="${element.id}">
                        <input type="hidden" name="action" value="delete">
                        <button type="submit" class="btn btn-danger">
                            DELETE
                        </button>
                      </form>
                    </c:if>
                    
                </div>
            </div>
        </c:forEach>
    </div>
</div>

  </body>
</html>
