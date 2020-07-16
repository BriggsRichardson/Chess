/*
   Briggs Richardson
   
   The stack class has the data structure of a stack. It contains
   records of MoveUndo, which has all the properties necessary
   for a undo-move function. This way, the chess program can
   undo a move when it is evaluating within minimax.
*/

public class Stack
{
    private MoveUndo[] store;
    private static int front = 0;
    private static final int ALLOC = 150;

    public Stack()
    {
        store = new MoveUndo[ALLOC];
    }

    public void insert (int r1, int c1, int r2, int c2, boolean sMoved,
                        boolean cMoved, boolean sWhite, boolean cWhite, String cName,
                        boolean castle, boolean passant, boolean promotion)
    {
        if (front == store.length)
        {
            MoveUndo[] temp = new MoveUndo[store.length * 2];
            for (int i = 0; i < store.length; ++i)
            {
                temp[i] = store[i];
            }
            store = temp;
        }

        store[front] = new MoveUndo();

        store[front]._r1 = r1;
        store[front]._c1 = c1;
        store[front]._r2 = r2;
        store[front]._c2 = c2;
        store[front]._sMoved = sMoved;
        store[front]._cMoved = cMoved;
        store[front]._sWhite = sWhite;
        store[front]._cWhite = cWhite;
        store[front]._cName = cName;
        store[front]._castle = castle;
        store[front]._passant = passant;
        store[front]._promotion = promotion;

        ++front;
    }

    public MoveUndo pop()
    {
        --front;
        return store[front];
    }

    public void clearStack()
    {
        for (int i = 0; i < front; ++i)
        {
            store[i] = null;
        }
    }
}