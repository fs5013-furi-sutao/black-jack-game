import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class BlackJack {
    private static final String MESSAGE_FOR_DRAWN_CARD_FACE_UP = "%s に「%s」が配られました。 %n";
    private static final String MESSAGE_FOR_DRAWN_CARD_FACE_DOWN = "%s に「？」が配られました。 %n";
    private static final String MESSAGE_FOR_DEALER_TURN_UP_HAND = "ディーラーの「？」のカードは「%s」でした。 %n";

    private static final String MESSAGE_FOR_BUSTED = "バーストしました";
    private static final String MESSAGE_FOR_LOSE = "あなたの負けです";
    private static final String MESSAGE_FOR_DRAW = "引き分けです";
    private static final String MESSAGE_FOR_WIN = "あなたの勝ちです";
    private static final String MESSAGE_FOR_BLACK_JACK = "ブラックジャックです";

    private static final String MESSAGE_FOR_COIN_AMOUNT = "[所持コイン] %d %n%n";
    private static final String MESSAGE_FOR_ACTION_BET = "%d コインを賭けた %n";
    private static final String MESSAGE_FOR_PAY_BACK_AT_DRAW = "[引き分け] %d の払い戻し %n";
    private static final String MESSAGE_FOR_PAY_BACK_AT_WIN = "[勝ち] %d の払い戻し %n";
    private static final String MESSAGE_FOR_PAY_BACK_AT_BLACK_JACK = "[BLACK JACK] %d の払い戻し %n";

    private static final String MESSAGE_FOR_VALUE_OF_DEALER = "ディーラーの合計は %d です。 %n";
    private static final String MESSAGE_FOR_VALUE_OF_PLAYER = "現在の合計は %d です。 %n";

    private static final String MESSAGE_FOR_REQUIRED_HIT_OR_STAND = "もう１枚カードを引きますか？(Y/N): ";
    private static final String MESSAGE_FOR_INVALID_INPUT = "Y か N で回答してください。";

    private static final String ROUND_START_LINE = "■ ラウンド %d : ---------------------------------- %n";

    private static final Scanner STDIN = new Scanner(System.in);

    private static final int NUM_OF_PLAYERS = 2;
    private static final int NUM_OF_DRAWN_CARDS_AT_FIRST = 2;
    private static final int SECOND_HAND_INDX = 1;

    private static final int PLAYER_INDX = 0;
    private static final int DEALER_INDX = 1;

    private static final String[] NAMES = { "あなた", "ディーラー", };

    private static final String[] SUIT_TYPES = { "スペード", "ダイヤ", "クラブ", "ハート", };

    private static final String ACE_CARD = "A";
    private static final String[] RANK_TYPES = { "A", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "J", "Q", "K", };

    private static final int FACE_CARD_VALUE = 10;
    private static final int ACE_CARD_AS_SMALL_VALUE = 1;
    private static final int ACE_CARD_AS_LARGE_VALUE = 11;
    private static final int DEALER_LIMIT_VALUE = 17;
    private static final int BLACK_JACK_VALUE = 21;

    private static final int COIN_AT_STARTNIG = 100;
    private static final int AMOUNT_OF_ONE_BET = 10;

    private static final int AMOUNT_OF_COIN_BACK_AT_DRAW = 10;
    private static final int AMOUNT_OF_COIN_BACK_AT_NORMAL_WIN = 20;
    private static final int AMOUNT_OF_COIN_BACK_AT_BLACK_JACK = 30;

    public static void main(String[] args) {
        int coins = COIN_AT_STARTNIG;
        int round = 0;
        playGame(coins, round);
    }

    private static void playGame(int coins, int round) {
        showRoundStartLine(++round);
        coins = bet(coins);
        showCoinAmount(coins);

        List<String> deck = new LinkedList<>();
        prepareDeck(deck);

        List<List<String>> eachHands = initEachHands(NUM_OF_PLAYERS);
        drawCardsAtFirst(deck, eachHands, NUM_OF_DRAWN_CARDS_AT_FIRST);

        oparateByPlayer(deck, eachHands);
        operateByDealer(deck, eachHands);

        judgeResult(deck, eachHands, coins, round);
        
        if (hasCoin(coins)) {
            showCoinAmount(coins);
            playGame(coins, round);
            return;
        }
    }

    private static void judgeResult(List<String> deck,
            List<List<String>> eachHands, int coins, int round) {
        coins = runInCaseOfDraw(eachHands, coins);
        coins = runInCaseOfWin(eachHands, coins);
        runInCaseOfLose(eachHands);
    }

    private static void runInCaseOfLose(List<List<String>> eachHands) {
        if (isLosePlayer(eachHands)) {
            show(MESSAGE_FOR_LOSE);
        }
    }

    private static int runInCaseOfWin(List<List<String>> eachHands, int coins) {
        if (isWinPlayer(eachHands)) {
            List<String> playerHand = getPlayerHand(eachHands);

            if (isBlackJack(playerHand)) {
                coins += AMOUNT_OF_COIN_BACK_AT_BLACK_JACK;
                showMessagePayBackAtBlackJack();
                show(MESSAGE_FOR_BLACK_JACK);
                showCoinAmount(coins);

            } else {
                coins += AMOUNT_OF_COIN_BACK_AT_NORMAL_WIN;
                showMessagePayBackAtNormalWin();
                showCoinAmount(coins);
            }

            show(MESSAGE_FOR_WIN);
        }
        return coins;
    }

    private static void show(String message) {
        System.out.println(message);
    }

    private static void showWithNoLine(String message) {
        System.out.print(message);
    }

    private static int runInCaseOfDraw(List<List<String>> eachHands,
            int coins) {
        if (isDraw(eachHands)) {
            coins += AMOUNT_OF_COIN_BACK_AT_DRAW;
            showMessagePayBackAtDraw();
            show(MESSAGE_FOR_DRAW);
            showCoinAmount(coins);
        }
        return coins;
    }

    private static void showMessagePayBackAtNormalWin() {
        System.out.format(MESSAGE_FOR_PAY_BACK_AT_WIN,
                AMOUNT_OF_COIN_BACK_AT_NORMAL_WIN);
    }

    private static void showMessagePayBackAtBlackJack() {
        System.out.format(MESSAGE_FOR_PAY_BACK_AT_BLACK_JACK,
                AMOUNT_OF_COIN_BACK_AT_BLACK_JACK);
    }

    private static void showMessagePayBackAtDraw() {
        System.out.format(MESSAGE_FOR_PAY_BACK_AT_DRAW,
                AMOUNT_OF_COIN_BACK_AT_DRAW);
    }

    private static void showRoundStartLine(int round) {
        System.out.format(ROUND_START_LINE, round);
    }

    private static boolean hasCoin(int coins) {
        return coins >= AMOUNT_OF_ONE_BET;
    }

    private static void showCoinAmount(int coins) {
        System.out.format(MESSAGE_FOR_COIN_AMOUNT, coins);
    }

    private static List<String> getDealerHand(List<List<String>> eachHands) {
        return eachHands.get(DEALER_INDX);
    }

    private static List<String> getPlayerHand(List<List<String>> eachHands) {
        return eachHands.get(PLAYER_INDX);
    }

    private static boolean isBlackJack(List<String> hand) {
        return has(hand, ACE_CARD) && hand.size() == 2 && calcValue(hand) == BLACK_JACK_VALUE;
    }

    private static boolean has(List<String> hand, String card) {
        return hand.contains(card);
    }

    private static int bet(int coins) {
        showMessageActionBet(AMOUNT_OF_ONE_BET);
        return coins - AMOUNT_OF_ONE_BET;
    }

    private static void showMessageActionBet(int amount) {
        System.out.format(MESSAGE_FOR_ACTION_BET, amount);
    }

    private static boolean isWinPlayer(List<List<String>> eachHands) {
        List<String> playerHand = getPlayerHand(eachHands);
        return (isMoreThanDealerValue(eachHands) && !isBusted(playerHand))
                || isOnlyDealerBusted(eachHands);
    }

    private static boolean isLosePlayer(List<List<String>> eachHands) {
        return !isWinPlayer(eachHands) && !isDraw(eachHands);
    }

    private static boolean isDraw(List<List<String>> eachHands) {
        List<String> playerHand = getPlayerHand(eachHands);
        List<String> dealerHand = getDealerHand(eachHands);
        return calcValue(playerHand) == calcValue(dealerHand)
                || (isBusted(playerHand) && isBusted(dealerHand));
    }

    private static boolean isOnlyDealerBusted(List<List<String>> eachHands) {
        List<String> playerHand = getPlayerHand(eachHands);
        List<String> dealerHand = getDealerHand(eachHands);
        return !isBusted(playerHand) && isBusted(dealerHand);
    }

    private static boolean isBusted(List<String> hand) {
        return calcValue(hand) > BLACK_JACK_VALUE;
    }

    private static boolean isMoreThanDealerValue(List<List<String>> eachHands) {
        List<String> playerHand = getPlayerHand(eachHands);
        List<String> dealerHand = getDealerHand(eachHands);
        return calcValue(playerHand) > calcValue(dealerHand);
    }

    private static List<List<String>> initEachHands(int numOfPlayers) {
        List<List<String>> eachHands = new ArrayList<>();
        for (int i = 0; i < numOfPlayers; i++) {
            eachHands.add(new ArrayList<>());
        }
        return eachHands;
    }

    private static void oparateByPlayer(List<String> deck,
            List<List<String>> eachHands) {
        List<String> playerHand = getPlayerHand(eachHands);
        actHitOrStandByPlayer(deck, playerHand);
    }

    private static void operateByDealer(List<String> deck,
            List<List<String>> eachHands) {
        List<String> hand = getDealerHand(eachHands);
        showDealerTurnUpHand(hand, SECOND_HAND_INDX);
        actHitOrStandByDealer(deck, hand);
    }

    private static void showDealerTurnUpHand(List<String> hand, int index) {
        System.out.format(MESSAGE_FOR_DEALER_TURN_UP_HAND, hand.get(index));
    }

    private static void actHitOrStandByDealer(List<String> deck,
            List<String> hand) {
        boolean isDealt = false;
        while (!isValueOverDealerLimit(hand)) {

            if (!isBusted(hand) && !isBlackJack(hand)) {
                draw(deck, hand);
                showDrawnCardFaceUp(NAMES[DEALER_INDX], hand);
            }
            showValueOfDealer(hand);
            isDealt = true;
        }

        runInCaseOfBusted(hand);
        runInCaseOfBlackJack(hand);

        if (!isDealt) {
            showValueOfDealer(hand);
        }
        println();
    }

    private static void runInCaseOfBlackJack(List<String> hand) {
        if (isBlackJack(hand)) {
            show(MESSAGE_FOR_BLACK_JACK);
        }
    }

    private static void runInCaseOfBusted(List<String> hand) {
        if (isBusted(hand)) {
            show(MESSAGE_FOR_BUSTED);
        }
    }

    private static boolean isValueOverDealerLimit(List<String> hand) {
        return calcValue(hand) >= DEALER_LIMIT_VALUE;
    }

    private static void actHitOrStandByPlayer(List<String> deck,
            List<String> hand) {
        if (isBlackJack(hand)) {
            show(MESSAGE_FOR_BUSTED);
            println();
            return;
        }

        showValueOfPlayer(hand);
        String inputtedUserAnswer = requireHitOrStand();

        while (isChooseActionHit(inputtedUserAnswer)) {
            draw(deck, hand);
            showDrawnCardFaceUp(NAMES[PLAYER_INDX], hand);
            showValueOfPlayer(hand);

            if (isBusted(hand)) {
                show(MESSAGE_FOR_BUSTED);
                println();
                break;
            }

            if (isBlackJack(hand)) {
                break;
            }
            inputtedUserAnswer = requireHitOrStand();
        }
    }

    private static String recieveInputtedYorN() {
        String inputtedStr = recieveInputtedStr();

        if (!isCorrectActionInRange(inputtedStr)) {
            show(MESSAGE_FOR_INVALID_INPUT);

            return requireHitOrStand();
        }
        return inputtedStr;
    }

    private static boolean isCorrectActionInRange(String str) {
        return isChooseActionHit(str) || isChooseActionStand(str);
    }

    private static boolean isChooseActionStand(String str) {
        return "N".equals(str) || "n".equals(str);
    }

    private static boolean isChooseActionHit(String str) {
        return "Y".equals(str) || "y".equals(str);
    }

    private static String recieveInputtedStr() {
        return STDIN.nextLine();
    }

    private static String requireHitOrStand() {
        showWithNoLine(MESSAGE_FOR_REQUIRED_HIT_OR_STAND);

        String inputtedUserAnswer = recieveInputtedYorN();
        println();
        return inputtedUserAnswer;
    }

    private static void showValueOfPlayer(List<String> hand) {
        int value = calcValue(hand);
        System.out.format(MESSAGE_FOR_VALUE_OF_PLAYER, value);
    }

    private static void showValueOfDealer(List<String> hand) {
        int value = calcValue(hand);
        System.out.format(MESSAGE_FOR_VALUE_OF_DEALER, value);
    }

    private static int calcValue(List<String> hand) {
        int value = 0;
        for (String card : hand) {
            if (!isFaceCard(card)) {
                value += parseToInt(card);
                continue;
            }

            if (!isAceCard(card)) {
                value += FACE_CARD_VALUE;
                continue;
            }

            value += calcValueIfAceCard(card, value);
        }

        return value;
    }

    private static int calcValueIfAceCard(String card, int value) {
        if (!isOverBusted(value, ACE_CARD_AS_LARGE_VALUE)) {
            return ACE_CARD_AS_LARGE_VALUE;
        }
        return ACE_CARD_AS_SMALL_VALUE;
    }

    private static boolean isOverBusted(int value, int adder) {
        return isBusted(value + adder);
    }

    private static boolean isBusted(int value) {
        return value > BLACK_JACK_VALUE;
    }

    private static boolean isAceCard(String card) {
        return ACE_CARD.equals(card);
    }

    private static boolean isFaceCard(String card) {
        try {
            parseToInt(card);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    private static int parseToInt(String card) {
        return Integer.parseInt(card);
    }

    private static void drawCardsAtFirst(List<String> deck,
            List<List<String>> eachHands, int drawTimes) {
        int lastDrawTimes = drawTimes - 1;

        List<String> playerHand = getPlayerHand(eachHands);
        List<String> dealerHand = getDealerHand(eachHands);

        for (int i = 0; i < drawTimes; i++) {

            draw(deck, playerHand);
            showDrawnCardFaceUp(NAMES[PLAYER_INDX], playerHand);
            draw(deck, dealerHand);

            if (i == lastDrawTimes) {
                showDrawnCardFaceDown(NAMES[DEALER_INDX]);
                println();
                continue;
            }
            showDrawnCardFaceUp(NAMES[DEALER_INDX], dealerHand);
            println();
        }
    }

    private static void println() {
        System.out.println();
    }

    private static void showDrawnCardFaceDown(String name) {
        System.out.format(MESSAGE_FOR_DRAWN_CARD_FACE_DOWN, name);
    }

    private static void showDrawnCardFaceUp(String name, List<String> hands) {
        System.out.format(MESSAGE_FOR_DRAWN_CARD_FACE_UP, name,
                getLastCardInHands(hands));
    }

    private static Object getLastCardInHands(List<String> hands) {
        int lastIndx = hands.size() - 1;
        return hands.get(lastIndx);
    }

    private static void draw(List<String> deck, List<String> hand) {
        String drawedCard = drawCard(deck);
        hand.add(drawedCard);
    }

    private static String drawCard(List<String> deck) {
        if (isDeckEmpty(deck)) {
            prepareDeck(deck);
        }

        int firstIndx = 0;
        String drawedCard = deck.get(firstIndx);
        deck.remove(firstIndx);
        return drawedCard;
    }

    private static boolean isDeckEmpty(List<String> deck) {
        return deck.size() == 0;
    }

    private static void initDeck(List<String> deck) {
        for (int i = 0; i < SUIT_TYPES.length; i++) {
            for (String rank : RANK_TYPES) {
                deck.add(rank);
            }
        }
    }

    private static void shuffleDeck(List<String> deck) {
        Collections.shuffle(deck);
    }

    private static void prepareDeck(List<String> deck) {
        initDeck(deck);
        shuffleDeck(deck);
    }
}
