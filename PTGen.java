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

	static void subtrees(Graph graph, String id, int h, Random rand) {

		boolean coin = rand.nextBoolean();

		if (coin) {

			for (int i = 0; i < BRANCH_SIZE; i++) {
				String name = Integer.valueOf(nodeID++).toString();
				Node node = graph.addNode(name);
				node.addAttribute("ui.label", name);
				graph.addEdge(name + "--" + id, name, id);
				if (h > 1) subtrees(graph, name, h - 1, rand);
			}
		}
	}

	public static void main(String[] args) throws IOException {

		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		int h = Integer.parseInt(args[0]);
		int l = Integer.parseInt(args[1]);
		Random rand = new Random(Integer.parseInt(args[2]));
		Graph graph = new SingleGraph("binary");
		graph.addAttribute("ui.stylesheet", "node { size: 20px; fill-color: #FFFFFF; stroke-width: 2px;" +
						    "stroke-mode: plain;shadow-mode: plain; shadow-width: 0px;" +
						    "shadow-color: #CCC; shadow-offset: 3px, -3px; text-style: bold; }" +
						    "node.root {size: 30px; stroke-width: 2px; text-size: 12; }");
		graph.addAttribute("ui.antialias");
		Node root = graph.addNode("r");
		root.addAttribute("ui.label", "R");
		root.addAttribute("ui.class", "root");
		if (h > 1) subtrees(graph, "r", h - 1, rand);
		graph.display();
	}
}
