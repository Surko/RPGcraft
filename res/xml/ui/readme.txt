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
		