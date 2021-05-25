package gov.va.api.lighthouse.facilities.v0;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiExceptionsV0 {
  public static final class BingException extends RuntimeException {
    public BingException(String msg) {
      super("Bing error: " + msg);
    }

    public BingException(Throwable cause) {
      super("Bing error: " + cause.getMessage(), cause);
    }
  }
}
