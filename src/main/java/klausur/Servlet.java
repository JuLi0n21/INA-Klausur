package klausur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/controller")
public class Servlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ControllerHelper controllerhelper;

        if (request.getSession().getAttribute("helper") == null) {
            controllerhelper = new ControllerHelper(request, response);
        } else {
            controllerhelper = (ControllerHelper) request.getSession().getAttribute("helper");
        }

        controllerhelper.doGet(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ControllerHelper controllerhelper;

        if (request.getSession().getAttribute("helper") == null) {
            controllerhelper = new ControllerHelper(request, response);
        } else {
            controllerhelper = (ControllerHelper) request.getSession().getAttribute("helper");
        }

        controllerhelper.doPost(request, response);

    }
}