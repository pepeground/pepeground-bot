FROM openjdk:8-jdk

ADD bot/target/scala-2.12/bot-assembly-0.1.jar /bot.jar

CMD ["/usr/bin/java", "-jar", "-server", "/bot.jar"]
