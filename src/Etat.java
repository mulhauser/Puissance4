import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Etat {

	// A qui de jouer ?
	protected int joueur;
	protected String[][] plateau;
	protected static final int colonne = 7, ligne = 6;


	enum FinDePartie {
		NON, MATCHNUL, ORDI_GAGNE, HUMAIN_GAGNE
	}

	public Etat() {
		this.plateau = new String[ligne][colonne];
		for (int i = 0; i < ligne; i++) {
			for (int j = 0; j < colonne; j++) {
				this.plateau[i][j] = " ";
			}
		}
		// this.plateau = plateauTest;
	}

	public static int getColonne() {
		return colonne;
	}

	public static int getLigne() {
		return ligne;
	}

	public int getJoueur() {
		return joueur;
	}

	public void setJoueur(int joueur) {
		this.joueur = joueur;
	}

	public String[][] getPlateau() {
		return this.plateau;
	}

	public String getPlateau(int l, int h) {
		return plateau[l][h];
	}

	public void setPlateau(int l, int h, String val) {
		plateau[l][h] = val;
	}

	public void copieEtat(Etat src) {
		this.joueur = src.getJoueur();
		for (int i = 0; i < ligne; i++) {
			for (int j = 0; j < colonne; j++) {
				plateau[i][j] = src.getPlateau()[i][j];
			}
		}
		// plateau = src.getPlateau().clone();
	}

	public boolean jouerCoup(Coup c) {
		if (plateau[c.getLigne()][c.getColonne()] != " ") {
			return false;
		} else {
			String val;
			if (joueur == 0) {
				val = "X";
			} else {
				val = "O";
			}
			setPlateau(c.getLigne(), c.getColonne(), val);
			this.joueur = autreJoueur(this.joueur);
			return true;
		}
	}

	static int autreJoueur(int j) {
		return (1 - j);
	}


	public void ordiJoueMCTS(int tempsmax) {
		long tic, toc, temps = 0;
		tic = System.currentTimeMillis();

		ArrayList<Coup> coups = new ArrayList<Coup>();
		Coup meilleur_coup;
		Noeud racine = new Noeud(null, null, this);
		racine.getEtat().copieEtat(this);

		coups = coupsPossibles(racine.getEtat());

		Random rand = new Random();
		// random entre 0 et coups.length - 1
		int nombreAleatoire = rand.nextInt(coups.size());
		// Random rand = new Random();
		// int nombreAleatoire = rand.nextInt(max - min + 1) + min;
		meilleur_coup = coups.get(nombreAleatoire);
		
		
		int iter = 0;	
		Noeud vl;
		int delta;
		racine.getListNonVisites().addAll(coups);
		do {
			
			vl = treePolicy(racine);
			//System.out.println(vl.getListNonVisites().size());
			vl = defaultPolicy(vl);
			
			backUp(vl);
			
			toc = System.currentTimeMillis(); // A Revoir pour les secondes
			temps = TimeUnit.MILLISECONDS.toSeconds((int) (((double) (toc - tic))));
			iter++;
			
		} while (temps < tempsmax);

		for(Noeud child:racine.getListEnfants()){
			//double t1 = child.getRecompense()/child.getNbSimulation();
			//double t2 = Math.sqrt(2) * Math.sqrt(2*Math.log(racine.getNbSimulation())/child.getNbSimulation());
			//double probaWin = t1 + t2;
			double probaWin = child.getRecompense()/child.getNbSimulation();
			///////////// proba de victoire ici//////////
			// à l'air correct sauf au dernier coup quand l'ordi va gagner , la proba est proche de 0 car il n'a pas besoin de faire beaucoup de simulations
			System.out.println(" Coup("+child.getCoup().getLigne()+","+child.getCoup().getColonne()+")  -  " + "Recompense associee: " +child.getRecompense()+"  -  Nombre de simulations pour le coup : "+child.getNbSimulation() + " -  Estimation de la probabilite de victoire : " + probaWin);
		}
		Noeud res = bestChild(racine,0);
		System.out.println("---------");
		System.out.println("Nombre de simulations: " +iter);
		System.out.println(" Coup Joue("+res.getCoup().getLigne()+","+res.getCoup().getColonne()+")  -  " + "Recompense associee: " +res.getRecompense()+"  -  Nombre de simulations pour le coup : "+res.getNbSimulation() + "\n");
		jouerCoup(res.getCoup());
		
	}

	
	
	
	public Noeud treePolicy(Noeud n){
		while(n.getEtat().testFin() == FinDePartie.NON){
			if(n.getListNonVisites().size() != 0){
				return expand(n);
			}else{
				n = bestChild(n, Math.sqrt(2));
			}
		}
		return n;
	}
	
	
	
	
	
	public Noeud expand(Noeud n){
		ArrayList<Coup> coups = n.getListNonVisites();
		
		Random rand = new Random();
		int nombreAleatoire = rand.nextInt(coups.size());
		Coup coupAleatoire = coups.get(nombreAleatoire);
		
		n.getListNonVisites().remove(nombreAleatoire);
		Noeud nPrim = ajouterEnfant(n,coupAleatoire);
		nPrim.getListNonVisites().addAll(coupsPossibles(nPrim.getEtat()));
		//System.out.println(nPrim.getListNonVisites().size());
		return nPrim;
		
	}
	
	
	
	
	public Noeud bestChild(Noeud n, double c){
		Noeud res = null;
		double max = -1000;
		double min = 50000; 
		double recompense = 0;
		int nbSimulChild;
		int nbSimulParent;
		double t1, t2, t3;
		int minMax;
		//if(n.getEtat().getJoueur() == 1) minMax = -1;
		//else minMax = 1;
		for(Noeud child:n.getListEnfants()){
			recompense = child.getRecompense();
			nbSimulChild = child.getNbSimulation();
			nbSimulParent = n.getNbSimulation();
			
			// CRITERE DE SELECTION : ROBUSTE-MAX
			t1 = recompense/nbSimulChild;
			t2 = c * Math.sqrt(2*Math.log(nbSimulParent)/nbSimulChild);
			t3 = t1 + t2;
	
			
			// CRITERE DE SELECTION : MAX
			//t3 = child.getRecompense();
			
			
			// cRITERE DE SELECTION : ROBUSTE
			//t3 = child.getNbSimulation();
			
			
			
			if(t3 > max){
				max = t3;
				res = child;
			}
			if(c == 0 && (child.getEtat().testFin() == FinDePartie.MATCHNUL || child.getEtat().testFin() == FinDePartie.ORDI_GAGNE)){
				return child;
			}
			
		}
		return res;
	}
	
	
	
	public Noeud defaultPolicy(Noeud e){
		//System.out.println(e.getListNonVisites().size());
		//int dpt = 0;
		while(e.getEtat().testFin() == FinDePartie.NON){

			ArrayList<Coup> coups = e.getListNonVisites();
			
			
			///////////////////////////////////////////	
			//////////	AMELIORATION QUESTION 3 ///////
			///////////////////////////////////////////
			boolean b = false;
			for(Coup dispo : coupsPossibles(e.getEtat())){
				//Noeud n = ajouterEnfant(e, dispo);
				Noeud n = new Noeud(e, dispo,e.getEtat());
				n.getEtat().jouerCoup(dispo);
				if(n.getEtat().testFin() == FinDePartie.ORDI_GAGNE){
					e.getListNonVisites().remove(dispo);
					e = ajouterEnfant(e, dispo);
					e.getListNonVisites().addAll(coupsPossibles(e.getEtat()));
					b = true;
					//dpt++;
				}
			}
			if(!b){
				Random rand = new Random();
				int nombreAleatoire = rand.nextInt(coups.size());
				Coup coupAleatoire = coups.get(nombreAleatoire);
				e.getListNonVisites().remove(coupAleatoire);
				e = ajouterEnfant(e,coupAleatoire);
				e.getListNonVisites().addAll(coupsPossibles(e.getEtat()));
				//dpt++;
			}
			
		}
		//e.getRecompense();
		//System.out.println(dpt);
		return e;
	}
	
	
	public void backUp(Noeud vl){
		while(vl != null){
			vl.incrementeNbSimulation();
			vl.backUpRecompense(vl.getRecompense());
			//System.out.println(vl.getRecompense());
			//System.out.println(vl.recompense);
			//System.out.println(i);
			vl = vl.getParent();
		}
		
	}

	

	


	public ArrayList<Coup> coupsPossibles(Etat etat) {
		ArrayList<Coup> coups = new ArrayList<Coup>();
		int k = 0;
		int i = 5, j;
		boolean libre = false;
		for (j = 0; j < 7; j++) {
			while(i >= 0 && !libre){
				if (etat.getPlateau(i, j) == " ") {
					//System.out.println(j);
					coups.add(k, new Coup(i, j));
					libre = true;
					k++;
				}
				i--;
			}
			i = 5;
			libre = false;
		}
		return coups;
	}

	
	
	public Noeud ajouterEnfant(Noeud parent, Coup coup) {
		Noeud enfant = new Noeud(parent, coup,this);
		enfant.getEtat().jouerCoup(coup);
		parent.setEnfants(parent.getNbEnfants(), enfant);
		parent.setNbEnfantsPlus(); // parent->nb_enfants++;
		return enfant;
	}

	
	
	public FinDePartie testFin() {
		int i, j, k, n = 0;
		for (i = 0; i < 6; i++) {
			for (j = 0; j < 7; j++) {
				if (getPlateau(i, j) != " ") {
					n++; // nb coups joués

					// lignes
					k = 0;
					while (k < 4 && i + k < 6 && getPlateau(i + k, j) == getPlateau(i, j))
						k++;
					if (k == 4)
						return getPlateau(i, j) == "O" ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE; 

					// colonnes
					k = 0;
					while (k < 4 && j + k < 7 && getPlateau(i, j + k) == getPlateau(i, j))
						k++;
					if (k == 4)
						return getPlateau(i, j) == "O" ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE; 

					// diagonales
					k = 0;
					while (k < 4 && i + k < 6 && j + k < 7 && getPlateau(i + k, j + k) == getPlateau(i, j))
						k++;
					if (k == 4)
						return getPlateau(i, j) == "O" ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE; 

					k = 0;
					while (k < 4 && i + k < 6 && j - k >= 0 && getPlateau(i + k, j - k) == getPlateau(i, j))
						k++;
					if (k == 4)
						return getPlateau(i, j) == "O" ? FinDePartie.ORDI_GAGNE : FinDePartie.HUMAIN_GAGNE; 
				}
			}
		}
		// et sinon tester le match nul
		if (n == 7 * 6)
			return FinDePartie.MATCHNUL;
		
		return FinDePartie.NON;
	}

	
	
	// Affichage du Plateau de l'Etat courant
	public void affichage() {
		System.out.print("   |");
		for (int j = 0; j < 7; j++)
			System.out.print(" " + j + " |");
		System.out.println("");
		System.out.print("--------------------------------");
		System.out.println("");

		for (int i = 0; i < 6; i++) {
			System.out.print(" " + i + " |");
			for (int j = 0; j < 7; j++)
				System.out.print(" " + this.plateau[i][j]  +" |");
			System.out.println("");
			System.out.print("--------------------------------");
			System.out.println("");
		}
	}

}
