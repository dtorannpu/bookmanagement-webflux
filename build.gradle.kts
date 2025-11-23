import org.flywaydb.gradle.task.AbstractFlywayTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
    dependencies {
        classpath(libs.org.flywaydb.flyway.database.postgresql)
    }
}

plugins {
    alias(libs.plugins.org.springframework.boot)
    alias(libs.plugins.io.spring.dependency.management)
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.spring)
    alias(libs.plugins.org.jlleitschuh.gradle.ktlint)
    alias(libs.plugins.org.jooq.jooq.codegen.gradle)
    alias(libs.plugins.co.uzzu.dotenv.gradle)
    alias(libs.plugins.org.flywaydb.flyway)
    alias(libs.plugins.org.openapi.generator)
    alias(libs.plugins.jacoco)
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

val javaVersion = System.getenv("JAVA_VERSION") ?: "21"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

kotlin.sourceSets.main {
    kotlin.srcDir(layout.buildDirectory.dir("generated-sources/jooq"))
    kotlin.srcDir(layout.buildDirectory.dir("generated-sources/api/src/main/kotlin"))
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://plugins.gradle.org/m2/")
    }
}

dependencies {
    implementation(libs.org.springframework.boot.spring.boot.starter.jooq)
    implementation(libs.org.jooq.jooq)
    implementation(libs.org.jooq.jooq.kotlin)
    implementation(libs.org.jooq.jooq.kotlin.coroutines)
    implementation(libs.com.fasterxml.jackson.module.jackson.module.kotlin)
    implementation(libs.org.flywaydb.flyway.core)
    implementation(libs.org.flywaydb.flyway.database.postgresql)
    implementation(libs.org.jetbrains.kotlin.kotlin.reflect)
    implementation(libs.org.springframework.boot.spring.boot.starter.validation)
    implementation(libs.org.springdoc.springdoc.openapi.starter.webflux.api)
    implementation(libs.org.springframework.boot.spring.boot.starter.webflux)
    implementation(libs.org.springframework.boot.spring.boot.r2dbc)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.webflux.test)
    testImplementation(libs.org.testcontainers.testcontainers.junit.jupiter)
    testImplementation(libs.org.springframework.boot.spring.boot.testcontainers)
    testImplementation(libs.org.testcontainers.testcontainers.postgresql)
    testImplementation(libs.io.projectreactor.reactor.test)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.jooq.test)
    developmentOnly(libs.org.springframework.boot.spring.boot.docker.compose)
    runtimeOnly(libs.org.postgresql.postgresql)
    runtimeOnly(libs.org.postgresql.r2dbc.postgresql)
    runtimeOnly(libs.io.r2dbc.r2dbc.pool)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    jooqCodegen(libs.org.postgresql.postgresql)
    jooqCodegen(libs.org.jooq.jooq)
    jooqCodegen(libs.org.jooq.jooq.meta)
    jooqCodegen(libs.org.jooq.jooq.codegen)
    jooqCodegen(libs.co.uzzu.dotenv.gradle)
    testImplementation(libs.org.jetbrains.kotlin.kotlin.test)
    testImplementation(libs.org.mockito.kotlin.mockito.kotlin)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
    testImplementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.test)
    testImplementation(libs.io.mockk.mockk)
}

kotlin {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.fromTarget(javaVersion))
    }
}

