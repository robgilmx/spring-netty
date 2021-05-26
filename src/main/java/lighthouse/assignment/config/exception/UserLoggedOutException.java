package lighthouse.assignment.config.exception;

public class UserLoggedOutException extends RuntimeException {

    public UserLoggedOutException() {
        super("The current user is not logged in to a channel.");
    }
}
