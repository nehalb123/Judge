package com.jalgoarena.compile

import com.google.common.base.Preconditions
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.CharBuffer
import javax.tools.*

open class InMemoryJavaCompiler(
        private val jalgoarenaJudgeClasspath: String = "build/classes/kotlin/main"
) : JvmCompiler {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun run(className: String, source: String): MutableMap<String, ByteArray?> {

        val javaFileName = "$className.java"

        val fileManager = MemoryJavaFileManager(standardFileManager())
        val diagnostics = DiagnosticCollector<JavaFileObject>()

        val compilationTask = compilationTask(
                javaFileName, source, fileManager, diagnostics
        )

        if (!compilationTask.call()) {
            return getCompilationError(diagnostics)
        }

        val classBytes = fileManager.classBytes

        try {
            fileManager.close()
        } catch (exp: IOException) {
            logger.error("Cannot close in memory file", exp)
        }

        return classBytes
    }

    private fun standardFileManager(): StandardJavaFileManager {
        return javaCompiler.getStandardFileManager(null, null, null)
    }

    private fun compilationTask(
            fileName: String,
            source: String,
            fileManager: MemoryJavaFileManager,
            diagnostics: DiagnosticCollector<JavaFileObject>): JavaCompiler.CompilationTask {

        return javaCompiler.getTask(
                null,
                fileManager,
                diagnostics,
                javacOptions(),
                null,
                prepareTheCompilationUnit(fileName, source)
        )
    }

    private fun prepareTheCompilationUnit(fileName: String, source: String): List<JavaFileObject> {
        return listOf(SourceCodeStringInputBuffer(fileName, source))
    }

    protected open fun javacOptions(): List<String> {
        return listOf(
                "-nowarn",
                "-classpath", buildClassPath()
        )
    }

    private fun buildClassPath(): String {
        val classpath = mutableListOf<String>()

        addToClassPath(classpath, jalgoarenaJudgeClasspath)

        val result = classpath.joinToString(File.pathSeparator)
        logger.info("Classpath: $result")
        return result
    }

    private fun addToClassPath(classpath: MutableList<String>, path: String) {
        when {
            File(path).exists() -> classpath.add(File(path).absolutePath)
            else -> logger.warn("[err] Could not find class path: $path")
        }
    }

    private fun getCompilationError(
            diagnostics: DiagnosticCollector<JavaFileObject>): MutableMap<String, ByteArray?> {

        val errorMsg = StringBuilder()

        diagnostics.diagnostics.forEach { message ->
            errorMsg.append(message)
            errorMsg.append(System.lineSeparator())
        }

        throw CompileErrorException(errorMsg.toString())
    }

    private class SourceCodeStringInputBuffer(fileName: String, val code: String)
        : SimpleJavaFileObject(MemoryJavaFileManager.toUri(fileName), JavaFileObject.Kind.SOURCE) {

        override fun getCharContent(ignoreEncodingErrors: Boolean): CharBuffer {
            return CharBuffer.wrap(code)
        }
    }

    companion object {

        private val javaCompiler: JavaCompiler = ToolProvider.getSystemJavaCompiler()

        init {
            Preconditions.checkNotNull(
                    javaCompiler, "Could not get Java compiler. Please, ensure that JDK is used instead of JRE."
            )
        }
    }
}
