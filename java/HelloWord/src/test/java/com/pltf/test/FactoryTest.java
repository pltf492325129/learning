package com.pltf.test;

import com.pltf.pojo.Student;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FactoryTest {
    @Test
    public void factoryBeanTest() {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("spring-factory.xml");
        Student bean = ioc.getBean(Student.class);
        System.out.println(bean);
    }
}
