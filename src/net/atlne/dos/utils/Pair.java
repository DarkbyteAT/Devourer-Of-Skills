package net.atlne.dos.utils;

public class Pair <M, N> {
	
	/**Stores the key for the pair.*/
	protected M key;
	/**Stores the value for the pair.*/
	protected N value;

	/**Constructor for the Pair, takes in the key and value.*/
	public Pair(M key, N value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		else if (obj == null || getClass() != obj.getClass())
			return false;
		
		Pair<?, ?> other = (Pair<?, ?>) obj;
		
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key)) {
			return false;
		}
		
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value)) {
			return false;
		}
		
		return true;
	}

	public M getKey() {
		return key;
	}

	public N getValue() {
		return value;
	}

	public void setKey(M key) {
		this.key = key;
	}

	public void setValue(N value) {
		this.value = value;
	}
}