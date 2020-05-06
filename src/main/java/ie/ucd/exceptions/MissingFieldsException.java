package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class MissingFieldsException extends RuntimeException {
  public MissingFieldsException() {
    super();
  }

  public MissingFieldsException(String message) {
    super(message);
  }

  public MissingFieldsException(String message, Throwable cause) {
    super(message, cause);
  }

  public MissingFieldsException(Throwable cause) {
    super(cause);
  }
}