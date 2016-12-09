import java.io.*;
import java.util.Random;

import org.graphstream.graph.Node;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class PTGen {

	private static final int DOMAIN_SIZE = 2;
	private static final int BRANCH_SIZE = 2;
	static int nodeID = 1;

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
			String node = chain(graph, Integer.valueOf(id).toString(), l);
			if (rand.nextBoolean())
				for (int i = 0; i < BRANCH_SIZE; i++)
					subtrees(graph, node, h - 1, l, rand);
		}
	}

	public static void main(String[] args) throws IOException {

		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

		int h = Integer.parseInt(args[0]);
		int l = Integer.parseInt(args[1]);
		Random rand = new Random(Integer.parseInt(args[2]));
		Graph graph = new SingleGraph("binary");

		graph.addAttribute("ui.antialias");
		graph.addAttribute("ui.stylesheet", "node { size: 20px; fill-color: #FFFFFF; stroke-width: 2px;" +
						    "stroke-mode: plain;shadow-mode: plain; shadow-width: 0px;" +
						    "shadow-color: #CCC; shadow-offset: 3px, -3px; text-style: bold; }" +
						    "node.root {size: 30px; stroke-width: 2px; text-size: 12; }");

		Node root = graph.addNode("r");
		root.addAttribute("ui.label", "R");
		root.addAttribute("ui.class", "root");

		chain(graph, "r", h * l);
		for (int i = 1; i < h; i++)
			for (int j = 0; j < BRANCH_SIZE - 1; j++)
				subtrees(graph, Integer.valueOf(i * l).toString(), h - i, l, rand);

		graph.display();
	}
}
