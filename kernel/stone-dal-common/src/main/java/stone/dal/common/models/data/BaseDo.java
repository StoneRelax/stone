package stone.dal.common.models.data;

import java.util.HashSet;
import java.util.Set;
import org.springframework.util.ClassUtils;

/**
 * @author fengxie
 */
public class BaseDo {

  private States _state;
	private Set<String> _changes = new HashSet<>();
	private transient boolean _monitor;
	private Set<String> _lazyLoading = new HashSet<>();

  public States get_state() {
		return _state;
	}

  public void set_state(States _state) {
		this._state = _state;
	}

	public Set<String> get_changes() {
		return _changes;
	}

	public void set_changes(Set<String> _changes) {
		this._changes = _changes;
	}

  public void ackChange(String field) {
    _state = States.UPDATED;
    _changes.add(field);
	}

	public void monitor_on() {
		this._monitor = true;
	}

	public void monitor_off() {
		this._monitor = false;
	}

	public boolean monitor() {
		return _monitor;
	}

  public void markDel() {
    _state = States.DELETED;
  }

	public void markLazyLoadedField(String field) {
		_lazyLoading.add(field);
	}

	public boolean isLoaded(String field) {
		return _lazyLoading.contains(field);
	}

	protected Class getUserClass(Class clazz) {
		return ClassUtils.getUserClass(clazz);
	}

  public enum States {
    CREATE,
    UPDATED,
    DELETED
  }

}
