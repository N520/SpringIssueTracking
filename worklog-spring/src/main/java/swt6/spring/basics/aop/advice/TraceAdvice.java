package swt6.spring.basics.aop.advice;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class TraceAdvice {

	// Define pointCut in marker method
	//reuseable
	@Pointcut("execution(public * swt6.spring.basics.aop.logic..*find*(..))")
	private void findMethods() {
	}

//	@Before("execution(public * swt6.spring.basics.aop.logic..*find*(..))")
	@Before("swt6.spring.basics.aop.advice.TraceAdvice.findMethods()")
	public void traceBefore(JoinPoint jp) {
		String methodName = jp.getTarget().getClass().getName() + ". " + jp.getSignature().getName();

		System.out.println("---> " + methodName);
	}

	public void traceAfter(JoinPoint jp) {
		String methodName = jp.getTarget().getClass().getName() + ". " + jp.getSignature().getName();

		System.out.println("<--- " + methodName);
	}

	public Object traceAround(ProceedingJoinPoint pjp) throws Throwable {
		String methodName = pjp.getTarget().getClass().getName() + "." + pjp.getSignature().getName();
		System.out.println("==> " + methodName);
		Object retVal = pjp.proceed(); // delegates to method of target class.
		System.out.println("<== " + methodName);
		return retVal;
	}

	@AfterThrowing(pointcut="swt6.spring.basics.aop.advice.TraceAdvice.findMethods()", throwing = "ex")
	public void traceException(JoinPoint jp, Throwable ex) {
		String methodName = jp.getTarget().getClass().getName() + ". " + jp.getSignature().getName();
		System.out.printf("##> %s%n throw Exception <%s%n>", methodName, ex);
	}
}
