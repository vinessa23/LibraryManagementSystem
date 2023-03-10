/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import entity.Book;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import session.LendingSessionBeanLocal;

/**
 *
 * @author vinessa
 */
@Named(value = "bookManagedBean")
@ViewScoped
public class BookManagedBean implements Serializable {

    @EJB
    private LendingSessionBeanLocal lendingSessionBeanLocal;
    
    private Long bookId;
    private String title;
    private String isbn;
    private String author;
    
    private List<Book> books;
    private String searchType = "TITLE";
    private String searchString;
    /**
     * Creates a new instance of BookManagedBean
     */
    public BookManagedBean() {
    }
    
    @PostConstruct
    public void init() {
        if (getSearchString() == null || getSearchString().equals("")) {
            setBooks(lendingSessionBeanLocal.retrieveAllBooks());
        } else {
            switch (getSearchType()) {
                case "TITLE":
                    setBooks(lendingSessionBeanLocal.retrieveBooksByTitle(getSearchString()));
                    break;
                case "ISBN": {
                    setBooks(lendingSessionBeanLocal.retrieveBooksByISBN(getSearchString()));
                    break;
                }
                case "AUTHOR": {
                    setBooks(lendingSessionBeanLocal.retrieveBooksByAuthor(getSearchString()));
                }
            }
        }
    }
    
    public void handleSearch() {
        init();
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
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
