/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Book;
import entity.Member;
import error.BookNotAvailableForLendException;
import error.MemberExceedLendLimitException;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
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

    private int activeIndex = 0;

    /**
     * Creates a new instance of LendingManagedBean
     */
    public LendingManagedBean() {
        selectedBooks = new ArrayList<Book>();
    }

    @PostConstruct
    public void init() {
        if (getMemberSearchString() == null || getMemberSearchString().equals("")) {
            setMembers(memberSessionBeanLocal.retrieveAllMembers());
        } else {
            switch (getMemberSearchType()) {
                case "NAME":
                    setMembers(memberSessionBeanLocal.retrieveMembersByName(getMemberSearchString()));
                    break;
                case "IDENTITYNO": {
                    setMembers(memberSessionBeanLocal.retrieveMembersByIdentityNo(getMemberSearchString()));
                    break;
                }
            }
        }

        if (getBookSearchString() == null || getBookSearchString().equals("")) {
            setBooks(lendingSessionBeanLocal.retrieveAllBooks());
        } else {
            switch (getBookSearchType()) {
                case "TITLE":
                    setBooks(lendingSessionBeanLocal.retrieveBooksByTitle(getBookSearchString()));
                    break;
                case "ISBN": {
                    setBooks(lendingSessionBeanLocal.retrieveBooksByISBN(getBookSearchString()));
                    break;
                }
                case "AUTHOR": {
                    setBooks(lendingSessionBeanLocal.retrieveBooksByAuthor(getBookSearchString()));
                }
            }
        }
    }
    
    public boolean isBookAvailable(Book b) {
        return lendingSessionBeanLocal.isBookAvailable(b);
    }

    public void handleSearch() {
        init();
    }

    public void goToTab(int index) {
        this.setActiveIndex(index);
    }
    
    public void tabChangedListener(TabChangeEvent event) {
        System.out.println("Tab changed to " + event.getTab().getId());
        System.out.println("Tab changed to " + event.getTab().getTitle());
    }

    public void submitLendMember() {
        PrimeFaces current = PrimeFaces.current();
        FacesContext context = FacesContext.getCurrentInstance();
        if (getSelectedMember() == null) {
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Select at least 1 member"));
        }
        int numBooks = lendingSessionBeanLocal.numLendedBooksMember(getSelectedMember());
        if (numBooks == 2) {
            setSelectedMember(null);
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This member already borrowed 2 books!"));
//            current.executeScript("PF('myDialogVar').show();");
//            RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Select at least one item"));
//            context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This member already borrowed 2 books!"));
        }
        this.goToTab(1);
    }

    public void memberRowSelect(SelectEvent event) {
        PrimeFaces current = PrimeFaces.current();
        FacesContext context = FacesContext.getCurrentInstance();
        if (getSelectedMember() == null) {
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Select at least 1 member"));
        }
        int numBooks = lendingSessionBeanLocal.numLendedBooksMember(getSelectedMember());
        if (numBooks == 2) {
            setSelectedMember(null);
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This member already borrowed 2 books!"));
//            current.executeScript("PF('myDialogVar').show();");
//            RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage("Select at least one item"));
//            context.addMessage("growl", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This member already borrowed 2 books!"));
        }
    }

    public void submitLendBooks() {
        PrimeFaces current = PrimeFaces.current();
        FacesContext context = FacesContext.getCurrentInstance();
        if (getSelectedBooks().size() == 0) {
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "You must at least select 1 book!"));
            return;
        }
        int numBooks = lendingSessionBeanLocal.numLendedBooksMember(getSelectedMember());
        if (getSelectedBooks().size() > (2 - numBooks)) {
            setSelectedBooks(new ArrayList<Book>());
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This member exceed lend limit of 2 books"));
            return;
        }

    }

    public void bookRowSelect(SelectEvent event) {
        PrimeFaces current = PrimeFaces.current();
        FacesContext context = FacesContext.getCurrentInstance();
        int numBooks = lendingSessionBeanLocal.numLendedBooksMember(getSelectedMember());
        if (getSelectedBooks().size() > (2 - numBooks)) {
            getSelectedBooks().remove(event.getObject());
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This member can only lend " + (2 - numBooks) + " books!"));
            return;
        }
        if (!lendingSessionBeanLocal.isBookAvailable((Book) event.getObject())) {
            getSelectedBooks().remove(event.getObject());
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This book is not currently available."));
            return;
        }
    }

    public String lendBook() {
        PrimeFaces current = PrimeFaces.current();
        try {
            lendingSessionBeanLocal.lendBooks(getSelectedMember(), getSelectedBooks());
        } catch (BookNotAvailableForLendException ex) {
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "This book is not currently available."));
            return null;
        } catch (MemberExceedLendLimitException ex) {
            current.dialog().showMessageDynamic(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Member exceeds lend limit of 2 books."));
            return null;
        }
        return "index.xhtml?faces-redirect=true";
    }

    public Long getLendId() {
        return lendId;
    }

    public void setLendId(Long lendId) {
        this.lendId = lendId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Date getLendDate() {
        return lendDate;
    }

    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public List<Book> getSelectedBooks() {
        return selectedBooks;
    }

    public void setSelectedBooks(List<Book> selectedBooks) {
        this.selectedBooks = selectedBooks;
    }

    public String getBookSearchType() {
        return bookSearchType;
    }

    public void setBookSearchType(String bookSearchType) {
        this.bookSearchType = bookSearchType;
    }

    public String getBookSearchString() {
        return bookSearchString;
    }

    public void setBookSearchString(String bookSearchString) {
        this.bookSearchString = bookSearchString;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public Member getSelectedMember() {
        return selectedMember;
    }

    public void setSelectedMember(Member selectedMember) {
        this.selectedMember = selectedMember;
    }

    public String getMemberSearchType() {
        return memberSearchType;
    }

    public void setMemberSearchType(String memberSearchType) {
        this.memberSearchType = memberSearchType;
    }

    public String getMemberSearchString() {
        return memberSearchString;
    }

    public void setMemberSearchString(String memberSearchString) {
        this.memberSearchString = memberSearchString;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }
}
