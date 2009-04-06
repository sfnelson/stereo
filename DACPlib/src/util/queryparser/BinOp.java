package util.queryparser;

public abstract class BinOp implements Filter {
	public BinOp(Filter a, Filter b) {
		this.a = a; this.b = b;
	}
	public final Filter a;
	public final Filter b;
}
