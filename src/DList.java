import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// A Node is a node in a doubly-linked list.
class Node
{              // class for nodes in a doubly-linked list

    Node prev;              // previous Node in a doubly-linked list
    Node next;              // next Node in a doubly-linked list
//    ProcessLog log;
    //public char data;       // data stored in this Node

    ProcessLog log = new ProcessLog();
    Node()
    {                // constructor for head Node
        prev = this;           // of an empty doubly-linked list
        next = this;

        this.log.date = LocalDate.now();
        this.log.currentTime = log.time.format(DateTimeFormatter.ofPattern("HH:mm a"));
        this.log.action = "";
        this.log.result = "";
        this.log.barcode = "";

    }

    Node(String action, String result, String barcode)
    {
        // constructor for a Node with data
        prev = null;
        next = null;
//        log = new ProcessLog();
        this.log.date = LocalDate.now();
        this.log.currentTime = log.time.format(DateTimeFormatter.ofPattern("HH:mm a"));
        this.log.action = action;
        this.log.result = result;
        this.log.barcode = barcode;

    }

    public void append(Node newNode)
    {  // attach newNode after this Node
        newNode.prev = this;
        newNode.next = next;
        if (next != null)
        {
            next.prev = newNode;
        }
        next = newNode;
    }

    public void insert(Node newNode)
    {  // attach newNode before this Node

        newNode.prev = prev;
        newNode.next = this;
        prev.next = newNode;;
        prev = newNode;

//        Node node2 = node1.next;
//        node1.next = newNode;
//        newNode.next = node2;
//        newNode.prev = node1;
//
//        if (node2 != null)
//        {
//            node2.prev = newNode;
//        }


    }


    public void remove()
    {              // remove this Node
        next.prev = prev;                 // bypass this Node
        prev.next = next;

    }

}

class DList
{

    Node firstNode;
    Node lastNode;

    public DList()
    {
        firstNode = new Node();
        lastNode = new Node();
    }

    public void insert(Node newNode)
    {  // attach newNode before this Node
        if (lastNode == null)
        {
            firstNode = newNode;
            lastNode = newNode;
        }
        else {
            lastNode.next = newNode;
            newNode.prev = lastNode;
            lastNode = newNode;
        }

    }

    public Node find(String str)
    {

        // find Node containing x
        for (Node current = firstNode.next; current != firstNode && current != null; current = current.next)
        {
            if (current.log.barcode.contains(str))
            {        // is x contained in current Node?


                str = "DDL: " + current.log.date.toString() + " - " + current.log.currentTime + " - " +
                        current.log.action + " - " + current.log.result + " - " + current.log.barcode + "\n";

                return current;               // return Node containing x
            }
        }
        str = "DDL: " + str + " not found";
        return null;
    }

    public String toString()
    {
        String str = "";
        if (firstNode.next == firstNode)
        {             // list is empty, only header Node
            return "List Empty";
        }

        for (Node current = firstNode.next; current != firstNode && current != null; current = current.next)
        {
            str = str + current.log.date.toString() + " - " + current.log.currentTime + " - " +
                    current.log.action + " - " + current.log.result + " - " + current.log.barcode + "\n";
        }
        return str;
    }

    public void print()
    {
        String str = "";
        // print content of list
        if (firstNode.next == firstNode)
        {             // list is empty, only header Node
            return;
        }
        for (Node current = firstNode.next; current != firstNode; current = current.next)
        {
           str = current.log.date.toString() + " - " + current.log.currentTime + " - " +
                    current.log.action + " - " + current.log.result + " - " + current.log.barcode;
        }

    }
}
