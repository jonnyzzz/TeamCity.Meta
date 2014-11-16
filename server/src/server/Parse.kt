
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

import java.io.File
import jetbrains.buildServer.util.FileUtil
import org.jdom.xpath.XPath
import org.jdom.Attribute
import org.jdom.Text
import kotlin.platform.platformStatic

data public class MetaRunnerInfo(val xml: File,
                                 val id: String,
                                 val name: String,
                                 val description: String,
                                 val subRunners: List<String>)

public object MetaParser {
  [platformStatic]
  public fun parseRunnerSpec(xml : File) : MetaRunnerInfo? {
    try {
      val root = FileUtil.parseDocument(xml, false)!!
      val name = XPath.newInstance("@name")?.selectSingleNode(root) as Attribute
      val descr = XPath.newInstance("description/text()")?.selectSingleNode(root) as Text
      val runners = XPath.newInstance("settings/build-runners/runner/@type")!!.selectNodes(root)!! map {it as Attribute}

      return MetaRunnerInfo(
              xml,
              FileUtil.getNameWithoutExtension(xml),
              name.getValue()!!,
              descr.getValue()!!,
              runners.map { it -> it.getValue()!! }.toSortedSet().toList())
    } catch (e: Throwable) {
      return null
    }
  }
}
