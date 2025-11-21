# {{ gradle.version }} Release Notes

Release brings dropwizard 4-5 compatibility (due to dropwizard package change and move to jakarta api).

Java 8 support was dropped because dropwizard 4 does not support it (5 requires java 17).

Updated to orient 3.2: note that enterprise agent become open source and so could be used directly
(not as dependency `com.orientechnologies:agent:3.2.46` because it has embedded slf4j 1.7, which will fail
dropwizard startup)