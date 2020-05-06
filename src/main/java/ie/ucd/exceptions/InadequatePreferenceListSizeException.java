package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class InadequatePreferenceListSizeException extends RuntimeException {
  public InadequatePreferenceListSizeException() {
    super();
  }

  public InadequatePreferenceListSizeException(String message) {
    super(message);
  }

  public InadequatePreferenceListSizeException(String message, Throwable cause) {
    super(message, cause);
  }

  public InadequatePreferenceListSizeException(Throwable cause) {
    super(cause);
  }
}