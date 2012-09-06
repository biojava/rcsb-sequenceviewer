package org.rcsb.sequence.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * This class functions like a <tt>Map</tt> of <tt>Collections</tt>s.
 * <ul>
 *    <li>Specify the desired <tt>Map</tt> and <tt>Collection</tt> implementations in the constructor</li>
 *    <li>The naked value -- Not a collection thereof -- should be specified for generic V.</li>
 *    <li>Values are stored inside collections, unless they implement <tt>Collection&lt;V&gt;</tt></li>
 *    <li>Collections are instantiated from the supplied classes</li>
 *    <li>Additional methods for deep adding/finding an element are provided</li>
 *    <li>You can get a single collection containing all values by calling <tt>flattenedValues()</tt>
 * <ul>
 * @author mulvaney
 */
public class MapOfCollections<K, V> implements Map<K, Collection<V>>, Serializable {

	private static final long serialVersionUID = 1L;
	protected Map<K, Collection<V>> theMap;

	protected Class<? extends Collection> collectionClass;
	protected Object[] comparator;

	/**
	 * This constructor builds a <tt>Map</tt> of <tt>Collections</tt>s.
	 * <ul>
	 *    <li>Specify the desired <tt>Map</tt> and <tt>Collection</tt> implementations in the constructor</li>
	 *    <li>The naked value -- Not a collection thereof -- should be specified for generic V.</li>
	 *    <li>Values are stored inside collections, unless they implement <tt>Collection&lt;V&gt;</tt></li>
	 *    <li>Collections are instantiated from the supplied classes</li>
	 *    <li>Additional methods for deep adding/finding an element are provided</li>
	 *    <li>You can get a single collection containing all values by calling <tt>flattenedValues()</tt>
	 * <ul>
	 *
	 * @param mapImplementation
	 * @param collectionImplementation
	 */

	public MapOfCollections(Class<? extends Map> mapImplementation, Class<? extends Collection> collectionImplementation)
	{
		theMap = initMap(mapImplementation);
		collectionClass = collectionImplementation;
	}


	private static Map initMap(Class<? extends Map> mapImplementation)
	{
		Map map = null;
		try {
			map = mapImplementation.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Could not create MapOfCollections using " + mapImplementation.getCanonicalName() + " as map implementation. Does it have a zero-arg constructor?", e);
		}
		return map;
	}

	/**
	 * This constructor builds a <tt>Map</tt> of <tt>SortedSet</tt>s.
	 * <ul>
	 *    <li>Specify desired <tt>Map</tt>, <tt>SortedSet</tt> and <tt>Comparator</tt> implementations in the constructor</li>
	 *    <li>The naked value -- Not a collection thereof -- should be specified for generic V.</li>
	 *    <li>Values are stored inside collections, unless they implement <tt>Collection&lt;V&gt;</tt></li>
	 *    <li>Collections are instantiated from the supplied classes</li>
	 *    <li>Additional methods for deep adding/finding an element are provided</li>
	 *    <li>You can get a single collection containing all values by calling <tt>flattenedValues()</tt>
	 * <ul>
	 * 
	 * @param mapImplementation
	 * @param collectionImplementation
	 * @param comparator
	 */

	public MapOfCollections(Class<? extends Map> mapImplementation, Class<? extends SortedSet> collectionImplementation, Comparator<V> comparator)
	{
		this(mapImplementation, collectionImplementation);
		this.comparator = new Object[]{comparator};
	}

	/**
	 * Constructs a new <tt>MapOfCollections</tt> with the same mappings as the specified <tt>MapOfCollections</tt>.
	 * New value collections are instantiated and populated with the same values as in the specified <tt>MapOfCollections</tt>.
	 * @param moc
	 */

	public MapOfCollections(MapOfCollections<K, V> moc)
	{
		this(moc.theMap.getClass(), moc.collectionClass);
		this.comparator = moc.comparator;
		for(Entry<K,Collection<V>> e : moc.theMap.entrySet())
		{
			Collection<V> newCol = getCollectionInstance();
			newCol.addAll(e.getValue());
			put(e.getKey(), newCol);
		}
	}

	/**
	 * This constructor builds a <tt>HashMap</tt> of <tt>ArrayList</tt>s.
	 * <ul>
	 *    <li>The naked value -- Not a collection thereof -- should be specified for generic V.</li>
	 *    <li>Values are stored inside collections, unless they implement <tt>Collection&lt;V&gt;</tt></li>
	 *    <li>Collections are instantiated from the supplied classes</li>
	 *    <li>Additional methods for deep adding/finding an element are provided</li>
	 *    <li>You can get a single collection containing all values by calling <tt>flattenedValues()</tt>
	 * <ul>
	 *
	 */
	public MapOfCollections()
	{
		this(HashMap.class, ArrayList.class);
	}

	public void clear() {
		theMap.clear();
	}

	public boolean containsKey(Object key) {
		return theMap.containsKey(key);
	}

	/**
	 * If value is an instanceof <tt>Collection</tt>, this behaves the same as LinkedHashMap.containsValue(Object o).
	 * Otherwise it performs a linear search of all value collections for the value specified. This is probably quite slow.
	 */

