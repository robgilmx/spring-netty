package lighthouse.assignment.config.exception;

import java.io.Serializable;
import java.util.NoSuchElementException;

public class ChatException extends NoSuchElementException implements Serializable {

    public ChatException(String s) {
        super(s);
    }
}
