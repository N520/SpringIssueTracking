package swt6.spring.basics.ioc.util;

import javax.inject.Named;

@Named("consoleLogger")
public class ConsoleLogger implements Logger{

	private String prefix = "log";

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void log(String msg) {
		System.out.printf("%s: %s%n", prefix, msg);
	}
}
