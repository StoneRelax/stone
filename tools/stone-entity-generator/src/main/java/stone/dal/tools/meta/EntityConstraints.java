package stone.dal.tools.meta;

import java.util.HashSet;

/**
 * Description:
 * Author: Thinkpad on 2016/1/15..
 */
public class EntityConstraints {
    private String entity;
    private HashSet<FieldConstraints> fields = new HashSet<>();

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public HashSet<FieldConstraints> getFields() {
        return fields;
    }

    public void setFields(HashSet<FieldConstraints> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityConstraints that = (EntityConstraints) o;

        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
        return !(fields != null ? !fields.equals(that.fields) : that.fields != null);

    }

    @Override
    public int hashCode() {
        int result = entity != null ? entity.hashCode() : 0;
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }
}
