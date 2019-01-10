package stone.dal.common.api;


import org.springframework.util.ClassUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author fengxie
 */
public class DalObj {
	private DalStates _state;
	private Set<String> _changes = new HashSet<>();
	private transient boolean _monitor;
	private Set<String> _lazyLoading = new HashSet<>();

	public DalStates get_state() {
		return _state;
	}

	public void set_state(DalStates _state) {
		this._state = _state;
	}

	public Set<String> get_changes() {
		return _changes;
	}

	public void set_changes(Set<String> _changes) {
		this._changes = _changes;
	}

	public void addChange(String change) {
		_state = DalStates.UPDATED;
		_changes.add(change);
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

	public void markLazyLoadedField(String field) {
		_lazyLoading.add(field);
	}

	public boolean isLoaded(String field) {
		return _lazyLoading.contains(field);
	}

	protected Class getUserClass(Class clazz) {
		return ClassUtils.getUserClass(clazz);
	}


}
