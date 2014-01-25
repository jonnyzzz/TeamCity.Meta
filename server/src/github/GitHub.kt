/*
 * Copyright 2013-2013 Eugene Petrenko
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

package com.jonnyzzz.teamcity.plugins.meta.github

import java.io.File
import org.apache.http.client.methods.HttpGet
import java.util.zip.ZipInputStream
import jetbrains.buildServer.util.FileUtil
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import com.jonnyzzz.teamcity.plugins.meta.log4j
import com.jonnyzzz.teamcity.plugins.meta.catchIO
import com.jonnyzzz.teamcity.plugins.meta.trimStart
import com.jonnyzzz.teamcity.plugins.meta.div

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.08.13 9:35
 */
public class GitHubDownloader(val http:HttpClientWrapper) {
  private val LOG = log4j(javaClass<GitHubDownloader>())

  private fun error(url:String, message:String) : Throwable {
    throw RuntimeException("Failed to download from ${url}. ${message}")
  }

  private fun error(url:String, message:String, e:Throwable) : Throwable {
    throw RuntimeException("Failed to download from ${url}. ${message}. ${e.getMessage()}", e)
  }

  public fun download(dest : File, url : String) {
    val httpGet = HttpGet(url)
    http.execute(httpGet) {
      val status = getStatusLine()!!.getStatusCode()
      if (status != 200) throw error(url, "${status} returned")
      val entity = getEntity()

      if (entity == null) throw error(url, "No data was returned")
      val contentType = entity.getContentType()?.getValue()
      if ("application/zip" != contentType) throw error(url, "Invalid content-type: ${contentType}")

      catchIO(ZipInputStream(entity.getContent()!!), {error(url, "Failed to extract", it)}) { zip ->
        FileUtil.createEmptyDir(dest)

        while(true) {
          val ze = zip.getNextEntry()
          if (ze == null) break
          if (ze.isDirectory()) continue

          val name =
                  listOf(*ze.getName().replace("\\", "/").trimStart("/").trimStart(".").split('/'))
                  .iterator().skip(1).makeString("/")
          if (name == "") continue

          LOG.debug("content: ${name}")

          val file = dest / name
          file.getParentFile()?.mkdirs()

          catchIO(BufferedOutputStream(FileOutputStream(file)), {error(url, "Failed to create ${file}", it)}) {
            zip.copyTo(it)
          }
        }
      }
    }
  }
}
