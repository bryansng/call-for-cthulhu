package ie.ucd.exceptions;

@SuppressWarnings("serial")
public class UnknownStaffMemberNameException extends RuntimeException {
  public UnknownStaffMemberNameException() {
    super();
  }

  public UnknownStaffMemberNameException(String message) {
    super(message);
  }

  public UnknownStaffMemberNameException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnknownStaffMemberNameException(Throwable cause) {
    super(cause);
  }
}