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
public class MemberIdentityExistException extends Exception {

    /**
     * Creates a new instance of <code>MemberIdentityExistException</code>
     * without detail message.
     */
    public MemberIdentityExistException() {
    }

    /**
     * Constructs an instance of <code>MemberIdentityExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public MemberIdentityExistException(String msg) {
        super(msg);
    }
}
