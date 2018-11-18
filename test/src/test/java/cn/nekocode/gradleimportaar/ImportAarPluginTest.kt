/*
 * Copyright 2018. nekocode (nekocode.cn@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.nekocode.gradleimportaar

import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
class ImportAarPluginTest {
    @get:Rule
    val tmpFolder = TemporaryFolder()

    private lateinit var project: DefaultProject

    @Before
    fun setup() {
        project = ProjectBuilder.builder()
                .withName("single")
                .build() as DefaultProject
        project.plugins.apply(JavaLibraryPlugin::class.java)
        project.repositories.run {
            add(mavenCentral())
            add(google())
        }
    }

    @Test
    fun testAddDependency() {
        project.plugins.apply(ImportAarPlugin::class.java)

        val config = project.configurations.findByName("aarCompileOnly")
        Assert.assertNotNull(config)
        config!!

        project.dependencies.add("aarCompileOnly", "com.android.support:recyclerview-v7:27.0.2")
        val jarFiles = config.files
        Assert.assertTrue(jarFiles.isNotEmpty())

        jarFiles.forEach {
            Assert.assertTrue(it.name.toLowerCase().endsWith(".jar"))
        }
    }
}