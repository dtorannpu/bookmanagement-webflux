import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-database-postgresql:10.11.0")
    }
}

plugins {
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.23"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("org.jooq.jooq-codegen-gradle") version "3.19.7"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("org.flywaydb.flyway") version "10.11.0"
    id("org.openapi.generator") version "7.4.0"
    id("jacoco")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
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
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:3.19.7")
    implementation("org.jooq:jooq-kotlin:3.19.7")
    implementation("org.jooq:jooq-kotlin-coroutines:3.19.7")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-api:2.5.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.projectreactor:reactor-test")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    runtimeOnly("io.r2dbc:r2dbc-pool:1.0.1.RELEASE")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    jooqCodegen("org.postgresql:postgresql")
    jooqCodegen("org.jooq:jooq:3.19.7")
    jooqCodegen("org.jooq:jooq-meta:3.19.7")
    jooqCodegen("org.jooq:jooq-codegen:3.19.7")
    jooqCodegen("co.uzzu.dotenv:gradle:4.0.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("io.mockk:mockk:1.12.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget = JvmTarget.JVM_21
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

flyway {
    url = "jdbc:postgresql://localhost:${env.POSTGRES_PORT.value}/${env.POSTGRES_DB.value}"
    user = env.POSTGRES_USER.value
    password = env.POSTGRES_PASSWORD.value
}

ktlint {
    version.set("1.5.0")
    filter {
        exclude { element ->
            element.file.path.contains("generated")
        }
        // exclude("**/generated-sources/**")
        include("**/kotlin/**")
    }
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir/bookmanagement.yaml")
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
        ),
    )
}