tasks.test {
    useJUnitPlatform()
    extensions.configure(JacocoTaskExtension::class) {
        excludes = listOf("**/api/*", "**/db/**")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

jooq {
    configuration {

        // Configure the database connection here
        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://localhost:${env.POSTGRES_PORT.value}/${env.POSTGRES_DB.value}"
            user = env.POSTGRES_USER.value
            password = env.POSTGRES_PASSWORD.value
        }
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            strategy {
                name = "org.jooq.codegen.example.JPrefixGeneratorStrategy"
            }
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"

                // All elements that are generated from your schema (A Java regular expression.
                // Use the pipe to separate several expressions) Watch out for
                // case-sensitivity. Depending on your database, this might be
                // important!
                //
                // You can create case-insensitive regular expressions using this syntax: (?i:expr)
                //
                // Whitespace is ignored and comments are possible.
                includes = ".*"

                // All elements that are excluded from your schema (A Java regular expression.
                // Use the pipe to separate several expressions). Excludes match before
                // includes, i.e. excludes have a higher priority
                excludes = """
                flyway_schema_history
                """

                // The schema that is used locally as a source for meta information.
                // This could be your development schema or the production schema, etc
                // This cannot be combined with the schemata element.
                //
                // If left empty, jOOQ will generate all available schemata. See the
                // manual"s next section to learn how to generate several schemata
                inputSchema = "public"
            }

            // Generation flags: See advanced configuration properties
            generate {
                // Tell the KotlinGenerator to generate properties in addition to methods for these paths. Default is true.

                isImplicitJoinPathsAsKotlinProperties = true

                // Workaround for Kotlin generating setX() setters instead of setIsX() in byte code for mutable properties called
                // <code>isX</code>. Default is true.
                isKotlinSetterJvmNameAnnotationsOnIsPrefix = true

                // Generate POJOs as data classes, when using the KotlinGenerator. Default is true.
                isPojosAsKotlinDataClasses = true

                // Generate non-nullable types on POJO attributes, where column is not null. Default is false.
                isKotlinNotNullPojoAttributes = true

                // Generate non-nullable types on Record attributes, where column is not null. Default is false.
                isKotlinNotNullRecordAttributes = true

                // Generate non-nullable types on interface attributes, where column is not null. Default is false.
                isKotlinNotNullInterfaceAttributes = true

                // Generate defaulted nullable POJO attributes. Default is true.
                isKotlinDefaultedNullablePojoAttributes = false

                // Generate defaulted nullable Record attributes. Default is true.
                isKotlinDefaultedNullableRecordAttributes = false

                //                // Generate the DAO classes
                //                daos = true
                //
                //                // Annotate DAOs (and other types) with spring annotations, such as @Repository and @Autowired
                //                // for auto-wiring the Configuration instance, e.g. from Spring Boot's jOOQ starter
                //                springAnnotations = true
                //
                //                // Generate Spring-specific DAOs containing @Transactional annotations
                //                springDao = true
            }
            target {

                // The destination package of your generated classes (within the
                // destination directory)
                //
                // jOOQ may append the schema name to this package if generating multiple schemas,
                // e.g. org.jooq.your.packagename.schema1
                // org.jooq.your.packagename.schema2
                packageName = "com.example.bookmanagement.db.jooq.gen"

                // The destination directory of your generated classes
                // directory = "src/main/kotlin"
            }
        }
    }
}

tasks {
    withType<AbstractFlywayTask> {
        notCompatibleWithConfigurationCache("because https://github.com/flyway/flyway/issues/3550")
    }
}

flyway {
    url = "jdbc:postgresql://localhost:${env.POSTGRES_PORT.value}/${env.POSTGRES_DB.value}"
    user = env.POSTGRES_USER.value
    password = env.POSTGRES_PASSWORD.value
}

ktlint {
    version.set(libs.versions.ktlint)
    filter {
        exclude { element ->
            element.file.path.contains("generated")
        }
        include("**/kotlin/**")
    }
}

tasks.named("openApiGenerate") {
    dependsOn("compileTypeSpec")
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir/tsp-output/schema/openapi.yaml")
    outputDir.set(
        layout.buildDirectory
            .dir("generated-sources/api")
            .get()
            .toString(),
    )
    apiPackage.set("com.example.bookmanagement.api")
    invokerPackage.set("com.example.bookmanagement.api.invoker")
    modelPackage.set("com.example.bookmanagement.api.model")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "useSpringBoot3" to "true",
            "interfaceOnly" to "true",
            "reactive" to "true",
        ),
    )
}

tasks.register<Exec>("tspExists") {
    group = "typespec"
    description = "Checks if TypeSpec is installed"
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd.exe", "/d", "/c", "tsp", "--version")
    } else {
        commandLine("tsp", "--version")
    }
    isIgnoreExitValue = true

    doLast {
        if (executionResult.get().exitValue == 0) {
            println("TypeSpec is installed")
        } else {
            throw GradleException("TypeSpec is not installed. Run: npm install -g @typespec/compiler @typespec/openapi3")
        }
    }
}

tasks.register<Exec>("compileTypeSpec") {
    group = "typespec"
    description = "Compiles TypeSpec to OpenAPI"
    if (System.getProperty("os.name").lowercase().contains("windows")) {
        commandLine("cmd.exe", "/d", "/c", "tsp", "compile", "specs/main.tsp")
    } else {
        commandLine("tsp", "compile", "specs/main.tsp")
    }

    inputs.files(
        fileTree("specs") {
            include("**/*.tsp")
        },
    )

    outputs.dir("tsp-output")

    dependsOn("tspExists")
}
