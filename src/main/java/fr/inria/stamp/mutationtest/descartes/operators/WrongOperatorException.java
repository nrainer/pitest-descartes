package fr.inria.stamp.mutationtest.descartes.operators;


public class WrongOperatorException extends RuntimeException {

    public WrongOperatorException(String wrongID, String possibleCause) {
        this("Can't create a mutation operator from: " + wrongID + "Details: " + possibleCause);
    }

    public WrongOperatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongOperatorException(String message) {
        super(message);
    }
}
