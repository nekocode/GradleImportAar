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
import com.android.build.gradle.internal.publishing.AndroidArtifacts.ArtifactType
import com.google.common.collect.ImmutableList
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.transform.ArtifactTransform
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel

import java.util.regex.Pattern

import static org.gradle.api.internal.artifacts.ArtifactAttributes.ARTIFACT_FORMAT

/**
 * Debug command: ./gradlew -Dorg.gradle.daemon=false -Dorg.gradle.debug=true :pure-java-lib:buildDependents
 *
 * @author nekocode (nekocode.cn@gmail.com)
 */
class ImportAarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        final JavaPluginConvention convention =
                (JavaPluginConvention) project.getConvention().getPlugins().get("java")
        if (convention == null || project.getPlugins().findPlugin(AndroidBasePlugin) != null) {
            throw new RuntimeException("The 'import-aar' plugin can only be used in a pure java module.")
        }

        final Configuration config = project.getConfigurations().maybeCreate("aarCompileOnly")
        config.extendsFrom(project.getConfigurations().getByName("compileOnly"))
        config.getAttributes().attribute(ARTIFACT_FORMAT, ArtifactType.JAR.getType())

        convention.getSourceSets().findByName("main").compileClasspath += config

        project.getPluginManager().apply(IdeaPlugin)
        project.getExtensions().getByType(IdeaModel).getModule().getScopes()
                .get("PROVIDED").get("plus").add(config)

        /**
         * Register VariantTransform
         */
        final DependencyHandler dependencies = project.getDependencies()
        dependencies.registerTransform {
            it.getFrom().attribute(ARTIFACT_FORMAT, AndroidArtifacts.TYPE_AAR)
            it.getTo().attribute(ARTIFACT_FORMAT, ArtifactType.EXPLODED_AAR.getType())
            it.artifactTransform(ExtractAarTransform)
        }

        dependencies.registerTransform {
            it.getFrom().attribute(ARTIFACT_FORMAT, ArtifactType.EXPLODED_AAR.getType())
            it.getTo().attribute(ARTIFACT_FORMAT, "aar-jars")
            it.artifactTransform(AarTransform) { params(ArtifactType.JAR, false) }
        }

        dependencies.registerTransform {
            it.getFrom().attribute(ARTIFACT_FORMAT, "aar-jars")
            it.getTo().attribute(ARTIFACT_FORMAT, ArtifactType.JAR.getType())
            it.artifactTransform(AarJarArtifactTransform)
        }
    }

    static class AarJarArtifactTransform extends ArtifactTransform {

        @Override
        List<File> transform(File file) {
            final String[] names = file.getPath().split(Pattern.quote(File.separator))
            final String aarName = names[names.length - 4]
            final File renamedJar = new File(getOutputDirectory(), aarName + ".jar")
            renamedJar << file.bytes
            return ImmutableList.of(renamedJar)
        }
    }
}
