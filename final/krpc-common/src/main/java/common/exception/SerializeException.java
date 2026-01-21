package common.exception;

/**
 * @ClassName SerializeException
 * @Description ToDo
 * 
 * 
 * @Version 1.0.0
 */
public class SerializeException extends RuntimeException{
    public SerializeException(String message) {
        super(message);
    }
    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
