package klausur;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import klausur.beans.Itemdata;
import klausur.beans.Userdata;
import klausur.helperclasses.Loginmanager;
import klausur.helperclasses.ValidationUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Routed die Funktionen weiter, oder fuehrt diese aus.
 */
public class ControllerHelper extends Helperbase {

    HttpServletRequest request;

    HttpServletResponse response;

    Userdata myUserdata;

    ArrayList<Itemdata> database = new ArrayList<>();

    public ControllerHelper(HttpServletRequest request, HttpServletResponse response) {

        super(request, response);
        this.request = request;
        this.response = response;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.doGet(request, response);

        this.database = super.getDatabase();
        //set database to displaying itemlist
        if ((request.getParameter("itemlist") != null) && request.getParameter("itemlist").equals("loaditems")) {
            request.getSession().setAttribute("items", this.database);
        }


        ArrayList<Itemdata> currentitems = new ArrayList<>();
        List<String> warenkorb = new ArrayList<>();
        List<String> amounts = new ArrayList<>();
        String message = "Default message.";

        //Bereits im Warenkorb
        if (request.getSession().getAttribute("warenkorb") != null) {
            currentitems = (ArrayList<Itemdata>) request.getSession().getAttribute("warenkorb");
        }

        //neue items oder veraenderungen des warenkorbs
        if (request.getParameterValues("items") != null) {
            warenkorb = Arrays.asList(request.getParameterValues("items"));
            amounts = Arrays.asList(request.getParameterValues("amount"));
        }

        //wenn warenkorb gespeichert werden soll
        if ((request.getParameter("action") != null) && request.getParameter("action").equals("save")) {

        if(!warenkorb.isEmpty()){

            if(currentitems.isEmpty())
            {
                for(Itemdata item : this.database){

                    for(int i = 0; i < warenkorb.toArray().length; i++){

                        if(item.getName().equals(warenkorb.get(i))){

                            //hole anzahl items
                            int itemamount = item.getAmount();

                            //gucke ob genung items da sind
                            if((itemamount - Integer.parseInt(amounts.get(i))) < 0){

                                //wenn nein setzte die restlichen items in den warenkorb, und setze restlichen items auf 0
                                currentitems.add(new Itemdata(item.getName(), itemamount, -1));
                                itemamount = 0;
                            } else {

                                //speichere items im warenkorb und ziehe von datenbank ab
                                currentitems.add(new Itemdata(item.getName(), Integer.parseInt(amounts.get(i)), -1));
                                itemamount = (itemamount - Integer.parseInt(amounts.get(i)));
                            }
                            item.setAmount(itemamount);

                        }
                    }
                }

                //fuege items der session hinzu
                message = "Warenkorb wurde gespeichert.";
                request.getSession().setAttribute("warenkorb",currentitems);
            } else{

                for(Itemdata item : this.database){

                    for(int i = 0; i < warenkorb.toArray().length; i++){

                        if(item.getName().equals(warenkorb.get(i))){

                            //hole anzahl items
                            int itemamount = item.getAmount();

                            //gucke ob genung items da sind
                            if((itemamount - currentitems.get(i).getAmount() + Integer.parseInt(amounts.get(i))) < 0){

                                //wenn nein setzte die restlichen items in den warenkorb, und setze restlichen items auf 0
                                amounts.set(i,item.getAmount().toString());
                                itemamount = 0;
                            } else {

                                //speichere items im warenkorb und ziehe von datenbank ab
                                currentitems.set(i,new Itemdata(item.getName(), Integer.parseInt(amounts.get(i)), -1));
                                itemamount = (itemamount - currentitems.get(i).getAmount() + Integer.parseInt(amounts.get(i)));
                            }
                            item.setAmount(itemamount);

                        }
                    }
                }

                currentitems.clear();
                for(int i = 0; i < warenkorb.toArray().length; i++){
                    currentitems.add(new Itemdata(warenkorb.get(i), Integer.parseInt(amounts.get(i)), -1));
                }

                message = "Warenkorb wurde gespeichert.";
                //fuege items der session hinzu
                request.getSession().setAttribute("warenkorb",currentitems);

            }
        } else {

            for(Itemdata item : this.database){

                for(int i = 0; i < currentitems.toArray().length; i++){

                    if(item.getName().equals(currentitems.get(i).getName())) {

                        //alle items aus warenkorb entfernt gespeicherten wieder zurueklegen
                        item.setAmount(item.getAmount() + currentitems.get(i).getAmount());

                    }
                }
            }

            message = "Warenkorb erfolgreich gelehrt";
            currentitems.clear();
        }

        //save items after updating values
            saveDatabase(database);

        if(!Loginmanager.IsLoggedIn(request,response)) {
                String error = "Bitte zu erst Anmelden.";
                response.sendRedirect("/login.jsp?error=" + error);
                System.err.println("not logged in");
                return;
            }

            response.sendRedirect("/?message=" + message);
            return;
        }


        //wenn produkte gekauft werden sollen. Werden nicht gespeichert.
        if ((request.getParameter("action") != null) && request.getParameter("action").equals("buy")) {

            this.database = super.getDatabase();
            request.getSession().setAttribute("items", database);

            if (!currentitems.isEmpty()) {

                for (Itemdata item : this.database) {

                    for (int i = 0; i < currentitems.toArray().length; i++) {

                        if (currentitems.get(i).getName().equals(item.getName())) {

                            item.setAmount(item.getAmount() + currentitems.get(i).getAmount());

                        }
                    }
                }

                message = "Viel Dank für ihren Einkauf!";
            }

            currentitems.clear();
            super.saveDatabase(this.database);
            request.getSession().setAttribute("warenkorb", currentitems);
            response.sendRedirect("/?message=" + message);
            return;
        }

        response.sendRedirect("/?message=" + message);
    }


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.doPost(request, response);

