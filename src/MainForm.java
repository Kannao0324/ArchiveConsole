import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.util.HashMap;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.commons.lang3.StringUtils;

/****************************************************************
 PROGRAM:   Archive Console
 AUTHOR:    Kanna Ono
 DUE DATE:  08/11/2024

 FUNCTION:  Archive Console application manages the data of the CD archive list
 and communicate with the robot to perform the operations received from the main form.
 The user can see the CD list,add, update, sort, filter and search the list.
 The binary tree and hashMap data can be created and display in the selected traversal.
 The user can send a request to add, retrieve, return, remove and sort the CD
 to a robot through the network.

 INPUT:     sample CD list data in txt file provided from the client

 OUTPUT:   Update the existing CD list data in the txt file and Save a HashMap in the txt file

 NOTES:     any relevant information that would be of
 additional help to someone looking at the program.
 ****************************************************************/


/**
 *  MainForm Class for containing variables and different data types
 *  Add extension of JFrame and implementing ActionListener
 */
public class MainForm extends JFrame implements ActionListener {
    SpringLayout layout = new SpringLayout();
    SpringLayout tableLayout = new SpringLayout();
    FileManager fileManager = new FileManager();
    BinaryTree theTree = new BinaryTree();

    SpringLayout itemLayout = new SpringLayout();
    SpringLayout processLayout = new SpringLayout();
    SpringLayout automatedLayout = new SpringLayout();

    // Create all the label in the application
    JLabel lblLabel, lblSearch,lblArchive,lblSort,
            lblTitle,lblId,lblAuthor,lblSection,lblX,lblY,lblBarCode,lblDescription,
            lblAutomation, lblSortSection,
            lblProcessLog,lblDisplay,lblHashmap,lblFind, lblMessage;

    JTextArea txaDescription,txaOutput;

    // Create a recorded text filed
    JTextField txtSearch,txtTitle,txtId,txtAuthor,txtSection,txtX,txtY,txtBarcode,txtSortSection,txtFind;

    // Create all the buttons
    JButton btnSearch, btnByTitle, btnByAuthor,btnByBarcode,
            btnNewItem,btnSaveUpdate,
            btnRetrieve,btnRemove,btnReturn,btnAdd,btnRandom,btnMostly,btnReverse,
            btnExit,btnProcess,btnPreOrder,btnInOrder,btnPostOrder,btnGraphical,btnSave,btnDisplay,
            btnConnect;

    // Create all the table
    JTable tblArchiveCD;

    MyModel dataModel;

    static ArrayList<Object[]> CDValueList;
    static String[] columnNames;

    int maxID = 0;

   // CDModel dataModel;
   DList processList = new DList();

    HashMap<String,Integer> myTable = new HashMap<>();

    JPanel plTable,plItem,plProcess,plAutomation;

    JButton btnMainConnect;
    JLabel lblAutoMessage;


    //CHAT RELATED ---------------------------
    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread1 client = null;
    private String serverName = "localhost";
    private int serverPort = 4444;

    //AutomationForm RELATED ---------------------------
    AutomationForm autoForm;


    /**
     * Create a constructor to set up the window, layout, all the components
     * Add reading to the file and closing function
     * @throws HeadlessException
     */
    public MainForm() throws HeadlessException {


        Runtime.getRuntime().addShutdownHook(new Thread()
                                             {
                                                 public void run()
                                                 {
                                                     System.out.println("shutdown");
                                                     try {
                                                         Thread.sleep(2000);
                                                         System.out.println("application closed");
                                                     } catch (InterruptedException e) {
                                                         throw new RuntimeException(e);
                                                     }
                                                 }
                                             }



        );
        setSize(1000,700);

        setLocation(0,0);
        setTitle("CD Archive Prototype");
        setLayout(layout);
        setResizable(false);

        // When window closes, stop running
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                int confirmed = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit the program?", "Exit Program Message Box",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                else{
                    setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
                }

            }
        });

        // Create an object to change JLabel labelColor
        Color labelColor = Color.decode("#2471A3");
        Color panelColor = Color.decode("#a3b8c8");

        // The application label
        lblLabel = UIBuilderLibrary.BuildJLabelWithNorthWestAnchor("  Archive Console",0,0,layout,this);
        lblLabel.setFont(new Font("Arial", Font.BOLD, 20));
        lblLabel.setPreferredSize(new Dimension(1000,30));
        lblLabel.setBackground(labelColor);
        lblLabel.setForeground(Color.white);
        lblLabel.setOpaque(true);
        add(lblLabel);

        lblSearch = UIBuilderLibrary.BuildJLabelInlineBelow("  Search String: ",5,layout, lblLabel);
        lblSearch.setVerticalAlignment(SwingConstants.CENTER);
        add(lblSearch);

        txtSearch = UIBuilderLibrary.BuildJTextFieldInlineToRight(15,5,layout,lblSearch);
        add(txtSearch);

        btnSearch = UIBuilderLibrary.BuildJButtonInlineToRight(80,20,"Search", 10, this, layout, txtSearch);
        add(btnSearch);

        // Table components
        plTable = new JPanel(tableLayout);
        plTable.setPreferredSize(new Dimension(620,265));
        plTable.setBackground(panelColor);
        layout.putConstraint(SpringLayout.WEST, plTable,10,SpringLayout.WEST,this);
        layout.putConstraint(SpringLayout.NORTH, plTable,60,SpringLayout.NORTH,this);
        add(plTable);

        lblArchive = UIBuilderLibrary.BuildJLabelWithNorthWestAnchor("Archive CDs",0,10,tableLayout, this);
        lblArchive.setFont(new Font("Arial", Font.BOLD, 16));
        lblArchive.setPreferredSize(new Dimension(620,20));
        lblArchive.setHorizontalAlignment(SwingConstants.CENTER);
        plTable.add(lblArchive);

