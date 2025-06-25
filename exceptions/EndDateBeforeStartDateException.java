package exceptions;
public class EndDateBeforeStartDateException extends Exception {
    public EndDateBeforeStartDateException(String message) {
        super(message);
    }
}