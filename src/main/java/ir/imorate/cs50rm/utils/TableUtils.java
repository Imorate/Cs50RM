package ir.imorate.cs50rm.utils;

import ir.imorate.cs50rm.shell.table.BeanTableModelBuilder;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import java.util.Map;

public final class TableUtils {

    public static String beanTable(Object bean, Map<String, Object> header, int totalWidth) {
        TableModel model = new BeanTableModelBuilder(bean, header).build();
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        return tableBuilder.build().render(totalWidth);
    }

}

