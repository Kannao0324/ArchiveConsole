import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.net.*;
import java.io.*;
import org.apache.commons.lang3.StringUtils;

/**
 *  AutomationForm Class for containing variables and different data types
 *  Add extension of JFrame and implementing ActionListener
 *  This class is used for the robot to perform the requested operation from the main form
 */
public class AutomationForm extends JFrame implements ActionListener {

    SpringLayout layout = new SpringLayout();
    SpringLayout tableLayout = new SpringLayout();

    JLabel lblAutomation,lblCurrent,lblBarcode,lblSection,lblSortSection,lblArchive;
    JComboBox cboAction;
    JTextField txtCurrent,txtBarcode,txtSection;
    JButton btnProcess,btnAddItem,btnExit,btnConnect;
    ArrayList<Object[]> CDValueList;
    String[] columnNames;
    JTable tblArchive;
    JPanel plAction;

    JLabel lblServerMessage;

    //CHAT RELATED ---------------------------
    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread2 client2 = null;
    private String serverName = "localhost";
    private int serverPort = 4444;
    //----------------------------------------




    String addMessage = "A new CD will have been handed to the robotic arm.\n" +
            "When the 'Process' button is pressed, the robotic arm will read and return the new Barcode.\n" +
            "The Barcode will be entered into the 'Barcode' field.\n" +
            "Focus will be given to the 'Section' text field so the operator can indicate which section the CD needs to be added to.\n" +
            "The 'Add Item' button will be enabled.";
    String removeMessage = "The 'Remove' request will send commands to the robotic arm to find the relevant CD and make it available to the operator.\n\n" +
            "Once the CD is found, a message is sent back to the main Archive Console.\n\n" +
            "The main Archive Console will then remove the CD from the collection.";
    ;

    String retrievemessage = "The 'Retrieve' request will send commands to the robotic arm to find the relevant CD" +
                              "and make it available to the operator.\n" +
                              "\nOnce the CD is found, a message is sent back to the main Archive Console.\n" +
                              "\nThe main Archive Console will then note that the CD is currently out of the collection.";

    String returnMessage = "A CD will have been handed to the robotic arm.\n\n" +
            "When the 'Process' button is pressed, the robotic arm will place the CD back into its recorded position within the collection.\n\n" +
            "A message will then be sent back to the main Archive Console.";


    String secondRemoveMessage = "The CD is available for collection, and has been removed from the database.";

    String secondRetrieveMessage = "The CD is available for collection, and has been noted as 'On-Loan' from the collection.";


    /**
     * Create a constructor to set up the window, layout, all the components
     * Add closing function
     * @throws HeadlessException
     */
    public AutomationForm() throws HeadlessException {
        setSize(750, 450);

        setLocation(550,300);
        setTitle("Automation Console");
        setLayout(layout);
        setResizable(false);

        // When window closes, stop running
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
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
        lblAutomation = UIBuilderLibrary.BuildJLabelWithNorthWestAnchor("  Automation Console",0,0,layout,this);
        lblAutomation.setFont(new Font("Arial", Font.BOLD, 20));
        lblAutomation.setPreferredSize(new Dimension(750,30));
        lblAutomation.setBackground(labelColor);
        lblAutomation.setForeground(Color.white);
        lblAutomation.setOpaque(true);
        add(lblAutomation);

        lblCurrent = UIBuilderLibrary.BuildJLabelInlineBelow("Current Requested Action: ",10,layout, lblAutomation);
        lblCurrent.setPreferredSize(new Dimension(250,25));
        lblCurrent.setVerticalAlignment(SwingConstants.CENTER);
        lblCurrent.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblCurrent);

        String[] cboSelection = {"Add","Remove","Retrieve","Return","Sort"};

