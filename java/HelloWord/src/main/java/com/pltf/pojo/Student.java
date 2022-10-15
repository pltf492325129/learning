package com.pltf.pojo;

import org.springframework.context.annotation.Bean;

import java.util.Arrays;

public class Student implements Person{

    public String name;
    public int age;
    public int id;
    public int higth;
    public Clazz clazz;
    private String[] hobby;

    public void setHobby(String[] hobby) {
        this.hobby = hobby;
    }

    public String[] getHobby() {
        return hobby;
    }

    public Student() {
    }

    public Student(String name, int age, int id, int higth, Clazz clazz) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.higth = higth;
        this.clazz = clazz;
    }

    public Student(String name, int age, int id, int higth) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.higth = higth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHigth() {
        return higth;
    }

    public void setHigth(int higth) {
        this.higth = higth;
    }

    public Clazz getClazz() {
        return clazz;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }

    public Student(String name, int age, int id, int higth, Clazz clazz, String[] hobby) {
        this.name = name;
        this.age = age;
        this.id = id;
        this.higth = higth;
        this.clazz = clazz;
        this.hobby = hobby;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", id=" + id +
                ", higth=" + higth +
                ", clazz=" + clazz +
                ", hobby=" + Arrays.toString(hobby) +
                '}';
    }
}
