import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class BlackJack {
    private static final String MESSAGE_FOR_DRAWN_CARD_FACE_UP = "%s に %d 枚目のカード「%s」が配られました。 %n";
    private static final String MESSAGE_FOR_DRAWN_CARD_FACE_DOWN = "%s に %d 枚目のカード「？」が配られました。 %n";
    private static final String MESSAGE_FOR_DEALER_TURN_UP_HAND = "ディーラーの「？」のカードは「%s」でした。 %n";

    private static final String MESSAGE_FOR_WIN_WITH_BLACK_JACK = "%s はブラックジャックで勝ちました %n";
    private static final String MESSAGE_FOR_WIN = "%s は勝ちました %n";
    private static final String MESSAGE_FOR_DRAW = "%s は引き分けました %n";
    private static final String MESSAGE_FOR_LOSE = "%s は負けました %n";
    private static final String MESSAGE_FOR_BLACK_JACK = "%s はブラックジャックです %n";
    private static final String MESSAGE_FOR_BUSTED = "%s はバーストしました %n";

    private static final String MESSAGE_FOR_COIN_AMOUNT = ":: %s の所持コイン= %d %n";
    private static final String MESSAGE_FOR_ACTION_BET = "%d コインを賭けた %n";
    private static final String MESSAGE_FOR_PAY_BACK_AT_DRAW = "[引き分け] %d コインの払い戻し %n";
    private static final String MESSAGE_FOR_PAY_BACK_AT_WIN = "[勝ち] %d コインの払い戻し %n";
    private static final String MESSAGE_FOR_PAY_BACK_AT_BLACK_JACK = "[BLACK JACK] %d コインの払い戻し %n";

    private static final String MESSAGE_FOR_TOTAL_VALUE = "%S の合計は %d です。 %n";

    private static final String MESSAGE_FOR_REQUIRED_HIT_OR_STAND = "もう１枚カードを引きますか？(Y/N): ";
    private static final String MESSAGE_FOR_INVALID_INPUT = "Y か N で回答してください。";

    private static final String ROUND_START_LINE = "■ ラウンド %d : ---------------------------------- %n";
    private static final String EMPTY_LINE = "";

    private static final Scanner STDIN = new Scanner(System.in);

    private static final int NUM_OF_DEALER = 1;
    private static final int NUM_OF_PLAYERS = 2;
    private static final int NUM_OF_MEMBERS = NUM_OF_DEALER + NUM_OF_PLAYERS;
    private static final int NUM_OF_DRAWN_CARDS_AT_FIRST = 2;
    private static final int SECOND_HAND_INDX = 1;

    private static final String PLAYER_NAME_FORMAT = "プレーヤー %d ";
    private static final String DEALER_NAME = "ディーラー";

    private static final String[] SUIT_TYPES = { "スペード", "ダイヤ", "クラブ", "ハート", };

    private static final String ACE_CARD = "A";
    private static final String[] RANK_TYPES = { "A", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "J", "Q", "K", };

    private static final int FACE_CARD_VALUE = 10;
    private static final int ACE_CARD_AS_SMALL_VALUE = 1;
    private static final int ACE_CARD_AS_LARGE_VALUE = 11;
    private static final int DEALER_LIMIT_VALUE = 17;
    private static final int BLACK_JACK_VALUE = 21;

    private static final int COIN_AMOUNT_AT_STARTING = 100;
    private static final int AMOUNT_OF_ONE_BET = 10;

    private static final int AMOUNT_OF_COIN_BACK_AT_DRAW = 10;
    private static final int AMOUNT_OF_COIN_BACK_AT_NORMAL_WIN = 20;
    private static final int AMOUNT_OF_COIN_BACK_AT_BLACK_JACK = 30;

    public static void main(String[] args) {
        List<Integer> coins = initCoins(NUM_OF_PLAYERS,
                COIN_AMOUNT_AT_STARTING);
        int round = 0;
        playGame(coins, round);
    }

    private static void playGame(List<Integer> coins, int round) {
        showRoundStartLine(++round);
        bet(coins);
        showMessageActionBet(AMOUNT_OF_ONE_BET);
        showCoinAmount(coins);

        List<String> deck = new LinkedList<>();
        prepareDeck(deck);

        List<List<String>> eachHands = initEachHands(NUM_OF_MEMBERS);
        drawCardsAtFirst(deck, eachHands, coins, NUM_OF_DRAWN_CARDS_AT_FIRST);

        oparateByPlayer(deck, eachHands);
        operateByDealer(deck, eachHands);

        showResults(eachHands);
        computeCoins(eachHands, coins);

        if (!isAllPlayersNoCoin(coins)) {
            showCoinAmount(coins);
            playGame(coins, round);
            return;
        }
    }

    private static void showResults(List<List<String>> eachHands) {
        int index = 0;
        while (!isOverMaxPlayerIndex(NUM_OF_PLAYERS, index)) {
            showResultInCaseOfDraw(eachHands, index);
            showResultInCaseOfWin(eachHands, index);
            showResultInCaseOfLose(eachHands, index);
            index++;
        }
    }

    private static boolean isOverMaxPlayerIndex(int numOfPlayers, int index) {
        return index >= NUM_OF_PLAYERS;
    }

    private static void showResultInCaseOfWin(List<List<String>> eachHands,
            int index) {
        if (isWinPlayer(eachHands, index)) {
            List<String> playerHand = getPlayerHand(eachHands, index);

            if (isBlackJack(playerHand)) {
                showMessageWinBlackJack(getPlayerName(index));
                showMessagePayBackAtBlackJack();

            } else {
                showMessageWin(getPlayerName(index));
                showMessagePayBackAtNormalWin();
            }
            show(EMPTY_LINE);
        }
    }

    private static void showResultInCaseOfDraw(List<List<String>> eachHands,
            int index) {
        if (isDraw(eachHands, index)) {
            showMessageDraw(getPlayerName(index));
            showMessagePayBackAtDraw();
            show(EMPTY_LINE);
        }
    }

    private static void showResultInCaseOfLose(List<List<String>> eachHands,
            int index) {
        if (isLosePlayer(eachHands, index)) {
            showMessageLose(getPlayerName(index));
            show(EMPTY_LINE);
        }
    }

    private static void computeCoins(List<List<String>> eachHands,
            List<Integer> coins) {
        int index = 0;
        while (!isOverMaxPlayerIndex(NUM_OF_PLAYERS, index)) {
            computeCoinInCaseOfDraw(eachHands, index, coins);
            computeCoinInCaseOfWin(eachHands, index, coins);
            computeCoinInCaseOfLose(eachHands, index, coins);
            index++;
        }
    }

    private static void computeCoinInCaseOfWin(List<List<String>> eachHands,
            int index, List<Integer> coins) {
        if (isWinPlayer(eachHands, index)) {
            List<String> playerHand = getPlayerHand(eachHands, index);

            if (isBlackJack(playerHand)) {
                coins.set(index,
                        coins.get(index) + AMOUNT_OF_COIN_BACK_AT_BLACK_JACK);

            } else {
                coins.set(index,
                        coins.get(index) + AMOUNT_OF_COIN_BACK_AT_NORMAL_WIN);
            }
        }
    }

    private static void computeCoinInCaseOfDraw(List<List<String>> eachHands,
            int index, List<Integer> coins) {
        if (isDraw(eachHands, index)) {
            coins.set(index, coins.get(index) + AMOUNT_OF_COIN_BACK_AT_DRAW);
        }
    }

    private static void computeCoinInCaseOfLose(List<List<String>> eachHands,
            int index, List<Integer> coins) {
        if (isLosePlayer(eachHands, index)) {
            // No operations.
        }
    }

    private static void show(String message) {
        System.out.println(message);
    }

    private static void showWithNoLine(String message) {
        System.out.print(message);
    }

    private static void showMessageWinBlackJack(String name) {
        System.out.format(MESSAGE_FOR_WIN_WITH_BLACK_JACK, name);
    }

    private static void showMessageWin(String name) {
        System.out.format(MESSAGE_FOR_WIN, name);
    }

    private static void showMessageDraw(String name) {
        System.out.format(MESSAGE_FOR_DRAW, name);
    }

    private static void showMessageLose(String name) {
        System.out.format(MESSAGE_FOR_LOSE, name);
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

    private static boolean isAllPlayersNoCoin(List<Integer> coins) {
        for (int coin : coins) {
            if (coin >= AMOUNT_OF_ONE_BET) {
                return false;
            }
        }
        return true;
    }

    private static void showCoinAmount(List<Integer> coins) {
        int index = 0;
        while (!isOverMaxPlayerIndex(NUM_OF_PLAYERS, index)) {
            System.out.format(MESSAGE_FOR_COIN_AMOUNT, getPlayerName(index),
                    coins.get(index));
            index++;
        }
        show(EMPTY_LINE);
    }

    private static List<String> getDealerHand(List<List<String>> eachHands) {
        return eachHands.get(eachHands.size() - 1);
    }

    private static List<String> getPlayerHand(List<List<String>> eachHands,
            int index) {
        return eachHands.get(index);
    }

    private static boolean isBlackJack(List<String> hand) {
        return has(hand, ACE_CARD) && hand.size() == 2
                && calcValue(hand) == BLACK_JACK_VALUE;
    }

    private static boolean has(List<String> hand, String card) {
        return hand.contains(card);
    }

    private static void bet(List<Integer> coins) {
        int index = 0;
        while (!isOverMaxPlayerIndex(NUM_OF_PLAYERS, index)) {
            int coin = coins.get(index);
            if (coin != 0) {
                coins.set(index, coin - AMOUNT_OF_ONE_BET);
            }
            index++;
        }
    }

    private static void showMessageActionBet(int amount) {
        System.out.format(MESSAGE_FOR_ACTION_BET, amount);
    }

    private static boolean isWinPlayer(List<List<String>> eachHands,
            int index) {
        List<String> playerHand = getPlayerHand(eachHands, index);
        return (isMoreThanDealerValue(eachHands, index)
                && !isBusted(playerHand))
                || isOnlyDealerBusted(eachHands, index);
    }

    private static boolean isLosePlayer(List<List<String>> eachHands,
            int index) {
        return !isWinPlayer(eachHands, index) && !isDraw(eachHands, index);
    }

    private static boolean isDraw(List<List<String>> eachHands, int index) {
        List<String> playerHand = getPlayerHand(eachHands, index);
        List<String> dealerHand = getDealerHand(eachHands);
        return calcValue(playerHand) == calcValue(dealerHand)
                || (isBusted(playerHand) && isBusted(dealerHand));
    }

    private static boolean isOnlyDealerBusted(List<List<String>> eachHands,
            int index) {
        List<String> playerHand = getPlayerHand(eachHands, index);
        List<String> dealerHand = getDealerHand(eachHands);
        return !isBusted(playerHand) && isBusted(dealerHand);
    }

    private static boolean isBusted(List<String> hand) {
        return calcValue(hand) > BLACK_JACK_VALUE;
    }

    private static boolean isMoreThanDealerValue(List<List<String>> eachHands,
            int index) {
        List<String> playerHand = getPlayerHand(eachHands, index);
        List<String> dealerHand = getDealerHand(eachHands);
        return calcValue(playerHand) > calcValue(dealerHand);
    }

    private static List<List<String>> initEachHands(int numOfmembers) {
        List<List<String>> eachHands = new ArrayList<>();
        int index = 0;
        while (!isOverMaxMemberIndex(numOfmembers, index)) {
            eachHands.add(new ArrayList<>());
            index++;
        }
        return eachHands;
    }

    private static boolean isOverMaxMemberIndex(int numOfmembers, int index) {
        return index > numOfmembers;
    }

    private static void oparateByPlayer(List<String> deck,
            List<List<String>> eachHands) {
        int index = 0;
        while (!isOverMaxPlayerIndex(NUM_OF_PLAYERS, index)) {
            List<String> playerHand = getPlayerHand(eachHands, index);
            actHitOrStandByPlayer(deck, playerHand, index);
            index++;
        }
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
                showDrawnCardFaceUp(DEALER_NAME, hand);
            }
            showTotalValue(DEALER_NAME, hand);
            isDealt = true;
        }

        runInCaseOfBusted(DEALER_NAME, hand);
        runInCaseOfBlackJack(DEALER_NAME, hand);

        if (!isDealt) {
            showTotalValue(DEALER_NAME, hand);
        }
        show(EMPTY_LINE);
    }

    private static void runInCaseOfBlackJack(String name, List<String> hand) {
        if (isBlackJack(hand)) {
            showMessageBlackJack(name);
        }
    }

    private static void runInCaseOfBusted(String name, List<String> hand) {
        if (isBusted(hand)) {
            showMessageBusted(name);
            ;
        }
    }

    private static boolean isValueOverDealerLimit(List<String> hand) {
        return calcValue(hand) >= DEALER_LIMIT_VALUE;
    }

    private static void actHitOrStandByPlayer(List<String> deck,
            List<String> hand, int index) {
        if (isBlackJack(hand)) {
            showMessageBlackJack(getPlayerName(index));
            show(EMPTY_LINE);
            return;
        }

        showTotalValue(getPlayerName(index), hand);
        String inputtedUserAnswer = requireHitOrStand();

        while (isChooseActionHit(inputtedUserAnswer)) {
            draw(deck, hand);
            showDrawnCardFaceUp(getPlayerName(index), hand);
            showTotalValue(getPlayerName(index), hand);

            if (isBusted(hand)) {
                showMessageBusted(getPlayerName(index));
                show(EMPTY_LINE);
                break;
            }

            if (isBlackJack(hand)) {
                showMessageBlackJack(getPlayerName(index));
                show(EMPTY_LINE);
                break;
            }
            inputtedUserAnswer = requireHitOrStand();
        }
    }

    private static void showMessageBusted(String name) {
        System.out.format(MESSAGE_FOR_BUSTED, name);
    }

    private static void showMessageBlackJack(String name) {
        System.out.format(MESSAGE_FOR_BLACK_JACK, name);
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

    private static void showTotalValue(String name, List<String> hand) {
        int value = calcValue(hand);
        System.out.format(MESSAGE_FOR_TOTAL_VALUE, name, value);
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
            List<List<String>> eachHands, List<Integer> coins, int drawTimes) {
        int lastDrawTimes = drawTimes - 1;

        List<String> dealerHand = getDealerHand(eachHands);

        for (int i = 0; i < drawTimes; i++) {

            for (int j = 0; j < NUM_OF_PLAYERS; j++) {
                if (coins.get(j) == 0) {
                    continue;
                }

                List<String> playerHand = getPlayerHand(eachHands, j);
                draw(deck, playerHand);
                showDrawnCardFaceUp(getPlayerName(j), playerHand);
            }

            draw(deck, dealerHand);

            if (i == lastDrawTimes) {
                showDrawnCardFaceDown(DEALER_NAME, i);
                continue;
            }
            showDrawnCardFaceUp(DEALER_NAME, dealerHand);
            show(EMPTY_LINE);
        }
        show(EMPTY_LINE);
    }

    private static String getPlayerName(int i) {
        return String.format(PLAYER_NAME_FORMAT, ++i);
    }

    private static void println() {
        System.out.println();
    }

    private static void showDrawnCardFaceDown(String name, int index) {
        System.out.format(MESSAGE_FOR_DRAWN_CARD_FACE_DOWN, name, ++index);
    }

    private static void showDrawnCardFaceUp(String name, List<String> hands) {
        System.out.format(MESSAGE_FOR_DRAWN_CARD_FACE_UP, name, hands.size(),
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

    private static List<Integer> initCoins(int numOfPlayers, int amount) {
        List<Integer> coins = new ArrayList<>();
        for (int i = 0; i < numOfPlayers; i++) {
            coins.add(amount);
        }
        return coins;
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
