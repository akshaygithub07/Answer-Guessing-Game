import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.math.*;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.*;
import java.util.List;
//import java.util.Locale.Category;
//import java.util.jar.JarEntry;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
//import javax.swing.text.StyledEditorKit;

public class quizle extends Game  {
    protected  boolean yourTurn = false;
    Timer t;

    String selectedOption = new String();
    String getFavFruit = new String();
    String getFavFruit2 = new String();

    private JLabel time;
    private String wordleWord;
    //private String selectedWord;
    private double startTime;
    private double endTime;

    private boolean isPlayed = false;
    private boolean isWon = false;
    //private int level = 5;

    private int greenCounter = 0;
    private int yellowCounter = 0;

    private List<String> wordleWordsList;
    private List<String> wordleList = new ArrayList<>();
    private List<String> absoluteWord;
    private List<Double> scoresList = new ArrayList<>();

    //private List<String> wordlehintList;
    //private List<String> hintList = new ArrayList<>();
    //private List<String> absolutehint;
    private String wordlehint;

    private String user1_word = "";
    private String user2_word = "";

    private int wordCounter1 = 0;
    private int wordCounter2 = 0;

    private boolean is_2player_game = false;
   // private boolean is_online_game = false;

    private JLabel[][] word_player1;
    private JLabel[][] word_player2;
    private JPanel jpCenter;
    private double highestScore;
    private int wordnum;

    private JPanel jpTrial;
    private JLabel[][] letters;
    private final String[][] alphabet = { 
        {"Q","W","E","R","T","Y","U","I","O","P"},
        {"A","S","D","F","G","H","J","K","L",""},
        {"Z","X","C","V","B","N","M", "Enter", "Delete",""}
    };

    JPanel jpConfetti;


    public quizle() throws FileNotFoundException {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.BLUE);
        setLayout(new BorderLayout());

        setIconRegion();
        showTime();
        setNumberOfPlayer();

