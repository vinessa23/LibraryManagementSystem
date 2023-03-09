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

    public Staff staffLogin(String username, String password) throws InvalidLoginException;

    public Staff retrieveStaffByUsername(String username) throws StaffNotFoundException;
    
}
