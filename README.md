# MeetMeHalfwayRest

 1) mvn package -Dmaven.test.skip=true
 2) java -jar target/gs-rest-service-0.1.0.jar
 
 3)java.lang.ClassNotFoundException: org.json.simple.parser.ParseException
	at java.net.URLClassLoader.findClass(URLClassLoader.java:381) ~[na:1.8.0_91]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424) ~[na:1.8.0_91]
	at org.springframework.boot.loader.LaunchedURLClassLoader.doLoadClass(LaunchedURLClassLoader.java:178) ~[gs-rest-service-0.1.0.jar!/:0.1.0]
	at org.springframework.boot.loader.LaunchedURLClassLoader.loadClass(LaunchedURLClassLoader.java:142) ~[gs-rest-service-0.1.0.jar!/:0.1.0]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:357) ~[na:1.8.0_91]
	at hello.MeetMeHalfway.<init>(MeetMeHalfway.java:109) ~[gs-rest-service-0.1.0.jar!/:0.1.0]
	at hello.MeetMeHalfwayController.greeting(MeetMeHalfwayController.java:19) ~[gs-rest-service-0.1.0.jar!/:0.1.0]
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[na:1.8.0_91]
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[na:1.8.0_91]
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[na:1.8.0_91]
	at java.lang.reflect.Method.invoke(Method.java:498) ~[na:1.8.0_91]
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:221) ~[spring-web-4.2.6.RELEASE.jar!/:4.2.6.RELEASE]
