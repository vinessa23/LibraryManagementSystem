/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Member;
import error.MemberIdentityExistException;
import error.MemberNotFoundException;
import error.UnknownPersistenceException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author vinessa
 */
@Stateless
public class MemberSessionBean implements MemberSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Long createNewMember(Member member) throws MemberIdentityExistException, UnknownPersistenceException {

        try {
            em.persist(member);
            em.flush(); //only need to flush bcs we are returning the id!
            return member.getMemberId();
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new MemberIdentityExistException("Identity number " + member.getIdentityNo() + " is already registered!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public void updateMember(Member newM) throws MemberNotFoundException {
        try {
            Member m = retrieveMemberById(newM.getMemberId());
            m.setFirstName(newM.getFirstName());
            m.setLastName(newM.getLastName());
            m.setGender(newM.getGender());
            m.setIdentityNo(newM.getIdentityNo());
            m.setPhone(newM.getPhone());
            m.setAddress(newM.getAddress());
        } catch (MemberNotFoundException e) {
            throw new MemberNotFoundException(e.getMessage());
        }
    }

    @Override
    public List<Member> retrieveAllMembers() {
        Query query = em.createQuery("SELECT m FROM Member m");
        List<Member> members = query.getResultList();
        //IF want to do lazy fetching
        for (Member c : members) {
            c.getLending().size(); //for to many relationship
        }
        return members;
    }

    //retrieve by primary key ID
    @Override
    public Member retrieveMemberById(Long id) throws MemberNotFoundException {
        Member member = em.find(Member.class, id);
        if (member != null) {
            member.getLending().size();
            return member;
        } else {
            throw new MemberNotFoundException("Member ID " + id + " does not exist!");
        }
    }

    //if null then get all members
    @Override
    public List<Member> retrieveMembersByName(String name) {
        Query q;
        if (name != null) {
            q = em.createQuery("SELECT m FROM Member m WHERE LOWER(m.firstName) LIKE :name OR LOWER(m.lastName) LIKE :name");
            q.setParameter("name", "%" + name.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT m FROM Member m");
        }

        List<Member> members = q.getResultList();
        for (Member c : members) {
            c.getLending().size(); //for to many relationship
        }
        return members;
    }

    @Override
    public Member retrieveMemberByIdentityNo(String identity) throws MemberNotFoundException {
        Query query = em.createQuery("SELECT m FROM Member m WHERE LOWER(m.identityNo) = :inIdentity");
        query.setParameter("inIdentity", identity.toLowerCase());
        try {
            Member member = (Member) query.getSingleResult();
            member.getLending().size();
            return member;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new MemberNotFoundException("Member identity " + identity + " does not exist!");
        }
    }
}
