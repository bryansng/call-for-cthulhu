package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class ProjectsEmptyException extends RuntimeException {
  public ProjectsEmptyException() {
    super();
  }

  public ProjectsEmptyException(String message) {
    super(message);
  }

  public ProjectsEmptyException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProjectsEmptyException(Throwable cause) {
    super(cause);
  }
}