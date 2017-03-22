package swt6.spring.basics.aop.test;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.basics.aop.logic.EmployeeIdNotFoundException;
import swt6.spring.basics.aop.logic.WorkLogFacade;

public class AopTest {

	public static void reflectClass(Class<?> clazz) {
		System.out.println("class=" + clazz.getName());
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> itf : interfaces)
			System.out.println("  implements " + itf.getName());

		// for (Method m : clazz.getMethods())
		// System.out.println(" " + m.getName() + "()");
	}

	private static void testAOP(String configFileName) throws EmployeeIdNotFoundException {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(configFileName)) {
			System.out.println("------------- traceAdvice -----------");
			WorkLogFacade workLog = factory.getBean("workLog", WorkLogFacade.class);
			workLog.findAllEmployees();
			workLog.findEmployeeById(3L);

			for (long i = 0; i < 9; i++) {
				workLog.findEmployeeById(i);
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("=============== testAOP (config based) ===============");
		try {
			testAOP("swt6/spring/basics/aop/applicationContext-xml-config.xml");
		} catch (EmployeeIdNotFoundException e) {
			
		}

		 System.out.println("============= testAOP (annotation based) =============");
		 try {
			testAOP("swt6/spring/basics/aop/applicationContext-annotation-config.xml");
		} catch (EmployeeIdNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
}
