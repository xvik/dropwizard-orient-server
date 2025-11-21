# {{ gradle.version }} Release Notes

Release brings dropwizard 5 compatibility (due to dropwizard package change and move to jakarta api).

!!! note
    It was impossible to preserve dropwizard 3 or 4 because of:

      - validation api usage (javax in 3.x, jakarta in 5.x)
      - jetty api usage was required in 4.x, but base jetty package changed in dropwizard 5

Java 8-16 support was dropped because dropwizard 5 requires java 17.

Updated to orient 3.2: note that enterprise agent become open source and so could be used directly
(not as dependency `com.orientechnologies:agent:3.2.46` because it has embedded slf4j 1.7, which will fail
dropwizard startup)