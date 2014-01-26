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

data public class MetaRunners(val runners: List<MetaRunnerInfo>)

public class ModelBuidler(val github : GitHubDownloader,
                          val plugin : PluginDescriptor,
                          val paths : ServerPaths) {

  private val Path : File by Delegates.lazy { paths.getCacheDirectory(plugin.getPluginName()) / "meta" }

  public val model : MetaRunners
    get() = MetaRunners(listMetaRunners(getFetchedPath()))

  public fun reset() {
    FileUtil.delete(Path)
  }

  private fun getFetchedPath() : File {
    if (Path.isDirectory() && Path.listFiles()?.size?:0 > 0) return Path

    FileUtil.createDir(Path)
    github.download(Path, Kontants.GITHUB_DOWNLOAD_URL)
    return Path
  }

  private fun listMetaRunners(path:File) : List<MetaRunnerInfo> =
    path
            .listFilesRec()
            .map { MetaParser.parseRunnerSpec(it) }
            .filterNotNull()
            .sortBy { it.id }

}