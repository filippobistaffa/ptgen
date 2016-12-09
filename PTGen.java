import java.io.*;
import java.util.*;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

public class PTGen {

	private static final int DOMAIN_SIZE = 2;
	private static final int BRANCH_SIZE = 2;
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

		pt.display();
	}
}
