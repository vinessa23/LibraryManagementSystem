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
import javax.ejb.Local;

/**
 *
 * @author vinessa
 */
@Local
public interface MemberSessionBeanLocal {

    public Long createNewMember(Member member) throws MemberIdentityExistException, UnknownPersistenceException;

    public List<Member> retrieveAllMembers();

    public Member retrieveMemberById(Long id) throws MemberNotFoundException;

    public List<Member> retrieveMembersByName(String name);

    public Member retrieveMemberByIdentityNo(String identity) throws MemberNotFoundException;

    public void updateMember(Member newM) throws MemberNotFoundException;

    public List<Member> retrieveMembersByIdentityNo(String identity);
    
}
