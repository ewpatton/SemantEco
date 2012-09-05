package edu.rpi.tw.eScience.WaterQualityPortal.species;

@Deprecated
public class SpeciesNameAgent {
	
	static public SpeciesHierarchy getSpeciesHierarchy(int columnId){
		String spcClass="", subClass="";
		if(columnId<21 || columnId>225){
			System.err.println("can Not get the species class for column: "+columnId);
		}
		else if(columnId>=21 && columnId<66){
			spcClass="Fish";
			subClass=getSpeciesSubClassForFish(columnId);
		}
		else if(columnId>=66 && columnId<75){
			spcClass="Amphibians";
			subClass=getSpeciesSubClassForAmphibians(columnId);
		}
		else if(columnId>=75 && columnId<80){
			spcClass="Reptiles";
			subClass=getSpeciesSubClassForReptiles(columnId);
		}
		else if(columnId>=80 && columnId<142){
			spcClass="Birds";
			subClass=getSpeciesSubClassForBirds(columnId);
		}
		else if(columnId>=142 && columnId<183){
			spcClass="Mammals";
			subClass=getSpeciesSubClassForMammals(columnId);
		}
		else if(columnId>=183){
			spcClass="Invertebrates";
			subClass=getSpeciesSubClassForInvertebrates(columnId);
		}
		SpeciesHierarchy spc = new SpeciesHierarchy(spcClass, subClass);
		return spc;
	}
	
	/*Fish begins at Column 21 Pacific Lamprey
	Amphibians begins at Column 66 Cascade Torrent Salamander
	Reptiles begins at Column 75 Western Pond Turtle 
	Birds begins at Column 80 American White Pelican
	Mammals begins at Column 142 Dall's Porpoise
	Invertebrates begins at Column 183 Blue-gray Taildropper
	 */
	static public String getSpeciesClass(int columnId){
		String spcClass="";
		if(columnId<21 || columnId>225){
			System.err.println("can Not get the species class for column: "+columnId);
		}
		else if(columnId>=21 && columnId<66){
			spcClass="Fish";
		}
		else if(columnId>=66 && columnId<75){
			spcClass="Amphibians";
		}
		else if(columnId>=75 && columnId<80){
			spcClass="Reptiles";
		}
		else if(columnId>=80 && columnId<142){
			spcClass="Birds";
		}
		else if(columnId>=142 && columnId<183){
			spcClass="Mammals";
		}
		else if(columnId>=183){
			spcClass="Invertebrates";
		}
		return spcClass;
	}


	static public void findSpeciesClassBound(String spc, int colId){
		//fish
		if(spc.compareTo("Pacific Lamprey")==0){
			System.out.println("Column " + colId+ " Pacific Lamprey");
		}//
		//AMPHIBIANS
		if(spc.compareTo("Cascade Torrent Salamander")==0){
			System.out.println("Column " + colId+ " Cascade Torrent Salamander");
		}//REPTILES
		if(spc.compareTo("Western Pond Turtle")==0){
			System.out.println("Column " + colId+ " Western Pond Turtle");
		}		
		//BIRDS
		if(spc.compareTo("American White Pelican")==0){
			System.out.println("Column " + colId+ " American White Pelican");
		}
		//MAMMALS
		if(spc.compareTo("Dall's Porpoise")==0){
			System.out.println("Column " + colId+ " Dall's Porpoise");
		}
		//Invertebrates
		if(spc.compareTo("Blue-gray Taildropper")==0){
			System.out.println("Column " + colId+ " Blue-gray Taildropper");
		}			
	}
	

