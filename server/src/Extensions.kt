/*
 * Copyright 2000-2014 Eugene Petrenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jonnyzzz.teamcity.plugins.meta

import jetbrains.buildServer.messages.DefaultMessagesInfo
import java.io.IOException
import jetbrains.buildServer.RunBuildException
import java.io.File
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.io.OutputStreamWriter
import jetbrains.buildServer.util.FileUtil
import java.io.Closeable
import org.apache.log4j.Logger

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 16.08.13 22:40
 */

inline fun io<T>(errorMessage: String, body: () -> T): T {
  try {
    return body()
  } catch (e: IOException) {
    throw RunBuildException("${errorMessage}. ${e.getMessage()}", e)
  }
}

fun writeUTF(file:File, text:String) {
  val writer = OutputStreamWriter(BufferedOutputStream(FileOutputStream(file)), "utf-8")
  try {
    writer.write(text)
  } finally {
    FileUtil.close(writer)
  }
}


fun String?.isEmptyOrSpaces() : Boolean = com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces(this)

fun String.splitHonorQuotes() : List<String> = jetbrains.buildServer.util.StringUtil.splitHonorQuotes(this).filterNotNull()

fun Collection<String>.join(sep : String) : String = jetbrains.buildServer.util.StringUtil.join(sep, this)!!
fun Array<String>.join(sep : String) : String = jetbrains.buildServer.util.StringUtil.join(sep, this)!!

fun String.n(s:String) : String = this + "\n" + s

fun String?.fetchArguments() : Collection<String> {
  if (this == null || this.isEmptyOrSpaces()) return listOf<String>()

  return this
          .split("[\\r\\n]+")
          .map { it.trim() }
          .filter { !it.isEmptyOrSpaces() }
          .flatMap{ it.splitHonorQuotes() }
}


fun File.resolve(relativePath : String) : File = jetbrains.buildServer.util.FileUtil.resolvePath(this, relativePath)

data class TempFileName(val prefix : String, val suffix : String)
fun File.tempFile(details : TempFileName) : File = com.intellij.openapi.util.io.FileUtil.createTempFile(this, details.prefix, details.suffix, true) ?: throw IOException("Failed to create temp file under ${this}")

fun File.smartDelete() = com.intellij.openapi.util.io.FileUtil.delete(this)

//we define this category to have plugin logging without logger configs patching
fun log4j<T>(clazz : Class<T>) : Logger = Logger.getLogger("jetbrains.buildServer.${clazz.getName()}")!!
fun File.div(child : String) : File = File(this, child)

fun File.listFilesRec() : List<File> {
  fun listFilesRec(root: File, it : File.() -> Unit) {
    val files = root.listFiles()?:array<File>()
    files.filter { it.isFile() } forEach { file -> file.it() }
    files.filter { it.isDirectory()} forEach { dir -> listFilesRec(dir, it)}
  }

  val list = arrayListOf<File>()
  listFilesRec(this) { list.add(this) }
  return list
}

tailRecursive
fun String.trimStart(x : String) : String = if (startsWith(x)) substring(x.length()).trimStart(x) else this

inline fun <T, S:Closeable>using(stream:S, action:(S)->T) : T {
  try {
    return action(stream)
  } finally {
    FileUtil.close(stream)
  }
}

inline fun <T, S:Closeable>catchIO(stream:S, error:(IOException) -> Throwable, action:(S)->T) : T =
        using(stream) {
          try {
            action(stream)
          } catch(e: IOException) {
            throw error(e)
          }
        }

fun <K,V,T:MutableMap<K, V>> T.plus(m:Map<K, V>) : T {
  this.putAll(m)
  return this
}

fun <K, T:Iterable<K>> T.firstOrEmpty() : K? {
  for(k in this) return k
  return null
}

inline fun having<T>(t:T, ƒ : T.()->Unit) : T {
  t.ƒ()
  return t
}
