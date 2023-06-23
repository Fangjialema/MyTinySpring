import com.fangjialema.bean.AnnotationApplicationContext;
import com.fangjialema.test.TestClassA;
import com.fangjialema.test.TestClassB;
import com.fangjialema.test.TestClassC;

public class TestMain {
    public static void main(String[] args) throws Exception {
        AnnotationApplicationContext context=new AnnotationApplicationContext(TinySpringConfig.class);
        TestClassA testClassA = (TestClassA) context.getBean("TestClassA");
        TestClassB testClassB = (TestClassB) context.getBean("TestClassB");
        TestClassC testClassC = (TestClassC) context.getBean("TestClassC");
        testClassC.testMethod();
    }
}
