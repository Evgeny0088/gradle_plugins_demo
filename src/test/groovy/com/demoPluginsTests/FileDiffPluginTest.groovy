package com.demoPluginsTests

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification
import spock.lang.TempDir

class FileDiffPluginTest extends Specification {

    @TempDir
    File temporaryDir
    File buildFile
    final String pluginName = 'fileDiff'

    def setup(){
        buildFile = new File(temporaryDir, 'build.gradle')
        buildFile << """
            plugins {
                id 'com.demoPlugins-${pluginName}'
            }
        """
    }

    def "should  diff 2 files of same length"() {
        given:
        File testFile1 = new File(temporaryDir, 'testFile1.txt')
        testFile1.createNewFile()
        File testFile2 = new File(temporaryDir, 'testFile2.txt')
        testFile2.createNewFile()

        buildFile << """
            fileDiff {
                file1 = file('${testFile1.getName()}')
                file2 = file('${testFile2.getName()}')
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(temporaryDir)
                .withArguments(pluginName)
                .withPluginClasspath()
                .build()

        then:
        result.output.contains("Files have the same size")
        result.task(":${pluginName}").outcome == TaskOutcome.SUCCESS
    }

    def "can  diff 2 files of differing length"() {
        given:
        File testFile1 = new File(temporaryDir, 'testFile1.txt')
        testFile1 << 'Short text'
        File testFile2 = new File(temporaryDir, 'testFile2.txt')
        testFile2 << 'Longer text'

        buildFile << """
            fileDiff {
                file1 = file('${testFile1.getName()}')
                file2 = file('${testFile2.getName()}')
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(temporaryDir)
                .withArguments(pluginName)
                .withPluginClasspath()
                .build()

        then:
        result.output.contains('testFile2.txt was the largest file at ' + 'Longer text'.bytes.length + ' bytes')
        result.task(":${pluginName}").outcome == TaskOutcome.SUCCESS
    }

    def "should throw exception and failed if files do not exist"() {
        given:

        buildFile << """
            fileDiff {
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(temporaryDir)
                .withArguments('fileDiff')
                .withPluginClasspath()
                .buildAndFail()

        then:
        String diffResult = "Files aren,t found in working directory..."
        result.output.contains(diffResult)
        result.task(":${pluginName}").outcome == TaskOutcome.FAILED
    }
}