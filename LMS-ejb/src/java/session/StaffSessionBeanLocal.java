/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Staff;
import error.InvalidLoginException;
import error.StaffNotFoundException;
import javax.ejb.Local;

/**
 *
 * @author vinessa
 */
@Local
public interface StaffSessionBeanLocal {

    public Staff retrieveStaffByEmail(String email) throws StaffNotFoundException;

    public Staff staffLogin(String email, String password) throws InvalidLoginException;
    
}
