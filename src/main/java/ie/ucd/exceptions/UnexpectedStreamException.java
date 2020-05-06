package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class UnexpectedStreamException extends RuntimeException {
  public UnexpectedStreamException() {
    super();
  }

  public UnexpectedStreamException(String message) {
    super(message);
  }

  public UnexpectedStreamException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnexpectedStreamException(Throwable cause) {
    super(cause);
  }
}