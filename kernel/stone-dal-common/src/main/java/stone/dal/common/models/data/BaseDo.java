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

  private transient boolean _attached;
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
    _state = States.Updated;
    _changes.add(field);
	}

  public void attach() {
    this._attached = true;
  }

  public void detatch() {
    this._attached = false;
  }

  public boolean check_attached() {
    return _attached;
	}

  public void markDel() {
    _state = States.Deleted;
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
    Updated,
    Deleted,
  }

}
