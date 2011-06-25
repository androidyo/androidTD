package utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/*****************************************************************************
 * 本类用于帮助获取和跟新配置文件 
 ******************************************************************************/
public class ConfigurationFile
{
    Properties config = new Properties();
    File fichier;
    
    /**
     * Constructeur
     * 
     * @param cheminFichier le chemin complet vers le fichier de configuration
     * @throws IOException 
     */
    public ConfigurationFile(String cheminFichier)
    {
        try
        {
            fichier = new File(cheminFichier);
            
            if(fichier.exists())
                chargement();
            else
                creerFichier();
       
                
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Permet de creer le fichier de conf avec des valeur par defauts
     */
    private void creerFichier()
    {
        try
        {
            fichier.createNewFile();
            
            chargement();
            
            config.setProperty("IP_SE", "127.0.0.1");
            config.setProperty("PORT_SE", "1234");
            config.setProperty("PORT_SJ", "2345");
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }  
    }

    /*************************************************************************
     * Chargement du fichier de configuration.
     * @throws IOException 
     ************************************************************************/
    private void chargement() throws IOException
    {
        if(fichier == null)
            throw new IllegalArgumentException("Fichier incorrect");
        
        
        // On fait pointer notre Properties sur le fichier
        FileInputStream fis;
        
        fis = new FileInputStream(fichier);
        config.load(fis);
        fis.close();
        fis = null; // Optimisation garbage collector

    }
    
    /*************************************************************************
     * Récupère la valeur associé à une clé
     * 
     * @param cle la clé
     * @return String value
     ************************************************************************/
    public String getProperty(String cle)
    {
        String valeur = config.getProperty(cle);
        
        // Clé inconnue
        if (valeur == null)
            throw new IllegalArgumentException("La valeur correspondant à '" + cle + "' n'existe pas dans le fichier '" + fichier.getAbsolutePath() +"'");
        
        return valeur;
    }
    
    /*************************************************************************
     * Modifie la valeur associé à une clé
     * 
     * @param cle la clé
     * @param valeur String 
     ************************************************************************/
    public void setProperty(String cle, String valeur)
    {
        FileOutputStream fos;
        try 
        {
            fos = new FileOutputStream(fichier);
            config.setProperty(cle,valeur);
            config.store(fos,"Derniere mise a jour :");
            fos.close();
            fos = null;
        } 
        catch (FileNotFoundException e) { e.printStackTrace();} 
        catch (IOException e)           { e.printStackTrace();}
    }
}