<!-- Logback configuration. See http://logback.qos.ch/manual/index.html -->
<!-- Scanning is currently turned on; This will impact performance! -->
<configuration scan="false" scanPeriod="120 seconds">
  <!-- Silence Logback's own status messages about config parsing
  <statusListener class="ch.qos.logback.core.status.NopStatusListener" /> -->

  <!-- Simple file output -->
  <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <!-- encoder defaults to ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
    <encoder>
      <!--
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} %X{io.pedestal} - %msg%n</pattern>
      -->
      <pattern>%d{HH:mm:ss} %-5level %logger{36} %X{io.pedestal} - %msg%n</pattern>
    </encoder>

    <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
      <!-- rollover daily -->
      <fileNamePattern>/app/logs/nordnetservice-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
      <!-- or whenever the file size reaches 64 MB -->
      <maxFileSize>3 MB</maxFileSize>
    </rollingPolicy>

    <!-- Safely log to the same file from multiple JVMs. Degrades performance! -->
    <prudent>true</prudent>
  </appender>


  <!-- Console output -->
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- encoder defaults to ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
    <encoder>
      <pattern>%d{HH:mm:ss} %-5level %logger{36} %X{io.pedestal} - %msg%n</pattern>
    </encoder>
    <!-- Only log level INFO and above -->
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
      <level>INFO</level>
    </filter>
  </appender>


  <!-- Enable FILE and STDOUT appenders for all log messages.
       By default, only log at level INFO and above. -->
  <root level="INFO">
    <appender-ref ref="FILE" />
    <appender-ref ref="STDOUT" />
  </root>

  <!-- For loggers in the these namespaces, log at all levels. -->
  <logger name="user" level="ALL" />
  <!-- To log pedestal internals, enable this and change ThresholdFilter to DEBUG
    <logger name="io.pedestal" level="ALL" />
  -->

</configuration>