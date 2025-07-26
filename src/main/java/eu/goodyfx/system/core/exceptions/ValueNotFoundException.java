package eu.goodyfx.system.core.exceptions;

public class ValueNotFoundException extends Exception {

    /**
     * This Exceptions Throws whenever a Value was not Found.
     * @param errorMessage The Message
     */
    public ValueNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
