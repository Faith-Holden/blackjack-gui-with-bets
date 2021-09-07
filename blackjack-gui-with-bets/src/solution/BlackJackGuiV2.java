package solution;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.image.Image;
import resources.classes.BlackjackHand;
import resources.classes.Card;
import resources.classes.Deck;

public class BlackJackGuiV2 extends Application{
    Canvas canvas = new Canvas(99*5, (123*2)+150);
    Image cardImages =new Image("resources\\images\\cards.png");
    boolean isOnGoingGame = false;
    Label dealerInfo;
    Button newGameButton;
    Button standButton;
    Button hitButton;
    Deck deck;
    BlackjackHand dealerHand;
    BlackjackHand playerHand;
    TextField betField;
    int moneyPool = 100;
    int bet = 0;



    public void start(Stage primaryStage){

        BorderPane root = new BorderPane();
        root.setStyle("-fx-border-color: black; -fx-border-width: 5px; " +
                "-fx-background-color: black");
        primaryStage.setResizable(false);
        root.setCenter(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        dealerInfo = new Label("Welcome to BlackJack, The dealer is ready to play.");
        root.setTop(dealerInfo);
        dealerInfo.setFont(Font.font("",FontWeight.BOLD, 20));
        dealerInfo.setAlignment(Pos.BASELINE_CENTER);
        dealerInfo.setPrefWidth(canvas.getWidth()+10);
        dealerInfo.setMinHeight(80);
        dealerInfo.setStyle("-fx-border-color: black; -fx-border-width: 5px; " +
                "-fx-background-color: gray");

        hitButton = new Button("Hit");
        hitButton.setPrefWidth(canvas.getWidth()/5);
        hitButton.setOnAction(evt -> onClickedHitButton());
        hitButton.setDisable(true);

        standButton = new Button("Stand");
        standButton.setPrefWidth(canvas.getWidth()/5);
        standButton.setOnAction(evt-> onCLickedStandButton());
        standButton.setDisable(true);

        newGameButton = new Button("Start Game");
        newGameButton.setDefaultButton(true);
        newGameButton.setOnAction(evt -> onClickedStartGame());
        newGameButton.setPrefWidth(canvas.getWidth()/5);

        Label betInfo = new Label("Your Bet = ");
        betInfo.setPrefWidth((canvas.getWidth()/5)-10);
        betInfo.setFont(Font.font("",FontWeight.BOLD, 15));
        betInfo.setTextFill(Color.GRAY);
        betInfo.setAlignment(Pos.BASELINE_RIGHT);

        betField =  new TextField("");
        betField.setPrefWidth((canvas.getWidth()/5)-10);
        betField.requestFocus();

        HBox buttonBar = new HBox();
        root.setBottom(buttonBar);
        buttonBar.getChildren().add(hitButton);
        buttonBar.getChildren().add(standButton);
        buttonBar.getChildren().add(newGameButton);
        buttonBar.getChildren().add(betInfo);
        buttonBar.getChildren().add(betField);
        buttonBar.setSpacing(5);
        buttonBar.setStyle("-fx-border-color: black; -fx-border-width: 5px; " +
                "-fx-background-color: black");

        drawTable();
        primaryStage.show();
    }



    private void onClickedStartGame(){
        drawTable();
        deck = new Deck();
        deck.shuffle();
        dealerHand = new BlackjackHand();
        playerHand = new BlackjackHand();
        isOnGoingGame =true;
        toggleButtons();
        verifyBet();
        drawBetInfo();
        if(bet<1||bet>moneyPool){
            toggleButtons();
            return;
        }

        //deal initial hand and draw it
        playerHand.addCard(deck.dealCard());
        playerHand.addCard(deck.dealCard());
        dealerHand.addCard(deck.dealCard());
        dealerHand.addCard(deck.dealCard());
        drawCard(dealerHand.getCard(0),10,20);
        drawCard(null, 99, 20);
        for(int i = 0; i<playerHand.getCardCount(); i++){
            drawCard(playerHand.getCard(i), ((89*i)+10),(int)canvas.getHeight()-153);
        }

        dealerInfo.setText("");

        if(dealerHand.getBlackjackValue() == 21){
            dealerInfo.setText("The dealer won with a BlackJack, getting 21 points.");
            drawCard(dealerHand.getCard(1), 99,20);
            for(int i = 0; i<dealerHand.getCardCount(); i++){
                drawCard(dealerHand.getCard(i),((89*i)+10),20);
            }
            toggleButtons();
            didWinBet(false);
        }else if(playerHand.getBlackjackValue()==21){
            dealerInfo.setText("You won with a BlackJack, getting 21 points. " );
            for(int i = 0; i<dealerHand.getCardCount(); i++){
                drawCard(dealerHand.getCard(i),((89*i)+10),20);
            }
            toggleButtons();
            didWinBet(true);
        }else{
            dealerInfo.setText("Would you like to hit or stand?");
        }
    }

    private void onClickedHitButton(){
        playerHand.addCard(deck.dealCard());
        drawCard(playerHand.getCard(playerHand.getCardCount()-1),(89*(playerHand.getCardCount()-1)+10),(int)canvas.getHeight()-153);
        if(playerHand.getBlackjackValue()>21){
            dealerInfo.setText("Your went over 21. Your score was " + playerHand.getBlackjackValue()+
                    ".");
            toggleButtons();
            didWinBet(false);
            for(int i = 0; i<dealerHand.getCardCount(); i++){
                drawCard(dealerHand.getCard(i),((89*i)+10),20);
            }
        }else if(playerHand.getCardCount()==5){
            dealerInfo.setText("Your won by having 5 cards and less than 21 points."+
                    "\n Your score was " + playerHand.getBlackjackValue() + ".");
            toggleButtons();
            didWinBet(true);
            for(int i = 0; i<dealerHand.getCardCount(); i++){
                drawCard(dealerHand.getCard(i),((89*i)+10),20);
            }

        }else if(dealerHand.getBlackjackValue()<17){
            dealerHand.addCard(deck.dealCard());
            drawCard(null, (89*(dealerHand.getCardCount()-1)+10), 20);
            if(dealerHand.getBlackjackValue()>21){
                dealerInfo.setText("The dealer went over 21. Their score was " + dealerHand.getBlackjackValue()+
                        ".");
                for(int i = 0; i<dealerHand.getCardCount(); i++){
                    drawCard(dealerHand.getCard(i),((89*i)+10),20);
                }
                toggleButtons();
                didWinBet(true);
            }
        }
    }

    private void onCLickedStandButton(){
        while(true){
            if(dealerHand.getBlackjackValue()<17) {
                dealerHand.addCard(deck.dealCard());
                drawCard(null, (89 * (dealerHand.getCardCount() - 1) + 10), 20);
                if (dealerHand.getBlackjackValue() > 21) {
                    dealerInfo.setText("You won. The dealer went over 21. Their score was " +
                            dealerHand.getBlackjackValue() + ".");
                    for (int i = 0; i < dealerHand.getCardCount(); i++) {
                        drawCard(dealerHand.getCard(i), ((89 * i) + 10), 20);
                    }
                    toggleButtons();
                    didWinBet(true);
                }
            } else{
                if(dealerHand.getBlackjackValue()>21){
                    dealerInfo.setText("You won. The dealer went over 21. " +
                            "\n Their score was " +
                            dealerHand.getBlackjackValue() + ".");
                    didWinBet(true);
                } else if(playerHand.getBlackjackValue()>dealerHand.getBlackjackValue()){
                    dealerInfo.setText("You won. Your scores were: \n You="+playerHand.getBlackjackValue()
                            +", dealer=" + dealerHand.getBlackjackValue()+".");
                    didWinBet(true);
                } else if(playerHand.getBlackjackValue()<dealerHand.getBlackjackValue()){
                    dealerInfo.setText("The dealer won. Your scores were: \n You=="+playerHand.getBlackjackValue()
                            +", dealer=" + dealerHand.getBlackjackValue()+".");
                    didWinBet(false);
                } else if(playerHand.getBlackjackValue()==dealerHand.getBlackjackValue()){
                    dealerInfo.setText("The dealer wins on ties. Your scores were "+playerHand.getBlackjackValue()
                            + ".");
                    didWinBet(false);
                }
                for (int i = 0; i < dealerHand.getCardCount(); i++) {
                    drawCard(dealerHand.getCard(i), ((89 * i) + 10), 20);
                }
                toggleButtons();
                break;
            }
        }
    }

    private void drawTable (){
        GraphicsContext graphicsContext =  canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.rgb(40,108,65));
        graphicsContext.fillRect(0,0, canvas.getWidth(),canvas.getHeight());

        graphicsContext.setFill(Color.BLACK);
        graphicsContext.setFont(Font.font("", FontWeight.BOLD, 15));
        graphicsContext.fillText("Dealer's Hand:", 10,15);
        graphicsContext.fillText("Your Hand:", 10,canvas.getHeight()-158);
        graphicsContext.fillText("You have $" + moneyPool + ".", 10, canvas.getHeight()-15);

    }

