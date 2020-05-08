package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class EmptyResearchActivityException extends RuntimeException {
  public EmptyResearchActivityException() {
    super();
  }

  public EmptyResearchActivityException(String message) {
    super(message);
  }

  public EmptyResearchActivityException(String message, Throwable cause) {
    super(message, cause);
  }

  public EmptyResearchActivityException(Throwable cause) {
    super(cause);
  }
}