//Source:  http://www.newthinktank.com/2013/03/binary-tree-in-java/
// New Think Tank


public class BinaryTree {

    BTNode root;
    String str = "";


    public void addNode(int key, String name) {

        // Create a new Node and initialize it

        BTNode newNode = new BTNode(key, name);

        // If there is no root this becomes root

        if (root == null) {

            root = newNode;

        } else {

            // Set root as the Node we will start
            // with as we traverse the tree

            BTNode focusNode = root;

            // Future parent for our new Node

            BTNode parent;

            while (true) {

                // root is the top parent so we start
                // there

                parent = focusNode;

                // Check if the new node should go on
                // the left side of the parent node

                if (key < focusNode.barcode) {

                    // Switch focus to the left child

                    focusNode = focusNode.leftChild;

                    // If the left child has no children

                    if (focusNode == null) {

                        // then place the new node on the left of it

                        parent.leftChild = newNode;
                        return; // All Done

                    }

                } else { // If we get here put the node on the right

                    focusNode = focusNode.rightChild;

                    // If the right child has no children

                    if (focusNode == null) {

                        // then place the new node on the right of it

                        parent.rightChild = newNode;
                        return; // All Done

                    }

                }

            }
        }

    }

    // All nodes are visited in ascending order
    // Recursion is used to go to one node and
    // then go to its child nodes and so forth

    public void inOrderTraverseTree(BTNode focusNode) {

        if (focusNode != null) {

            // Traverse the left node
            inOrderTraverseTree(focusNode.leftChild);

            // Visit the currently focused on node
            str = str + focusNode.barcode + " - " + focusNode.title + "\n";

            // Traverse the right node
            inOrderTraverseTree(focusNode.rightChild);

        }

    }

    public void preorderTraverseTree(BTNode focusNode) {

        if (focusNode != null) {

            //System.out.println(focusNode);
            str = str + focusNode.barcode + " - " + focusNode.title + "\n";
            preorderTraverseTree(focusNode.leftChild);
            preorderTraverseTree(focusNode.rightChild);

        }

    }

    public void postOrderTraverseTree(BTNode focusNode) {

        if (focusNode != null) {

            postOrderTraverseTree(focusNode.leftChild);
            postOrderTraverseTree(focusNode.rightChild);
            str = str + focusNode.barcode + " - " + focusNode.title + "\n";

        }

    }

    public BTNode findNode(int barcode) {

        // Start at the top of the tree

        BTNode focusNode = root;

        if (root == null)
        {
            return null;
        }

        // While we haven't found the Node
        // keep looking

        while (focusNode.barcode != barcode) {

            // If we should search to the left

            if (barcode < focusNode.barcode) {

                // Shift the focus Node to the left child

                focusNode = focusNode.leftChild;

            } else {

                // Shift the focus Node to the right child

                focusNode = focusNode.rightChild;

            }

            // The node wasn't found

            if (focusNode == null)
                return null;

        }

        return focusNode;

    }

//    public static void main(String[] args) {
//
//        BinaryTree theTree = new BinaryTree();
//
//        theTree.addNode(50, "Boss");
//
//        theTree.addNode(25, "Vice President");
//
//        theTree.addNode(15, "Office Manager");
//
//        theTree.addNode(30, "Secretary");
//
//        theTree.addNode(75, "Sales Manager");
//
//        theTree.addNode(85, "Salesman 1");
//
//        // Different ways to traverse binary trees
//
//        // theTree.inOrderTraverseTree(theTree.root);
//
//        // theTree.preorderTraverseTree(theTree.root);
//
//        // theTree.postOrderTraverseTree(theTree.root);
//
//        // Find the node with key 75
//
//        System.out.println("\nNode with the key 75");
//
//        System.out.println(theTree.findNode(75));
//
//    }
}

class BTNode {

    int barcode;
    String title;

    BTNode leftChild;
    BTNode rightChild;

    String str;

    BTNode(int key, String name) {

        this.barcode = key;
        this.title = name;

    }

//    public String toString() {
//
//       // return barcode + " - " + title;
//
//        /*
//         * return name + " has the key " + key + "\nLeft Child: " + leftChild +
//         * "\nRight Child: " + rightChild + "\n";
//         */
//
//
////        if (firstNode.next == firstNode)
////        {             // list is empty, only header Node
////            return "List Empty";
////        }
//
//
//
//        str =  str + barcode + " - " + title + "\n";
//
//
//
//        return str;
//    }

}
