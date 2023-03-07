/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Staff;
import error.InvalidLoginException;
import error.StaffNotFoundException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author vinessa
 */
@Stateless
public class StaffSessionBean implements StaffSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    //retrieve by non-ID that must be unique
    @Override
    public Staff retrieveStaffByEmail(String email) throws StaffNotFoundException {
        Query query = em.createQuery("SELECT c FROM Staff c WHERE LOWER(c.email) = :inEmail");
        query.setParameter("inEmail", email.toLowerCase());
        try {
            Staff staff = (Staff) query.getSingleResult();
            return staff;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new StaffNotFoundException("Staff email " + email + " does not exist!");
        }
    }

    @Override
    public Staff staffLogin(String email, String password) throws InvalidLoginException {
        try {
            Staff staff = retrieveStaffByEmail(email);

            if (staff.getPassword().equals(password)) {
                return staff;
            } else {
                throw new InvalidLoginException("Email does not exist or invalid password!");
            }
        } catch (StaffNotFoundException ex) {
            throw new InvalidLoginException("Email does not exist or invalid password!");
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
