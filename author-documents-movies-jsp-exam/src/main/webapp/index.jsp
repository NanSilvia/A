<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Forum - Welcome</title>
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
      }
      h1 {
        margin-bottom: 2rem;
      }
      .btn {
        display: inline-block;
        margin: 0 1rem;
        padding: 0.75rem 2rem;
        font-size: 1.1rem;
        border: none;
        border-radius: 5px;
        background: #007bff;
        color: #fff;
        cursor: pointer;
        transition: background 0.2s;
        text-decoration: none;
      }
      .btn:hover {
        background: #0056b3;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <h1>Welcome to the Forum</h1>
      <a href="login.jsp" class="btn">Login</a>
    </div>
  </body>
</html>
