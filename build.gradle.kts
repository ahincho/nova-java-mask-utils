plugins {
    id("java-library")
    id("maven-publish")
    id("info.solidsoft.pitest") version "1.19.0-rc.1"
    jacoco
    checkstyle
    id("net.nemerosa.versioning") version "4.0.1"
    id("signing")
    id("org.owasp.dependencycheck") version "12.2.2"
    id("org.cyclonedx.bom") version "3.2.4"
}

versioning {
    releaseMode = "snapshot"
    displayMode = "snapshot"
    releaseBuild = false
}

group = "pe.edu.nova.java.libs"
version = findProperty("version") as String

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

// Force patched versions of any transitive deps that carry known CVEs (CVSS >= 7).
// These versions are taken straight from the NVD API responses for each CVE
// (verified 2026-07-13), and from maven central. They are ABIS stable upgrades:
//  - Apache HttpComponents Core 5.4.2+ for CVE-2026-54428, CVE-2026-54399
//  - Apache Commons BeanUtils 1.11.0+ for CVE-2025-48734
//  - plexus-utils 3.5.1+ for CVE-2025-67030 (commit 6d780b3 per NVD)
// Applied globally so they cover any classpath (compile, runtime, even buildscript
// transitives) so OWASP's gate reflects the real, patched state.
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.apache.httpcomponents" && requested.name.startsWith("httpcore")) {
            useVersion("4.4.16")
            because("CVE-2026-54428, CVE-2026-54399 require httpcore 4.4.16+")
        }
        if (requested.group == "org.apache.httpcomponents.core5" && requested.name.startsWith("httpcore5")) {
            useVersion("5.4.2")
            because("CVE-2026-54428, CVE-2026-54399 require httpcore5 5.4.2+")
        }
        if (requested.group == "commons-beanutils" && requested.name == "commons-beanutils") {
            useVersion("1.11.0")
            because("CVE-2025-48734 requires commons-beanutils 1.11.0+")
        }
        if (requested.group == "org.codehaus.plexus" && requested.name == "plexus-utils") {
            useVersion("3.5.1")
            because("CVE-2025-67030 requires plexus-utils 3.5.1+")
        }
    }
}

val junitVersion = "6.0.3"
val jqwikVersion = "1.9.3"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-launcher:$junitVersion")
    testImplementation("net.jqwik:jqwik:$jqwikVersion")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        xml.outputLocation.set(
            layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml")
        )
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.javadoc {
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:all", "-quiet")
        encoding = "UTF-8"
        charSet = "UTF-8"
    }
}

checkstyle {
    // Only lint production code. Test suites commonly rely on static-import
    // wildcards (org.junit.jupiter.api.Assertions.*, net.jqwik.api.*), which
    // is an accepted convention that would otherwise trip AvoidStarImport.
    sourceSets = listOf(project.sourceSets.main.get())
}

dependencyCheck {
    // NVD_API_KEY / NOVA_OWASP_FAIL_ON_CVSS are injected by reusable-owasp-check.yml.
    // Locally (no env vars set) this defaults to "never fail" (11.0, matches plugin default)
    // and an empty NVD key (slower updates, acceptable for local dev).
    failBuildOnCVSS = (System.getenv("NOVA_OWASP_FAIL_ON_CVSS") ?: "11").toFloat()
    nvd.apiKey = System.getenv("NVD_API_KEY") ?: ""

    // Must match the path reusable-owasp-check.yml caches AND restores the
    // shared nova-devops NVD mirror into. Do NOT rely on the plugin's
    // built-in default here - it was never verified/documented and previous
    // cache sizes (15-57MB) strongly suggest it did not match what was
    // being cached. Locally (no env var set) this falls back to a plain,
    // dedicated directory outside ~/.gradle so it is never confused with
    // unrelated Gradle caches.
    data.directory = System.getenv("NOVA_OWASP_DATA_DIR")
        ?: "${System.getProperty("user.home")}/.dependency-check-data"

    // mask-utils is a pure library with no runtime deps declared. By default
    // the OWASP Gradle plugin analyzes every configuration including test
    // ones and (via the gradle daemon's own classpath) the classpath of
    // buildscript plugins that are NOT propagated to consumers. That
    // surfaces CVEs in deps like httpcore (transitive of jgit/grgit),
    // plexus-utils (transitive of maven-plugin-api used by some build
    // plugins), and commons-beanutils that are build-time only and
    // never reach a downstream project consuming this artifact.
    //
    // Restricting analysis to the configurations that actually contribute
    // to the published artifact means a green OWASP job here is honest:
    // it means "the library I'm shipping has no known CVEs", not "the build
    // environment has no known CVEs" (the latter is what Dependabot's
    // built-in GitHub Advisory DB scan covers, separately).
    scanConfigurations = listOf("compileClasspath", "runtimeClasspath")

    // Investigation (2026-07-13, docs/java/06-semantic-versioning-en-java.md):
    // a cold NVD sync took 50+ min mostly due to cache scoping, NOT these
    // analyzers - but disabling ecosystems that plainly do not exist anywhere
    // in this repo removes real (if smaller) analyze-phase overhead and
    // network surface at zero detection-feature cost.
    //
    // Deliberately NOT disabled, despite this being a Java library:
    //  - nodeEnabled / nodeAudit.enabled: package.json IS present (commitlint/
    //    lefthook devDependencies) - keep scanning it for real.
    //  - opensslEnabled: harmless/fast on a pure-JVM project, could still
    //    catch an embedded native lib's version string; no reason to disable.
    // RetireJS IS disabled: it fingerprints vendored/bundled JS *library*
    // files (jQuery, lodash, etc.) - the only .js file in this repo is
    // commitlint.config.js (a config file, not a vendored library), which
    // will never match a RetireJS signature.
    analyzers {
        retirejs.enabled = false
        assemblyEnabled = false
        nuspecEnabled = false
        nugetconfEnabled = false
        msbuildEnabled = false
        golangDepEnabled = false
        golangModEnabled = false
        swiftEnabled = false
        swiftPackageResolvedEnabled = false
        cocoapodsEnabled = false
        composerEnabled = false
        cpanEnabled = false
        cmakeEnabled = false
        autoconfEnabled = false
        bundleAuditEnabled = false
        pyDistributionEnabled = false
        pyPackageEnabled = false
        rubygemsEnabled = false
dartEnabled = false
    }
}


pitest {
    junit5PluginVersion.set("1.2.3")
    targetClasses.set(setOf("pe.edu.nova.java.libs.mask.utils.*"))
    targetTests.set(setOf("pe.edu.nova.java.libs.mask.utils.*"))
    mutators.set(setOf("DEFAULTS"))
    outputFormats.set(setOf("HTML", "XML"))
    pitestVersion.set("1.23.1")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ahincho/nova-java-mask-utils")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

signing {
    val gpgKeyId: String? = System.getenv("GPG_SIGNING_KEY_ID")
    val gpgKey: String? = System.getenv("GPG_SIGNING_KEY")
    val gpgPassword: String? = System.getenv("GPG_SIGNING_PASSWORD")

    if (gpgKeyId != null && gpgKey != null) {
        useInMemoryPgpKeys(gpgKeyId, gpgKey, gpgPassword ?: "")
        sign(publishing.publications)
    }
}