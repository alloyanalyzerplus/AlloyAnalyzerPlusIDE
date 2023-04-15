package parser.etc;

import static parser.etc.Context.logger;

public class Timer {
  private long timestamp;

  public void record() {
    timestamp = System.nanoTime();
  }

  public void show(String header) {
    logger.info(header + ": " + (System.nanoTime() - timestamp));
  }

  public void showAndRecord(String header) {
    show(header);
    record();
  }
}
