/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RescaleOp;
import javax.swing.JComponent;
import rpgcraft.utils.MathUtils;

/**
 *
 * @author Kirrie
 */
public class ImageOperation { 
    // Vseobecne : hocijaky obrazok ktory dedi od Image
    private Image origImage; 
    
    // destImage : Prekonvertovany obrazok origImage
    // showImage : Vysledky obrazok po operaciach
    private BufferedImage destImage,showImage; 
    private static ImageOperation io;
        
    private RescaleOp rescale; 
            
    /**
     * Konstruktor pre objekt ktory dostava ako parameter obrazok nad ktorym 
     * sa budu vykonavat metody tejto triedy.
     * @param img Originalny obrazok.
     */
    public ImageOperation(Image img) {
        this.origImage = img;
    }
    
    private ImageOperation() {
        
    }
    
    public static ImageOperation getInstance() {
        if (io == null) {
            return io = new ImageOperation();
        } else {
            return io;
        }
    }
    
    public int getWidth() {
        return origImage.getWidth(null);
    }
    
    public int getHeight() {
        return origImage.getHeight(null);
    }
    
    /**
     * Konstruktor pre objekt ktory dostava ako parameter obrazok nad ktorym 
     * sa budu vykonavat metody tejto triedy.
     * @param img Originalny obrazok.
     */    
    public ImageOperation(BufferedImage img) {
        this.origImage = img;
    }
    
    /**
     * Metoda vytvori BufferedImage z originalneho obrazku priradeneho k objektu
     * cim budeme mat v tomto objekte stale ulozeny original pre dalsie zmeny pomocou 
     * definovanych metod v tejto triede.
     * Tejto funkcie vyuzivame v triede ImageButton.
     * 
     * @param type Typ obrazku aky chceme mat v BufferedImage.
     * @see ImageButton
     */
    public void createBufferedImages(int type) {
        
        destImage = new BufferedImage(origImage.getWidth(null),
                                  origImage.getHeight(null),
                                  type);
        destImage.getGraphics().drawImage(origImage, 0, 0, null);
        showImage = destImage;
    }
    
    /**
     * Metoda nextOp pripravi objekt na dalsiu operaciu, takze je urcite vhodne volat
     * po kazdej vykonanej operacii. V podstate iba priradi do destImage (obrazok
     * na zmenu) obrazok showImage (obrazok po zmene ktory budeme zobrazovat).
     */
    public void nextOp() {
        destImage = showImage;        
    }
    
    /**
     * Metoda ktora nastavi originalny obrazok, ktory menime, na ten zadany parametrom. 
     * @param img Obrazok, ktory menime
     */
    public void setOrigImage(Image img) {
        this.origImage = img;
    }
    
    /**
     * Metoda ktora vykona transformaciu podla parametru xForm, co je 
     * objekt AffineTransform. Tento objekt je blizsie popisany v triede AffineTransform.
     * @param xForm Transformacna matica
     * @see AffineTransform
     */
    public void transformImage(AffineTransform xForm) {
         AffineTransformOp op = new AffineTransformOp(xForm, AffineTransformOp.TYPE_BILINEAR);

         showImage = op.filter(destImage, null);
    }
    
    /**
     * Metoda ktora skaluje obrazok podla pomocnej funkcie filter
     * z RescaleOp. Tymto sposobom sa da lahko nastavit tmavost obrazku 
     * ako aj jeho velkost. Nevyhoda je ta ze stratime transparentnost.
     * @param scaleFactor Skalovaci parameter, blizsie funkcie popisane v triede RescaleOp
     * @param contrast Offset/Kontrast, blizsie funkcie popisane v triede RescaleOp
     * @see RescaleOp
     */
    public void rescale(float scaleFactor, float contrast) {
        rescale = new RescaleOp(scaleFactor, contrast, null);
        rescale.filter(destImage, showImage);
    }
    
    /**
     * Metoda ktora skaluje obrazok podla pomocnej funkcie filter
     * z RescaleOp. Tymto sposobom sa da lahko nastavit tmavost obrazku 
     * ako aj jeho velkost. Metoda je blizka rescale s tym rozdielom ze 
     * tato metoda skaluje a vyrovnava kontrasty podla viacerych hodnot.
     * Nevyhoda je ta ze stratime transparentnost.
     * @param scaleFactors Skalovacie parametre, blizsie funkcie popisane v triede RescaleOp
     * @param contrasts Offsety/Kontrasyt, blizsie funkcie popisane v triede RescaleOp
     * @see RescaleOp
     */
    public void arrayRescale(float[] scaleFactors, float[] contrasts) {
        rescale = new RescaleOp(scaleFactors, contrasts, null);
        rescale.filter(destImage, showImage);
    }
    
    /**
     * Metoda ktora zrotuje obrazok o parameter theta, ktory je zadany v radianoch!.   
     * @param theta Ako moc sa otoci obrazok
    */
    @Deprecated
    public void rotatetest1(double theta) {
        double angle = theta / 360;
        showImage = new BufferedImage((int)(Math.cos(angle) * destImage.getWidth()), destImage.getHeight(), destImage.getType());        
        Graphics2D g = (Graphics2D)showImage.getGraphics();
        AffineTransform xForm = AffineTransform.getRotateInstance(angle / 360);
        g.drawImage(destImage, xForm, null);
    }
    
