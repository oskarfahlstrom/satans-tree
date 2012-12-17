import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class JTreeTestaren {

	static JFrame frame;
	static DefaultMutableTreeNode root = new DefaultMutableTreeNode("colors");
	static JTree tree;
	static JScrollPane panel;
	static JTextField inputField;
	static String inputText;

	static boolean allowUpdate = true;

	/**
	 * @author Jacob Bergvall, Oskar Fahlström
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// For thread safety.
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				showGUI();
			}
		});
	}

	private static void showGUI() {
		frame = new JFrame("Rubbish JTree");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add the container to our window
		addComponents(frame.getContentPane());

		// Make stuff visible
		frame.pack();
		frame.setVisible(true);
	}

	public static void addComponents(Container pane) {
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));

		DefaultMutableTreeNode light = new DefaultMutableTreeNode("light");
		root.add(light);
		String lightLabels[] = { "white", "yellow", "orange" };
		for (int i = 0; i < lightLabels.length; i++)
			light.add(new DefaultMutableTreeNode(lightLabels[i]));

		DefaultMutableTreeNode dark = new DefaultMutableTreeNode("dark");

		root.add(dark);
		String darkLabels[] = { "red", "green", "blue" };
		for (int i = 0; i < darkLabels.length; i++)
			dark.add(new DefaultMutableTreeNode(darkLabels[i]));

		DefaultMutableTreeNode dark2 = new DefaultMutableTreeNode("dark");
		root.add(dark2);
		String dark2Labels[] = { "purple", "black" };
		for (int i = 0; i < dark2Labels.length; i++)
			dark2.add(new DefaultMutableTreeNode(dark2Labels[i]));

		tree = new JTree(root);
		panel = new JScrollPane(tree);
		inputField = new JTextField("/");
		inputField.setAlignmentY(Component.CENTER_ALIGNMENT);

		inputField.getDocument().addDocumentListener(new DocumentListener() {
			private void updatePanel() {
				panel.revalidate();
				// panel.setSize(panel.getPreferredSize());

				inputText = inputField.getText().toString();
				String divider = ("/");

				if (inputText.contains(divider)) {
					inputField.setBackground(Color.WHITE);
					inputField.setForeground(Color.BLACK);

					if (inputText.charAt(0) != '/')
						inputText = 1 / 0 + "";
					inputText = inputText.substring(1);
					StringTokenizer tk = new StringTokenizer(inputText, divider);
					DefaultMutableTreeNode deepestChosen = root;
					while (tk.hasMoreTokens()) {
						String nextPath = tk.nextToken();
						DefaultMutableTreeNode temp = (DefaultMutableTreeNode) deepestChosen
								.clone();
						deepestChosen = BFS(nextPath, deepestChosen);
						if (deepestChosen == null) {
							deepestChosen = temp;
							System.out.println(deepestChosen.getUserObject()
									.toString());
							System.out.println(inputText);
							if (!deepestChosen.getUserObject().toString()
									.startsWith(inputText)) {
								inputField.setBackground(Color.RED);
								inputField.setForeground(Color.WHITE);
							}
							break;
						}
					}

					// Use deepestChosen to select the node in the tree
					tree.setExpandsSelectedPaths(true);
					tree.expandPath(new TreePath(deepestChosen.getPath()));
					tree.setSelectionPath(new TreePath(deepestChosen.getPath()));

				} else {
					inputField.setBackground(Color.RED);
					inputField.setForeground(Color.WHITE);
				}

			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				allowUpdate = false;
				updatePanel();
				allowUpdate = true;
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				allowUpdate = false;
				updatePanel();
				allowUpdate = true;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				allowUpdate = false;
				updatePanel();
				allowUpdate = true;
			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if (!allowUpdate)
					return;
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();

				// Shows the path from the root to the chosen node
				String path = "";
				for (TreeNode pathNode : node.getPath()) {
					path = path
							+ "/"
							+ ((DefaultMutableTreeNode) pathNode)
									.getUserObject().toString();

				}
				// If the node has a child, add a "/" to the end of the path
				if (!node.isLeaf()) {
					path = path + "/";
				}
				// Update the text field with the chosen path
				inputField.setText(path);
				allowUpdate = true;
			}
		});

		pane.add(inputField);
		pane.add(panel);
	}

	static DefaultMutableTreeNode BFS(String nextPath,
			DefaultMutableTreeNode root) {

		@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> iter = root
				.breadthFirstEnumeration();
		while (iter.hasMoreElements()) {
			DefaultMutableTreeNode node = iter.nextElement();
			// System.out.println(node.toString());
			if (node.toString().equals(nextPath)) {
				return node;
			}
		}
		return null;
	}

	public static TreePath getPath(TreeNode treeNode) {
		List<Object> nodes = new ArrayList<Object>();
		if (treeNode != null) {
			nodes.add(treeNode);
			treeNode = treeNode.getParent();
			while (treeNode != null) {
				nodes.add(0, treeNode);
				treeNode = treeNode.getParent();
			}
		}

		return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
	}
}
