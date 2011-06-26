package views.common;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import models.creatures.Creature;
import models.creatures.WaveOfCreatures;
import models.game.Game;

public class Panel_WaveInfo extends JPanel 
{
    private static final long serialVersionUID = 1L;
    private Game jeu;
     
    public Panel_WaveInfo(Game jeu)
    {
        setOpaque(false);
        
        this.jeu = jeu;
        creerPanel();
    }
    
    private void creerPanel()
    {
        // vertical layout
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        int noVague = jeu.getNumVagueCourante();
        for(int i=noVague;i<noVague+10;i++)
        {
            WaveOfCreatures vague = jeu.getTerrain().getVagueDeCreatures(i);
            
            JPanel panelVague = new JPanel();
            panelVague.setOpaque(false);
            
            Creature c = vague.getNouvelleCreature();
            
            JLabel lNoVague = new JLabel(i+".");
            panelVague.add(lNoVague);
            
            JLabel lQ = new JLabel(vague.getNbCreatures()+"x");
            panelVague.add(lQ);
            
//            JLabel lNom = new JLabel(c.getNom());
//            panelVague.add(lNom);
            
            JLabel lImage = new JLabel(new ImageIcon(c.getImage()));
            panelVague.add(lImage);
            
            JLabel lType = new JLabel("["+c.getNomType()+"]");
            panelVague.add(lType);
            
            JLabel lNomSante = new JLabel("H :");
            panelVague.add(lNomSante);
            
            JLabel lSante = new JLabel(c.getSanteMax()+"");
            panelVague.add(lSante);
            
            JLabel lNomGain = new JLabel("G :");
            panelVague.add(lNomGain);
            
            JLabel lGain = new JLabel(c.getNbPiecesDOr()+"");
            panelVague.add(lGain);
            
            JLabel lNomVitesse = new JLabel("S :");
            panelVague.add(lNomVitesse);
            
            JLabel lVitesse = new JLabel(c.getVitesseReelle()+"");
            panelVague.add(lVitesse);
            
            add(panelVague);
        }  
    }
}
