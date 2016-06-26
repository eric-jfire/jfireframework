package licp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.FileResourceLoader;

public class Demo
{
    public static void main(String[] args) throws IOException
    {
        FileResourceLoader fileResourceLoader = new FileResourceLoader("z:/");
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(fileResourceLoader, cfg);
        Template template = gt.getTemplate("IntField.java");
        String type = "Boolean";
        template.binding("type", type);
        FileOutputStream outputStream = new FileOutputStream(new File("z:/", type + "Field.java"));
        template.renderTo(outputStream);
    }
}
