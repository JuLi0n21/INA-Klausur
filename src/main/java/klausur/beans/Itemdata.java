package klausur.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Itemdata {

    @Id
    @GeneratedValue
    private int id;
    private String name;

    private Integer amount;

    private double price;
    public Itemdata() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Itemdata(String name, Integer amount, double price)
    {
        this.name = name;
        this.amount = amount;
        this.price = price;

    }
}
