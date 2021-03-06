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

package com.jonnyzzz.teamcity.plugins.meta.web

import com.jonnyzzz.teamcity.plugins.meta.github.GitHubDownloader
import jetbrains.buildServer.web.openapi.PluginDescriptor
import jetbrains.buildServer.serverSide.ServerPaths
import java.io.File
import com.jonnyzzz.teamcity.plugins.meta.div
import jetbrains.buildServer.util.FileUtil
import com.jonnyzzz.teamcity.plugins.meta.Kontants
import com.jonnyzzz.teamcity.plugins.meta.listFilesRec
import jetbrains.buildServer.util.XmlXppAbstractParser
import org.jdom.xpath.XPath
import kotlin.properties.Delegates
import org.jdom.Attribute
import org.jdom.Text
import java.util.Date
import com.jonnyzzz.teamcity.plugins.meta.log4j

data public class MetaRunners(val downloadedDate : Date, val runners: List<MetaRunnerInfo>)

public class ModelBuidler(val github : GitHubDownloader,
                          val plugin : PluginDescriptor,
                          val paths : ServerPaths) {
  private val LOG = log4j(javaClass<GitHubDownloader>())

  private val Path : File by Delegates.lazy { paths.getCacheDirectory(plugin.getPluginName()) / "meta" }
  private val Timestamp : File by Delegates.lazy { Path / ".timestamp" }

  public val model : MetaRunners
    get() {
      val runners = listMetaRunners(getFetchedPath())
      val timestamp = readTimestamp()
      return MetaRunners(timestamp, runners)
    }

  public fun reset() {
    FileUtil.delete(Path)
  }

  private fun getFetchedPath() : File {
    if (Path.isDirectory() && Path.listFiles()?.size?:0 > 0) return Path

    val time = Date().getTime()

    FileUtil.createDir(Path)
    github.download(Path, Kontants.GITHUB_DOWNLOAD_URL)

    Timestamp.getParentFile().mkdirs()
    Timestamp.writeText(time.toString())

    return Path
  }

  private fun readTimestamp() : Date {
    if (!Timestamp.isFile()) return Date(0)

    try {
      return Date(Timestamp.readText().toLong())
    } catch(t : Throwable) {
      LOG.debug("Failed to read timestamp. " + t.getMessage(), t)
      FileUtil.delete(Timestamp)
    }

    return Date(0)
  }

  private fun listMetaRunners(path:File) : List<MetaRunnerInfo> =
    path
            .listFilesRec()
            .map { MetaParser.parseRunnerSpec(it) }
            .filterNotNull()
            .sortBy { it.id }

}