package com.jalgoarena.compile

import com.jalgoarena.JAlgoArenaJudgeApp
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.net.URL
import java.net.URLClassLoader

interface JvmCompiler {
    fun compileMethod(qualifiedClassName: String, methodName: String, parameterCount: Int, source: String): Pair<Any, Method> {

        val classBytes = run(qualifiedClassName, source)
        val clazz = Class.forName(
                qualifiedClassName, true, MemoryClassLoader(classBytes)
        )

        clazz.declaredMethods
                .filter { it.name == methodName && it.parameterCount == parameterCount && Modifier.isPublic(it.modifiers)}
                .forEach {
                    try {
                        return Pair(clazz.newInstance(), it)
                    } catch (e: InstantiationException) {
                        LOG.error("Error during creating of new class instance", e)
                        throw IllegalStateException(e.message)
                    } catch (e: IllegalAccessException) {
                        LOG.error("Error during creating of new class instance", e)
                        throw IllegalStateException(e.message)
                    }
                }

        throw NoSuchMethodError(methodName)
    }

    fun run(className: String, source: String): MutableMap<String, ByteArray?>

    private class MemoryClassLoader(val classNameToBytecode: MutableMap<String, ByteArray?>)
        : URLClassLoader(arrayOfNulls<URL>(0), JAlgoArenaJudgeApp::class.java.classLoader) {

        override fun findClass(className: String): Class<*> {
            val bufferOfBytecode = classNameToBytecode[className]

            return if (bufferOfBytecode == null) {
                super.findClass(className)
            } else {
                clearByteMap(className)
                defineClass(className, bufferOfBytecode, 0, bufferOfBytecode.size)
            }
        }

        private fun clearByteMap(className: String) {
            classNameToBytecode[className] = null
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(JvmCompiler::class.java)
    }
}
