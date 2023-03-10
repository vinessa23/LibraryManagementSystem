/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Book;
import entity.Member;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import session.LendingSessionBeanLocal;
import session.MemberSessionBeanLocal;
import session.StaffSessionBeanLocal;

/**
 *
 * @author vinessa
 */
@Named(value = "lendingManagedBean")
@ViewScoped
public class LendingManagedBean implements Serializable {

    @EJB(name = "MemberSessionBeanLocal")
    private MemberSessionBeanLocal memberSessionBeanLocal;

    @EJB(name = "LendingSessionBeanLocal")
    private LendingSessionBeanLocal lendingSessionBeanLocal;
    
    private Long lendId;
    private Long memberId;
    private Long bookId;
    private Date lendDate;
    private Date returnDate;
    private BigDecimal fineAmount;
    
    private List<Book> books;
    private List<Book> selectedBooks;
    private String bookSearchType = "TITLE";
    private String bookSearchString;
    
    private List<Member> members;
    private Member selectedMember;
    private String memberSearchType = "NAME";
    private String memberSearchString;

    /**
     * Creates a new instance of LendingManagedBean
     */
    public LendingManagedBean() {
    }
    
    @PostConstruct
    public void init() {
        if (memberSearchString == null || memberSearchString.equals("")) {
            members = memberSessionBeanLocal.retrieveAllMembers();
        } else {
            switch (memberSearchType) {
                case "NAME":
                    members = memberSessionBeanLocal.retrieveMembersByName(memberSearchString);
                    break;
                case "IDENTITYNO": {
                    members = memberSessionBeanLocal.retrieveMembersByIdentityNo(memberSearchString);
                    break;
                }
            }
        }
        
        if (bookSearchString == null || bookSearchString.equals("")) {
            books = lendingSessionBeanLocal.retrieveAllBooks();
        } else {
            switch (bookSearchType) {
                case "TITLE":
                    books = lendingSessionBeanLocal.retrieveBooksByTitle(bookSearchString);
                    break;
                case "ISBN": {
                    books = lendingSessionBeanLocal.retrieveBooksByISBN(bookSearchString);
                    break;
                }
                case "AUTHOR": {
                    books = lendingSessionBeanLocal.retrieveBooksByAuthor(bookSearchString);
                }
            }
        }
    }
    
    public void handleSearch() {
        init();
    }
    
    
}