	static public String getSpeciesSubClassForFish(int columnId){
		String subClass="";
		if(columnId<21 || columnId>65){
			System.err.println("can Not get the species subclass for column: "+columnId);
		}
		else if(columnId>=21 && columnId<23){//Lamprey| Column 21 |Pacific Lamprey
			subClass="Lamprey";
		}
		else if(columnId>=23 && columnId<25){//Sturgeon| Column 23 |Green Sturgeon
			subClass="Sturgeon";
		}
		else if(columnId>=25 && columnId<26){//Mud-minnow| Column 25 |Olympic Mudminnow
			subClass="Mud-minnow";
		}
		else if(columnId>=26 && columnId<27){//Herring| Column 26 |Pacific Herring
			subClass="Herring";
		}
		else if(columnId>=27 && columnId<30){//Minnow| Column 27 |Lake Chub
			subClass="Minnow";
		}
		else if(columnId>=30 && columnId<31){//Sucker| Column 30 |Mountain Sucker
			subClass="Sucker";
		}
		else if(columnId>=31 && columnId<34){//Smelt| Column 31 |Eulachon
			subClass="Smelt";
		}
		//Trout, Salmon, Whitefish| Column 34 |Bull Trout/ Dolly Varden
		else if(columnId>=34 && columnId<45){
			subClass="Trout, Salmon, Whitefish";
		}
		else if(columnId>=45 && columnId<48){//Cod| Column 45 |Pacific Cod
			subClass="Cod";
		}
		else if(columnId>=48 && columnId<61){//Rockfish| Column 48 |Black Rockfish
			subClass="Rockfish";
		}
		else if(columnId>=61 && columnId<62){//Greenling| Column 61 |Lingcod
			subClass="Greenling";
		}
		else if(columnId>=62 && columnId<63){//Sculpin| Column 62 |Margined Sculpin
			subClass="Sculpin";
		}
		else if(columnId>=63 && columnId<64){//Sand Lance| Column 63 |Pacific Sand Lance
			subClass="Sand Lance";
		}
		else if(columnId>=64 && columnId<66){//Right-eye Flounder| Column 64 |English Sole
			subClass="Right-eye Flounder";
		}		
		return subClass;
	}
	
	/*Salamanders| Column 66 |Cascade Torrent Salamander
Frogs and Toads| Column 70 |Columbia Spotted Frog
Turtles| Column 75 |Western Pond Turtle*/
	static public String getSpeciesSubClassForAmphibians(int columnId){
		String subClass="";
		if(columnId<66 || columnId>74){
			System.err.println("can Not get the species subclass for column: "+columnId);
		}
		//Salamanders| Column 66 |Cascade Torrent Salamander
		else if(columnId>=66 && columnId<70){
			subClass="Salamanders";
		}
		//Frogs and Toads| Column 70 |Columbia Spotted Frog
		else if(columnId>=70 && columnId<75){
			subClass="Frogs and Toads";
		}
		return subClass;
	}
	
	/*Turtles| Column 75 |Western Pond Turtle
Snakes| Column 76 |California Mountain Kingsnake
Lizards| Column 79 |Sagebrush Lizard
Marine Birds| Column 80 |American White Pelican*/
	static public String getSpeciesSubClassForReptiles(int columnId){
		String subClass="";
		if(columnId<75 || columnId>79){
			System.err.println("can Not get the species subclass for column: "+columnId);
		}
		//Turtles| Column 75 |Western Pond Turtle
		else if(columnId>=75 && columnId<76){
			subClass="Turtles";
		}
		//Snakes| Column 76 |California Mountain Kingsnake
		else if(columnId>=76 && columnId<79){
			subClass="Snakes";
		}
		//Lizards| Column 79 |Sagebrush Lizard
		else if(columnId>=79 && columnId<80){
			subClass="Lizards";
		}
		return subClass;
	}
	
	static public String getSpeciesSubClassForBirds(int columnId){
		String subClass="";
		if(columnId<80 || columnId>141){
			System.err.println("can Not get the species subclass for column: "+columnId);
		}
		//Marine Birds| Column 80 |American White Pelican
		else if(columnId>=80 && columnId<95){
			subClass="Marine Birds";
		}
		//Herons| Column 95 |Black-crowned Night-heron
		else if(columnId>=95 && columnId<97){
			subClass="Herons";
		}
		//Waterfowl| Column 97 |Brant
		else if(columnId>=97 && columnId<105){
			subClass="Waterfowl";
		}
		//Hawks, Falcons, Eagles| Column 105 |Bald Eagle
		else if(columnId>=105 && columnId<112){
			subClass="Hawks, Falcons, Eagles";
		}
		//Upland Game Birds| Column 112 |Chukar
		else if(columnId>=112 && columnId<120){
			subClass="Upland Game Birds";
		}
		//Cranes| Column 120 |Sandhill Crane
		else if(columnId>=120 && columnId<121){
			subClass="Cranes";
		}
		//Shorebirds| Column 121 |Snowy Plover
		else if(columnId>=121 && columnId<125){
			subClass="Shorebirds";
		}
		//Pigeons| Column 125 |Band-tailed Pigeon
		else if(columnId>=125 && columnId<126){
			subClass="Pigeons";
		}
		//Cuckoos| Column 126 |Yellow-billed Cuckoo
		else if(columnId>=126 && columnId<127){
			subClass="Cuckoos";
		}
		//Owls| Column 127 |Burrowing Owl
		else if(columnId>=127 && columnId<131){
			subClass="Owls";
		}
		//Woodpeckers| Column 131 |Black-backed Woodpecker
		else if(columnId>=131 && columnId<135){
			subClass="Woodpeckers";
		}
		//Perching Birds| Perching Birds: Column 135 |Loggerhead Shrike
		else if(columnId>=135 && columnId<142){
			subClass="Perching Birds";
		}
		return subClass;
	}

