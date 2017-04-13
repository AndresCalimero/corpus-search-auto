package perez.garcia.andres.models;

public enum ShowOnly {
	ALL, HITS, TOKENS, TOTAL;
	
	public static ShowOnly getFromName(String name) {
		return ShowOnly.valueOf(name.toUpperCase());
	}
	
	@Override
	public String toString() {
		if (this.equals(ALL)) {
			return "hits/tokens/total";
		} else {
			return this.name().toLowerCase();
		}
	}
}
