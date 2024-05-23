package app.pooi.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext APPLICATIONCONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATIONCONTEXT = applicationContext;
    }

    public static Object getBean(String name) throws BeansException {
        return APPLICATIONCONTEXT.getBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return APPLICATIONCONTEXT.getBean(requiredType);
    }
}
