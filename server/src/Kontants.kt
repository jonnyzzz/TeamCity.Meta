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

import kotlin.properties.Delegates
import jetbrains.buildServer.serverSide.TeamCityProperties

public object Kontants {
  public val GITHUB_DOWNLOAD_URL: String by Delegates.lazy {
    TeamCityProperties.getProperty("jonnyzzz.teamcity.meta.download.url", "https://github.com/JetBrains/meta-runner-power-pack/archive/master.zip")
  }
}