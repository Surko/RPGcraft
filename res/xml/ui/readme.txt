Dolezite do buducnosti :
Listy
Tag <action>
	click - pocet klikov na vykonanie
	clicktype - co za klik musi byt vykonany (onListElement - kliknutie na podelement listu)
	BODY : LOAD - nacitanie mapy, pouzitie @ s upresnujucim prikazom, miestom
			prikazy : LIST - miesto odkial nacitava, pouzitie # s upresnujucim id

(LOAD@LIST#_id - nacita save s menom ktory sa nachadza v liste. Meno suboru sa nachadza v resource s id = _id,
toto sa da pouzit aj ked list sa sklada zo zlozitejsych/viacerych komponent kedze sa navigujeme podla id a idcka su
univerzalne pre kazdy resource. Takze keby chcem nacitat mapu s menom rovnym tomu co sa nachadza v resource s id 
rowText tak by konecny prikaz vyzeral LOAD@LIST#rowText. Tymto sa v LoadSaveListenery ulozi do premennej save data pod id = rowText
a nasledne nacita mapu podla tejto premennej)
		
rozne typy akcii :
<action type="KEY" code="VK_W">COMPOP@REINITIALIZE</action>
<action type="KEY" code="VK_A">COMPOP@ADD_COMP_TO(text_edit,MAINCONTAINER)</action>
<action type="KEY" code="VK_S">COMPOP@REMOVE_COMP_ALL
DATA@ASSIGN(ahoj,a)
DATA@ASSIGN(VAR#a,b)</action>   
<action type="KEY" code="VK_D">COMPOP@SELECTME</action>
<action type="KEY" code="VK_Q">COMPOP@UNSELECTME</action> 
<action type="KEY" code="VK_I">DATA@IF(COMPOP@IS_VISIBLE(inventory),COMPOP@SET_INVISIBLE(inventory),COMPOP@SET_VISIBLE(inventory))</action>
<action type="KEY" code="VK_S">COMPOP@REMOVE_COMP_ALLEXCEPTMENU</action>
<action>COMPOP@REMOVE_COMP_TEMP(SaveView01)</action>
<action>COMPOP@SET_VISIBLE(GameText1)</action>
<action>COMPOP@SET_VISIBLE(btn_createnewgame)</action>
<action click="2" clicktype="onListElement">LOAD@LOAD(LIST#_id)</action>