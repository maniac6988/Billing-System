import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AuctionService {
    private String itemName;
    private double currentPrice;
    private String highestBidder;

    public void addItem(String itemName, double startingPrice) {
        this.itemName = itemName;
        this.currentPrice = startingPrice;
        this.highestBidder = "No bids yet";
    }

    public boolean placeBid(String bidder, double bidAmount) {
        if (bidAmount > currentPrice) {
            currentPrice = bidAmount;
            highestBidder = bidder;
            return true;
        } else {
            return false;
        }
    }

    public String getCurrentHighestBidder() {
        return highestBidder;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public String getItemName() {
        return itemName;
    }
}

public class AuctionGUI {
    private JFrame frame;
    private AuctionService auctionService;
    private JTextArea displayArea;
    private JTextField itemNameField;
    private JTextField startingPriceField;
    private JTextField bidderNameField;
    private JTextField bidAmountField;
    private JButton addItemButton;
    private JButton placeBidButton;
    private JButton startAuctionButton;
    private JLabel timerLabel;

    private volatile boolean auctionRunning = false; // Control auction state
    private int auctionTime = 60; // Auction duration in seconds

    public AuctionGUI() {
        auctionService = new AuctionService();
        createUI();
    }

    private void createUI() {
        frame = new JFrame("Multithreaded Auction Service");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Create main display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create panel for adding items
        JPanel addItemPanel = new JPanel();
        addItemPanel.setLayout(new GridLayout(3, 2));

        addItemPanel.add(new JLabel("Item Name:"));
        itemNameField = new JTextField();
        addItemPanel.add(itemNameField);

        addItemPanel.add(new JLabel("Starting Price:"));
        startingPriceField = new JTextField();
        addItemPanel.add(startingPriceField);

        addItemButton = new JButton("Add Item");
        addItemPanel.add(addItemButton);
        frame.add(addItemPanel, BorderLayout.NORTH);

        // Create panel for bidding
        JPanel bidPanel = new JPanel();
        bidPanel.setLayout(new GridLayout(3, 2));

        bidPanel.add(new JLabel("Bidder Name:"));
        bidderNameField = new JTextField();
        bidPanel.add(bidderNameField);

        bidPanel.add(new JLabel("Bid Amount:"));
        bidAmountField = new JTextField();
        bidPanel.add(bidAmountField);

        placeBidButton = new JButton("Place Bid");
        bidPanel.add(placeBidButton);
        frame.add(bidPanel, BorderLayout.SOUTH);

        // Add timer and auction control buttons
        JPanel buttonPanel = new JPanel();
        timerLabel = new JLabel("Time Left: N/A");
        startAuctionButton = new JButton("Start Auction (60 seconds)");

        buttonPanel.add(startAuctionButton);
        buttonPanel.add(timerLabel);
        frame.add(buttonPanel, BorderLayout.EAST);

        // Action listeners
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = itemNameField.getText();
                double startingPrice = Double.parseDouble(startingPriceField.getText());
                auctionService.addItem(name, startingPrice);
                displayArea.append("Added item: " + name + " with starting price: " + startingPrice + "\n");
                itemNameField.setText("");
                startingPriceField.setText("");
            }
        });

        placeBidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (auctionRunning) {
                    String bidder = bidderNameField.getText();
                    double bidAmount = Double.parseDouble(bidAmountField.getText());
                    boolean success = auctionService.placeBid(bidder, bidAmount);
                    if (success) {
                        displayArea.append("Bid placed successfully! " + bidder + " placed " + bidAmount + " on " + auctionService.getItemName() + "\n");
                    } else {
                        displayArea.append("Bid failed. Make sure the bid is higher than the current highest bid.\n");
                    }
                    bidderNameField.setText("");
                    bidAmountField.setText("");
                } else {
                    displayArea.append("Auction is not running. Please start the auction.\n");
                }
            }
        });

        startAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!auctionRunning) {
                    auctionRunning = true;
                    new Thread(new AuctionTimer()).start(); // Start auction timer in a new thread
                }
            }
        });

        frame.setVisible(true);
    }

    private class AuctionTimer implements Runnable {
        @Override
        public void run() {
            auctionTime = 60; // Reset auction time to 60 seconds
            while (auctionTime > 0 && auctionRunning) {
                try {
                    Thread.sleep(1000); // Wait for 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                auctionTime--;
                SwingUtilities.invokeLater(new Runnable() { // Update timer label on the Swing thread
                    @Override
                    public void run() {
                        timerLabel.setText("Time Left: " + auctionTime + " seconds");
                    }
                });
            }
            auctionRunning = false; // Stop auction
            SwingUtilities.invokeLater(new Runnable() { // Display final auction result
                @Override
                public void run() {
                    displayArea.append("Auction ended! Winner: " + auctionService.getCurrentHighestBidder() + " with a bid of " + auctionService.getCurrentPrice() + "\n");
                    timerLabel.setText("Time Left: Auction Ended");
                }
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AuctionGUI();
            }
        });
    }
}
