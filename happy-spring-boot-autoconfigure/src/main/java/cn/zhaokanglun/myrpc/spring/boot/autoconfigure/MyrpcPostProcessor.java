package cn.zhaokanglun.myrpc.spring.boot.autoconfigure;

import cn.zhaokanglun.myrpc.bootstrap.ReferenceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyrpcPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MyrpcPostProcessor.class);
    private final Map<String, Object> referenceCache = new HashMap<>();
    private ReferenceFactory referenceFactory;

    @Autowired
    public void setReference(ReferenceFactory referenceFactory) {
        this.referenceFactory = referenceFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, @Nonnull String beanName) throws BeansException {
        Class<?> clz = bean.getClass();
        Field[] fields = clz.getDeclaredFields();
        logger.debug("bean name: " + beanName + " fields: " + Arrays.toString(fields));
        for (Field field : fields) {
            if (field.getAnnotation(MyrpcReference.class) == null) {
                continue;
            }
            logger.info("myrpc reference created for bean name: " + beanName + " field: " + Arrays.toString(fields));
            Object serviceReference = referenceCache.get(field.getType().getSimpleName());
            if (serviceReference == null) {
                serviceReference = referenceFactory.getReference(field.getType());
                referenceCache.put(field.getType().getSimpleName(), serviceReference);
            }
            ReflectionUtils.makeAccessible(field);
            try {
                field.set(bean, serviceReference);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }
}
