package com.pltf.pojo;

import org.springframework.beans.factory.FactoryBean;

public class UserFactoryBean implements FactoryBean {

    @Override
    public Object getObject() throws Exception {
        return new Student();
    }

    @Override
    public Class<?> getObjectType() {
        return Student.class;
    }
}
