package klausur;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import klausur.beans.Itemdata;
import klausur.beans.Userdata;
import klausur.helperclasses.PersistenceUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The type Helperbase.
 */
public class Helperbase {

    protected HttpServletResponse response;
    protected HttpServletRequest request;
    protected PersistenceUtil<Userdata> datapersistenceUtil;

    protected PersistenceUtil<Itemdata> itemPersistenceUtil;

    /**
     * Instantiates a new Helperbase.
     *
     * @param request  the request
     * @param response the response
     */
    public Helperbase(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.datapersistenceUtil = new PersistenceUtil<>(Userdata.class);
        this.itemPersistenceUtil = new PersistenceUtil<>(Itemdata.class);

    }

    /**
     * Sets Helper and resets bean in cause no helper exists
     *
     * @param request  the request
     * @param response the response
     * @throws IOException the io exception
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      defaultvalues();
        if (request.getSession().getAttribute("helper") == null) {
            request.getSession().setAttribute("myUserdata", null);
            request.getSession().setAttribute("helper", this);
        }
    }

    /**
     * Sets Helper and resets bean in cause no helper exists
     *
     * @param request  the request
     * @param response the response
     * @throws IOException the io exception
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        defaultvalues();
        if (request.getSession().getAttribute("helper") == null) {
            request.getSession().setAttribute("myUserdata", null);
            request.getSession().setAttribute("helper", this);
        }
    }

    /**
     * Creates a set of default users and Produkts in cause they dont exist in the database.
     */
    protected void defaultvalues() {

        if (this.datapersistenceUtil.obtainWhere("username", String.class, "user1").isEmpty()) {
            Userdata userdata1 = new Userdata();

            userdata1.setUsername("user1");
            userdata1.setPassword("pw1");

            this.datapersistenceUtil.saveOrUpdate(userdata1);

            //damit nicht jedes mal die komplette daten bank nachgeguckt wird;
            if (this.datapersistenceUtil.obtainWhere("username", String.class, "user2").isEmpty()) {
                Userdata userdata2 = new Userdata();

                userdata2.setUsername("user2");
                userdata2.setPassword("pw2");

                this.datapersistenceUtil.saveOrUpdate(userdata2);
            }


            if (this.itemPersistenceUtil.obtainWhere("name", String.class, "Apple").isEmpty()) {

                Itemdata item1 = new Itemdata("Apple", 5,0.3);

                this.itemPersistenceUtil.saveOrUpdate(item1);
            }

            if (this.itemPersistenceUtil.obtainWhere("name", String.class, "Banana").isEmpty()) {

                Itemdata item2 = new Itemdata("Banana", 52,0.5);

                this.itemPersistenceUtil.saveOrUpdate(item2);
            }

            if (this.itemPersistenceUtil.obtainWhere("name", String.class, "Grapes").isEmpty()) {

                Itemdata item3 = new Itemdata("Grapes", 12,1.2);

                this.itemPersistenceUtil.saveOrUpdate(item3);
            }

            if (this.itemPersistenceUtil.obtainWhere("name", String.class, "Melon").isEmpty()) {

                Itemdata item4 = new Itemdata("Melon", 15, 3);

                this.itemPersistenceUtil.saveOrUpdate(item4);
            }

            if (this.itemPersistenceUtil.obtainWhere("name", String.class, "Pear").isEmpty()) {

                Itemdata item5 = new Itemdata("Pear", 2 , 1);

                this.itemPersistenceUtil.saveOrUpdate(item5);
                }

        }
    }

    protected ArrayList<Itemdata> getDatabase() {

        return new ArrayList<Itemdata>(this.itemPersistenceUtil.obtainAll());
    }


    protected void saveDatabase(ArrayList<Itemdata> database){
        database.forEach(item -> System.err.println(item.getName()));
        database.forEach(itemPersistenceUtil::saveOrUpdate);
    }

}
