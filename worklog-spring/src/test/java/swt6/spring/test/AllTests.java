package swt6.spring.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import swt6.spring.test.employee.IssueTrackingDalEmployeeTest;
import swt6.spring.test.entry.IssueTrackingDalEntryTest;
import swt6.spring.test.issue.IssueTrackingDalIssueTest;
import swt6.spring.test.project.IssueTrackingDalProjectTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({ IssueTrackingDalEmployeeTest.class, IssueTrackingDalEntryTest.class,
		IssueTrackingDalProjectTest.class, IssueTrackingDalIssueTest.class, })

public class AllTests {

}
