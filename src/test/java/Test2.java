import com.fangjialema.test.TestClassC;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.Arrays;

public class Test2 {
    public static void main(String[] aargs) {
        Enhancer enhancer = new Enhancer();
        Class<?> clazz=TestClassC.class;
        enhancer.setSuperclass(TestClassC.class);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            if (args.length==0)
                System.out.format("调用前，调用对象为:%s，调用的方法名：%s，方法没有入参\n",clazz.getSimpleName(),method.getName());
            else
                System.out.format("调用前，调用对象为:%s，调用的方法名：%s，方法的入参是：%s\n",clazz.getSimpleName(), method.getName(), Arrays.toString(args));
            Object res = proxy.invokeSuper(obj, args);
            if (res==null)
                System.out.format("调用后，调用对象为:%s，调用的方法名：%s，返回结果是：void\n",clazz.getSimpleName(), method.getName());
            else
                System.out.format("调用后，调用对象为:%s，调用的方法名：%s，结果是：%s\n",clazz.getSimpleName(), method.getName(), res.toString());
            return res;
        });
        TestClassC o = (TestClassC)enhancer.create();
        o.toString();
    }
}
