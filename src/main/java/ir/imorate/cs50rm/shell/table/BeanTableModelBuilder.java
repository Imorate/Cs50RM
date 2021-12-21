package ir.imorate.cs50rm.shell.table;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.TableModel;

import java.util.Map;

public class BeanTableModelBuilder {

    private final Object bean;
    private Map<String, Object> headerMap;

    public BeanTableModelBuilder(Object bean, Map<String, Object> header) {
        this.bean = bean;
        this.headerMap = header;
    }

    public BeanTableModelBuilder withHeader(Map<String, Object> header) {
        this.headerMap = header;
        return this;
    }

    public TableModel build() {
        final BeanWrapper wrapper = new BeanWrapperImpl(bean);
        Object[][] entityProperties = new Object[headerMap.size()][2];
        int i = 0;
        for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
            entityProperties[i][0] = entry.getValue();
            entityProperties[i][1] = wrapper.getPropertyValue(entry.getKey());
            i++;
        }
        return new ArrayTableModel(entityProperties);
    }
}