//        // Create sample column names
//        String[] columnNames =
//                { "ID","Title", "Author", "Section","X","Y","Barcode","Description","On-Loan"};
//
//        // Create sample list data
//        CDValues = new ArrayList();
//        CDValues.add(new Object[] {"1","Busn History 2015","Management","B","1","57","43783278","Business History - tax records, events and achievements in 2015",false});
//        CDValues.add(new Object[] {"2","Busn History 2015","Management","B","1","57","43783278","Business History - tax records, events and achievements in 2015",true});

        // Retrieve header data from the filemanager
       columnNames = fileManager.ReadHeaderFromFile();
        // Retrieve body data from the filemanager
       CDValueList = fileManager.ReadBodyFromFile();
        // Store the retrieved data from file to Model class
        dataModel = new MyModel(CDValueList,columnNames);
        // Create JTable and store Model data into the Jtable
        tblArchiveCD = new JTable(dataModel);
        tblArchiveCD.isForegroundSet();
        tblArchiveCD.setShowHorizontalLines(true);
        tblArchiveCD.setRowSelectionAllowed(true);
        tblArchiveCD.setColumnSelectionAllowed(true);
        tblArchiveCD.setAutoCreateRowSorter(true);

        //When the table is clicked, display the selected data into textfileds
        AddMouseListenerToJTable(tblArchiveCD);
        plTable.add(tblArchiveCD);

        JScrollPane tableScroll = new JScrollPane(tblArchiveCD);
        tableScroll.setPreferredSize(new Dimension(610,180));
        tableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableLayout.putConstraint(SpringLayout.WEST,tableScroll,5,SpringLayout.WEST,plTable);
        tableLayout.putConstraint(SpringLayout.NORTH,tableScroll,50,SpringLayout.NORTH,plTable);
        plTable.add(tableScroll);

        // Sort label and buttons in the Jtable panel
        lblSort = UIBuilderLibrary.BuildJLabelInlineBelow("Sort: ",5,tableLayout, tableScroll);
        lblSort.setPreferredSize(new Dimension(35,25));
        lblSort.setVerticalAlignment(SwingConstants.CENTER);
        plTable.add(lblSort);

        btnByTitle = UIBuilderLibrary.BuildJButtonInlineToRight(100,25,"By Title", 10, this, tableLayout, lblSort);
        plTable.add(btnByTitle);
        btnByAuthor = UIBuilderLibrary.BuildJButtonInlineToRight(100,25,"By Author", 2, this, tableLayout, btnByTitle);
        plTable.add(btnByAuthor);
        btnByBarcode = UIBuilderLibrary.BuildJButtonInlineToRight(100,25,"By Barcode", 40, this, tableLayout, btnByAuthor);
        plTable.add(btnByBarcode);

        // Item Panel
        plItem = new JPanel(itemLayout);
        plItem.setPreferredSize(new Dimension(320,265));
        plItem.setBackground(panelColor);
        layout.putConstraint(SpringLayout.WEST, plItem,650,SpringLayout.WEST,this);
        layout.putConstraint(SpringLayout.NORTH, plItem,60,SpringLayout.NORTH,this);
        add(plItem);


        lblTitle = UIBuilderLibrary.BuildJLabelWithNorthWestAnchor("Title: ",10,10,itemLayout, this);
        plItem.add(lblTitle);
        lblAuthor = UIBuilderLibrary.BuildJLabelInlineBelow("Author: ",10,itemLayout, lblTitle);
        plItem.add(lblAuthor);
        lblSection = UIBuilderLibrary.BuildJLabelInlineBelow("Section: ",10,itemLayout, lblAuthor);
        plItem.add(lblSection);
        lblX = UIBuilderLibrary.BuildJLabelInlineBelow("X: ",10,itemLayout, lblSection);
        plItem.add(lblX);
        lblY = UIBuilderLibrary.BuildJLabelInlineBelow("Y: ",10,itemLayout, lblX);
        plItem.add(lblY);
        lblBarCode = UIBuilderLibrary.BuildJLabelInlineBelow("Barcode: ",10,itemLayout, lblY);
        plItem.add(lblBarCode);
        lblDescription = UIBuilderLibrary.BuildJLabelInlineBelow("Description: ",10,itemLayout, lblBarCode);
        plItem.add(lblDescription);

        txtTitle = UIBuilderLibrary.BuildJTextFieldInlineToRight(15,50,itemLayout,lblTitle);
        txtTitle.setPreferredSize(new Dimension(15,20));
        plItem.add(txtTitle);
        lblId = UIBuilderLibrary.BuildJLabelInlineToRight("ID: ",5,itemLayout,txtTitle);
        lblId.setPreferredSize(new Dimension(20,20));
        lblId.setVerticalAlignment(SwingConstants.CENTER);
        plItem.add(lblId);
        txtId = UIBuilderLibrary.BuildJTextFieldInlineToRight(2,3,itemLayout,lblId);
        txtId.setEditable(false);
        plItem.add(txtId);


        txtAuthor = UIBuilderLibrary.BuildJTextFieldInlineBelow(15,6,itemLayout,txtTitle);
        txtAuthor.setPreferredSize(new Dimension(15,20));
        plItem.add(txtAuthor);
        txtSection = UIBuilderLibrary.BuildJTextFieldInlineBelow(7,6,itemLayout,txtAuthor);
        txtSection.setPreferredSize(new Dimension(7,20));
        plItem.add(txtSection);
        txtX = UIBuilderLibrary.BuildJTextFieldInlineBelow(7,6,itemLayout,txtSection);
        txtX.setPreferredSize(new Dimension(7,20));
        plItem.add(txtX);
        txtY = UIBuilderLibrary.BuildJTextFieldInlineBelow(7,6,itemLayout,txtX);
        txtY.setPreferredSize(new Dimension(7,20));
        plItem.add(txtY);
        txtBarcode = UIBuilderLibrary.BuildJTextFieldInlineBelow(15,6,itemLayout,txtY);
        txtBarcode.setPreferredSize(new Dimension(15,20));
        plItem.add(txtBarcode);

        txaDescription = new JTextArea();
        txaDescription.setLineWrap(true);
        //txaDescription.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txaDescription);
        scroll.setPreferredSize(new Dimension(220,40));
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemLayout.putConstraint(SpringLayout.WEST,scroll,0,SpringLayout.WEST,txtBarcode);
        itemLayout.putConstraint(SpringLayout.NORTH,scroll,10,SpringLayout.SOUTH,txtBarcode);
        plItem.add(scroll);

        btnNewItem = UIBuilderLibrary.BuildJButtonInlineBelow(100,25,"New Item", 50, this, itemLayout, lblDescription);
        plItem.add(btnNewItem);
        btnSaveUpdate = UIBuilderLibrary.BuildJButtonInlineToRight(120,25,"Save/Update", 80, this, itemLayout, btnNewItem);
        plItem.add(btnSaveUpdate);




        // Automation Panel
        plAutomation = new JPanel(automatedLayout);
        plAutomation.setPreferredSize(new Dimension(320,265));
        plAutomation.setBackground(panelColor);
        layout.putConstraint(SpringLayout.WEST, plAutomation,0,SpringLayout.WEST,plItem);
        layout.putConstraint(SpringLayout.NORTH, plAutomation,20,SpringLayout.SOUTH,plItem);
        add(plAutomation);

        lblAutomation = UIBuilderLibrary.BuildJLabelWithNorthWestAnchor("Automation Action Request for the Item above: ",30,10,automatedLayout, this);
        plAutomation.add(lblAutomation);

        btnRetrieve = UIBuilderLibrary.BuildJButtonInlineBelow(130,25,"Retrieve", 10, this, automatedLayout, lblAutomation);
        btnRetrieve.setHorizontalAlignment(SwingConstants.CENTER);
        plAutomation.add(btnRetrieve);
        btnRemove = UIBuilderLibrary.BuildJButtonInlineToRight(130,25,"Remove", 10, this, automatedLayout, btnRetrieve);
        plAutomation.add(btnRemove);
        btnReturn = UIBuilderLibrary.BuildJButtonInlineBelow(130,25,"Return", 10, this, automatedLayout, btnRetrieve);
        plAutomation.add(btnReturn);
        btnAdd = UIBuilderLibrary.BuildJButtonInlineToRight(130,25,"Add to Collection", 10, this, automatedLayout, btnReturn);
        plAutomation.add(btnAdd);

        lblSortSection = UIBuilderLibrary.BuildJLabelInlineBelow("Sort Section: ",10,automatedLayout, btnReturn);
        lblSortSection.setVerticalAlignment(SwingConstants.CENTER);
        plAutomation.add(lblSortSection);
        txtSortSection = UIBuilderLibrary.BuildJTextFieldInlineToRight(7,6,automatedLayout,lblSortSection);
        txtSortSection.setPreferredSize(new Dimension(7,20));
        plAutomation.add(txtSortSection);

        btnRandom = UIBuilderLibrary.BuildJButtonInlineBelow(170,25,"Random Collection Sort", 15, this, automatedLayout, txtSortSection);
        plAutomation.add(btnRandom);
        btnMostly = UIBuilderLibrary.BuildJButtonInlineBelow(170,25,"Mostly Sorted Sort", 5, this, automatedLayout, btnRandom);
        plAutomation.add(btnMostly);
        btnReverse = UIBuilderLibrary.BuildJButtonInlineBelow(170,25,"Reverse Order Sort", 5, this, automatedLayout, btnMostly);
        plAutomation.add(btnReverse);

        lblAutoMessage = UIBuilderLibrary.BuildJLabelInlineBelow("Server message: ",200,automatedLayout, lblAutomation);
        plAutomation.add(lblAutoMessage);

