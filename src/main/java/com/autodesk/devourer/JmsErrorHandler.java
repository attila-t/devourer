package com.autodesk.devourer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class JmsErrorHandler implements ErrorHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(JmsErrorHandler.class);

  @Override
  public void handleError(Throwable t) {
    LOGGER.error("Unhandled exception caught", t);
  }

}
