package ch.ahoegger.photobox.db.util;

/**
 * <h3>{@link DbAccessException}</h3>
 *
 * @author aho
 */
public class DbAccessException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * @param message
   * @param cause
   */
  public DbAccessException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message
   */
  public DbAccessException(String message) {
    super(message);
  }

  /**
   * @param cause
   */
  public DbAccessException(Throwable cause) {
    super(cause);
  }

}
