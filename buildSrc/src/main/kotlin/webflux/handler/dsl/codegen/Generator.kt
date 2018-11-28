package webflux.handler.dsl.codegen

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.*
import java.io.File

const val CODE_GEN_OUTPUT_DIR = "kotlin/generated/dsl"

fun Project.codeGenOutputDir(): File {
    return File(this.buildDir, CODE_GEN_OUTPUT_DIR)
}

open class CodeGen : DefaultTask() {
    @TaskAction
    fun generate() {
        generateParameterDsl(project.codeGenOutputDir())
        generatePathVariableDsl(project.codeGenOutputDir())
    }
}