/*
 * Copyright 1997-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 * 
 * ------
 * This class has been adapted to work with the double primitive type instead
 * of Objects. The copyright of the modification belongs to:
 * Copyright 2008-2009 Iver T.I. <http://www.iver.es>  All Rights Reserved.
 * 
 * The modifications by Iver are subject to the same license as the
 * original class from Sun Microsystems.
 */

package org.gvsig.app.documents.table.summarize.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;
import java.util.Vector;

/**
 * Resizable-array implementation similar to the <tt>List</tt> interface, except
 * that it works for <tt>double</tt> values instead of Objects. Implements
 * methods similar to the list operations, except the methods that return
 * Iterators. This class provides methods to manipulate the size of the array
 * that is used internally to store the list.
 * 
 * <p>
 * The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * <tt>iterator</tt>, and <tt>listIterator</tt> operations run in constant time.
 * The <tt>add</tt> operation runs in <i>amortized constant time</i>, that is,
 * adding n elements requires O(n) time. All of the other operations run in
 * linear time (roughly speaking). The constant factor is low compared to that
 * for the <tt>LinkedList</tt> implementation.
 * 
 * <p>
 * Each <tt>DoubleList</tt> instance has a <i>capacity</i>. The capacity is the
 * size of the array used to store the elements in the list. It is always at
 * least as large as the list size. As elements are added to an DoubleList, its
 * capacity grows automatically. The details of the growth policy are not
 * specified beyond the fact that adding an element has constant amortized time
 * cost.
 * 
 * <p>
 * An application can increase the capacity of an <tt>DoubleList</tt> instance
 * before adding a large number of elements using the <tt>ensureCapacity</tt>
 * operation. This may reduce the amount of incremental reallocation.
 * 
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an <tt>DoubleList</tt> instance concurrently, and at
 * least one of the threads modifies the list structurally, it <i>must</i> be
 * synchronized externally. (A structural modification is any operation that
 * adds or deletes one or more elements, or explicitly resizes the backing
 * array; merely setting the value of an element is not a structural
 * modification.) This is typically accomplished by synchronizing on some object
 * that naturally encapsulates the list.
 * 
 * 
 * @author Josh Bloch
 * @author Neal Gafter
 * @author Adapted to <tt>double</tt> primitive type by Cesar Martinez Izquierdo
 *         <cesar.martinez@iver.es>
 * @see Collection
 * @see List
 * @see LinkedList
 * @see Vector
 * @since 1.2
 */

