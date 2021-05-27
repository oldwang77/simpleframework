package org.simpleframework.inject;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * 提供依赖注入的功能
 */
@Slf4j
public class DependencyInjector {
    /**
     * bean容器
     */
    private BeanContainer beanContainer;
    public DependencyInjector(){
        // 获取容器的单例
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * 执行IOC
     */
    public void doIoc(){
        if(ValidationUtil.isEmpty(beanContainer.getClasses())){
            log.warn("empty classSet in BeanContainer");
            return ;
        }
        //1 遍历bean容器中的所有class对象
        for(Class<?> clazz : beanContainer.getClasses()){
            // 2 遍历该对象的所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            if(ValidationUtil.isEmpty(fields)){
                continue;
            }
            // 3 遍历成员变量，寻找是否有autowire注解
            for(Field field:fields){
                // 实现了被@Autowire注解标记后的逻辑
                if(field.isAnnotationPresent(Autowired.class)){
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();

                    // 4 获取这些成员变量的类型
                    Class<?> fieldClass = field.getType();
                    // 5 根据成员变量的类型在容器中获取对应的实例
                    // filedClass => com.imooc.service.combine.HeadLineShopCategoryCombineService
                    Object fieldValue = getFieldInstance(fieldClass,autowiredValue);
                    if(fieldValue==null){
                        throw new RuntimeException("unable to inject relevant type，target fieldClass is:"
                                + fieldClass.getName() + " autowiredValue is : " + autowiredValue);
                    }else{
                        // 通过反射将对应的成员变量实例注入到成员变量所在类的实例中
                        Object targetBean = beanContainer.getBean(clazz);
                        ClassUtil.setField(field,targetBean,fieldValue,true);
                    }
                }
            }
        }
    }

    /**
     * 根据Class在beanContainer里获取其实例或者实现类
     * @param fieldClass
     * @param autowiredValue
     * @return
     */
    private Object getFieldInstance(Class<?> fieldClass,String autowiredValue){
        Object fieldValue = beanContainer.getBean(fieldClass);
        if(fieldValue!=null){
            return fieldValue;
        }else{
            // 上面为空，还可能fileClass是接口
            Class<?> implementedClass = getImplementedClass(fieldClass,autowiredValue);
            if(implementedClass!=null){
                return beanContainer.getBean(implementedClass);
            }else{
                return null;
            }
        }
    }

    /**
     * 获取接口的实现类
     */
    public Class<?> getImplementedClass(Class<?> fieldClass,String autowiredValue){
        Set<Class<?>> classSet = beanContainer.getClassBySuper(fieldClass);
        if(!ValidationUtil.isEmpty(classSet)){
            // 如果@value没有指定value的名称
            if(ValidationUtil.isEmpty(autowiredValue)){
                // @Autowired注入是基于类型注入的！
                if(classSet.size()==1){
                    return classSet.iterator().next();
                }else{
                    //如果多于两个实现类且用户未指定其中一个实现类，则抛出异常
                    throw new RuntimeException("multiple implemented classes for " +
                            fieldClass.getName() + " please set @Autowired's value to pick one");
                }
            } else{
                // 如果@Autowire指定了注入的value名称
                for(Class<?> clazz:classSet){
                    // 我们设置的@Autowired注解的value和clazz的类名相同，那么就返回
                    if(autowiredValue.equals(clazz.getSimpleName())){
                        return clazz;
                    }
                }
            }
        }
        return null;
    }
}
