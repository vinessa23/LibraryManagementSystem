/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Member;
import error.MemberIdentityExistException;
import error.UnknownPersistenceException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import session.MemberSessionBeanLocal;

/**
 *
 * @author vinessa
 */
@Named(value = "memberManagedBean")
@ViewScoped
public class MemberManagedBean implements Serializable {

    @EJB
    private MemberSessionBeanLocal memberSessionBeanLocal;
    
    private String firstName;
    private String lastName;
    private Character gender;
    private Integer age;
    private String identityNo;
    private String phone;
    private String address;
    
    private List<Member> members;
    private Long memberId;
    private Member selectedMember;
    
    private String searchType = "NAME";
    private String searchString;
    
    public MemberManagedBean() {
    }
    
    @PostConstruct
    public void init() {
        if (searchString == null || searchString.equals("")) {
            members = memberSessionBeanLocal.retrieveAllMembers();
        } else {
            switch (searchType) {
                case "NAME":
                    members = memberSessionBeanLocal.retrieveMembersByName(searchString);
                    break;
                case "IDENTITYNO": {
                    members = memberSessionBeanLocal.retrieveMembersByIdentityNo(searchString);
                    break;
                }
            }
        }
    }
    
    public void handleSearch() {
        init();
    }
    
    public String registerMember() {
        FacesContext context = FacesContext.getCurrentInstance();
        Member m = new Member();
        m.setFirstName(firstName.trim());
        m.setLastName(lastName.trim());
        m.setGender(Character.toUpperCase(gender));
        m.setAge(age);
        m.setIdentityNo(identityNo.trim());
        m.setPhone(phone);
        m.setAddress(address);
        
        try {
            memberSessionBeanLocal.createNewMember(m);
        } catch (MemberIdentityExistException | UnknownPersistenceException e) {
            //show with an error icon
            context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
            return null;
        }
        context.addMessage("growl", new FacesMessage("Success",
                "Successfully registered member"));
        return "memberList.xhtml?faces-redirect=true";
    }
    
        public void loadSelectedMember() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            this.setSelectedMember(memberSessionBeanLocal.retrieveMemberById(getMemberId()));
            firstName = this.getSelectedMember().getFirstName();
            lastName = this.getSelectedMember().getLastName();
            gender = this.getSelectedMember().getGender();
            age = this.getSelectedMember().getAge();
            identityNo = this.getSelectedMember().getIdentityNo();
            phone = this.getSelectedMember().getPhone();
            address = this.getSelectedMember().getAddress();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load customer"));
        }
    } //end loadSelectedMember

    public void updateMember(ActionEvent evt) {
        FacesContext context = FacesContext.getCurrentInstance();
        Member m = getSelectedMember();
        m.setFirstName(firstName);
        m.setLastName(lastName);
        m.setGender(gender);
        m.setAge(age);
        m.setIdentityNo(identityNo);
        m.setPhone(phone);
        m.setAddress(address);
        try {
            memberSessionBeanLocal.updateMember(m);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to update customer"));
            return;
        }
        //need to make sure reinitialize the customers collection
        init();
        context.addMessage(null, new FacesMessage("Success",
                "Successfully updated customer"));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Member getSelectedMember() {
        return selectedMember;
    }

    public void setSelectedMember(Member selectedMember) {
        this.selectedMember = selectedMember;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
    
}
