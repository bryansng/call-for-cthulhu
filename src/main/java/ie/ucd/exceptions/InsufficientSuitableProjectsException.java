package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class InsufficientSuitableProjectsException extends RuntimeException {
  public InsufficientSuitableProjectsException() {
    super();
  }

  public InsufficientSuitableProjectsException(String message) {
    super(message);
  }

  public InsufficientSuitableProjectsException(String message, Throwable cause) {
    super(message, cause);
  }

  public InsufficientSuitableProjectsException(Throwable cause) {
    super(cause);
  }
}