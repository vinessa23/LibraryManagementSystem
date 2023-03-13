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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import session.LendingSessionBeanLocal;
import session.MemberSessionBeanLocal;

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
    
    public void submitLendMember() {
        PrimeFaces current = PrimeFaces.current();
        FacesContext context = FacesContext.getCurrentInstance();       
        if (selectedMember == null) {
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Select at least 1 member"));
            return;
        }
        int numBooks = lendingSessionBeanLocal.numLendedBooksMember(selectedMember);
        if(numBooks == 2) {
            selectedMember = null;
//            current.executeScript("PF('myDialogVar').show();");
//            RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Select at least one item"));
            context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This member already borrowed 2 books!"));
        }
    }
    
    public void submitLendBooks() {
        int numBooks = lendingSessionBeanLocal.numLendedBooksMember(selectedMember);
        
    }
    
    public void bookRowSelect(SelectEvent event) {
        PrimeFaces current = PrimeFaces.current();
        FacesContext context = FacesContext.getCurrentInstance();
        int numBooks = lendingSessionBeanLocal.numLendedBooksMember(selectedMember);
        if (selectedBooks.size() > (2 - numBooks)) {
            selectedBooks.remove(event.getObject());
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This member can only lend " + (2 - numBooks) + " books!"));
            return;
        }
        if (!lendingSessionBeanLocal.isBookAvailable((Book)event.getObject())) {
            selectedBooks.remove(event.getObject());
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This book is not currently available."));
            return;
        }
    }
    
    public void lendBook() {
        
    }
}
