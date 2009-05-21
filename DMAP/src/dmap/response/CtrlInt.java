package dmap.response;

import java.util.ArrayList;
import java.util.List;

import interfaces.Constants;
import api.Node;
import api.Reader;
import api.Response;
import api.Writer;

public class CtrlInt extends Response {

	private List<Integer> ids;

	public static CtrlInt read(Reader reader) {

		List<Integer> ids = new ArrayList<Integer>(1);

		for (Constants c: reader) {
			switch (c) {
			case dmap_listing:
				Reader list = reader.nextComposite(c);
				for (Constants d: list) {
					switch (d) {
					case dmap_listingitem:
						Reader node = list.nextComposite(d);
						for (Constants e: node) {
							switch (e) {
							case dmap_itemid:
								ids.add(node.nextInteger(e));
							}
						}
					}
				}
			}

		}
		return new CtrlInt(ids);
	}

	public CtrlInt(int id) {
		super(Constants.dacp_controlint, Response.OK);

		ids = new ArrayList<Integer>(1);
		ids.add(id);
	}

	public CtrlInt(List<Integer> ids) {
		super(Constants.dacp_controlint, Response.OK);

		this.ids = ids;
	}

	public void write(Writer writer) {

		List<Node> nodes = new ArrayList<Node>(1);
		for (final int i: ids) {
			nodes.add(new Node() {
				public Constants type() {
					return Constants.dmap_listingitem;
				}
				public void write(Writer writer) {
					writer.appendInteger(Constants.dmap_itemid, i);
					writer.appendBoolean(Constants.dmcp_ik, true);
					writer.appendBoolean(Constants.dmcp_sp, true);
					writer.appendBoolean(Constants.dmcp_sv, true);
					writer.appendBoolean(Constants.dacp_ss, true);
					writer.appendBoolean(Constants.dacp_su, true);
					writer.appendBoolean(Constants.dacp_sg, true);
				}
			});
		}
		writer.appendList(Constants.dmap_listing, (byte)0, nodes);
	}
	
	public Iterable<Integer> ids() {
		return ids;
	}
}
