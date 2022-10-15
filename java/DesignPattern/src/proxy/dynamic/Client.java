package proxy.dynamic;

public class Client {
    public static void main(String[] args) {
        TeacherDao teacherDao = new TeacherDao();
        ProxyFactory proxyFactory = new ProxyFactory(teacherDao);
        ITeacherDao proxyInstance = (ITeacherDao)proxyFactory.getProxyInstance();
        //proxyInstance.teach();
        proxyInstance.sayHello("paul");
    }
}
