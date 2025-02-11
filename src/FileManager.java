import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * File management class for writing our application data to the selected files and reading it back again.
 */
public class FileManager
{
    String[] CDheader;
    ArrayList<Object[]> CDList = new ArrayList<>();

   String fileName = "CD_ArchivePrototype_SampleData.txt";
   //String fileName = "CD_ArchivePrototype_SampleData - Copy.txt";

   /**
    * Takes a provided a array of data with the header details
    * reads it from a txt file in semicolon delimited format.
    * @return
    */
   String[] ReadHeaderFromFile()
   {
       // Use try catch to get the bufferedReader work
      try
      {
          BufferedReader input = new BufferedReader(new FileReader(fileName));

         // Split the line in the file
         String line;

         line = input.readLine();
         // Create a temp array and split the line with ;
         String[] temp = line.split(";");
         CDheader = temp;

         // Close the reading file
         input.close();
      }
      // if the reading file does not work, display the error message
      catch (Exception ex)
      {
         System.out.println(ex.getMessage());
         // Reset the object and clear the form
         CDheader = null;

      }
      return CDheader;
   }


    ArrayList<Object[]> ReadBodyFromFile()
   {

       // Use try catch to get the bufferedReader work
       try
       {
           BufferedReader input = new BufferedReader(new FileReader(fileName));
           // Split the line in the fileArrayList<Object[]>
           String line;
           line = input.readLine();


           // Read the line in the array and compare the next line is null or not
           while((line = input.readLine()) !=null)
           {
               // Create a temp array and split the line with ;
               String[] tempArray = line.split(";");

               // Put each value into each argument
               CDList.add(tempArray);
            }
       // Close the reading file
       input.close();
   }
    // if the reading file does not work, display the error message
      catch (Exception ex)
    {
        System.out.println(ex.getMessage());
        // Reset the object and clear the form

        CDList = null;
    }
      return CDList;
    }


    public void WriteToFile(String[] header,ArrayList<Object[]> CDList ) throws IOException
    {
          // Use try catch to get the bufferedWriter work
             try {
                 // Create a new object
                 BufferedWriter output = new BufferedWriter(new FileWriter(fileName));

                 header = CDheader;
                 output.write(String.join(";",header));
                 // Add a new line
                 output.newLine();

                  for (int i = 0; i < CDList.size(); i++) {

                    // Write data to the file
                    Object[] temp = CDList.get(i);
                    // If there is no data in the arraylist, break the loop
                    if (temp == null)
                    {
                        break;
                    }
                      for (int j = 0; j < 9; j++) {
                          //then go through each thing in temp and write it.
                          output.write(temp[j] + ";");
                      }
                      if (temp[0] != "")
                      {
                          // Add a new line
                          output.newLine();
                      }

                    }

                output.close();
            }
            // If the bufferwriter is not working, display a message
              catch(IOException io)
              {
                    System.out.println(io);
              }
        }

    public void WriteHashMapToFile(String hashData) {

        String hashmapFile = "HashMap_BinaryTree.txt";
        // Use try catch to get the bufferedWriter work
        try {
            // Create a new object
            BufferedWriter output = new BufferedWriter(new FileWriter(hashmapFile));

                // If there is no data in the array, break the loop
                if (hashData == null) {
                    return;
                }
                // Write data to the file
                output.write(hashData);
                // Add a new line
                output.newLine();

            output.close();
        }
        // If the bufferwriter is not working, display a message
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    }