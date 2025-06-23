plugins {
  id("java")
}

repositories {
  mavenCentral()
  maven {
    name = "papermc"
    url = uri("https://repo.papermc.io/repository/maven-public/")
  }
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks{
  jar {
    archiveBaseName.set("SurvivalFlight")
    archiveVersion.set("")
    manifest {
      attributes(
        "Main-Class" to "io.moranyue.survivalflight.SurvivalFlight",
        "Implementation-Version" to archiveVersion.getOrElse("1.0.0")
      )
    }
  }
}