    /**
     * Metoda ktora zrotuje obrazok o parameter theta, ktory je zadany v radianoch!, okolo
     * bodu zadaneho parametrami origX a origY zodpovedajuce za suradnice.     
     * @param theta Ako moc sa otoci obrazok
     * @param origX Kde na x osi sa bude otacat
     * @param origY Kde na y osi sa bude otacat
     */
    @Deprecated
    public void rotatetest2(double theta, double origX, double origY) {
        showImage = new BufferedImage(destImage.getWidth(), destImage.getHeight(), destImage.getType());
        Graphics2D g = showImage.createGraphics();
        AffineTransform xForm = AffineTransform.getRotateInstance(theta, origX, origY);
        g.drawImage(destImage, xForm, null);
    }
    
    public void rotate(double theta) {
       rotate(theta, (int)(destImage.getWidth()/2), (int)(destImage.getHeight()/2));
    }
    
    /**
     * 
     * @param theta
     * @param origX
     * @param origY 
     */
    public void rotate(double theta, int origX, int origY) {
      AffineTransform xForm = new AffineTransform();
     
      xForm.rotate(theta * Math.PI / 180, origX, origY);

      AffineTransform translationTransform;
      translationTransform = findTranslation(xForm, destImage);
      xForm.preConcatenate(translationTransform);
      
      BufferedImageOp op = new AffineTransformOp(xForm, AffineTransformOp.TYPE_BILINEAR);

      showImage = op.filter(destImage, null);
    }
    
    
    /**
     * 
     * @param xForm
     * @param bi
     * @return 
     */
    private AffineTransform findTranslation(AffineTransform xForm, BufferedImage bi) {
        Point2D pointInt, pointOut;

        pointInt = new Point2D.Double(0.0, 0.0);
        pointOut = xForm.transform(pointInt, null);
        double ytrans = pointOut.getY();

        pointInt = new Point2D.Double(0, bi.getHeight());
        pointOut = xForm.transform(pointInt, null);
        double xtrans = pointOut.getX();

        AffineTransform tat = new AffineTransform();
        tat.translate(-xtrans, -ytrans);
        return tat;
      }
    
    /**
     * Metoda nam vrati originalny obrazok s ktorym pracujeme. Len pre debug vyuzitie
     * @return Original
     */
    public BufferedImage getDestImg() {
        return destImage;
    }
    
    /**
     * Metoda nam vrati zmeneny obrazok ktory vyuzivame v dalsich metodach
     * @return Zmeneny 
     */
    public BufferedImage getShowImg() {
        return showImage;                
    }
    
    /**
     * Metoda ktora nam vrati nami nastaveny kontrast pre obrazok
     * @return Premenna offset
     */
    public float[] getContrasts() {
        return rescale.getOffsets(null);
    }
    
    /**
     * Metoda getScale nam vrati nami pozadovanu zmenu vo velkosti obrazku <=>
     * skalovacia premenna scaleFactor
     * 
     * @return Premennu scaleFactor
     */
    public float[] getScale() {
        return rescale.getScaleFactors(null);
    }
    
    /**
     * Metoda ktora zmensi aj originalny aj zmeneny obrazok podla zadanych parametrov
     * , kde po uskutocneni zmeny zostanu obrazky s dlzkou constx a vyskou (consty - begy).
     * Obrazok musi byt vacsi ako begx + constx a begy + consty, kedze metoda vybera
     * tento kusok z originalneho obrazku.
     * @param begx Zaciatok suradnice x odkial vyrezavame obrazok
     * @param begy Zaciatok suradnice y odkial vyrezavame obrazok
     * @param constx Dlzka,po suradnici x, vyrezavanej casti
     * @param consty Dlzka,po suradnici y, vyrezavanej casti
     */
    public void cropBufferedImage(int begx,int begy, int constx, int consty) {
        destImage = destImage.getSubimage(begx, begy, constx, consty);
        showImage = new BufferedImage(constx, consty, destImage.getType());
    }
    
    
    /**
     * Metoda zvacsi alebo zmensi obrazok podla zadanych parametrov. Kvalita obrazka sa zhorsi.
     * @param width Dlzka noveho obrazku.
     * @param height Vyska noveho obrazku. 
     * @param type Druh noveho obrazku pouzivany pri <b>Buffered Image</b>
     * <i>TYPE_INT_ARGB</i>, <i>TYPE_INT_RGB</i>, etc...
     * @see BufferedImage#TYPE_INT_ARGB
     * @see BufferedImage#TYPE_INT_RGB
     * @see BufferedImage
     */
        
    public void resizeImage(int width, int height, int type){
	showImage = new BufferedImage(width, height, type);
	Graphics2D g = showImage.createGraphics();
	g.drawImage(destImage, 0, 0, width, height, null);
	g.dispose();        
    }
    
    /**
     * Metoda zvacsi alebo zmensi obrazok podla zadanych parametrov. Kvalita obrazka sa zhorsi
     * ale nie az tolko ako pri volani metody resizeImage. Toto je zarucene vdaka nastaveniu renderovacich
     * napovied (interpolation, rendering, antialiasing).
     * Tato metoda je ale pomalsia ako metoda resizeImage.
     * @param width Dlzka noveho obrazku.
     * @param height Vyska noveho obrazku. 
     * @param type Druh noveho obrazku pouzivany pri <b>Buffered Image</b>
     * <i>TYPE_INT_ARGB</i>, <i>TYPE_INT_RGB</i>, etc...
     * @see BufferedImage#TYPE_INT_ARGB
     * @see BufferedImage#TYPE_INT_RGB
     * @see BufferedImage
     */
    public void betterresizeImage(int width, int height, int type) {
	showImage = new BufferedImage(width, height, type);
	Graphics2D g = showImage.createGraphics();		        	
        
        // happen to be better in resizing
        
	g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	g.setRenderingHint(RenderingHints.KEY_RENDERING,
	RenderingHints.VALUE_RENDER_QUALITY);
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.drawImage(destImage, 0, 0, width, height, null);
        
        g.dispose();

    }	   
    
}
