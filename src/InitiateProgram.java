/*
   Briggs Richardson

   The chess client is what starts the event-driven reaction type program.
   It does so by creating an instance of the ChessGUI class, and which
   creates other instances and initiates the event reaction program
*/

public class InitiateProgram
{
    public static void main(String[] args)
    {
        new ChessGUI();
    }
}
