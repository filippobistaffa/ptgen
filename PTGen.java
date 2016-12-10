import java.io.*;
import java.util.*;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class PTGen {

	private static final int DOMAIN_SIZE = 2;
	private static final int BRANCH_SIZE = 2;
	private static final float MAX_VALUE = 10f;
	private static int nodeID = 1;

	static String chain(Graph graph, String id, int l) {

		String oldid = id, newid;
		for (int i = 0; i < l; i++) {
			newid = Integer.valueOf(nodeID++).toString();
			Node node = graph.addNode(newid);
			node.addAttribute("ui.label", newid);
			graph.addEdge(newid + "--" + oldid, newid, oldid);
			oldid = newid;
		}
		return oldid;
	}

	static void subtrees(Graph graph, String id, int h, int l, Random rand) {

		if (h >= 1) {
			String node = chain(graph, id, l);
			if (rand.nextBoolean())
				for (int i = 0; i < BRANCH_SIZE; i++)
					subtrees(graph, node, h - 1, l, rand);
		}
	}

	static Collection<Node> children(Graph pt, int index) {

		Collection<Node> hs = new HashSet<Node>();
		Node node = pt.getNode(index);

		for (Edge edge : node.getEdgeSet())
			if (edge.getOpposite(node).getIndex() > index)
				hs.add(edge.getOpposite(node));

		return hs;
	}

	static void computedescendants(Graph pt, int index, List<Collection<Node>> descendants) {

		for (Node ch : children(pt, index)) {
			computedescendants(pt, ch.getIndex(), descendants);
			descendants.get(index).add(pt.getNode(ch.getId()));
			descendants.get(index).addAll(descendants.get(ch.getIndex()));
		}
	}

	static void printconstraint(PrintWriter wcsp, Edge e, Random rand) {

		int n = (int)Math.pow(DOMAIN_SIZE, 2);
		wcsp.println(String.format("2 %d %d 0 %d", e.getNode0().getIndex(), e.getNode1().getIndex(), n));

		for (int i = 0; i < n; i++)
			wcsp.println(String.format("%d %d %f", i / DOMAIN_SIZE, i % DOMAIN_SIZE,
							       rand.nextFloat() * MAX_VALUE));
	}

	public static void main(String[] args) throws IOException {

		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		int h = Integer.parseInt(args[0]);
		int l = Integer.parseInt(args[1]);
		Random rand = new Random(Integer.parseInt(args[2]));
		Graph pt = new SingleGraph("pt");

		pt.addAttribute("ui.antialias");
		pt.addAttribute("ui.stylesheet", "node { size: 20px; fill-color: #FFFFFF; stroke-width: 2px;" +
						 "stroke-mode: plain;shadow-mode: plain; shadow-width: 0px;" +
						 "shadow-color: #CCC; shadow-offset: 3px, -3px; text-style: bold; }" +
						 "node.root {size: 30px; stroke-width: 2px; text-size: 12; }");

		Node root = pt.addNode("0");
		root.addAttribute("ui.label", "R");
		root.addAttribute("ui.class", "root");

		chain(pt, "0", h * l);
		for (int i = Math.abs(rand.nextInt()) % 2; i < h; i++)
			for (int j = 0; j < BRANCH_SIZE - 1; j++)
				subtrees(pt, Integer.valueOf(i * l).toString(), h - i, l, rand);

		//pt.display();

		Graph primal = Graphs.clone(pt);
		List<Collection<Node>> descendants = new ArrayList<Collection<Node>>();

		for (int i = 0; i < pt.getNodeCount(); i++)
			descendants.add(new HashSet<Node>());

		computedescendants(pt, 0, descendants);

		for (int i = 0; i < pt.getNodeCount(); i++)
			descendants.get(i).removeAll(children(pt, i));

		for (Node node : pt.getEachNode())
			for (Node desc : descendants.get(node.getIndex())) {
				//System.out.println(node.getId() + "--" + desc.getId());
				//if (rand.nextBoolean())
					primal.addEdge(node.getId() + "--" + desc.getId(), node.getId(), desc.getId());
			}

		//primal.display();

		PrintWriter wcsp = new PrintWriter(args[3], "UTF-8");
		wcsp.println(String.format("%s %d %d %d %d", args[2], primal.getNodeCount(), DOMAIN_SIZE,
							     primal.getEdgeCount(), Integer.MAX_VALUE));

		for (int i = 0; i < pt.getNodeCount() - 1; i++)
			wcsp.print(String.format("%d ", DOMAIN_SIZE));
		wcsp.println(String.format("%d", DOMAIN_SIZE));

		for (Edge edge : primal.getEachEdge())
			printconstraint(wcsp, edge, rand);

		wcsp.close();
	}
}
