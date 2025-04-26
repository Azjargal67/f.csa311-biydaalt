package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FlashcardSystem {
    private List<Card> cards;
    private CardOrganizer organizer;
    private List<Achievement> achievements;
    private int repetitions;
    private boolean invertCards;
    private long roundStartTime;
    private int roundCorrectAnswers;

    public FlashcardSystem() {
        cards = new ArrayList<>();
        organizer = new RandomSorter();
        achievements = new ArrayList<>();
        repetitions = 1;
        invertCards = false;
        initializeAchievements();
    }

    private void initializeAchievements() {
        achievements.add(new Achievement("CORRECT", "Suuliin toirogt buh carduudiig zuw hariulsan"));
        achievements.add(new Achievement("REPEAT", "Neg cardiig 5-aas deesh udaa zuw hariulsan"));
        achievements.add(new Achievement("CONFIDENT", "Neg cardiig dor hayj 3-aas deesh zuw hariulsan"));
        achievements.add(new Achievement("SPEED", "Carduudiig dundjaar 5-aas baga secondad hariulsan"));
    }

    public void loadCards(String filename) throws IOException {
        cards.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    cards.add(new Card(parts[0].trim(), parts[1].trim()));
                }
            }
        }
    }

    private void loadDefaultCards() {
        cards.clear();
        cards.add(new Card("1 + 1 = ?", "2"));
        cards.add(new Card("2 + 2 = ?", "4"));
        cards.add(new Card("11 ^ 2 = ?", "121"));
        cards.add(new Card("1 - 1 = ?", "0"));
    }

    private void loadCardsFromInput() {
        Scanner scanner = new Scanner(System.in);
        cards.clear();
        
        int cardCount;
        while (true) {
            System.out.print("Heden card nemeh we?: ");
            try {
                cardCount = Integer.parseInt(scanner.nextLine().trim());
                if (cardCount < 1) {
                    System.out.println("0-ees ih too oruulna uu.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. too oruulna.");
            }
        }
        
        System.out.println("Card oruulna uu (format: asuult|hariult):");
        for (int i = 1; i <= cardCount; i++) {
            while (true) {
                System.out.print("Card " + i + ": ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Hooson baina.Try again.");
                    continue;
                }
                String[] parts = input.split("\\|");
                if (parts.length == 2 && !parts[0].trim().isEmpty() && !parts[1].trim().isEmpty()) {
                    cards.add(new Card(parts[0].trim(), parts[1].trim()));
                    System.out.println("SUCCESS!");
                    break;
                } else {
                    System.out.println("Format buruu baina. Format: asuult|hariult");
                }
            }
        }
        
        System.out.println("\nTanii oruulsan card:");
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            System.out.println((i + 1) + ". Asuult: " + card.getQuestion() + " | Hariult: " + card.getAnswer());
        }
        while (true) {
            System.out.print("Oruulsan carduud zuw baina uu? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();
            if (confirmation.equals("yes")) {
                break;
            } else if (confirmation.equals("no")) {
                System.out.println("Dahin oruulah.");
                cards.clear();
                loadCardsFromInput();
                return;
            } else {
                System.out.println("'yes' or 'no'.");
            }
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        roundCorrectAnswers = 0;
        roundStartTime = System.currentTimeMillis();

        List<Card> organizedCards = organizer.organize(cards);
        int totalQuestions = organizedCards.size() * repetitions;

        System.out.println("\nCardiin daraalal:");
        for (int i = 0; i < organizedCards.size(); i++) {
            System.out.println((i + 1) + ". " + organizedCards.get(i).getQuestion());
        }

        for (Card card : organizedCards) {
            for (int i = 0; i < repetitions; i++) {
                String question = invertCards ? card.getAnswer() : card.getQuestion();
                String correctAnswer = invertCards ? card.getQuestion() : card.getAnswer();
                
                while (true) {
                    System.out.println("\nAsuult: " + question);
                    System.out.print("Hariult: ");
                    String userAnswer = scanner.nextLine().trim();
                    if (userAnswer.isEmpty()) {
                        System.out.println("Hooson baina dahin oroldono uu.");
                        continue;
                    }
                    
                    boolean correct = userAnswer.equalsIgnoreCase(correctAnswer);
                    card.recordAttempt(correct);
                    
                    if (correct) {
                        roundCorrectAnswers++;
                        System.out.println("Correct!");
                    } else {
                        System.out.println("Incorrect");
                    }
                    
                    updateAchievements(card);
                    break;
                }
            }
        }

        checkRoundAchievements();
        printStatistics(totalQuestions);
        printAchievements();
    }

    private void updateAchievements(Card card) {
        for (Achievement achievement : achievements) {
            if (achievement.getName().equals("REPEAT") && card.getTotalAttempts() > 5) {
                achievement.setAchieved(true);
            }
            if (achievement.getName().equals("CONFIDENT") && card.getCorrectCount() >= 3) {
                achievement.setAchieved(true);
            }
        }
    }

    private void checkRoundAchievements() {
        for (Achievement achievement : achievements) {
            if (achievement.getName().equals("CORRECT") && 
                roundCorrectAnswers == cards.size() * repetitions) {
                achievement.setAchieved(true);
            }
            if (achievement.getName().equals("SPEED")) {
                long duration = System.currentTimeMillis() - roundStartTime;
                double avgTime = (double)duration / (cards.size() * repetitions * 1000);
                if (avgTime < 5.0) {
                    achievement.setAchieved(true);
                }
            }
        }
    }

    private void printStatistics(int totalQuestions) {
        System.out.println("\n----- STATISTIC -----");
        System.out.println("Niit asuult: " + totalQuestions);
        System.out.println("Zuw hariulsan: " + roundCorrectAnswers);
        System.out.println("Buruu hariulsan: " + (totalQuestions - roundCorrectAnswers));
        System.out.println("Accuracy: " + String.format("%.2f%%", (double)roundCorrectAnswers / totalQuestions * 100));
    }

    private void printAchievements() {
        System.out.println("\nAchievement");
        boolean anyAchieved = false;
        for (Achievement achievement : achievements) {
            if (achievement.isAchieved()) {
                System.out.println(" ---CORRECT--- " + achievement.getName() + ": " + achievement.getDescription());
                anyAchieved = true;
            } else {
                System.out.println(" ---INCORRECT--- " + achievement.getName() + ": " + achievement.getDescription());
            }
        }
        if (!anyAchieved) {
            System.out.println("amjilt garaagui baina, dahin oroldono uu");
        }
    }

    public void setOrganizer(String order) {
        switch (order.toLowerCase()) {
            case "random":
                organizer = new RandomSorter();
                break;
            case "worst-first":
                organizer = new WorstFirstSorter();
                break;
            case "recent-mistakes-first":
                organizer = new RecentMistakesFirstSorter();
                break;
            default:
                throw new IllegalArgumentException("error: " + order);
        }
    }

    public void setRepetitions(int reps) {
        if (reps < 1) {
            throw new IllegalArgumentException("dawtalt dor hayj 1 baih");
        }
        this.repetitions = reps;
    }

    public void setInvertCards(boolean invert) {
        this.invertCards = invert;
    }

    public static void printHelp() {
        System.out.println("Хэрэглээ: java -jar FlashCard-1.0-SNAPSHOT.jar [--help]");
        System.out.println("Сонголтууд:");
        System.out.println("  --help\tЭндээс тусламжийн мэдээлэл харах");
        System.out.println("Анхаар: Энэ програм зөвхөн меню дээр суурилсан үйлдэл дэмждэг. Шууд файлын оролтыг дэмжихгүй.");
    }

    private boolean showMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n----- WELCOME TO FLASHCARD APP -----");
            System.out.println("1. Default cards");
            System.out.println("2. Card nemeh");
            System.out.println("3. Help");
            System.out.println("4. Exit");
            System.out.print(" (1-4): ");
            
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    System.out.println("Start...");
                    loadDefaultCards();
                    break;
                case "2":
                    System.out.println("Card nemeh:");
                    loadCardsFromInput();
                    break;
                case "3":
                    System.out.print("Файлын нэрийг оруулна уу (жишээ, cards.txt): ");
                    String filename = scanner.nextLine().trim();
                    try {
                        loadCards(filename);
                        System.out.println(filename + "-аас картуудыг ачааллаа");
                    } catch (IOException e) {
                        System.err.println("Файлыг уншихад алдаа гарлаа: " + e.getMessage());
                        System.out.println("Дахин оролдоно уу эсвэл өөр сонголт хийнэ үү.");
                        continue;
                    }
                    break;
                case "4":
                    System.out.println("THANKS FOR PLAYING! ^^");
                    return false;
                default:
                    System.out.println("(1-4).");
                    continue;
            }

            while (true) {
                System.out.print("Heden udaa dawtah we: ");
                try {
                    int reps = Integer.parseInt(scanner.nextLine().trim());
                    setRepetitions(reps);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("too oruulna uu.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            while (true) {
                System.out.println("\nCardiin daraalal songono uu:");
                System.out.println("1. Random");
                System.out.println("2. worst-first");
                System.out.println("3. recent-mistakes-first");
                System.out.print("(1-3): ");
                String orderChoice = scanner.nextLine().trim();
                try {
                    switch (orderChoice) {
                        case "1":
                            setOrganizer("random");
                            break;
                        case "2":
                            setOrganizer("worst-first");
                            break;
                        case "3":
                            setOrganizer("recent-mistakes-first");
                            break;
                        default:
                            System.out.println("(1-3).");
                            continue;
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }

            while (true) {
                System.out.print("Asuult hariultiin bair solih uu? (yes/no): ");
                String invertChoice = scanner.nextLine().trim().toLowerCase();
                if (invertChoice.equals("yes")) {
                    setInvertCards(true);
                    break;
                } else if (invertChoice.equals("no")) {
                    setInvertCards(false);
                    break;
                } else {
                    System.out.println("'yes' or 'no'.");
                }
            }

            return true;
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            printHelp();
            return;
        }

        FlashcardSystem system = new FlashcardSystem();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            if (!system.showMenu()) {
                break;
            }

            try {
                system.run();
                
                while (true) {
                    System.out.println("\ndahin togloh uu? (yes/no)");
                    String playAgain = scanner.nextLine().trim().toLowerCase();
                    if (playAgain.equals("yes")) {
                        break;
                    } else if (playAgain.equals("no")) {
                        System.out.println("THANKS FOR PLAYING! ^^");
                        return;
                    } else {
                        System.out.println("'yes' OR 'no'.");
                    }
                }
                system = new FlashcardSystem();
                
            } catch (Exception e) {
                System.err.println("error: " + e.getMessage());
                System.out.println("\ndahin oroldoh uu? (yes/no)");
                String tryAgain = scanner.nextLine().trim().toLowerCase();
                if (!tryAgain.equals("yes")) {
                    System.out.println("THANKS FOR PLAYING! ^^");
                    break;
                }
                system = new FlashcardSystem();
            }
        }
    }
}