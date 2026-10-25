plugins {
	java
	id("org.springframework.boot") version "4.1.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "8.10.4"
}

group = "com.dev"
version = "1.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:3.1.1")
	runtimeOnly("org.xerial:sqlite-jdbc")
	testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

spotless {
	java {
		removeUnusedImports()
		eclipse().configProperties(
			"""
			org.eclipse.jdt.core.formatter.tabulation.char=tab
			org.eclipse.jdt.core.formatter.tabulation.size=4
			org.eclipse.jdt.core.formatter.indentation.size=4
			org.eclipse.jdt.core.formatter.continuation_indentation=1
			""".trimIndent(),
		)
		trimTrailingWhitespace()
		endWithNewline()
	}
}
