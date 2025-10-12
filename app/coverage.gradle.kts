import org.gradle.api.tasks.testing.logging.TestExceptionFormat

// Configure JaCoCo extension
extensions.configure<JacocoPluginExtension> {
    toolVersion = "0.8.13"
}
// Coverage thresholds
val minimumCoverage = 70

// File filter configurations for JaCoCo
val fileFilterIncludeJacoco = listOf(
    "${projectDir}/src/main/java",
    "${projectDir}/src/main/kotlin"
)

// Exclude generated and irrelevant files from coverage
val fileFilterExcludeJacoco = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",
    "**/databinding/**/*.*",
    "**/generated/**/*.*",
    "**/di/**/*.*",
)

// Data class for coverage parsing
data class CoverageData(val covered: Int, val total: Int)

// Helper function to parse coverage from XML
fun parseCoverageFromXml(xmlContent: String): Map<String, CoverageData> {
    val coverageMap = mutableMapOf<String, CoverageData>()
    val counterRegex = """<counter type="(\w+)" missed="(\d+)" covered="(\d+)"/>""".toRegex()

    counterRegex.findAll(xmlContent).forEach { match ->
        val type = match.groupValues[1].lowercase()
        val missed = match.groupValues[2].toInt()
        val covered = match.groupValues[3].toInt()
        val total = missed + covered

        if (type in listOf("instruction", "branch", "line", "method", "class")) {
            coverageMap[type] = CoverageData(covered, total)
        }
    }

    return coverageMap
}

// JaCoCo Test Report Task - Use afterEvaluate to ensure proper initialization
afterEvaluate {
    tasks.register<JacocoReport>("jacocoTestReport") {
        dependsOn("testDebugUnitTest")
        group = "Reporting"
        description = "Generate Jacoco coverage reports"

        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }

        val debugTree = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes")) {
            exclude(fileFilterExcludeJacoco)
        }
        val kotlinDebugTree = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
            exclude(fileFilterExcludeJacoco)
        }

        classDirectories.setFrom(files(listOf(debugTree, kotlinDebugTree)))
        sourceDirectories.setFrom(files(fileFilterIncludeJacoco))
        executionData.setFrom(fileTree(layout.buildDirectory) {
            include("**/*.exec", "**/*.ec")
        })
    }

    // JaCoCo Coverage Verification Task
    tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        dependsOn("jacocoTestReport")
        group = "Verification"
        description = "Verify Jacoco coverage thresholds"

        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = (minimumCoverage / 100).toBigDecimal()
                }
            }
            rule {
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = (minimumCoverage / 100).toBigDecimal()
                }
            }
        }

        val debugTree = fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes")) {
            exclude(fileFilterExcludeJacoco)
        }
        val kotlinDebugTree = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
            exclude(fileFilterExcludeJacoco)
        }

        classDirectories.setFrom(files(listOf(debugTree, kotlinDebugTree)))
        sourceDirectories.setFrom(files(fileFilterIncludeJacoco))
        executionData.setFrom(fileTree(layout.buildDirectory) {
            include("**/*.exec", "**/*.ec")
        })
    }

    // Print Coverage Task
    tasks.register("printCoverage") {
        dependsOn("jacocoTestReport")
        group = "Reporting"
        description = "Print coverage summary to console"

        doLast {
            val reportFile = file("${layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")

            if (reportFile.exists()) {
                val xmlContent = reportFile.readText()
                val coverageData = parseCoverageFromXml(xmlContent)

                println("\n" + "=".repeat(60))
                println("📊 JACOCO COVERAGE REPORT (MIN COVERAGE: $minimumCoverage%)")
                println("=".repeat(60))

                coverageData.forEach { (type, data) ->
                    val percentage = if (data.total > 0) {
                        (data.covered.toDouble() / data.total.toDouble() * 100)
                    } else 0.0

                    val status = if (percentage >= minimumCoverage) "✅" else "❌"
                    println(String.format("%s %-12s: %6.2f%% (%d/%d)",
                        status, type.uppercase(), percentage, data.covered, data.total))
                }

                println("=".repeat(60))
                val overallCovered = coverageData.values.sumOf { it.covered }
                val overallTotal = coverageData.values.sumOf { it.total }
                val overallPercentage = if (overallTotal > 0) {
                    (overallCovered.toDouble() / overallTotal.toDouble() * 100)
                } else 0.0

                val overallStatus = if (overallPercentage >= minimumCoverage) "✅" else "❌"
                println(String.format("%s %-12s: %6.2f%% (%d/%d)",
                    overallStatus, "OVERALL", overallPercentage, overallCovered, overallTotal))
                println("=".repeat(60) + "\n")

            } else {
                println("❌ Coverage report not found. Run 'jacocoTestReport' first.")
            }
        }
    }

    // Make coverage verification also print coverage
    tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        finalizedBy("printCoverage")
    }
}