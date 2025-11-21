# {{ gradle.version }} Release Notes

Release brings dropwizard 3-5 compatibility (due to dropwizard package change).

Java 8 support was dropped because dropwizard 3-4 does not support it (5 requires java 17).

Updated to orient 3.2: note that enterprise agent become open source and so could be used directly
( as dependency `com.orientechnologies:agent:3.2.46`)