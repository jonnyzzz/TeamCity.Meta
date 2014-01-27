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

import jetbrains.buildServer.web.openapi.PageExtension
import jetbrains.buildServer.web.openapi.SimplePageExtension
import jetbrains.buildServer.web.openapi.PlaceId
import jetbrains.buildServer.web.openapi.PagePlaces
import jetbrains.buildServer.web.openapi.PluginDescriptor
import javax.servlet.http.HttpServletRequest
import jetbrains.buildServer.web.util.WebUtil
import jetbrains.buildServer.controllers.BaseController
import jetbrains.buildServer.web.openapi.WebControllerManager
import javax.servlet.http.HttpServletResponse
import org.springframework.web.servlet.ModelAndView
import com.jonnyzzz.teamcity.plugins.meta.having
import org.springframework.security.core.authority.AuthorityUtils
import jetbrains.buildServer.util.FileUtil
import jetbrains.buildServer.serverSide.ProjectManager
import com.jonnyzzz.teamcity.plugins.meta.div
import jetbrains.buildServer.controllers.AuthorizationInterceptor
import jetbrains.buildServer.serverSide.auth.SecurityContext
import jetbrains.buildServer.serverSide.auth.AuthUtil
import jetbrains.buildServer.serverSide.impl.auth.ServerAuthUtil
import jetbrains.buildServer.serverSide.auth.Permissions
import jetbrains.buildServer.serverSide.auth.Permission

public class ThePaths(val plugin : PluginDescriptor) {
  public val install_button_html : String
     get() = plugin.getPluginResourcesPath("install_button.html")

  public val install_button_res : String
     get() = plugin.getPluginResourcesPath("install_button.jsp")

  public val install_lits_html : String
     get() = plugin.getPluginResourcesPath("install_list.html")

  public val install_list_res : String
     get() = plugin.getPluginResourcesPath("install_list.jsp")

  public val install_action : String
     get() = plugin.getPluginResourcesPath("install.html")
}

public class Auth(val auth : SecurityContext) {
  public fun isAllowed(projectId : String) : Boolean {
    try {
      assertAllowed(projectId)
      return true
    } catch (t:Throwable) {
      return false
    }
  }

  public fun assertAllowed(projectId : String) {
    ServerAuthUtil.checkHasPermissionForProject(
            auth.getAuthorityHolder(),
            projectId,
            Permission.EDIT_PROJECT)
  }
}

public class AllPagesHeaderPagePlace(
        pagePlaces : PagePlaces,
        plugin : PluginDescriptor,
        val paths : ThePaths,
        val auth : Auth
) : SimplePageExtension(
        pagePlaces,
        PlaceId.ALL_PAGES_HEADER,
        "meta-all-" + plugin.getPluginName(),
        plugin.getPluginResourcesPath("all_pages.jsp")) {

  override fun isAvailable(request: HttpServletRequest): Boolean {
    val path = WebUtil.getOriginalPathWithoutContext(request)

    return path?.startsWith("/admin/editProject.html")?: false
           && request.getParameter("tab") == "metaRunner"
           && with(request.getParameter("projectId")) {
              this != null && auth.isAllowed(this)
           }
  }

  override fun fillModel(model: Map<String, Any>, request: HttpServletRequest) {
    super<SimplePageExtension>.fillModel(model, request)
    having(model as MutableMap){
      put("jonnyzzzMetaInstallButtonController", paths.install_button_html + "?projectId=" + request.getParameter("projectId"))
    }
  }
}

public class InstallButtonController(web : WebControllerManager,
                                     plugin : PluginDescriptor,
                                     val paths : ThePaths,
                                     val auth: Auth) : BaseController() {
  {
    web.registerController(paths.install_button_html, this)
  }

  override fun doHandle(request: HttpServletRequest, response: HttpServletResponse): ModelAndView? {
    val projectId = request.getParameter("projectId")!!
    auth.assertAllowed(projectId)
    return having(ModelAndView(paths.install_button_res)) {
      getModel()?.put("metaListPath", paths.install_lits_html + "?projectId=" + projectId)
    }
  }
}

public class InstallListController(web: WebControllerManager,
                                   plugin: PluginDescriptor,
                                   val paths : ThePaths,
                                   val auth : Auth,
                                   val model : ModelBuidler) : BaseController() {
  {
    web.registerController(paths.install_lits_html, this)
  }

  override fun doHandle(request: HttpServletRequest, response: HttpServletResponse): ModelAndView? {
    val projectId = request.getParameter("projectId")!!
    auth.assertAllowed(projectId)
    return having(ModelAndView(paths.install_list_res)) {
      with(getModel()!!) {
        put("model", model.model)
        put("projectId", request.getParameter("projectId")!!)
        put("installPath", paths.install_action)
      }
    }
  }
}

public class InstallController(web : WebControllerManager,
                               plugin : PluginDescriptor,
                               val projects : ProjectManager,
                               val auth : Auth,
                               val paths : ThePaths,
                               val model : ModelBuidler) : BaseController() {
  {
    web.registerController(paths.install_action, this)
  }


  override fun doHandle(request: HttpServletRequest, response: HttpServletResponse): ModelAndView? {
    if (!"POST".equalsIgnoreCase(request.getMethod()!!)) return having(null) {
      response.setStatus(404)
    }

    val projectId = request.getParameter("projectId")!!
    auth.assertAllowed(projectId)

    val project = projects.findProjectByExternalId(projectId)!!
    val id = request.getParameter("meta")!!
    val it = model.model.runners.find { it.id == id }!!

    val dest = project.getPluginDataDirectory("metaRunners") / (projectId + "_" + it.id + ".xml")
    dest.getParentFile()?.mkdirs()
    FileUtil.copy(it.xml, dest)

    return having(null) {
      having(response) {
        setStatus(200)
        setContentType("text/plain")
        getWriter()!!.write("OK")
      }
    }
  }
}