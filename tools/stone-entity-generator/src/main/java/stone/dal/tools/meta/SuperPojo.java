package stone.dal.tools.meta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Title: SuperPojo</p>
 * <p>Description: SuperPojo</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: IPACS e-Solutions (S) Pte Ltd</p>
 *
 * @author Jinni
 */
public class SuperPojo implements Serializable {

    private String state;
    private List<String> changes = new ArrayList<String>();
    private transient boolean monitor;

    public static final long serialVersionUID = -1L;

    public transient static final String UPDATED = "UPDATED";
    public transient static final String DELETED = "DELETED";
    public transient static final String NEW = "NEW";

    public void change(String... fields) {
        Collections.addAll(changes, fields);
    }

    public boolean monitor() {
        return monitor;
    }

    public void off_monitor() {
        monitor = false;
    }

    public void monitor_on() {
        monitor = true;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getChanges() {
        return changes;
    }

    public void setChanges(List<String> changes) {
        this.changes = changes;
    }

    public static Class getCanonicalClazz(Class clazz) {
        Class _clazz = clazz;
        if (clazz.getName().contains("$")) {
            _clazz = clazz.getSuperclass();
        }
        if (_clazz.getName().contains("$")) {
            _clazz = _clazz.getSuperclass();
        }
        return _clazz;
    }

    /**
     * Unique key string
     *
     * @return String Unique key string
     */
    public String uniqueKeys() {
        return null;
    }

    /**
     * Unique key object
     *
     * @return Object Unique key object
     */
    public Object objWithUniqueKeysOnly() {
        return null;
    }
}
