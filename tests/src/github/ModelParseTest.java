/*
 * Copyright 2000-2014 JetBrains s.r.o.
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

package github;

import com.jonnyzzz.teamcity.plugins.meta.web.MetaParser;
import com.jonnyzzz.teamcity.plugins.meta.web.MetaRunnerInfo;
import jetbrains.buildServer.BaseTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;

public class ModelParseTest extends BaseTestCase {

  @Test
  public void test_parses_meta_xml() throws IOException {
    final MetaRunnerInfo info = MetaParser.parseRunnerSpec(createTempFile(SAMPLE));
    Assert.assertNotNull(info);

    Assert.assertEquals(info.getName(), "Gerrit Verification");
    Assert.assertEquals(info.getDescription().trim(), "Verifies Gerrit changes");
    Assert.assertEquals(info.getSubRunners(), Arrays.asList("Ant"));
  }


  public static final String SAMPLE = "<meta-runner name=\"Gerrit Verification\">\n" +
          "<description>\n" +
          "Verifies Gerrit changes\n" +
          "</description>\n" +
          "<settings>\n" +
          "<parameters>\n" +
          "<param name=\"system.gerrit.host\" value=\"\" spec=\"text display='normal' label='Gerrit server' validationMode='not_empty'\"/>\n" +
          "<param name=\"system.gerrit.project.name\" value=\"\" spec=\"text display='normal' label='Gerrit project name' validationMode='not_empty'\"/>\n" +
          "<param name=\"system.gerrit.commit\" value=\"%build.vcs.number%\" spec=\"text display='normal' label='Gerrit commit for verification' validationMode='not_empty'\"/>\n" +
          "<param name=\"system.gerrit.success.vote\" value=\"+1\" spec=\"text display='normal' label='Succesful build vote' validationMode='not_empty'\"/>\n" +
          "<param name=\"system.gerrit.failure.vote\" value=\"-1\" spec=\"text display='normal' label='Failed build vote' validationMode='not_empty'\"/>\n" +
          "<param name=\"system.gerrit.username\" value=\"\" spec=\"text display='normal' label='Gerrit username' validationMode='not_empty'\"/>\n" +
          "<param name=\"system.gerrit.password\" value=\"zxx775d03cbe80d301b\" spec=\"password display='normal' label='Gerrit password'\"/>\n" +
          "</parameters>\n" +
          "<build-runners>\n" +
          "<runner name=\"Publish build status\" type=\"Ant\">\n" +
          "<parameters>\n" +
          "<param name=\"build-file\">\n" +
          "<![CDATA[<project name=\"publish-build-status\" default=\"publish-status\"> <target name=\"publish-status\" unless=\"%teamcity.build.branch.is_default%\"> <echo>##teamcity[setParameter name='system.gerrit.verification.successful' value='true']</echo> </target> </project>]]>\n" +
          "</param>\n" +
          "<param name=\"build-file-path\" value=\"build.xml\"/>\n" +
          "<param name=\"teamcity.coverage.emma.include.source\" value=\"true\"/>\n" +
          "<param name=\"teamcity.coverage.emma.instr.parameters\" value=\"-ix -*Test*\"/>\n" +
          "<param name=\"teamcity.coverage.idea.includePatterns\" value=\"*\"/>\n" +
          "<param name=\"teamcity.step.mode\" value=\"execute_if_success\"/>\n" +
          "<param name=\"use-custom-build-file\" value=\"true\"/>\n" +
          "</parameters>\n" +
          "</runner>\n" +
          "<runner name=\"Verify\" type=\"Ant\">\n" +
          "<parameters>\n" +
          "<param name=\"build-file\">\n" +
          "<![CDATA[<project name=\"gerrit.verify\" default=\"verify\"> <target name=\"verify\" unless=\"%teamcity.build.branch.is_default%\"> <condition property=\"gerrit.vote\" value=\"%system.gerrit.success.vote%\" else=\"%system.gerrit.failure.vote%\"> <isset property=\"gerrit.verification.successful\"/> </condition> <condition property=\"gerrit.message\" value=\"Build successful\" else=\"Build failed\"> <isset property=\"gerrit.verification.successful\"/> </condition> <sshexec host=\"%system.gerrit.host%\" username=\"%system.gerrit.username%\" password=\"%system.gerrit.password%\" keyfile=\"${user.home}/.ssh/id_rsa\" trust=\"true\" port=\"29418\" command=\"gerrit review --project %system.gerrit.project.name% --verified ${gerrit.vote} -m '${gerrit.message}' %system.gerrit.commit%\"/> </target> </project>]]>\n" +
          "</param>\n" +
          "<param name=\"build-file-path\" value=\"build.xml\"/>\n" +
          "<param name=\"runnerArgs\" value=\"-lib %teamcity.tool.ant-net-tasks%\"/>\n" +
          "<param name=\"teamcity.coverage.emma.include.source\" value=\"true\"/>\n" +
          "<param name=\"teamcity.coverage.emma.instr.parameters\" value=\"-ix -*Test*\"/>\n" +
          "<param name=\"teamcity.coverage.idea.includePatterns\" value=\"*\"/>\n" +
          "<param name=\"teamcity.step.mode\" value=\"execute_if_failed\"/>\n" +
          "<param name=\"use-custom-build-file\" value=\"true\"/>\n" +
          "</parameters>\n" +
          "</runner>\n" +
          "</build-runners>\n" +
          "<requirements/>\n" +
          "</settings>\n" +
          "</meta-runner>";
}
