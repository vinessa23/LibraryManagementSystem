/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package error;

/**
 *
 * @author vinessa
 */
public class BookNotAvailableForLendException extends Exception{

    /**
     * Creates a new instance of <code>BookNotAvailableForLendException</code>
     * without detail message.
     */
    public BookNotAvailableForLendException() {
    }

    /**
     * Constructs an instance of <code>BookNotAvailableForLendException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public BookNotAvailableForLendException(String msg) {
        super(msg);
    }
}