        //logout request
        if (request.getParameter("logout") != null) {
            Loginmanager.logout(request, response);
            return;
        }

        //Login request
        if (request.getParameter("login") != null) {
            Loginmanager.login(request, response);
            return;
        }

        //register request
        if (request.getParameter("register") != null) {
            Loginmanager.register(request, response);
            return;
        }

        if (request.getParameter("saveitems") != null) {

            StringBuilder error = new StringBuilder();

            try{

                //nicht perfekt aber, alle itembeans werden geloescht und dann alle neuen gespeichert
                List<String> names;
                List<String> amounts;
                List<String> prices;

                if(request.getParameterValues("Name") != null && request.getParameterValues("Amount") != null && request.getParameterValues("Price") != null) {

                    names = Arrays.asList(request.getParameterValues("Name"));
                    amounts = Arrays.asList(request.getParameterValues("Amount"));
                    prices = Arrays.asList(request.getParameterValues("Price"));

                    ArrayList<Itemdata> database = new ArrayList<>();

                    for(int i = 0; i < names.toArray().length; i++){

                        System.err.println(names.get(i));
                        System.err.println(amounts.get(i));
                        System.err.println(prices.get(i));

                        database.add(new Itemdata(names.get(i), Integer.parseInt(amounts.get(i)), Double.parseDouble(prices.get(i))));

                    }

                    itemPersistenceUtil.resetDatabase();


                    ValidationUtil<Itemdata> valdator = new ValidationUtil<Itemdata>();
                    database.forEach(item -> {
                        if(valdator.isValid(item)){
                            itemPersistenceUtil.saveOrUpdate(item);
                        } else {
                            error.append(item.getName()).append(" ");
                        }
                    });
                }

                if(!error.isEmpty()){
                    response.sendRedirect("/?error=Bei den folgenden Items ist ein Fehler aufgetrette: " + error);
                }
                response.sendRedirect("/?message=Datenbank gespeichert.");
                return;

            } catch (Exception err){

                response.sendRedirect("/?error= " + err );
                return;
            }

        }

        response.sendRedirect("/?error=Irgendwas ist Schief gelaufen.");
    }
}
