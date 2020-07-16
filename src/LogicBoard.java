/*
   Briggs Richardson

   The LogicBoard is responsible for the LOGIC of the chess game. It contains
   a 8x8 array of Pieces, and maintains/updates the array of pieces along with
   the 8x8 array of JButtons w/ Icons in the ChessGUI class. The main function
   of the LogicBoard class entails determining if a move from 1 location on
   the array to another is a VALID move. There are several other methods that
   the ActionListner ChessPieceListener uses to keep the game updated. This
   class contains the tools, which the ChessPieceListener uses with many
   conditional statements.

   At the end is the AI component of the game, which uses the minimax algorithm
   to determine the best move, and stores it in the CompLoc record
*/

public class LogicBoard
{
    private Piece[][] pieces;
    private static boolean _isWhiteTurn;

    public Move lastMove;
    private BestMove bestMove = new BestMove();
    private Stack restore = new Stack();
    private static int DEPTH_MAX = 4;

    // Points for piece placement (positional rankings for AI board evaluation)
    private double[][] pawn_table =
            {
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0},
                    {1.0, 1.0, 2.0, 3.0, 3.0, 2.0, 1.0, 1.0},
                    {0.5, 0.5, 1.0, 2.5, 2.5, 1.0, 0.5, 0.5},
                    {0.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, 0.0},
                    {0.5, -.5, -1., 0.0, 0.0, -1., -.5, 0.5},
                    {0.5, 1.0, 1.0, -2., -2., 1.0, 1.0, 0.5},
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
            };

    private double[][] rook_table =
            {
                    {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                    {0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5},
                    {-.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -.5},
                    {-.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -.5},
                    {-.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -.5},
                    {-.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -.5},
                    {-.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -.5},
                    {0.0, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0},
            };

    private double[][] knight_table =
            {
                    {-5., -4., -3., -3., -3., -3., -4., -5.},
                    {-4., -2., 0.0, 0.0, 0.0, 0.0, -2., -4.},
                    {-3., 0.0, 1.0, 1.5, 1.5, 1.0, 0.0, -3.},
                    {-3., 0.5, 1.5, 2.0, 2.0, 1.5, 0.5, -3.},
                    {-3., 0.0, 1.5, 2.0, 2.0, 1.5, 0.0, -3.},
                    {-3., 0.5, 1.0, 1.5, 1.5, 1.0, 0.5, -3.},
                    {-4., -2., 0.0, 0.5, 0.5, 0.0, -2., -4.},
                    {-5., -4., -3., -3., -3., -3., -4., -5.},
            };

    private double[][] bishop_table =
            {
                    {-2., -1., -1., -1., -1., -1., -1., -2.},
                    {-1., 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.},
                    {-1., 0.0, 0.5, 1.0, 1.0, 0.5, 0.0, -1.},
                    {-1., 0.5, 0.5, 1.0, 1.0, 0.5, 0.5, -1.},
                    {-1., 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, -1.},
                    {-1., 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.},
                    {-1., 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, -1.},
                    {-2., -1., -1., -1., -1., -1., -1., -2.},
            };

    private double[][] queen_table =
            {
                    {-2., -1., -1., -.5, -.5, -1., -1., -2.},
                    {-1., 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.},
                    {-1., 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -1.},
                    {-.5, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -.5},
                    {0.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -.5},
                    {-1., 0.5, 0.5, 0.5, 0.5, 0.5, 0.0, -1.},
                    {-1., 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, -1.},
                    {-2., -1., -1., -.5, -.5, -1., -1., -2.},
            };

    private double[][] king_table =
            {
                    {-3., -4., -4., -5., -5., -4., -4., -3.},
                    {-3., -4., -4., -5., -5., -4., -4., -3.},
                    {-3., -4., -4., -5., -5., -4., -4., -3.},
                    {-3., -4., -4., -5., -5., -4., -4., -3.},
                    {-2., -3., -3., -4., -4., -3., -3., -2.},
                    {-1., -2., -2., -2., -2., -2., -2., -1.},
                    {2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0},
                    {2.0, 3.0, 1.0, 0.0, 0.0, 1.0, 3.0, 2.0},
            };
    // Array of all the possible moves (used for checking for
    // checkmate, stalemate, and generating a computer move)
    private PossibleCoordinates availableMoves[];

    public LogicBoard()
    {
        // Initialization of data member instances.
        pieces = new Piece[8][8];
        lastMove = new Move();
        availableMoves = new PossibleCoordinates[500];

        initializeGame();
    }

    /*
       initializeGame creates all the pieces and puts them in the starting
       position. It sets the first move to white, and initializes the data
       members of lastMove.
       - This is used when the game starts.
       - Also used when the user wants to RESET the game (thus the nulls)
    */
    public void initializeGame()
    {
        _isWhiteTurn = true;
        lastMove.r1 = 0;
        lastMove.r2 = 0;
        lastMove.c1 = 0;
        lastMove.c2 = 0;
        lastMove.name = null;

        // TRUE = White piece. FALSE = Black piece.
        for (int c = 0; c < 8; ++c)
        {
            pieces[1][c] = new Pawn (false);
            pieces[6][c] = new Pawn (true);
        }
        for (int c = 0; c < 8; ++c)
        {
            pieces[2][c] = null;
            pieces[3][c] = null;
            pieces[4][c] = null;
            pieces[5][c] = null;
        }
        pieces[7][2] = new Bishop (true);
        pieces[7][5] = new Bishop (true);
        pieces[0][2] = new Bishop (false);
        pieces[0][5] = new Bishop (false);
        pieces[0][0] = new Rook (false);
        pieces[0][7] = new Rook (false);
        pieces[7][0] = new Rook (true);
        pieces[7][7] = new Rook (true);
        pieces[0][1] = new Knight (false);
        pieces[0][6] = new Knight (false);
        pieces[7][1] = new Knight (true);
        pieces[7][6] = new Knight (true);
        pieces[0][3] = new Queen (false);
        pieces[7][3] = new Queen (true);
        pieces[0][4] = new King (false);
        pieces[7][4] = new King (true);
    }

    // Returns a boolean for whose turn it is (TRUE = WHITE) (FALSE = BLACK)
    public boolean getIsWhiteTurn()
    {
        return _isWhiteTurn;
    }

    public void switchTurn()
    {
        if (_isWhiteTurn)
            _isWhiteTurn = false;
        else
            _isWhiteTurn = true;
    }

    /*
       The method first checks if any basic rules are broken. Rules including:
       1) Can't move something that's not there
       2) Can't move when it's not one's turn
       3) Can't capture one's own piece
       4) Can't break the rules of how a specific piece CAN MOVE.
       5) Can't play a move that puts your king in check


       It then assumes the move is not valid unless proven otherwise
    */
    public boolean isValid(int r1, int c1, int r2, int c2)
    {
        if (pieces[r1][c1] == null)
            return false;
        else if (pieces[r1][c1].get_isWhite() != _isWhiteTurn)
            return false;
        else if (pieces[r2][c2] != null &&
                pieces[r2][c2].get_isWhite() == _isWhiteTurn)
            return false;
        else if (!pieces[r1][c1].canMove(r1, c1, r2, c2))
            return false;
        else if (moveEndangers(r1,c1,r2,c2))
            return false;

        boolean isValidMove = false;

        if (pieces[r1][c1] instanceof Pawn)
            isValidMove = validPawnMove(r1, c1, r2, c2);
        else if (pieces[r1][c1] instanceof King)
            isValidMove = validKingMove(r1, c1, r2, c2);
        else if (pieces[r1][c1] instanceof Knight)
            isValidMove = true;
        else
            isValidMove = isPathClear(r1, c1, r2, c2);

        return isValidMove;
    }

    // Returns true is the specified spot has a piece that is
    // the opposite color as the color whose turn it is
    private boolean isEnemyThere(int r, int c)
    {
        return (pieces[r][c] != null &&
                pieces[r][c].get_isWhite() != _isWhiteTurn);
    }

    /*
       Method that returns true if the squares inbetween the
       starting square and the target square are empty.
       Assumes the path is clear, if one of the squares is not
       null, then the path is proven to be not clear
    */
    private boolean isPathClear(int r1, int c1, int r2, int c2)
    {
        boolean pathClear = true;

        // Check horizontal paths, going from left to right
        if (r1 == r2)
        {
            if (c1 > c2)
            {
                int temp = c1;
                c1 = c2;
                c2 = temp;
            }
            for (int i = c1 + 1; i < c2; ++i)
            {
                if (pieces[r1][i] != null)
                    pathClear = false;
            }
        }
        // Check vertical paths, going top to bottom
        else if (c1 == c2)
        {
            if (r1 > r2)
            {
                int temp = r1;
                r1 = r2;
                r2 = temp;
            }
            for (int i = r1 + 1; i < r2; ++i)
            {
                if (pieces[i][c1] != null)
                    pathClear = false;
            }
        }
        // Check diagonal paths, from origin point to destination point
        else
        {
            int difference = r2 - r1;
            if (difference < 0)
                difference *= -1;
            for (int i = 0; i < difference - 1; ++i)
            {
                // Incrementing the squares depending on direction of diagonal line
                if (r1 > r2 && c1 < c2) // NORTH EAST
                {
                    --r1;
                    ++c1;
                }
                else if (r1 > r2 && c1 > c2) // NORTH WEST
                {
                    --r1;
                    --c1;
                }
                else if (r1 < r2 && c1 > c2) // SOUTH WEST
                {
                    ++r1;
                    --c1;
                }
                else // SOUTH EAST
                {
                    ++r1;
                    ++c1;
                }
                if (pieces[r1][c1] != null)
                    pathClear = false;
            }
        }
        return pathClear;
    }

    // Determines if a pawn can legally move on the board.
    // Assumes it can't move, unless proven otherwise.
    private boolean validPawnMove(int r1, int c1, int r2, int c2)
    {
        boolean isValid = false;
        int diffR;
        int diffC;

        // Straight move - path must be clear, and pawn can't capture
        if (c1 == c2)
            isValid = (isPathClear(r1,c1,r2,c2) &&
                    !isEnemyThere(r2,c2));
        else // Pawn wants to move diagonally (wants to capture)
        {
            isValid = isEnemyThere(r2, c2); // Only rule, there has to be an enemy
            if (!isValid )
            {
                // Only case the rule of having an enemy there is not applied is
                // En Passant. Then, the method checks lastMove to see if it can
                // move diagonally to capture En Passant.
                if (lastMove.name != null &&
                        (lastMove.name).equals("Pawn"))
                {
                    diffR = lastMove.r2 - lastMove.r1;
                    diffC = lastMove.c2 - c1;
                    diffR = (diffR < 0)? -1*diffR : diffR;
                    diffC = (diffC < 0)? -1*diffC : diffC;
                    if (diffR == 2 && diffC == 1)
                    {
                        // Now we check if the pawn is in the right spot to capture
                        // we already proved right column difference, now we need
                        // the right row placement (same row!)
                        if (lastMove.r2 == r1 && (c2 == lastMove.c2))
                        {
                            if (!isEnemyThere(r2, c2))
                                isValid = true;
                        }
                    }
                }
            }
        }
        return isValid;
    }

    // Determines if a king can move legally on the board
    // Assumes it can't, unless proven otherwise.
    private boolean validKingMove(int r1, int c1, int r2, int c2)
    {
        boolean isValid = false;

        int colDiff = c2 - c1;
        colDiff = (colDiff < 0)? (-1*colDiff):colDiff;

        if (colDiff != 2) // colDiff being 2 means the king wants to cancel
        {                 // Other than that, all the rules say is needs a
            isValid = isPathClear(r1, c1, r2, c2);            // clear path
        }
        else // Rules for castling
        {
         /*
            Checks both sides of the board (castle Long, castle Short)
            Rules: Needs a clear path between king and rook
            and both rook and king can not have moved already
         */
            if (c2 == 2 && isPathClear(r1, c1, r1, 0))
            {
                if (pieces[r1][c1] != null && pieces[r1][0] != null)
                {
                    isValid = (!pieces[r1][c1].get_hasMoved() &&
                            !pieces[r1][0].get_hasMoved());
                }
            }
            else if (c2 == 6 && isPathClear(r1, c1, r1, 7))
            {
                if (pieces[r1][c1] != null && pieces[r1][7] != null)
                {
                    isValid = (!pieces[r1][c1].get_hasMoved() &&
                            !pieces[r1][7].get_hasMoved());
                }
            }
        }
        return isValid;
    }

    // If a the chosen piece is a pawn, rook, or king, then it will
    // let it know it has been moved if it has never been moved.
    public void setFirstMove(int r, int c)
    {
        if (pieces[r][c] != null)
        {
            if (pieces[r][c] instanceof Pawn ||
                    pieces[r][c] instanceof Rook ||
                    pieces[r][c] instanceof King)
            {
                if (!pieces[r][c].get_hasMoved())
                    pieces[r][c].moved();
            }
        }
    }

    // Returns true if the king was castled, false otherwise
    public boolean kingCastled(int r1, int c1, int c2)
    {
        return (pieces[r1][c1] instanceof King &&
                (c2-c1 == 2 || c2-c1 == -2));
    }

    // Determines if a move is a Pawn capture via En Passant
    // Let's the updateBoard function know so it can update the board
    // in the special case
    public boolean pawnEnPassant(int r1, int c1, int r2, int c2)
    {
        boolean pawnEnPassant = false;
        // Capturing piece must be a pawn
        if (pieces[r1][c1] != null && pieces[r1][c1] instanceof Pawn)
        {
            // Captured piece must be a pawn OF OPPOSITE COLOR
            if (pieces[r1][c2] != null && pieces[r1][c2] instanceof Pawn
                    && pieces[r1][c1].get_isWhite() != pieces[r1][c2].get_isWhite())
            {
                // lastMove must be equal to pawn
                if (lastMove.name != null && (lastMove.name).equals("Pawn"))
                {
                    // Must be a capture move
                    if (c1 != c2)
                    {
                        // Must be at a proper location
                        if ((r2 == 2 || r2 == 5) && (r1 == 3 || r1 == 4))
                        {
                            if (pieces[r2][c2] == null)
                                pawnEnPassant = true;
                        }
                    }
                }
            }
        }
        return pawnEnPassant;
    }

    /*
       If a starting pawn advances two units forward, then it is temporarily
       stored in the instance lastMove, so that an enemy pawn may capture it
       via En Passant. If not, lastMove is reset.
    */
    public void setPassantOpportunity(int r1, int c1, int r2, int c2)
    {
        int diffR;
        diffR = r2 - r1;
        diffR = (diffR < 0)? -1 * diffR : diffR;
        if (pieces[r1][c1] instanceof Pawn && diffR == 2)
        {
            lastMove.r1 = r1;
            lastMove.c1 = c1;
            lastMove.r2 = r2;
            lastMove.c2 = c2;
            lastMove.name = "Pawn";
        }
        else
        {
            lastMove.r1 = 0;
            lastMove.c1 = 0;
            lastMove.r2 = 0;
            lastMove.c2 = 0;
            lastMove.name = null;
        }
    }

    // Moves the locations of Piece's in the [8][8] Piece array
    // If the board is updating, a valid move was played.
    // NOTE: If it's a castle, it let's the rook know it has been moved.
    public void updateBoard(int r1, int c1, int r2, int c2)
    {
        if (kingCastled(r1, c1, c2))
        {
            pieces[r2][c2] = pieces[r1][c1]; // Update King
            pieces[r1][c1] = null;
            if (c2 == 2)                     // Update Rook
            {
                pieces[r1][3] = pieces[r1][0];
                pieces[r1][3].moved();
                pieces[r1][0] = null;
            }
            else
            {
                pieces[r1][5] = pieces[r1][7];
                pieces[r1][5].moved();
                pieces[r1][7] = null;
            }
        }
        else if (pawnEnPassant(r1, c1, r2, c2))
        {
            pieces[r2][c2] = pieces[r1][c1];
            pieces[r1][c1] = null;
            if (pieces[r2][c2].get_isWhite())
            {
                pieces[r2+1][c2] = null;
            }
            else
            {
                pieces[r2-1][c2] = null;
            }
        }
        else if (pawnAtEnd(r1, c1, r2, c2))
        {
            pieces[r1][c1] = null;
            pieces[r2][c2] = null;
            if (_isWhiteTurn)
                pieces[r2][c2] = new Queen (true);
            else
                pieces[r2][c2] = new Queen (false);
        }
        else
        {
            setPassantOpportunity(r1, c1, r2, c2);
            pieces[r2][c2] = pieces[r1][c1];
            pieces[r1][c1] = null;
        }
    }

    // Returns true if a move would put a pawn at the last rank/first rank
    public boolean pawnAtEnd(int r1, int c1, int r2, int c2)
    {
        if (pieces[r1][c1] != null &&
                pieces[r1][c1] instanceof Pawn)
        {
            if (r2 == 0 || r2 == 7)
                return true;
        }
        return false;
    }

    // Determines if an enemy piece is threatening
    // a piece (used for the king)
    private boolean isThreatened(int r, int c, boolean isWhite)
    {
        boolean isThreat = false;

        for (int row = 0; row < 8; ++row)
        {
            for (int col = 0; col < 8; ++col)
            {

                if (pieces[row][col] != null &&
                        pieces[row][col].get_isWhite() != isWhite &&
                        pieces[row][col].canMove(row, col, r, c))
                {
                    if (pieces[row][col] instanceof Knight)
                        isThreat = true;
                    else if (pieces[row][col] instanceof Pawn && col != c)
                        isThreat = true;
                    else if (isPathClear (row, col, r, c) &&
                            (pieces[row][col] instanceof Bishop ||
                                    pieces[row][col] instanceof Queen ||
                                    pieces[row][col] instanceof Rook ||
                                    pieces[row][col] instanceof King ))
                    {
                        isThreat = true;
                    }
                }
            }
        }
        return isThreat;
    }

    // Searches through the logic board, finds the king of the passed in
    // boolean paramater's color. Then determines if that king is in check.
    public boolean kingInCheck(boolean isWhite)
    {
        int kingRow = 0;
        int kingCol = 0;
        for (int r = 0; r < 8; ++r)
        {
            for (int c = 0; c < 8; ++c)
            {
                if (pieces[r][c] != null &&
                        pieces[r][c] instanceof King &&
                        pieces[r][c].get_isWhite() == isWhite)
                {
                    kingRow = r;
                    kingCol = c;
                }
            }
        }
        // Using the king's position. Determines if there are threats
        return isThreatened(kingRow, kingCol, isWhite);
    }

    // Determines if moving a piece puts the king in check
    // 1) Make the move. 2) Determine if in check 3) Undo the move
    private boolean moveEndangers(int r1, int c1, int r2, int c2)
    {
        boolean endangers;
        Piece temp = pieces[r2][c2];
        pieces[r2][c2] = pieces[r1][c1];
        pieces[r1][c1] = null;
        if (kingInCheck(_isWhiteTurn))
            endangers = true;
        else
            endangers = false;
        pieces[r1][c1] = pieces[r2][c2];
        pieces[r2][c2] = temp;
        return endangers;
    }

    // Goes through the array, finds all pieces of the player whose turn it is,
    // determines if they have any available moves left. This is used for
    // Checkmate and Stalemate
    public boolean noMovesLeft()
    {
        boolean availableMove = false;
        int count;
        for (int r = 0; r < 8 && !availableMove; ++r)
        {
            for (int c = 0; c < 8 && !availableMove; ++c)
            {
                count = 0;
                if (pieces[r][c] != null &&
                        pieces[r][c].get_isWhite() == _isWhiteTurn)
                {
                    count = storePossibleMoves(r, c);
                    if (count > 0)
                        availableMove = true;
                }
            }
        }
        emptyPossibleMoves();
        if (!availableMove)
            return true;
        else
            return false;
    }

    // Takes a piece and stores all of its available moves in the array
    // availableMoves, and returns the number of available moves
    public int storePossibleMoves(int r1, int c1)
    {
        int count = 0;
        for (int r2 = 0; r2 < 8; ++r2)
        {
            for (int c2 = 0; c2 < 8; ++c2)
            {
                if (isValid(r1, c1, r2, c2))
                {
                    availableMoves[count] = new PossibleCoordinates();
                    availableMoves[count]._r1 = r1;
                    availableMoves[count]._c1 = c1;
                    availableMoves[count]._r2 = r2;
                    availableMoves[count]._c2 = c2;
                    ++count;
                }
            }
        }
        return count;
    }

    // availableMoves, returns array of available moves for one color
    public PossibleCoordinates[] storePossibleMoves(boolean isWhite)
    {
        PossibleCoordinates[] arr = new PossibleCoordinates[250];
        boolean save = _isWhiteTurn;
        if (isWhite)
            _isWhiteTurn = true;
        else
            _isWhiteTurn = false;

        int count = 0;
        for (int r = 0; r < 8; ++r)
        {
            for (int c = 0; c < 8; ++c)
            {
                if (pieces[r][c] != null)
                {
                    if (pieces[r][c].get_isWhite() == isWhite)
                    {
                        // Efficient way of checking for valid squares
                        // Knight, need to check 8 squares (if they're within 8x8)
                        // Rook and Queen check the CROSS (straight)
                        // Bishop and Queen check the EX (diagonal)
                        // PAWN CHECKS TWO UNITS AHEAD AND ITS DIAGONALS
                        // king checks all squares around (IF ITS WITHIN 8X8)

                        if (pieces[r][c] instanceof Knight)
                        {
                            if (r - 2 >= 0 && c + 1 <= 7)
                            {
                                if (isValid(r, c, r-2, c+1))
                                {
                                    addPossMove(r, c, r-2, c+1, count, arr);
                                    ++count;
                                }
                            }
                            if (r - 2 >= 0 && c - 1 >= 0)
                            {
                                if (isValid(r, c, r-2, c-1))
                                {
                                    addPossMove(r, c, r-2, c-1, count, arr);
                                    ++count;
                                }
                            }
                            if (r - 1 >= 0 && c + 2 <= 7)
                            {
                                if (isValid(r, c, r-1, c+2))
                                {
                                    addPossMove(r, c, r-1, c+2, count, arr);
                                    ++count;
                                }
                            }
                            if (r - 1 >= 0 && c - 2 >= 0)
                            {
                                if (isValid(r, c, r-1, c-2))
                                {
                                    addPossMove(r, c, r-1, c-2, count, arr);
                                    ++count;
                                }
                            }
                            if (r + 2 <= 7 && c + 1  <= 7)
                            {
                                if (isValid(r, c, r+2, c+1))
                                {
                                    addPossMove(r, c, r+2, c+1, count, arr);
                                    ++count;
                                }
                            }
                            if (r + 2 <= 7 && c - 1 >= 0)
                            {
                                if (isValid(r, c, r+2, c-1))
                                {
                                    addPossMove(r, c, r+2, c-1, count, arr);
                                    ++count;
                                }
                            }
                            if (r + 1 <= 7 && c + 2 <= 7)
                            {
                                if (isValid(r, c, r+1, c+2))
                                {
                                    addPossMove(r, c, r+1, c+2, count, arr);
                                    ++count;
                                }
                            }
                            if (r + 1 <= 7 && c - 2 >= 0)
                            {
                                if (isValid(r, c, r+1, c-2))
                                {
                                    addPossMove(r, c, r+1, c-2, count, arr);
                                    ++count;
                                }
                            }
                        }
                        else if (pieces[r][c] instanceof Pawn)
                        {
                            if (pieces[r][c].get_isWhite())
                            {
                                if (r - 1 >= 0)
                                {
                                    if (isValid(r,c, r-1, c))
                                    {
                                        addPossMove(r, c, r-1, c, count, arr);
                                        ++count;
                                    }

                                    if (c + 1 <= 7)
                                    {
                                        if (isValid(r, c, r-1, c+1))
                                        {
                                            addPossMove(r, c, r-1, c+1, count, arr);
                                            ++count;
                                        }
                                    }
                                    if (c - 1 >= 0)
                                    {
                                        if (isValid(r, c, r-1, c-1))
                                        {
                                            addPossMove(r, c, r-1, c -1, count, arr);
                                            ++count;
                                        }
                                    }

                                }
                                if (r - 2 >= 0)
                                {
                                    if (isValid(r, c, r-2, c))
                                    {
                                        addPossMove(r, c, r-2, c, count, arr);
                                        ++count;
                                    }
                                }
                            }
                            else
                            {
                                if (r + 1 <= 7)
                                {
                                    if (isValid(r,c, r+1, c))
                                    {
                                        addPossMove(r, c, r+1, c, count, arr);
                                        ++count;
                                    }

                                    if (c + 1 <= 7)
                                    {
                                        if (isValid(r, c, r+1, c+1))
                                        {
                                            addPossMove(r, c, r+1, c+1, count, arr);
                                            ++count;
                                        }
                                    }
                                    if (c - 1 >= 0)
                                    {
                                        if (isValid(r, c, r+1, c-1))
                                        {
                                            addPossMove(r, c, r+1, c -1, count, arr);
                                            ++count;
                                        }
                                    }
                                }
                                if (r + 2 <= 7)
                                {
                                    if (isValid(r, c, r+2, c))
                                    {
                                        addPossMove(r, c, r+2, c, count, arr);
                                        ++count;
                                    }
                                }
                            }
                        }
                        else if (pieces[r][c] instanceof King)
                        {
                            if (r + 1 <= 7)
                            {
                                if (isValid (r, c, r+1, c))
                                {
                                    addPossMove(r, c, r+1, c, count, arr);
                                    ++count;
                                }
                                if (c + 1 <= 7)
                                {
                                    if (isValid(r, c, r+1, c+1))
                                    {
                                        addPossMove(r, c, r+1, c+1, count, arr);
                                        ++count;
                                    }
                                }
                                if (c - 1 >= 0)
                                {
                                    if (isValid(r, c, r+1, c-1))
                                    {
                                        addPossMove(r, c, r+1, c-1, count, arr);
                                        ++count;
                                    }
                                }
                            }
                            if (r - 1 >= 0)
                            {
                                if (isValid(r, c, r-1, c))
                                {
                                    addPossMove(r, c, r-1, c, count, arr);
                                    ++count;
                                }
                                if (c + 1 <= 7)
                                {
                                    if (isValid(r, c, r-1, c+1))
                                    {
                                        addPossMove(r, c, r-1, c+1, count, arr);
                                        ++count;
                                    }
                                }
                                if (c - 1 >= 0)
                                {
                                    if (isValid(r, c, r-1, c-1))
                                    {
                                        addPossMove(r, c, r-1, c-1, count, arr);
                                        ++count;
                                    }
                                }
                            }
                            if (c + 1 <= 7)
                            {
                                if (isValid(r, c, r, c+1))
                                {
                                    addPossMove(r, c, r, c+1, count, arr);
                                    ++count;
                                }
                            }
                            if (c - 1 >= 0)
                            {
                                if (isValid(r, c, r, c-1))
                                {
                                    addPossMove(r, c, r, c-1, count, arr);
                                    ++count;
                                }
                            }
                            if (c + 2 <= 7)
                            {
                                if (isValid(r, c, r, c+2))
                                {
                                    addPossMove(r, c, r, c+2, count, arr);
                                    ++count;
                                }
                            }
                            if (c - 2 >= 0)
                            {
                                if (isValid(r, c, r, c-2))
                                {
                                    addPossMove(r, c, r, c-2, count, arr);
                                    ++count;
                                }
                            }
                        }
                        if (pieces[r][c] instanceof Rook || pieces[r][c] instanceof Queen)
                        {
                            int temp = r;
                            while (temp >= 0) // up
                            {
                                if (isValid(r, c, temp, c))
                                {
                                    addPossMove(r, c, temp, c, count, arr);
                                    ++count;
                                }
                                --temp;
                            }
                            temp = r;
                            while (temp <= 7) // down
                            {
                                if (isValid(r, c, temp, c))
                                {
                                    addPossMove(r, c, temp, c, count, arr);
                                    ++count;
                                }
                                ++temp;
                            }
                            temp = c;
                            while (temp >= 0) // left
                            {
                                if (isValid(r, c, r, temp))
                                {
                                    addPossMove(r, c, r, temp, count, arr);
                                    ++count;
                                }
                                --temp;
                            }
                            temp = c;
                            while (temp <= 7) // right
                            {
                                if (isValid(r, c, r, temp))
                                {
                                    addPossMove(r, c, r, temp, count, arr);
                                    ++count;
                                }
                                ++temp;
                            }
                        }
                        if (pieces[r][c] instanceof Bishop || pieces[r][c] instanceof Queen)
                        {
                            int tempR = r;
                            int tempC = c;
                            while (tempR >= 0 && tempC <= 7) // up right
                            {
                                if (isValid(r, c, tempR, tempC))
                                {
                                    addPossMove(r, c, tempR, tempC, count, arr);
                                    ++count;
                                }
                                --tempR;
                                ++tempC;
                            }
                            tempR = r;
                            tempC = c;
                            while (tempR >= 0 && tempC >= 0) // up left
                            {
                                if (isValid(r, c, tempR, tempC))
                                {
                                    addPossMove(r, c, tempR, tempC, count, arr);
                                    ++count;
                                }
                                --tempR;
                                --tempC;
                            }
                            tempR = r;
                            tempC = c;
                            while (tempR <= 7 && tempC <= 7) // down right
                            {
                                if (isValid(r, c, tempR, tempC))
                                {
                                    addPossMove(r, c, tempR, tempC, count, arr);
                                    ++count;
                                }
                                ++tempR;
                                ++tempC;
                            }
                            tempR = r;
                            tempC = c;
                            while (tempR <= 7 && tempC >= 0) // down left
                            {
                                if (isValid(r, c, tempR, tempC))
                                {
                                    addPossMove(r, c, tempR, tempC, count, arr);
                                    ++count;
                                }
                                ++tempR;
                                --tempC;
                            }
                        }
                    }
                }
            }
        }
        PossibleCoordinates[] temp = new PossibleCoordinates[count];
        for (int i = 0; i < count; ++i)
        {
            temp[i] = arr[i];
        }
        arr = temp;
        _isWhiteTurn = save;
        return arr;
    }

    private void addPossMove
            (int r1, int c1, int r2, int c2, int count, PossibleCoordinates[] arr)
    {
        arr[count] = new PossibleCoordinates();
        arr[count]._r1 = r1;
        arr[count]._c1 = c1;
        arr[count]._r2 = r2;
        arr[count]._c2 = c2;
    }

    // Empties the array of availableMoves
    private void emptyPossibleMoves()
    {
        int i = 0;
        while (availableMoves[i] != null)
        {
            availableMoves[i++] = null;
        }
    }

    // Returns the number of black pieces
    public int countNumBlackPieces()
    {
        int count = 0;
        for (int r = 0; r < 8; ++r)
        {
            for (int c = 0; c < 8; ++c)
            {
                if (pieces[r][c] != null &&
                        !pieces[r][c].get_isWhite())
                    ++count;
            }
        }
        return count;
    }

    // AI computer turn
    public void computerTurn()
    {
        int numPieces = getNumPieces();

//       if (numPieces < 5)
//          DEPTH_MAX = 7;
//       else if (numPieces < 9)
//          DEPTH_MAX = 6;
//       else if (numPieces < 16)
//          DEPTH_MAX = 5;
//       else
//          DEPTH_MAX = 4;

        double bestEval = minimax ( DEPTH_MAX , Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
        restore.clearStack();

        CompLoc.r1 = bestMove.r1;
        CompLoc.c1 = bestMove.c1;
        CompLoc.r2 = bestMove.r2;
        CompLoc.c2 = bestMove.c2;
    }

    // Provide an evaluation for a board position
    // Counts points for existence of pieces and factors WHERE they are.
    public double evaluatePosition()
    {
        double total = 0;
        int multiplier;
        for (int r = 0; r < 8; ++r)
        {
            for (int c = 0; c < 8; ++c)
            {
                if (pieces[r][c] != null)
                {
                    if (pieces[r][c].get_isWhite())
                    {
                        if (pieces[r][c] instanceof Pawn)
                            total += 10.0 + pawn_table[r][c];
                        else if (pieces[r][c] instanceof Knight)
                            total += 30.0 + knight_table[r][c];
                        else if (pieces[r][c] instanceof Bishop)
                            total += 30.0 + bishop_table[r][c];
                        else if (pieces[r][c] instanceof Rook)
                            total += 50.0 + rook_table[r][c];
                        else if (pieces[r][c] instanceof Queen)
                            total += 90.0 + queen_table[r][c];
                        else
                            total += 900.0 + king_table[r][c];
                    }
                    else
                    {
                        if (pieces[r][c] instanceof Pawn)
                            total += -10.0 - pawn_table[(-r+7)][c];
                        else if (pieces[r][c] instanceof Knight)
                            total += -30.0 - knight_table[(-r+7)][c];
                        else if (pieces[r][c] instanceof Bishop)
                            total += -30.0 - bishop_table[(-r+7)][c];
                        else if (pieces[r][c] instanceof Rook)
                            total += -50.0 - rook_table[(-r+7)][c];
                        else if (pieces[r][c] instanceof Queen)
                            total += -90.0 - queen_table[(-r+7)][c];
                        else
                            total += -900.0 - king_table[(-r+7)][c];
                    }
                }
            }
        }
        return total;
    }

    public void addOnStack(int r1, int c1, int r2, int c2)
    {
        boolean castle = kingCastled(r1, c1, c2);
        boolean promotion = pawnAtEnd(r1, c1, r2, c2);
        boolean passant = pawnEnPassant(r1, c1, r2, c2);
        boolean startMoved = true;
        boolean captureMoved = true;
        boolean startIsWhite = false;
        if (pieces[r1][c1] != null)
            startIsWhite = pieces[r1][c1].get_isWhite();

        boolean captureIsWhite = false;
        String captureName = "NULL";

        if (pieces[r2][c2] != null)
            captureIsWhite = pieces[r2][c2].get_isWhite();

        if (pieces[r1][c1] instanceof Pawn ||
                pieces[r1][c1] instanceof Rook ||
                pieces[r1][c1] instanceof King)
            startMoved = pieces[r1][c1].get_hasMoved();

        if (pieces[r2][c2] != null)
        {
            if (pieces[r2][c2] instanceof Pawn ||
                    pieces[r2][c2] instanceof Rook ||
                    pieces[r2][c2] instanceof King)
                captureMoved = pieces[r2][c2].get_hasMoved();
        }

        if (pieces[r2][c2] != null)
        {
            if (pieces[r2][c2] instanceof Pawn)
                captureName = "PAWN";
            else if (pieces[r2][c2] instanceof Rook)
                captureName = "ROOK";
            else if (pieces[r2][c2] instanceof Bishop)
                captureName = "BISHOP";
            else if (pieces[r2][c2] instanceof Knight)
                captureName = "KNIGHT";
            else if (pieces[r2][c2] instanceof King)
                captureName = "KING";
            else if (pieces[r2][c2] instanceof Queen)
                captureName = "QUEEN";
        }

        restore.insert(r1, c1, r2, c2, startMoved, captureMoved, startIsWhite,
                captureIsWhite, captureName, castle, passant, promotion);
    }

    public void undo()
    {
        MoveUndo s = restore.pop();
        if (s._castle)
        {
            pieces[s._r1][s._c1] = pieces[s._r2][s._c2];
            pieces[s._r1][s._c1].undoMoved();
            pieces[s._r2][s._c2] = null;

            if (s._c2 == 6)
            {
                pieces[s._r1][7] = pieces[s._r1][5];
                pieces[s._r1][7].undoMoved();
                pieces[s._r1][5] = null;
            }
            else
            {
                pieces[s._r1][0] = pieces[s._r1][3];
                pieces[s._r1][0].undoMoved();
                pieces[s._r1][3] = null;
            }
        }
        else if (s._passant)
        {
            pieces[s._r1][s._c1] = pieces[s._r2][s._c2];
            pieces[s._r2][s._c2] = null;
            pieces[s._r1][s._c2] = new Pawn (!s._sWhite);
            pieces[s._r1][s._c2].moved();
            lastMove.name = "Pawn";
            if (s._r2 == 2)
                lastMove.r1 = 1;
            else
                lastMove.r1 = 6;
            lastMove.r2 = s._r1;
            lastMove.c1 = s._c2;
            lastMove.c2 = s._c2;
        }
        else if (s._promotion)
        {
            if (s._r2 == 0)
            {
                pieces[s._r1][s._c1] = new Pawn (true);
                pieces[s._r1][s._c1].moved();
            }
            else if (s._r2 == 7)
            {
                pieces[s._r1][s._c1] = new Pawn (false);
                pieces[s._r1][s._c1].moved();
            }
            if (s._cName.equals("NULL"))
                pieces[s._r2][s._c2] = null;
            else if (s._cName.equals("KING"))
            {
                pieces[s._r2][s._c2] = new King (s._cWhite);
                if (s._cMoved)
                    pieces[s._r2][s._c2].moved();
            }
            else if (s._cName.equals("ROOK"))
            {
                pieces[s._r2][s._c2] = new Rook (s._cWhite);
                if (s._cMoved)
                    pieces[s._r2][s._c2].moved();
            }
            else if (s._cName.equals("PAWN"))
            {
                pieces[s._r2][s._c2] = new Pawn (s._cWhite);
                if (s._cMoved)
                    pieces[s._r2][s._c2].moved();
            }
            else if (s._cName.equals("BISHOP"))
            {
                pieces[s._r2][s._c2] = new Bishop (s._cWhite);
            }
            else if (s._cName.equals("KNIGHT"))
            {
                pieces[s._r2][s._c2] = new Knight (s._cWhite);
            }
            else if (s._cName.equals("QUEEN"))
            {
                pieces[s._r2][s._c2] = new Queen (s._cWhite);
            }
        }
        else
        {
            pieces[s._r1][s._c1] = pieces[s._r2][s._c2];
            if (pieces[s._r1][s._c1] != null &&
                    pieces[s._r1][s._c1] instanceof Pawn ||
                    pieces[s._r1][s._c1] instanceof Rook ||
                    pieces[s._r1][s._c1] instanceof King)
                if (!s._sMoved)
                    pieces[s._r1][s._c1].undoMoved();
            if (s._cName.equals("NULL"))
                pieces[s._r2][s._c2] = null;
            else if (s._cName.equals("KING"))
            {
                pieces[s._r2][s._c2] = new King (s._cWhite);
                if (s._cMoved)
                    pieces[s._r2][s._c2].moved();
            }
            else if (s._cName.equals("ROOK"))
            {
                pieces[s._r2][s._c2] = new Rook (s._cWhite);
                if (s._cMoved)
                    pieces[s._r2][s._c2].moved();
            }
            else if (s._cName.equals("PAWN"))
            {
                pieces[s._r2][s._c2] = new Pawn (s._cWhite);
                if (s._cMoved)
                    pieces[s._r2][s._c2].moved();
            }
            else if (s._cName.equals("BISHOP"))
            {
                pieces[s._r2][s._c2] = new Bishop (s._cWhite);
            }
            else if (s._cName.equals("KNIGHT"))
            {
                pieces[s._r2][s._c2] = new Knight (s._cWhite);
            }
            else if (s._cName.equals("QUEEN"))
            {
                pieces[s._r2][s._c2] = new Queen (s._cWhite);
            }
        }
    }

    public double minimax ( int depth, double alpha, double beta, boolean maximizer)
    {
        PossibleCoordinates[] possMoves = storePossibleMoves(maximizer);
        if (depth == 0 || possMoves.length == 0)
        {
            if (possMoves.length == 0)
                return -10000000;
            else
                return evaluatePosition();
        }

        double bestEval = (maximizer)? -1000000 : 1000000;
        double eval;

        if (maximizer)
        {
            for (int i = 0; i < possMoves.length; ++i)
            {
                addOnStack(possMoves[i]._r1, possMoves[i]._c1, possMoves[i]._r2,
                        possMoves[i]._c2);

                updateBoard(possMoves[i]._r1, possMoves[i]._c1, possMoves[i]._r2,
                        possMoves[i]._c2);

                eval = minimax ( depth - 1, alpha, beta,  false);
                bestEval = Math.max(bestEval, eval);
                undo();
                alpha = Math.max(alpha, eval);
                if (beta <= alpha)
                    break;
            }
            return bestEval;
        }
        else
        {
            for (int i = 0; i < possMoves.length; ++i)
            {
                addOnStack(possMoves[i]._r1, possMoves[i]._c1, possMoves[i]._r2,
                        possMoves[i]._c2);

                updateBoard(possMoves[i]._r1, possMoves[i]._c1, possMoves[i]._r2,
                        possMoves[i]._c2);

                eval = minimax ( depth - 1, alpha, beta, true);
                if (depth == DEPTH_MAX)
                {
                    if (eval < bestEval)
                    {
                        bestMove.r1 = possMoves[i]._r1;
                        bestMove.c1 = possMoves[i]._c1;
                        bestMove.r2 = possMoves[i]._r2;
                        bestMove.c2 = possMoves[i]._c2;
                    }
                }

                bestEval = Math.min(bestEval, eval);

                undo();

                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break;
            }
            return bestEval;
        }

    }

    public void displayBoard()
    {
        System.out.println();
        for (int r = 0; r < 8; ++r)
        {
            for (int c = 0; c < 8; ++c)
            {
                if (pieces[r][c] == null)
                {
                    System.out.print("- ");
                }
                else if (pieces[r][c] instanceof Pawn)
                {
                    System.out.print("P ");
                }
                else if (pieces[r][c] instanceof Rook)
                {
                    System.out.print("R ");
                }
                else if (pieces[r][c] instanceof Knight)
                {
                    System.out.print("N ");
                }
                else if (pieces[r][c] instanceof Bishop)
                {
                    System.out.print("B ");
                }
                else if (pieces[r][c] instanceof Queen)
                {
                    System.out.print("Q ");
                }
                else
                    System.out.print("K ");

            }
            System.out.println();
        }
        System.out.println();
    }

    public int getNumPieces()
    {
        int numPieces = 0;
        for (int r = 0; r < 8; ++r)
        {
            for (int c = 0; c < 8; ++c)
            {
                if (pieces[r][c] != null)
                    ++numPieces;
            }
        }
        return numPieces;
    }
}

