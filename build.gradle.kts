plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.badgod"
version = "0.0.6"

repositories {
    mavenCentral()
}

dependencies {
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("org.hamcrest:hamcrest-library:3.0")
    testImplementation("org.testcontainers:testcontainers:1.21.3")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.slf4j:slf4j-simple:2.0.17")
    testImplementation("com.google.code.gson:gson:2.13.1")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging.events("failed")
    testLogging.showExceptions = true
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name = "JayReq"
        description = "A batteries included Java HTTP client"
        inceptionYear = "2024"
        url = "https://github.com/robpelger/jayreq"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "robpelger"
                name = "Robert Pelger"
                url = "https://github.com/robpelger/"
            }
        }
        scm {
            url = "https://github.com/robpelger/jayreq.git"
            connection = "scm:git:git://github.com/robpelger/jayreq.git"
            developerConnection = "scm:git:ssh://git@github.com/robpelger/jayreq.git"
        }
    }
}
