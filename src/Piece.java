/*
   Briggs Richardson
   
   The Super Class of all chess pieces on the pieces[][] array.
   It contains methods that the sub-class specific pieces will 
   have for information retreival for the LogicBoard to determine
   legal moves.
   
   Two abstract methods: get_isWhite, canMove are used by every
   piece, whereas get_hasMoved() and moved() are used only in the
   Rook, King, and Pawn classes: Since they are the only pieces that
   the status of having moved or not matters (castling, pawn advance 2)
   
   Below Piece are each individual specific piece, with their properties,
   and most importantly a function that tells the logic board if a move
   is POSSIBLE by a given piece (regardless of other rules of the board),
   like a Pawn CAN move forwards, but it can't move backwards... etc.
*/

abstract public class Piece
{
    protected boolean _isWhite;

    public Piece(boolean isWhite)
    {
        _isWhite = isWhite;
    }

    // Returns TRUE = white, FALSE = black
    public boolean get_isWhite()
    {
        return _isWhite;
    }

    /* Returns TRUE if the piece itself can move from one
       location to another, not taking other rules into
       consideration.
    */
    abstract boolean canMove(int r1, int c1, int r2, int c2);

    public boolean get_hasMoved()
    {
        return true;
    }

    // Sets the piece's hasMoved variable to TRUE -> meaning it's moved
    public void moved()
    {
        return;
    }

    // Sets the piece's moved to false
    public void undoMoved()
    {
        return;
    }
}

class King extends Piece
{
    private boolean _hasMoved;

    public King(boolean isWhite)
    {
        super(isWhite);
        _hasMoved = false;
    }

    public boolean get_hasMoved()
    {
        return _hasMoved;
    }

    public void moved()
    {
        _hasMoved = true;
    }

    public void undoMoved()
    {
        _hasMoved = false;
    }

    public boolean canMove(int r1, int c1, int r2, int c2)
    {
        int colDiff = c2 - c1;
        int rowDiff = r2 - r1;
        boolean isValid = false;

        colDiff = (colDiff < 0)? (-1*colDiff) : colDiff;
        rowDiff = (rowDiff < 0)? (-1*rowDiff) : rowDiff;

        if (colDiff == 1 && rowDiff == 1) // Diagonal moves
            isValid = true;
        else if (colDiff == 1 && rowDiff == 0) // Horizontal moves
            isValid = true;
        else if (colDiff == 0 && rowDiff == 1) // Vertical moves
            isValid = true;
        else if (colDiff == 2 && rowDiff == 0 && !_hasMoved) // Castling
            isValid = true;

        return isValid;
    }
}

class Queen extends Piece
{
    public Queen(boolean isWhite)
    {
        super(isWhite);
    }


    public boolean canMove(int r1, int c1, int r2, int c2)
    {
        int colDiff = c2 - c1;
        int rowDiff = r2 - r1;

        colDiff = (colDiff < 0)? (-1*colDiff) : colDiff;
        rowDiff = (rowDiff < 0)? (-1*rowDiff) : rowDiff;
        // First boolean expression is for diagonal moves,
        // and the second is checking for straight lines
        return ((colDiff == rowDiff) || (r1 == r2 || c1 == c2));
    }
}

class Rook extends Piece
{

    private boolean _hasMoved;

    public Rook(boolean isWhite)
    {
        super(isWhite);
        _hasMoved = false;
    }

    public boolean get_hasMoved()
    {
        return _hasMoved;
    }

    public void moved()
    {
        _hasMoved = true;
    }

    public void undoMoved()
    {
        _hasMoved = false;
    }

    public boolean canMove(int r1, int c1, int r2, int c2)
    {
        return (r1 == r2 || c1 == c2);
    }
}

class Bishop extends Piece
{
    public Bishop(boolean isWhite)
    {
        super(isWhite);
    }


    // A legal bishop move is one that is perfectly diagonal
    // A diagonal move means the difference in the change of the
    // row must be equal to the difference in the change of the column
    public boolean canMove(int r1, int c1, int r2, int c2)
    {
        int colDiff = c2 - c1;
        int rowDiff = r2 - r1;

        colDiff = (colDiff < 0)? (-1*colDiff) : colDiff;
        rowDiff = (rowDiff < 0)? (-1*rowDiff) : rowDiff;

        if (colDiff == rowDiff)
            return true;
        else
            return false;
    }
}

class Knight extends Piece
{


    public Knight(boolean isWhite)
    {
        super(isWhite);
    }


    public boolean canMove(int r1, int c1, int r2, int c2)
    {
        int colDiff = c2 - c1;
        int rowDiff = r2 - r1;
        colDiff = (colDiff < 0)? (-1*colDiff) : colDiff;
        rowDiff = (rowDiff < 0)? (-1*rowDiff) : rowDiff;

        return ((rowDiff == 2 && colDiff == 1) ||
                (rowDiff == 1 && colDiff == 2));
    }
}

class Pawn extends Piece
{
    private boolean _hasMoved;


    public Pawn (boolean isWhite)
    {
        super(isWhite);
        _hasMoved = false;
    }

    public boolean get_hasMoved()
    {
        return _hasMoved;
    }
    public void moved()
    {
        _hasMoved = true;
    }
    public void undoMoved()
    {
        _hasMoved = false;
    }

    // Assumes the pawn move is not legal, unless any of the conditions
    // are met, then it will return the move as legal
    public boolean canMove(int r1, int c1, int r2, int c2)
    {
        boolean validMove = false;

        if (_isWhite) // WHITE PAWNS
        {
            // Straight move -- only allowed to either advance one unit
            // forward if already moved, or a choice of 1 or 2 if player hasn't
            if (c1 == c2)
            {
                if ((r1 - r2 == 1))
                    validMove = true;
                else if (!_hasMoved && (r1 - r2 == 2))
                    validMove = true;
            }
            // Capture move -- only allowed to capture pieces one unit diagonally
            // in the forward direction
            else if ((r1 - r2 == 1) && ((c2 - c1 == 1) || (c2 - c1 == -1)))
                validMove = true;
        }
        else // BLACK PAWNS
        {
            // Same rules as white, except forward is in the opposite direction
            if (c1 == c2)
            {
                if ((r2 - r1 == 1))
                    validMove = true;
                else if (!_hasMoved && (r2 - r1 == 2))
                    validMove = true;
            }
            else if ((r2 - r1 == 1) && ((c2 - c1 == 1) || (c2 - c1 == -1)))
                validMove = true;
        }
        return validMove;
    }
}