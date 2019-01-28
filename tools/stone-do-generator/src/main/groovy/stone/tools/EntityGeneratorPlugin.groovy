package stone.tools

import org.gradle.api.Plugin
import org.gradle.api.Project
import stone.dal.tools.meta.ExcelColumnMeta

//import stone.dal.tools.DoGenerator

class EntityGeneratorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.tasks.create('generateEntity') {
            // todo why unable to load class 'freemarker.cache.TemplateLoader'.
//            DoGenerator doGenerator = new DoGenerator()
//            doGenerator.build("/Users/xzang/workspace/musketeers/stone/tools/stone-entity-generator/do-meta.xlsx", "stone.dal.pojo")
            ExcelColumnMeta excelColumnMeta = new ExcelColumnMeta()
            excelColumnMeta.setTitle("test title")
            println "*************"
            println excelColumnMeta.getTitle()
            println "HelloWorld!"
        }
    }

}