//       btnMainConnect = UIBuilderLibrary.BuildJButtonInlineBelow(170,25,"Connect", 5, this, automatedLayout, btnReverse);
//       plAutomation.add(btnMainConnect);

        btnExit = UIBuilderLibrary.BuildJButtonInlineBelow(320,25,"Exit", 5, this, layout, plAutomation);
        add(btnExit);

        // Process log area
        plProcess = new JPanel(processLayout);
        plProcess.setPreferredSize(new Dimension(620,265));
        plProcess.setBackground(panelColor);
        layout.putConstraint(SpringLayout.WEST, plProcess,0,SpringLayout.WEST,plTable);
        layout.putConstraint(SpringLayout.NORTH, plProcess,20,SpringLayout.SOUTH,plTable);
        add(plProcess);

        lblProcessLog = UIBuilderLibrary.BuildJLabelWithNorthWestAnchor("Process Log: ",10,5,processLayout, this);
        lblProcessLog.setPreferredSize(new Dimension(100,25));
        lblProcessLog.setVerticalAlignment(SwingConstants.CENTER);
        plProcess.add(lblProcessLog);
        btnProcess = UIBuilderLibrary.BuildJButtonInlineToRight(110,25,"Process Log", 390, this, processLayout, lblProcessLog);
        plProcess.add(btnProcess);

        txaOutput = new JTextArea();
        txaOutput.setLineWrap(true);
        //txaDescription.setWrapStyleWord(true);
        JScrollPane outputScroll = new JScrollPane(txaOutput);
        outputScroll.setPreferredSize(new Dimension(600,150));
        outputScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        processLayout.putConstraint(SpringLayout.WEST,outputScroll,0,SpringLayout.WEST,lblProcessLog);
        processLayout.putConstraint(SpringLayout.NORTH,outputScroll,5,SpringLayout.SOUTH,lblProcessLog);
        plProcess.add(outputScroll);

        lblDisplay = UIBuilderLibrary.BuildJLabelInlineBelow("Display Binary Tree: ",12,processLayout, outputScroll);
        lblDisplay.setPreferredSize(new Dimension(115,25));
        lblDisplay.setVerticalAlignment(SwingConstants.CENTER);
        plProcess.add(lblDisplay);

        btnPreOrder = UIBuilderLibrary.BuildJButtonInlineToRight(100,25,"Pre-Order", 0, this, processLayout, lblDisplay);
        plProcess.add(btnPreOrder);
        btnInOrder = UIBuilderLibrary.BuildJButtonInlineToRight(100,25,"In-Order", 5, this, processLayout, btnPreOrder);
        plProcess.add(btnInOrder);
        btnPostOrder = UIBuilderLibrary.BuildJButtonInlineToRight(100,25,"Post-Order", 5, this, processLayout, btnInOrder);
        plProcess.add(btnPostOrder);
        btnGraphical = UIBuilderLibrary.BuildJButtonInlineToRight(100,25,"Graphical", 5, this, processLayout, btnPostOrder);
        plProcess.add(btnGraphical);

        lblHashmap = UIBuilderLibrary.BuildJLabelInlineBelow("HashMap / Set: ",5,processLayout, lblDisplay);
        lblHashmap.setPreferredSize(new Dimension(115,25));
        lblHashmap.setVerticalAlignment(SwingConstants.CENTER);
        lblHashmap.setHorizontalAlignment(SwingConstants.RIGHT);
        plProcess.add(lblHashmap);

        btnSave = UIBuilderLibrary.BuildJButtonInlineToRight(100,25,"Save", 0, this, processLayout, lblHashmap);
        plProcess.add(btnSave);
        btnDisplay = UIBuilderLibrary.BuildJButtonInlineToRight(100,25,"Display", 5, this, processLayout, btnSave);
        plProcess.add(btnDisplay);

        lblFind = UIBuilderLibrary.BuildJLabelInlineToRight("Find Bar Code: ",95,processLayout, btnDisplay);
        lblFind.setPreferredSize(new Dimension(85,25));
        lblFind.setVerticalAlignment(SwingConstants.CENTER);
        plProcess.add(lblFind);
        txtFind = UIBuilderLibrary.BuildJTextFieldInlineToRight(8,6,processLayout,lblFind);
        txtFind.setPreferredSize(new Dimension(15,25));
        txtFind.addActionListener(this);
        plProcess.add(txtFind);

        // Automation form opening and connecting to the main form when the main form opening
        getParameters();
        autoForm = new AutomationForm();

        addWindowListener((new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Connect network for the server
                connect(serverName, serverPort);
            }
        }));

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnSearch)
        {
            TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(tblArchiveCD.getModel());
            tblArchiveCD.setRowSorter(rowSorter);
            String search = txtSearch.getText();

            if (search.trim().isEmpty())
            {
                rowSorter.setRowFilter(null);

            }
            else
            {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + search));
            }
        }

        if (e.getSource() == btnNewItem)
        {
            ClearAllTextFields();
        }


        if (e.getSource() == btnByTitle)
        {
            int size = CDValueList.size();
            // Insertion Sort
            for (int i = 0; i < size; i++)
            {
                    //Create a temp array to store the one of the array's value
                    Object[] keyValue = CDValueList.get(i);
                    int j = i - 1;
                    while ((j >= 0) && (((Comparable) CDValueList.get(j)[1]).compareTo(keyValue[1]) > 0))
                     {
                        CDValueList.set(j + 1, CDValueList.get(j));
                         j = j - 1;
                     }
                         CDValueList.set(j + 1, keyValue);
                         repaint();
            }

            //Binary Tree
                //Clear the root
                theTree.root = null;

                //Loop the row in the Arraylist
                for (int i = 0; i < size; i++) {
                    //Get the barcode value
                    int barcode = Integer.parseInt(CDValueList.get(i)[6].toString());
                    //Get the title value
                    String title = String.valueOf(CDValueList.get(i)[1]);
                    //Add these value in a binary tree and create the tree
                    theTree.addNode(barcode, title);
                    txaOutput.setText("Binary Tree Created\n\n");
                }
        }

        if (e.getSource() == btnByAuthor)
        {
            int size = CDValueList.size();
            //Merge Sort
            MergeSort(CDValueList,0,size -1);
            repaint();
        }

        if (e.getSource() == btnByBarcode)
        {
            int size = CDValueList.size();
            // Bubble Sort
            for(int j=0; j<size; j++)
            {
                for(int i=j+1; i<size; i++)
                {
                    if((CDValueList.get(i)[6]).toString().compareToIgnoreCase(CDValueList.get(j)[6].toString())<0)
                    {
                        //Create a temp array to store the one of the array's value
                        Object[] words = CDValueList.get(j);
                        CDValueList.set(j, CDValueList.get(i));
                        CDValueList.set(i, words);

                    }
                }
                repaint();
            }

        }

        if (e.getSource() == btnSaveUpdate)
        {
            int size = CDValueList.size();
            //Loop through the arraylist to find the highest ID
            for (int i = 0; i < size; i++) {
                //Get the current ID value
                int currentID = Integer.parseInt(CDValueList.get(i)[0].toString());
                //Update maxID if the currentID is higher
                if (currentID > maxID)
                {
                    maxID = currentID;
                }
            }

            String id;
            int selectedRow = tblArchiveCD.getSelectedRow();
            // Check if it is a new entry
            if (txtId.getText().equals("") && selectedRow == -1)
            {
                //Increment the MaxID for a new entry
                maxID++;
                id = String.valueOf(maxID);
                txtId.setText(id);
            }
            else {
                // Retrieve the existing ID
                id = txtId.getText();
            }

            //Validate text fields
            if (TextFieldValidation()) return;

            // Retrieve user entries from each textfields
            String title = txtTitle.getText();
            String author = txtAuthor.getText();
            String section = txtSection.getText();
            String x = txtX.getText();
            String y = txtY.getText();
            String barcode = txtBarcode.getText();
            String description = txaDescription.getText();
            //Create an array for the row data
            Object[] rowData = {id,title,author,section,x,y,barcode,description,"No"};
            //Save or update the entry
            dataModel.addOrUpdateRow(rowData,selectedRow);

            try {

                // Sort back by ID with insertion Sort
                for (int i = 0; i < size; i++)
                {
                    //Create a temp array to store the one of the array's value
                    Object[] keyValue = CDValueList.get(i);
                    // Covert ID to an Integer
                    int keyId = Integer.parseInt(keyValue[0].toString());

                    int j = i - 1;
                    while (j >= 0 && Integer.parseInt(CDValueList.get(j)[0].toString()) > keyId)
                    {
                        CDValueList.set(j + 1, CDValueList.get(j));
                        j = j - 1;
                    }
                    CDValueList.set(j + 1, keyValue);
                    repaint();
                }

                fileManager.WriteToFile(columnNames,CDValueList);
                JOptionPane.showMessageDialog(this,"Saved successfully.");
                ClearAllTextFields();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (e.getSource() == btnProcess)
        {
            txaOutput.setText(processList.toString());
        }

        if (e.getSource() == btnPreOrder)
        {
            if (theTree.root == null)
            {
                txaOutput.setText("No Binary Tree Created:\n\n");
                return;
            }
            theTree.str = "";
            theTree.preorderTraverseTree(theTree.root);
            txaOutput.setText(theTree.str);

        }
        if (e.getSource() == btnInOrder)
        {
            if (theTree.root == null)
            {
                txaOutput.setText("No Binary Tree Created:\n\n");
                return;
            }
            theTree.str = "";
            theTree.inOrderTraverseTree(theTree.root);
            txaOutput.setText(theTree.str);
        }

        if (e.getSource() == btnPostOrder)
        {
            if (theTree.root == null)
            {
                txaOutput.setText("No Binary Tree Created:\n\n");
                return;
            }
            theTree.str = "";
            theTree.postOrderTraverseTree(theTree.root);
            txaOutput.setText(theTree.str);
        }

        if (e.getSource() == btnSave)
        {
            int size = CDValueList.size();
            // Loop through the arraylist
           for (int i = 0; i < size; i++)
           {
                //int barcode = Integer.parseInt(String.valueOf(theTree.str));
                int barcode = Integer.parseInt(CDValueList.get(i)[6].toString());
                //String key = theTree.str.toString();
                String key = CDValueList.get(i)[1].toString();
                myTable.put(key, barcode);

           }
            // Display the confirmation message when created
            txaOutput.setText("Hashmap of binary tree is saved");
            // Save to the file
            fileManager.WriteHashMapToFile(myTable.toString());
        }

        if (e.getSource() == btnDisplay)
        {
            txaOutput.setText(myTable.toString());
        }

        if (e.getSource() == txtFind) {

            if (txtFind.getText().isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Please enter the barcode.");
                return;
            }
            // Find the selected node from Dlist
            Node myNode = processList.find(txtFind.getText());

            // Find the selected node from the Binary Tree
            BTNode node = theTree.findNode(Integer.parseInt(txtFind.getText()));
            // No process log is selected and no binary tree is created
            if (myNode == null && node == null) {
                txaOutput.setText("DDL: No matching list in the log\n");
                txaOutput.append("BT: No matching Binary Tree or hasn't created yet. Please click 'By Title' button to create the binary tree");
                return;
            }
            if (myNode == null && node != null)
            {
                txaOutput.setText("DDL: No matching list in the log\n");
                String barcode = "BT: " + node.barcode + " - " + node.title;
                txaOutput.append(barcode);
                return;
            }
            // if the user enter the barcode that doesn't match or exist
            if (myNode != null && node == null ){
                String str = "DDL: " + myNode.log.date.toString() + " - " + myNode.log.currentTime + " - " +
                        myNode.log.action + " - " + myNode.log.result + " - " + myNode.log.barcode + "\n";
                txaOutput.setText(str);
                txaOutput.append("BT: No matching binary tree");
                return;
            }
            else{
                String barcode = "BT: " + node.barcode + " - " + node.title;
                String str = "DDL: " + myNode.log.date.toString() + " - " + myNode.log.currentTime + " - " +
                        myNode.log.action + " - " + myNode.log.result + " - " + myNode.log.barcode + "\n";
                //Display the entered barcode from the Dlist
                txaOutput.setText(str);
                // Display the entered barcode from the Binary Tree
                txaOutput.append(barcode);
            }



        }

        if (e.getSource() == btnRetrieve)
        {
            if (!txtBarcode.getText().isEmpty()) {
                DisplayAutomationForm("", "Retrieve");
                DisplayProcessList("SENT", "Retrieved Item", txtBarcode.getText());
                send();
            }
            else
            {
                JOptionPane.showMessageDialog(this,"Please select the CD from the list.");
                return;
            }

        }

        if (e.getSource() == btnRemove)
        {
            if (!txtBarcode.getText().isEmpty()) {
            DisplayAutomationForm("","Remove");
            DisplayProcessList("SENT", "Remove Item", txtBarcode.getText());
            send();
            }
            else
            {
                JOptionPane.showMessageDialog(this,"Please select the CD from the list.");
                return;
            }

        }
        if (e.getSource() == btnReturn)
        {
            if (!txtBarcode.getText().isEmpty()) {
            DisplayAutomationForm("","Return");
            DisplayProcessList("SENT", "Return Item", txtBarcode.getText());
            send();
            }
            else
            {
                JOptionPane.showMessageDialog(this,"Please select the CD from the list.");
                return;
            }
        }
        if (e.getSource() == btnAdd)
        {
            if (!txtBarcode.getText().isEmpty()) {
            DisplayAutomationForm("","Add");
            DisplayProcessList("SENT", "New Item Detail Recorded", txtBarcode.getText());
            send();
            }
            else
            {
                JOptionPane.showMessageDialog(this,"Please select the CD from the list.");
                return;
            }
        }

        if (e.getSource() == btnRandom)
        {
            if (!txtSortSection.getText().isEmpty()) {
                DisplayAutomationForm("Random Collection", "Sort");
                sortSend();
            }
            else
            {
                JOptionPane.showMessageDialog(this,"Please enter the CD section to sort.");
                return;
            }

        }

        if (e.getSource() == btnMostly )
        {
            if (!txtSortSection.getText().isEmpty()) {
            DisplayAutomationForm("Mostly Sorted","Sort");
            sortSend();
            }
            else
            {
                JOptionPane.showMessageDialog(this,"Please enter the CD section to sort.");
                return;
            }
        }

        if (e.getSource() == btnReverse)
        {
            if (!txtSortSection.getText().isEmpty()) {
            DisplayAutomationForm("Reverse Order","Sort");
            sortSend();
            }
        else
             {
            JOptionPane.showMessageDialog(this,"Please enter the CD section to sort.");
            return;
             }

        }


        // Close button
        if(e.getSource() == btnExit)
        {
            int confirmed = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to exit the program?", "Exit Program Message Box",
                    JOptionPane.YES_NO_OPTION);

            if (confirmed == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
            else{
                setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
            }
        }


        // This button is for testing the connection
//        if (e.getSource() == btnProcess)
//        {
//            send();
//            txtBarcode.requestFocus();
//            txtTitle.requestFocus();
//        }
    }

    /**
     * This method is used for use input validation to save a new CD entry or update the existing data
     * @return Return false if the validations don't meet the requirement
     */
    private boolean TextFieldValidation() {
        // Display a message if the text field is null or blank
        if (txtTitle.getText().isBlank())
        {
            JOptionPane.showMessageDialog(this,"Please enter the title");
            return true;
        }
        if (txtAuthor.getText().isBlank())
        {
            JOptionPane.showMessageDialog(this,"Please enter the author");
            return true;
        }
        if (txtSection.getText().isBlank())
        {
            JOptionPane.showMessageDialog(this,"Please enter the section");
            return true;
        }
        if (txtX.getText().isBlank())
        {
            JOptionPane.showMessageDialog(this,"Please enter the X position");
            return true;
        }
        if (!StringUtils.isNumeric(txtX.getText()))
        {
            JOptionPane.showMessageDialog(this,"Only allows numbers in X position section. Please try again.");
            return true;
        }
        if (txtY.getText().isBlank())
        {
            JOptionPane.showMessageDialog(this,"Please enter the Y position");
            return true;
        }
        if (!StringUtils.isNumeric(txtY.getText()))
        {
            JOptionPane.showMessageDialog(this,"Only allows numbers in Y position section. Please try again.");
            return true;
        }
        if (txtBarcode.getText().isBlank())
        {
            JOptionPane.showMessageDialog(this,"Please enter the barcode");
            return true;
        }
        if (!StringUtils.isNumeric(txtBarcode.getText()))
        {
            JOptionPane.showMessageDialog(this,"Only allows numbers in barcode section. Please try again.");
            return true;
        }
        if (txaDescription.getText().isBlank())
        {
            JOptionPane.showMessageDialog(this,"Please enter the description");
            return true;
        }
        return false;
    }

    /**
     *  This method is used for clearing all the text fields for CD entry to add a new CD detail
     */
    private void ClearAllTextFields() {
        txtId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtSection.setText("");
        txtX.setText("");
        txtY.setText("");
        txtBarcode.setText("");
        txaDescription.setText("");
    }

    /**
     *  The method is used for display process lists in the output text area
     * @param action for RCVD or SENT based on what the robot acts
     * @param result for the request result displayed
     * @param barcode for display barcode of the selected CD data
     */
    private void DisplayProcessList(String action,String result, String barcode)
    {

        if (!txtBarcode.getText().equals(""))
        {
                //processList.firstNode.insert(new Node(action, result, barcode));
                processList.firstNode.insert(new Node(action, result, barcode));
                txaOutput.setText(processList.toString());
        }
        else
        {
            txaOutput.setText("");
        }
        
    }


    /**
     * The method for determining the input value is an integer
     * @param input to be stored integer values
     * @return Return true or false based on the user input
     */
    private boolean IsAnInteger(String input)
    {
        try
        {
            Integer.parseInt(input);
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    /**
     *  This method is part of the merge sort process, where it merges the divided portions of the list
     *  (left and right) back together in sorted order.
     *  @param list the ArrayList of Object arrays that holds the data to be merged and sorted
     *  @param left the starting index of the first sublist within the ArrayList to merge
     *  @param mid the ending index of the first sublist
     *  @param right the ending index of the second sublist within the ArrayList to merge
     */
    public static void MergeArrayList (ArrayList<Object[]> list,int left, int mid,int right)
    {
        // Divide data by 2 groups
        int firstGroup = mid - left + 1;
        int secondGroup = right - mid;

        //Create temp array
        ArrayList<Object[]> leftArray = new ArrayList<>(firstGroup);
        ArrayList<Object[]> rightArray = new ArrayList<>(secondGroup);

        //Store data to temp array
        for (int i = 0; i < firstGroup; i++)
        {
            leftArray.add(i,list.get(left +i));
        }
        for (int j = 0; j < secondGroup; j++)
        {
            rightArray.add(j,list.get(mid + 1 + j));
        }
        // Set the first indexes for each group
        int i =0, j = 0;
        int k = left;

        while (i < firstGroup && j < secondGroup)
        {
            if (((Comparable)leftArray.get(i)[2]).compareTo(rightArray.get(j)[2]) <= 0)
            {
                list.set(k,leftArray.get(i));
                i++;
            }
            else
            {
                list.set(k,rightArray.get(j));
                j++;
            }
            k++;
        }

        while (i < firstGroup)
        {
            list.set(k,leftArray.get(i));
            i++;
            k++;
        }

        while (j < secondGroup)
        {
            list.set(k,rightArray.get(j));
            j++;
            k++;
        }
    }

    /**
     * Sorts an ArrayList of Object arrays using the Merge Sort algorithm.
     * @param list takes one of the value in the Array list to be sorted
     * @param left the starting index of the portion of the list to sort
     * @param right the ending index of the portion of the list to sort
     */
    public static  void MergeSort(ArrayList<Object[]> list,int left,int right)
    {
        if (left < right)
        {
            int mid = (left + right) / 2;
            MergeSort(list,left,mid);
            MergeSort(list,mid + 1,right);

            MergeArrayList(list,left,mid,right);
        }
    }

    /**
     * This method is to set the sort label and combobox for the request sent from the main form
     * @param randomCollection Label to display what type of sort will be performed
     * @param action Actions to be requested from the main form
     */
    private void DisplayAutomationForm(String randomCollection, String action) {

        autoForm.lblSortSection.setText(randomCollection);
        autoForm.cboAction.setSelectedItem(action);
    }


    /**
     * The method for retrieving data value from the grid when the mouse is rolled over
     * @param table the field where the data is displayed
     */
    private void AddMouseListenerToJTable(JTable table)
    {
        table.addMouseListener(new MouseAdapter() {
            // When the mouse rolls over the grid, action the event
            @Override
            public void mouseClicked(MouseEvent e) {
                // Convert e.getSource method to textfield
                JTable table = (JTable) e.getSource();
                int selection= table.getSelectedRow();
                // Add the retrieved data in the population text field
                txtId.setText(table.getValueAt(selection,0).toString());
                txtTitle.setText(table.getValueAt(selection,1).toString());
                txtAuthor.setText(table.getValueAt(selection,2).toString());
                txtSection.setText(table.getValueAt(selection,3).toString());
                txtX.setText(table.getValueAt(selection,4).toString());
                txtY.setText(table.getValueAt(selection,5).toString());
                txtBarcode.setText(table.getValueAt(selection,6).toString());
                txaDescription.setText(table.getValueAt(selection,7).toString());
                //table.clearSelection();

            }
        });
    }


    /**
     * This method is to open a socket connection to the specified server and
     * handles any exceptions during the connection process.
     *  @param serverName the name of the server to connect to
     *  @param serverPort the port number on the server to connect to
     */
    public void connect(String serverName, int serverPort)
    {
        println("Establishing connection. Please wait ...");
        try
        {
            socket = new Socket(serverName, serverPort);
            println("Connected: " + socket);
            open();
        }
        catch (UnknownHostException uhe)
        {
            println("Host unknown: " + uhe.getMessage());
        }
        catch (IOException ioe)
        {
            println("Unexpected exception: " + ioe.getMessage());
        }
    }

    /**
     * This method formats string to the output stream and flushes the stream to ensure it is sent.
     * If an error occurs during the send operation, it displays the error and closes the connection.
     */
    private void send()
    {
        try
        {
            String str = "Main: " + txtBarcode.getText() + ": " + txtSection.getText();
            streamOut.writeUTF(str);
            streamOut.flush();

        }
        catch (IOException ioe)
        {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }


    /**
     * This method formats string to the output stream and flushes the stream
     * to ensure it is sent when one of the sorts buttons si clicked
     * If an error occurs during the send operation, it displays the error and closes the connection.
     */
    private void sortSend()
    {
        try
        {
            String str = "Main: " + txtSortSection.getText().toUpperCase();
            streamOut.writeUTF(str);
            streamOut.flush();

        }
        catch (IOException ioe)
        {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }

    /**
     *  This method is called when the run methods is called in the chatClientThread 1 class.
     *  It is used for parsing the message, checks the sender, and displays it if the sender is not "Main".
     *  It then updates the process list display with the received message details.
     * @param msg the incoming message to be processed
     */
    public void handle(String msg)
    {
        if (msg.equals(".bye"))
        {
            println("Good bye. Press EXIT button to exit ...");
            close();
        }
        else
        {
            String temp[] = msg.split(": ");
            System.out.println("Handle: " + msg);
            if (!temp[1].equals("Main"))
            {
                println(msg);
                DisplayProcessList("RCVD",temp[4],temp[2]);
            }
        }
    }

    /**
     *  This method creates a `DataOutputStream` for the current socket connection,
     *  and initiates a new `ChatClientThread1` instance to handle incoming messages in a separate thread.
     *  This method is called when connecting to the server
     */
    public void open()
    {
        try
        {
            streamOut = new DataOutputStream(socket.getOutputStream());
            client = new ChatClientThread1(this, socket);
        }
        catch (IOException ioe)
        {
            println("Error opening output stream: " + ioe);
        }
    }

    /**
     * This method is used for safely closing the output stream, socket connection, and associated client thread.
     */
    public void close()
    {
        try
        {
            if (streamOut != null)
            {
                streamOut.close();
            }
            if (socket != null)
            {
                socket.close();
            }
        }
        catch (IOException ioe)
        {
            println("Error closing ...");
        }
        client.close();
        client.interrupt();
    }

    /**
     * println method is used for displaying a message in the label to send from the main form to the automation form
     * @param msg contains messages that we want to send
     */
    void println(String msg)
    {
        //display.appendText(msg + "\n");
        lblAutoMessage.setText(msg);
    }

    /**
     *  This method is used for determining the serverName and serverPort to connect to.
     */
    public void getParameters()
    {
//        serverName = getParameter("host");
//        serverPort = Integer.parseInt(getParameter("port"));
        serverName = "localhost";
        serverPort = 4444;
    }
}
