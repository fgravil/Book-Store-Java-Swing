package com.fredgravil.bookstore;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javax.swing.*;

public class BookStore extends JFrame{

    private static Scanner scan;
    private int amtItems;
    private int itemNum = 0;
    private String bookInfo = "";
    private float totalPrice;
    private ArrayList<String> books = new ArrayList<>();

    JButton processButton;
    JButton confirmButton;
    JButton viewButton;
    JButton finishButton;
    JButton newOrderButton;
    JButton exitButton;
    JTextField numItemsInput;
    JTextField bookIdInput;
    JTextField quantityInput;
    JTextField itemInfoInput;
    JTextField totalInput;
    JLabel numItemsLabel;
    JLabel bookIdLabel;
    JLabel quantityLabel;
    JLabel itemInfoLabel;
    JLabel totalLabel;

    public String Search(String id){

        String bookId;
        try {
            URL url = getClass().getResource("inventory.txt");
            scan = new Scanner(new File(url.getPath()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        do{
            bookId = scan.nextLine();

            if(id.compareTo(bookId.substring(0,5)) == 0){

                return bookId;
            }

        }while (scan.hasNext());

        scan.close();
        return null;
    }

    public float getPrice(int discount){

        int commaCnt = 0;
        int startNum = 0;

        while(commaCnt  < 2){

            if(bookInfo.charAt(startNum) == ','){
                commaCnt++;
            }
            startNum++;
        }

        float price = Float.parseFloat( bookInfo.substring(startNum+1,bookInfo.length()) );

        if(discount == 10)
            price -= price * .10;
        else if(discount == 15)
            price -= price * .15;
        else if(discount == 20)
            price -= price * .20;

        return price;


    }

    public int getDiscount(int numBooks){

        if(numBooks < 5){
            return 0;
        }
        else if(numBooks < 10){
            return 10 ;
        }
        else if(numBooks < 15){
            return 15;
        }
        else{
            return 20;
        }
    }

    public void WriteFile(StringBuffer input){

        try{
            File file = new File("output.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.print(input.toString());
            pw.close();

            System.out.println("Data successfully appended at the end of file");

        }catch(IOException ioe){
            System.out.println("Exception occurred:");
            ioe.printStackTrace();
        }


    }

    public static void main(String[] args){

        new BookStore();
    }

    public  BookStore(){

        this.setSize(500,400);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension dimension = tk.getScreenSize();

        int xPos = (dimension.width / 2) - (this.getWidth() / 2);
        int yPos = (dimension.height / 2) - (this.getHeight() / 2);
        this.setLocation(xPos,yPos);

        this.setTitle("Ye Olde Book Shoppe");

        JPanel panel = new JPanel();

        numItemsLabel = new JLabel("Enter number of items in this order:");
        numItemsInput = new JTextField();
        numItemsInput.setColumns(40);

        panel.add(numItemsLabel);
        panel.add(numItemsInput);

        bookIdLabel = new JLabel("Enter Book Id for Item #1:");
        bookIdInput = new JTextField();
        bookIdInput.setColumns(40);

        panel.add(bookIdLabel);
        panel.add(bookIdInput);

        quantityLabel = new JLabel("Enter quantity for Item #1:");
        quantityInput = new JTextField();
        quantityInput.setColumns(40);

        panel.add(quantityLabel);
        panel.add(quantityInput);

        itemInfoLabel = new JLabel("Item #1 info:");
        itemInfoInput = new JTextField();
        itemInfoInput.setEnabled(false);
        itemInfoInput.setColumns(40);

        panel.add(itemInfoLabel);
        panel.add(itemInfoInput);

        totalLabel = new JLabel("Order subtotal for " + (itemNum+1) + " item(s):");
        totalInput = new JTextField();
        totalInput.setEnabled(false);
        totalInput.setColumns(40);

        panel.add(totalLabel);
        panel.add(totalInput);

        processButton = new JButton("Process Item #" + (itemNum+1));
        ButtonListener processListener = new ButtonListener();
        processButton.addActionListener(processListener);

        confirmButton = new JButton("Confirm Item #"+(itemNum+1));
        confirmButton.setEnabled(false);
        ButtonListener confirmListener = new ButtonListener();
        confirmButton.addActionListener(confirmListener);

        viewButton = new JButton("View Order");
        ButtonListener viewListener = new ButtonListener();
        viewButton.setEnabled(false);
        viewButton.addActionListener(viewListener);

        finishButton = new JButton("Finish Order");
        finishButton.setEnabled(false);
        ButtonListener finishListener = new ButtonListener();
        finishButton.addActionListener(finishListener);

        newOrderButton = new JButton("New Order");
        ButtonListener newOrderListener = new ButtonListener();
        newOrderButton.addActionListener(newOrderListener);

        exitButton = new JButton("Exit");
        ButtonListener exitListener = new ButtonListener();
        exitButton.addActionListener(exitListener);

        panel.add(processButton);
        panel.add(confirmButton);
        panel.add(viewButton);
        panel.add(finishButton);
        panel.add(newOrderButton);
        panel.add(exitButton);

        this.add(panel);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == processButton){

                String bookId = bookIdInput.getText();
                amtItems = Integer.parseInt(numItemsInput.getText());

                //If customer wants no item, disable processButton button
                if(amtItems <=  0 )
                {
                    processButton.setEnabled(false);
                    bookIdInput.setEnabled(false);
                    numItemsInput.setEnabled(false);

                }
                else {

                    numItemsInput.setEnabled(false);
                    bookInfo = Search(bookId);

                    if(bookInfo == null){
                        JOptionPane.showMessageDialog(null, "Book Id " + bookId + " not in file");
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Item #" + (itemNum+1) + " accepted");
                        itemInfoInput.setText(bookInfo);

                        processButton.setEnabled(false);
                        confirmButton.setEnabled(true);
                        viewButton.setEnabled(true);

                        if(itemNum == amtItems) {
                            finishButton.setEnabled(true);
                            processButton.setEnabled(false);
                            bookIdInput.setEnabled(false);
                            numItemsInput.setEnabled(false);
                        }
                        else
                            itemNum++;

                    }
                }
            }

            else if(e.getSource() == confirmButton){

                int quantity = Integer.parseInt(quantityInput.getText());
                int discount = getDiscount(quantity);
                float price = Float.parseFloat(String.format("%.2f",getPrice(discount)));
                totalPrice += price * quantity;

                bookInfo = bookInfo + ", " + quantity + ", " + discount  + "%, $" + price;

                itemInfoInput.setText(bookInfo);
                totalInput.setText("$" + totalPrice);
                bookIdInput.setText("");
                quantityInput.setText("");

                if(itemNum != amtItems) {

                    bookIdLabel.setText("Enter Book ID for item #" + (itemNum+1) + ":");
                    quantityLabel.setText("Enter quantity for item #" + (itemNum+1) + ":");
                    itemInfoLabel.setText("Item #" + (itemNum+1) + " info:");
                    totalLabel.setText("Order subtotal for " + (itemNum+1) + " item(s):");

                    processButton.setEnabled(true);
                    processButton.setText("Process Item #" + (itemNum+1));
                    confirmButton.setText("Confirm Item #" + (itemNum+1));
                }
                else {

                    processButton.setEnabled(false);
                    finishButton.setEnabled(true);
                    bookIdInput.setEnabled(false);
                    quantityInput.setEnabled(false);
                }

                confirmButton.setEnabled(false);

                books.add(bookInfo);

            }

            else if(e.getSource() == viewButton){

                StringBuffer booksDisplay = new StringBuffer();

                for(int i=0; i < books.size(); i++)
                {
                    booksDisplay.append((i+1) +". " + books.get(i) +"\n" );
                }

                JOptionPane.showMessageDialog(null,booksDisplay);
            }

            else if(e.getSource() == finishButton){

                StringBuffer displayInfo = new StringBuffer();
                StringBuffer booksToOutput = new StringBuffer();

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss a z");
                Date d= new Date();
                String date = dateFormat.format(d);

                dateFormat = new SimpleDateFormat("yyMMddhhmmss");
                String dateId = dateFormat.format(d);

                displayInfo.append("Date: " + date + "\n");
                displayInfo.append("Number of line items: " + amtItems + "\n");
                displayInfo.append("Item# / ID / Title / Price / Qty / Disc % /Subtotal: " + "\n\n");

                for(int i = 0; i < books.size(); i++){
                    displayInfo.append((i+1) + ". " + books.get(i) + "\n");
                    booksToOutput.append(dateId+ ", " + books.get(i) + ", " + date +"\n");
                }

                displayInfo.append("\n\nOrder subtotal: $" + new DecimalFormat("##.##").format(totalPrice) + "\n\n");
                displayInfo.append("Tax rate: 6%\n\n");

                float tax = (float) (totalPrice * .06);
                float priceWithTax = (totalPrice + tax);


                displayInfo.append("Tax amount: $"  + new DecimalFormat("##.##").format(tax) + "\n\n");
                displayInfo.append("Order Total: $" + new DecimalFormat("##.##").format(priceWithTax) + "\n\n");

                displayInfo.append("Thanks for shopping at Ye Olde Book Shoppe");
                JOptionPane.showMessageDialog(null, displayInfo );

                WriteFile(booksToOutput);

                finishButton.setEnabled(false);


            }

            else if(e.getSource() == newOrderButton){
                amtItems = 0;
                itemNum = 0;

                bookIdLabel.setText("Enter Book Id for Item #1:");
                quantityLabel.setText("Enter quantity for Item #1:");
                itemInfoLabel.setText("Item #1 info:");
                totalLabel.setText("Order subtotal for " + (itemNum+1) + " item(s):");

                numItemsInput.setText("");
                itemInfoInput.setText("");
                totalInput.setText("");

                numItemsInput.setEnabled(true);
                bookIdInput.setEnabled(true);
                quantityInput.setEnabled(true);

                processButton.setText("Process Item #" + (itemNum+1));
                confirmButton.setText("Confirm Item #" + (itemNum+1));

                processButton.setEnabled(true);
                confirmButton.setEnabled(false);
                finishButton.setEnabled(false);


                books.clear();
            }

            else if(e.getSource() == exitButton){
                System.exit(0);
            }

        }
    }
}
