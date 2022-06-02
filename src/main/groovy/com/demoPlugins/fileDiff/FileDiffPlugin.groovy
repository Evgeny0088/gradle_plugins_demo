package com.demoPlugins.fileDiff

import org.gradle.api.Plugin
import org.gradle.api.Project

class FileDiffPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        String taskName = 'fileDiff'
        project.extensions.create(taskName, FileDiffExtension)
        project.tasks.register(taskName, FileDiffTask.class,
                task -> {
                    task.file1 = project.fileDiff.file1
                    task.file2 = project.fileDiff.file2
                }
        )
    }
}
