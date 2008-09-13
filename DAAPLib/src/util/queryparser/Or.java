package util.queryparser;

import interfaces.Track;

public class Or extends BinOp {
	
	public Or(Filter a, Filter b) {
		super(a, b);
	}
	
	public boolean check(Track t) {
		return a.check(t) || b.check(t);
	}
	
	public String toString() {
		return "(" + a + " , " + b + ")";
	}
}