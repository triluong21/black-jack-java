import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class BlackJack {
    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString(){
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) {
                if (value == "A") {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value); // for 2 through 10
        }

        public boolean isAce() {
            return value == "A";
        }

        public String getCardPath() {
            return "./cards/" + toString() + ".png";
        }

    }

    ArrayList<Card> deck;
    Random random = new Random();

    // Dealer
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    int dealerSum;
    int dealerAceCount;

    // Player
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;

    // Window

    int boardWidth = 600;
    int boardHeight = boardWidth;
    int cardWidth = 110; // ratio should be 1/1.4
    int cardHeight = 154;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
                // Draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                if (!stayButton.isEnabled()) {
                    hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getCardPath())).getImage();
                }
                g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

                // Draw dealer hand
                for (int i = 0; i < dealerHand.size(); i++) {
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getCardPath())).getImage();
                    g.drawImage(cardImg, cardWidth + 25 + (cardWidth +5)*i, 20, cardWidth, cardHeight, null);

                }

                // Draw player hand
                for (int i = 0; i < playerHand.size(); i++) {
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getCardPath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5)*i, 320, cardWidth, cardHeight, null);
                }

                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("STAY: ");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if (playerSum > 21) {
                        message = "You Lose!";
                    }
                    else if (dealerSum > 21) {
                        message = "You Win!";
                    }
                    // both player and dealer <= 21
                    else if (playerSum == dealerSum) {
                        message = "Tie!";
                    }
                    else if (playerSum > dealerSum) {
                        message = "You Win!";
                    }
                    else if (playerSum < dealerSum) {
                        message = "You Lose!";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);
                }
        
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    JPanel buttonJPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton stayButton = new JButton("Stay");


    BlackJack() {
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonJPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonJPanel.add(stayButton);
        frame.add(buttonJPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size()-1);
                playerSum += card.getValue();
                playerAceCount += card.isAce()? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce() > 21){ // A + 2 + J --> 1 + 2 + J
                    hitButton.setEnabled(false);
                }
                gamePanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);
                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce()? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();    
            }
        });

        gamePanel.repaint();
    }

    public void startGame() {
        // deck
        buildDeck();
        shuffleDeck();

        // Dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size()-1); // remove card from deck at last index
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("Dealer:");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        // Player
        playerHand = new ArrayList<>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size()-1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

        System.out.println("Player:");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);


    }

    public void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }
        System.out.println("Build Deck:");
        System.out.println(deck);        
    }

    
    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }
    
        System.out.println("Shuffle Deck:");
        System.out.println(deck);
    }

    public int reducePlayerAce(){
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce(){
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;            
        }
        return dealerSum;
    }
}
