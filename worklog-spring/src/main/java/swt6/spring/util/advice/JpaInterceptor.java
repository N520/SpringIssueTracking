package swt6.spring.util.advice;

import javax.persistence.EntityManagerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.support.TransactionSynchronizationManager;
import swt6.util.JpaUtil;

@Aspect
public class JpaInterceptor {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private EntityManagerFactory entityManagerFactory;

	protected EntityManagerFactory getEntityManagerFactory() {
		return this.entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory emFactory) {
		this.entityManagerFactory = emFactory;
	}

	@Around("execution(* swt6.spring.client.InteractiveClient.*(..))")
	public Object holdEntityManger(ProceedingJoinPoint pjp) throws Throwable {
		System.err.println("ENTERING -dasödlasödk");
		if (entityManagerFactory == null)
			throw new IllegalArgumentException("Property 'entityManagerFactory' is required");

		boolean participate = false;
		if (TransactionSynchronizationManager.hasResource(entityManagerFactory))
			participate = true;
		else {
			logger.trace("Opening EntityManager");
			System.err.println("manager is open");
			JpaUtil.openEntityManager(entityManagerFactory);
		}

		try {
			System.err.println("proceeding");
			return pjp.proceed(); // delegates to method of target class.
		} finally {
			if (!participate) {
				JpaUtil.closeEntityManager(entityManagerFactory);
				logger.trace("Closed EntityManager");
				System.err.println("manager is closed");
			}
		}
	}

}