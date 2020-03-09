//import org.gradle.api.Plugin
//import org.gradle.api.Project
//
//public class BlogPlugin implements Plugin<Project> {
//
//    @Override
//    public void apply(Project project) {
//        def showGitStatusTask  = target.tasks.create("showGitStatus") << {
//            println "git status".execute().text
//        }
//
//        showGitStatusTask.group = "Git Group"
//        showGitStatusTask.description = "The description is: show git status now"
//    }
//}
//
