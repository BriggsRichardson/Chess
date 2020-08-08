/*
   Briggs Richardson

   Creates the JFrame, JPanel, and JButtons that are the graphics of the chess
   game. The JButtons contain icons that resemble the pieces of the game. The
   8x8 array of JButtons are maintained with the 8x8 array of Pieces in the
   LogicBoard class. The ChessGUI class is for the looks, the LogicBoard is for
   the logic. The inner class ChessPieceListener is what handles the
   communication between the Logic and the Graphics, a second inner class Action
   Listener is used for the options above the game, mainly for dictating if the
   game is played against a human or computer (indicated by boolean variable)

*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;

public class ChessGUI
{
    private static LogicBoard logic; // Logic instance
    private static ButtonTracker bt; // Stores coordinates for moves

    private JFrame frame;

    private JPanel chessBoardPanel;
    private JPanel optionPanel;

    private JButton[][] squares;
    private JButton newGame;
    private JButton playComputer;
    private JButton playHuman;

    // These variables are meant for the ActionListeners, to help
    // with the logic within the middle grounds of logic/graphics
    private static boolean inCheckMate = false;
    private static boolean inStaleMate = false;

    private boolean computerMode = false; // Option choice of which mode
    // the game is in
    private ImageIcon wPawn = new ImageIcon("../src/icons/wPawn.png");
    private ImageIcon bPawn = new ImageIcon("../src/icons/bPawn.png");
    private ImageIcon wKnight = new ImageIcon("../src/icons/wKnight.png");
    private ImageIcon bKnight = new ImageIcon("../src/icons/bKnight.png");
    private ImageIcon wBishop = new ImageIcon("../src/icons/wBishop.png");
    private ImageIcon bBishop = new ImageIcon("../src/icons/bBishop.png");
    private ImageIcon wRook = new ImageIcon("../src/icons/wRook.png");
    private ImageIcon bRook = new ImageIcon("../src/icons/bRook.png");
    private ImageIcon wQueen = new ImageIcon("../src/icons/wQueen.png");
    private ImageIcon bQueen = new ImageIcon("../src/icons/bQueen.png");
    private ImageIcon wKing = new ImageIcon("../src/icons/wKing.png");
    private ImageIcon bKing = new ImageIcon("../src/icons/bKing.png");

    public ChessGUI()
    {
        // Initialization of logic instance and sounds
        logic = new LogicBoard();
        createGUI();
    }

    // Initializes the JFrame, JPanels, and JButtons for the graphics
    private void createGUI()
    {
        // Creates the frame and layout of the application
        frame = new JFrame("Chess Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        chessBoardPanel = new JPanel();

        chessBoardPanel.setLayout(new GridLayout(8,8));
        optionPanel = new JPanel();
        optionPanel.setLayout(new GridLayout(1,3));

        // Sets up the JButtons (for each chess square)
        squares = new JButton[8][8];
        for (int row = 0; row < 8; ++row)
        {
            for (int col = 0; col < 8; ++col)
            {
                squares[row][col] = new JButton();
                squares[row][col].addActionListener(new ChessPieceListener(row, col));
                if (row%2 == 0 && col%2 == 1 || row%2 == 1 && col%2 == 0)
                    squares[row][col].setBackground(Color.BLACK);
                else
                    squares[row][col].setBackground(Color.WHITE);

                chessBoardPanel.add(squares[row][col]);
            }
        }

        // Sets up the icons
        startingIcons();

        // Add three options to the top of the game
        newGame = new JButton("New Game");
        playComputer = new JButton("Play Computer");
        playHuman = new JButton("Player Human");

        newGame.addActionListener(new OptionListener());
        playComputer.addActionListener(new OptionListener());
        playHuman.addActionListener(new OptionListener());

        optionPanel.add(newGame);
        optionPanel.add(playComputer);
        optionPanel.add(playHuman);

        // Sets the size and location of the application
        frame.add(chessBoardPanel, BorderLayout.SOUTH);
        frame.add(optionPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    // Assigns icons in starting game position
    // Used to initialize the game, and when the user wants to reset it.
    private void startingIcons()
    {
        for (int r = 2; r < 6; ++r)
        {
            for (int c = 0; c < 8; ++c)
            {
                squares[r][c].setIcon(null);
            }
        }
        for (int c = 0; c < 8; ++c)
        {
            squares[1][c].setIcon(bPawn);
            squares[6][c].setIcon(wPawn);
        }
        squares[7][0].setIcon(wRook);
        squares[7][1].setIcon(wKnight);
        squares[7][2].setIcon(wBishop);
        squares[7][3].setIcon(wQueen);
        squares[7][4].setIcon(wKing);
        squares[7][5].setIcon(wBishop);
        squares[7][6].setIcon(wKnight);
        squares[7][7].setIcon(wRook);
        squares[0][0].setIcon(bRook);
        squares[0][1].setIcon(bKnight);
        squares[0][2].setIcon(bBishop);
        squares[0][3].setIcon(bQueen);
        squares[0][4].setIcon(bKing);
        squares[0][5].setIcon(bBishop);
        squares[0][6].setIcon(bKnight);
        squares[0][7].setIcon(bRook);
    }

    // Moves an icon from the starting location to the ending position
    // The normal case for moving Icons
    public void moveIcon(int rStart, int cStart, int rEnd, int cEnd)
    {
        squares[rEnd][cEnd].setIcon(squares[rStart][cStart].getIcon());
        squares[rStart][cStart].setIcon(null);
    }

    // This method handles the special case of the king castling, since
    // two icons must move.
    public void castleIcons(int r1, int c1, int c2)
    {
        squares[r1][c2].setIcon(squares[r1][c1].getIcon());
        squares[r1][c1].setIcon(null); // Update King
        if (c2 == 2) // Update Rooks
        {
            squares[r1][3].setIcon(squares[r1][0].getIcon());
            squares[r1][0].setIcon(null);
        }
        else
        {
            squares[r1][5].setIcon(squares[r1][7].getIcon());
            squares[r1][7].setIcon(null);
        }
    }

    // This method handles the special case of En Passant
    public void removePassantedIcon(int r1, int c1, int r2, int c2)
    {
        if (r2 == 2) // En Passant on the Top of the Board
        {
            squares[r2+1][c2].setIcon(null);
        }
        else // En Passant on the Bottom of the Board
        {
            squares[r2-1][c2].setIcon(null);
        }
        squares[r2][c2].setIcon(squares[r1][c1].getIcon());
        squares[r1][c1].setIcon(null);
    }

    // This method handles the special case of the pawn entering its last
    // rank of the Board, being replaced with an Icon of the users choosing
    // indicated by "promotionChoice" variable.
    public void promoteIcon(int r1, int c1, int r2, int c2)
    {
        squares[r1][c1].setIcon(null);
        if (logic.getIsWhiteTurn())
            squares[r2][c2].setIcon(wQueen);
        else
            squares[r2][c2].setIcon(bQueen);
    }

    // The inner class which acts as the middle man between the Logic
    // and the Graphics. Each JButton contains a ChessPieceListener, so
    // that it can keep track of coordinates
    private class ChessPieceListener implements ActionListener
    {
        private int row; // The location of the clicked upon square
        private int col;

        public ChessPieceListener(int r, int c)
        {
            row = r;
            col = c;
        }

        public int getRow()
        {
            return row;
        }
        public int getCol()
        {
            return col;
        }

        public void actionPerformed(ActionEvent e)
        {
            // If bt is null, then this is the user's first click.
            if (bt == null)
            {
                // We assign the coordinates of the first click into ButtonTracker
                bt = new ButtonTracker(row, col);
                squares[row][col].setBackground(Color.GREEN); // GREEN INDICATES
            }                                                // 1st CLICK
            else    // if the ButtonTracker is not null, it's the second click
            {       // indicating a chess move is proposed.

                // Reset the color from green to either black or white.
                if (bt.r1%2 == 0 && bt.c1%2 == 1 || bt.r1%2 == 1 && bt.c1%2 == 0)
                    squares[bt.r1][bt.c1].setBackground(Color.BLACK);
                else
                    squares[bt.r1][bt.c1].setBackground(Color.WHITE);

                // bt.r1 and bt.c1 are the coordinates stored within ButtonTracker
                // holding the first click, the r2 and c2 are the coordinates of
                // the second click.
                int r2 = row;
                int c2 = col;

                // FIRST, check if the move is valid, and the game isn't over
                // USING the Logic instance methods.
                if (logic.isValid(bt.r1, bt.c1, r2, c2) && !inCheckMate &&
                        !inStaleMate)
                {
                    playTurn();

                    // This is the END of the turn, now we check if computerMode
                    // is on. And if it is, we handle the events for the computer's
                    // turn. If not, the other user plays as the turn is switched.
                    if (computerMode && !logic.getIsWhiteTurn() && !inCheckMate &&
                            !inStaleMate)
                    {
                        logic.computerTurn(); // generates the move the computer
                        // wants to play.
                        playTurn();
                    }
                }
                // Re-assign bt to null, indicating the next click
                // will be considered the first click (for next move)
                bt = null;
            }
        }

        /*
           This is middle ground between the Logic and the Graphics for the game

           This method handles maintaining the icons and the LogicBoard according
           to the moves throughout the game.

           If the computerMode is on and it's the computer's turn, change the
           coordinates to its choice (stored in CompLoc), and have the default
           promotion choice be queen.
        */
        public void playTurn()
        {
            // We establish the coordinates of the desired move
            int r2, c2;
            if (computerMode && !logic.getIsWhiteTurn())
            {
                // Computer's move. Change coordinates to its choice of move
                // logic.computerTurn() generates the CompLoc move...
                bt.r1 = CompLoc.r1;
                bt.c1 = CompLoc.c1;
                r2 = CompLoc.r2;
                c2 = CompLoc.c2;
            }
            else // User's turn. We leave bt as it is, and store r2,c2 as
            {    // the coordinates of the second click
                r2 = row;
                c2 = col;
            }

            // First, maintain storage for Pawn En Passant opportunities
            // and initializing a piece's first move (pawn, rook, king)
            // for future movements (castle, double pawn advance)

            logic.setFirstMove(bt.r1, bt.c1);

            // Condition for promotion, if pawn reaches end. Prompt for
            // promotion choice
            if (logic.pawnAtEnd(bt.r1, bt.c1, r2, c2))
                promoteIcon(bt.r1, bt.c1, r2, c2);
                // If a pawn captures en Passant, call special method which
                // handles removing icon for En Passant captures
            else if (logic.pawnEnPassant(bt.r1, bt.c1, r2, c2))
                removePassantedIcon(bt.r1, bt.c1, r2, c2);
                // If the king castles, call special method which handles
                // moving icons for the castle movement
            else if (logic.kingCastled(bt.r1, bt.c1, c2))
                castleIcons(bt.r1, bt.c1, c2);
            else // Normal case
                moveIcon(bt.r1, bt.c1, r2, c2);


            //logic.setPassantOpportunity(bt.r1, bt.c1, r2, c2);
            // Update the LogicBoard for all three situations through
            // logic.updateBoard
            logic.updateBoard(bt.r1, bt.c1, r2, c2);

            logic.switchTurn();

            // After switching turns, determine if the game is over
            // or if the king was put in check. Also, play sounds for
            // each situation (mate, check, and normal movement)
            if (logic.noMovesLeft())
            {
                if (logic.kingInCheck(logic.getIsWhiteTurn()))
                    inCheckMate = true;
                else
                    inStaleMate = true;
            }
            if (inCheckMate)
            {
                JOptionPane.showMessageDialog(null, "CHECKMATE");
            }
            else if (inStaleMate)
            {
                JOptionPane.showMessageDialog(null, "STALEMATE");
            }
        }
    }

    // The actionListener class that handles the three buttons at the top
    // of the game : "New Game" "Play Computer" "Play Human"
    private class OptionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == newGame)
            {
                // reset status of game to beginning
                startingIcons();
                logic.initializeGame();
                inCheckMate = false;
                inStaleMate = false;
            }
            else if (e.getSource() == playComputer)
            {
                // Reset the game, and turn computerMode on,
                computerMode = true;
                startingIcons();
                logic.initializeGame();
                inCheckMate = false;
                inStaleMate = false;
            }
            else if (e.getSource() == playHuman)
            {
                // Reset the game, and turn computerMode off.
                computerMode = false;
                startingIcons();
                logic.initializeGame();
                inCheckMate = false;
                inStaleMate = false;
            }
        }
    }
}






