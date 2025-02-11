import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

        class MyModel extends AbstractTableModel
        {
            ArrayList<Object[]> al;
            // the headers
            String[] header;
            // to hold the column index for the Sent column
            int col;
            // constructor
            MyModel(ArrayList<Object[]> obj, String[] header)
            {
                // save the header
                this.header = header;
                // and the data
                al = obj;
                // get the column index for the Sent column
                col = this.findColumn("OnLoan");
            }
            // method that needs to be overload. The row count is the size of the ArrayList
            public int getRowCount()
            {
                return al.size();
            }
            // method that needs to be overload. The column count is the size of our header
            public int getColumnCount()
            {
                return header.length;
            }
            // method that needs to be overload. The object is in the arrayList at rowIndex
            public Object getValueAt(int rowIndex, int columnIndex)
            {
                return al.get(rowIndex)[columnIndex];
            }
            // a method to return the column name
            public String getColumnName(int index)
            {
                return header[index];
            }

//            public Class getColumnClass(int columnIndex)
//            {
//                if (columnIndex == col)
//                {
//                    return Boolean.class; // For every cell in column 7, set its class to Boolean.class
//                }
//                return super.getColumnClass(columnIndex); // Otherwise, set it to the default class
//            }

            // a method to add a new line to the table
            void add(String id, String title,String author,String section,String x,String y,String barcode,String description, boolean onLoan)
            {
                // make it an array[8] as this is the way it is stored in the ArrayList
                // (not best design but we want simplicity)
                Object[] item = new Object[9];
                item[0] = id;
                item[1] = title;
                item[2] = author;
                item[3] = section;
                item[4] = x;
                item[5] = y;
                item[6] = barcode;
                item[7] = description;
                item[8] = onLoan;

                al.add(item);
                // inform the GUI that I have change
                fireTableDataChanged();
            }

            public void addOrUpdateRow(Object[] rowData, int rowIndex)
            {
                if (rowIndex >= 0 && rowIndex < al.size())
                {
                    //Update the existing row
                    al.set(rowIndex,rowData);
                }
                else
                {
                    //Add a new row
                    al.add(rowData);
                }
                // inform the GUI that I have change
                fireTableDataChanged();
            }
        }



