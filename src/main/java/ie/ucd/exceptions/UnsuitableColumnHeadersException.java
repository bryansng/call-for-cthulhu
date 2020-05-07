package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class UnsuitableColumnHeadersException extends RuntimeException {
  public UnsuitableColumnHeadersException() {
    super();
  }

  public UnsuitableColumnHeadersException(String message) {
    super(message);
  }

  public UnsuitableColumnHeadersException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnsuitableColumnHeadersException(Throwable cause) {
    super(cause);
  }
}