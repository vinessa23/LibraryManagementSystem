/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;

/**
 *
 * @author vinessa
 */
@Named(value = "lendingManagedBean")
@ViewScoped
public class LendingManagedBean implements Serializable {

    /**
     * Creates a new instance of LendingManagedBean
     */
    public LendingManagedBean() {
    }
    
}
