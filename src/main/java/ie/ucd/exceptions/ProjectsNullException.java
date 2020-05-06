package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class ProjectsNullException extends RuntimeException {
  public ProjectsNullException() {
    super();
  }

  public ProjectsNullException(String message) {
    super(message);
  }

  public ProjectsNullException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProjectsNullException(Throwable cause) {
    super(cause);
  }
}