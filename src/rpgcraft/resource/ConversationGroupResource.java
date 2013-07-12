/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.xml.ConversationGroupXML;

/**
 * ConversationGroupResource ktore dedi od AbstraktnehoResource je trieda ktora umoznuje
 * vytvorit resources konverzanych grup z xml suborov, ktore sa daju pouzit v entitovych resource
 * ako mozne konverzacie ktore moze mat entita. Na vytvorenie nam pomaha metoda parse,
 * ktorej predavame jeden vrchol z xmlka na rozparsovanie. Vytvaranie noveho resource
 * prevadzame volanim metody newBundledResource. Navrat nejakeho resource pomocou metody
 * getResource. 
 * @see AbstractResource
 */
public final class ConversationGroupResource extends AbstractResource<ConversationGroupResource> {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(ConversationGroupResource.class.getName());
    private static final String DELIM = "[,]";
    private static HashMap<String, ConversationGroupResource> groupResources = new HashMap();
    
    private String id, label;
    private ArrayList<ConversationResource> members;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor (privatny) ktory vytvori instanciu ConversationGroupResource z xml suboru.
     * Prvy element ktory parsujeme/prechadzame je zadany v parametri <b>elem</b>.
     * Po rozparsovani zvalidujeme konverzacnu grupu a vlozime hu do listu k dalsim.
     * @param elem Xml element z ktoreho vytvarame resource
     */
    private ConversationGroupResource(Element elem) {
        parse(elem);
        validate();
        groupResources.put(id, this);
    }
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vrati ConversationGroupResource podla parametru <b>id</b>.
     * @param id Id KonverzacnejGrupy ktore hladame
     * @return ConversationGroupResource s danym menom
     */
    public static ConversationGroupResource getResource(String id) {
        return groupResources.get(id);
    }
    
    /**
     * Metoda ktora vytvori novy objekt typu ConversationGroupResource. Kedze je staticka a public
     * tak sa tymto padom stava jediny sposob ako vytvorit instanciu ConversationGroupResource.
     * @param elem Element z ktoreho vytvarame konverzacnu grupu
     * @return ConversationGroupResource z daneeho elementu
     */
    public static ConversationGroupResource newBundledResource(Element elem) {
        return new ConversationGroupResource(elem);
    }
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati konverzacie ktore su mozne pre jednu konverzacnu grupu
     * @return Konverzacne resource mozne pre tuto grupu.
     */
    public ArrayList<ConversationResource> getMembers() {
        return members;
    }
    
    /**
     * Metoda ktora vrati napis konverzacie, ktory sa bude ukazovat pri rozpravani
     * s entitou.
     * @return Nadpis pre jeden typ konverzacie
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Metoda ktora vrati id konverzacnej grupy.
     * @return Id konverzacnej grupy
     */
    public String getId() {
        return id;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Parsovanie ">
    /**
     * Metoda ktora rozparsuvava xml subor takym sposobom ze dostava ako parameter <b>elem</b>
     * co je jeden elem z xml suboru aj so vsetkymi jeho podelementami. Ulohou je prejst vsetky
     * tieto podelementy a podla mien tychto podelemntov vykonat definovane akcie.
     * (ConversationGroupXML.MSGS -> rozparsuje to co je medzi tagmi podla delimiteru. Kazdy rozprasovany
     * kus predstavuje id ConversationResource ktory pridame do listu members.
     * @param elem Element z xml ktory rozparsovavame do ConversationGroupResource
     */
    @Override
    protected void parse(Element elem) {
        NodeList nl = elem.getChildNodes(); 
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {
                case ConversationGroupXML.ID : {
                    this.id = eNode.getTextContent();
                } break;
                case ConversationGroupXML.LABEL : {
                    this.label = eNode.getTextContent();
                } break;
                case ConversationGroupXML.MSGS : {
                    String[] sMembers = eNode.getTextContent().split(DELIM);
                    members = new ArrayList<>();
                    for (String s : sMembers) {
                        members.add(ConversationResource.getResource(s));
                    }
                } break;                    
            }
        }
    }
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">
    /**
     * Validacna metoda ktora zvaliduje resource aby bol pouzitelny.
     */
    private void validate() {
        
    }
    
    /**
     * Metoda ktora vrati ci sa dva resource rovnaju.
     * @param obj Objekt s ktorym porovnavame resource.
     * @return True/false ci sa rovnaju
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConversationGroupResource other = (ConversationGroupResource) obj;

        if (!this.id.equals(other.id)) {
            return false;
        }
        return true;                 
    }
    
    /**
     * Metoda ktora vrati hash kod pre resource.
     * @return Hashkod pre grupovu konverzaciu
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }
        
    /**
     * Metoda ktora okopiruje konverzacne resource z parametra <b>res</b> do tohoto resource.
     * @param res Resource z ktoreho kopirujeme
     * @throws Exception Chyba pri kopirovanie
     */
    @Override
    protected void copy(ConversationGroupResource res) throws Exception {
        this.id = res.id;
        this.label = res.label;
        this.members = res.members;
    }
    // </editor-fold>
}
