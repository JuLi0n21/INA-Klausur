package klausur.beans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;

@Entity
public class Userdata {

    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Size(min = 3, max = 16, message = "username must be 3-16 chars long. ")
    private String username;

    @NotNull
    @Pattern(regexp = ".*\\S.*\\d.*", message = "must contain atlesat one number")
    @Size(min = 3, max = 16, message = "password must be 3-16 chars long")
    private String password;

    ArrayList<String> warenkorb;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getWarenkorb() {
        return warenkorb;
    }

    public void setWarenkorb(ArrayList<String> warenkorb) {
        this.warenkorb = warenkorb;
    }

}