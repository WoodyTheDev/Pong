package woodythedev;

import java.util.Date;

public class ErrorMessage {
  private int statusCode;
  private Date timestamp = new Date();
  private String message;
  private String description;

  public ErrorMessage(int statusCode, String message, String description) {
    this.statusCode = statusCode;
    this.message = message;
    this.description = description;
  }

  public Date setTimestamp(Date timestamp) {
    return this.timestamp = timestamp;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public String getMessage() {
    return message;
  }

  public String getDescription() {
    return description;
  }
}
