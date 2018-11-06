package ch.ahoegger.photobox.security;

import java.security.AccessController;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

/**
 * <h3>{@link ServletFilterHelper}</h3>
 *
 * @author aho
 */
public final class ServletFilterHelper {

  /**
   * Returns <code>true</code> if running as a {@link Subject} with a principal corresponding to the authenticated
   * remote user.
   *
   * @see HttpServletRequest#getRemoteUser()
   */
  public static boolean isRunningWithValidSubject(HttpServletRequest req) {
    String username = req.getRemoteUser();
    if (username == null || username.isEmpty()) {
      return false;
    }

    Subject subject = Subject.getSubject(AccessController.getContext());
    if (subject == null || subject.getPrincipals().isEmpty()) {
      return false;
    }

    for (Principal principal : subject.getPrincipals()) {
      if (username.equalsIgnoreCase(principal.getName())) {
        return true;
      }
    }
    return false;
  }
}
