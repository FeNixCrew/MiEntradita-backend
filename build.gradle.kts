import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.4"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	war
	kotlin("jvm") version "1.4.31"
	kotlin("plugin.spring") version "1.4.31"
	kotlin("plugin.jpa") version "1.4.31"
	jacoco
}

group = "ar.edu.unq"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("io.jsonwebtoken:jjwt:0.9.1")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("commons-validator:commons-validator:1.4.1")
	implementation("org.springframework.boot:spring-boot-starter-mail:2.2.5.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("com.h2database:h2")
	implementation("com.mercadopago:sdk-java:1.8.0")
	implementation ("org.slf4j:slf4j-api:1.7.30")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
	dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestReport {
	reports {
		xml.isEnabled = true
		csv.isEnabled = false
		html.destination = layout.buildDirectory.dir("jacocoHtml").get().asFile
	}

	classDirectories.setFrom(
		sourceSets.main.get().output.asFileTree.matching {
			include("ar/edu/unq/mientradita/model/**")
			include("ar/edu/unq/mientradita/persistence/**")
			include("ar/edu/unq/mientradita/service/**")
			exclude("ar/edu/unq/mientradita/service/MailSender*")
			exclude("ar/edu/unq/mientradita/service/Reminder*")
			exclude("ar/edu/unq/mientradita/model/builders")
			exclude("ar/edu/unq/mientradita/service/dto/**")
		}
	)
}