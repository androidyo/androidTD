package utils;

// class providing playback functions to the keyboard
import java.io.*;

@SuppressWarnings ("serial")
class SaisieIncorrecteException extends Exception {}

public class Keyboard
{

   /* Lecture d'une cha�ne */
   public static String lireString () throws IOException
   {
      String ligne_lue = null;
      
      try
      {
         InputStreamReader lecteur = new InputStreamReader(System.in);
         BufferedReader entree = new BufferedReader(lecteur);
         ligne_lue = entree.readLine();
      }
      catch (IOException e)
      {
         throw e;
      }
    
      return ligne_lue; 
   } // lireString

   /* Lecture d'un float */
   public static float lireFloat () throws SaisieIncorrecteException
   {
      float x = 0.f;
      
      try
      {
         String ligne_lue = lireString();
         x = Float.parseFloat(ligne_lue);
      }
      catch (Exception e)
      {
         throw new SaisieIncorrecteException();
      }
      
      return x ;
   } // lireFloat

   /* Lecture d'un double */
   public static double lireDouble () throws SaisieIncorrecteException
   {
      double x = 0.0;
      
      try
      {
         String ligne_lue = lireString();
         x = Double.parseDouble(ligne_lue);
      }
      catch (Exception e)
      {
         throw new SaisieIncorrecteException();
      }
      
      return x ;
   } // lireDouble
  
   /* Lecture d'un int */
   public static int lireInt () throws SaisieIncorrecteException
   {
      int n = 0;
      
      try
      {
         String ligne_lue = lireString();
         n = Integer.parseInt(ligne_lue);
      }
      catch (Exception e)
      {
         throw new SaisieIncorrecteException();
      }
      
      return n;
   } // lireInt

}


