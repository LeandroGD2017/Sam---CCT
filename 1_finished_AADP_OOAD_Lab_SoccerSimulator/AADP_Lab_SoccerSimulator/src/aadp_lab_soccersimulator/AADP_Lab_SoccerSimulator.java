/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package aadp_lab_soccersimulator;
/*
sam
-Gustavo Lambert - 2021278
-Leandro Guimareaes - 2019299
-Lucas Barros - 2020301
- https://github.com/LeandroGD2017/Sam---CCT

-Created separate methods for getting valid team names, player data, and number of matches to improve code readability and maintainability.
-Created a Player class to encapsulate player data and improve code organization.
-Extracted the code for inserting and displaying player data into separate methods for better code modularity.
-Improved variable names to make the code more self-explanatory.
-Added comments to explain the purpose of each method and section of code.
-These changes make the code more structured, easier to understand, and more maintainable.
*/        
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AADP_Lab_SoccerSimulator {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String dbName = "world_cup";
        String[] teams = {"Ireland", "Brazil", "Argentina", "Japan", "Mexico", "Senegal", "Tunisia", "Qatar"};
        String DB_URL = "jdbc:mysql://localhost/" + dbName;
        String USER = "football";
        String PASS = "Java is almost as good as football";
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/", USER, PASS);
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + dbName +";");
            stmt.execute("USE " + dbName + ";");
            //this is a try to open the database
            
            
            //
            for (String team : teams) {
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS "+ team + " ("
                                + "name VARCHAR(30) NOT NULL,"
                                + "number INT NOT NULL PRIMARY KEY,"
                                + "birth VARCHAR(30),"
                                + "position VARCHAR(30),"
                                + "goalsScored INT,"
                                + "background TEXT(1000));"
                );                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        int option;
        boolean exit = false;
        Scanner sc = new Scanner(System.in);
        
        do {
            System.out.println("Welcome! What would you like to do today? Please enter the number corresponding to your choice.");
            System.out.println("1. Enter a new player to a team.");
            System.out.println("2. See the players on a team.");
            System.out.println("3. Simulate a number of matches.");
            System.out.println("4. Exit the program.");            
            
            try {
                option = Integer.parseInt(sc.nextLine());
                
                if (option == 1) {
                    String teamName = getValidTeamName(sc, teams);
                    Player player = getPlayerData(sc);
                    insertPlayerData(DB_URL, USER, PASS, teamName, player);
                } else if (option == 2) {
                    String teamName = getValidTeamName(sc, teams);
                    displayPlayerData(DB_URL, USER, PASS, teamName);
                } else if (option == 3) {
                    int numMatches = getValidNumMatches(sc);
                    simulateMatches(numMatches, teams);
                } else if (option == 4) {
                    System.out.println("Goodbye, and thank you for using the SoccerSimulator!");
                    exit = true;
                } else {
                    System.out.println("That is an invalid number. Please try again!");
                }
            } catch (Exception e) {
                System.out.println("That is not a number. Please try again!");
            }         
        } while (!exit);   
    }     
    
    private static String getValidTeamName(Scanner sc, String[] teams) {
        boolean validTeam = false;
        String teamName;
        
        System.out.println("Please follow the instructions to enter player data.");
        
        do {
            System.out.println("For which team would you like to enter data?");
            teamName = sc.nextLine();
            
            for (String team : teams) {
                if (teamName.toLowerCase().equals(team.toLowerCase())) {
                    validTeam = true;
                    break;
                }
            }
            
            if (teamName.toLowerCase().equals("exit")) {
                break;
            }
            
            if (!validTeam) {
                System.out.println("That is not one of the teams. Please try again!");
            }
        } while (!validTeam);
        
        return teamName;
    }
    
    private static Player getPlayerData(Scanner sc) {
        String name;
        int number = 0;
        String birth;
        String position;
        int goalsScored = 0;
        String background;
        boolean validPlayer = false;
        
        System.out.println("Please enter the player's name: ");
        name = sc.nextLine();
        
        System.out.println("Please enter the player's number: ");
        
        do {
            try {
                number = Integer.parseInt(sc.nextLine()); 
                
                if (number < 1) {
                    System.out.println("Please enter a positive integer");
                } else {
                    validPlayer = true;
                }
            } catch (Exception e) {
                System.out.println("That is not a number. please try again!");
            }
        } while (!validPlayer);                                              
        
        System.out.println("Please enter the player's date of birth: ");
        birth = sc.nextLine();         
        
        System.out.println("Please enter the player's position: ");
        position = sc.nextLine(); 
        
        System.out.println("Please enter the number of goals the player has scored: ");
        validPlayer = false;
        
        do {
            try {
                goalsScored = Integer.parseInt(sc.nextLine()); 
                
                if (goalsScored < 1) {
                    System.out.println("Please enter a positive integer");
                } else {
                    validPlayer = true;
                }
            } catch (Exception e) {
                System.out.println("That is not a number. please try again!");
            }
        } while (!validPlayer);     
        
        System.out.println("Please enter the player's background: ");
        background = sc.nextLine();                          
        
        System.out.println("Thank you for entering a player"); 
        
        return new Player(name, number, birth, position, goalsScored, background);
    }
    
    private static void insertPlayerData(String DB_URL, String USER, String PASS, String teamName, Player player) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            stmt.execute(
                    String.format("INSERT INTO %s (name, number, birth, position, goalsScored, background) "
                            + "VALUES (\"%s\", %d, \"%s\", \"%s\", %d,  \"%s\") ;",
                            teamName, player.getName(), player.getNumber(), player.getBirth(), player.getPosition(), player.getGoalsScored(), player.getBackground())
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }      
    }
    
    private static void displayPlayerData(String DB_URL, String USER, String PASS, String teamName) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from " + teamName + ";");
            String name;
            int number;
            String birth;
            String position;
            int goalsScored;
            String background;
            
            while (rs.next()) {
                name = rs.getString("name");
                number = rs.getInt("number");
                birth = rs.getString("birth");
                position = rs.getString("position");
                goalsScored = rs.getInt("goalsScored");
                background = rs.getString("background");              
                
                System.out.println(String.format("Name: %s -- Number: %d -- DoB: %s -- Position: %s -- Number of goals scored: %d", name, number, birth, position, goalsScored));
                System.out.println("Background:");
                System.out.println(background);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }                     
    }
    
    private static int getValidNumMatches(Scanner sc) {
        System.out.println("How many matches would you like to simulate?");
        Boolean validNum = false;
        int numMatches = 0;
        
        do {
            try {
                numMatches = Integer.parseInt(sc.nextLine()); 
                
                if (numMatches < 1) {
                    System.out.println("Please enter a positive integer");
                } else {
                    validNum = true;
                }
            } catch (Exception e) {
                System.out.println("That is not a number. please try again!");
            }
        } while (!validNum);
        
        return numMatches;
    }
    
    private static void simulateMatches(int numMatches, String[] teams) {
        for (int matchNum = 1; matchNum <= numMatches; matchNum++) {
            int team1Num = (int) (Math.floor(Math.random() * teams.length));
            String team1 = teams[team1Num];
            int team2Num;
            
            do {
                team2Num = (int) (Math.floor(Math.random() * teams.length));
            } while (team1Num == team2Num);
            
            String team2 = teams[team2Num];
            int team1Score = (int) (Math.floor(Math.random() * 6));
            int team2Score = (int) (Math.floor(Math.random() * 6));
            
            System.out.println("Time for match: " + matchNum);
            
            if (team1Score > team2Score) {
                System.out.println(String.format("Congratulations %s! %s scored %d goals and %s scored %d goals.", team1, team1, team1Score, team2, team2Score));
            } else if (team1Score < team2Score) {
                System.out.println(String.format("Congratulations %s! %s scored %d goals and %s scored %d goals.", team2, team1, team1Score, team2, team2Score));
            } else {
                System.out.println(String.format("It was a draw!! %s scored %d goals and %s scored %d goals.",  team1, team1Score, team2, team2Score));
            }
        }
    }
}

class Player {
    private final String name;
    private final int number;
    private final String birth;
    private final String position;
    private final int goalsScored;
    private final String background;
    //class player more organized
    public Player(String name, int number, String birth, String position, int goalsScored, String background) {
        this.name = name;
        this.number = number;
        this.birth = birth;
        this.position = position;
        this.goalsScored = goalsScored;
        this.background = background;
    }
    
    public String getName() {
        return name;
    }
    
    public int getNumber() {
        return number;
    }
    
    public String getBirth() {
        return birth;
    }
    
    public String getPosition() {
        return position;
    }
    
    public int getGoalsScored() {
        return goalsScored;
    }
    
    public String getBackground() {
        return background;
    }
}