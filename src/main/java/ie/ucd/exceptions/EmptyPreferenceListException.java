package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class EmptyPreferenceListException extends RuntimeException {
  public EmptyPreferenceListException() {
    super();
  }

  public EmptyPreferenceListException(String message) {
    super(message);
  }

  public EmptyPreferenceListException(String message, Throwable cause) {
    super(message, cause);
  }

  public EmptyPreferenceListException(Throwable cause) {
    super(cause);
  }
}