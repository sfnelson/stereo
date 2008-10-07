package util.serializer;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import reader.DACPResponseParser;

import util.DACPConstants;
import util.node.BooleanNode;
import util.node.ByteNode;
import util.node.Composite;
import util.node.IntegerNode;
import util.node.LongLongNode;
import util.node.LongNode;
import util.node.Node;
import util.node.StringNode;

import writer.DACPResponseGenerator;

public class WriteVisitorTest {

	private static PipedOutputStream out;
	private static PipedInputStream in;
	
	private DACPResponseGenerator visitor;
	
	@BeforeClass
	public static void setUp() throws Exception {
		out = new PipedOutputStream();
		in = new PipedInputStream();
		
		in.connect(out);
	}
	
	@Before
	public void prepare() throws Exception {
		visitor = new DACPResponseGenerator();
	}

	@Test
	public void testVisitBooleanNode() throws Exception {
		
		Node node = new BooleanNode(5, true);
		visitor.visit(node, out);
		
		int length = 9;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 1, 1 };
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitByteNode() throws Exception {
		
		Node node = new ByteNode(5, (byte)'c');
		visitor.visit(node, out);
		
		int length = 9;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 1, (byte)'c' };
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitComposite() throws Exception {
		
		Composite node = new Composite(5);
		
		node.append(new ByteNode(1, (byte)'a'));
		node.append(new IntegerNode(2, 2));
		node.append(new StringNode(3, "hello"));
		node.append(new BooleanNode(4, true));
		node.append(new LongNode(5, 1l));
		
		visitor.visit(node, out);
		
		int length = 67;
		
		//System.out.println(in.available());
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 59, 
				0, 0, 0, 1, 0, 0, 0, 1, 'a',
				0, 0, 0, 2, 0, 0, 0, 4, 0, 0, 0, 2,
				0, 0, 0, 3, 0, 0, 0, 5, 'h', 'e', 'l', 'l', 'o',
				0, 0, 0, 4, 0, 0, 0, 1, 1,
				0, 0, 0, 5, 0, 0, 0, 8, 0, 0, 0, 0, 0, 0, 0, 1
				};
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	
	@Test
	public void complexCompositeTest() throws Exception{
		Composite node = new Composite(5);
		Composite node2 = new Composite(2);
		Composite node3 = new Composite(3);
		
		node2.append(new BooleanNode(6, true));
		node2.append(new BooleanNode(7, true));
		node3.append(new BooleanNode(8, true));
		node3.append(new BooleanNode(9, true));
		
		node.append(node2);	
		node.append(node3);
		
		visitor.visit(node, out);
		
		int length = 60;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 52, 
				0, 0, 0, 2, 0, 0, 0, 18,
				0, 0, 0, 6, 0, 0, 0, 1, 1,
				0, 0, 0, 7, 0, 0, 0, 1, 1,
				0, 0, 0, 3, 0, 0, 0, 18,
				0, 0, 0, 8, 0, 0, 0, 1, 1, 
				0, 0, 0, 9, 0, 0, 0, 1, 1, 
				};
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void complexCompositeTestWithLists() throws Exception{
		
		List<Node> bools1 = new ArrayList<Node>();
		bools1.add(new BooleanNode(6, true));
		bools1.add(new BooleanNode(7, true));
 
		List<Node> bools2 = new ArrayList<Node>();
		bools2.add(new BooleanNode(8, true));
		bools2.add(new BooleanNode(9, true));
		
		List<Node> comps = new ArrayList<Node>();
		
		Composite node2 = new Composite(2);
		for(Node n: bools1){
			node2.append(n);
		}

		Composite node3 = new Composite(3);
		for(Node n: bools2){
			node3.append(n);
		}
		
		comps.add(node2);
		comps.add(node3);
		
		Composite node = new Composite(5);
		for(Node n:comps){
			node.append(n);
		}
		
		
		visitor.visit(node, out);
		
		int length = 60;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 52, 
				0, 0, 0, 2, 0, 0, 0, 18,
				0, 0, 0, 6, 0, 0, 0, 1, 1,
				0, 0, 0, 7, 0, 0, 0, 1, 1,
				0, 0, 0, 3, 0, 0, 0, 18,
				0, 0, 0, 8, 0, 0, 0, 1, 1, 
				0, 0, 0, 9, 0, 0, 0, 1, 1, 
		};
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testJorisVisit() throws Exception {

		Composite parent4 = new Composite(DACPConstants.apso);

		Composite parent3 = new Composite(DACPConstants.mlcl);
		
		Composite parent1 = new Composite(DACPConstants.mlit);
		parent1.append(new StringNode(DACPConstants.ARTIST, "Joris"));
		parent1.append(new StringNode(DACPConstants.NAME, "Cool"));
		
		Composite parent2 = new Composite(DACPConstants.mlit);
		parent2.append(new StringNode(DACPConstants.ARTIST, "William"));
		parent2.append(new StringNode(DACPConstants.NAME, "Extreme"));
		
		parent3.append(parent1);
		parent3.append(parent2);
		
		parent4.append(parent3);
		
		System.out.println(parent4);
		
		visitor.visit(parent4, out);
		int length = 100;		
		byte[] read = new byte[length];
		in.read(read);
		
		
		DACPResponseParser drg = new DACPResponseParser();
		Composite tree = drg.parse(new ByteArrayInputStream(read));
		
		System.out.println(tree);

	}


	@Test
	public void testVisitIntegerNode() throws Exception {
		Node node = new IntegerNode(5, 50);
		visitor.visit(node, out);
		
		int length = 12;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 4, 0, 0, 0, 50 };
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitLongLongNode() throws Exception {
		Node node = new LongLongNode(5, 1, 2, 3, 4);
		visitor.visit(node, out);
		
		int length = 24;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 16, 0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4 };
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitLongNode() throws Exception {
		Node node = new LongNode(5, 72623859790382856l);
		visitor.visit(node, out);
		
		System.out.println(Integer.MAX_VALUE);
		
		int length = 16;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 8, 1, 2, 3, 4, 5, 6, 7, 8 };
		
		in.read(read);
		
		assertTrue(compare(read, check));
	}

	@Test
	public void testVisitStringNode() throws Exception {
		Node node = new StringNode(5, "hello world!");
		visitor.visit(node,out);
		
		int length = 20;
		
		assertTrue(in.available() == length);
		
		byte[] read = new byte[length];
		byte[] check = new byte[] { 0, 0, 0, 5, 0, 0, 0, 12, 'h', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd', '!' };
		in.read(read);
		
		assertTrue(compare(read, check));
	}
	
	public static boolean compare(byte[] from, byte[] to) {
		for (int i = 0; i < from.length; i++) {
			if (from[i] != to[i]) {
				System.out.println(from[i] + " != " + to[i] + " at " + i);
				System.out.println(printOut(from));
				System.out.println(printOut(to));
				return false;
			}
		}
		return true;
	}

	private static String printOut(byte[] byteArray) {
		String ret = "";
		for(byte b:byteArray){
			ret+=b+" ";
		}
		return ret;
	}
}
