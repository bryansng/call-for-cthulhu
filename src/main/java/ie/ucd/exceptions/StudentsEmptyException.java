package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class StudentsEmptyException extends RuntimeException {
  public StudentsEmptyException() {
    super();
  }

  public StudentsEmptyException(String message) {
    super(message);
  }

  public StudentsEmptyException(String message, Throwable cause) {
    super(message, cause);
  }

  public StudentsEmptyException(Throwable cause) {
    super(cause);
  }
}