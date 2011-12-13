package tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

public class MapedCollection<K, V> extends Hashtable<K, Collection<V>> {

	private static final long serialVersionUID = 3032270919408520500L;

	public Collection<V> add(final K key, final V value) {
		if (this.containsKey(key)) {
			final Collection<V> contenu = this.get(key);
			contenu.add(value);
			return this.put(key, contenu);
		} else {
			final Collection<V> contenu = new ArrayList<V>();
			;
			contenu.add(value);
			return this.put(key, contenu);
		}
	}

	public boolean containsAvalue(final V value) {
		for (final K k : this.keySet())
			for (final V v : this.get(k))
				if (v.equals(value))
					return true;
		return false;
	}

	public Collection<V> getAllValues() {
		final Collection<V> finalValues = new ArrayList<V>();
		for (final Collection<V> l : super.values())
			finalValues.addAll(l);
		return finalValues;
	}
}
