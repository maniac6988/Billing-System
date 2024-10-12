import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

class Item {
    private String name;
    private double price;

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}

public class BillingSystem {
    private JFrame frame;
    private JTextField itemNameField;
    private JTextField itemPriceField;
    private JTextArea receiptArea;
    private JButton addItemButton;
    private JButton checkoutButton;
    private JButton newEntryButton;
    private JButton clearReceiptButton;
    private ArrayList<Item> cart;
    private double totalAmount;

    public BillingSystem() {
        cart = new ArrayList<>();
        totalAmount = 0.0;
        createUI();
    }

    private void createUI() {
        frame = new JFrame("Billing System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400); // Increased size for better layout
        frame.setLayout(new BorderLayout());

        // Panel for item input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        inputPanel.add(new JLabel("Item Name:"));
        itemNameField = new JTextField();
        inputPanel.add(itemNameField);

        inputPanel.add(new JLabel("Item Price:"));
        itemPriceField = new JTextField();
        inputPanel.add(itemPriceField);

        addItemButton = new JButton("Add Item");
        inputPanel.add(addItemButton);
        
        newEntryButton = new JButton("New Entry");
        inputPanel.add(newEntryButton);

        checkoutButton = new JButton("Checkout");
        inputPanel.add(checkoutButton);

        clearReceiptButton = new JButton("Clear Receipt");
        inputPanel.add(clearReceiptButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        // Area for receipt display
        receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Action listeners
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = itemNameField.getText();
                double price;

                try {
                    price = Double.parseDouble(itemPriceField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid price.");
                    return;
                }

                Item item = new Item(name, price);
                cart.add(item);
                totalAmount += price;

                receiptArea.append("Added: " + item.getName() + " - $" + new DecimalFormat("#.00").format(item.getPrice()) + "\n");
                itemNameField.setText("");
                itemPriceField.setText("");
            }
        });

        newEntryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the input fields for new entry
                itemNameField.setText("");
                itemPriceField.setText("");
            }
        });

        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder receipt = new StringBuilder();
                receipt.append("Receipt:\n");
                receipt.append("====================\n");

                for (Item item : cart) {
                    receipt.append(item.getName() + " - $" + new DecimalFormat("#.00").format(item.getPrice()) + "\n");
                }
                receipt.append("====================\n");
                receipt.append("Total Amount: $" + new DecimalFormat("#.00").format(totalAmount) + "\n");

                receiptArea.setText(receipt.toString());
                totalAmount = 0.0; // Reset total after checkout
                cart.clear(); // Clear the cart
            }
        });

        clearReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the receipt area
                receiptArea.setText("");
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BillingSystem();
            }
        });
    }
}
