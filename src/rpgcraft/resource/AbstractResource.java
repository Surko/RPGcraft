/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import org.w3c.dom.Element;

/**
 * Abstraktne resource ktore tvori ako keby interface pre ostatne resource subory.
 * Kedze je to abstraktna trieda, tak hu nemozme instancovat ale jedine vytvarat potomkov
 * ktore sa uz daju. Trieda vyuziva generiky pri mene ktora urcuje pri podedenych triedach
 * co za resource vytvarame => pri navrate novych resource dostavame taky resource aky chceme.
 */
public abstract class AbstractResource<T extends AbstractResource> {
    
    /**
     * Typ skriptov
     */
    public enum ScriptType {
        LISTENER,
        LUA
    }
    
    /**
     * Typ spustania skriptov
     */
    public enum ActionType {
        START,
        THROUGHT,
        END
    }
    
    /**
     * Metoda ktora rozparsuvava element zadany v parametri <b>elem</b>. Kazda podedena
     * trieda musi tuto metodu implementovat pre spravne fungovanie.
     * @param elem Element ktory rozparsovavame
     */    
    protected abstract void parse(Element elem);
    /**
     * Metoda ktora kopiruje jeden resource do druheho. Resource su rovnakeho typu.
     * @param res Resource z ktoreho kopirujeme
     * @throws Exception Chyba pri kopirovani nejakeho atributu.
     */
    protected abstract void copy(T res) throws Exception;
}
