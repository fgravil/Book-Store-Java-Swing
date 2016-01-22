package com.fredgravil.bookstore;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

    public String Search(String id){

        String bookId;
        try {
            URL url = getClass().getResource("inventory.txt");
            scan = new Scanner(new File(url.getPath()));
            System.out.println(url.toString());
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

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("output.txt"), "utf-8"))) {
            writer.write(input.toString());
        } catch (IOException e) {
            e.printStackTrace();
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

        JLabel numItemsLabel = new JLabel("Enter number of items in this order:");
        numItemsLabel.setBorder(new EmptyBorder(0,0,0,20));
        numItemsInput = new JTextField();
        numItemsInput.setColumns(40);

        panel.add(numItemsLabel);
        panel.add(numItemsInput);

        JLabel bookIdLabel = new JLabel("Enter Book Id for Item #1:");
        bookIdLabel.setBorder(new EmptyBorder(0,0,0,20));
        bookIdInput = new JTextField();
        bookIdInput.setColumns(40);

        panel.add(bookIdLabel);
        panel.add(bookIdInput);

        JLabel quantityLabel = new JLabel("Enter quantity for Item #1:");
        quantityInput = new JTextField();
        quantityInput.setColumns(40);

        panel.add(quantityLabel);
        panel.add(quantityInput);

        JLabel itemInfoLabel = new JLabel("Item #1 info:");
        itemInfoInput = new JTextField();
        itemInfoInput.setEnabled(false);
        itemInfoInput.setColumns(40);

        panel.add(itemInfoLabel);
        panel.add(itemInfoInput);

        JLabel totalLabel = new JLabel("Order subtotal for 0 item(s):");
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

        System.out.println(new Date());

    }

    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == process ){

                String bookId = bookIdInput.getText().toString();
                amtItems = Integer.parseInt(numItemsInput.getText().toString());

                //If customer wants no item, disable process button
                if(amtItems <=  0 || itemNum > amtItems)
                {
                    process.setEnabled(false);
                }
                else {

                    numItemsInput.setEnabled(false);
                    bookInfo = Search(bookId);

                    if(bookInfo == null){
                        JOptionPane.showMessageDialog(null, String.format("Book Id " + bookId + " not in file"));
                    }
                    else{
                        JOptionPane.showMessageDialog(null, String.format("Item #" + itemNum + " accepted"));
                        itemInfoInput.setText(bookInfo);

                        process.setEnabled(false);
                        confirm.setEnabled(true);
                        view.setEnabled(true);

                        if(itemNum == amtItems)
                            finish.setEnabled(true);

                        itemNum++;
                    }
                }
            }

            else if(e.getSource() == confirm ){

                int quantity = Integer.parseInt(quantityInput.getText().toString());
                int discount = getDiscount(quantity);
                float price = Float.parseFloat(String.format("%.2f",getPrice(discount)).toString());
                totalPrice += price * quantity;

                bookInfo = bookInfo + " " + quantity + " " + discount  + "% $" + price;
                itemInfoInput.setText(bookInfo);
                totalInput.setText("$" + totalPrice);

                bookIdInput.setText("");
                quantityInput.setText("");

                process.setText("Process Item #" + itemNum);
                process.setEnabled(true);
                confirm.setText("Confirm Item #" + itemNum);
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

                SimpleDateFormat dateFormat = new SimpleDateFormat("yy/mm/dd hh:mm:ss");
                Date date = new Date();
                dateFormat.applyPattern("dd/mm/yy hh:mm:ss ");
                String dateId = dateFormat.format(date);
                dateId = dateId.replaceAll("\\s","");

                displayInfo.append(date + "\n");
                displayInfo.append("Number of line items: " + amtItems + "\n");
                displayInfo.append("Item# / ID / Title / Price / Qty / Disc % /Subtotal: " + "\n\n");

                for(int i = 0; i < books.size(); i++){
                    displayInfo.append(i+1 + ". " + books.get(i) + "\n");
                    booksToOutput.append(dateId+ ", " + books.get(i));
                }

                displayInfo.append("\n\nOrder subtotal: " + totalPrice + "\n\n");
                displayInfo.append("Tax rate: %6\n\n");

                float tax = (float) (totalPrice * .06);
                float priceWithTax = (float) (totalPrice + tax);


                displayInfo.append("Tax amount: $"  + tax + "\n\n");
                displayInfo.append("Order Total: $" + priceWithTax + "\n\n");

                displayInfo.append("Thanks for shopping at Ye Olde Book Shoppe");
                JOptionPane.showMessageDialog(null, displayInfo );

                WriteFile(booksToOutput);


            }

            else if(e.getSource() == newOrder ){
                amtItems = 1;
                numItemsInput.setText("");
                numItemsInput.setEnabled(true);
                books.clear();
            }

            else if(e.getSource() == exit ){
                System.exit(0);
            }

        }
    }
}
