package proxy.dynamicCglib;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import proxy.dynamic.TeacherDao;

import java.lang.reflect.Method;

public class ProxyFactory implements MethodInterceptor{
    private Object target;


    public ProxyFactory(Object target) {
        this.target = target;
    }
    public Object getProxyInstance() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy)
            throws Throwable {
        System.out.println("Cglib代理开始，功能开始增强");
        Object invoke = method.invoke(target, args);
        System.out.println("Cglic代理结束");
        return invoke;
    }
}