	/*Marine Mammals| Column 142 |Dall's Porpoise
Bats| Column 155 |Roosting Concentrations of: Big-brown Bat, Myotis bats, Pallid Bat
Rabbits| Column 158 |Black-tailed Jackrabbit
Rodents| Column 161 |Gray-tailed Vole
Terrestrial Carnivores| Column 167 |Cascade Red Fox
Large Ungulates| Column 174 |Bighorn Sheep
Gastropods| Column 183 |Blue-gray Taildropper*/
	static public String getSpeciesSubClassForMammals(int columnId){
		String subClass="";
		if(columnId<142 || columnId>182){
			System.err.println("can Not get the species subclass for column: "+columnId);
		}
		//Marine Mammals| Column 142 |Dall's Porpoise
		else if(columnId>=142 && columnId<155){
			subClass="Marine Mammals";
		}
		//Bats| Column 155 |Roosting Concentrations of: Big-brown Bat, Myotis bats, Pallid Bat
		else if(columnId>=155 && columnId<158){
			subClass="Bats";
		}
		//Rabbits| Column 158 |Black-tailed Jackrabbit
		else if(columnId>=158 && columnId<161){
			subClass="Rabbits";
		}
		//Rodents| Column 161 |Gray-tailed Vole
		else if(columnId>=161 && columnId<167){
			subClass="Rodents";
		}
		//Terrestrial Carnivores| Column 167 |Cascade Red Fox
		else if(columnId>=167 && columnId<174){
			subClass="Terrestrial Carnivores";
		}
		//Large Ungulates| Column 174 |Bighorn Sheep
		else if(columnId>=174 && columnId<183){
			subClass="Large Ungulates";
		}		
		return subClass;
	}
	
	/*Gastropods| Column 183 |Blue-gray Taildropper
Bivalves| Column 191 |California Floater
Crustaceans| Column 199 |Dungeness Crab
Beetles| Column 201 |Beller's Ground Beetle
Dragonfly| Column 206 |Columbia Clubtail
Worms| Column 208 |Giant Palouse Earthworm
Millepedes| Column 209 |Leschi's Millipede
Butterflies| Column 210 |Chinquapin Hairstreak
Urchins| Column 225 |Red Urchin*/
	static public String getSpeciesSubClassForInvertebrates(int columnId){
		String subClass="";
		if(columnId<183 || columnId>225){
			System.err.println("can Not get the species subclass for column: "+columnId);
		}
		//Gastropods| Column 183 |Blue-gray Taildropper
		else if(columnId>=183 && columnId<191){
			subClass="Gastropods";
		}
		//Bivalves| Column 191 |California Floater
		else if(columnId>=191 && columnId<199){
			subClass="Bivalves";
		}
		//Crustaceans| Column 199 |Dungeness Crab
		else if(columnId>=199 && columnId<201){
			subClass="Crustaceans";
		}
		//Beetles| Column 201 |Beller's Ground Beetle
		else if(columnId>=201 && columnId<206){
			subClass="Beetles";
		}
		//Dragonfly| Column 206 |Columbia Clubtail
		else if(columnId>=206 && columnId<208){
			subClass="Dragonfly";
		}
		//Worms| Column 208 |Giant Palouse Earthworm
		else if(columnId>=208 && columnId<209){
			subClass="Worms";
		}		
		//Millepedes| Column 209 |Leschi's Millipede
		else if(columnId>=209 && columnId<210){
			subClass="Rodents";
		}
		//Butterflies| Column 210 |Chinquapin Hairstreak
		else if(columnId>=210 && columnId<225){
			subClass="Butterflies";
		}
		//Urchins| Column 225 |Red Urchin
		else if(columnId>=225){
			subClass="Urchins";
		}	
		return subClass;
	}
	