	public boolean containsValue(Object value) {
		if(value instanceof Collection)
		{
			return theMap.containsValue(value);
		}

		// linear search. boo!
		for(final Collection<V> c : theMap.values())
		{
			if(c != null && c.contains(value))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isEmpty() {
		return theMap.isEmpty();
	}

	public int size() {
		return theMap.size();
	}

	public int size(K key) {
		Collection<V> col = get(key);
		return col == null ? 0 : col.size();
	}

	public Collection<V> get(Object key) {
		return theMap.get(key);
	}

	public V getFirst(Object key)
	{
		return getFirstOrLast(key, false);
	}

	public V getLast(Object key)
	{
		return getFirstOrLast(key, true);
	}


	private V getFirstOrLast(Object key, boolean getLast)
	{
		Collection<V> col = get(key);
		V result = null;
		if(col != null && col.size() > 0)
		{
			if(col instanceof SortedSet)
			{
				// get by native sort
				SortedSet<V> ss = (SortedSet<V>)col;
				result = getLast ? ss.last() : ss.first();
			}
			else if(col instanceof List)
			{
				// get by index
				int idx = getLast ? col.size() - 1 : 0;
				result = ((List<V>)col).get(idx);
			}
			else
			{
				// iterate!!! woo!!!
				Iterator<V> it = col.iterator();
				do
				{
					result = it.next();
				}
				while(getLast && it.hasNext());
			}
		}
		return result;
	}

	
	public Collection<V> put(K key, Collection<V> value) {
		
		return theMap.put( key, value);
	}

	




	/**
	 * Add all values to the specified key
	 * @param key
	 * @param values
	 * @return <tt>true</tt> if the collection mapped to the specified key changed as a result of the call
	 */
	public boolean addAll(K key, Collection<V> values) {
		Collection<V> col = theMap.get(key);
		if(col == null)
		{
			col = getCollectionInstance();

			theMap.put(key, col);
		}
		return col.addAll(values);
	}

	/**
	 * Append the specified value to all keys
	 * @param keys
	 * @param value
	 * @return <tt>true</tt> unless <tt>keys</tt> is <tt>null</tt>
	 */
	public boolean addAll(Collection<K> keys, V value)
	{
		if(keys == null) return false;
		for(K key : keys)
		{
			putOne(key, value);
		}
		return true;
	}

	public void putAll(Map<? extends K, ? extends Collection<V>> t) {
		theMap.putAll(t);
	}

	public Collection<V> remove(Object key) {
		return theMap.remove(key);
	}

	public boolean remove(K key, V value) {
		Collection<V> c = theMap.get(key);
		boolean removed = false;
		if(c != null) 
		{
			removed = c.remove(value);
			if(c.size() == 0) remove(key); // if the collection is now empty go ahead and remove it from the map
		}
		return removed;
	}

	public Iterator<V> iterator(K key)
	{
		Collection<V> col = get(key);
		if(col == null) col = Collections.emptyList();
		return col.iterator();
	}

	public Collection<Collection<V>> values() {
		return theMap.values();
	}

	public Set<java.util.Map.Entry<K, Collection<V>>> entrySet() {
		return theMap.entrySet();
	}

	public Set<K> keySet() {
		return theMap.keySet();
	}

	/**
	 * Add given value to the collection held by the given key. If no collection exists there,
	 * create it first.
	 * @param key
	 * @param value
	 * @return
	 */
	public Collection<V> putOne(K key, V value) {
		Collection<V> c = theMap.get(key);
		if(c == null)
		{
			c = getCollectionInstance();

			theMap.put(key, c);
		}

		c.add(value);

		return Collections.emptyList();
	}

	/**
	 * Get a flattened List of all values. 
	 * Map keys are iterated over in the order specified (or not) by the chosen Map implementation.
	 * @return flattenedValues.
	 */
	public List<V> flattenedValues() 
	{
		List<V> result = new ArrayList<V>();
		for(Collection<V> c : theMap.values())
		{
			if(c != null)
			{
				result.addAll( c );
			}
		}
		return result;
	}


	/**
	 * Get a Map<K, String> containing all values for each key concatenated into a comma-separated String.
	 * @return the map
	 */
	public Map<K, String> flattenedMapToStrings()
	{
		Map<K, String> result = initMap(theMap.getClass());

		for(K key : theMap.keySet())
		{
			result.put(key, colToStr(theMap.get(key)));
		}

		return result;
	}

	private String colToStr(Collection<V> col)
	{
		if(col.size() == 1) 
		{
			return String.valueOf(col.iterator().next());
		}

		StringBuilder result = new StringBuilder();
		for(V value : col)
		{
			result.append(String.valueOf(value))
			.append(',')
			.append(' ');
		}
		result.delete(result.length() - 2, result.length());
		return result.toString();
	}


	private static final Class[] INSTANTIATE_SORTED_SET = { Comparator.class };


	public Collection<V> getCollectionInstance()
	{
		Collection<V> c;
		try {
			if(comparator != null)
			{
				c = collectionClass.getConstructor(INSTANTIATE_SORTED_SET).newInstance(comparator);
			}
			else
			{
				c = collectionClass.newInstance();
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not instantiate Collection implementation " + collectionClass.getCanonicalName());
		}
		return c;
	}

}
