package github;/*
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

import com.jonnyzzz.teamcity.plugins.meta.github.GitHubDownloader;
import com.jonnyzzz.teamcity.plugins.meta.github.HttpClientWrapperImpl;
import jetbrains.buildServer.BaseTestCase;
import junit.framework.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 *         Date: 13.08.13 23:18
 */
public class GitHubDownloadTest extends BaseTestCase {
  @Test
  public void test() throws IOException {
    enableDebug();

    File dir = createTempDir();
    new GitHubDownloader(new HttpClientWrapperImpl()).download(dir, "https://github.com/JetBrains/meta-runner-power-pack/archive/master.zip");

    File[] files = dir.listFiles();
    Assert.assertNotNull(files);

    System.out.println("result: " + Arrays.toString(files));

    Assert.assertTrue(files.length > 0);
    Assert.assertTrue(new File(dir, "README.md").isFile());
    Assert.assertTrue(new File(dir, "LICENSE").isFile());
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void error() throws IOException {
    File dir = createTempDir();
    new GitHubDownloader(new HttpClientWrapperImpl()).download(dir, "http://blog.jonnyzzz.name");
  }
}
