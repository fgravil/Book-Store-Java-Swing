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
    private int itemNum = 1;
    private String bookInfo = "";
    private float totalPrice;
    private ArrayList<String> books = new ArrayList<>();

    JButton process;
    JButton confirm;
    JButton view;
    JButton finish;
    JButton newOrder;
    JButton exit;
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

        totalLabel = new JLabel("Order subtotal for " + itemNum+ "item(s):");
        totalInput = new JTextField();
        totalInput.setEnabled(false);
        totalInput.setColumns(40);

        panel.add(totalLabel);
        panel.add(totalInput);

        process = new JButton("Process Item #1");
        ButtonListener processListener = new ButtonListener();
        process.addActionListener(processListener);

        confirm = new JButton("Confirm Item #1");
        confirm.setEnabled(false);
        ButtonListener confirmListener = new ButtonListener();
        confirm.addActionListener(confirmListener);

        view = new JButton("View Order");
        ButtonListener viewListener = new ButtonListener();
        view.setEnabled(false);
        view.addActionListener(viewListener);

        finish = new JButton("Finish Order");
        finish.setEnabled(false);
        ButtonListener finishListener = new ButtonListener();
        finish.addActionListener(finishListener);

        newOrder = new JButton("New Order");
        ButtonListener newOrderListener = new ButtonListener();
        newOrder.addActionListener(newOrderListener);

        exit = new JButton("Exit");
        ButtonListener exitListener = new ButtonListener();
        exit.addActionListener(exitListener);

        panel.add(process);
        panel.add(confirm);
        panel.add(view);
        panel.add(finish);
        panel.add(newOrder);
        panel.add(exit);

        this.add(panel);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == process ){

                String bookId = bookIdInput.getText();
                amtItems = Integer.parseInt(numItemsInput.getText());

                //If customer wants no item, disable process button
                if(amtItems <=  0 )
                {
                    process.setEnabled(false);
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
                        JOptionPane.showMessageDialog(null, "Item #" + itemNum + " accepted");
                        itemInfoInput.setText(bookInfo);

                        process.setEnabled(false);
                        confirm.setEnabled(true);
                        view.setEnabled(true);

                        if(itemNum == amtItems) {
                            finish.setEnabled(true);
                            process.setEnabled(false);
                            bookIdInput.setEnabled(false);
                            numItemsInput.setEnabled(false);
                        }
                        else
                            itemNum++;

                    }
                }
            }

            else if(e.getSource() == confirm ){

                int quantity = Integer.parseInt(quantityInput.getText());
                int discount = getDiscount(quantity);
                float price = Float.parseFloat(String.format("%.2f",getPrice(discount)));
                totalPrice += price * quantity;

                bookInfo = bookInfo + " " + quantity + " " + discount  + "% $" + price;
                itemInfoInput.setText(bookInfo);
                totalInput.setText("$" + totalPrice);

                bookIdLabel.setText("Enter Book ID for item #" + itemNum + ":");
                quantityLabel.setText("Enter quantity for item #" + itemNum + ":");
                itemInfoLabel.setText("Item #" + itemNum + " info:");
                totalInput.setText("Order subtotal for " + itemNum + " item(s):");

                bookIdInput.setText("");
                quantityInput.setText("");

                if(itemNum != amtItems)
                    process.setEnabled(true);
                else {
                    process.setEnabled(false);
                    bookIdInput.setEnabled(false);
                    quantityInput.setEnabled(false);
                }

                confirm.setEnabled(false);

                books.add(bookInfo);

            }

            else if(e.getSource() == view ){

                StringBuffer booksDisplay = new StringBuffer();

                for(int i=0; i < books.size(); i++)
                {
                    booksDisplay.append(i+1 +". " + books.get(i) +"\n" );
                }

                JOptionPane.showMessageDialog(null,booksDisplay);
            }

            else if(e.getSource() == finish ){

                StringBuffer displayInfo = new StringBuffer();
                StringBuffer booksToOutput = new StringBuffer();

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss a z");
                Date d= new Date();
                String date = dateFormat.format(d);

                dateFormat = new SimpleDateFormat("yyMMddhhmmss");
                String dateId = dateFormat.format(d);
//                dateId = dateId.replaceAll("\\s","");

                displayInfo.append("Date: " + date + "\n");
                displayInfo.append("Number of line items: " + amtItems + "\n");
                displayInfo.append("Item# / ID / Title / Price / Qty / Disc % /Subtotal: " + "\n\n");

                String bookData;
                for(int i = 0; i < books.size(); i++){
                    displayInfo.append(i+1 + ". " + books.get(i) + "\n");
                    bookData = books.get(i).substring(7,books.get(i).length());
                    booksToOutput.append(dateId+ ", " + bookData + "\n");
                }

                displayInfo.append("\n\nOrder subtotal: $" + new DecimalFormat("##.##").format(totalPrice) + "\n\n");
                displayInfo.append("Tax rate: 6%\n\n");

                float tax = (float) (totalPrice * .06);
                float priceWithTax = (float) (totalPrice + tax);


                displayInfo.append("Tax amount: $"  + new DecimalFormat("##.##").format(tax) + "\n\n");
                displayInfo.append("Order Total: $" + new DecimalFormat("##.##").format(priceWithTax) + "\n\n");

                displayInfo.append("Thanks for shopping at Ye Olde Book Shoppe");
                JOptionPane.showMessageDialog(null, displayInfo );

                WriteFile(booksToOutput);


            }

            else if(e.getSource() == newOrder ){
                amtItems = 1;
                numItemsInput.setText("");
                itemInfoInput.setText("");
                totalInput.setText("");
                numItemsInput.setEnabled(true);
                books.clear();
            }

            else if(e.getSource() == exit ){
                System.exit(0);
            }

        }
    }
}
