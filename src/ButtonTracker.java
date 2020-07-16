/*
   Briggs Richardson
   
   These classes are responsible for storing coordinates of locations
   within the chess board, that can be used later within the program.
   A comment before each class will describe what its purpose is.
*/


/*
   Stores the user's FIRST CLICK, and when the second click occurs,
   the ActionListener calls the ButtonTracker for reference of
   the FIRST CLICK (what wants to be moved)
*/
public class ButtonTracker
{
    public int r1;
    public int c1;

    public ButtonTracker(int row, int col)
    {
        r1 = row;
        c1 = col;
    }
}

/*
   Stores a move's coordinates, from where it originated, to where it went.
   It also stores the name of the piece that was moved.
   This is used for the En Passant Rule, and is only set when a pawn moves
   2 squares forward.
*/
class Move
{
    public int r1;
    public int c1;
    public int r2;
    public int c2;
    public String name;
}

/*
   This class stores the coordinates to a square on the chess board
   in which a piece is available to move to (legally). There is an 
   array storing these PossibleCoordinates. This is mainly used for
   the AI to store possible moves, and pick a random Possible Coordinate
   to move to. 
*/
class PossibleCoordinates
{
    public int _r1;
    public int _c1;
    public int _r2;
    public int _c2;
}

/*
   This class stores the starting square and the ending square of the AI's
   choice of where it wants to move. The CHESSGUI class uses this class to
   call the logicBoard / chessGUI methods to make the move happen.
*/
final class CompLoc
{
    public static int r1;
    public static int c1;
    public static int r2;
    public static int c2;
}

// Stores all the information necessary to undo a move
class MoveUndo
{
    public int _r1;
    public int _c1;
    public int _r2;
    public int _c2;
    public boolean _sMoved;
    public boolean _cMoved;
    public boolean _sWhite;
    public boolean _cWhite;
    public String _cName;
    public boolean _castle;
    public boolean _passant;
    public boolean _promotion;
}

// This record holds the move (start to end) that the computer chooses.
class BestMove
{
    public int r1;
    public int c1;
    public int r2;
    public int c2;
}