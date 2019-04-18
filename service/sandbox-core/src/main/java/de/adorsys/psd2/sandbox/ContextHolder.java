package de.adorsys.psd2.sandbox;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ContextHolder implements ApplicationContextAware {

  public static ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    if (applicationContext.getParent() == null) {
      context = applicationContext;
    }

  }
}
