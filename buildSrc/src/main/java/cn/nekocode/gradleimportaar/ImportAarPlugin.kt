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

import com.android.build.gradle.api.AndroidBasePlugin
import com.android.build.gradle.internal.dependency.AarTransform
import com.android.build.gradle.internal.dependency.ExtractAarTransform
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.transform.ArtifactTransform
import org.gradle.api.internal.artifacts.ArtifactAttributes.ARTIFACT_FORMAT
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import java.io.File

/**
 * Debug: ./gradlew :pJL:build -Dorg.gradle.daemon=false -Dorg.gradle.debug=true
 * @author nekocode (nekocode.cn@gmail.com)
 */
class ImportAarPlugin : Plugin<Project> {

    companion object {
        const val CONFIG_NAME_POSTFIX = "Aar"
    }

    override fun apply(project: Project) {
        // Check if this project is a pure java project
        val java = project.convention.findPlugin(JavaPluginConvention::class.java)
        if (java == null || project.plugins.findPlugin(AndroidBasePlugin::class.java) != null) {
            throw IllegalStateException("The 'import-aar' plugin can only be used in a pure java module.")
        }

        val aar = AndroidArtifacts.ArtifactType.AAR.type
        val jar = AndroidArtifacts.ArtifactType.JAR.type

        // Create aar configurations
        val allConfigs = project.configurations.toList()
        for (config in allConfigs) {
            val aarConfig = project.configurations.maybeCreate(config.name + CONFIG_NAME_POSTFIX)
            aarConfig.attributes.attribute(ARTIFACT_FORMAT, jar)

            // Add extracted jars to original configuration after project evaluating
            project.afterEvaluate {
                aarConfig.forEach { jarFile ->
                    project.dependencies.add(config.name, project.files(jarFile))
                }
            }

            // Tell Idea our aar configuration
            project.pluginManager.apply(IdeaPlugin::class.java)
            project.extensions.getByType(IdeaModel::class.java)
                    .module.scopes["PROVIDED"]!!["plus"]!!.add(aarConfig)
        }

        // Register aar transform
        project.dependencies.run {
            registerTransform {
                it.from.attribute(ARTIFACT_FORMAT, aar)
                it.to.attribute(ARTIFACT_FORMAT, jar)
                it.artifactTransform(AarJarArtifactTransform::class.java)
            }
        }
    }

    class AarJarArtifactTransform : ArtifactTransform() {

        override fun transform(input: File): MutableList<File> {
            val extractTrans = ExtractAarTransform()
            extractTrans.outputDirectory = File(outputDirectory, "exploded")
            var files = extractTrans.transform(input)

            val aarTrans = AarTransform(AndroidArtifacts.ArtifactType.JAR, false, false)
            aarTrans.outputDirectory = outputDirectory
            files = files.flatMap { aarTrans.transform(it) }

            // Copy and rename the classes.jar
            val jarFile = files.singleOrNull() ?: return arrayListOf()
            val renamedJarFile = File(outputDirectory, "${input.nameWithoutExtension}.jar")
            renamedJarFile.writeBytes(jarFile.readBytes())

            return arrayListOf(renamedJarFile)
        }
    }
}