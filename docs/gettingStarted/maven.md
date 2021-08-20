# Maven

## Repository

```xml
<repository>
  <snapshots>
    <enabled>false</enabled>
  </snapshots>
  <id>github-bebauer</id>
  <name>GitHub bebauer Apache Maven Packages</name>
  <url>https://maven.pkg.github.com/bebauer/webflux-handler-dsl</url>
</repository>
```

Configure maven settings for access:
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>github-bebauer</id>
      <username>github username</username>
      <password>github personal access token with read:packages scope</password>
    </server>
  </servers>
</settings>
```

## Dependency

```xml
<dependency>
  <groupId>de.bebauer</groupId>
  <artifactId>webflux-handler-dsl</artifactId>
  <version>1.2.1</version>
</dependency>
```