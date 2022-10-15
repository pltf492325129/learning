package com.pltf.test;

import com.pltf.pojo.Clazz;
import com.pltf.pojo.Person;
import com.pltf.pojo.Student;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.sql.SQLException;


public class TestHelloWord {
    @Test
    public void test1() {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("ApplicationContext.xml");
//        Person student = (Person) ioc.getBean(Person.class);
//        System.out.println(student);

//        Person student = (Person) ioc.getBean("studentTwo");
//        System.out.println(student);

        Person student = (Person) ioc.getBean("studentFour", Student.class);
        System.out.println(student);
    }
    @Test
    public void test2() {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("ApplicationContext.xml");
//        Clazz classOne = (Clazz) ioc.getBean("clazzOne");
//        System.out.println(classOne);
        Clazz classOne = (Clazz) ioc.getBean("clazzThree");
        System.out.println(classOne);

    }
    @Test
    public void test3() throws SQLException {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("spring-datasource.xml");
        DataSource dataSource = ioc.getBean(DataSource.class);
        System.out.println(dataSource.getConnection());
    }
}
