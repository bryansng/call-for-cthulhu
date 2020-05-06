package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class StudentsNullException extends RuntimeException {
  public StudentsNullException() {
    super();
  }

  public StudentsNullException(String message) {
    super(message);
  }

  public StudentsNullException(String message, Throwable cause) {
    super(message, cause);
  }

  public StudentsNullException(Throwable cause) {
    super(cause);
  }
}