package swt6.spring.basics.ioc.test;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import swt6.spring.basics.ioc.logic.WorkLogFacade;
import swt6.spring.basics.ioc.logic.WorkLogImplFactoryBased;

public class IoCTest {

	public static void main(String[] args) {
		System.out.println("=========== testSimple ===========");
		testSimple();

		System.out.println("=========== testXmlConfig ===========");
		 testXmlConfig();

		System.out.println("=========== testAnnotationConfig ===========");
		testAnotationConfig();

	}
	//
	// private static void testXmlConfig() {
	// try (AbstractApplicationContext factory = new
	// ClassPathXmlApplicationContext(
	// "swt6/spring/basics/ioc/test/applicationContext-xml-config.xml")) {
	// System.out.println("###> worklog setter s");
	// WorkLogFacade workLog1 = factory.getBean("workLog-setter-injected",
	// WorkLogFacade.class);
	// WorkLogFacade workLog2 = factory.getBean("workLog-constructor-injected",
	// WorkLogFacade.class);
	// }
	// }

	private static void testAnotationConfig() {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/basics/ioc/test/applicationContext-annotation-config.xml")) {
			WorkLogFacade workLog1 = factory.getBean("workLog", WorkLogFacade.class);
			
			System.out.println("###> WorkLog-annotation");

			workLog1.findAllEmployees();
			workLog1.findEmployeeById(3L);

		} catch (Exception e) {

		}
	}

	private static void testXmlConfig() {
		try (AbstractApplicationContext factory = new ClassPathXmlApplicationContext(
				"swt6/spring/basics/ioc/test/applicationContext-test-config.xml")) {
			WorkLogFacade workLog1 = factory.getBean("workLog-setter-injected", WorkLogFacade.class);
			WorkLogFacade workLog2 = factory.getBean("workLog-constructor-injected", WorkLogFacade.class);
			System.out.println("###> WorkLog-setter-injected");
			// WorkLogFacade workLog1 =
			// factory.getBean("workLog-setter-injected", WorkLogFacade.class);
			workLog1.findAllEmployees();
			workLog1.findEmployeeById(3L);

			System.out.println("###> WorkLog-constructor-injected");
			// WorkLogFacade workLog2 =
			// factory.getBean("workLog-consturctor-injected",
			// WorkLogFacade.class);
			workLog2.findAllEmployees();
			workLog2.findEmployeeById(3L);
		} catch (Exception e) {

		}
	}

	private static void testSimple() {
		WorkLogImplFactoryBased workLog = new WorkLogImplFactoryBased();
		workLog.findAllEmployees();
		workLog.findEmployeeById(3L);
	}

}