        try {
            winCounter();
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            gameCounter();
        } catch (IOException e) {
            System.out.println(e);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(quizle.class.getResourceAsStream("highScores.txt"), StandardCharsets.ISO_8859_1))) {
            Double score;
            score = Double.valueOf(reader.readLine());
            highestScore = score;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public double scoreCalculator(double startTime,double endTime,int greenCounter,int wordCounter1) {
        double score = (greenCounter / (((endTime - startTime) / 500000) + (wordCounter1))) + (5 * yellowCounter) ;
        if(score<100){
            score = score+100;
        }
        BigDecimal bd = new BigDecimal(score);
        bd = bd.round(new MathContext(3));
        return bd.doubleValue();
    }

    public void textImporter(String filename) throws FileNotFoundException {
        List<String> wordList = new ArrayList<>();
        List<String> hintList = new ArrayList<>();

        /* creating a dictianry of all the possible words */
        try (BufferedReader reader = new BufferedReader( new InputStreamReader(quizle.class.getResourceAsStream("dictionary.txt"), StandardCharsets.ISO_8859_1))) {
            String word;
            while ((word = reader.readLine()) != null) {
                wordleList.add(word.toUpperCase());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(filename);

        /* creating a array of words from selected category */
        try (BufferedReader reader = new BufferedReader( new InputStreamReader(quizle.class.getResourceAsStream("Categories/" + filename + ".txt"), StandardCharsets.ISO_8859_1))) {
            String word;
            while ((word = reader.readLine()) != null) {
                wordList.add(word.toUpperCase());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /* creating a array of hints from selected category */
        try (BufferedReader reader = new BufferedReader( new InputStreamReader(quizle.class.getResourceAsStream("Categories/" + filename + "Hints.txt"), StandardCharsets.ISO_8859_1))) {
            String hints;
            while ((hints = reader.readLine()) != null) {
                hintList.add(hints.toUpperCase());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        Random rand = new Random();
        wordnum = rand.nextInt(wordList.size());
        wordleWord = wordList.get(wordnum);
        wordleWordsList = Arrays.asList(wordleWord.split(""));
        wordlehint = hintList.get(wordnum);
        absoluteWord = wordleWordsList;

        System.out.println(wordleWord);
        System.out.println(wordlehint);
    }

    public void play(JLabel[][] word_player,int wordCounter,String user_word) throws IOException {
        isPlayed = true;
        winCounter();
        wordleWordsList = new ArrayList<String>(absoluteWord);
        user_word = user_word.toUpperCase();

        /* check if word exists */
        if(!checker(user_word)){
            JOptionPane.showMessageDialog(null,
                    user_word + " is not in our database , please enter another word", "Warning"
                    , JOptionPane.INFORMATION_MESSAGE);
            for (int i = 0; i<5;i++){
                word_player[wordCounter][i].setText("");
            }
        } else{
            String[] userWordsArray = user_word.split("");
            
            /* initially set all the grids gray */
            for (int i = 0; i < 5; i++) {
                word_player[wordCounter][i].setBackground(Color.gray);
            }
            
            /* set grid color green or yellow */
            for (int i = 0; i < 5; i++) {
                word_player[wordCounter][i].setText(userWordsArray[i]);

                if (wordleWordsList.contains(userWordsArray[i])) {
                    int index = wordleWordsList.indexOf(userWordsArray[i]);
                    if (userWordsArray[i].equals(wordleWordsList.get(i))) {
                        word_player[wordCounter][i].setBackground(Color.green);
                        greenCounter++;
                        wordleWordsList.set(i, "");
                    } else {
                        if(!wordleWordsList.get(index).equals(userWordsArray[index])){
                            wordleWordsList.set(index,"");
                            yellowCounter++;
                            word_player[wordCounter][i].setBackground(Color.yellow);
                        }
                    }
                }
            }

            for(int i = 0; i < 5; i++){
                for(int j = 0; j < 3; j++){
                    for(int k = 0; k < 10; k++){
                        if(letters[j][k].getText().equals(word_player[wordCounter][i].getText())) {
                            if((letters[j][k].getBackground() == Color.white || letters[j][k].getBackground() == Color.gray)) {
                                letters[j][k].setBackground(word_player[wordCounter][i].getBackground());
                            } else if(letters[j][k].getBackground() == Color.yellow &&
                                    word_player[wordCounter][i].getBackground() == Color.green) {
                                letters[j][k].setBackground(word_player[wordCounter][i].getBackground());
                            }
                        }
                    }
                }
            }

            wordCounter++;

            /* check if word enterd is correct and if only one player is playing */
            if (user_word.equals(wordleWord) && !is_2player_game) {
                endTime = System.currentTimeMillis();
                confetti();

                /* check for highscore */
                if(scoreCalculator(startTime,endTime,greenCounter,wordCounter1)>=highestScore){
                    JOptionPane.showMessageDialog(null,
                            "<html> Congratulations! You break the high score!" + "<br/><FONT COLOR=green>Highest score was: " + highestScore + "<br/><FONT COLOR=red> Your score is: " + scoreCalculator(startTime,endTime,greenCounter,wordCounter1), "Congratulations"
                            , JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "You win! Your score is: " + scoreCalculator(startTime, endTime, greenCounter, wordCounter1) + "\n" + "Highest Score Was: " + highestScore + " ;)", "Congratulations"
                            , JOptionPane.INFORMATION_MESSAGE);
                }
                
                ScoreWriter();
                isWon = true;
                winCounter();
                gameCounter();

            } else if(user_word.equals(wordleWord) ) {
                confetti();
                if (wordCounter1 == wordCounter2){
                    JOptionPane.showMessageDialog(null, "Player1 wins!", "Congrats", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Player2 wins!", "Congrats", JOptionPane.INFORMATION_MESSAGE);
                }

                isWon = true;
                winCounter();
                gameCounter();
            }

            if(!is_2player_game){
                wordCounter1++;
            }
            else if(is_2player_game){
                if(wordCounter1 == wordCounter2){
                    wordCounter1++;
                }
                else{
                    wordCounter2++;
                }
            }

        }

        if(wordCounter == 5 && !user_word.equals(wordleWord) && !is_2player_game) {
            JOptionPane.showMessageDialog(null,
                    "You lost!, The word was " + wordleWord, ":("
                    , JOptionPane.INFORMATION_MESSAGE);
            gameCounter();
        } else if(wordCounter == 5 && !user_word.equals(wordleWord) ){
            if (wordCounter1 > wordCounter2) {
                JOptionPane.showMessageDialog(null,
                        "Player1 lost!", ":("
                        , JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Player2 lost too!, The word was " + wordleWord, ":("
                        , JOptionPane.INFORMATION_MESSAGE);
                gameCounter();
            }
        }

        if(!is_2player_game){
            user1_word = "";
        } else if(is_2player_game){
            if(wordCounter1 == wordCounter2){
                user1_word = "";
            }
            else{
                user2_word = "";
            }
        }
    }

    public void guessField_Keyboard() {
        jpTrial = new JPanel();
        jpTrial.setLayout(null);

        letters = new JLabel[3][10];

        Border border = BorderFactory.createLineBorder(Color.black, 1);
        
        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < alphabet[i].length; j++) {
                letters[i][j] = new JLabel(alphabet[i][j], SwingConstants.CENTER);
                letters[i][j].setBorder(border);

                letters[i][j].setOpaque(true);
                letters[i][j].setBackground(Color.white);
                if(i == 1) {
                    letters[i][j].setBounds(j*35+30, i*25+325, 35, 25);
                }
                else {
                    letters[i][j].setBounds(j*35+15, i*25+325, 35, 25);
                }
                if(alphabet[i][j].equalsIgnoreCase("Enter" )) {
                    letters[i][j].setBounds(j*35+15, i*25+325, 50, 25);
                }
                else if ( alphabet[i][j].equalsIgnoreCase("Delete")) {
                    letters[i][j].setBounds(j*35+30, i*25+325, 50, 25);
                }
                if(alphabet[i][j].equals("")){
                    letters[i][j].setBorder(null);
                    letters[i][j].setOpaque(false);
                    letters[i][j].setBounds(j*35+60, i*25+15, 50, 25);
                }
                jpTrial.add(letters[i][j]);
            }

        }

        JLabel hintLabel = new JLabel("HINT: " + wordlehint);
        hintLabel.setBounds(40, 283, 300, 50);
        hintLabel.setHorizontalAlignment(JLabel.CENTER);
        jpTrial.add(hintLabel);
        
        word_player1 = new JLabel[5][5];

        border = BorderFactory.createLineBorder(Color.black, 2);

        for (int i = 0; i < word_player1.length; i++) {
            for (int j = 0; j < 5; j++) {
                word_player1[i][j] = new JLabel("", SwingConstants.CENTER);
                word_player1[i][j].setBorder(border);
                word_player1[i][j].setOpaque(true);
                word_player1[i][j].setBackground(Color.white);
                word_player1[i][j].setBounds(75*j+7, 60*i, 70, 50);
                jpTrial.add(word_player1[i][j]);
            }
        }
        jpTrial.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


            }

            @Override
            public void keyPressed(KeyEvent e) {
                String key = "";
                if(e.getKeyCode()>=65 && e.getKeyCode()<=90 && user1_word.length() < 5){
                    key += e.getKeyChar();
                    user1_word += e.getKeyChar();
                    key = key.toUpperCase();
                    word_player1[wordCounter1][user1_word.length()-1].setText(key);
                }
                else if(user1_word.length()==5 && e.getKeyCode()==10){
                    try {
                        play(word_player1, wordCounter1, user1_word);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else if(e.getKeyCode() == 8 && user1_word.length()>0){
                    word_player1[wordCounter1][user1_word.length()-1].setText("");
                    user1_word = user1_word.substring(0, user1_word.length()-1);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        jpTrial.requestFocusInWindow();
        jpTrial.setBackground(Color.white);
        add(jpTrial, BorderLayout.CENTER);
    }

    public void guessField_2players() {
        jpTrial = new JPanel();
        jpTrial.setLayout(null);

        letters = new JLabel[3][10];

        Border border = BorderFactory.createLineBorder(Color.black, 1);
        
        for (int i = 0; i < letters.length; i++) {
            for (int j = 0; j < alphabet[i].length; j++) {
                letters[i][j] = new JLabel(alphabet[i][j], SwingConstants.CENTER);
                letters[i][j].setBorder(border);
               

                letters[i][j].setOpaque(true);
                letters[i][j].setBackground(Color.white);
                if(i == 1) {
                    letters[i][j].setBounds(j*35+230, i*25+315, 35, 25);
                }
                else {
                    letters[i][j].setBounds(j*35+215, i*25+315, 35, 25);
                }
                if(alphabet[i][j].equalsIgnoreCase("Enter" )) {
                    letters[i][j].setBounds(j*35+215, i*25+315, 50, 25);
                }
                else if ( alphabet[i][j].equalsIgnoreCase("Delete")) {
                    letters[i][j].setBounds(j*35+230, i*25+315, 50, 25);
                }
                if(alphabet[i][j].equals("")){
                    letters[i][j].setBorder(null);
                    letters[i][j].setOpaque(false);
                    letters[i][j].setBounds(j*35+260, i*25+5, 50, 25);
                }
                jpTrial.add(letters[i][j]);

            }
        }
            
        JLabel hintLabel = new JLabel("HINT: " + wordlehint);
        hintLabel.setBounds(40, 283, 700, 50);
        hintLabel.setHorizontalAlignment(JLabel.CENTER);
        jpTrial.add(hintLabel);

        word_player1 = new JLabel[5][5];

        border = BorderFactory.createLineBorder(Color.black, 2);
        for (int i = 0; i < word_player1.length; i++) {
            for (int j = 0; j < 5; j++) {
                word_player1[i][j] = new JLabel("", SwingConstants.CENTER);
                word_player1[i][j].setBorder(border);
                word_player1[i][j].setOpaque(true);
                word_player1[i][j].setBackground(Color.white);
                word_player1[i][j].setBounds(75*j+7, 60*i, 70, 50);
                jpTrial.add(word_player1[i][j]);
            }
        }
        jpTrial.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


            }

            @Override
            public void keyPressed(KeyEvent e) {
                String key = "";
                if(e.getKeyCode()>=65 && e.getKeyCode()<=90 && user1_word.length() < 5 && wordCounter1 == wordCounter2){
                    key += e.getKeyChar();
                    user1_word += e.getKeyChar();
                    key = key.toUpperCase();
                    word_player1[wordCounter1][user1_word.length()-1].setText(key);
                }
                else if(user1_word.length()==5 && e.getKeyCode()==10 && wordCounter1 == wordCounter2){
                    try {
                        play(word_player1,wordCounter1,user1_word);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else if(e.getKeyCode() == 8 && user1_word.length()>0 && wordCounter1 == wordCounter2){
                    word_player1[wordCounter1][user1_word.length()-1].setText("");
                    user1_word = user1_word.substring(0, user1_word.length()-1);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        word_player2 = new JLabel[5][5];

        for (int i = 0; i < word_player2.length; i++) {
            for (int j = 0; j < 5; j++) {
                word_player2[i][j] = new JLabel("", SwingConstants.CENTER);
                word_player2[i][j].setBorder(border);
                word_player2[i][j].setOpaque(true);
                word_player2[i][j].setBackground(Color.white);
                word_player2[i][j].setBounds(75*j+407, 60*i, 70, 50);
                jpTrial.add(word_player2[i][j]);
            }
        }
        jpTrial.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {


            }
            @Override
            public void keyPressed(KeyEvent e) {
                String key = "";
                if(e.getKeyCode()>=65 && e.getKeyCode()<=90 && user2_word.length() < 5 && wordCounter1 > wordCounter2){
                    key += e.getKeyChar();
                    user2_word += e.getKeyChar();
                    key = key.toUpperCase();
                    word_player2[wordCounter2][user2_word.length()-1].setText(key);
                }
                else if(user2_word.length()==5 && e.getKeyCode()==10 && wordCounter1 > wordCounter2){
                    try {
                        play(word_player2,wordCounter2,user2_word);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                else if(e.getKeyCode() == 8 && user2_word.length()>0 && wordCounter1 > wordCounter2){
                    word_player2[wordCounter2][user2_word.length()-1].setText("");
                    user2_word = user2_word.substring(0, user2_word.length()-1);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        jpTrial.requestFocusInWindow();
        jpTrial.setBackground(Color.white);
        add(jpTrial, BorderLayout.CENTER);
    }

    public void setIconRegion() {
        JPanel IconPanel = new JPanel();
        ImageIcon image = new ImageIcon(getClass().getClassLoader().getResource("i.png"));
        JLabel icon = new JLabel(image);
        IconPanel.add(icon);
        IconPanel.setBackground(Color.white);
        add(IconPanel, BorderLayout.NORTH);
    }

    public void center_Region() {
        jpCenter = new JPanel(new GridBagLayout());
        JLabel bestOfLuckText = new JLabel("BEST OF THE LUCK");
        JButton startBtn = new JButton("Start");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.CENTER;

        jpCenter.add(bestOfLuckText, constraints);
        constraints.gridy = 1;
        jpCenter.add(startBtn, constraints);


        String[] optionsToChoose = {"foods", "physicsAndChem", "space","animals","tough"};

        selectedOption = (String) JOptionPane.showInputDialog(
                null,
                "Select prefered topic ",
                "Choose Topic",
                JOptionPane.QUESTION_MESSAGE,
                null,
                optionsToChoose,
                optionsToChoose[0]
            );
        
        jpCenter.setBackground(Color.white);

        /* Check if option is selected.
         * If not selected goback to previous screen.
        */

        if (selectedOption == null) {
            setNumberOfPlayer();
        } else {
            try {
                textImporter(selectedOption);
            } catch(IOException e){
                System.out.println(e);
            }

            startBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remove(jpCenter);
                    if (is_2player_game) {
                        guessField_2players();
                    } else {
                        guessField_Keyboard();
                    }
                    jpTrial.requestFocusInWindow();
                    revalidate();
                    repaint();
                    startTime = System.currentTimeMillis();
                }
            });

            jpCenter.setBackground(Color.white);
            add(jpCenter, BorderLayout.CENTER);
        }
   }


    public void setNumberOfPlayer() {
        JPanel jpPlayers = new JPanel();
        jpPlayers.setLayout(null);

        JLabel text1 = new JLabel("Please Select Game Mode!", SwingConstants.CENTER);
        text1.setBounds(100,90,200,20);
        jpPlayers.add(text1);

        JLabel text2 = new JLabel("Score is only available in single mode.", SwingConstants.CENTER);
        text2.setBounds(75,110,250,20);
        jpPlayers.add(text2);

        JButton playerOneBtn = new JButton("1 Player");
        playerOneBtn.setBounds(90,130,100,30);

        JButton playerTwoBtn = new JButton("2 Players");
        playerTwoBtn.setBounds(200,130,100,30);

        JButton rulesBtn = new JButton("Rules");
        rulesBtn.setBounds(100,300,200,40);

        JButton statsBtn = new JButton("Game Statistics");
        statsBtn.setBounds(100,350,200,40);

        playerOneBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remove(jpPlayers);
                setSize(400,630);
                setLocationRelativeTo(null);
                center_Region();
                revalidate();
                repaint();
            }
        });

        playerTwoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                is_2player_game = true;
                remove(jpPlayers);
                setSize(800,750);
                setLocationRelativeTo(null);
                center_Region();
                revalidate();
                repaint();
            }
        });

        rulesBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame S = new JFrame("Rules");
                S.setLayout(new GridBagLayout());
                S.getContentPane().setBackground(Color.white);
                
                JLabel announce = new JLabel("<html>1. You have six tries to guess the five-letter secret word. The five-letter secret word is the answer to the hint provided."  +"<br/>" +"<br/>"+ " 2. After each guess, the colour of the letter tiles will change to show how close your guess was to the actual secret word."+"<br/>"+"<br/>"+"-Green- correct letter in the correct place "+"<br/>"+"<br/>"+"-Yellow- The letter exists in the word but in another position. "+"<br/>"+"<br/>"+"-Grey- The letter does not exist in the word."+"<br/>"+"<br/>"+"3. You can use the keyboard display to type your word or you can use the actual keyboard. You'll see a Statistics dialogue after a successful guess or six unsuccessful guesses."+"<br/> <FONT size=5> "  );
                announce.setBounds(200,100,300,250);
                announce.setHorizontalAlignment(JLabel.CENTER);
                announce.setVerticalAlignment(JLabel.CENTER);

                S.setLayout(new BorderLayout());
                S.add(announce,BorderLayout.CENTER);
                S.setSize(600, 450);
                S.setLocationRelativeTo(null);
                S.setVisible(true);
            }
        });

        statsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame S = new JFrame("Stats");
                S.setLayout(new GridBagLayout());
                S.getContentPane().setBackground(Color.white);
                double temp = 0;
                double temp2 = 0;

                try {
                    temp2 = winCounter();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    temp = gameCounter();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                BigDecimal AI = new BigDecimal(temp2*100/temp);
                AI = AI.round(new MathContext(4));

                JLabel announce = new JLabel("<html>Highest score is: " + highestScore +"<br/>"+"<br/>" + " Games played so far: "+ temp+"<br/>" + "<br/> Games won so far: "+ temp2 +"<br/>"+ "<br/><FONT COLOR=green> Win percentage: "+ AI +"<br/>"+"<br/> <FONT size=5> "  );
                announce.setBounds(200,150,100,250);
                announce.setHorizontalAlignment(JLabel.CENTER);
                announce.setVerticalAlignment(JLabel.CENTER);
                S.setLayout(new BorderLayout());
                
                S.add(announce,BorderLayout.CENTER);
                S.setSize(350, 350);
                S.setLocationRelativeTo(null);
                S.setVisible(true);
            }
        });

        jpPlayers.add(playerOneBtn);
        jpPlayers.add(playerTwoBtn);
        jpPlayers.add(rulesBtn);
        jpPlayers.add(statsBtn);
        jpPlayers.setBackground(Color.white);
        add(jpPlayers, BorderLayout.CENTER);
    }

    /* check if word exists in the dictionary */
    public boolean checker(String user_word) {
        for (String s : wordleList) {
            if (s.equals(user_word)) {
                return true;
            }
        }
        return false;
    }

    public void showTime(){
        JPanel jpTime = new JPanel();
        time = new JLabel();
        time.setHorizontalAlignment(JLabel.CENTER);
        time.setFont(UIManager.getFont("Label.font").deriveFont(Font.ITALIC, 15f));
        time.setText(DateFormat.getDateTimeInstance().format(new Date()));
        time.setOpaque(true);
        jpTime.add(time);


        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time.setText(DateFormat.getDateTimeInstance().format(new Date()));
            }
        });
        
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.setInitialDelay(0);
        timer.start();

        add(jpTime, BorderLayout.SOUTH);
    }

    public void ScoreWriter() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(quizle.class.getResourceAsStream("highScores.txt"), StandardCharsets.ISO_8859_1))) {
            Double score;
            while ((score = Double.valueOf(reader.readLine())) != null) {
                scoresList.add(score);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        scoresList.add(scoreCalculator(startTime,endTime,greenCounter,wordCounter1));
        Collections.sort(scoresList);
        Collections.reverse(scoresList);

        try (PrintWriter writer = new PrintWriter(new FileWriter("highScores.txt"))) {
            for (Double score : scoresList) {
                writer.println(score);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* returns the number of times game has been played
     * also increments it if the game is completed
     */
    public int gameCounter() throws IOException {
        int gameCounter = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(quizle.class.getResourceAsStream("gameCount.txt"), StandardCharsets.ISO_8859_1))) {
            gameCounter = Integer.valueOf(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (isPlayed) {
            gameCounter++;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("gameCount.txt"), StandardCharsets.ISO_8859_1))) {
            writer.write(String.valueOf(gameCounter));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return gameCounter;
    }

    /* returns the number of times player has won
     * also increments it if the player has won 
     */
    public int winCounter() throws IOException {
        int winCounter = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(quizle.class.getResourceAsStream("winCount.txt"), StandardCharsets.ISO_8859_1))) {
            winCounter = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(isWon) {
            winCounter++;
        }

        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("winCount.txt"), StandardCharsets.ISO_8859_1))) {
            writer.write(String.valueOf(winCounter));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return winCounter;
    }

    public void confetti(){
        jpConfetti = new confetti();
        jpConfetti.setBackground(Color.white);

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        };
        t = new Timer(500,al);
        t.start();
        remove(jpTrial);
        add(jpConfetti,BorderLayout.CENTER);
        repaint();
    }
}