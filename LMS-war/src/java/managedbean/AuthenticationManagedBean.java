package managedbean;

import entity.Staff;
import error.InvalidLoginException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import session.StaffSessionBeanLocal;

@Named(value = "authenticationManagedBean")
@SessionScoped
public class AuthenticationManagedBean implements Serializable {

    @EJB
    private StaffSessionBeanLocal staffSessionBeanLocal;

    private String username = null;
    private String password = null;
    private Staff loggedInStaff = null;

    public AuthenticationManagedBean() {
    }

    public String login() {
        //by right supposed to use a session bean to
        //do validation here
        //...
        
        try {
            loggedInStaff = staffSessionBeanLocal.staffLogin(username, password);
            return "/index.xhtml?faces-redirect=true";
        } catch (InvalidLoginException ex) {
            username = null;
            password = null;
            loggedInStaff = null;
            return "login.xhtml";
        }
    } //end login

    public String logout() {
        username = null;
        password = null;
        setLoggedInStaff(null);

        return "/login.xhtml?faces-redirect=true";
    } //end logout

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Staff getLoggedInStaff() {
        return loggedInStaff;
    }

    public void setLoggedInStaff(Staff loggedInStaff) {
        this.loggedInStaff = loggedInStaff;
    }

}
