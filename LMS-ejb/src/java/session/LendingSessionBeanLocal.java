/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Book;
import entity.LendAndReturn;
import error.BookNotFoundException;
import error.FineNotPaidException;
import error.LendingNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author vinessa
 */
@Local
public interface LendingSessionBeanLocal {

    public List<Book> retrieveAllBooks();

    public Book retrieveBookById(Long id) throws BookNotFoundException;

    public List<Book> retrieveBooksByAuthor(String author);

    public List<Book> retrieveBooksByTitle(String title);

    public Book retrieveBookByISBN(String isbn) throws BookNotFoundException;

    public LendAndReturn retrieveLendingByBook(Book book) throws LendingNotFoundException;

    public BigDecimal calculateFine(LendAndReturn lending);

    public void returnBook(LendAndReturn lending, boolean isFinePaid) throws FineNotPaidException;

    public List<LendAndReturn> getActiveLendings();

    public List<Book> getLendedBooks();

    public List<Book> getAvailableBooks();
    
}
