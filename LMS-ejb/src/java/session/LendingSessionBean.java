/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Book;
import entity.LendAndReturn;
import entity.Member;
import error.BookNotFoundException;
import error.FineNotPaidException;
import error.LendingNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.faces.context.FacesContext;

/**
 *
 * @author vinessa
 */
@Stateless
public class LendingSessionBean implements LendingSessionBeanLocal {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Book> retrieveAllBooks() {
        Query query = em.createQuery("SELECT b FROM Book b");
        List<Book> books = query.getResultList();
        //IF want to do lazy fetching
        for (Book c : books) {
            c.getLending().size(); //for to many relationship
        }
        return books;
    }

    //retrieve by primary key ID
    @Override
    public Book retrieveBookById(Long id) throws BookNotFoundException {
        Book book = em.find(Book.class, id);
        if (book != null) {
            book.getLending().size();
            return book;
        } else {
            throw new BookNotFoundException("Book ID " + id + " does not exist!");
        }
    }
    
    @Override
    public List<Book> retrieveBooksByAuthor(String author) {
        Query q;
        if (author != null) {
            q = em.createQuery("SELECT m FROM Book m WHERE LOWER(m.author) LIKE :author");
            q.setParameter("author", "%" + author.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT m FROM Book m");
        }
        List<Book> books = q.getResultList();
        for (Book b : books) {
            b.getLending().size(); //for to many relationship
        }
        return books;
    }
    
    @Override
    public List<Book> retrieveBooksByTitle(String title) {
        Query q;
        if (title != null) {
            q = em.createQuery("SELECT m FROM Book m WHERE LOWER(m.title) LIKE :title");
            q.setParameter("name", "%" + title.toLowerCase() + "%");
        } else {
            q = em.createQuery("SELECT m FROM Book m");
        }

        List<Book> books = q.getResultList();
        for (Book b : books) {
            b.getLending().size(); //for to many relationship
        }
        return books;
    }
    
    @Override
    public Book retrieveBookByISBN(String isbn) throws BookNotFoundException {
        Query query = em.createQuery("SELECT m FROM Book m WHERE LOWER(m.isbn) = :isbn");
        query.setParameter("isbn", isbn.toLowerCase());
        try {
            Book book = (Book) query.getSingleResult();
            book.getLending().size();
            return book;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new BookNotFoundException("Book ISBN " + isbn + " does not exist!");
        }
    }
    
    @Override
    public List<Book> retrieveBooksByISBN(String isbn) {
        Query query = em.createQuery("SELECT m FROM Book m WHERE LOWER(m.isbn) LIKE :isbn");
        query.setParameter("isbn", "%" + isbn.toLowerCase() + "%");
        List<Book> books = query.getResultList();
        for (Book b : books) {
            b.getLending().size(); //for to many relationship
        }
        return books;
    }
    
    @Override
    public LendAndReturn retrieveLendingByBook(Book book) throws LendingNotFoundException {
        //find lending details that match this book and hasnt been return yet
        Query query = em.createQuery("SELECT l FROM LendAndReturn l WHERE l.bookId = :bookId AND l.returnDate IS NULL");
        query.setParameter("bookId", book.getBookId());
        try {
            return (LendAndReturn) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new LendingNotFoundException("Book is not currently being lent");
        }
    }
    
    @Override
    public void lendBooks(Member m, List<Book> books) {
        // check whether the books are available
        for (Book b : books) {
            if(!isBookAvailable(b)) {
                //return error
            }
        }
        //check that member only lend 2 books
        int numBooksLended = numLendedBooksMember(m);
        if(books.size() + numBooksLended > 2) {
            //return error
        }
        
        for (Book b : books) {
            LendAndReturn lend = new LendAndReturn();
            lend.setMemberId(m.getMemberId());
            lend.setBookId(b.getBookId());
            ZoneId defaultZoneId = ZoneId.systemDefault();
            LocalDate todayLD = LocalDate.now();
            Date todayDate = Date.from(todayLD.atStartOfDay(defaultZoneId).toInstant());
            lend.setLendDate(todayDate);
            lend.setFineAmount(BigDecimal.ZERO);
            lend.setReturnDate(null);
            lend.setBook(b);
            lend.setMember(m);
            b.getLending().add(lend);
            m.getLending().add(lend);
            em.persist(lend);
            em.flush();
        }
    }

    @Override 
    public BigDecimal calculateFine(LendAndReturn lending) {
        long numDays = ChronoUnit.DAYS.between(lending.getLendDate().toInstant(), LocalDate.now());
        if(Long.compare(numDays, 14l) < 0) { //lending period < 14 days
            return BigDecimal.ZERO;
        } else {
            BigDecimal numDaysFined = new BigDecimal(Long.sum(numDays, -12l));
            BigDecimal finePerDay = new BigDecimal(0.5);
            return numDaysFined.multiply(finePerDay);
        }
    }
    
    @Override
    public void returnBook(LendAndReturn lending, boolean isFinePaid) throws FineNotPaidException {
        if(isFinePaid) {
            lending.setFineAmount(calculateFine(lending));
            ZoneId defaultZoneId = ZoneId.systemDefault();
            LocalDate todayLD = LocalDate.now();
            Date todayDate = Date.from(todayLD.atStartOfDay(defaultZoneId).toInstant());
            lending.setReturnDate(todayDate);
        } else {
            throw new FineNotPaidException("Member must pay the fine first before returning the book");
        }
    }
    
    @Override
    public List<LendAndReturn> getActiveLendings() {
        Query query = em.createQuery("SELECT l FROM LendAndReturn l WHERE l.returnDate IS NULL");
        return query.getResultList();
    }
    
    @Override
    public List<Book> getLendedBooks() {
        List<LendAndReturn> activeLendings = getActiveLendings();
        List<Book> lendedBooks = new ArrayList<Book>();
        for(LendAndReturn l : activeLendings) {
            lendedBooks.add(l.getBook());
        }
        return lendedBooks;
    }
    
    @Override
    public List<Book> getAvailableBooks() {
        List<Book> books = retrieveAllBooks();
        books.removeAll(getLendedBooks());
        return books;
    }
    
    @Override
    public boolean isBookAvailable(Book book) {
        List<Book> availableBooks = getAvailableBooks();
        if(availableBooks.contains(book)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public int numLendedBooksMember(Member m) {
        int res = 0;
        List<LendAndReturn> activeLendings = getActiveLendings();
        for(LendAndReturn l : activeLendings) {
            if(l.getMemberId().equals(m.getMemberId())) {
                res++;
            }
        }
        return res;
    }
}
