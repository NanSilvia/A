<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Forum Login</title>
    <style>
      body {
        font-family: Arial, sans-serif;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        margin: 0;
        background-color: #f5f5f5;
      }
      .login-container {
        background-color: white;
        padding: 2rem;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        width: 100%;
        max-width: 400px;
      }
      .form-group {
        margin-bottom: 1rem;
      }
      label {
        display: block;
        margin-bottom: 0.5rem;
      }
      input {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid #ddd;
        border-radius: 4px;
        box-sizing: border-box;
      }
      button {
        width: 100%;
        padding: 0.75rem;
        background-color: #007bff;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
      }
      button:hover {
        background-color: #0056b3;
      }
      .error {
        color: red;
        margin-bottom: 1rem;
      }
    </style>
  </head>
  <body>
    <div class="login-container">
      <h2>Forum Login</h2>
      <c:if test="${not empty error}"
        >l
        <div class="error">${error}</div>
      </c:if>
      <form action="login" method="post" onsubmit="return validateForm()">
        <div class="form-group">
          <label for="username">Username:</label>
          <input type="text" id="username" name="username" required />
        </div>
        <div class="form-group">
          <label for="moviedocname">Movie or Document name:</label>
          <input type="text" id="moviedocname" name="moviedocname" required />
        </div>
        <button type="submit">Login</button>
      </form>
    </div>

    <script>
      function validateForm() {
        const username = document.getElementById("username").value.trim();
        const moviedocname = document
          .getElementById("moviedocname")
          .value.trim();

        if (username === "") {
          alert("Please enter a username");
          return false;
        }
        if (moviedocname === "") {
          alert(
            "Please enter a the name of a movie or a document you authored!"
          );
          return false;
        }
        return true;
      }
    </script>
  </body>
</html>
