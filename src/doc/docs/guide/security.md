# Security features

Orient provides advances [security features](https://orientdb.dev/docs/3.2.x/security/Security-OrientDB-New-Security-Features.html)

Normally, security features could be configured with [external json file](https://orientdb.dev/docs/3.2.x/security/Security-Config.html),
but you can also configure it directly in [yaml configuration](configuration.md) 

### LDAP user import

!!! note
    OLDAPImporter by default is released only with Enterprise Edition, but technically it’s part of the security module, 
    that is open-source, so you can add it to your CE distribution.
    [source](https://community.orientdb.org/t/oldapimporter-class-not-found/904)
    
To enable LDAP users import attach `com.orientechnologies:orientdb-security:3.2.46`
and configure [ldap importer](https://orientdb.dev/docs/3.2.x/security/Security-Config.html#ldapimporter)
in the security config.

### Other features

I did not try it, but probably it would be possible to activate other features like 
[auditing/syslog](https://orientdb.dev/docs/3.2.x/security/Security-OrientDB-New-Security-Features.html#auditingsyslog),
[karberos](https://orientdb.dev/docs/3.2.x/security/Security-OrientDB-New-Security-Features.html#okerberosauthenticator),
[password validator](https://orientdb.dev/docs/3.2.x/security/Security-OrientDB-New-Security-Features.html#password-validator)   