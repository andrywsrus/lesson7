package lesson7;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class TestExecutor {
    public static void start(Class testClass) {
        List<Method> listTests = new ArrayList<>();
        Method mBefore = null;
        Method mAfter = null;

        try {
            Object o = testClass.getDeclaredConstructor().newInstance();

            for (Method method: testClass.getMethods()
            ) {
                Annotation annoTest = method.getAnnotation(TestOne.class);
                if (annoTest != null) {
                    listTests.add(method);
                    continue;
                }

                Annotation annoBeforeSuite = method.getAnnotation(BeforeSuite.class);
                if (annoBeforeSuite != null) {
                    if (mBefore != null) {
                        throw new RuntimeException("@BeforeSuite is more than in one place...");
                    }

                    mBefore = method;
                    continue;
                }

                Annotation annoAfterSuite = method.getAnnotation(AfterSuite.class);
                if (annoAfterSuite != null) {
                    if (mAfter != null) {
                        throw new RuntimeException("@AfterSuite is more than in one place...");
                    }
                    mAfter = method;
                }
            }

            listTests.sort(new Comparator<>() {
                @Override
                public int compare(Method o1, Method o2) {
                    return o2.getAnnotation(TestOne.class).priority() - o1.getAnnotation(TestOne.class).priority();
                }
            });

            if (mBefore != null) {
                System.out.println("@BeforeSuite");
                mBefore.invoke(o);
            }

            for (Method m: listTests
            ) {
                System.out.println("@Test");
                m.invoke(o);
            }

            if (mAfter != null) {
                System.out.println("@AfterSuite");
                mAfter.invoke(o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}