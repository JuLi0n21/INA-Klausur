<!DOCTYPE html>
<head>

   <title>URL-Parameter Eingabe</title>
   <meta charset="utf-8">
    </head>
    <body>

    <%@ page import="klausur.beans.Userdata" %>
    <% if(session.getAttribute("mybean") != null)
    {
        response.sendRedirect("Warenkorb.jsp");
    }  %>

    <h1>Login</h1>

    <form action="controller" method="post">
        <input type="text" name="username" placeholder="username">
        <input type="password" name="password" placeholder="password">
        <input type="submit" name="login" value="login">
    </form>

    <hr>
    <h1>Register</h1>
        <form action="controller" method="post">
            <input type="text" name="username" placeholder="username">
            <input type="password" name="password" placeholder="password">
            <input type="password" name="password2" placeholder="Repeat password">
            <input type="submit" name="register" value="register">
        </form>
<!-- Gibt Fehler aus so weit ein erorr zurück gekommen ist -->
    <%@ page contentType="text/html; charset=UTF-8" %>
    <% String error = request.getParameter("error");
        if (error != null) { %>
            <p style="color: red;">
            Error: <%= error.toString() %>
            </p>
        <% } %>

    </body>
</html>