public class DoubleList implements RandomAccess, Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3575943640370271838L;

	/**
	 * The array buffer into which the elements of the DoubleList are stored.
	 * The capacity of the DoubleList is the length of this array buffer.
	 */
	private transient double[] elementData;

	/**
	 * The size of the DoubleList (the number of elements it contains).
	 * 
	 * @serial
	 */
	private int size;

	/**
	 * The number of times this list has been <i>structurally modified</i>.
	 * Structural modifications are those that change the size of the list, or
	 * otherwise perturb it in such a fashion that iterations in progress may
	 * yield incorrect results.
	 * 
	 * <p>
	 * This field is used by the iterator and list iterator implementation
	 * returned by the {@code iterator} and {@code listIterator} methods. If the
	 * value of this field changes unexpectedly, the iterator (or list iterator)
	 * will throw a {@code ConcurrentModificationException} in response to the
	 * {@code next}, {@code remove}, {@code previous}, {@code set} or
	 * {@code add} operations. This provides <i>fail-fast</i> behavior, rather
	 * than non-deterministic behavior in the face of concurrent modification
	 * during iteration.
	 * 
	 * <p>
	 * <b>Use of this field by subclasses is optional.</b> If a subclass wishes
	 * to provide fail-fast iterators (and list iterators), then it merely has
	 * to increment this field in its {@code add(int, E)} and
	 * {@code remove(int)} methods (and any other methods that it overrides that
	 * result in structural modifications to the list). A single call to
	 * {@code add(int, E)} or {@code remove(int)} must add no more than one to
	 * this field, or the iterators (and list iterators) will throw bogus
	 * {@code ConcurrentModificationExceptions}. If an implementation does not
	 * wish to provide fail-fast iterators, this field may be ignored.
	 */
	protected transient int modCount = 0;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the list
	 * @exception IllegalArgumentException
	 *                if the specified initial capacity is negative
	 */
	public DoubleList(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		this.elementData = new double[initialCapacity];
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public DoubleList() {
		this(10);
	}

	/**
	 * Trims the capacity of this <tt>DoubleList</tt> instance to be the list's
	 * current size. An application can use this operation to minimize the
	 * storage of an <tt>DoubleList</tt> instance.
	 */
	public void trimToSize() {
		modCount++;
		int oldCapacity = elementData.length;
		if (size < oldCapacity) {
			double[] oldData = elementData;
			elementData = new double[size];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Increases the capacity of this <tt>DoubleList</tt> instance, if
	 * necessary, to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity
	 */
	public void ensureCapacity(int minCapacity) {
		modCount++;
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			double[] oldData = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			// minCapacity is usually close to size, so this is a win:
			elementData = new double[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Returns the number of elements in this list.
	 * 
	 * @return the number of elements in this list
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 * 
	 * @return <tt>true</tt> if this list contains no elements
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns <tt>true</tt> if this list contains the specified element. More
	 * formally, returns <tt>true</tt> if and only if this list contains at
	 * least one element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 * 
	 * @param o
	 *            element whose presence in this list is to be tested
	 * @return <tt>true</tt> if this list contains the specified element
	 */
	public boolean contains(double o) {
		return indexOf(o) >= 0;
	}

	/**
	 * Returns the index of the first occurrence of the specified element in
	 * this list, or -1 if this list does not contain the element. More
	 * formally, returns the lowest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 */
	public int indexOf(double o) {
		for (int i = 0; i < size; i++) {
			if (o == elementData[i]) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified element in this
	 * list, or -1 if this list does not contain the element. More formally,
	 * returns the highest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 */
	public int lastIndexOf(double o) {
		for (int i = size - 1; i >= 0; i--) {
			if (o == elementData[i])
				return i;
		}
		return -1;
	}

	/**
	 * Returns a shallow copy of this <tt>DoubleList</tt> instance. (The
	 * elements themselves are not copied.)
	 * 
	 * @return a clone of this <tt>DoubleList</tt> instance
	 */
	public Object clone() {
		try {
			DoubleList v = (DoubleList) super.clone();
			v.elementData = new double[size];
			System.arraycopy(elementData, 0, v.elementData, 0, size);
			v.modCount = 0;
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence (from first to last element).
	 * 
	 * <p>
	 * The returned array will be "safe" in that no references to it are
	 * maintained by this list. (In other words, this method must allocate a new
	 * array). The caller is thus free to modify the returned array.
	 * 
	 * <p>
	 * This method acts as bridge between array-based and collection-based APIs.
	 * 
	 * @return an array containing all of the elements in this list in proper
	 *         sequence
	 */
	public double[] toArray() {
		double[] result = new double[size];
		System.arraycopy(elementData, 0, result, 0, size);
		return result;
	}

	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence (from first to last element); the runtime type of the returned
	 * array is that of the specified array. If the list size matches the
	 * specified array's size, it is returned therein. Otherwise, a new array is
	 * allocated with the runtime type of the specified array and the size of
	 * this list.
	 * 
	 * @param a
	 *            the array into which the elements of the list are to be
	 *            stored, if it is big enough; otherwise, a new array of the
	 *            same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list
	 * @throws ArrayStoreException
	 *             if the runtime type of the specified array is not a supertype
	 *             of the runtime type of every element in this list
	 * @throws NullPointerException
	 *             if the specified array is null
	 */
	public double[] toArray(double[] a) {
		if (a.length != size) {
			// Make a new array of a's runtime type, but my contents:
			a = new double[size];
		}
		System.arraycopy(elementData, 0, a, 0, size);
		return a;
	}

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index
	 *            index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public double get(int index) {
		rangeCheck(index);

		return elementData[index];
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 * 
	 * @param index
	 *            index of the element to replace
	 * @param element
	 *            element to be stored at the specified position
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public double set(int index, double element) {
		rangeCheck(index);

		double oldValue = elementData[index];
		elementData[index] = element;
		return oldValue;
	}

	/**
	 * Appends the specified element to the end of this list.
	 * 
	 * @param e
	 *            element to be appended to this list
	 * @return <tt>true</tt> (as specified by {@link Collection#add})
	 */
	public boolean add(double e) {
		ensureCapacity(size + 1); // Increments modCount!!
		elementData[size++] = e;
		return true;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted
	 * @param element
	 *            element to be inserted
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public void add(int index, double element) {
		rangeCheckForAdd(index);

		ensureCapacity(size + 1); // Increments modCount!!
		System.arraycopy(elementData, index, elementData, index + 1, size
				- index);
		elementData[index] = element;
		size++;
	}

	/**
	 * Removes the element at the specified position in this list. Shifts any
	 * subsequent elements to the left (subtracts one from their indices).
	 * 
	 * @param index
	 *            the index of the element to be removed
	 * @return the element that was removed from the list
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public double removeAt(int index) {
		rangeCheck(index);

		modCount++;
		double oldValue = elementData[index];

		int numMoved = size - index - 1;
		size--;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index,
					numMoved);
		return oldValue;
	}

	/**
	 * Removes the first occurrence of the specified element from this list, if
	 * it is present. If the list does not contain the element, it is unchanged.
	 * Returns <tt>true</tt> if this list contained the specified element (or
	 * equivalently, if this list changed as a result of the call).
	 * 
	 * @param o
	 *            element to be removed from this list, if present
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(double o) {
		for (int index = 0; index < size; index++)
			if (o == elementData[index]) {
				fastRemove(index);
				return true;
			}

		return false;
	}

	/*
	 * Private remove method that skips bounds checking and does not return the
	 * value removed.
	 */
	private void fastRemove(int index) {
		modCount++;
		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index,
					numMoved);
		size--;
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after
	 * this call returns.
	 */
	public void clear() {
		modCount++;
		size = 0;
	}

	/**
	 * Appends all of the elements in the specified collection to the end of
	 * this list, in the order that they are returned by the specified
	 * collection's Iterator. The behavior of this operation is undefined if the
	 * specified collection is modified while the operation is in progress.
	 * (This implies that the behavior of this call is undefined if the
	 * specified collection is this list, and this list is nonempty.)
	 * 
	 * @param c
	 *            collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public boolean addAll(DoubleList c) {
		double[] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew); // Increments modCount
		System.arraycopy(a, 0, elementData, size, numNew);
		size += numNew;
		return numNew != 0;
	}

	/**
	 * Inserts all of the elements in the specified collection into this list,
	 * starting at the specified position. Shifts the element currently at that
	 * position (if any) and any subsequent elements to the right (increases
	 * their indices). The new elements will appear in the list in the order
	 * that they are returned by the specified collection's iterator.
	 * 
	 * @param index
	 *            index at which to insert the first element from the specified
	 *            collection
	 * @param c
	 *            collection containing elements to be added to this list
	 * @return <tt>true</tt> if this list changed as a result of the call
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public boolean addAll(int index, DoubleList c) {
		rangeCheckForAdd(index);

		double[] a = c.toArray();
		int numNew = a.length;
		ensureCapacity(size + numNew); // Increments modCount

		int numMoved = size - index;
		if (numMoved > 0)
			System.arraycopy(elementData, index, elementData, index + numNew,
					numMoved);

		System.arraycopy(a, 0, elementData, index, numNew);
		size += numNew;
		return numNew != 0;
	}

	/**
	 * Removes from this list all of the elements whose index is between
	 * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive. Shifts any
	 * succeeding elements to the left (reduces their index). This call shortens
	 * the list by {@code (toIndex - fromIndex)} elements. (If
	 * {@code toIndex==fromIndex}, this operation has no effect.)
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if {@code fromIndex} or {@code toIndex} is out of range (
	 *             {@code fromIndex < 0 ||
	 *          fromIndex >= size() ||
	 *          toIndex > size() ||
	 *          toIndex < fromIndex})
	 */
	protected void removeRange(int fromIndex, int toIndex) {
		modCount++;
		int numMoved = size - toIndex;
		System.arraycopy(elementData, toIndex, elementData, fromIndex, numMoved);

		size = size - (toIndex - fromIndex);
	}

	/**
	 * Checks if the given index is in range. If not, throws an appropriate
	 * runtime exception. This method does *not* check if the index is negative:
	 * It is always used immediately prior to an array access, which throws an
	 * ArrayIndexOutOfBoundsException if index is negative.
	 */
	private void rangeCheck(int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * A version of rangeCheck used by add and addAll.
	 */
	private void rangeCheckForAdd(int index) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * Constructs an IndexOutOfBoundsException detail message. Of the many
	 * possible refactorings of the error handling code, this "outlining"
	 * performs best with both server and client VMs.
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}

	/**
	 * Removes from this list all of its elements that are contained in the
	 * specified collection.
	 * 
	 * @param c
	 *            DoubleList containing elements to be removed from this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws ClassCastException
	 *             if the class of an element of this list is incompatible with
	 *             the specified collection (optional)
	 * @throws NullPointerException
	 *             if this list contains a null element and the specified
	 *             collection does not permit null elements (optional), or if
	 *             the specified collection is null
	 * @see DoubleList#contains(double)
	 */
	public boolean removeAll(DoubleList c) {
		return batchRemove(c, false);
	}

	/**
	 * Retains only the elements in this list that are contained in the
	 * specified collection. In other words, removes from this list all of its
	 * elements that are not contained in the specified collection.
	 * 
	 * @param c
	 *            collection containing elements to be retained in this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws ClassCastException
	 *             if the class of an element of this list is incompatible with
	 *             the specified collection (optional)
	 * @throws NullPointerException
	 *             if this list contains a null element and the specified
	 *             collection does not permit null elements (optional), or if
	 *             the specified collection is null
	 * @see DoubleList#contains(double)
	 */
	public boolean retainAll(DoubleList c) {
		return batchRemove(c, true);
	}

	private boolean batchRemove(DoubleList c, boolean complement) {
		final double[] elementData = this.elementData;
		int r = 0, w = 0;
		boolean modified = false;
		try {
			for (; r < size; r++)
				if (c.contains(elementData[r]) == complement)
					elementData[w++] = elementData[r];
		} finally {
			// Preserve behavioral compatibility with AbstractCollection,
			// even if c.contains() throws.
			if (r != size) {
				System.arraycopy(elementData, r, elementData, w, size - r);
				w += size - r;
			}
			if (w != size) {
				modCount += size - w;
				size = w;
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Save the state of the <tt>DoubleList</tt> instance to a stream (that is,
	 * serialize it).
	 * 
	 * @serialData The length of the array backing the <tt>DoubleList</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each a <tt>double</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s)
			throws java.io.IOException {
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		s.defaultWriteObject();

		// Write out array length
		s.writeInt(elementData.length);

		// Write out all elements in the proper order.
		for (int i = 0; i < size; i++)
			s.writeDouble(elementData[i]);

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	private void readObject(java.io.ObjectInputStream s)
			throws java.io.IOException, ClassNotFoundException {
		// Read in size, and any hidden stuff
		s.defaultReadObject();

		// Read in array length and allocate array
		int arrayLength = s.readInt();
		double[] a = elementData = new double[arrayLength];

		// Read in all elements in the proper order.
		for (int i = 0; i < size; i++)
			a[i] = s.readDouble();
	}

	/**
	 * Returns a view of the portion of this list between the specified
	 * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive. (If
	 * {@code fromIndex} and {@code toIndex} are equal, the returned list is
	 * empty.) The returned list is backed by this list, so non-structural
	 * changes in the returned list are reflected in this list, and vice-versa.
	 * The returned list supports all of the optional list operations.
	 * 
	 * <p>
	 * This method eliminates the need for explicit range operations (of the
	 * sort that commonly exist for arrays). Any operation that expects a list
	 * can be used as a range operation by passing a subList view instead of a
	 * whole list. For example, the following idiom removes a range of elements
	 * from a list:
	 * 
	 * <pre>
	 * list.subList(from, to).clear();
	 * </pre>
	 * 
	 * Similar idioms may be constructed for {@link #indexOf(double)} and
	 * {@link #lastIndexOf(double)}, and all of the algorithms in the
	 * {@link Collections} class can be applied to a subList.
	 * 
	 * <p>
	 * The semantics of the list returned by this method become undefined if the
	 * backing list (i.e., this list) is <i>structurally modified</i> in any way
	 * other than via the returned list. (Structural modifications are those
	 * that change the size of this list, or otherwise perturb it in such a
	 * fashion that iterations in progress may yield incorrect results.)
	 * 
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	public DoubleList subList(int fromIndex, int toIndex) {
		subListRangeCheck(fromIndex, toIndex, size);
		return new SubList(this, 0, fromIndex, toIndex);
	}

	static void subListRangeCheck(int fromIndex, int toIndex, int size) {
		if (fromIndex < 0)
			throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
		if (toIndex > size)
			throw new IndexOutOfBoundsException("toIndex = " + toIndex);
		if (fromIndex > toIndex)
			throw new IllegalArgumentException("fromIndex(" + fromIndex
					+ ") > toIndex(" + toIndex + ")");
	}

	private class SubList extends DoubleList implements RandomAccess {
		private final DoubleList parent;
		private final int parentOffset;
		private final int offset;
		private int size;
		private int modCount = 0;

		SubList(DoubleList parent, int offset, int fromIndex, int toIndex) {
			this.parent = parent;
			this.parentOffset = fromIndex;
			this.offset = offset + fromIndex;
			this.size = toIndex - fromIndex;
			this.modCount = DoubleList.this.modCount;
		}

		public double set(int index, double e) {
			rangeCheck(index);
			checkForComodification();
			double oldValue = DoubleList.this.elementData[offset + index];
			DoubleList.this.elementData[offset + index] = e;
			return oldValue;
		}

		public double get(int index) {
			rangeCheck(index);
			checkForComodification();
			return DoubleList.this.elementData[offset + index];
		}

		public int size() {
			checkForComodification();
			return this.size;
		}

		public void add(int index, double e) {
			rangeCheckForAdd(index);
			checkForComodification();
			parent.add(parentOffset + index, e);
			this.modCount = parent.modCount;
			this.size++;
		}

		public double removeAt(int index) {
			rangeCheck(index);
			checkForComodification();
			double result = parent.removeAt(parentOffset + index);
			this.modCount = parent.modCount;
			this.size--;
			return result;
		}

		protected void removeRange(int fromIndex, int toIndex) {
			checkForComodification();
			parent.removeRange(parentOffset + fromIndex, parentOffset + toIndex);
			this.modCount = parent.modCount;
			this.size -= toIndex - fromIndex;
		}

		public boolean addAll(DoubleList c) {
			return addAll(this.size, c);
		}

		public boolean addAll(int index, DoubleList c) {
			rangeCheckForAdd(index);
			int cSize = c.size();
			if (cSize == 0)
				return false;

			checkForComodification();
			parent.addAll(parentOffset + index, c);
			this.modCount = parent.modCount;
			this.size += cSize;
			return true;
		}

		public DoubleList subList(int fromIndex, int toIndex) {
			subListRangeCheck(fromIndex, toIndex, size);
			return new SubList(this, offset, fromIndex, toIndex);
		}

		private void rangeCheck(int index) {
			if (index < 0 || index >= this.size)
				throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}

		private void rangeCheckForAdd(int index) {
			if (index < 0 || index > this.size)
				throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}

		private String outOfBoundsMsg(int index) {
			return "Index: " + index + ", Size: " + this.size;
		}

		private void checkForComodification() {
			if (DoubleList.this.modCount != this.modCount)
				throw new ConcurrentModificationException();
		}
	}
}
