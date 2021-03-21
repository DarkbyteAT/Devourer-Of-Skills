package net.atlne.dos.utils.collections;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class HACollections {
	
	/**Prints an array.*/
	@SafeVarargs
	public static <T> void print (T... array) {
		for(T obj : array)
			System.out.println(obj);
	}
	
	/**Converts an {@link java.util.ArrayList} to an array of generic T type.*/
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(List<T> list) {
		return (T[]) list.toArray();
	}
	
	/**Converts an {@link java.util.ArrayList} to an array of primitive int types.*/
	public static int[] toIntArray(List<Integer> intList) {
		int[] array = new int[intList.size()];
		Iterator<Integer> iterator = intList.iterator();
		for(int i = 0; i < array.length; i++)
			array[i] = iterator.next().intValue();
		return array;
	}
	
	/**Finds the index of the maximum element in a given array of integers.*/
	public static int maxIndex(int[] array) {
		int maxI = 0;
		for(int i = 1; i < array.length; i++)
			if(array[i] > array[maxI])
				maxI = i;
		return maxI;
	}
	
	/**Finds the index of the minimum element in a given array of integers.*/
	public static int minIndex(int[] array) {
		int minI = 0;
		for(int i = 1; i < array.length; i++)
			if(array[i] < array[minI])
				minI = i;
		return minI;
	}
	
	/**Finds the index of the minimum non-zero element in a given array of integers.*/
	public static int minNonZeroIndex(int[] array) {
		int minI = 0;
		
		for(int i = 0; i < array.length; i++) {
			if(array[i] == 0) {
				continue;
			} else if(array[i] < array[minI] || array[minI] == 0) {
				minI = i;
			}
		}
		
		return minI;
	}
	
	/**Flattens a 2D "matrix"-array into a 1D "vector"-array.*/
	@SuppressWarnings("unchecked")
	public static <T> T[] flattenMatrix(T[][] matrix) {
		if(matrix.length > 0) {
			Object[] vector = new Object[matrix.length * matrix[0].length];
			int vectorStep = 0;
			
			for(int i = 0; i < matrix.length; i++) {
				for(int j = 0; j < matrix[i].length; j++) {
					vector[vectorStep++] = matrix[i][j];
				}
			}
			
			return (T[]) vector;
		} else return (T[]) new Object[0];
	}
	
	/**Trims the dimension of a 1D "vector"-array.*/
	@SuppressWarnings("unchecked")
	public static <T> T[] trimDimension(int newDimension, T[] vector) {
		if(newDimension < vector.length) {
			Object[] trimmed = new Object[newDimension];
			for(int i = 0; i < newDimension; i++)
				trimmed[i] = vector[i];
			return (T[]) trimmed;
		} else return vector;
	}
	
	/**Collects elements from an array, from the start to the end index.*/
	@SuppressWarnings("unchecked")
	public static <T> T[] subset(int start, int end, T[] array) {
		Object[] subset = new Object[(end - start) + 1];
		for(int i = 0; i < subset.length; i++)
			subset[i] = array[start + i];
		return (T[]) subset;
	}
	
	/**Reverses an array.*/
	public static <T> T[] reverse(T[] array) {
		for(int i = 0; i < array.length; i++) {
			T temp = array[i];
			array[i] = array[array.length - (i + 1)];
			array[array.length - (i + 1)] = temp;
		}
		
		return array;
	}
	
	/**Maps a function over an array.*/
	@SuppressWarnings("unchecked")
	public static <M, N> N[] map(Function<M, N> function, M[] array) {
		Object[] mapped = new Object[array.length];
		for(int i = 0; i < array.length; i++)
			mapped[i] = function.apply(array[i]);
		return (N[]) mapped;
	}
	
	/**Finds the average of an List of numbers.*/
	public static int average(List<Integer> nums) {
		int total = 0;
		for(int n : nums)
			total += n;
		return nums.size() > 0 ? total / nums.size() : 0;
	}
}