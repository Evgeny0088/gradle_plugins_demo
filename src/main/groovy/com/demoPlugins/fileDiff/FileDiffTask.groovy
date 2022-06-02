package com.demoPlugins.fileDiff

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction;

abstract class FileDiffTask extends DefaultTask {

    @InputFile
    @Optional
    abstract RegularFileProperty getFile1()

    @InputFile
    @Optional
    abstract RegularFileProperty getFile2()

    @OutputFile
    abstract RegularFileProperty getResultFile()

    FileDiffTask() {
        resultFile.convention(project.layout.buildDirectory.file('diff-result.txt'))
    }

    @TaskAction
    def diff(){
        String diffResult
        if (file1.isPresent() && file2.isPresent()){

            long fileSize1 = size(file1)
            long fileSize2 = size(file2)

            if (fileSize1 == fileSize2){
                diffResult = "Files have the same size at ${file1.get().asFile.size()} bytes."
            }else {
                File largestFile = fileSize1 > fileSize2 ? file1.get().asFile : file2.get().asFile
                diffResult = "${largestFile.toString()} was the largest file at ${largestFile.size()} bytes."
            }
            resultFile.get().asFile.write diffResult
            println "File written to $resultFile"
            println diffResult

        } else {
            diffResult = "Files aren,t found in working directory..."
            throw new GradleException(diffResult)
        }
    }

    private static long size(RegularFileProperty regularFileProperty) {
        return regularFileProperty.get().asFile.size()
    }

}