/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.resource.StringResource;

/**
 *
 * @author Kirrie
 */
public class ImageOperation { 
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(ImageOperation.class.getName());
    
    // Vseobecne : hocijaky obrazok ktory dedi od Image
    private Image origImage; 
    
    // destImage : Prekonvertovany obrazok origImage
    // showImage : Vysledky obrazok po operaciach
    private BufferedImage destImage,showImage; 
    private static ImageOperation io;
    private static int index;    
    private RescaleOp rescale; 
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor pre objekt ktory dostava ako parameter obrazok nad ktorym 
     * sa budu vykonavat metody tejto triedy.
     * @param img Originalny obrazok.
     */
    public ImageOperation(Image img) {
        this.origImage = img;
    }
    
    /**
     * Privatny konstruktor pre vytvorenie instancie ImageOperation.
     */
    private ImageOperation() {
        
    }
    // </editor-fold>
    
    /**
     * Metoda ktora vrati instanciu ImageOperation. Raz je volana s vytvaranim instancie ImageOperation
     * dalsie razy iba vrati tuto instanciu
     * @return Instancia ImageOperation
     */
    public static ImageOperation getInstance() {
        if (io == null) {
            return io = new ImageOperation();
        } else {
            return io;
        }
    }
    
    /**
     * Metoda ktora vrati sirku originalneho obrazku
     * @return Sirka orig. obrazku
     */
    public int getWidth() {
        return origImage.getWidth(null);
    }
    
    /**
     * Metoda ktora vrati vysku originalneho obrazku
     * @return Vyska orig. obrazku
     */
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
     * @param type Typ obrazku aky chceme mat v BufferedImage.
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
    
    /**
     * Metoda ktora zrotuje obrazok volanim metody rotate s 3 parametrami. Oproti tejto metode
     * je ina v tom ze rotujeme v strede obrazku.
     * @param theta Pocet radianov o ktore rotujeme.
     */
    public void rotate(double theta) {
       rotate(theta, (int)(destImage.getWidth()/2), (int)(destImage.getHeight()/2));
    }
    
    /**
     * Metoda ktora zrotuje obrazok o theta radianov okolo bodu [origX,origY].
     * Je to vseobecna metoda rotate s jednym parametrom. Pre spravne zrotovanie 
     * nastavujeme maticu afinnych transormacii pre rotovanie a maticu pre translaciu.
     * Nasledne spojime tieto afinne transformacie metodou preConcatenate a pouzijeme 
     * na nas obrazok.
     * @param theta Pocet radianov rotovania obrazku
     * @param origX X-ova pozicia rotovania
     * @param origY Y-ova pozicia rotovania
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
     * Metoda ktora sfinalizuje operacie nad obrazkom tym ze priradi showImage (obrazok na ukazanie)
     * obrazok s ktorym sme pracovali (destImage)
     */
    public void finalizeOp() {
        showImage = destImage;
    }
    
    /**
     * Metoda ktora vytvori translacnu maticu. Najprv vytvarame dva body [0,0] a [0,height]
     * ktore transformujeme podla rotacnej matice zadanej parametrom <b>xForm</b>,
     * co nam vrati zrotovane vrcholy podla ktorych vytvorime translacnu maticu
     * @param xForm Rotacna matica pre zrotovanie bodov
     * @param bi Obrazok ktory transformujeme (nevyuzite v metode)
     * @return Translacna matica.
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
     * Metoda ktora zmensi obrazok podla zadanych parametrov
     * , kde po uskutocneni zmeny zostanu obrazky s dlzkou constx a vyskou (consty - begy).
     * Novo vytvoreny image bude ulozeny v showImage a pre vykonanie dalsich operacii musi byt zavolana
     * metoda nextOp.
     * Obrazok musi byt vacsi ako begx + constx a begy + consty, kedze metoda vybera
     * tento kusok z originalneho obrazku.
     * @param begx Zaciatok suradnice x odkial vyrezavame obrazok
     * @param begy Zaciatok suradnice y odkial vyrezavame obrazok
     * @param constx Dlzka vyrezavanej casti
     * @param consty Vyska vyrezavanej casti
     */
    public void cropBufferedImage(int begx,int begy, int constx, int consty) {
        
        if (begx == -1 || begy == -1 || constx == -1 || consty == -1) {
            LOG.log(Level.SEVERE, StringResource.getResource("_erasteroper"));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_erasteroper"),
                    null).renderSpecific(StringResource.getResource("_label_imageoperation"));
        }
        
        if (begx + constx > destImage.getWidth() || begy + consty > destImage.getHeight()) {
            Image toDraw = destImage.getSubimage(begx, begy, destImage.getWidth() - begx, destImage.getHeight() - begy);
            destImage = new BufferedImage(constx, consty, destImage.getType());
            destImage.getGraphics().drawImage(toDraw, 0, 0, null);            
            return;
        }
        
        if (constx < destImage.getWidth() || consty < destImage.getHeight()) {
            destImage = destImage.getSubimage(begx, begy, constx, consty);            
            return;
        } 
        
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
