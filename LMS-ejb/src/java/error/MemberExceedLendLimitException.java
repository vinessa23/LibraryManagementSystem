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
public class MemberExceedLendLimitException extends Exception {

    /**
     * Creates a new instance of <code>MemberExceedLendLimitException</code>
     * without detail message.
     */
    public MemberExceedLendLimitException() {
    }

    /**
     * Constructs an instance of <code>MemberExceedLendLimitException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public MemberExceedLendLimitException(String msg) {
        super(msg);
    }
}
