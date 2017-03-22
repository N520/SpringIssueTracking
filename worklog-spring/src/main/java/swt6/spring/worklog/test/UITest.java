package swt6.spring.worklog.test;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import swt6.spring.worklog.domain.Employee;
import swt6.spring.worklog.domain.LogbookEntry;
import swt6.spring.worklog.ui.UIProcessFacade;
import swt6.util.DateUtil;

public class UITest {

  private static void uiTest(String configFile) {

    // create domain objects

    Employee empl1 = new Employee("Sepp", "Forcher", DateUtil.getDate(1935, 12, 12));
    Employee empl2 = new Employee("Alfred", "Kunz", DateUtil.getDate(1944, 8, 10));
    Employee empl3 = new Employee("Sigfried", "Hinz", DateUtil.getDate(1954, 5, 3));

    LogbookEntry entry1 =
        new LogbookEntry("Analyse", DateUtil.getTime(10, 0), DateUtil.getTime(13, 45));
    LogbookEntry entry2 =
        new LogbookEntry("Implementierung", DateUtil.getTime(10, 15), DateUtil.getTime(14, 30));
    LogbookEntry entry3 =
        new LogbookEntry("Testen", DateUtil.getTime(10, 15), DateUtil.getTime(14, 30));

    try (AbstractApplicationContext appCtx =
        new ClassPathXmlApplicationContext(configFile)) {
      UIProcessFacade uiComp = appCtx.getBean("uiProcessComponent", UIProcessFacade.class);

      // add employees

      System.out.println("----------------- saveEmployees ----------------- ");
      uiComp.saveEmployees(empl1, empl2, empl3);

      // add LogbookEntries
      empl1.addLogbookEntry(entry1);
      empl1.addLogbookEntry(entry2);
      empl2.addLogbookEntry(entry3);

      System.out.println("----------------- saveEmployees ----------------- ");
      uiComp.saveEmployees(empl1, empl2);

      System.out.println("----------------- findById ----------------- ");
      uiComp.findById(1L);

      System.out.println("----------------- findAll ----------------- ");
      uiComp.findAll();
    }
  }

  public static void main(String[] args) {
    System.out.println("=========================================================");
    System.out.println("====================== UITest (JPA) =====================");
    System.out.println("=========================================================");
    uiTest("swt6/spring/worklog/applicationContext-jpa1.xml");
  }
}