        cboAction = new JComboBox(cboSelection);
        cboAction.setSelectedIndex(0);
        cboAction.addActionListener(this);
        cboAction.setPreferredSize(new Dimension(180,20));
        layout.putConstraint(SpringLayout.WEST, cboAction,5,SpringLayout.EAST,lblCurrent);
        layout.putConstraint(SpringLayout.NORTH, cboAction,0,SpringLayout.NORTH,lblCurrent);
        add(cboAction);

        btnProcess = UIBuilderLibrary.BuildJButtonInlineToRight(100,20,"Process", 10, this, layout, cboAction);
        add(btnProcess);

        lblSortSection = UIBuilderLibrary.BuildJLabelInlineToRight("Sort", 30,layout,btnProcess);
        add(lblSortSection);


        lblBarcode = UIBuilderLibrary.BuildJLabelInlineBelow("Bar Code of Selected Item: ",10,layout, lblCurrent);
        lblBarcode.setPreferredSize(new Dimension(250,20));
        lblBarcode.setVerticalAlignment(SwingConstants.CENTER);
        lblBarcode.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblBarcode);

        txtBarcode = UIBuilderLibrary.BuildJTextFieldInlineToRight(7,5,layout,lblBarcode);
        txtBarcode.setPreferredSize(new Dimension(80,20));
        add(txtBarcode);

        lblSection = UIBuilderLibrary.BuildJLabelInlineToRight("Section: ",5,layout, txtBarcode);
        lblSection.setPreferredSize(new Dimension(50,20));
        lblSection.setVerticalAlignment(SwingConstants.CENTER);
        lblSection.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblSection);
        txtSection = UIBuilderLibrary.BuildJTextFieldInlineToRight(3,5,layout,lblSection);
        txtSection.setPreferredSize(new Dimension(15,20));
        txtSection.addActionListener(this);
        add(txtSection);

        btnAddItem= UIBuilderLibrary.BuildJButtonInlineToRight(100,20,"Add Item", 10, this, layout, txtSection);
        btnAddItem.setEnabled(false);
        add(btnAddItem);

        //btnConnect= UIBuilderLibrary.BuildJButtonInlineToRight(100,20,"Connect", 10, this, layout, btnAddItem);
        //add(btnConnect);

        lblServerMessage = UIBuilderLibrary.BuildJLabelInlineBelow("Server message: ",10,layout, txtBarcode);
        lblServerMessage.setPreferredSize(new Dimension(300,20));
        lblServerMessage.setVerticalAlignment(SwingConstants.CENTER);
        lblServerMessage.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblServerMessage);

        // Table
        plAction = new JPanel(tableLayout);
        plAction.setPreferredSize(new Dimension(715,240));
        plAction.setBackground(panelColor);
        layout.putConstraint(SpringLayout.WEST, plAction,10,SpringLayout.WEST,this);
        layout.putConstraint(SpringLayout.NORTH, plAction,120,SpringLayout.NORTH,this);
        add(plAction);

        lblArchive = UIBuilderLibrary.BuildJLabelWithNorthWestAnchor("Archive CDs",300,10,tableLayout, this);
        lblArchive.setFont(new Font("Arial", Font.BOLD, 16));
        plAction.add(lblArchive);


        // Creat a header for the table
        columnNames = new String[]{"ID", "Title", "Author", "Section", "X", "Y", "Barcode", "Description", "On-Loan"};
        // Retrieve body data from the filemanager
        CDValueList = MainForm.CDValueList;


        // Store the retrieved data from file to Model class
        MyModel dataModel = new MyModel(CDValueList,columnNames);
        // Create JTable and store Model data into the Jtable
        tblArchive = new JTable(dataModel);
        tblArchive.isForegroundSet();
        tblArchive.setShowHorizontalLines(true);
        tblArchive.setRowSelectionAllowed(true);
        tblArchive.setColumnSelectionAllowed(true);
        tblArchive.setAutoCreateRowSorter(true);
        plAction.add(tblArchive);

        JScrollPane tableScroll = new JScrollPane(tblArchive);
        tableScroll.setPreferredSize(new Dimension(695,180));
        tableScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableLayout.putConstraint(SpringLayout.WEST,tableScroll,10,SpringLayout.WEST,plAction);
        tableLayout.putConstraint(SpringLayout.NORTH,tableScroll,40,SpringLayout.NORTH,plAction);
        plAction.add(tableScroll);


        btnExit = UIBuilderLibrary.BuildJButtonWithSouthEastAnchor(100,20,"Exit", 0, 30, this,layout,plAction);
        add(btnExit);

        getParameters();

        addWindowListener((new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                //Connect the network
                connect(serverName, serverPort);
            }
        }));

        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

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


        if (e.getSource() == btnProcess)
        {
            // In the case of no selected barcode and section occurs
            if (txtBarcode.getText().isEmpty() && txtSection.getText().isEmpty())
            {
                    JOptionPane.showMessageDialog(this,"The request can't proceeded. Please go back to the main form");
                    return;
            }
            // If the user selected no existing section to sort, display the message
            if (tblArchive.getRowCount() == 0)
            {
                JOptionPane.showMessageDialog(this,"This section doesn't exist. Please select other section.");
                return;
            }
            // Sort by barcode in the selected section
            if (cboAction.getSelectedItem().equals("Sort"))
            {
                int size = CDValueList.size();
                // Bubble Sort
                for(int j=0; j< size; j++)
                {
                    for(int i=j+1; i< size; i++)
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
            send();
            txtBarcode.requestFocus();
            txtSection.requestFocus();
        }
    }

    /**
     * This method is to filter the CD List by the barcode or section based on the requested action
     */
    private void UpdateTheTable() {
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<>(tblArchive.getModel());
        tblArchive.setRowSorter(rowSorter);
        // This is for sort action filtered by the section
        if (txtBarcode.getText().isBlank()) {

            String search = txtSection.getText();

            if (search.trim().isEmpty()) {
                rowSorter.setRowFilter(null);

            } else rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + search, 3));
        }
        // This is for other actions filtered by barcode
        else
        {
            String search = txtBarcode.getText();

            if (search.trim().isEmpty())
            {
                rowSorter.setRowFilter(null);

            }
            else
            {
                rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + search));
            }
        }
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
            String str = "Robot: " + txtBarcode.getText() + ": " + txtSection.getText() + ": " + cboAction.getSelectedItem().toString();
            streamOut.writeUTF(str);
            streamOut.flush();

            txtBarcode.setText("");
            txtSection.setText("");

        }
        catch (IOException ioe)
        {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }

    /**
     *  This method is called when the run methods is called in the chatClientThread 2 class.
     *  It is used for parsing the message, checks the sender, and displays it if the sender is not "Robot".
     *  It then updates the process list display with the received message details.
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
            //12345: Main: barcode: section
            String temp[] = msg.split(": ");
            System.out.println("Handle: " + msg);
            if (!temp[1].equals("Robot"))
            {
                // If the action is sort, only section is displayed
                if (cboAction.getSelectedItem().equals("Sort"))
                {
                txtBarcode.setText("");
                txtSection.setText("");
                txtSection.setText(temp[2]);
                println(msg);
                }
                // if Other actions are selected, display barcode and section
                else {
                    txtBarcode.setText(temp[2]);
                    txtSection.setText(temp[3]);
                    println(msg);
                }
                UpdateTheTable();
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
            client2 = new ChatClientThread2(this, socket);
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
        client2.close();
        client2.interrupt();
    }
    /**
     *  println method is used for displaying a message in the label to send from a robot to the main form .
     */
    void println(String msg)
    {
        //display.appendText(msg + "\n");
        lblServerMessage.setText(msg);
    }

    /**
     *  This method is used for determining the serverName and serverPort to connect to.
     */
    public void getParameters()
    {
        serverName = "localhost";
        serverPort = 4444;
    }

}


