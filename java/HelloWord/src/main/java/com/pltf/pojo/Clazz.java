package com.pltf.pojo;

import java.util.List;

public class Clazz {
    public int cid;
    public String cinfo;
    public List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Clazz() {
    }

    public Clazz(int cid, String cinfo) {
        this.cid = cid;
        this.cinfo = cinfo;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCinfo() {
        return cinfo;
    }

    public void setCinfo(String cinfo) {
        this.cinfo = cinfo;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                "cid=" + cid +
                ", cinfo='" + cinfo + '\'' +
                ", students=" + students +
                '}';
    }
}