    private void drawCard(Card card, int xCoord, int yCoord) {
        int cardRow, cardCol;
        if (card == null) {
            cardRow = 4;   // row and column of a face down card
            cardCol = 2;
        }
        else {
            cardRow = 3 - card.getSuit();
            cardCol = card.getValue() - 1;
        }
        double sx,sy;  // top left corner of source rect for card in cardImages
        sx = 79 * cardCol;
        sy = 123 * cardRow;
        canvas.getGraphicsContext2D().drawImage(cardImages, sx,sy,79,123, xCoord,yCoord,79,123 );
    } // end drawCard()

    private void drawBetInfo(){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        String betString;
        graphicsContext.setFill(Color.rgb(40,108,65));
        graphicsContext.fillRect(0,170,canvas.getWidth(),50);
        graphicsContext.fillRect(0,canvas.getHeight()-30,canvas.getWidth(),20);

        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillText("You have $" + moneyPool + ".", 10, canvas.getHeight()-15);

        if(bet < 1|| bet>moneyPool){
            betString = "Sorry, that was not a legal bet."
                    +"\nPlease type a number from 1 to " + moneyPool + " and click \"Start Game\"";
        }else{
            betString = "You placed a bet for $" + bet + ".";
        }


        graphicsContext.fillText(betString, 10, canvas.getHeight()-208);
    }

    private void toggleButtons(){
        if(isOnGoingGame){
            newGameButton.setDisable(true);
            hitButton.setDisable(false);
            standButton.setDisable(false);
            betField.setEditable(false);
            isOnGoingGame = false;
        }else{
            newGameButton.setDisable(false);
            hitButton.setDisable(true);
            standButton.setDisable(true);
            betField.setEditable(true);
        }
    }

    private void verifyBet(){
        String betString = betField.getText();
        int betNumber;
        try{
            betNumber = Integer.parseInt(betString);
        }catch (NumberFormatException e){
            betField.selectAll();
            betNumber = -1;
            betField.requestFocus();
        }
        bet = betNumber;
    }

    private void didWinBet(boolean gameWon){
        if(gameWon){
            moneyPool +=bet;
        }else{
            moneyPool-=bet;
        }

        betField.requestFocus();
        drawBetInfo();
    }

    public static void main(String[]Args){
        launch(Args);
    }
}