	static public void findSpeciesSubClassBound(String spc, int colId){
		//fish Lamprey
		if(spc.compareTo("Pacific Lamprey")==0){
			System.out.println("Lamprey| Column " + colId+ " |Pacific Lamprey");
		}
		//fish Sturgeon
		if(spc.compareTo("Green Sturgeon")==0){
			System.out.println("Sturgeon| Column " + colId+ " |Green Sturgeon");
		}
		//fish Mud-minnow
		if(spc.compareTo("Olympic Mudminnow")==0){
			System.out.println("Mud-minnow| Column " + colId+ " |Olympic Mudminnow");
		}
		//fish Herring
		if(spc.compareTo("Pacific Herring")==0){
			System.out.println("Herring| Column " + colId+ " |Pacific Herring");
		}
		//fish Minnow
		if(spc.compareTo("Lake Chub")==0){
			System.out.println("Minnow| Column " + colId+ " |Lake Chub");
		}
		//fish Sucker
		if(spc.compareTo("Mountain Sucker")==0){
			System.out.println("Sucker| Column " + colId+ " |Mountain Sucker");
		}		
		//fish Smelt
		if(spc.compareTo("Eulachon")==0){
			System.out.println("Smelt| Column " + colId+ " |Eulachon");
		}	
		//fish Trout, Salmon, Whitefish
		if(spc.compareTo("Bull Trout/ Dolly Varden")==0){
			System.out.println("Trout, Salmon, Whitefish| Column " + colId+ " |Bull Trout/ Dolly Varden");
		}	
		//fish Cod
		if(spc.compareTo("Pacific Cod")==0){
			System.out.println("Cod| Column " + colId+ " |Pacific Cod");
		}	
		//fish Rockfish
		if(spc.compareTo("Black Rockfish")==0){
			System.out.println("Rockfish| Column " + colId+ " |Black Rockfish");
		}		
		//fish Greenling
		if(spc.compareTo("Lingcod")==0){
			System.out.println("Greenling| Column " + colId+ " |Lingcod");
		}	
		//fish Sculpin
		if(spc.compareTo("Margined Sculpin")==0){
			System.out.println("Sculpin| Column " + colId+ " |Margined Sculpin");
		}		
		//fish Sand Lance
		if(spc.compareTo("Pacific Sand Lance")==0){
			System.out.println("Sand Lance| Column " + colId+ " |Pacific Sand Lance");
		}	
		//fish Right-eye Flounder
		if(spc.compareTo("English Sole")==0){
			System.out.println("Right-eye Flounder| Column " + colId+ " |English Sole");
		}			
		//Amphibians Salamanders
		if(spc.compareTo("Cascade Torrent Salamander")==0){
			System.out.println("Salamanders| Column " + colId+ " |Cascade Torrent Salamander");
		}	
		//Amphibians Frogs and Toads
		if(spc.compareTo("Columbia Spotted Frog")==0){
			System.out.println("Frogs and Toads| Column " + colId+ " |Columbia Spotted Frog");
		}
		//Reptiles Turtles
		if(spc.compareTo("Western Pond Turtle")==0){
			System.out.println("Turtles| Column " + colId+ " |Western Pond Turtle");
		}	
		//Reptiles Snakes
		if(spc.compareTo("California Mountain Kingsnake")==0){
			System.out.println("Snakes| Column " + colId+ " |California Mountain Kingsnake");
		}
		//Reptiles Lizards
		if(spc.compareTo("Sagebrush Lizard")==0){
			System.out.println("Lizards| Column " + colId+ " |Sagebrush Lizard");
		}
		//Birds Marine Birds
		if(spc.compareTo("American White Pelican")==0){
			System.out.println("Marine Birds| Column " + colId+ " |American White Pelican");
		}
		//Birds Herons 
		if(spc.compareTo("Black-crowned Night-heron")==0){
			System.out.println("Herons| Column " + colId+ " |Black-crowned Night-heron");
		}
		//Birds Waterfowl
		if(spc.compareTo("Brant")==0){
			System.out.println("Waterfowl| Column " + colId+ " |Brant");
		}				
		//Birds Hawks, Falcons, Eagles 
		if(spc.compareTo("Bald Eagle")==0){
			System.out.println("Hawks, Falcons, Eagles| Column " + colId+ " |Bald Eagle");
		}			
		//Birds Upland Game Birds  
		if(spc.compareTo("Chukar")==0){
			System.out.println("Upland Game Birds| Column " + colId+ " |Chukar");
		}	
		//Birds Cranes  
		if(spc.compareTo("Sandhill Crane")==0){
			System.out.println("Cranes| Column " + colId+ " |Sandhill Crane");
		}			
		//Birds Shorebirds   
		if(spc.compareTo("Snowy Plover")==0){
			System.out.println("Shorebirds| Column " + colId+ " |Snowy Plover");
		}	
		//Birds Pigeons   
		if(spc.compareTo("Band-tailed Pigeon")==0){
			System.out.println("Pigeons| Column " + colId+ " |Band-tailed Pigeon");
		}			
		//Birds Cuckoos    
		if(spc.compareTo("Yellow-billed Cuckoo")==0){
			System.out.println("Cuckoos| Column " + colId+ " |Yellow-billed Cuckoo");
		}
		//Birds Owls     
		if(spc.compareTo("Burrowing Owl")==0){
			System.out.println("Owls| Column " + colId+ " |Burrowing Owl");
		}
		//Birds Swifts      
		if(spc.compareTo("Vaux’s Swift")==0){
			System.out.println("Swifts| Column " + colId+ " |Vaux’s Swift");
		}
		//Birds Woodpeckers       
		if(spc.compareTo("Black-backed Woodpecker")==0){
			System.out.println("Woodpeckers| Column " + colId+ " |Black-backed Woodpecker");
		}
		//BIRDS Perching Birds 
		if(spc.compareTo("Loggerhead Shrike")==0){
			System.out.println("Perching Birds| Perching Birds: Column " + colId+ " |Loggerhead Shrike");
		}
		//Mammals Marine Mammals 
		if(spc.compareTo("Dall's Porpoise")==0){
			System.out.println("Marine Mammals| Column " + colId+ " |Dall's Porpoise");
		}		
		//Mammals Shrews  
		if(spc.compareTo("Merriam’s Shrew")==0){
			System.out.println("Shrews| Column " + colId+ " |Merriam’s Shrew");
		}			
		//Mammals Bats  
		if(spc.compareTo("Roosting Concentrations of: Big-brown Bat, Myotis bats, Pallid Bat")==0){
			System.out.println("Bats| Column " + colId+ " |Roosting Concentrations of: Big-brown Bat, Myotis bats, Pallid Bat");
		}	
		//Mammals Rabbits   
		if(spc.compareTo("Black-tailed Jackrabbit")==0){
			System.out.println("Rabbits| Column " + colId+ " |Black-tailed Jackrabbit");
		}
		//Mammals Rodents    
		if(spc.compareTo("Gray-tailed Vole")==0){
			System.out.println("Rodents| Column " + colId+ " |Gray-tailed Vole");
		}
		//Mammals Terrestrial Carnivores     
		if(spc.compareTo("Cascade Red Fox")==0){
			System.out.println("Terrestrial Carnivores| Column " + colId+ " |Cascade Red Fox");
		}
		//Mammals Large Ungulates    
		if(spc.compareTo("Bighorn Sheep")==0){
			System.out.println("Large Ungulates| Column " + colId+ " |Bighorn Sheep");
		}
		//Invertebrates Gastropods
		if(spc.compareTo("Blue-gray Taildropper")==0){
			System.out.println("Gastropods| Column " + colId+ " |Blue-gray Taildropper");
		}
		//Invertebrates Bivalves
		if(spc.compareTo("California Floater")==0){
			System.out.println("Bivalves| Column " + colId+ " |California Floater");
		}
		//Invertebrates Crustaceans
		if(spc.compareTo("Dungeness Crab")==0){
			System.out.println("Crustaceans| Column " + colId+ " |Dungeness Crab");
		}
		//Invertebrates Beetles
		if(spc.compareTo("Beller's Ground Beetle")==0){
			System.out.println("Beetles| Column " + colId+ " |Beller's Ground Beetle");
		}
		//Invertebrates Dragonfly
		if(spc.compareTo("Columbia Clubtail")==0){
			System.out.println("Dragonfly| Column " + colId+ " |Columbia Clubtail");
		}
		//Invertebrates Worms
		if(spc.compareTo("Giant Palouse Earthworm")==0){
			System.out.println("Worms| Column " + colId+ " |Giant Palouse Earthworm");
		}
		//Invertebrates Millepedes
		if(spc.compareTo("Leschi's Millipede")==0){
			System.out.println("Millepedes| Column " + colId+ " |Leschi's Millipede");
		}
		//Invertebrates Butterflies
		if(spc.compareTo("Chinquapin Hairstreak")==0){
			System.out.println("Butterflies| Column " + colId+ " |Chinquapin Hairstreak");
		}
		//Invertebrates Urchins
		if(spc.compareTo("Red Urchin")==0){
			System.out.println("Urchins| Column " + colId+ " |Red Urchin");
		}
	}



}
