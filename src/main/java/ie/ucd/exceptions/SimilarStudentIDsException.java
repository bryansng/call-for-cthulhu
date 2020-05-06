package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class SimilarStudentIDsException extends RuntimeException {
  public SimilarStudentIDsException() {
    super();
  }

  public SimilarStudentIDsException(String message) {
    super(message);
  }

  public SimilarStudentIDsException(String message, Throwable cause) {
    super(message, cause);
  }

  public SimilarStudentIDsException(Throwable cause) {
    super(cause);
  }
}