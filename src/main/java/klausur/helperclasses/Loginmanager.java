package klausur.helperclasses;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import klausur.beans.Itemdata;
import klausur.beans.Userdata;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Loginmanager {


    public  static boolean IsLoggedIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.err.println(request.getSession().getAttribute("myUserdata"));
        if (request.getSession().getAttribute("myUserdata") == null) {
           return false;
        }
        return true;
    }

    public static void login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = !request.getParameter("username").isBlank() ? request.getParameter("username") : null;
        String password = !request.getParameter("password").isBlank() ? request.getParameter("password") : null;

        if (username == null || password == null) {
            String error = "invalid input";
            response.sendRedirect("/login.jsp?error=" + error);

        } else {
            PersistenceUtil<Userdata> persistenceUtil = new PersistenceUtil<Userdata>(Userdata.class);

            persistenceUtil.obtainAll().forEach(User -> {
                //System.out.println(User.getUsername() + " " + User.getPassword());

            });

            Userdata myUserdata = new Userdata();

            List<Userdata> usernames = persistenceUtil.obtainWhere("username", String.class, username);
            List<Userdata> passwords = persistenceUtil.obtainWhere("password", String.class, password);

            //schaue ob ein paar aus password und username existiert. Stelle bean zuverfügung sofern vorhanden
            usernames.forEach(userdata -> passwords.forEach(pass -> {
                //  System.err.println(pass.getId() + " " +  userdata.getId());
                if (pass.getId().equals(userdata.getId())) {

                    myUserdata.setId(userdata.getId());
                    myUserdata.setUsername(userdata.getUsername());
                    myUserdata.setPassword(userdata.getPassword());
                    myUserdata.setWarenkorb(userdata.getWarenkorb());

                    if(myUserdata.getUsername().equals("user1")){
                        request.getSession().setAttribute("admin", true);
                    } else {
                        request.getSession().setAttribute("admin", null);
                    }
                        request.getSession().setAttribute("myUserdata", myUserdata);

                }
            }));

            //gehe zurück sofer ein fehler aufgetretten ist // anmeldedaten nicht correct
            if (myUserdata.getUsername() == null) {
                String error = "Invalid Credentials, pls check agian";
                response.sendRedirect("/login.jsp?error=" + error);
            } else {
                response.sendRedirect("/");
            }

        }
    }

    public static void register(HttpServletRequest request, HttpServletResponse response) throws IOException {

        PersistenceUtil<Userdata> datapersistenceUtil = new PersistenceUtil<>(Userdata.class);

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String passwordrepeat = request.getParameter("password2");

        if (!datapersistenceUtil.obtainWhere("username", String.class, username).isEmpty()) {
            String message = "username not avaliable";
            response.sendRedirect("/login.jsp?error=" + message);
        } else {
            if (!password.equals(passwordrepeat)) {
                String message = "passwords dont match";
                response.sendRedirect("/login.jsp?error=" + message);
            } else {

                ValidationUtil<Userdata> validationUtil = new ValidationUtil<>();

                Userdata userdata1 = new Userdata();

                userdata1.setUsername(username);
                userdata1.setPassword(password);

                try {
                    if(!validationUtil.isValid(userdata1)){
                        StringBuilder error = new StringBuilder();

                        validationUtil.getViolations().forEach(violation -> error.append(violation.getMessage()));

                        response.sendRedirect("/login.jsp?error=" + error);

                    } else {
                        datapersistenceUtil.saveOrUpdate(userdata1);
                        request.getSession().setAttribute("myUserdata", userdata1);
                        response.sendRedirect("/");
                    }
                } catch (Exception error) {

                    response.sendRedirect("/login.jsp?error=" + error);
                }
            }
        }
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {


        System.err.println(request.getSession().getAttribute("items") != null);
        System.err.println(request.getSession().getAttribute("warenkorb") != null);

        System.err.println((request.getSession().getAttribute("warenkorb") != null) && (request.getSession().getAttribute("items") != null));

        if ((request.getSession().getAttribute("warenkorb") != null) && (request.getSession().getAttribute("items") != null)) {

            ArrayList<Itemdata> currentitems = (ArrayList<Itemdata>) request.getSession().getAttribute("warenkorb");

            ArrayList<Itemdata> database = (ArrayList<Itemdata>) request.getSession().getAttribute("items");

            System.err.println(currentitems);
            System.err.println(database);

            for (Itemdata item : database) {

                for (int i = 0; i < currentitems.toArray().length; i++) {

                    System.err.println(currentitems.get(i).getName());
                    System.err.println(item.getName());

                    if (currentitems.get(i).getName().equals(item.getName())) {

                        item.setAmount(item.getAmount() + currentitems.get(i).getAmount());

                    }
                }
            }
            PersistenceUtil<Itemdata> persistenceUtil = new PersistenceUtil<>(Itemdata.class);

            database.forEach(persistenceUtil::saveOrUpdate);

        }

        request.getSession().setAttribute("helper", null);

        if (request.getSession().getAttribute("myUserdata") != null) {
            request.getSession().setAttribute("myUserdata", null);
        }

        if (request.getSession().getAttribute("warenkorb") != null) {
            request.getSession().setAttribute("warenkorb", null);
        }

        response.sendRedirect("/");
    }
}
